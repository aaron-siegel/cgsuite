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
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.cgsuite.exception.InputException;
import org.cgsuite.exception.SyntaxException;
import org.cgsuite.lang.Domain;
import org.cgsuite.lang.EvalUtil;
import org.cgsuite.lang.parser.ParserUtil;
import org.cgsuite.output.Output;
import org.cgsuite.output.StyledTextOutput;
import org.openide.util.RequestProcessor;
import scala.Symbol;
import scala.collection.JavaConverters;
import scala.collection.mutable.AnyRefMap;

/**
 *
 * @author asiegel
 */
public class CalculationCapsule implements Runnable
{
    private final static Logger log = Logger.getLogger(CalculationCapsule.class.getName());

    public final static RequestProcessor REQUEST_PROCESSOR = new RequestProcessor
        (WorksheetPanel.class.getName(), 1, true);

    private AnyRefMap<Symbol,Object> varMap;
    private String text;
    private Output[] output;
    private boolean isErrorOutput;

    public CalculationCapsule(AnyRefMap<Symbol,Object> varMap, String text)
    {
        this.varMap = varMap;
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
            //CgsuitePackage.refreshAll();
            Tree tree = ParserUtil.parseScript(text);
            Output result = EvalUtil.evaluate(varMap, tree);
            output = new Output[] { result };
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
/*
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
    */

    private static Output[] getExceptionOutput(String input, Throwable exc, boolean includeLine)
    {
        if (exc instanceof SyntaxException)
        {
            RecognitionException recog = ((SyntaxException) exc).exc();
            
            int line = recog.line;
            int col  = recog.charPositionInLine;

            String message =
                (includeLine && line > 0 ? "Error (Line " + line + ":" + col + "): " : "")
                + getMessageForException(recog);

            if (input == null || line <= 0)
            {
                return new Output[] { new StyledTextOutput(message) };
            }
            else
            {
                List<Output> output = new ArrayList<Output>();
                output.add(errorOutput("Syntax error."));
                output.addAll(Arrays.asList(getLineColOutput("Worksheet", input, line, col)));
                return output.toArray(new Output[0]);
            }
        }
        else if (exc instanceof InputException)
        {
            return getStackOutput(input, (InputException) exc).toArray(new Output[0]);
        }/*
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
        }*/
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
        /*
        if (exc.getInvocationTarget() != null)
        {
            output.add(errorOutput("  during call to " + exc.getInvocationTarget() + "\n"));
        }
        */
        for (Token token : JavaConverters.asJavaCollection(exc.tokenStack()))
        {
            assert token.getInputStream() != null : "Input stream is null: " + token + " (" + exc.getMessage() + ")";
            output.addAll(Arrays.asList(getLineColOutput(token.getInputStream().getSourceName(), input, token.getLine(), token.getCharPositionInLine())));
            /*
            String source = token.getInputStream().getSourceName();
            if (source.equals("Worksheet"))
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
            */
        }
        return output;
    }

    /*
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
*/
    private static Output[] getLineColOutput(String source, String input, int line, int col)
    {
        if (source.equals("Worksheet")) {
            Output[] output = new Output[3];
            output[0] = errorOutput("  at worksheet input:");
            // Advance to the point in the input string where the exceptional line begins.
            int lineStartIndex = 0;
            for (int i = 1; i < line; i++)
            {
                lineStartIndex = input.indexOf('\n', lineStartIndex) + 1;
            }
            int lineEndIndex = input.indexOf('\n', lineStartIndex);
            // Next get the part of the input string that has the error.
            output[1] = errorOutput(
                "  " + input.substring(
                    Math.max(lineStartIndex, lineStartIndex + col - 24),
                    Math.min(lineEndIndex == -1 ? input.length() : lineEndIndex, lineStartIndex + col + 25)
                ));
            String pointerStr = "  ";
            for (int i = 0; i < Math.min(col - 1, 22); i++)
            {
                pointerStr += " ";
            }
            output[2] = errorOutput(pointerStr + (col == 0 ? "^^" : "^^^"));
            return output;
        } else {
            return new Output[] { errorOutput("  at " + source + " line " + line + ":" + col) };
        }
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
