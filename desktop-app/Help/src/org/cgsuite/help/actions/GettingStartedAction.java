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
        CgsuiteHelpTopComponent.openAndNavigateTo(CgsuiteHelpTopComponent.GETTING_STARTED_PAGE);
    }
}
