/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.script.actions;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(
        category = "File",
        id = "org.cgsuite.ui.script.actions.NewClassFileAction"
)
@ActionRegistration(
        iconBase = "org/cgsuite/ui/script/actions/thermograph-16x16.png",
        displayName = "#CTL_NewClassFileAction"
)
@ActionReference(path = "Menu/File/New CGScript File", position = 100)
@NbBundle.Messages("CTL_NewClassFileAction=Class")
public final class NewClassFileAction extends NewFileAction {

    @Override
    public String getTemplate() {
        return "Templates/CGSuite/ClassTemplate.cgs";
    }

    @Override
    public String getApproveText() {
        return "New Class";
    }
    
}
