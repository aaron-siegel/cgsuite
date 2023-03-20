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

package org.cgsuite.output;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A panel that displays {@link org.cgsuite.output.Output}.
 */
public class OutputBox extends JPanel implements FocusListener
{
    private final static int CHARACTERS_AT_A_TIME = Integer.MAX_VALUE;
    
    private Output output;
    private int worksheetWidth;
    private int numCharactersDisplayed;
    private Dimension size;

    private boolean highlighted;
    
    private final static Stroke DASH_STROKE = new BasicStroke(
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

        setBackground(Color.white);
        worksheetWidth = 0;
        size = new Dimension(0, 0);
        numCharactersDisplayed = CHARACTERS_AT_A_TIME;
        
        addFocusListener(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
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

    public Output getOutput()
    {
        return output;
    }
    
    public void setOutput(Output newOutput)
    {
        output = newOutput;
        if (worksheetWidth != 0)
        {
            recalc();
        }
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
            ((Graphics2D) g).setStroke(DASH_STROKE);
            g.drawRect(0, 0, size.width-1, size.height-1);
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
