/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer;

import java.util.List;

/**
 *
 * @author asiegel
 */
public interface ExplorerWindow
{
    public ExplorerViewNode getSelectedNode();
    public List<ExplorerViewNode> getSelectionPath();
}
