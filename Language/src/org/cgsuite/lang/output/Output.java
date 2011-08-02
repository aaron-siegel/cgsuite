/*
 * Output.java
 *
 * Created on March 31, 2003, 4:30 PM
 * $Id: Output.java,v 1.8 2007/02/16 20:10:13 asiegel Exp $
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.PrintWriter;

/**
 * An abstract output scheme that can be converted to any of several forms of
 * output.  Combinatorial Game Suite supports numerous object types (various
 * implementations of {@link org.cgsuite.Game}, strings, tables, etc.) and several
 * output methods (plain text, LaTeX, screen images, etc.)  <code>Output</code>
 * objects serve as abstract intermediate representations between the internal
 * data structures and the output that users actually see.  This avoids
 * excessive code duplication, and simplifies the task of generating output
 * for plug-ins.
 * <p>
 * It is rarely necessary to implement <code>Output</code> directly.  The
 * standard implementations, {@link StyledTextOutput} and {@link ImageOutput},
 * should be suitable for most plug-ins.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.8 $ $Date: 2007/02/16 20:10:13 $
 */
public interface Output
{
    /**
     * Writes this output in text format.
     */
    void write(PrintWriter out, Mode mode);
    
    /**
     * Gets the amount of space required to display this output completely
     * with the specified preferred width.  The result should be
     * <i>exactly</i> the amount of space needed to render this output
     * completely using
     * {@link #paint(Graphics2D, int) paint}.
     *
     * @param   preferredWidth The desired width of the output, in pixels.
     * @return  The amount of space required to render this output
     *          completely.
     * @see     #paint(Graphics2D, int) paint
     */
    Dimension getSize(int preferredWidth);
    
    /**
     * Paints this output on the specified <code>Graphics2D</code>.
     * The output will paint only those regions bounded by the
     * <code>Graphics2D</code> object's clip.
     * <p>
     * The <code>preferredWidth</code> parameter is the <i>desired</i> width
     * of the image, in pixels.  It is not an absolute requirement.  The
     * <code>Output</code> object will attempt to fit the image within the
     * specified <code>preferredWidth</code>, <i>provided</i> that this does not
     * otherwise compromise the readability of the display.
     *
     * @param   graphics The graphics context on which the output should be
     *          painted.
     * @param   preferredWidth The desired width of the output, in pixels.
     */
    void paint(Graphics2D graphics, int preferredWidth);
        
    /**
     * An output mode.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.8 $ $Date: 2007/02/16 20:10:13 $
     * @since   0.7
     */
    public enum Mode
    {
        /** Graphical output (typically an image on the screen). */
        GRAPHICAL       (false),
        /** Plain-text output. */
        PLAIN_TEXT      (true),
        /** LaTeX source. */
        LATEX_SOURCE    (true);
        
        private boolean isTextMode;
        
        private Mode(boolean isTextMode)
        {
            this.isTextMode = isTextMode;
        }
        
        public boolean isTextMode()
        {
            return isTextMode;
        }
    }
}
