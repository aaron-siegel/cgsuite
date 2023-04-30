/*
 * Relation.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: Relation.java,v 1.1 2006/10/18 20:52:26 asiegel Exp $
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

package org.cgsuite.core.misere.solver;

public class Relation
{
    private Word lhs, rhs;

    public Relation(Word lhs, Word rhs)
    {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Word getLHS()
    {
        return lhs;
    }

    public Word getRHS()
    {
        return rhs;
    }

    public String toString()
    {
        return lhs.toString() + "=" + rhs.toString();
    }
}
