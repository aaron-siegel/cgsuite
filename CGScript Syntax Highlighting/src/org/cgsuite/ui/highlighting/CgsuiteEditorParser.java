/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import javax.swing.event.ChangeListener;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.cgsuite.lang.parser.CgsuiteLexer;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.cgsuite.lang.parser.CgsuiteTreeAdaptor;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 * @author asiegel
 */
public class CgsuiteEditorParser extends Parser
{
    private Snapshot snapshot;
    private CgsuiteParser cgsuiteParser;

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent sme) throws ParseException
    {
        this.snapshot = snapshot;
        ANTLRStringStream input = new ANTLRStringStream(snapshot.getText().toString());
        CgsuiteLexer lexer = new CgsuiteLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cgsuiteParser = new CgsuiteParser(tokens);
        cgsuiteParser.setTreeAdaptor(new CgsuiteTreeAdaptor());
        try
        {
            cgsuiteParser.compilationUnit();
        }
        catch (Exception ex)
        {
            throw new ParseException("Exception thrown by ANTLR delegate: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Result getResult(Task task)
    {
        return new CgsuiteEditorParserResult(snapshot, cgsuiteParser);
    }

    @Override
    public void cancel()
    {
    }

    @Override
    public void addChangeListener(ChangeListener cl)
    {
    }

    @Override
    public void removeChangeListener(ChangeListener cl)
    {
    }

    public static class CgsuiteEditorParserResult extends Result
    {
        private CgsuiteParser cgsuiteParser;
        private boolean valid;

        CgsuiteEditorParserResult(Snapshot snapshot, CgsuiteParser cgsuiteParser)
        {
            super(snapshot);
            this.cgsuiteParser = cgsuiteParser;
            this.valid = true;
        }

        public CgsuiteParser getCgsuiteParser() throws ParseException
        {
            if (valid)
                return cgsuiteParser;
            else
                throw new ParseException();
        }

        @Override
        protected void invalidate()
        {
            valid = false;
        }
    }

}
