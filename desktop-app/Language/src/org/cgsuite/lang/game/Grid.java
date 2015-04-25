/*
 * Grid.java
 *
 * Created on June 20, 2003, 12:23 PM
 * $Id: Grid.java,v 1.10 2007/02/16 20:10:14 asiegel Exp $
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

package org.cgsuite.lang.game;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteCollection;
import org.cgsuite.lang.CgsuiteEnumValue;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteList;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.InputException;

/**
 * A two-dimensional array suitable for representing grid-based games.  The
 * array is stored in a compact, memory-efficient structure.  In addition,
 * several utilities are provided, including methods to decompose a grid
 * into its connected components and to compare grids while ignoring
 * symmetries.
 * <p>
 * In addition to the size of the grid, it's possible to specify the number
 * of bits to use per grid entry.  The smaller the number of bits per entry,
 * the more efficient memory usage will be.  However, fewer bits per entry
 * also restricts the range of values that each entry can have.  See the
 * documentation for the <code>BITS_PER_ENTRY_*</code> constants for details.
 * Note that for speed reasons, no internal checks are performed to insure
 * that each entry falls within the specified range.  If an entry is set to
 * a value outside the specified range, the resulting behavior is undefined.
 * <p>
 * Setting the bits per entry below 8 causes a marginal performance deficit
 * when accessing the grid.  However, the smaller memory footprint means that
 * the grid will be cloned substantially faster.  In most applications (such
 * as canonicalizing games) this leads to an overall gain in speed as well as
 * memory efficiency.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.10 $ $Date: 2007/02/16 20:10:14 $
 */
