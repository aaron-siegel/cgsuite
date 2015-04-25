/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.worksheet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.cgsuite.lang.CgsuiteClassLoadException;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Domain;
import org.cgsuite.lang.InputException;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
import org.cgsuite.lang.parser.CgsuiteLexer;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.cgsuite.lang.parser.CgsuiteParser.SyntaxError;
import org.cgsuite.lang.parser.CgsuiteTree;
import org.cgsuite.lang.parser.CgsuiteTreeAdaptor;
import org.openide.util.RequestProcessor;

/**
 *
 * @author asiegel
 */
public class CalculationCapsule implements Runnable
{
    private final static Logger log = Logger.getLogger(CalculationCapsule.class.getName());
    private final static Domain WORKSPACE_DOMAIN = new Domain
        (null, null, CgsuitePackage.DEFAULT_PACKAGE_IMPORTS, CgsuitePackage.DEFAULT_CLASS_IMPORTS);

    public final static RequestProcessor REQUEST_PROCESSOR = new RequestProcessor
        (WorksheetPanel.class.getName(), 1, true);

    private Domain domain;
    private String text;
    private Output[] output;
    private boolean isErrorOutput;

    public CalculationCapsule(String text)
    {
        this(text, WORKSPACE_DOMAIN);
    }

    public CalculationCapsule(String text, Domain domain)
    {
        this.domain = domain;
        this.text = text;
    }
    
    public RequestProcessor.Task createTask()
    {
        return REQUEST_PROCESSOR.create(this);
    }
    
    public void runAndWait()
    {
        RequestProcessor.Task task = createTask();
        REQUEST_PROCESSOR.submit(task);
        task.waitFinished();
    }

    @Override
    public void run()
    {
        try
        {
            CgsuitePackage.refreshAll();
            ANTLRStringStream input = new ANTLRStringStream(text);
            CgsuiteLexer lexer = new CgsuiteLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CgsuiteParser parser = new CgsuiteParser(tokens);
            parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
            CgsuiteParser.script_return r = parser.script();
            CgsuiteTree tree = (CgsuiteTree) r.getTree();

            if (!lexer.getErrors().isEmpty())
            {
                RecognitionException exc = lexer.getErrors().get(0).getException();
                output = getLineColOutput(text, exc.line, exc.charPositionInLine, "Syntax error.");
                isErrorOutput = true;
            }
            else if (!parser.getErrors().isEmpty())
            {
                RecognitionException exc = parser.getErrors().get(0).getException();
                String message;
                if (exc.token.getType() == CgsuiteParser.SLASHES)
                    message = "Syntax error: every slash | must be enclosed by braces { }.";
                else
                    message = "Syntax error.";
                output = getLineColOutput(text, exc.line, exc.charPositionInLine, message);
                isErrorOutput = true;
            }
            else
            {
                output = new Output[] { invoke(tree) };
            }
        }
        catch (Throwable exc)
        {
            try
            {
                output = getExceptionOutput(text, exc, false);
            }
            catch (Throwable exc2)
            {
                log.log(Level.WARNING, "Exception thrown extracting error output!", exc2);
                output = new Output[] { errorOutput("An unexpected error occurred.") };
            }
            isErrorOutput = true;
        }
    }

    public Output[] getOutput()
    {
        return output;
    }

    public boolean isErrorOutput()
    {
        return isErrorOutput;
    }

    private Output invoke(CgsuiteTree tree)
    {
        log.info("Beginning calculation.");
        long startTime = System.currentTimeMillis();
        
        try
        {
            CgsuiteObject retval = domain.script(tree).simplify();

            if (retval == CgsuiteObject.NIL)
                return new StyledTextOutput("");
            else
                return retval.toOutput();
        }
        finally
        {
            log.info("Calculation completed in " + (System.currentTimeMillis()-startTime) + " ms.");
        }
    }
    
