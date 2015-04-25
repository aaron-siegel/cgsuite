/*
 * ExplorerNode.java
 *
 * Created on November 7, 2005, 2:03 PM
 * $Id: ExplorerNode.java,v 1.1 2005/11/10 00:14:38 asiegel Exp $
 */

/* ****************************************************************************

    Combinatorial Game Suite - A program to analyze combinatorial games
    Copyright (C) 2003-06  Aaron Siegel (asiegel@users.sourceforge.net)
    http://cgsuite.sourceforge.net/

    Combinatorial Game Suite is free software; you can redistribute it
    and/or modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2 of the
    License, or (at your option) any later version.

    Combinatorial Game Suite is distributed in the hope that it will be
    useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Combinatorial Game Suite; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA

**************************************************************************** */

package org.cgsuite.lang.explorer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cgsuite.lang.Game;

public class ExplorerNode
{
    private final static BasicStroke SINGLE_STROKE = new BasicStroke(1.0f);
    private final static BasicStroke DOUBLE_STROKE = new BasicStroke(2.0f);

    private final Explorer explorer;
    private final Game g;
    private List<ExplorerNode> leftChildren, rightChildren;
    
    public ExplorerNode(Explorer explorer, Game g)
    {
        this.explorer = explorer;
        this.g = g;
        leftChildren = new ArrayList<ExplorerNode>();
        rightChildren = new ArrayList<ExplorerNode>();
    }

    public Game getG()
    {
        return g;
    }

    public boolean isRoot()
    {
        synchronized(explorer)
        {
            return explorer.isRoot(this);
        }
    }
    
    public List<ExplorerNode> getLeftChildren()
    {
        return Collections.unmodifiableList(leftChildren);
    }
    
    public List<ExplorerNode> getRightChildren()
    {
        return Collections.unmodifiableList(rightChildren);
    }
    
    public ExplorerNode addChild(Game h, boolean left)
    {
        return left? addLeftChild(h) : addRightChild(h);
    }
    
    public ExplorerNode addLeftChild(Game h)
    {
        synchronized (explorer)
        {
            ExplorerNode node = findLeftChild(h);
            
            if (node == null)
            {
                node = explorer.create(h);
                leftChildren.add(node);
            }
            
            return node;
        }
    }
    
    public ExplorerNode addRightChild(Game h)
    {
        synchronized (explorer)
        {
            ExplorerNode node = findRightChild(h);
            
            if (node == null)
            {
                node = explorer.create(h);
                rightChildren.add(node);
            }
            
            return node;
        }
    }
    
    public ExplorerNode findLeftChild(Game h)
    {
        synchronized (explorer)
        {
            for (ExplorerNode node : leftChildren)
            {
                if (node.getG().equals(h))
                    return node;
            }
            return null;
        }
    }
    
    public ExplorerNode findRightChild(Game h)
    {
        synchronized (explorer)
        {
            for (ExplorerNode node : rightChildren)
            {
                if (node.getG().equals(h))
                    return node;
            }
            return null;
        }
    }
    
    public boolean removeLeftChild(ExplorerNode child)
    {
        synchronized (explorer)
        {
            return leftChildren.remove(child);
        }
    }
    
    public boolean removeRightChild(ExplorerNode child)
    {
        synchronized (explorer)
        {
            return rightChildren.remove(child);
        }
    }
    
    public void paintNode(Graphics2D g, int radius, boolean selected)
    {
        g.setColor(Color.lightGray);
        g.fillOval(radius / 8, radius / 8, radius * 2 - radius / 4, radius * 2 - radius / 4);
        if (selected)
        {
            g.setColor(Color.black);
            g.setStroke(DOUBLE_STROKE);
            g.drawOval(radius / 8, radius / 8, radius * 2 - radius / 4, radius * 2 - radius / 4);
            g.setStroke(SINGLE_STROKE);
        }
    }
    
    public Set<ExplorerNode> followers()
    {
        synchronized (explorer)
        {
            return buildFollowers(new HashSet<ExplorerNode>());
        }
    }
    
    private Set<ExplorerNode> buildFollowers(Set<ExplorerNode> followers)
    {
        if (followers.contains(this))
        {
            return followers;
        }
        
        followers.add(this);
        
        for (ExplorerNode node : leftChildren)
        {
            node.buildFollowers(followers);
        }
        for (ExplorerNode node : rightChildren)
        {
            node.buildFollowers(followers);
        }
        
        return followers;
    }
}
