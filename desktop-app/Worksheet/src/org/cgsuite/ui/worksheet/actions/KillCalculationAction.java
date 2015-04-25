/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.worksheet.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.cgsuite.ui.worksheet.WorksheetTopComponent;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

@ActionID(category = "System",
id = "org.cgsuite.ui.worksheet.actions.KillCalculationAction")
@ActionRegistration(displayName = "#CTL_KillCalculationAction")
@ActionReferences({
    @ActionReference(path = "Menu/System", position = 100)
})
@Messages("CTL_KillCalculationAction=Kill Calculation")
public final class KillCalculationAction implements ActionListener
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        WorksheetTopComponent tc = (WorksheetTopComponent) WindowManager.getDefault().findTopComponent("WorksheetTopComponent");
        tc.getWorksheetPanel().killCalculation();
    }
}
