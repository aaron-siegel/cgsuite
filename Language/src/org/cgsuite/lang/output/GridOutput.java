/*
 * GridOutput.java
 *
 * Created on January 29, 2004, 3:47 PM
 * $Id: GridOutput.java,v 1.5 2006/04/07 06:02:47 asiegel Exp $
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.cgsuite.Grid;

/**
 * A grid to display as output.
 *
 * @see     Output
 * @author  Aaron Siegel
 * @version $Revision: 1.5 $ $Date: 2006/04/07 06:02:47 $
 */
public class GridOutput implements Output
{
    public final static String BLACK_BISHOP = "blackbishop.png";
    public final static String BLACK_KING = "blackking.png";
    public final static String BLACK_KNIGHT = "blackknight.png";
    public final static String BLACK_PAWN = "blackpawn.png";
    public final static String BLACK_QUEEN = "blackqueen.png";
    public final static String BLACK_ROOK = "blackrook.png";
    public final static String BLACK_STONE = "blackstone.gif";
    public final static String FROG = "frog.png";
    public final static String GRAY_STONE = "graystone.png";
    public final static String TOAD = "toad.png";
    public final static String WHITE_BISHOP = "whitebishop.png";
    public final static String WHITE_KING = "whiteking.png";
    public final static String WHITE_KNIGHT = "whiteknight.png";
    public final static String WHITE_PAWN = "whitepawn.png";
    public final static String WHITE_QUEEN = "whitequeen.png";
    public final static String WHITE_ROOK = "whiterook.png";
    public final static String WHITE_STONE = "whitestone.gif";

    private final static Icon[] DEFAULT_ICONS =
    {
        null,
        getIcon(BLACK_STONE),
        getIcon(WHITE_STONE)
    };

    protected Grid grid;
    protected Icon[] icons;
    protected Dimension size, cellSize;
    protected String alt;

    public GridOutput(Grid grid)
    {
        this(grid, DEFAULT_ICONS, grid.toString());
    }

    public GridOutput(Grid grid, Icon[] icons)
    {
        this(grid, icons, grid.toString());
    }

    public GridOutput(Grid grid, Icon[] icons, String alt)
    {
        this.grid = grid.copy();
        if (icons != null)
        {
            this.icons = (Icon[]) icons.clone();
            cellSize = calculateIconDimensions(icons, true);
            size = calculateGridImageDimensions(grid, icons, cellSize, 1, 1);
        }
        this.alt = alt;
    }

    public void write(java.io.PrintWriter out, Output.Mode mode)
    {
        if (mode == Output.Mode.PLAIN_TEXT)
        {
            out.print(alt);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Dimension getSize(int preferredWidth)
    {
        return size;
    }

    protected Icon getIcon(int row, int col)
    {
        return icons[grid.getAt(row, col)];
    }

    @Override
    public void paint(Graphics2D g, int preferredWidth)
    {
        Dimension totalSize = calculateGridImageDimensions(grid, icons, cellSize, 1, 1);

        g.setBackground(Color.white);
        g.clearRect(0, 0, totalSize.width, totalSize.height);

        // Draw the grid
        g.setColor(Color.black);
        for (int row = 0; row <= grid.getNumRows(); row++)
        {
            g.drawLine(
                0,
                row * (3+cellSize.height),
                grid.getNumColumns() * (3+cellSize.width),
                row * (3+cellSize.height)
                );
        }
        for (int col = 0; col <= grid.getNumColumns(); col++)
        {
            g.drawLine(
                col * (3+cellSize.width),
                0,
                col * (3+cellSize.width),
                grid.getNumRows() * (3+cellSize.height)
                );
        }

        // Add the icons
        for (int row = 0; row < grid.getNumRows(); row++)
        {
            for (int col = 0; col < grid.getNumColumns(); col++)
            {
                Icon icon = getIcon(row, col);
                if (icon != null)
                {
                    icon.paintIcon(
                        null,
                        g,
                        col * (3+cellSize.width) + 2 + (cellSize.width-icon.getIconWidth()) / 2,
                        row * (3+cellSize.height) + 2 + (cellSize.height-icon.getIconHeight()) / 2
                        );
                }
            }
        }
    }
    
    public static Dimension calculateIconDimensions(Icon[] icons, boolean forceSquares)
    {
        int maximumWidth = 0, maximumHeight = 0;
        for (int i = 0; i < icons.length; i++)
        {
            if (icons[i] != null)
            {
                maximumHeight = Math.max(maximumHeight, icons[i].getIconHeight());
                maximumWidth = Math.max(maximumWidth, icons[i].getIconWidth());
            }
        }
        if (forceSquares)
        {
            maximumWidth = maximumHeight = Math.max(maximumWidth, maximumHeight);
        }
        return new Dimension(maximumWidth, maximumHeight);
    }
    
    private static Dimension calculateGridImageDimensions(
        Grid grid,
        Icon[] icons,
        Dimension cellSize,
        int gap,
        int lineThickness
        )
    {
        return new Dimension(
            (grid.getNumColumns() * (cellSize.width + 2 * gap + lineThickness)) + lineThickness,
            (grid.getNumRows() * (cellSize.height + 2 * gap + lineThickness)) + lineThickness
            );
    }

    private static final Map<String,Icon> iconMap = new HashMap<String,Icon>();

    public static Icon getIcon(String iconName)
    {
        if (!iconMap.containsKey(iconName))
        {
            iconMap.put(iconName, new ImageIcon(GridOutput.class.getResource(iconName)));
        }
        return iconMap.get(iconName);
    }
}