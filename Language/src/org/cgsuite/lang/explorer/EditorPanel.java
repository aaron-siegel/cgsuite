/*
 * EditorPanel.java
 *
 * $Id: EditorPanel.java,v 1.13 2005/12/09 23:23:29 asiegel Exp $
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

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import org.cgsuite.lang.CgsuiteObject;


/**
 * A graphical component used by the GUI to edit game positions.
 * {@link IOHandler}s are responsible for creating appropriate instances of
 * this class, populating them with data, and constructing objects based on
 * the user's input.
 * Every graphical component used to edit game positions must be
 * a subclass of <code>EditorPanel</code>.  Although <code>EditorPanel</code>s
 * are most commonly used to edit instances of {@link org.cgsuite.Game}, this
 * is not a requirement, and editors can theoretically be built for any
 * <code>Object</code>.
 * <p>
 * Grid-based games (those played on a two-dimensional board) can use the
 * {@link GridEditorPanel} subclass.  Custom subclasses should be built for
 * other types of games (or grid-based games for which
 * <code>GridEditorPanel</code> is not suitable).
 * <p>
 * When subclassing <code>EditorPanel</code>, make sure your subclass calls
 * <code>firePropertyChange(EDIT_STATE_PROPERTY, ...)</code>
 * whenever its state is modified.  This
 * will insure that the UI's graphical components are properly updated in
 * response to an edit.
 *
 * @author  Samson de Jager
 * @author  Aaron Siegel
 * @version $Revision: 1.13 $ $Date: 2005/12/09 23:23:29 $
 */
public abstract class EditorPanel extends JPanel implements Scrollable
{
    /**
     * Used as an argument to <code>firePropertyChange</code> to indicate that
     * changes have been made to the state of this editor panel.  The remaining
     * arguments to <code>firePropertyChange</code> are ignored, beyond the
     * usual semantics that no event is fired if the old and new values are
     * equal.
     */
    public final static String EDIT_STATE_PROPERTY =
        "org.cgsuite.ui.explorer.EditorPanel.editState";
    
    /**
     * Refreshes the panel's display based on the specified object.  It is safe
     * to throw an <code>IllegalArgumentException</code> if the object cannot
     * cannot be handled by this editor.
     *
     * @param   obj The object used to update this editor.
     * @throws  IllegalArgumentException <code>obj</code> is not suitable for
     *          this editor.
     */
    //public abstract void updateEditState(Object obj);
    
    /**
     * Constructs an object that corresponds to the
     * current state of the editor.
     *
     * @throws  InvalidEditStateException The current editor state does
     *          not represent a valid object (for example, if it represents an
     *          illegal position in a game).
     */
    public abstract CgsuiteObject constructObject();
    
    @Override
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }
    
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return 20;
    }
    
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return 80;
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth()
    {
        return false;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }
}
