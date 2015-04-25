/*
 * GridEditorPanel.java
 *
 * $Id: GridEditorPanel.java,v 1.19 2007/02/16 20:10:13 asiegel Exp $
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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collections;
import java.util.EnumSet;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteList;
import org.cgsuite.lang.CgsuiteMethod;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuiteString;
import org.cgsuite.lang.game.Grid;
import org.cgsuite.lang.output.GridOutput;

/**
 * A generalized, reusable grid-based <code>EditorPanel</code>.  A
 * <code>GridEditorPanel</code> displays a {@link org.cgsuite.util.Grid} in a
 * rectangular array, mapping the <code>Grid</code>'s integer values to
 * images using a user-specified array of icons.  Entries in the grid can be
 * modified using a variety of user input methods.
 * <p>
 * The easiest way to create a game for use with <code>GridEditorPanel</code>
 * is to subclass {@link AbstractGridGame}, or implement {@link GridEditable}
 * directly.  Classes that implement <code>GridEditable</code> can be used
 * with <code>GridEditorPanel</code> with no further modification.  A variety
 * of methods are provided for customizing the grid's behavior and display.
 * If additional flexibility is needed, <code>GridEditorPanel</code> can be
 * subclassed, or a new {@link EditorPanel} written from scratch.
 * <p>
 * The icons used to display the grid are specified by an array of
 * <code>Icon</code> objects, either in the <code>GridEditorPanel</code>'s
 * constructor or by a call to {@link #setIcons(Icon[]) setIcons}.
 * If the grid has value <code>n</code> at a certain location, then the
 * <code>n<sup>th</sup></code> entry in the array is displayed there.
 * <code>null</code> entries are permissible and correspond to blank spaces in
 * the grid.  A variety of icons are built-in to cgsuite, and can be accessed
 * by calling {@link PluginUtilities#getIcon(String) PluginUtilities.getIcon};
 * see the {@link PluginUtilities} documentation for more details.
 * If no icons are specified, then the default array is:
 * <p>
 * <code>{ null, BLACK_STONE, WHITE_STONE }</code>
 * <p>
 * where <code>BLACK_STONE</code> and <code>WHITE_STONE</code> correspond to
 * the icons obtained via <code>PluginUtilities.getIcon</code>.
 *
 * @author  Samson de Jager
 * @author  Aaron Siegel
 * @version $Revision: 1.19 $ $Date: 2007/02/16 20:10:13 $
 * @see     PluginUtilities
 */
public class GridEditorPanel extends EditorPanel
{
    private final static Cursor DEFAULT_CURSOR, DRAG_CURSOR, INVALID_DRAG_CURSOR;
    
    static
    {
        Cursor dragCursor, invalidDragCursor;
        try
        {
            dragCursor = Cursor.getSystemCustomCursor("MoveDrop.32x32");
            invalidDragCursor = Cursor.getSystemCustomCursor("MoveNoDrop.32x32");
        }
        catch (AWTException exc)
        {
            dragCursor = Cursor.getDefaultCursor();
            invalidDragCursor = Cursor.getDefaultCursor();
        }
        
        DEFAULT_CURSOR = Cursor.getDefaultCursor();
        DRAG_CURSOR = dragCursor;
        INVALID_DRAG_CURSOR = invalidDragCursor;
    }

    private CgsuiteClass type;
    private Icon[] icons;
    private Dimension cellSize;
    private EnumSet<Permission> permissions;
    
    private Insets gridInsets;
    
    private int draggingRow = -1, draggingCol = -1, draggingXDisplacement, draggingYDisplacement, prevDragX, prevDragY;
    
    protected Grid grid;
    
    protected JPopupMenu popupMenu;
    
    /**
     * Constructs a new <code>GridEditorPanel</code> with the specified list
     * of icons, initial grid, and permissions.
     *
     * @param   icons The icons used to display the grid in this editor.
     * @param   initialGrid The initial layout for the grid.
     * @param   permissions The permissions for this
     *          <code>GridEditorPanel</code>.
     */
    public GridEditorPanel(CgsuiteClass type, Grid initialGrid, CgsuiteList iconList)
    {
        super();

        this.type = type;
        this.permissions = EnumSet.allOf(Permission.class);
        
        if (initialGrid.getCgsuiteClass() == Grid.STRIP_TYPE)
            this.permissions.remove(Permission.RESIZE_VERTICAL);
        
        this.icons = new Icon[iconList.size()];
        for (int i = 0; i < iconList.size(); i++)
        {
            String literal = ((CgsuiteString) iconList.get(i+1).resolve("Literal")).toJavaString();
            this.icons[i] = GridOutput.getIcon(literal);
        }
        cellSize = GridOutput.calculateIconDimensions(this.icons, true);
        setGrid(initialGrid);
        setBackground(Color.white);
        gridInsets = new Insets(20, 20, 20, 20);
        
        addListeners();
    }

