/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.explorer;

import org.cgsuite.core.Game;
import org.cgsuite.output.Output;
import org.cgsuite.output.OutputTarget;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author asiegel
 */
public class Explorer implements OutputTarget
{
    private ExplorerWindow window;
    private List<ExplorerNode> roots;
    private List<ExplorerListener> listeners;
    private List<ExplorerNode> allNodes;

    public Explorer()
    {
        this(null);
    }

    public Explorer(Game g)
    {
        this.roots = new ArrayList<ExplorerNode>();
        this.listeners = new ArrayList<ExplorerListener>();
        this.allNodes = new ArrayList<ExplorerNode>();

        if (g != null)
            addAsRoot(g);

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                window = createWindow(Explorer.this);
            }
        });
    }
   
    public ExplorerWindow createWindow(Explorer client)
    {
        ExplorerTopComponent component = new ExplorerTopComponent();
        component.setExplorer(client);
        component.open();
        component.requestActive();
        return component;
    }

    public void addListener(ExplorerListener l)
    {
        this.listeners.add(l);
    }

    private void fireNodeAddedEvent(final ExplorerNode node)
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
    
    public synchronized List<ExplorerNode> roots()
    {
        List<ExplorerNode> copy = new ArrayList<ExplorerNode>();
        copy.addAll(roots);
        return copy;
    }
    
    public synchronized ExplorerNode firstRoot()
    {
        return roots.isEmpty() ? null : roots.get(0);
    }
    
    public boolean isRoot(ExplorerNode node)
    {
        return roots.contains(node);
    }

    public synchronized ExplorerNode addAsRoot(Game g)
    {
        ExplorerNode root = create(g);
        roots.add(root);
        return root;
    }
    
    public synchronized void add(Game g)
    {
        findOrAdd(g);
    }

    public synchronized ExplorerNode findOrAdd(Game g)
    {
        for (ExplorerNode node : allNodes)
        {
            if (node.getG().equals(g))
                return node;
        }
        
        for (ExplorerNode node : allNodes)
        {
            Game nodeG = node.getG();
            if (nodeG.leftOptions().toSeq().contains(g))
            {
                return node.addLeftChild(g);
            }
            if (nodeG.rightOptions().toSeq().contains(g))
            {
                return node.addRightChild(g);
            }
        }
        
        return addAsRoot(g);
    }

    public synchronized Game selection()
    {
        ExplorerNode node = window.getSelectedNode();
        return (node == null)? null : node.getG();
    }
    
    public synchronized Seq<Game> selectionPath()
    {
        List<ExplorerNode> path = window.getSelectionPath();
        List<Game> list = new ArrayList<Game>();
        for (ExplorerNode node : path)
        {
            list.add(node.getG());
        }
        return JavaConverters.asScalaBuffer(list);
    }

    synchronized ExplorerNode create(Game g)
    {
        ExplorerNode node = new ExplorerNode(this, g);
        allNodes.add(node);
        fireNodeAddedEvent(node);
        return node;
    }

    @Override
    public Output toOutput() {
        throw new RuntimeException("not implemented");
    }
}
