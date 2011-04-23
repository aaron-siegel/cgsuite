/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import org.cgsuite.lang.explorer.Explorer;
import org.cgsuite.lang.explorer.ExplorerWindowCreator;
import org.cgsuite.ui.explorer.ExplorerTopComponent;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=ExplorerWindowCreator.class)
public final class NewExplorerAction implements ActionListener, ExplorerWindowCreator
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        ExplorerTopComponent component = new ExplorerTopComponent();
        component.open();
        component.requestActive();
    }

    @Override
    public void createWindow(final Explorer client)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                ExplorerTopComponent component = new ExplorerTopComponent();
                component.setExplorer(client);
                component.open();
                component.requestActive();
            }
        });
    }
}