    @Override
    public Dimension getMinimumSize()
    {
        return new Dimension(4+cellSize.width, 4+cellSize.height);
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(
            gridInsets.left + gridInsets.right + grid.getNumColumns() * (3+cellSize.width) + 1,
            gridInsets.top + gridInsets.bottom + grid.getNumRows() * (3+cellSize.height) + 1
            );
    }
    
    public void setGridInsets(Insets gridInsets)
    {
        this.gridInsets = gridInsets;
        revalidate();
        repaint();
    }
    
    public Insets getGridInsets()
    {
        return gridInsets;
    }
    
    /**
     * Gets the current configuration of the grid in this editor.
     * 
     * @return  the grid configuration currently displayed in this editor
     */
    public Grid getGrid()
    {
        return (Grid) grid.createCrosslink();
    }
    
    /**
     * Programmatically sets the displayed grid.
     *
     * @param   grid The new grid to display in this editor.
     */
    public final void setGrid(Grid grid)
    {
        this.grid = grid.clone(grid.getBitsPerEntry());
        revalidate();
        repaint();
    }
    
    /**
     * Gets the icons used to display the grid in this editor.
     *
     * @return The icons used to display the grid in this editor.
     */
    public Icon[] getIcons()
    {
        return icons.clone();
    }
    
    /**
     * Sets the icons used to display the grid in this editor.
     *
     * @param   icons The new icons used to display the grid in this editor.
     */
    public void setIcons(Icon[] icons)
    {
        this.icons = icons.clone();
        cellSize = GridOutput.calculateIconDimensions(icons, true);
        revalidate();
        repaint();
    }
    
    /**
     * Gets the default permissions for user-modification of this grid.
     */
    public EnumSet<Permission> getPermissions()
    {
        return permissions.clone();
    }
    
    /**
     * Sets the default permissions for user-modification of this grid.
     *
     * @param   permissions the new permissions for this grid
     */
    public void setPermissions(EnumSet<Permission> permissions)
    {
        this.permissions = permissions.clone();
    }
    
    /**
     * Programatically removes the specified row.
     *
     * @param   row The row to remove.
     * @throws  IllegalArgumentException <code>row</code> is out of bounds,
     *          or the grid has only one row.
     */
    public void removeRow(int row)
    {
        if (row < 0 || row >= grid.getNumRows() || grid.getNumRows() == 1)
        {
            throw new IllegalArgumentException("row");
        }
        Grid newGrid = new Grid(grid.getCgsuiteClass(), grid.getNumRows()-1, grid.getNumColumns(), grid.getBitsPerEntry());
        newGrid.paste(grid, 1, 1, row, grid.getNumColumns(), 1, 1);
        newGrid.paste(grid, row+2, 1, grid.getNumRows(), grid.getNumColumns(), row+1, 1);
        setGrid(newGrid);
        firePropertyChange(EDIT_STATE_PROPERTY, Boolean.TRUE, Boolean.FALSE);
        revalidate();
    }
    
    /** 
     * Programatically removes the specified column.
     *
     * @param   col The column to remove.
     * @throws  IllegalArgumentException <code>col</code> is out of bounds,
     *          or the grid has only one column.
     */
    public void removeColumn(int col)
    {
        if (col < 0 || col >= grid.getNumColumns() || grid.getNumColumns() == 1)
        {
            throw new IllegalArgumentException("col");
        }
        Grid newGrid = new Grid(grid.getCgsuiteClass(), grid.getNumRows(), grid.getNumColumns()-1, grid.getBitsPerEntry());
        newGrid.paste(grid, 1, 1, grid.getNumRows(), col, 1, 1);
        newGrid.paste(grid, 1, col+2, grid.getNumRows(), grid.getNumColumns(), 1, col+1);
        setGrid(newGrid);
        firePropertyChange(EDIT_STATE_PROPERTY, Boolean.TRUE, Boolean.FALSE);
        revalidate();
    }
    
