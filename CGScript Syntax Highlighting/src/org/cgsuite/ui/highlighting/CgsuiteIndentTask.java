/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import javax.swing.text.BadLocationException;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.cgsuite.lang.CgsuiteLexer;
import org.cgsuite.lang.CgsuiteParser;
import org.cgsuite.lang.CgsuiteTreeAdaptor;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;

/**
 *
 * @author asiegel
 */
public class CgsuiteIndentTask implements IndentTask
{
    private Context context;

    CgsuiteIndentTask(Context context)
    {
        this.context = context;
    }

    @Override
    public void reindent() throws BadLocationException
    {
        int offset = context.lineStartOffset(context.caretOffset());
        int prevLineOffset = context.lineStartOffset(offset-1);
        int prevIndent = context.lineIndent(prevLineOffset);
        context.modifyIndent(offset, prevIndent);

//        ANTLRStringStream input = new ANTLRStringStream(context.document().getText(0, context.document().getLength()));
//        CgsuiteLexer lexer = new CgsuiteLexer(input);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        CgsuiteParser parser = new CgsuiteParser(tokens);
//        parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
    }

    @Override
    public ExtraLock indentLock()
    {
        return null;
    }
}
