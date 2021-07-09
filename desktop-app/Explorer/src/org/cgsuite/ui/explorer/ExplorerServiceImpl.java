/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer;

import java.util.HashMap;
import java.util.Map;
import org.cgsuite.kernel.NodeInfo;
import org.cgsuite.kernel.client.ExplorerService;
import org.cgsuite.output.Output;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author asiegel
 */
@ServiceProvider(service=ExplorerService.class)
public class ExplorerServiceImpl implements ExplorerService
{
    
    private final static Map<String, ExplorerView> KNOWN_EXPLORERS = new HashMap<String, ExplorerView>();
    
    @Override
    public void newExplorerWindow(String id)
    {
        ExplorerTopComponent component = new ExplorerTopComponent();
        ExplorerView explorerView = new ExplorerView(id);
        KNOWN_EXPLORERS.put(id, explorerView);
        component.setExplorerView(explorerView);
        component.open();
        component.requestActive();
    }
    
    @Override
    public void rootNodeCreated(String id, NodeInfo info)
    {
        ExplorerView explorer = KNOWN_EXPLORERS.get(id);
        explorer.createRootNode(info.nodeOrdinal(), info.output());
    }
    
    @Override
    public void nodeExpanded(String id, int nodeOrdinal, Iterable<NodeInfo> leftOptions, Iterable<NodeInfo> rightOptions)
    {
        ExplorerView explorer = KNOWN_EXPLORERS.get(id);
        explorer.expandNode(nodeOrdinal, leftOptions, rightOptions);
    }
    
}
