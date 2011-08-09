/*
 * APInfo.java
 *
 * Created on November 14, 2006, 8:59 AM
 * $Id: APInfo.java,v 1.2 2007/02/13 22:39:46 asiegel Exp $
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

package org.cgsuite.lang.impartial;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;

/**
 * Utility class for representing information concerning arithmeto-periodic behaviour.
 * @author malbert
 * @version $Revision: 1.2 $ $Date: 2007/02/13 22:39:46 $
 */
public class APInfo extends CgsuiteObject
{
    public final static CgsuiteClass TYPE = CgsuitePackage.lookupPackage("game.heap").forceLookupClassInPackage("Periodicity");
    
	private int preperiod;
	private int period;
	private int saltus;
	
    /**
     * Basic constructor.
     * @param preperiod The preperiod.
     * @param period The period.
     * @param saltus The saltus.
     */
	public APInfo(int preperiod, int period, int saltus) {
        super(TYPE);
		this.preperiod = preperiod;
		this.period = period;
		this.saltus = saltus;
	}
	
    /**
     * Return the preperiod.
     * @return The preperiod.
     */
	public int getPreperiod() {
		return preperiod;
	}
	
    /**
     * Return the period.
     * @return The period.
     */
	public int getPeriod(){
		return period;
	}
	
    /**
     * Return the saltus.
     * @return The saltus.
     */
	public int getSaltus(){
		return saltus;
	}
	
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final APInfo other = (APInfo) obj;
        if (this.preperiod != other.preperiod) {
            return false;
        }
        if (this.period != other.period) {
            return false;
        }
        if (this.saltus != other.saltus) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.preperiod;
        hash = 67 * hash + this.period;
        hash = 67 * hash + this.saltus;
        return hash;
    }
	
}
