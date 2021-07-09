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

package org.cgsuite.ui.explorer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cgsuite.output.Output;

public class ExplorerViewNode
{
    private final static BasicStroke SINGLE_STROKE = new BasicStroke(1.0f);
    private final static BasicStroke DOUBLE_STROKE = new BasicStroke(2.0f);

    private final ExplorerView explorer;
    private final int ordinal;
    private final Output output;
    private List<ExplorerViewNode> leftChildren, rightChildren;
    
    public ExplorerViewNode(ExplorerView explorer, int ordinal, Output output)
    {
        this.explorer = explorer;
        this.ordinal = ordinal;
        this.output = output;
        leftChildren = new ArrayList<ExplorerViewNode>();
        rightChildren = new ArrayList<ExplorerViewNode>();
    }
    
    public int getOrdinal() {
        return ordinal;
    }
    
    public Output getOutput() {
        return output;
    }

    public boolean isRoot()
    {
        synchronized (explorer)
        {
            return explorer.isRoot(this);
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
    
    public List<ExplorerViewNode> getLeftChildren()
    {
        return leftChildren;
    }

    
    public List<ExplorerViewNode> getRightChildren()
    {
        return rightChildren;
    }
    
    public Set<ExplorerViewNode> followers()
    {
        synchronized (explorer)
        {
            return buildFollowers(new HashSet<ExplorerViewNode>());
        }
    }
    
    private Set<ExplorerViewNode> buildFollowers(Set<ExplorerViewNode> followers)
    {
        if (followers.contains(this))
        {
            return followers;
        }
        
        followers.add(this);
        
        for (ExplorerViewNode node : leftChildren)
        {
            node.buildFollowers(followers);
        }
        for (ExplorerViewNode node : rightChildren)
        {
            node.buildFollowers(followers);
        }
        
        return followers;
    }
    
}
