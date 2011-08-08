/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.help.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.cgsuite.help.CgsuiteHelpTopComponent;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

@ActionID(category = "Help",
id = "org.cgsuite.help.actions.GettingStartedAction")
@ActionRegistration(displayName = "#CTL_GettingStartedAction")
@ActionReferences({
    @ActionReference(path = "Menu/Help", position = 200)
})
@Messages("CTL_GettingStartedAction=Getting Started")
public final class GettingStartedAction implements ActionListener
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        CgsuiteHelpTopComponent helpComponent = (CgsuiteHelpTopComponent) WindowManager.getDefault().findTopComponent("CgsuiteHelpTopComponent");
        helpComponent.open();
        helpComponent.getHelpViewer().setCurrentID("tutorials.using-cgsuite.Getting Started");
    }
}
