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

    private ExplorerNode root;
    private List<ExplorerListener> listeners;
    Map<Game,ExplorerNode> gameLookup;

    public Explorer()
    {
        this(null);
    }

    public Explorer(Game g)
    {
        super(TYPE);

        this.root = new ExplorerNode(this, null);
        this.listeners = new ArrayList<ExplorerListener>();
        this.gameLookup = new HashMap<Game,ExplorerNode>();

        if (g != null)
            addAsRoot(g);

        ExplorerWindowCreator ewc = Lookup.getDefault().lookup(ExplorerWindowCreator.class);
        if (ewc != null)
            ewc.createWindow(this);
    }

    public void addListener(ExplorerListener l)
    {
        this.listeners.add(l);
    }

    void fireNodeAddedEvent(final ExplorerNode node)
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

    public void addAsRoot(Game g)
    {
        this.root.addLeftChild(g);
    }

    public ExplorerNode getRootNode()
    {
        return root;
    }

    public ExplorerNode lookupGame(Game g)
    {
        return gameLookup.get(g);
    }

    ExplorerNode lookupOrCreate(Game g)
    {
        ExplorerNode node = gameLookup.get(g);
        if (node == null)
        {
            node = new ExplorerNode(this, g);
            this.gameLookup.put(g, node);
            fireNodeAddedEvent(node);
        }
        return node;
    }
}
