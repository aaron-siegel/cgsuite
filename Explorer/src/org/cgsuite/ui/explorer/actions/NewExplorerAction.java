/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.cgsuite.lang.explorer.Explorer;
import org.cgsuite.lang.explorer.ExplorerWindow;
import org.cgsuite.lang.explorer.ExplorerWindowFactory;
import org.cgsuite.ui.explorer.ExplorerTopComponent;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=ExplorerWindowFactory.class)
public final class NewExplorerAction implements ActionListener, ExplorerWindowFactory
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        ExplorerTopComponent component = new ExplorerTopComponent();
        component.open();
        component.requestActive();
    }

    @Override
    public ExplorerWindow createWindow(Explorer client)
    {
        ExplorerTopComponent component = new ExplorerTopComponent();
        component.setExplorer(client);
        component.open();
        component.requestActive();
        return component;
    }
}
