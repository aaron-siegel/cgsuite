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

/**
 * Utility class for representing information concerning arithmeto-periodic behaviour.
 * @author malbert
 * @version $Revision: 1.2 $ $Date: 2007/02/13 22:39:46 $
 */
public class APInfo {
	
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
	
    /**
     * String representation, for example: "Preperiod = 23, Period = 57, Saltus = 12".
     * @return A String representing this APInfo object.
     */
	public String toString() {
		return "Preperiod = " + preperiod + ", Period = " + period + ", Saltus = " + saltus;
	}
	
    /**
     * Compares with another APInfo object. Lexicographic in the order: preperiod, period, saltus.
     * @param o The object to compare to.
     * @return The result of the comparison.
     */
	public int compareTo(APInfo o) {
		if (this.preperiod != o.preperiod) {
			return this.preperiod - o.preperiod;
		}
		
		if (this.period != o.period) {
			return this.period - o.period;
		}
		
		return this.saltus - o.saltus;
	}
	
    /**
     * Compare with another object for equality.
     * @param o The object to compare to.
     * @return The result of the comparison.
     */
	public boolean equals(Object o) {
		
		if (o instanceof APInfo) {
			return (this.compareTo((APInfo) o) == 0);
		}
		
		return false;
	}
	
    /**
     * A hashcode for this object, computed as a linear combination of the three data fields.
     * @return A hashcode for this object.
     */
	 public @Override int hashCode() {
		 return 31973*preperiod + 25719*period + 439823*saltus;
	 }
       
    
}
