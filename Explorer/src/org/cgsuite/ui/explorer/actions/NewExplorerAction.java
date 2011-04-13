/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.cgsuite.ui.explorer.ExplorerTopComponent;

public final class NewExplorerAction implements ActionListener
{

    @Override
    public void actionPerformed(ActionEvent e)
    {
        ExplorerTopComponent component = new ExplorerTopComponent();
        component.open();
        component.requestActive();
    }
}
