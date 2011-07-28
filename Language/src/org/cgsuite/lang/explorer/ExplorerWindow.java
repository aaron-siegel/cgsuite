/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.explorer;

import java.util.List;

/**
 *
 * @author asiegel
 */
public interface ExplorerWindow
{
    public ExplorerNode getSelectedNode();
    public List<ExplorerNode> getSelectionPath();
}
