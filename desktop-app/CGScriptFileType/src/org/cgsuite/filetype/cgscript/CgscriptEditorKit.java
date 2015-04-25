/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.filetype.cgscript;

import javax.swing.Action;
import javax.swing.text.TextAction;
import org.netbeans.modules.editor.NbEditorKit;

/**
 *
 * @author asiegel
 */
public class CgscriptEditorKit extends NbEditorKit
{
    @Override
    public String getContentType()
    {
        return "text/x-cgscript";
    }

    @Override
    protected Action[] createActions()
    {
        Action[] actions = new Action[] { new ToggleCommentAction("//") };
        return TextAction.augmentList(super.createActions(), actions);
    };
}