    /**
     * Programmatically inserts an empty row at the specified index.  The new
     * row will have the specified index, and any subsequent rows will be
     * shifted down.  If <code>index</code> is equal to the number of rows
     * currently in the grid, then the new row will be added at the bottom of
     * the grid.
     *
     * @param   index The index for the new row.
     * @throws  IllegalArgumentException <code>index</code> is out of bounds.
     */
    public void addRow(int index)
    {
        if (index < 0 || index > grid.getNumRows())
        {
            throw new IllegalArgumentException("index");
        }
        Grid newGrid = new Grid(grid.getCgsuiteClass(), grid.getNumRows()+1, grid.getNumColumns(), grid.getBitsPerEntry());
        newGrid.paste(grid, 1, 1, index, grid.getNumColumns(), 1, 1);
        newGrid.paste(grid, index+1, 1, grid.getNumRows(), grid.getNumColumns(), index+2, 1);
        setGrid(newGrid);
        firePropertyChange(EDIT_STATE_PROPERTY, Boolean.TRUE, Boolean.FALSE);
        revalidate();
    }
    
    /**
     * Programmatically inserts an empty column at the specified index.  The
     * new column will have the specified index, and any subsequent rows will
     * be shifted to the right.  If <code>index</code> is equal to the number
     * of columns currently in the grid, then the new row will be added at the
     * far right of the grid.
     *
     * @param   index The index for the new column.
     * @throws  IllegalArgumentException <code>index</code> is out of bounds.
     */
    public void addColumn(int index)
    {
        if (index < 0 || index > grid.getNumColumns())
        {
            throw new IllegalArgumentException("index");
        }
        Grid newGrid = new Grid(grid.getCgsuiteClass(), grid.getNumRows(), grid.getNumColumns()+1, grid.getBitsPerEntry());
        newGrid.paste(grid, 1, 1, grid.getNumRows(), index, 1, 1);
        newGrid.paste(grid, 1, index+1, grid.getNumRows(), grid.getNumColumns(), 1, index+2);
        setGrid(newGrid);
        firePropertyChange(EDIT_STATE_PROPERTY, Boolean.TRUE, Boolean.FALSE);
        revalidate();
    }
    
    /**
     * Gets the row corresponding to the specified y-coordinate.  If the
     * coordinate is above the entire grid, then this method will return -1;
     * if it is below the entire grid, then this method will return the
     * number of rows in the grid.
     *
     * @param   yCoord The y-coordinate (using this component's coordinate
     *          space).
     * @return  The corresponding row in the grid.
     */
    public int getRow(int yCoord)
    {
        if (yCoord < gridInsets.top)
        {
            return -1;
        }
        int row = (yCoord - gridInsets.top) / (3 + cellSize.height);
        if (row >= grid.getNumRows())
        {
            return grid.getNumRows();
        }
        else
        {
            return row;
        }
    }
    
    /**
     * Gets the column corresponding to the specified x-coordinate.  If the
     * coordinate is to the left of the entire grid, then this method will
     * return -1; if it is to the right of the entire grid, then this method
     * will return the number of columns in the grid.
     *
     * @param   xCoord The x-coordinate (using this component's coordinate
     *          space).
     * @return  The corresponding column in the grid.
     */
    public int getColumn(int xCoord)
    {
        if (xCoord < gridInsets.left)
        {
            return -1;
        }
        int col = (xCoord - gridInsets.left) / (3 + cellSize.width);
        if (col >= grid.getNumColumns())
        {
            return grid.getNumColumns();
        }
        else
        {
            return col;
        }
    }
    
    private boolean isContainedInGrid(MouseEvent evt)
    {
        return isContainedInGrid(getRow(evt.getY()), getColumn(evt.getX()));
    }
    
    private boolean isContainedInGrid(int row, int col)
    {
        return
            row >= 0 &&
            row < grid.getNumRows() &&
            col >= 0 &&
            col < grid.getNumColumns();
    }
    
    protected boolean allowValue(int row, int col, int value)
    {
        return true;
    }
    
    protected boolean allowAddRow(int index)
    {
        return permissions.contains(Permission.RESIZE_VERTICAL);
    }
    
    protected boolean allowRemoveRow(int row)
    {
        return grid.getNumRows() > 1 && permissions.contains(Permission.RESIZE_VERTICAL);
    }
    
    protected boolean allowAddColumn(int index)
    {
        return permissions.contains(Permission.RESIZE_HORIZONTAL);
    }
    
