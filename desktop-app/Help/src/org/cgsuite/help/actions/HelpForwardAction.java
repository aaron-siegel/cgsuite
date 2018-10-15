/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.help.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.cgsuite.help.CgsuiteHelpTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

@ActionID(category = "Help", id = "org.cgsuite.help.actions.HelpForwardAction")
@ActionRegistration(displayName = "#CTL_HelpForwardAction")
@ActionReferences({
    //@ActionReference(path = "Menu/Help", position = 60, separatorAfter = 70),
    @ActionReference(path = "Shortcuts", name = "D-RIGHT")
})
@Messages("CTL_HelpForwardAction=Forward")
public final class HelpForwardAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        CgsuiteHelpTopComponent helpComponent = (CgsuiteHelpTopComponent) WindowManager.getDefault().findTopComponent("CgsuiteHelpTopComponent");
        helpComponent.open();
        helpComponent.navigateForward();
    }
    
}
