/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import java.util.Collection;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;

/**
 *
 * @author asiegel
 */
public class CgsuiteEditorParserFactory extends ParserFactory
{
    @Override
    public Parser createParser(Collection<Snapshot> snapshots)
    {
        return new CgsuiteEditorParser();
    }
}
