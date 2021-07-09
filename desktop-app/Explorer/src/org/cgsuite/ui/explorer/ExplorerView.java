/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import org.cgsuite.kernel.NodeInfo;
import org.cgsuite.output.Output;

/**
 *
 * @author asiegel
 */
public class ExplorerView
{
    private String id;
    private List<ExplorerViewNode> roots;
    private List<ExplorerListener> listeners;
    private Map<Integer, ExplorerViewNode> allNodes;

    public ExplorerView(String id)
    {
        this.id = id;
        this.roots = new ArrayList<ExplorerViewNode>();
        this.listeners = new ArrayList<ExplorerListener>();
        this.allNodes = new HashMap<Integer, ExplorerViewNode>();
    }
    
    public String getId()
    {
        return id;
    }
    
    public void createRootNode(int ordinal, Output output)
    {
        roots.add(createNode(ordinal, output));
    }
    
    private ExplorerViewNode createNode(int ordinal, Output output)
    {
        if (allNodes.containsKey(ordinal)) {
            return allNodes.get(ordinal);
        } else {
            ExplorerViewNode node = new ExplorerViewNode(this, ordinal, output);
            allNodes.put(ordinal, node);
            fireNodeAddedEvent(node);
            return node;
        }
    }
    
    public void expandNode(int ordinal, Iterable<NodeInfo> leftOptions, Iterable<NodeInfo> rightOptions)
    {
        ExplorerViewNode node = allNodes.get(ordinal);
        if (node != null) {
            for (NodeInfo leftOption : leftOptions) {
                node.getLeftChildren().add(createNode(leftOption.nodeOrdinal(), leftOption.output()));
            }
            for (NodeInfo rightOption: rightOptions) {
                node.getRightChildren().add(createNode(rightOption.nodeOrdinal(), rightOption.output()));
            }
        }
    }

    public void addListener(ExplorerListener l)
    {
        this.listeners.add(l);
    }

    private void fireNodeAddedEvent(final ExplorerViewNode node)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                for (ExplorerListener l : listeners)
                {
                    l.nodeAdded(node);
                }
            }
        });
    }

    public synchronized List<ExplorerViewNode> roots()
    {
        return java.util.Collections.unmodifiableList(roots);
    }
    
    public synchronized ExplorerViewNode firstRoot()
    {
        return roots.isEmpty() ? null : roots.get(0);
    }
    
    public boolean isRoot(ExplorerViewNode node)
    {
        return roots.contains(node);
    }

}
