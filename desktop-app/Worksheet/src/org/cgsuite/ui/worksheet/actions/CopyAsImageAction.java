/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.worksheet.actions;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallbackSystemAction;

@ActionID(category = "Edit", id = "org.cgsuite.ui.worksheet.actions.CopyAsImageAction")
@ActionRegistration(displayName = "#CTL_CopyAsImageAction")
@ActionReferences({
    @ActionReference(path = "Menu/Edit", position = 1150)
})
@NbBundle.Messages("CTL_CopyAsImageAction=Copy as Image")
public class CopyAsImageAction extends CallbackSystemAction {

    public final static String ACTION_MAP_KEY = "cgsuite/copy-as-image";

    @Override
    public String getName() {
        return NbBundle.getMessage(CopyAsImageAction.class, "CTL_CopyAsImageAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.cgsuite.ui.worksheet.actions.CopyAsImageAction");
    }

    @Override
    public Object getActionMapKey() {
        return ACTION_MAP_KEY;
    }

}
