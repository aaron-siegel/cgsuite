/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.explorer;

import java.util.HashMap;
import java.util.Map;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Game;

/**
 *
 * @author asiegel
 */
public class Explorer extends CgsuiteObject
{
    public static final CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Explorer");
    public static Explorer INSTANCE;

    private ExplorerNode root;
    Map<Game,ExplorerNode> gameLookup;

    public Explorer()
    {
        super(TYPE);
        Explorer.INSTANCE = this;
        this.root = new ExplorerNode(this, null);
        this.gameLookup = new HashMap<Game,ExplorerNode>();
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
        }
        return node;
    }
}
