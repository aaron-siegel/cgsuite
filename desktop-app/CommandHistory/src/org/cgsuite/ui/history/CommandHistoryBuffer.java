/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.history;

import javax.swing.ListModel;

/**
 *
 * @author asiegel
 */
public interface CommandHistoryBuffer extends ListModel
{
    public int getSize();
    public String getElementAt(int index);
    public void addCommand(String command);
    public void addCommandListener(CommandListener l);
    public void removeCommandListener(CommandListener l);
}
