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

package org.cgsuite.lang.output;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;
import org.openide.util.Lookup;

/**
 * A panel that displays {@link org.cgsuite.plugin.Output}.
 */
public class OutputBox extends JPanel implements MouseListener, FocusListener
{
    private final static int CHARACTERS_AT_A_TIME = Integer.MAX_VALUE;
    
    private Action copyAction;
    private Action quickSaveAction;
    
    private Output output;
    private int worksheetWidth;
    private int numCharactersDisplayed;
    private Dimension size;
    
    private JButton displayMoreButton;
    private JPopupMenu displayMorePopupMenu;
    private JPopupMenu mainPopupMenu;
    private JMenuItem copyMenuItem;
    private JMenuItem quickSaveMenuItem;
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
            @Override
            public void addLayoutComponent(String name, Component comp)
            {
            }
            
            @Override
            public void layoutContainer(Container parent)
            {
                if (displayMoreButton != null)
                {
                    displayMoreButton.setLocation
                        (size.width - displayMoreButton.getWidth(), size.height - displayMoreButton.getHeight());
                }
            }
            
            @Override
            public Dimension minimumLayoutSize(Container parent)
            {
                return size;
            }
            
            @Override
            public Dimension preferredLayoutSize(Container parent)
            {
                return size;
            }
            
            @Override
            public void removeLayoutComponent(Component comp)
            {
            }
        });
        
        copyAction = new AbstractAction("Copy")
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                copy();
            }
        };
        
        quickSaveAction = new AbstractAction("Save")
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                quickSave();
            }
        };
        
        getActionMap().put(DefaultEditorKit.copyAction, copyAction);
        copyMenuItem = new JMenuItem(copyAction);
        quickSaveMenuItem = new JMenuItem(quickSaveAction);
        
        mainPopupMenu = new JPopupMenu();
        mainPopupMenu.add(copyMenuItem);
        mainPopupMenu.add(quickSaveMenuItem);

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
            displayMoreButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent evt)
                {
                    showDisplayMorePopupMenu();
                }
            });
            add(displayMoreButton);

            JMenuItem showNextMenuItem = new JMenuItem("Show Next " + CHARACTERS_AT_A_TIME + " Characters");
            showNextMenuItem.setMnemonic('N');
            showNextMenuItem.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    numCharactersDisplayed += CHARACTERS_AT_A_TIME;
                    recalc();
                }
            });
            JMenuItem showAllMenuItem = new JMenuItem("Show All Remaining Text");
            showAllMenuItem.setMnemonic('A');
            showAllMenuItem.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    numCharactersDisplayed = -1;
                    recalc();
                }
            });
            
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
    
    public void copy()
    {
        StringSelection text = new StringSelection(output.toString());
        getClipboard().setContents(text, text);
    }
    
    private Clipboard getClipboard()
    {
        Clipboard c = Lookup.getDefault().lookup(Clipboard.class);

        if (c == null)
        {
            c = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        
        return c;
    }
    
    private void quickSave()
    {
        try
        {
            BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            output.paint((Graphics2D) image.getGraphics(), worksheetWidth);
            File file = new File("/Users/asiegel/CGSuite/image.png");
            ImageIO.write(image, "png", file);
        }
        catch (IOException exc)
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

    @Override
    public Dimension getMinimumSize()
    {
        return size;
    }
    
    @Override
    public Dimension getMaximumSize()
    {
        return size;
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        return size;
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (output == null)
            return;
        
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
    
    @Override
    public void mouseClicked(MouseEvent evt)
    {
        requestFocusInWindow();
    }
    
    @Override
    public void mouseEntered(MouseEvent evt)
    {
    }
    
    @Override
    public void mouseExited(MouseEvent evt)
    {
    }
    
    @Override
    public void mousePressed(MouseEvent evt)
    {
        if (evt.isPopupTrigger())
        {
            showMainPopupMenu(evt);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent evt)
    {
        if (evt.isPopupTrigger())
        {
            showMainPopupMenu(evt);
        }
    }
    
    @Override
    public void focusGained(FocusEvent evt)
    {
        if (!evt.isTemporary())
        {
            highlighted = true;
            repaint();
        }
    }
    
    @Override
    public void focusLost(FocusEvent evt)
    {
        if (!evt.isTemporary())
        {
            highlighted = false;
            repaint();
        }
    }
}
