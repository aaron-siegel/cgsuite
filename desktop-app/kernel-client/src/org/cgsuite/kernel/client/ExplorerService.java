/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.kernel.client;

import org.cgsuite.kernel.NodeInfo;

/**
 *
 * @author asiegel
 */
public interface ExplorerService
{
    void newExplorerWindow(String id);
    void rootNodeCreated(String id, NodeInfo info);
    void nodeExpanded(String id, int nodeOrdinal, Iterable<NodeInfo> leftOptions, Iterable<NodeInfo> rightOptions);
}
