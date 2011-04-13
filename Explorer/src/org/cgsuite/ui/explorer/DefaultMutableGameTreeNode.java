/*
 * DefaultMutableGameTreeNode.java
 *
 * Created on November 7, 2005, 2:03 PM
 * $Id: DefaultMutableGameTreeNode.java,v 1.1 2005/11/10 00:14:38 asiegel Exp $
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

package org.cgsuite.ui.explorer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultMutableGameTreeNode implements GameTreeNode
{
    private final static BasicStroke SINGLE_STROKE = new BasicStroke(1.0f);
    private final static BasicStroke DOUBLE_STROKE = new BasicStroke(2.0f);
    
    private List<DefaultMutableGameTreeNode> leftChildren, rightChildren;
    
    public DefaultMutableGameTreeNode()
    {
        leftChildren = new ArrayList<DefaultMutableGameTreeNode>();
        rightChildren = new ArrayList<DefaultMutableGameTreeNode>();
    }
    
    @Override
    public List<? extends DefaultMutableGameTreeNode> getLeftChildren()
    {
        return Collections.unmodifiableList(leftChildren);
    }
    
    @Override
    public List<? extends DefaultMutableGameTreeNode> getRightChildren()
    {
        return Collections.unmodifiableList(rightChildren);
    }
    
    public void addLeftChild(DefaultMutableGameTreeNode child)
    {
        if (!leftChildren.contains(child))
        {
            leftChildren.add(child);
        }
    }
    
    public void addRightChild(DefaultMutableGameTreeNode child)
    {
        if (!rightChildren.contains(child))
        {
            rightChildren.add(child);
        }
    }
    
    public boolean removeLeftChild(DefaultMutableGameTreeNode child)
    {
        return leftChildren.remove(child);
    }
    
    public boolean removeRightChild(DefaultMutableGameTreeNode child)
    {
        return rightChildren.remove(child);
    }
    
    @Override
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
    
    public Set<DefaultMutableGameTreeNode> followers()
    {
        return buildFollowers(new HashSet<DefaultMutableGameTreeNode>());
    }
    
    private Set<DefaultMutableGameTreeNode> buildFollowers(Set<DefaultMutableGameTreeNode> followers)
    {
        if (followers.contains(this))
        {
            return followers;
        }
        
        followers.add(this);
        
        for (DefaultMutableGameTreeNode node : leftChildren)
        {
            node.buildFollowers(followers);
        }
        for (DefaultMutableGameTreeNode node : rightChildren)
        {
            node.buildFollowers(followers);
        }
        
        return followers;
    }
}
