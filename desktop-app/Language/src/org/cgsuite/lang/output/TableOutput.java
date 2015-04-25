/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumSet;
import org.cgsuite.lang.CgsuiteList;
import org.cgsuite.lang.Table;

/**
 *
 * @author asiegel
 */
public class TableOutput extends AbstractOutput
{
    int numColumns, maxCellWidth;
    EnumSet<Table.Format> format;
    Output[][] cells;
    
    public TableOutput(Table table)
    {
        numColumns = table.getNumColumns();
        maxCellWidth = table.getMaxCellWidth();
        format = table.getFormat().clone();
        cells = new Output[table.getNumRows()][];
        
        for (int i = 0; i < cells.length; i++)
        {
            CgsuiteList row = table.getRow(i+1);
            cells[i] = new Output[row.size()];
            for (int j = 0; j < row.size(); j++)
            {
                if (row.get(j+1) != null)
                {
                    cells[i][j] = row.get(j+1).toOutput();
                }
            }
        }
    }
    
    @Override
    public Dimension getSize(int preferredWidth)
    {
        int hSpace, vSpace;
        if (format.contains(Table.Format.GRID_LINES_HORIZONTAL))
        {
            vSpace = 6;
        }
        else
        {
            vSpace = 0;
        }
        
        if (format.contains(Table.Format.GRID_LINES_VERTICAL))
        {
            hSpace = 6;
        }
        else
        {
            hSpace = 0;
        }
        
        int[] rowHeight = new int[cells.length];
        int[] colWidth = new int[numColumns];
        
        for (int i = 0; i < cells.length; i++)
        {
            for (int j = 0; j < cells[i].length; j++)
            {
                if (cells[i][j] != null)
                {
                    int cellWidth = cells[i][j].getSize(0).width, cellHeight = cells[i][j].getSize(0).height;
                    if (cellHeight > rowHeight[i])
                    {
                        rowHeight[i] = cellHeight;
                    }
                    if (cellWidth > colWidth[j])
                    {
                        colWidth[j] = cellWidth;
                    }
                }
            }
        }
        
        int width = 0, height = 0;
        for (int i = 0; i < cells.length; i++)
        {
            height += rowHeight[i] + vSpace * 2;
        }
        height += cells.length + 1;
        for (int j = 0; j < numColumns; j++)
        {
            width += colWidth[j] + hSpace * 2;
        }
        width += numColumns + 1;
        return new Dimension(width, height);
    }
    
    @Override
    public void paint(Graphics2D g, int preferredWidth)
    {
        Dimension size = getSize(0);
        
        g.setBackground(Color.white);
        g.setColor(Color.black);
        g.clearRect(0, 0, size.width, size.height);
        
        int hSpace, vSpace;
        if (format.contains(Table.Format.GRID_LINES_HORIZONTAL))
        {
            vSpace = 6;
        }
        else
        {
            vSpace = 0;
        }
        
        if (format.contains(Table.Format.GRID_LINES_VERTICAL))
        {
            hSpace = 6;
        }
        else
        {
            hSpace = 0;
        }
        
        int[] rowHeight = new int[cells.length];
        int[] colWidth = new int[numColumns];
        
        for (int i = 0; i < cells.length; i++)
        {
            for (int j = 0; j < cells[i].length; j++)
            {
                if (cells[i][j] != null)
                {
                    int cellWidth = cells[i][j].getSize(0).width, cellHeight = cells[i][j].getSize(0).height;
                    if (cellHeight > rowHeight[i])
                    {
                        rowHeight[i] = cellHeight;
                    }
                    if (cellWidth > colWidth[j])
                    {
                        colWidth[j] = cellWidth;
                    }
                }
            }
        }

        if (format.contains(Table.Format.GRID_LINES_HORIZONTAL))
        {
            for (int i = 0, pos = 0; i <= cells.length; i++)
            {
                g.drawLine(0, pos, size.width, pos);
                if (i < cells.length)
                {
                    pos += rowHeight[i] + vSpace * 2 + 1;
                }
            }
        }
        
        if (format.contains(Table.Format.GRID_LINES_VERTICAL))
        {
            for (int j = 0, pos = 0; j <= numColumns; j++)
            {
                g.drawLine(pos, 0, pos, size.height);
                if (j < numColumns)
                {
                    pos += colWidth[j] + hSpace * 2 + 1;
                }
            }
        }
        
        for (int i = 0, vPos = 0; i < cells.length; i++)
        {
            for (int j = 0, hPos = 0; j < Math.min(numColumns, cells[i].length); j++)
            {
                if (cells[i][j] != null &&
                    g.hitClip(hPos + hSpace + 1, vPos + vSpace + 1, colWidth[j], rowHeight[i]))
                {
                    // Center this cell in the table.
                    Dimension cellSize = cells[i][j].getSize(0);
                    int topEdge = (rowHeight[i] - cellSize.height) / 2;
                    int leftEdge = (colWidth[j] - cellSize.width) / 2;
                    if (g.hitClip(hPos+hSpace+1+leftEdge, vPos+vSpace+1+topEdge, cellSize.width, cellSize.height))
                    {
                        cells[i][j].paint((Graphics2D) g.create(hPos+hSpace+1+leftEdge, vPos+vSpace+1+topEdge, cellSize.width, cellSize.height), 0);
                    }
                }
                hPos += hSpace * 2 + 1 + colWidth[j];
            }
            vPos += vSpace * 2 + 1 + rowHeight[i];
        }
    }
    
