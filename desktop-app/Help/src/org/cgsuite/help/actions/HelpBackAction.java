/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.help.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.cgsuite.help.CgsuiteHelpTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

@ActionID(category = "Help", id = "org.cgsuite.help.actions.HelpBackAction")
@ActionRegistration(displayName = "#CTL_HelpBackAction", lazy = false)
@ActionReferences({
    //@ActionReference(path = "Menu/Help", position = 50),
    @ActionReference(path = "Shortcuts", name = "D-LEFT")
})
@Messages("CTL_HelpBackAction=Back")
public final class HelpBackAction extends AbstractAction {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        CgsuiteHelpTopComponent helpComponent = (CgsuiteHelpTopComponent) WindowManager.getDefault().findTopComponent("CgsuiteHelpTopComponent");
        helpComponent.open();
        helpComponent.navigateBack();
    }
    
}
