/*
 * Table.java
 *
 * Created on March 7, 2003, 8:21 PM
 * $Id: Table.java,v 1.19 2008/01/11 02:53:05 haoyuep Exp $
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

package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.cgsuite.lang.output.IntensityPlotOutput;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.TableOutput;

/**
 * A table whose entries may be any objects.  This is primarily useful for
 * plug-ins that want to display information in tabular form.  When output is
 * requested for a <code>Table</code>, it is displayed in a nicely arranged
 * grid format.
 *
 * @author  Aaron Siegel
 * @author  Dan Hoey
 * @version $Revision: 1.19 $ $Date: 2008/01/11 02:53:05 $
 */
public class Table extends CgsuiteCollection
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Table");
    
    private List<CgsuiteList> rows;
    private int numColumns;
    private int maxCellWidth;
    private EnumSet<Format> format;
    
    /**
     * A format specifier for a <code>Table</code>.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.19 $ $Date: 2008/01/11 02:53:05 $
     * @since   0.7
     */
    public enum Format
    {
        /** Indicates that the <code>Table</code> will be displayed
            with horizontal grid lines. */
        GRID_LINES_HORIZONTAL,
        /** Indicates that the <code>Table</code> will be displayed
            with vertical grid lines. */
        GRID_LINES_VERTICAL;
    }
    
    /**
     * Constructs a new <code>Table</code> with the specified number of columns.
     *
     * @param   numColumns The number of columns in this table.
     */
    public Table()
    {
        super(TYPE);
        
        rows = new ArrayList<CgsuiteList>();
        format = EnumSet.of(Format.GRID_LINES_HORIZONTAL, Format.GRID_LINES_VERTICAL);
    }
    
    @Override
    public int size()
    {
        return rows.size();
    }
    
    @Override
    public void unlink()
    {
        super.unlink();
        List<CgsuiteList> newRows = new ArrayList<CgsuiteList>(rows.size());
        for (CgsuiteList row : rows)
        {
            newRows.add((CgsuiteList) row.createCrosslink());
        }
        rows = newRows;
        format = format.clone();
    }
    
    @Override
    protected boolean hasMutableReferent()
    {
        return !rows.isEmpty();
    }
    
    @Override
    public void markImmutable()
    {
        if (isMutable())
        {
            super.markImmutable();
            for (CgsuiteObject obj : rows)
            {
                obj.markImmutable();
            }
        }
    }

    @Override
    public List<? extends CgsuiteObject> getUnderlyingCollection()
    {
        return rows;
    }
    
    @Override
    public void add(CgsuiteObject cells)
    {
        if (!(cells instanceof CgsuiteList))
            throw new IllegalArgumentException("Not a list.");
        
        rows.add((CgsuiteList) cells);
        numColumns = Math.max(numColumns, ((CgsuiteList) cells).size());
    }
    
    public CgsuiteList getRow(int row)
    {
        return rows.get(row-1);
    }
    
    public int getNumColumns()
    {
        return numColumns;
    }
    
    public int getNumRows()
    {
        return rows.size();
    }
    
    public EnumSet<Format> getFormat()
    {
        return format;
    }
    
    /**
     * Sets the maximum width of a cell, in pixels.  When the table is
     * displayed, the width of each cell in pixels will be limited to
     * <code>maxCellWidth</code>.
     *
     * @param   maxCellWidth The maximum width of a cell, in pixels.
     * @see     #getMaxCellWidth() getMaxCellWidth
     */
    public void setMaxCellWidth(int maxCellWidth)
    {
        if (maxCellWidth < 1)
        {
            throw new IllegalArgumentException("Max cell width cannot be < 1.");
        }
        this.maxCellWidth = maxCellWidth;
    }
    
    /**
     * Gets the maximum width of a cell, in pixels.
     *
     * @return  The maximum width of a cell, in pixels.
     * @see     #setMaxCellWidth(int) setMaxCellWidth
     */
    public int getMaxCellWidth()
    {
        return maxCellWidth;
    }
    
    public EnumSet<Format> getTableFormat()
    {
        return format;
    }
    
    public void setTableFormat(EnumSet<Format> format)
    {
        this.format = format;
    }
    
    public void setHorizontalLines(boolean show)
    {
        if (show)
            format.add(Format.GRID_LINES_HORIZONTAL);
        else
            format.remove(Format.GRID_LINES_HORIZONTAL);
    }
    
    public boolean getHorizontalLines()
    {
        return format.contains(Format.GRID_LINES_HORIZONTAL);
    }
    
    public IntensityPlotOutput intensityPlot()
    {
        int[][] array = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++)
        {
            int[] row = new int[rows.get(i).size()];
            for (int j = 1; j <= rows.get(i).size(); j++)
            {
                row[j-1] = ((CgsuiteInteger) rows.get(i).get(j)).intValue();
            }
            array[i] = row;
        }
        
        return new IntensityPlotOutput(array);
    }
    
    @Override
    public Output toOutput()
    {
        return new TableOutput(this);
    }
}