public class Grid extends CgsuiteObject implements Serializable
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Grid");
    public final static CgsuiteClass STRIP_TYPE = CgsuitePackage.forceLookupClass("Strip");

    private static int[] markers;
    private static int regionAt;
    private static RegionInfo[] regions;
    
    private int numRows;
    private int numColumns;
    private byte[] entries;
    private BitsPerEntry bitsPerEntry;
    
    private Grid(CgsuiteClass type)
    {
        super(type);
    }
    
    public Grid(int numColumns)
    {
        this(STRIP_TYPE);
        
        constructGrid(1, numColumns, BitsPerEntry.EIGHT);
    }

    /**
     * Constructs a new <code>Grid</code> with the specified dimensions and
     * {@link BitsPerEntry#EIGHT EIGHT} bits per entry.
     *
     * @param   numRows The number of rows in the grid.
     * @param   numColumns The number of columns in the grid.
     */
    public Grid(int numRows, int numColumns)
    {
        this(TYPE);

        constructGrid(numRows, numColumns, BitsPerEntry.EIGHT);
    }
    
    /**
     * Constructs a new <code>Grid</code> with the specified dimensions and
     * bits per entry.
     *
     * @param   numRows The number of rows in the grid.
     * @param   numColumns The number of columns in the grid.
     * @param   bitsPerEntry The number of bits of storage per grid entry.
     * @throws  IllegalArgumentException numRows or numColumns is negative.
     */
    public Grid(int numRows, int numColumns, BitsPerEntry bitsPerEntry)
    {
        this(TYPE);
        
        constructGrid(numRows, numColumns, bitsPerEntry);
    }
    
    public Grid(CgsuiteClass type, int numRows, int numColumns, BitsPerEntry bitsPerEntry)
    {
        this(type);
        
        constructGrid(numRows, numColumns, bitsPerEntry);
    }
    
    private void constructGrid(int numRows, int numColumns, BitsPerEntry bitsPerEntry)
    {
        if (type == STRIP_TYPE && numRows != 1)
        {
            throw new InputException("The position must contain just one row.");
        }
        
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.bitsPerEntry = bitsPerEntry;
        if (numRows == 0 || numColumns == 0)
        {
            this.entries = new byte[0];
        }
        else if (numRows < 0 || numColumns < 0)
        {
            throw new InputException("RowCount and ColumnCount must both be non-negative.");
        }
        else
        {
            this.entries = new byte[(numRows * numColumns + bitsPerEntry.perByte - 1) / bitsPerEntry.perByte];
        }
    }
    
    public static Grid parseStrip(String str, String charMap)
    {
        Grid strip = parse(STRIP_TYPE, str, charMap);
        
        if (strip.getNumRows() == 1)
            return strip;
        else
            throw new InputException("The position must contain just one row.");
    }
    
    public static Grid parseGrid(String str, String charMap)
    {
        return parse(TYPE, str, charMap);
    }

    public static Grid parse(CgsuiteClass type, String str, String charMap)
    {
        String[] strings = str.split("\\|");
        int numColumns = (strings.length == 0 ? 0 : strings[0].length());
        Grid grid = new Grid(type, strings.length, numColumns, BitsPerEntry.EIGHT);     // TODO Manage bits per entry
        for (int i = 0; i < strings.length; i++)
        {
            if (strings[i].length() != numColumns)
            {
                throw new InputException("All rows of the position must have equal length.");
            }
            for (int j = 0; j < numColumns; j++)
            {
                int value = charMap.indexOf(Character.toLowerCase(strings[i].charAt(j)));
                if (value == -1)
                {
                    throw new InputException
                        ("The position may only contain the following characters: " + charMap);
                }
                grid.putAt(i+1, j+1, value);
            }
        }
        return grid;
    }
    
    /**
     * Tests whether two grids are equal.  Two grids are considered equal
     * if they have the same dimensions and bits per entry, and all their
     * entries agree.
     *
     * @param   o The other grid to compare this to.
     * @return  <code>true</code> if this grid is equal to <code>o</code>.
     * @see     #equalsSymmetryInvariant(Grid) equalsSymmetryInvariant
     */
    @Override
    public boolean equals(Object o)
    {
        return o instanceof Grid && compareLike((Grid) o) == 0;
    }

    /**
     * Computes a hash code for this <code>Grid</code>.
     *
     * @return  A hash code for this grid.
     * @see     #hashCodeSymmetryInvariant() hashCodeSymmetryInvariant
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(entries);
    }
    
    /**
     * Converts this <code>Grid</code> to a <code>String</code> using the
     * specified character map.  This will map grid entries to characters
     * using the specified <code>charMap</code>: If the value at position
     * (row,col) in the grid is <code>i</code>, then the resulting
     * character will be <code>charMap.charAt(i)</code>.
     * <p>
     * Each row of the grid will be enclosed in quotes, with the rows
     * separated by commas.  For example, the grid:
     * <p>
     * <code>[[1,2,0,0],[0,1,0,1],[2,0,1,2]]</code>
     * <p>
     * under the character map:
     * <p>
     * <code>"xyz"</code>
     * <p>
     * would be converted as:
     * <p>
     * <code>"yzxx","xyxy","zxyz"</code>
     *
     * @param   charMap The mapping to use for converting values to
     *          characters.
     * @return  A string representation of this grid.
     */
    public String toString(String charMap)
    {
        if (numRows == 0)
        {
            return "\"\"";
        }
        
        if (charMap == null)
        {
            charMap = ".123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        // For each row, we need a character for each column, two quote marks,
        // and a comma (no comma needed for the first row).
        StringBuilder buf = new StringBuilder(numRows * (numColumns + 3) - 1);
        buf.append('"');
        for (int row = 1; row <= numRows; row++)
        {
            for (int col = 1; col <= numColumns; col++)
            {
                int value = getIntAt(row, col);
                if (value >= 0 && value < charMap.length())
                {
                    buf.append(charMap.charAt(value));
                }
                else
                {
                    buf.append('?');
                }
            }
            if (row <= numRows - 1)
            {
                buf.append('|');
            }
        }
        buf.append('"');
        return buf.toString();
    }
    
    @Override
    public void unlink()
    {
        super.unlink();
        entries = Arrays.copyOf(entries, entries.length);
    }
    
    /**
     * Clones this grid, changing the number of bits per entry as specified.
     * The clone will be indentical in value to this grid, but with the bits
     * per entry adjusted.
     *
     * @param   newBitsPerEntry The number of bits per entry to use in the
     *          new grid.
     * @return  An adjusted clone of this grid.
     */
    public Grid clone(BitsPerEntry newBitsPerEntry)
    {
        Grid clone = new Grid(type, numRows, numColumns, newBitsPerEntry);
        for (int i = 1; i <= numRows; i++)
        {
            for (int j = 1; j <= numColumns; j++)
            {
                clone.putAt(i, j, getIntAt(i, j));
            }
        }
        return clone;
    }

    @Override
    protected int compareLike(CgsuiteObject obj)
    {
        Grid other = (Grid) obj;
        
        if (bitsPerEntry != other.bitsPerEntry)
        {
            return bitsPerEntry.ordinal() - other.bitsPerEntry.ordinal();
        }
        if (numRows != other.numRows)
        {
            return numRows - other.numRows;
        }
        if (numColumns != other.numColumns)
        {
            return numColumns - other.numColumns;
        }
        return BYTE_ARRAY_COMPARATOR.compare(entries, other.entries);
    }
    
    /**
     * Gets the number of rows in this grid.
     *
     * @return  The number of rows in this grid.
     */
    public int getNumRows()
    {
        return numRows;
    }
    
    /**
     * Gets the number of columns in this grid.
     *
     * @return  The number of columns in this grid.
     */
    public int getNumColumns()
    {
        return numColumns;
    }
    
    public CgsuiteObject getAt(int row, int column)
    {
        int intValue = getIntAt(row, column);
        if (intValue == Integer.MIN_VALUE)
            return NIL;
        else
            return new CgsuiteInteger(intValue);
    }
    
    /**
     * Gets the value at the specified coordinate.
     *
     * @param   row The row of the coordinate.
     * @param   column The column of the coordinate.
     * @return  The value at (row, column).
     */
    public int getIntAt(int row, int column)
    {
        if (row < 1 || row > numRows || column < 1 || column > numColumns)
            return Integer.MIN_VALUE;
        
        int index = (row-1) * numColumns + (column-1);
        return (entries[index / bitsPerEntry.perByte] >>> ((index % bitsPerEntry.perByte) * bitsPerEntry.bits));
    }
    
    /**
     * Puts the specified value at the specified coordinate.
     *
     * @param   row The row of the coordinate.
     * @param   column The column of the coordinate.
     * @param   value The value to place at (row, column).
     */
    public int putAt(int row, int column, int value)
    {
        return putAt(entries, row, column, value);
    }
    
    private int putAt(byte[] array, int row, int column, int value)
    {
        // TODO Arg checking
        int index = (row-1) * numColumns + (column-1);
        int arrayIndex = index / bitsPerEntry.perByte;
        int shift = (index % bitsPerEntry.perByte) * bitsPerEntry.bits;
        array[arrayIndex] &= ~(bitsPerEntry.mask << shift);
        array[arrayIndex] |= (value << shift);
        return value;
    }
    
    public BitsPerEntry getBitsPerEntry()
    {
        return bitsPerEntry;
    }
    
    /**
     * Resets all of this grid's entries to 0.  This will generally be much
     * faster than iteratively calling {@link #putAt(int, int, int) putAt}.
     */
    public void clear()
    {
        Arrays.fill(entries, (byte) 0);
    }
    
    /**
     * Determines whether the coordinate that lies <code>distance</code>
     * units away from <code>(row, column)</code> in the specified
     * <code>direction</code> is valid for this grid.
     * <p>
     * This can be used to iterate over directional moves in games, e.g.:
     * <p>
     * <pre>
     *for (Grid.Direction dir : Grid.Direction.ORTHOGONALS)
     *    if (isValidShift(row, column, dir, 1))
     *        myMethod(row + dir.rowShift(1), column + dir.columnShift(1));
     *</pre>
     *
     * @param   row The row of the coordinate to examine.
     * @param   column The column of the coordinate to examine.
     * @param   direction The {@link Direction} of the shift.
     * @param   distance The distance from <code>(row, column)</code> to check.
     * @return  <code>true</code> if this is a valid shift.
     * @see     Direction#rowShift(int) Grid.Direction.rowShift
     * @see     Direction#columnShift(int) Grid.Direction.columnShift
     */
    public boolean isValidShift(int row, int column, Direction direction, int distance)
    {
        int newRow = row + direction.rowShift(distance),
            newCol = column + direction.columnShift(distance);
        return newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numColumns;
    }
    
    /**
     * Constructs the grid obtained by "slicing" this grid along the specified
     * coordinates.  The new grid will correspond to the rectangular portion of
     * this grid whose upper-left corner is <code>(startRow,startCol)</code>
     * (inclusive), and whose lower-right corner is
     * <code>(endRow,endCol)</code> (exclusive).  Thus the new grid will have
     * <code>endRow-startRow</code> rows and <code>endCol-startCol</code>
     * columns, and its value at <code>(row,col)</code> will be equal to this
     * grid's value at <code>(row+startRow,col+startCol)</code>.
     *
     * @param   startRow The row of this grid that corresponds to the first row
     *          of the subgrid (inclusive).
     * @param   endRow The row of this grid that corresponds to the last row
     *          of the subgrid (exclusive).
     * @param   startCol The column of this grid that corresponds to the first
     *          column of the subgrid (inclusive).
     * @param   endCol The column of this grid that corresponds to the last
     *          column of the subgrid (exclusive).
     * @return  The subgrid specified by <code>startRow</code>,
     *          <code>endRow</code>, <code>startCol</code> and
     *          <code>endCol</code>.
     * @since   0.6
     */
    public Grid subgrid(int startRow, int endRow, int startCol, int endCol)
    {
        Grid subgrid = new Grid(type, endRow-startRow+1, endCol-startCol+1, bitsPerEntry);
        for (int row = startRow; row <= endRow; row++)
        {
            for (int col = startCol; col <= endCol; col++)
            {
                subgrid.putAt(row-startRow+1, col-startCol+1, getIntAt(row, col));
            }
        }
        return subgrid;
    }
    
    /**
     * Copies all values from the specified grid into this grid.  If the
     * specified grid has value <code>x</code> at location
     * <code>(row,col)</code>, then after calling <code>paste</code>, this
     * grid will have value <code>x</code> at location
     * <code>(row+pasteRow,col+pasteCol)</code>.
     * 
     * @param   grid The grid to paste into this one.
     * @param   pasteRow The first row of this grid that will be modified.
     * @param   pasteCol The first column of this grid that will be modified.
     * @since   0.6
     */
    public void paste(Grid grid, int pasteRow, int pasteCol)
    {
        int rowsToPaste = Math.min(grid.getNumRows(), this.numRows - pasteRow + 1);
        int colsToPaste = Math.min(grid.getNumColumns(), this.numColumns - pasteCol + 1);
        for (int row = 1; row <= rowsToPaste; row++)
        {
            for (int col = 1; col <= colsToPaste; col++)
            {
                putAt(row+pasteRow-1, col+pasteCol-1, grid.getIntAt(row, col));
            }
        }
    }
    
    public void fill(int startRow, int endRow, int startCol, int endCol, int value)
    {
        for (int row = startRow; row <= endRow; row++)
        {
            for (int col = startCol; col <= endCol; col++)
            {
                putAt(row, col, value);
            }
        }
    }
    
    /**
     * Copies the specified values from the specified grid into this grid.
     * If the specified grid has value <code>x</code> at location
     * <code>(row,col)</code>, then after calling <code>paste</code>, this
     * grid will have value <code>x</code> at location
     * <code>(row-startRow+pasteRow,col-startCol+pasteCol)</code>.
     * 
     * @param   grid The grid to paste into this one.
     * @param   startRow the first row of the specified grid that will be
     *          copied (inclusive)
     * @param   startCol the first column of the specified grid that will be
     *          copied (inclusive)
     * @param   endRow the last row of the specified grid that will be
     *          copied (exclusive)
     * @param   endCol  the last row of the specified grid that will be
     *          copied (exclusive)
     * @param   pasteRow the first row of this grid that will be modified
     * @param   pasteCol the first column of this grid that will be modified
     * @since   1.0
     */
    public void paste(Grid grid, int startRow, int startCol, int endRow, int endCol, int pasteRow, int pasteCol)
    {
        for (int row = startRow; row <= endRow; row++)
        {
            for (int col = startCol; col <= endCol; col++)
            {
                putAt(row-startRow+pasteRow, col-startCol+pasteCol, grid.getIntAt(row, col));
            }
        }
    }
    
    private static Map<CgsuiteObject,Symmetry> SYM_CACHE = new HashMap<CgsuiteObject,Symmetry>();
    
    public Grid permute(CgsuiteEnumValue symmetry)
    {
        return permute(convertSym(symmetry));
    }
    
    private static Symmetry convertSym(CgsuiteEnumValue symmetry)
    {
        Symmetry javasym = SYM_CACHE.get(symmetry);

        if (javasym != null)
            return javasym;
        
        if (symmetry.getCgsuiteClass() != Symmetry.TYPE)
            throw new InputException("Not a symmetry.");
        
        Symmetry[] values = Symmetry.values();
        javasym = values[symmetry.getOrdinal()-1];
        
        SYM_CACHE.put(symmetry, javasym);
        return javasym;
    }
    
    public Grid permute(Symmetry symmetry)
    {
        Grid grid = new Grid(
            type,
            symmetry.isRotational() ? numColumns : numRows,
            symmetry.isRotational() ? numRows : numColumns,
            bitsPerEntry
            );

        for (int row = 1; row <= numRows; row++)
        {
            for (int col = 1; col <= numColumns; col++)
            {
                int value = getIntAt(row, col);
                int newRow = symmetry.isVertical()? numRows-row+1 : row;
                int newCol = symmetry.isHorizontal()? numColumns-col+1 : col;
                grid.putAt(
                    symmetry.isRotational()? newCol : newRow,
                    symmetry.isRotational()? newRow : newCol,
                    value
                    );
            }
        }
        
        return grid;
    }
    
    private static Map<CgsuiteCollection,EnumSet<Symmetry>> SYM_COLLECTION_CACHE = new HashMap<CgsuiteCollection,EnumSet<Symmetry>>();
    
    public Grid symmetryInvariant(CgsuiteCollection symmetries)
    {
        return symmetryInvariant(convertSymCollection(symmetries));
    }
    
    private static EnumSet<Symmetry> convertSymCollection(CgsuiteCollection symmetries)
    {
        EnumSet<Symmetry> javasym = SYM_COLLECTION_CACHE.get(symmetries);
        
        if (javasym != null)
            return javasym;
        
        Symmetry[] values = Symmetry.values();
        javasym = EnumSet.noneOf(Symmetry.class);
        for (CgsuiteObject obj : symmetries)
        {
            if (obj.getCgsuiteClass() != Symmetry.TYPE)
                throw new InputException("Not a collection of symmetry types.");
            
            int ordinal = ((CgsuiteEnumValue) obj).getOrdinal();
            javasym.add(values[ordinal-1]);
        }
        
        SYM_COLLECTION_CACHE.put(symmetries, javasym);
        return javasym;
    }
    
    public Grid symmetryInvariant(EnumSet<Symmetry> symmetries)
    {
        Grid result = this;
        for (Symmetry sym : symmetries)
        {
            if (sym != Symmetry.IDENTITY)
            {
                Grid next = permute(sym);
                if (next.compareLike(result) < 0)
                    result = next;
            }
        }
        return result;
    }
    
    /**
     * Constructs a new grid by mapping this grid's entries as specified.
     * The new grid will have the same size as this one.  If this grid has
     * value <code>x</code> at position (row, column), then the new grid
     * will have value <code>entryMap[x]</code> at position (row, column).
     * 
     * @param   entryMap An array specifying the mapping to apply.
     * @return  A mapped copy of this grid.
     * @throws  ArrayIndexOutOfBoundsException The value of one of this
     *          grid's entries is greater than
     *          <code>entryMap.length - 1</code>.
     * @since   0.6
     */
    public Grid mapEntries(int[] entryMap)
    {
        Grid newGrid = new Grid(type, numRows, numColumns, bitsPerEntry);
        for (int row = 0; row < numRows; row++)
        {
            for (int col = 0; col < numColumns; col++)
            {
                newGrid.putAt(row, col, entryMap[getIntAt(row, col)]);
            }
        }
        return newGrid;
    }
    
    private void initMarkers()
    {
        if (markers == null || markers.length < numRows * numColumns)
        {
            markers = null;
            markers = new int[numRows * numColumns * 2];
        }
        Arrays.fill(markers, 0, numRows * numColumns, -1);
    }
    
    public int libertyCount(int row, int col, int libertyValue)
    {
        int chainValue = getIntAt(row, col);
        if (chainValue == libertyValue || chainValue == -1)
            return -1;
        
        initMarkers();
        return libertyCount(row-1, col-1, libertyValue, chainValue);
    }
    
    private int libertyCount(int rowAt, int colAt, int libertyValue, int chainValue)
    {
        if (markers[rowAt * numColumns + colAt] >= 0)
        {
            // Already visited.
            return 0;
        }
        
        markers[rowAt * numColumns + colAt] = 0;
        
        if (getIntAt(rowAt+1, colAt+1) == libertyValue)
        {
            // Found a liberty.
            return 1;
        }
        else if (getIntAt(rowAt+1, colAt+1) != chainValue)
        {
            // Blocked.
            return 0;
        }
        
        int count = 0;
        
        for (Direction dir : Direction.ORTHOGONALS)
        {
            if (isValidShift(rowAt, colAt, dir, 1))
            {
                count += libertyCount(rowAt + dir.rowShift, colAt + dir.columnShift, libertyValue, chainValue);
            }
        }
        
        return count;
    }
        
    /**
     * Decomposes this grid into connected components.  The specified
     * <code>boundaryValue</code> indicates the value that separates grid
     * components.  After decomposition, each component will consist of a
     * single contiguous region completely surrounded by entries of value
     * <code>boundaryValue</code>.  The region will have the smallest
     * possible size.
     * <p>
     * If this grid has just one such contiguous component, and is already
     * of minimal size, then the return value will be an array of size one
     * whose single entry is <code>this</code>.
     * <p>
     * The <code>directions</code> parameter controls the type of
     * decomposition.  Typically, this will be one of:
     * <ul>
     *   <li>{@link Direction#ORTHOGONALS} to decompose into
     *     orthogonally connected regions; or
     *   <li>{@link Direction#ALL} to decompose into regions that
     *     are connected orthogonally and/or diagonally.
     * </ul>
     * Other direction sets are permitted (such as
     * {@link Direction#DIAGONALS}), but the direction set must be closed
     * under inverses (e.g., if it contains {@link Direction#NORTH}, then
     * it must also contain {@link Direction#SOUTH}).
     * 
     * @param   boundaryValue The value to use as a boundary.
     * @param   directions The directions to consider for decomposition.
     */
    public CgsuiteList decompose(int boundaryValue)
    {
        EnumSet<Direction> directions = Direction.ORTHOGONALS;
        initMarkers();
        regionAt = -1;
        for (int row = 0; row < numRows; row++)
        {
            for (int col = 0; col < numColumns; col++)
            {
                if (markers[row * numColumns + col] == -1 && getIntAt(row+1, col+1) != boundaryValue)
                {
                    // Found a new region.
                    regionAt++;
                    if (regions == null || regions.length <= regionAt)
                    {
                        RegionInfo[] oldRegions = regions;
                        regions = new RegionInfo[regions == null ? 16 : regions.length << 1];
                        if (oldRegions != null)
                        {
                            System.arraycopy(oldRegions, 0, regions, 0, oldRegions.length);
                        }
                        for (int i = (oldRegions == null ? 0 : oldRegions.length); i < regions.length; i++)
                        {
                            regions[i] = new RegionInfo();
                        }
                    }
                    regions[regionAt].left = regions[regionAt].right = col;
                    regions[regionAt].top = regions[regionAt].bottom = row;
                    markRegion(boundaryValue, directions, regionAt, row, col);
                }
            }
        }
        
        if (regionAt == 0 && regions[0].left == 0 && regions[0].right == numColumns-1 &&
            regions[0].top == 0 && regions[0].bottom == numRows - 1)
        {
            CgsuiteList list = new CgsuiteList(1);
            list.add(this);
            return list;
        }
        
        CgsuiteList decomposition = new CgsuiteList(regionAt+1);
        for (int regionIndex = 0; regionIndex <= regionAt; regionIndex++)
        {
            int nRows = regions[regionIndex].bottom - regions[regionIndex].top + 1,
                nColumns = regions[regionIndex].right - regions[regionIndex].left + 1;
            Grid grid = new Grid(type, nRows, nColumns, bitsPerEntry);
            for (int row = 0; row < nRows; row++)
            {
                for (int col = 0; col < nColumns; col++)
                {
                    int oldRow = row + regions[regionIndex].top,
                        oldCol = col + regions[regionIndex].left;
                    if (markers[oldRow * numColumns + oldCol] == regionIndex)
                    {
                        grid.putAt(row+1, col+1, getIntAt(oldRow+1, oldCol+1));
                    }
                    else
                    {
                        grid.putAt(row+1, col+1, boundaryValue);
                    }
                }
            }
            decomposition.add(grid);
        }
        
        return decomposition;
    }
    
    private void markRegion(int boundaryValue, EnumSet<Direction> directions, int regionIndex, int row, int col)
    {
        markers[row * numColumns + col] = regionIndex;
        if (row < regions[regionIndex].top)
        {
            regions[regionIndex].top = row;
        }
        else if (row > regions[regionIndex].bottom)
        {
            regions[regionIndex].bottom = row;
        }
        if (col < regions[regionIndex].left)
        {
            regions[regionIndex].left = col;
        }
        else if (col > regions[regionIndex].right)
        {
            regions[regionIndex].right = col;
        }
        for (Direction dir : directions)
        {
            if (isValidShift(row, col, dir, 1))
            {
                int nextRow = row + dir.rowShift,
                    nextCol = col + dir.columnShift;
                if (markers[nextRow * numColumns + nextCol] != regionIndex && getIntAt(nextRow+1, nextCol+1) != boundaryValue)
                {
                    markRegion(boundaryValue, directions, regionIndex, nextRow, nextCol);
                }
            }
        }
    }
    
    private final static Comparator<byte[]> BYTE_ARRAY_COMPARATOR = new Comparator<byte[]>()
    {
        @Override
        public int compare(byte[] grid1, byte[] grid2)
        {
            for (int i = 0; i < grid1.length; i++)
            {
                int cmp = grid1[i] - grid2[i];
                if (cmp != 0)
                    return cmp;
            }
            return 0;
        }
    };
    
    private static class RegionInfo
    {
        int left, right, top, bottom;
    }
    
    /**
     * Specifies the number of bits per grid entry.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.10 $ $Date: 2007/02/16 20:10:14 $
     * @since   0.7
     */
    public enum BitsPerEntry
    {
        /**
         * Indicates that this grid should use one bit per grid entry.  The value
         * of each entry must be either 0 or 1.
         */
        ONE     (1, 8, 0x01),
        /**
         * Indicates that this grid should use two bits per grid entry.  The value
         * of each entry must be between 0 and 3, inclusive.
         */
        TWO     (2, 4, 0x03),
        /**
         * Indicates that this grid should use four bits per grid entry.  The value
         * of each entry must be between 0 and 7, inclusive.
         */
        FOUR    (4, 2, 0x0f),
        /**
         * Indicates that this grid should use eight bits per grid entry.  The value
         * of each entry must be between 0 and 255, inclusive.
         */
        EIGHT   (8, 1, 0xff);
        
        private BitsPerEntry(int bits, int perByte, int mask)
        {
            this.bits = bits;
            this.perByte = perByte;
            this.mask = mask;
        }
        
        final int bits;
        final int perByte;
        final int mask;
    }
    
}
