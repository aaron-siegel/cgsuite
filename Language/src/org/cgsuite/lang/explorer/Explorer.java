/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteList;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Game;
import org.openide.util.Lookup;

/**
 *
 * @author asiegel
 */
public class Explorer extends CgsuiteObject
{
    public static final CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Explorer");

    private ExplorerWindow window;
    private List<ExplorerNode> roots;
    private List<ExplorerListener> listeners;
    private Map<Game,ExplorerNode> gameLookup;

    public Explorer()
    {
        this(null);
    }

    public Explorer(Game g)
    {
        super(TYPE);

        this.roots = new ArrayList<ExplorerNode>();
        this.listeners = new ArrayList<ExplorerListener>();
        this.gameLookup = new HashMap<Game,ExplorerNode>();

        if (g != null)
            addAsRoot(g);

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                ExplorerWindowFactory ewc = Lookup.getDefault().lookup(ExplorerWindowFactory.class);
                assert ewc != null;
                window = ewc.createWindow(Explorer.this);
            }
        });
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
        ExplorerNode root = new ExplorerNode(this, g);
        roots.add(root);
        return root;
    }

    public synchronized ExplorerNode findOrAdd(Game g)
    {
        ExplorerNode node = lookupGame(g);
        if (node == null)
            node = addAsRoot(g);
        
        return node;
    }

    public synchronized Game getSelection()
    {
        ExplorerNode node = window.getSelectedNode();
        return (node == null)? null : node.getG();
    }
    
    public synchronized CgsuiteList getSelectionPath()
    {
        List<ExplorerNode> path = window.getSelectionPath();
        CgsuiteList list = new CgsuiteList();
        for (ExplorerNode node : path)
        {
            list.add(node.getG());
        }
        return list;
    }

    public synchronized ExplorerNode lookupGame(Game g)
    {
        return gameLookup.get(g);
    }

    synchronized ExplorerNode create(Game g)
    {
        ExplorerNode node = new ExplorerNode(this, g);
        if (!gameLookup.containsKey(g))
            gameLookup.put(g, node);
        fireNodeAddedEvent(node);
        return node;
    }
}