    @Override
    public void write(PrintWriter out, Output.Mode mode)
    {
        if (mode != Output.Mode.PLAIN_TEXT)
        {
            throw new UnsupportedOperationException();
        }
        String[] outputStrings = new String[cells.length*numColumns];
        int[] columnWidths = new int[numColumns];
        for (int i = 0; i < cells.length; i++)
        {
            for (int j = 0; j < numColumns; j++)
            {
                if (j >= cells[i].length || cells[i][j] == null)
                {
                    outputStrings[i*numColumns+j] = "";
                }
                else
                {
                    outputStrings[i*numColumns+j] = cells[i][j].toString();
                    int width = stringWidth(outputStrings[i*numColumns+j]);
                    if (maxCellWidth != 0 && width > maxCellWidth)
                    {
                        outputStrings[i*numColumns+j] = "?";
                        width = 1;
                    }
                    columnWidths[j] = Math.max(columnWidths[j], width);
                }
            }
        }
        
        StringBuilder outputString = new StringBuilder();
        
        int[] cellWidths = new int[numColumns];
        int[] columnRowOffsets = new int[numColumns];

        String hyph="-----------------";
        String rowSep = "\n";

        if (format.contains(Table.Format.GRID_LINES_HORIZONTAL) && cells.length > 1)
        {
            StringBuilder hLine = new StringBuilder();
            hLine.append("\n");
            for (int j = 0; j < numColumns; j++)
            {
                while (hyph.length() <= columnWidths[j]) hyph = hyph.concat(hyph);
                hLine.append(hyph.substring(0,columnWidths[j]));
                if (j < numColumns-1)
                {
                    hLine.append(format.contains(Table.Format.GRID_LINES_VERTICAL)
                                 ? "-+-" : "-");
                }
            }
            hLine.append("\n");
            rowSep = hLine.toString();
        }

        for (int i = 0; i < cells.length; i++)
        {
            int rowHeight=0;
            for (int j = 0; j < numColumns; j++)
            {
                cellWidths[j] =stringWidth(outputStrings[i*numColumns+j]);
                columnRowOffsets[j] = stringHeight(outputStrings[i*numColumns+j]);
                if(rowHeight < columnRowOffsets[j]) rowHeight = columnRowOffsets[j];
            }
            for (int j = 0; j < numColumns; j++)
            {
                columnRowOffsets[j] = (rowHeight - columnRowOffsets[j])/2;
            }
            for (int h = 0; h < rowHeight; ++h)
            {
                for (int j = 0; j < numColumns; j++)
                {
                    String output = nthRow(outputStrings[i*numColumns+j],
                                           h - columnRowOffsets[j]);
                    int whitespace = columnWidths[j]-cellWidths[j];
                    int lws = whitespace/2;
                    int rws = whitespace - lws + cellWidths[j] - output.length();
                    String fmt = "%"+(lws>0 ? lws : "") + "s%s%" + (rws>0 ? rws : "") + "s";
                    outputString.append(String.format(fmt,"",output,""));
                    if (j < numColumns-1)
                    {
                        boolean vert = format.contains(Table.Format.GRID_LINES_VERTICAL);
                        outputString.append(vert ? " | " : " ");
                    }
                }
                if ( h < rowHeight-1 ) {
                    outputString.append("\n");
                }
            }
            if (i < cells.length-1)
            {
                outputString.append(rowSep);
            }
        }
        out.print(outputString.toString());
    }

    private int stringWidth(String s)
    {
        int nextNewline=s.indexOf('\n');
        if (nextNewline < 0)
        {
            return s.length();
        }
        int stringWidth=nextNewline;
        for (int pos=nextNewline+1; pos<s.length(); pos=nextNewline+1)
        {
            nextNewline=s.indexOf('\n',pos);
            if (nextNewline < 0)
            {
                nextNewline = s.length();
            }
            if (stringWidth < nextNewline-pos)
            {
                stringWidth = nextNewline-pos;
            }
        }
        return stringWidth;
    }

    private String nthRow(String s, int n)
    {
        if (n < 0) return "";
        int nextNewline = s.indexOf('\n');
        if(nextNewline<0) return n==0 ? s : "";
        int pos=0;
        for (int i=0; i<n; ++i)
        {
            if (nextNewline < 0) return "";
            pos=nextNewline+1;
            nextNewline=s.indexOf('\n',pos);
        }
        return s.substring(pos,nextNewline < 0 ? s.length() : nextNewline);
    }

    private int stringHeight(String s)
    {
        int stringHeight=0;
        int nextNewline;
        for (int pos=0; pos<s.length(); pos=nextNewline+1)
        {
            nextNewline=s.indexOf('\n',pos);
            stringHeight++;
            if ( nextNewline < 0 )
            {
                return stringHeight;
            }
        }
        return stringHeight;
    }
}