    protected boolean allowRemoveColumn(int col)
    {
        return grid.getNumColumns() > 1 && permissions.contains(Permission.RESIZE_HORIZONTAL);
    }
    
    protected Icon getIcon(int row, int col)
    {
        return icons[grid.getIntAt(row+1, col+1)];
    }
    
    /**
     * Programmatically cycles the value at the given cell one step through the
     * allowed values.
     *
     * @param   row The row of the cell to cycle.
     * @param   col The column of the cell to cycle.
     */
    public void cycleCell(int row, int col)
    {
        int value = grid.getIntAt(row+1, col+1);
        do
        {
            value++;
            if (value == icons.length)
            {
                value = 0;
            }
            if (allowValue(row, col, value))
            {
                setCell(row, col, value);
                break;
            }
        } while (value != grid.getIntAt(row+1, col+1));
    }
    
    /**
     * Programmatically sets the entry at the given cell to the given value.
     *
     * @param   row The row of the cell to cycle.
     * @param   col The column of the cell to cycle.
     * @param   value The new value for the specified cell.
     */
    public void setCell(int row, int col, int value)
    {
        if (value >= 0 && value < icons.length)
        {
            firePropertyChange(EDIT_STATE_PROPERTY, grid.getIntAt(row+1, col+1), value);
            grid.putAt(row+1, col+1, value);
            repaint(row, col);
        }
    }
    
    public void repaint(int row, int col)
    {
        repaint(
            gridInsets.left + col * (3+cellSize.width),
            gridInsets.top + row * (3+cellSize.height),
            3+cellSize.width,
            3+cellSize.height
            );
    }
    
    @Override
    protected void paintComponent(Graphics _g)
    {
        super.paintComponent(_g);
        Graphics2D g = (Graphics2D) _g;
        
        g.translate(gridInsets.left, gridInsets.top);
        
        // Draw the rol/col add/remove buttons.
        
        for (int row = 0; row <= grid.getNumRows(); row++)
        {
            //paintAddButton(g, -15, row * (3+cellSize.height) - 4);
            if (allowAddRow(row))
            {
                paintAddButton(g, grid.getNumColumns() * (3+cellSize.width) + 7, row * (3+cellSize.height) - 4);
            }
        }
        for (int col = 0; col <= grid.getNumColumns(); col++)
        {
            //paintAddButton(g, col * (3+cellSize.width) - 4, -15);
            if (allowAddColumn(col))
            {
                paintAddButton(g, col * (3+cellSize.width) - 4, grid.getNumRows() * (3+cellSize.height) + 7);
            }
        }
        for (int row = 0; row < grid.getNumRows(); row++)
        {
            if (allowRemoveRow(row))
            {
                paintRemoveButton(g, -15, row * (3+cellSize.height) + (3+cellSize.height)/2 - 4);
            }
            //paintRemoveButton(g, grid.getNumColumns() * (3+cellSize.width) + 7, row * (3+cellSize.height) + (3+cellSize.height)/2 - 4);
        }
        for (int col = 0; col < grid.getNumColumns(); col++)
        {
            if (allowRemoveColumn(col))
            {
                paintRemoveButton(g, col * (3+cellSize.width) + (3+cellSize.width)/2 - 4, -15);
            }
            //paintRemoveButton(g, col * (3+cellSize.width) + (3+cellSize.width)/2 - 4, grid.getNumRows() * (3+cellSize.height) + 7);
        }
        
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
        
        for (int row = 0; row < grid.getNumRows(); row++)
        {
            for (int col = 0; col < grid.getNumColumns(); col++)
            {
                if (grid.getIntAt(row+1, col+1) < icons.length &&
                    g.hitClip(
                        col * (3+cellSize.width) + 2,
                        row * (3+cellSize.height) + 2,
                        cellSize.width,
                        cellSize.height
                    ))
                {
                    Icon icon = getIcon(row, col);
                    if (draggingRow == row && draggingCol == col || icon == null)
                    {
                        g.setColor(getBackground());
                        g.fillRect(
                            col * (3+cellSize.width) + 2,
                            row * (3+cellSize.height) + 2,
                            cellSize.width,
                            cellSize.height
                            );
                    }
                    else
                    {
                        icon.paintIcon(
                            this,
                            g,
                            col * (3+cellSize.width) + 2 + (cellSize.width-icon.getIconWidth()) / 2,
                            row * (3+cellSize.height) + 2 + (cellSize.height-icon.getIconHeight()) / 2
                            );
                    }
                }
            }
        }
        
        if (isContainedInGrid(draggingRow, draggingCol) &&
            getIcon(draggingRow, draggingCol) != null)
        {
            getIcon(draggingRow, draggingCol).paintIcon(
                this,
                g,
                prevDragX - draggingXDisplacement,
                prevDragY - draggingYDisplacement
                );
        }
    }
    
