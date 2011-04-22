/*
 * ExplorerTreeComponent.java
 *
 * Created on November 7, 2005, 1:52 PM
 * $Id: ExplorerTreeComponent.java,v 1.3 2006/04/06 01:10:38 asiegel Exp $
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import org.cgsuite.lang.explorer.Explorer;
import org.cgsuite.lang.explorer.ExplorerNode;

public class ExplorerTreeComponent extends JComponent implements Scrollable
{
    public final static String NODE_RADIUS_PROPERTY = ExplorerTreeComponent.class.getName() + ".nodeRadius";
    
    private int nodeRadius;
    
    private Explorer explorer;
    
    private LinkedList<ExplorerNode> selectionPath;
    private NodeLayout selectedLayout;
    
    // Layout info
    private Set<NodeLayout> layouts;
    private Map<ExplorerNode,NodeLayout> primaryLayouts;
    private List<NodeLayout> rightmostLayouts;
    private int maxXCoord;
    private int maxDepth;
    
    private boolean treeValid = false;
    
    private boolean layoutRoot;
    
    private Set<ExplorerTreeListener> listeners;
    
    public ExplorerTreeComponent()
    {
        setLayout(null);
        setBackground(Color.white);
        nodeRadius = 16;
        
        explorer = Explorer.INSTANCE;
        listeners = new HashSet<ExplorerTreeListener>();
        
        selectionPath = new LinkedList<ExplorerNode>();
        
        layouts = new HashSet<NodeLayout>();
        primaryLayouts = new HashMap<ExplorerNode,NodeLayout>();
        rightmostLayouts = new ArrayList<NodeLayout>();
        layoutRoot = false;
        
        ActionMap am = getActionMap();
        InputMap im = getInputMap();
        String prefix = getClass().toString();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), prefix + ".up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), prefix + ".down");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), prefix + ".left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), prefix + ".right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), prefix + ".delete");
        
        am.put(prefix + ".up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                previousPosition();
        }});
        am.put(prefix + ".down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                nextPosition();
        }});
        am.put(prefix + ".left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                nextVariation(true);
        }});
        am.put(prefix + ".right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                nextVariation(false);
        }});
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                List<ExplorerNode> path = pathTo(evt.getX(), evt.getY());
                if (path != null)
                {
                    setSelectionPath(path);
                    requestFocusInWindow();
                }
        }});
    }
    
    @Override
    public Dimension getMinimumSize()
    {
        return new Dimension(nodeRadius * 10, nodeRadius * 14);
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        if (!treeValid)
        {
            doLayout();
        }
        return new Dimension(
            nodeRadius * 2 * (maxXCoord + 1) + 2,
            nodeRadius * (5 * maxDepth + 2) + 2
            );
//            nodeRadius * 2 * Math.max(maxXCoord + 1, 5) + 2,
//            nodeRadius * Math.max(5 * maxDepth + 2, 14) + 2
//            );
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }
    
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return nodeRadius;
    }
    
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return nodeRadius * 8;
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth()
    {
        return false;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }
    
    private void previousPosition()
    {
        if (selectionPath.size() <= (layoutRoot ? 0 : 1))
        {
            getToolkit().beep();
        }
        else
        {
            selectionPath.removeLast();
            refresh();
            fireTreeSelectionChanged();
        }
    }
    
    private void nextPosition()
    {
        ExplorerNode selNode = (selectionPath.isEmpty() ? explorer.getRootNode() : selectionPath.getLast());
        
        boolean left = true;
        if (selectionPath.size() > (layoutRoot ? 0 : 1))
        {
            ExplorerNode prevNode = (selectionPath.size() == 1 ? explorer.getRootNode() : selectionPath.get(selectionPath.size()-2));
            left = !prevNode.getLeftChildren().contains(selNode);
        }
        
        List<? extends ExplorerNode>
            primary = (left ? selNode.getLeftChildren() : selNode.getRightChildren()),
            secondary = (left ? selNode.getRightChildren() : selNode.getLeftChildren());
        
        if (primary.isEmpty() && secondary.isEmpty())
        {
            getToolkit().beep();
        }
        else
        {
            selectionPath.add((primary.isEmpty() ? secondary : primary).get(0));
            refresh();
            fireTreeSelectionChanged();
        }
    }
    
    private void nextVariation(boolean left)
    {
        if (selectionPath.isEmpty())
        {
            return;
        }
        
        ExplorerNode selNode = selectionPath.getLast();
        ExplorerNode prevNode = (selectionPath.size() == 1 ? explorer.getRootNode() : selectionPath.get(selectionPath.size()-2));
        
        ExplorerNode next;
        
        if (layoutRoot || selectionPath.size() > 1)
        {
            List<? extends ExplorerNode> children = (left ? prevNode.getLeftChildren() : prevNode.getRightChildren());
            if (children.isEmpty())
            {
                getToolkit().beep();
                return;
            }
            int index = children.indexOf(selNode) + 1;
            if (index == children.size())
            {
                index = 0;
            }
            next = children.get(index);
        }
        else
        {
            List<ExplorerNode> children = new ArrayList<ExplorerNode>();
            children.addAll(prevNode.getLeftChildren());
            children.addAll(prevNode.getRightChildren());
            assert children.contains(selNode);
            int index = children.indexOf(selNode) + (left ? -1 : 1);
            if (index == -1)
            {
                index = children.size()-1;
            }
            if (index == children.size())
            {
                index = 0;
            }
            next = children.get(index);
        }
        
        selectionPath.removeLast();
        selectionPath.add(next);
        refresh();
        fireTreeSelectionChanged();
    }
    
    public void addExplorerTreeListener(ExplorerTreeListener l)
    {
        listeners.add(l);
    }
    
    public void removeGameTreeListener(ExplorerTreeListener l)
    {
        listeners.remove(l);
    }
    
    protected void fireTreeSelectionChanged()
    {
        for (ExplorerTreeListener l : listeners)
        {
            l.selectionPathChanged(selectionPath);
        }
    }
    
    public boolean getLayoutRoot()
    {
        return layoutRoot;
    }
    
    public void setLayoutRoot(boolean layoutRoot)
    {
        this.layoutRoot = layoutRoot;
        refresh();
    }
    
    public LinkedList<ExplorerNode> pathTo(int x, int y)
    {
        NodeLayout layout = layoutAt(x, y);
        return layout == null ? null : layout.pathTo();
    }
    
    private NodeLayout layoutAt(int x, int y)
    {
        for (NodeLayout layout : layouts)
        {
            if (x >= nodeRadius * 2 * layout.xCoord &&
                y >= nodeRadius * 5 * layout.yCoord &&
                x < nodeRadius * (2 * layout.xCoord + 2) &&
                y < nodeRadius * (5 * layout.yCoord + 2))
            {
                return layout;
            }
        }
        return null;
    }
    
    public void refresh()
    {
        treeValid = false;
        revalidate();
    }
    
    public List<ExplorerNode> getSelectionPath()
    {
        return Collections.unmodifiableList(selectionPath);
    }
    
    public ExplorerNode getSelectedNode()
    {
        if (selectionPath.isEmpty())
        {
            return layoutRoot ? explorer.getRootNode() : null;
        }
        else
        {
            return selectionPath.getLast();
        }
    }
    
    public void setSelectionPath(List<ExplorerNode> path)
    {
        if (!path.equals(selectionPath))
        {
            selectionPath.clear();
            selectionPath.addAll(path);
            refresh();
            fireTreeSelectionChanged();
        }
    }
    
    public void setSelectedNode(ExplorerNode node)
    {
        selectionPath.clear();
        findPathToNode(selectionPath, node);
        refresh();
        fireTreeSelectionChanged();
    }
    
    private boolean findPathToNode(LinkedList<ExplorerNode> path, ExplorerNode target)
    {
        ExplorerNode prev = (path.isEmpty() ? explorer.getRootNode() : path.getLast());
        if (prev == target)
        {
            return true;
        }
        for (ExplorerNode node : prev.getLeftChildren())
        {
            if (!path.contains(node))
            {
                path.add(node);
                if (findPathToNode(path, target))
                {
                    return true;
                }
                path.removeLast();
            }
        }
        for (ExplorerNode node : prev.getRightChildren())
        {
            if (!path.contains(node))
            {
                path.add(node);
                if (findPathToNode(path, target))
                {
                    return true;
                }
                path.removeLast();
            }
        }
        return false;
    }
    
    public Rectangle getSelectionRectangle()
    {
        if (selectedLayout == null)
        {
            return new Rectangle();
        }
        else
        {
            return new Rectangle(
                nodeRadius * 2 * selectedLayout.xCoord,
                nodeRadius * 5 * selectedLayout.yCoord,
                nodeRadius * 2 + 2,
                nodeRadius * 2 + 2
                );
        }
    }
    
    public int getNodeRadius()
    {
        return nodeRadius;
    }
    
    public void setNodeRadius(int nodeRadius)
    {
        if (nodeRadius < 4 || (nodeRadius % 4) != 0)
        {
            throw new IllegalArgumentException("nodeRadius < 4 || (nodeRadius % 4) != 0");
        }
        else if (nodeRadius != this.nodeRadius)
        {
            int oldNodeRadius = this.nodeRadius;
            this.nodeRadius = nodeRadius;
            revalidate();
            repaint();
            firePropertyChange(NODE_RADIUS_PROPERTY, oldNodeRadius, nodeRadius);
        }
    }
    
    @Override
    public void doLayout()
    {
        if (!treeValid)
        {
            layoutTree();
        }
    }
    
    private void layoutTree()
    {
        maxDepth = maxXCoord = -1;
        layouts.clear();
        primaryLayouts.clear();
        rightmostLayouts.clear();
        
        if (explorer != null)
        {
            layoutNode(new LinkedList<ExplorerNode>(), 0, null, null);
        }
        
        for (NodeLayout layout : rightmostLayouts)
        {
            maxXCoord = Math.max(maxXCoord, layout.xCoord);
        }
        treeValid = true;
        
        revalidate();
        repaint();
    }
    
    private NodeLayout layoutNode(LinkedList<ExplorerNode> path, int depth, ExplorerNode leftKoNode, ExplorerNode rightKoNode)
    {
        if (path.isEmpty() && !layoutRoot)
        {
            layoutChildren(path, -1, null, null);
            return null;
        }
        
        ExplorerNode node = (path.isEmpty() ? explorer.getRootNode() : path.getLast());
        
        NodeLayout layout = new NodeLayout();
        layouts.add(layout);
        layout.node = node;
        layout.yCoord = depth;
        
        if (path.equals(selectionPath))
        {
            selectedLayout = layout;
        }
        
        if (primaryLayouts.containsKey(node) ||
            selectionPath != null && selectionPath.contains(node) && !compatibleLists(path, selectionPath))
        {
            // Either:
            //  (i) We've already laid out this node, or
            // (ii) This node is on the selection path, but we're laying it out
            //      along a different path.
            // In either case the layout is a transposition.
            layout.isTransposition = true;
            layout.xCoord = allocateXCoord(layout, depth, leftKoNode != null);
            return layout;
        }
        
        // Not a transposition.
        primaryLayouts.put(node, layout);
        
        boolean layoutLeftKoNode = false, layoutRightKoNode = false;
        
        if (leftKoNode == null)
        {
            // Try to assign a left ko for this node.
            // Note we always traverse the left options in reverse order:
            // this is to ensure that the "main" variation(s) appear closest
            // to the center of the tree.
            for (ListIterator<? extends ExplorerNode> i = node.getLeftChildren().listIterator(node.getLeftChildren().size()); i.hasPrevious();)
            {
                ExplorerNode next = i.previous();
                if (next.getRightChildren().contains(node))
                {
                    // Found one!
                    leftKoNode = next;
                    layoutLeftKoNode = true;
                    break;
                }
            }
        }
        if (rightKoNode == null)
        {
            for (ExplorerNode next : node.getRightChildren())
            {
                if (next.getLeftChildren().contains(node))
                {
                    rightKoNode = next;
                    layoutRightKoNode = true;
                    break;
                }
            }
        }
        
        if (layoutLeftKoNode)
        {
            path.add(leftKoNode);
            makeParent(layout, layoutNode(path, depth, null, node), true);
            path.removeLast();
        }
        
        int minXCoord = allocateXCoord(layout, depth, leftKoNode != null);
        layout.xCoord = layoutChildren(path, depth, leftKoNode, rightKoNode);
        if (layout.xCoord < minXCoord)
        {
            shiftLayout(layout, minXCoord - layout.xCoord, false);
        }
        
        if (layoutRightKoNode)
        {
            path.add(rightKoNode);
            makeParent(layout, layoutNode(path, depth, node, null), false);
            path.removeLast();
        }
        
        return layout;
    }
    
    private int layoutChildren(LinkedList<ExplorerNode> path, int depth, ExplorerNode leftKoNode, ExplorerNode rightKoNode)
    {
        ExplorerNode node = (path.isEmpty() ? explorer.getRootNode() : path.getLast());
        NodeLayout layout = primaryLayouts.get(node);
        
        int rightmostLeftNode = -1;
        
        // As before, the left edges are traversed in reverse order.
        for (ListIterator<? extends ExplorerNode> i = node.getLeftChildren().listIterator(node.getLeftChildren().size()); i.hasPrevious();)
        {
            ExplorerNode next = i.previous();
            if (!next.equals(leftKoNode))
            {
                path.add(next);
                NodeLayout target = layoutNode(path, depth + 1, null, null);
                path.removeLast();
                rightmostLeftNode = target.xCoord;
                makeParent(layout, target, true);
            }
        }
        int leftmostRightNode = -1;
        for (ExplorerNode next : node.getRightChildren())
        {
            if (!next.equals(rightKoNode))
            {
                path.add(next);
                NodeLayout target = layoutNode(path, depth + 1, null, null);
                path.removeLast();
                if (leftmostRightNode == -1)
                {
                    leftmostRightNode = target.xCoord;
                }
                makeParent(layout, target, false);
            }
        }
        
        if (rightmostLeftNode == -1)
        {
            return leftmostRightNode - 1;
        }
        else if (leftmostRightNode == -1)
        {
            return rightmostLeftNode + 1;
        }
        else
        {
            return (leftmostRightNode + rightmostLeftNode) / 2;
        }
    }
    
    private int allocateXCoord(NodeLayout layout, int depth, boolean useKoSpacing)
    {
        while (depth > maxDepth)
        {
            maxDepth++;
            rightmostLayouts.add(null);
        }
        
        int xCoord;
        NodeLayout currentRightmost = rightmostLayouts.get(depth);
        if (currentRightmost == null)
        {
            xCoord = 0;
        }
        else
        {
            xCoord = currentRightmost.xCoord + (useKoSpacing ? 3 : 2);
        }
        rightmostLayouts.set(depth, layout);
        return xCoord;
    }
    
    private static boolean compatibleLists(List<ExplorerNode> list1, List<ExplorerNode> list2)
    {
        int match = Math.min(list1.size(), list2.size());
        return list1.subList(0, match).equals(list2.subList(0, match));
    }
    
    private void shiftLayout(NodeLayout layout, int shift, boolean shiftKos)
    {
        layout.xCoord += shift;
        for (NodeLayout child : layout.children)
        {
            if (shiftKos || child.yCoord != layout.yCoord)
            {
                shiftLayout(child, shift, true);
            }
        }
    }
    
    private void makeParent(NodeLayout parent, NodeLayout child, boolean isLeftChild)
    {
        child.parent = parent;
        child.isLeftChild = isLeftChild;
        if (parent != null)
        {
            parent.children.add(child);
        }
    }
    
    @Override
    protected void paintComponent(Graphics _g)
    {
        int width = getSize().width;
        
        Graphics2D g = (Graphics2D) _g;
        g.setBackground(getBackground());
        g.clearRect(0, 0, getSize().width, getSize().height);
        g.translate(1, 1);
        g.setColor(Color.black);
        
        // Paint the links first, to make sure that the nodes cover them properly.
        for (NodeLayout layout : layouts)
        {
            if (!g.hitClip(0, nodeRadius * (5 * layout.yCoord + 1), width, nodeRadius * 5))
            {
                continue;
            }
            for (NodeLayout target : layout.children)
            {
                if (target.yCoord == layout.yCoord)
                {
                    // Ko child.
                    g.drawArc(
                        nodeRadius * (2 * Math.min(layout.xCoord, target.xCoord) + 1),
                        nodeRadius * (5 * layout.yCoord),
                        nodeRadius * (2 * Math.abs(layout.xCoord - target.xCoord)),
                        nodeRadius * 2,
                        0,
                        -180
                        );
                }
                else
                {
                    // Proper child.
                    g.drawLine(
                        nodeRadius * (2 * layout.xCoord + 1),
                        nodeRadius * (5 * layout.yCoord + 2) - nodeRadius / 4,
                        nodeRadius * (2 * target.xCoord + 1),
                        nodeRadius * (5 * target.yCoord) + nodeRadius / 4
                        );
                }
            }
        }
        
        // Paint the nodes.
        for (NodeLayout layout : layouts)
        {
            int x = nodeRadius * 2 * layout.xCoord;
            int y = nodeRadius * 5 * layout.yCoord;
            if (!g.hitClip(x, y, nodeRadius * 2, nodeRadius * 2))
            {
                continue;
            }
            g.clearRect(x, y, nodeRadius * 2, nodeRadius * 2);
            g.translate(x, y);
            layout.node.paintNode(g, nodeRadius, layout == selectedLayout);
            g.translate(-x, -y);
        }
        
        // Now paint the transposition links.  We paint these last
        // so that they appear over other surfaces.
        g.setColor(Color.green.darker());
        //g.setStroke(CGEFrame.SINGLE_STROKE);
        for (NodeLayout layout : layouts)
        {
            if (layout.isTransposition)
            {
                NodeLayout transpositionTarget = primaryLayouts.get(layout.node);
                assert transpositionTarget != null;
                if (layout.yCoord == transpositionTarget.yCoord)
                {
                    // Draw an arc instead of a line, for clarity
                    g.drawArc(
                        nodeRadius * (2 * Math.min(layout.xCoord, transpositionTarget.xCoord) + 1),
                        nodeRadius * 5 * layout.yCoord,
                        nodeRadius * 2 * Math.abs(layout.xCoord - transpositionTarget.xCoord),
                        nodeRadius * 2,
                        0,
                        180
                        );
                }
                else
                {
                    g.drawLine(
                        nodeRadius * (2 * layout.xCoord + 1),
                        nodeRadius * (5 * layout.yCoord + 1),
                        nodeRadius * (2 * transpositionTarget.xCoord + 1),
                        nodeRadius * (5 * transpositionTarget.yCoord + 1)
                        );
                }
            }
        }
    }
    
    class NodeLayout
    {
        ExplorerNode node;
        int xCoord, yCoord;
        boolean isTransposition;
        boolean isLeftChild;
        NodeLayout parent;
        List<NodeLayout> children;
        
        NodeLayout()
        {
            children = new ArrayList<NodeLayout>();
        }
        
        LinkedList<ExplorerNode> pathTo()
        {
            LinkedList<ExplorerNode> path = new LinkedList<ExplorerNode>();
            for (NodeLayout layout = this; layout != null; layout = layout.parent)
            {
                path.addFirst(layout.node);
            }
            if (layoutRoot)
            {
                path.removeFirst();
            }
            return path;
        }
    }
}
