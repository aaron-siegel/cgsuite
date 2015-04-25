/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.output;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.InputException;

/**
 *
 * @author asiegel
 */
public class IntensityPlotOutput extends CgsuiteObject implements Output
{
    public static final CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("IntensityPlot");
    
    private int[][] array;
    private int rowCount;
    private int columnCount;
    private int maxValue;
    
    public IntensityPlotOutput(int[][] array)
    {
        super(TYPE);
        
        this.array = array;
        this.rowCount = array.length;
        this.columnCount = -1;
        for (int i = 0; i < rowCount; i++)
        {
            if (columnCount >= 0 && columnCount != array[i].length)
                throw new InputException("Rows are not equal in size.");
            
            columnCount = Math.max(columnCount, array[i].length);
            
            for (int j = 0; j < columnCount; j++)
            {
                maxValue = Math.max(maxValue, array[i][j]);
            }
        }
    }

    @Override
    public void write(PrintWriter out, Mode mode)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Dimension getSize(int preferredWidth)
    {
        return new Dimension(columnCount * 8, rowCount * 8);
    }

    @Override
    public void paint(Graphics2D graphics, int preferredWidth)
    {
        for (int i = 0; i < rowCount; i++)
        {
            for (int j = 0; j < columnCount; j++)
            {
                float color = ((float) array[i][j]) / ((float) maxValue);
                graphics.setColor(new Color(color, color, color));
                graphics.fillRect(j*8, i*8, 8, 8);
            }
        }
    }
    
    @Override
    public IntensityPlotOutput toOutput()
    {
        return this;
    }
}
