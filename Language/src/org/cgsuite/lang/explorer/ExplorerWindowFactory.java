/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.explorer;

import org.openide.windows.TopComponent;

/**
 *
 * @author asiegel
 */
public interface ExplorerWindowFactory
{
    public ExplorerWindow createWindow(Explorer client);
}
