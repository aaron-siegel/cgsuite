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
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Domain;
import org.cgsuite.lang.InputException;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
import org.cgsuite.lang.parser.CgsuiteLexer;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.cgsuite.lang.parser.CgsuiteTree;
import org.cgsuite.lang.parser.CgsuiteTreeAdaptor;

/**
 *
 * @author asiegel
 */
public class CalculationCapsule implements Runnable
{
    private final static Domain WORKSPACE_DOMAIN = new Domain(CgsuitePackage.ROOT_IMPORT);
    
    private String text;
    private Output[] output;
    private boolean isErrorOutput;

    public CalculationCapsule(String text)
    {
        this.text = text;
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

            if (parser.getNumberOfSyntaxErrors() > 0)
            {
                RecognitionException exc = parser.getErrors().get(0).getException();
                output = getLineColOutput(text, exc.line, exc.charPositionInLine, "Syntax error.");
                isErrorOutput = true;
            }
            else
            {
                output = new Output[] { invoke(tree) };
            }
        }
        catch (Exception exc)
        {
            output = getExceptionOutput(text, exc, false);
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
        CgsuiteObject retval = WORKSPACE_DOMAIN.script(tree).simplify();
        return retval.toOutput();
    }
    
    private static List<Output> getStackOutput(String input, InputException exc)
    {
        List<Output> output = new ArrayList<Output>();
        output.add(errorOutput(exc.getMessage()));
        if (exc.getCause() != null)
        {
            output.add(errorOutput("  caused by " + exc.getCause().getClass().getName()));
            StackTraceElement[] javaStackTrace = exc.getCause().getStackTrace();
            for (int i = 0; i < 3 && i < javaStackTrace.length; i++)
            {
                StackTraceElement ste = javaStackTrace[i];
                output.add(errorOutput("  at " + ste.getClassName() + " line " + ste.getLineNumber() + "\n"));
            }
            if (javaStackTrace.length > 3)
            {
                output.add(errorOutput("  ......"));
            }
        }
        for (Token token : exc.getTokenStack())
        {
            String source = token.getInputStream().getSourceName();
            if (source == null)
            {
                output.add(errorOutput("  at worksheet input:\n"));
                output.addAll(Arrays.asList(getLineColOutput(input, token.getLine(), token.getCharPositionInLine(), "")));
            }
            else
            {
                output.add(errorOutput(
                    "  at " + source + " line " + token.getLine() + ":" + token.getCharPositionInLine() + "\n"
                    ));
            }
        }
        return output;
    }

    private static Output[] getExceptionOutput(String input, Exception exc, boolean includeLine)
    {
        int line = 0, col = 0;

        if (exc instanceof RecognitionException)
        {
            line = ((RecognitionException) exc).line;
            col  = ((RecognitionException) exc).charPositionInLine;
        }
        else if (exc instanceof InputException)
        {
            return getStackOutput(input, (InputException) exc).toArray(new Output[0]);
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
        output[1] = errorOutput(pointerStr + (col == 1 ? "^^" : "^^^"));
        output[2] = new StyledTextOutput(message);
        return output;
    }

    private static Output errorOutput(String msg)
    {
        return new StyledTextOutput(
            EnumSet.of(StyledTextOutput.Style.FACE_MONOSPACED, StyledTextOutput.Style.COLOR_RED),
            msg
            );
    }

    private static String getMessageForException(Exception exc)
    {
        if (exc instanceof InputException)
        {
            return exc.getMessage();
        }
        else if (exc instanceof antlr.NoViableAltException &&
                 ((antlr.NoViableAltException) exc).token.getType() == CgsuiteLexer.SLASHES)
        {
            return "Syntax error: Every slash | must be enclosed in braces { }.";
        }
        else
        {
            return "Syntax error.";
        }
    }

}