    private static List<Output> getStackOutput(String input, InputException exc)
    {
        List<Output> output = new ArrayList<Output>();
        output.add(errorOutput(exc.getMessage()));
        if (exc.getCause() != null)
        {
            output.add(errorOutput("  caused by " + exc.getCause().getClass().getName()));
            StackTraceElement[] javaStackTrace = exc.getCause().getStackTrace();
            for (int i = 0; i < 6 && i < javaStackTrace.length; i++)
            {
                StackTraceElement ste = javaStackTrace[i];
                output.add(errorOutput("  at " + ste.getClassName() + " line " + ste.getLineNumber()));
            }
            if (javaStackTrace.length > 3)
            {
                output.add(errorOutput("  ......"));
            }
        }
        if (exc.getInvocationTarget() != null)
        {
            output.add(errorOutput("  during call to " + exc.getInvocationTarget() + "\n"));
        }
        for (Token token : exc.getTokenStack())
        {
            assert token.getInputStream() != null : "Input stream is null: " + token + " (" + exc.getMessage() + ")";
            String source = token.getInputStream().getSourceName();
            if (source == null)
            {
                output.add(errorOutput("  at worksheet input:\n"));
                output.addAll(Arrays.asList(getLineColOutput(input, token.getLine(), token.getCharPositionInLine(), "")));
            }
            else
            {
                output.add(errorOutput(
                    "  at " + source + " line " + token.getLine() + ":" + token.getCharPositionInLine()
                    ));
            }
        }
        return output;
    }

    private static Output[] getExceptionOutput(String input, Throwable exc, boolean includeLine)
    {
        if (exc instanceof RecognitionException)
        {
            int line = ((RecognitionException) exc).line;
            int col  = ((RecognitionException) exc).charPositionInLine;

            String message =
                (includeLine && line > 0 ? "Error (Line " + line + ":" + col + "): " : "")
                + getMessageForException(exc);

            if (input == null || line <= 0)
            {
                return new Output[] { new StyledTextOutput(message) };
            }
            else
            {
                return getLineColOutput(input, line, col, message);
            }
        }
        else if (exc instanceof InputException)
        {
            return getStackOutput(input, (InputException) exc).toArray(new Output[0]);
        }
        else if (exc instanceof CgsuiteClassLoadException)
        {
            CgsuiteClassLoadException cclo = (CgsuiteClassLoadException) exc;
            if (cclo.getSyntaxErrors() != null)
            {
                return getSyntaxErrorsOutput(cclo);
            }
            else
            {
                return new Output[] { errorOutput("I/O Error loading classfile " + cclo.getClassFile().getName() + ": " + cclo.getCause().getMessage()) };
            }
        }
        else
        {
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            String[] strings = sw.toString().split("\n");
            Output[] output = new Output[strings.length];
            for (int i = 0; i < strings.length; i++)
            {
                output[i] = errorOutput(strings[i]);
            }
            return output;
        }
    }
    
    private static Output[] getSyntaxErrorsOutput(CgsuiteClassLoadException cclo)
    {
        List<Output> output = new ArrayList<Output>();
        
        SyntaxError se = cclo.getSyntaxErrors().get(0);
        
        output.add(errorOutput("Syntax error loading class file " + cclo.getClassFile().getPath() + ": " + se.getMessage()));
        output.add(errorOutput(
            "  at " + cclo.getClassFile().getNameExt() + " line " + se.getException().line + ":" + se.getException().charPositionInLine
            ));
        
        return output.toArray(new Output[output.size()]);
    }

    private static Output[] getLineColOutput(String input, int line, int col, String message)
    {
        Output[] output = new Output[3];
        // Advance to the point in the input string where the exceptional line begins.
        int lineStartIndex = 0;
        for (int i = 1; i < line; i++)
        {
            lineStartIndex = input.indexOf('\n', lineStartIndex) + 1;
        }
        int lineEndIndex = input.indexOf('\n', lineStartIndex);
        // Next get the part of the input string that has the error.
        output[0] = errorOutput(
            "  " + input.substring(
                Math.max(lineStartIndex, lineStartIndex + col - 24),
                Math.min(lineEndIndex == -1 ? input.length() : lineEndIndex, lineStartIndex + col + 25)
            ));
        String pointerStr = "  ";
        for (int i = 0; i < Math.min(col - 1, 22); i++)
        {
            pointerStr += " ";
        }
        output[1] = errorOutput(pointerStr + (col == 0 ? "^^" : "^^^"));
        output[2] = errorOutput(message);
        return output;
    }

    private static Output errorOutput(String msg)
    {
        return new StyledTextOutput(
            EnumSet.of(StyledTextOutput.Style.FACE_MONOSPACED, StyledTextOutput.Style.COLOR_RED),
            msg
            );
    }

    private static String getMessageForException(Throwable exc)
    {
        if (exc instanceof InputException)
        {
            return exc.getMessage();
        }
        else
        {
            return "Syntax error.";
        }
    }

}
