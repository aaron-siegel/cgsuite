/*
 * NotStopperException.java
 *
 * Created on April 27, 2003, 3:50 PM
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

import org.cgsuite.lang.CgsuiteException;

/**
 * Thrown if a loopy game's
 * {@link LoopyGame#canonicalizeStopper() canonicalizeStopper} method is
 * called, and that game is not a stopper.
 */
public class NotStopperException extends CgsuiteException
{
    /**
     * Constructs a new instance of <code>NotStopperException</code>.
     */
    public NotStopperException()
    {
    }
    
    /**
     * Constructs a new instance of <code>NotStopperException</code> with
     * the specified message.
     */
    public NotStopperException(String message)
    {
        super(message);
    }
}
