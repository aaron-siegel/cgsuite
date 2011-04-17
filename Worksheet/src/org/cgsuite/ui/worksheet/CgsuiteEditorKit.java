/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.worksheet;

import javax.swing.text.Document;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorKit;

/**
 *
 * @author asiegel
 */
public class CgsuiteEditorKit extends NbEditorKit
{
    @Override
    public Document createDefaultDocument()
    {
        return new NbEditorDocument("text/x-cgsuite")
        {
            @Override
            public ExtSyntaxSupport getSyntaxSupport()
            {
                return new ExtSyntaxSupport(this);
            }
        };
    }

    @Override
    public String getContentType()
    {
        return "text/x-cgsuite";
    }
}
