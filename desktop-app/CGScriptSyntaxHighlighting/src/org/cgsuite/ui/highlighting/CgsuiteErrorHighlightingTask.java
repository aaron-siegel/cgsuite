/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import org.antlr.runtime.CommonToken;
import org.cgsuite.lang.parser.CgsuiteParser.SyntaxError;
import org.cgsuite.ui.highlighting.CgsuiteEditorParser.CgsuiteEditorParserResult;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;

/**
 *
 * @author asiegel
 */
public class CgsuiteErrorHighlightingTask extends ParserResultTask<Result>
{
    @Override
    public void run(Result result, SchedulerEvent event)
    {
        try
        {
            CgsuiteEditorParserResult cgsResult = (CgsuiteEditorParserResult) result;
            List<SyntaxError> errors = new ArrayList<>();
            errors.addAll(cgsResult.getCgsuiteLexer().getErrors());
            errors.addAll(cgsResult.getCgsuiteParser().getErrors());
            Document document = result.getSnapshot().getSource().getDocument(false);
            List<ErrorDescription> errorDescriptions = new ArrayList<>();
            
            for (SyntaxError error : errors)
            {
                int startIndex, stopIndex;
                CommonToken token = (CommonToken) error.getException().token;
                
                if (token == null)
                {
                    // Lexer errors have no token.  Just highlight the offending character
                    startIndex = error.getException().index;
                    stopIndex = startIndex+2;
                }
                else
                {
                    // If there's a token, highlight the full token
                    startIndex = token.getStartIndex();
                    stopIndex = token.getStopIndex()+1;
                }

                ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
                    Severity.ERROR,
                    error.getMessage(),
                    document,
                    document.createPosition(startIndex),
                    document.createPosition(stopIndex)
                    );
                errorDescriptions.add(errorDescription);
            }
            HintsController.setErrors(document, "cgscript", errorDescriptions);
        }
        catch (Exception ex)
        {
        }
    }

    @Override
    public int getPriority()
    {
        return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass()
    {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel()
    {
    }
}
