/*
 * OutputBox.java
 *
 * Created on December 11, 2002, 10:48 PM
 * $Id: OutputBox.java,v 1.26 2007/04/09 23:51:51 asiegel Exp $
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

package org.cgsuite.ui.worksheet;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * A panel that displays {@link org.cgsuite.plugin.Output}.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.26 $ $Date: 2007/04/09 23:51:51 $
 */
public class OutputBox extends JPanel implements MouseListener, FocusListener
{
    private final static int CHARACTERS_AT_A_TIME = 200;
    
    private Output output;
    private int worksheetWidth;
    private int numCharactersDisplayed;
    private Dimension size;
    
    private JButton displayMoreButton;
    private JPopupMenu displayMorePopupMenu;
    private JPopupMenu mainPopupMenu;
    private JMenuItem copyMenuItem;
    private boolean highlighted;
    
    private static Stroke dashStroke = new BasicStroke(
        1.0f,
        BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER,
        10.0f,
        new float[] { 1.0f, 2.0f },
        0.0f
        );
    
    public OutputBox()
    {
        setLayout(new LayoutManager()
        {
            public void addLayoutComponent(String name, Component comp) {}
            public void layoutContainer(Container parent)
            {
                if (displayMoreButton != null)
                {
                    displayMoreButton.setLocation
                        (size.width - displayMoreButton.getWidth(), size.height - displayMoreButton.getHeight());
                }
            }
            public Dimension minimumLayoutSize(Container parent)
            {
                return size;
            }
            public Dimension preferredLayoutSize(Container parent)
            {
                return size;
            }
            public void removeLayoutComponent(Component comp) {}
        });
        /*
        copyAsMenu = new JMenu("Copy As");
        JMenuItem copyAsImageMenuItem = new JMenuItem("Image");
        copyAsImageMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JOptionPane.showMessageDialog(
                    MainFrame.getMainFrame(),
                    "Copying as an image is not supported in this version.",
                    "Unsupported Operation",
                    JOptionPane.ERROR_MESSAGE
                    );
        }});
        copyAsMenu.add(copyAsImageMenuItem);
        */
        JMenuItem qsave = new JMenuItem("QuickSave");
        qsave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quickSave();
        }});
        //copyAsMenu.add(copyAsTextMenuItem);
        /*
        mainPopupMenu = new JPopupMenu();
        mainPopupMenu.add(copyMenuItem);
        mainPopupMenu.add(qsave);
         * 
         */
        setBackground(Color.white);
        worksheetWidth = 0;
        size = new Dimension(0, 0);
        numCharactersDisplayed = CHARACTERS_AT_A_TIME;
        
        addMouseListener(this);
        addFocusListener(this);
    }
    
    public OutputBox(Output initialOutput)
    {
        this();
        setOutput(initialOutput);
    }
    
    public void setWorksheetWidth(int newWorksheetWidth)
    {
        if (worksheetWidth == newWorksheetWidth)
        {
            return;
        }
        worksheetWidth = newWorksheetWidth;
        recalc();
    }
    
    public void setOutput(Output newOutput)
    {
        output = newOutput;
        if (output instanceof StyledTextOutput && displayMoreButton == null)
        {
            displayMoreButton = new JButton("(More...)");
            displayMoreButton.setVisible(false);
            displayMoreButton.setSize(displayMoreButton.getPreferredSize());
            displayMoreButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    showDisplayMorePopupMenu();
            }});
            add(displayMoreButton);

            JMenuItem showNextMenuItem = new JMenuItem("Show Next " + CHARACTERS_AT_A_TIME + " Characters");
            showNextMenuItem.setMnemonic('N');
            showNextMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    numCharactersDisplayed += CHARACTERS_AT_A_TIME;
                    recalc();
            }});
            JMenuItem showAllMenuItem = new JMenuItem("Show All Remaining Text");
            showAllMenuItem.setMnemonic('A');
            showAllMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    numCharactersDisplayed = -1;
                    recalc();
            }});
            
            displayMorePopupMenu = new JPopupMenu();
            displayMorePopupMenu.add(showNextMenuItem);
            displayMorePopupMenu.add(showAllMenuItem);
        }
        if (worksheetWidth != 0)
        {
            recalc();
        }
        //copyMenuItem.setEnabled(output != null);
    }
    
    private void recalc()
    {
        if (output == null || worksheetWidth == 0)
        {
            size = new Dimension(0, 0);
        }
        else if (output instanceof StyledTextOutput)
        {
            StyledTextOutput sto = (StyledTextOutput) output;
            size = sto.getSize(worksheetWidth, numCharactersDisplayed);
            displayMoreButton.setVisible(numCharactersDisplayed >= 0 && numCharactersDisplayed < sto.characterCount());
        }
        else
        {
            size = output.getSize(worksheetWidth);
        }
        revalidate();
        repaint();
    }
    
    public void displayAll()
    {
        numCharactersDisplayed = -1;
        recalc();
    }
    
    private void quickSave()
    {
        try
        {
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage
                (size.width, size.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            output.paint((Graphics2D) image.getGraphics(), worksheetWidth);
            javax.imageio.ImageIO.write(image, "png", new java.io.File("image.png"));
        }
        catch (java.io.IOException exc)
        {
        }
    }
    
    private void showDisplayMorePopupMenu()
    {
        displayMorePopupMenu.show(this, displayMoreButton.getLocation().x, displayMoreButton.getLocation().y);
    }
    
    private void showMainPopupMenu(MouseEvent evt)
    {
        mainPopupMenu.show(this, evt.getX(), evt.getY());
    }

    public Dimension getMinimumSize()
    {
        return size;
    }
    
    public Dimension getMaximumSize()
    {
        return size;
    }
    
    public Dimension getPreferredSize()
    {
        return size;
    }
    
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (output instanceof StyledTextOutput)
        {
            ((StyledTextOutput) output).paint((Graphics2D) g, worksheetWidth, numCharactersDisplayed);
        }
        else
        {
            output.paint((Graphics2D) g, worksheetWidth);
        }
        if (highlighted)
        {
            g.setColor(Color.white);
            g.drawRect(0, 0, size.width-1, size.height-1);
            g.setColor(Color.black);
            ((Graphics2D) g).setStroke(dashStroke);
            g.drawRect(0, 0, size.width-1, size.height-1);
        }
    }
    
    public void mouseClicked(MouseEvent evt)
    {
        requestFocusInWindow();
    }
    
    public void mouseEntered(MouseEvent evt)
    {
    }
    
    public void mouseExited(MouseEvent evt)
    {
    }
    
    public void mousePressed(MouseEvent evt)
    {
        if (evt.isPopupTrigger())
        {
            showMainPopupMenu(evt);
        }
    }
    
    public void mouseReleased(MouseEvent evt)
    {
        if (evt.isPopupTrigger())
        {
            showMainPopupMenu(evt);
        }
    }
    
    public void focusGained(FocusEvent evt)
    {
        if (!evt.isTemporary())
        {
            highlighted = true;
            repaint();
        }
    }
    
    public void focusLost(FocusEvent evt)
    {
        if (!evt.isTemporary())
        {
            highlighted = false;
            repaint();
        }
    }
}