    private void paintAddButton(Graphics2D g, int x, int y)
    {
        g.setColor(Color.lightGray);
        g.drawOval(x, y, 8, 8);
        g.setColor(Color.gray);
        g.drawLine(x + 4, y, x + 4, y + 8);
        g.drawLine(x, y + 4, x + 8, y + 4);
    }
    
    private void paintRemoveButton(Graphics2D g, int x, int y)
    {
        g.setColor(Color.lightGray);
        g.drawOval(x, y, 8, 8);
        g.setColor(Color.gray);
        g.drawLine(x + 1, y + 1, x + 7, y + 7);
        g.drawLine(x + 7, y + 1, x + 1, y + 7);
    }
    
    private int hitAddRow(int x, int y)
    {
        x -= gridInsets.left;
        y -= gridInsets.top;
        
        for (int row = 0; row <= grid.getNumRows(); row++)
        {
            if (hitSmallRect(grid.getNumColumns() * (3+cellSize.width) + 7, row * (3+cellSize.height) - 4, x, y))
            {
                return row;
            }
        }
        return -1;
    }
    
    private int hitAddColumn(int x, int y)
    {
        x -= gridInsets.left;
        y -= gridInsets.top;
        
        for (int col = 0; col <= grid.getNumColumns(); col++)
        {
            if (hitSmallRect(col * (3+cellSize.width) - 4, grid.getNumRows() * (3+cellSize.height) + 7, x, y))
            {
                return col;
            }
        }
        return -1;
    }
    
    private int hitRemoveRow(int x, int y)
    {
        x -= gridInsets.left;
        y -= gridInsets.top;
        
        for (int row = 0; row < grid.getNumRows(); row++)
        {
            if (hitSmallRect(-15, row * (3+cellSize.height) + (3+cellSize.height)/2 - 4, x, y))
            {
                return row;
            }
        }
        return -1;
    }
    
    private int hitRemoveColumn(int x, int y)
    {
        x -= gridInsets.left;
        y -= gridInsets.top;
        
        for (int col = 0; col <= grid.getNumColumns(); col++)
        {
            if (hitSmallRect(col * (3+cellSize.width) + (3+cellSize.width)/2 - 4, -15, x, y))
            {
                return col;
            }
        }
        return -1;
    }
    
    private boolean hitSmallRect(int left, int top, int x, int y)
    {
        return x >= left && y >= top && x <= left+8 && y <= top+8;
    }
    
