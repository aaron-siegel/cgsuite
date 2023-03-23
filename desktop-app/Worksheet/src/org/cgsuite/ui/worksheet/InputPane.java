/*
 * InputPane.java
 *
 * Created on December 11, 2002, 11:07 PM
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

package org.cgsuite.ui.worksheet;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

public class InputPane extends JEditorPane
{
    JPopupMenu popupMenu;

    public InputPane()
    {
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showPopupMenu(evt);
                }
            }
            @Override public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showPopupMenu(evt);
                }
            }
        });
    }

    public void showPopupMenu(MouseEvent evt) {
        PopupMenuHelper.INPUT_PANE_POPUP_MENU.show(this, evt.getX(), evt.getY());
    }
    
    public void insert(String str, int pos)
    {
        assert SwingUtilities.isEventDispatchThread();
        
        try
        {
            getDocument().insertString(pos, str, null);
        }
        catch (BadLocationException exc)
        {
        }
    }

    public int getCaretLine()
    {
        assert SwingUtilities.isEventDispatchThread();
        
        return getDocument().getDefaultRootElement().getElementIndex(getCaretPosition());
    }

    public int getLineCount()
    {
        assert SwingUtilities.isEventDispatchThread();
        
        return getDocument().getDefaultRootElement().getElementCount();
    }
}