    private void addListeners()
    {
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                if (evt.getButton() == MouseEvent.BUTTON1)
                {
                    handleClick(evt);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent evt)
            {
                if (permissions.contains(Permission.SET_ENTRIES) && evt.isPopupTrigger())
                {
                    showPopupMenu(evt);
                }
                else if (evt.getButton() == MouseEvent.BUTTON1)
                {
                    int row = getRow(evt.getY()), col = getColumn(evt.getX());
                    if (permissions.contains(Permission.DRAG_ENTRIES) && isContainedInGrid(row, col) && allowValue(row, col, 0))
                    {
                        draggingRow = row;
                        draggingCol = col;
                        draggingXDisplacement = evt.getX() - (col * (3+cellSize.width) + 2);
                        draggingYDisplacement = evt.getY() - (row * (3+cellSize.height) + 2);
                        prevDragX = evt.getX();
                        prevDragY = evt.getY();
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent evt)
            {
                if (permissions.contains(Permission.SET_ENTRIES) && evt.isPopupTrigger())
                {
                    showPopupMenu(evt);
                }
                else if (evt.getButton() == MouseEvent.BUTTON1)
                {
                    if (isContainedInGrid(draggingRow, draggingCol))
                    {
                        repaint(
                            gridInsets.left + prevDragX - draggingXDisplacement,
                            gridInsets.top + prevDragY - draggingYDisplacement,
                            cellSize.width,
                            cellSize.height
                            );
                        repaint(draggingRow, draggingCol);
                        int row = getRow(evt.getY()), col = getColumn(evt.getX()), value = grid.getIntAt(draggingRow+1, draggingCol+1);
                        if (isContainedInGrid(row, col) && allowValue(row, col, value) &&
                            (row != draggingRow || col != draggingCol))
                        {
                            firePropertyChange(EDIT_STATE_PROPERTY, grid.getIntAt(draggingRow+1, draggingCol+1), 0);
                            grid.putAt(draggingRow+1, draggingCol+1, 0);
                            firePropertyChange(EDIT_STATE_PROPERTY, grid.getIntAt(row+1, col+1), value);
                            grid.putAt(row+1, col+1, value);
                            repaint(row, col);
                        }
                        setCursor(DEFAULT_CURSOR);
                    }
                    draggingRow = draggingCol = -1;
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent evt)
            {
                if (isContainedInGrid(draggingRow, draggingCol))
                {
                    repaint(
                        gridInsets.left + prevDragX - draggingXDisplacement,
                        gridInsets.top + prevDragY - draggingYDisplacement,
                        cellSize.width,
                        cellSize.height
                        );
                    prevDragX = evt.getX();
                    prevDragY = evt.getY();
                    repaint(
                        gridInsets.left + prevDragX - draggingXDisplacement,
                        gridInsets.top + prevDragY - draggingYDisplacement,
                        cellSize.width,
                        cellSize.height
                        );
                    int row = getRow(evt.getY()), col = getColumn(evt.getX()), value = grid.getIntAt(draggingRow+1, draggingCol+1);
                    if (isContainedInGrid(row, col) && allowValue(row, col, value))
                    {
                        setCursor(DRAG_CURSOR);
                    }
                    else
                    {
                        setCursor(INVALID_DRAG_CURSOR);
                    }
                }
            }
        });
    }
    
    private void showPopupMenu(MouseEvent evt)
    {
        final int row = getRow(evt.getY()), col = getColumn(evt.getX());
        
        if (!isContainedInGrid(row, col))
        {
            return;
        }
        
        JPopupMenu popup = new JPopupMenu();
        boolean allowed = false;
        for (int value = 0; value < icons.length; value++)
        {
            if (allowValue(row, col, value))
            {
                JMenuItem item;
                if (icons[value] == null)
                {
                    item = new JMenuItem("Empty");
                }
                else
                {
                    item = new JMenuItem(icons[value]);
                }
                final int fVal = value;
                item.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        setCell(row, col, fVal);
                    }
                });
                popup.add(item);
                allowed = true;
            }
        }
        
        if (allowed)
        {
            popup.show(this, evt.getX(), evt.getY());
        }
    }
    
    private void handleClick(MouseEvent evt)
    {
        int x = evt.getX(), y = evt.getY();
        
        {
            int row = hitAddRow(x, y);
            if (row != -1 && allowAddRow(row))
            {
                addRow(row);
            }
        }
        {
            int col = hitAddColumn(x, y);
            if (col != -1 && allowAddColumn(col))
            {
                addColumn(col);
            }
        }
        {
            int row = hitRemoveRow(x, y);
            if (row != -1 && allowRemoveRow(row))
            {
                removeRow(row);
            }
        }
        {
            int col = hitRemoveColumn(x, y);
            if (col != -1 && allowRemoveColumn(col))
            {
                removeColumn(col);
            }
        }
        
        if (permissions.contains(Permission.CYCLE_ENTRIES) && isContainedInGrid(evt))
        {
            cycleCell(getRow(y), getColumn(x));
        }
    }

    @Override
    public CgsuiteObject constructObject()
    {
        return type.lookupConstructor().invoke(Collections.<CgsuiteObject>singletonList(this.grid), CgsuiteMethod.EMPTY_PARAM_MAP);
    }
    
    /**
     * A permission flag for a <code>GridEditorPanel</code>.  This is used to
     * indicate which modify operations are allowed for a given panel.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.19 $ $Date: 2007/02/16 20:10:13 $
     * @since   0.7
     */
    public enum Permission
    {
        /** Indicates that the user may resize a <code>GridEditorPanel</code>
         *  horizontally. */
        RESIZE_HORIZONTAL,
        /** Indicates that the user may resize a <code>GridEditorPanel</code>
         *  vertically. */
        RESIZE_VERTICAL,
        /** Indicates that the user may cycle among the available grid entries by
         *  left-clicking the grid. */
        CYCLE_ENTRIES,
        /** Indicates that the user may set the values of the grid entries by
         *  selecting from a popup menu. */
        SET_ENTRIES,
        /** Indicates that the user may drag the grid entries to new locations. */
        DRAG_ENTRIES;
    }
}
