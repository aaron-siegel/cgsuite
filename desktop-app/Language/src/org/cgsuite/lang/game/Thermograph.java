/*
 * Thermograph.java
 *
 * Created on October 22, 2002, 10:00 PM
 * $Id: Thermograph.java,v 1.20 2007/02/13 22:39:46 asiegel Exp $
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

import java.awt.Color;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.output.PlotOutput;
import org.cgsuite.lang.output.StyledTextOutput;


/**
 * A thermograph.  Thermographs are represented internally as pairs of
 * trajectories.  As of 0.6 this class supports generalized thermography.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.20 $ $Date: 2007/02/13 22:39:46 $
 */
public class Thermograph extends CgsuiteObject
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Thermograph"); 
    
    /** A <code>Thermograph</code> that is equal to a mast at 0. */
    public final static Thermograph ZERO =
        new Thermograph(Trajectory.ZERO, Trajectory.ZERO);
    /** A <code>Thermograph</code> that is equal to a mast at -infinity. */
    public final static Thermograph NEGATIVE_INFINITY =
        new Thermograph(Trajectory.NEGATIVE_INFINITY, Trajectory.NEGATIVE_INFINITY);
    /** A <code>Thermograph</code> that is equal to a mast at infinity. */
    public final static Thermograph POSITIVE_INFINITY =
        new Thermograph(Trajectory.POSITIVE_INFINITY, Trajectory.POSITIVE_INFINITY);

    private Trajectory leftWall, rightWall;

    private Thermograph()
    {
        super(TYPE);
    }

    /**
     * Constructs a new <code>Thermograph</code> which is a simple mast at the
     * specified integer value.
     *
     * @param   mast The mast value of the thermograph.
     * @throws  IllegalArgumentException mast is not an integer.
     */
    public Thermograph(RationalNumber mast)
    {
        this();
        leftWall = rightWall = new Trajectory(mast);
    }

    /**
     * Constructs a new <code>Thermograph</code> from explicitly provided
     * walls.
     *
     * @param   leftWall The left wall of the thermograph.
     * @param   rightWall The right wall of the thermograph.
     * @throws  IllegalArgumentException if <code>rightWall.leq(leftWall)</code>
     *          returns <code>false</code>.
     */
    public Thermograph(Trajectory leftWall, Trajectory rightWall)
    {
        this();
        if (rightWall.leq(leftWall))
        {
            this.leftWall = leftWall;
            this.rightWall = rightWall;
        }
        else
        {
            throw new IllegalArgumentException("!rightWall.leq(leftWall)");
        }
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Thermograph &&
            ((Thermograph) o).leftWall.equals(leftWall) &&
            ((Thermograph) o).rightWall.equals(rightWall);
    }

    @Override
    public int hashCode()
    {
        return leftWall.hashCode() ^ rightWall.hashCode();
    }

    /**
     * Gets the left wall of this thermograph.
     *
     * @return  The left wall of this thermograph.
     */
    public Trajectory getLeftWall()
    {
        return leftWall;
    }

    /**
     * Gets the right wall of this thermograph.
     *
     * @return  The right wall of this thermograph.
     */
    public Trajectory getRightWall()
    {
        return rightWall;
    }

    /**
     * Gets the (left or right) value of this thermograph at the
     * specified temperature.  This is equivalent to
     * <p>
     * <code>(left ? getLeftWall() : getRightWall()).valueAt(t)</code>
     *
     * @param   left <code>true</code> to get the left value;
     *          <code>false</code> to get the right value
     * @param   t the temperature
     * @return  the value of this thermograph at <code>t</code>
     */
    public RationalNumber valueAt(boolean left, RationalNumber t)
    {
        return (left ? leftWall : rightWall).valueAt(t);
    }

    /**
     * Gets the temperature of this thermograph.  This is equal to the largest
     * critical temperature, or <code>-1</code> if this thermograph is a
     * straight vertical mast (in which case it must represent an integer).
     *
     * @return  The temperature of this thermograph.
     */
    public RationalNumber getTemperature()
    {
        RationalNumber leftTemperature = getLeftTemperature(), rightTemperature = getRightTemperature();
        // TODO: Check this carefully:
        if (leftWall.valueAt(leftTemperature).compareTo(rightWall.valueAt(rightTemperature)) > 0)
        {
            return RationalNumber.POSITIVE_INFINITY;
        }
        return leftTemperature.max(rightTemperature);
    }

    /**
     * Gets the left temperature of this thermograph.  This is equal to the
     * largest left critical temperature, or <code>-1</code> if the left wall
     * is a straight vertical mast.
     *
     * @return  The left temperature of this thermograph.
     */
    public RationalNumber getLeftTemperature()
    {
        return leftWall.getNumCriticalPoints() == 0 ? RationalNumber.NEGATIVE_ONE : leftWall.getCriticalPoint(0);
    }

    /**
     * Gets the right temperature of this thermograph.  This is equal to the
     * largest right critical temperature, or <code>-1</code> if the right wall
     * is a straight vertical mast.
     *
     * @return  The right temperature of this thermograph.
     */
    public RationalNumber getRightTemperature()
    {
        return rightWall.getNumCriticalPoints() == 0 ? RationalNumber.NEGATIVE_ONE : rightWall.getCriticalPoint(0);
    }

    /**
     * Gets the mast value of this thermograph.
     *
     * @return  The mast value of this thermograph.
     */
    public RationalNumber getMast()
    {
        if (leftWall.equals(Trajectory.POSITIVE_INFINITY))
        {
            return rightWall.getSlope(0).equals(RationalNumber.ZERO) ? rightWall.valueAt(getTemperature()) : RationalNumber.POSITIVE_INFINITY;
        }
        else if (rightWall.equals(Trajectory.NEGATIVE_INFINITY))
        {
            return leftWall.getSlope(0).equals(RationalNumber.ZERO) ? leftWall.valueAt(getTemperature()) : RationalNumber.NEGATIVE_INFINITY;
        }
        else
        {
            return leftWall.valueAt(getTemperature());
        }
    }

    /**
     * Tests whether this is a double-sente thermograph.
     * A <i>double-sente thermograph</i> is a thermograph in which the left wall
     * is asympotically strictly greater than the right wall.
     *
     * @return  <code>true</code> if this is a double-sente thermograph.
     */
    public boolean isDoubleSente()
    {
        return leftWall.valueAt(RationalNumber.POSITIVE_INFINITY).compareTo(rightWall.valueAt(RationalNumber.POSITIVE_INFINITY)) > 0;
    }

    /**
     * Tests whether this thermograph is less than or equal to <code>t</code>.
     * Returns <code>true</code> if the left wall of this thermograph is less
     * than or equal to the left wall of <code>t</code> at every temperature
     * between -epsilon and +infinity, and likewise for the right wall.
     * <p>
     * This method returns the same result as
     * <code>getLeftWall().leq(t.getLeftWall()) && getRightWall().leq(t.getRightWall())</code>.
     *
     * @param   t The thermograph to compare this against.
     * @return  <code>true</code> if this thermograph is less than or equal to
     *          <code>t</code>.
     */
    public boolean leq(Thermograph t)
    {
        return leftWall.leq(t.leftWall) && rightWall.leq(t.rightWall);
    }

    /**
     * Calculates the max of this thermograph and <code>t</code>.
     */
    public Thermograph max(Thermograph t)
    {
        return new Thermograph(leftWall.max(t.leftWall), rightWall.max(t.rightWall));
    }

    /**
     * Calculates the max of this thermograph and <code>t</code>.
     */
    public Thermograph min(Thermograph t)
    {
        return new Thermograph(leftWall.min(t.leftWall), rightWall.min(t.rightWall));
    }

    /**
     * Translates this thermograph by <code>r</code>.
     */
    public Thermograph translate(RationalNumber r)
    {
        return new Thermograph(leftWall.translate(r), rightWall.translate(r));
    }

    /**
     * Calculates the Left compound of this thermograph and <code>t</code>.
     * The <i>Left compound</i> of two thermographs <code>T</code> and
     * <code>U</code> is equal to
     * <p>
     * <code>{ max(T<sup>L</sup>+U<sup>R</sup>,T<sup>R</sup>+U<sup>L</sup>) | T<sup>R</sup>+U<sup>R</sup> }</code>
     *
     * @param   t the other thermograph to compound this with
     * @return  The resulting Left compound thermograph
     */
    public Thermograph leftCompound(Thermograph t)
    {
        Thermograph compound = new Thermograph();
        compound.rightWall = rightWall.add(t.rightWall);
        compound.leftWall = leftWall.add(t.rightWall).max(t.leftWall.add(rightWall));;
        return compound;
    }

    /**
     * Calculates the Right compound of this thermograph and <code>t</code>.
     * The <i>Right compound</i> of two thermographs <code>T</code> and
     * <code>U</code> is equal to
     * <p>
     * <code>{ T<sup>L</sup>+U<sup>L</sup> | min(T<sup>L</sup>+U<sup>R</sup>,T<sup>R</sup>+U<sup>L</sup>) }</code>
     *
     * @param   t the other thermograph to compound this with
     * @return  The resulting Right compound thermograph
     */
    public Thermograph rightCompound(Thermograph t)
    {
        Thermograph compound = new Thermograph();
        compound.leftWall = leftWall.add(t.leftWall);
        compound.rightWall = rightWall.add(t.leftWall).min(t.rightWall.add(leftWall));
        return compound;
    }
    
    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("Thermograph(");
        output.appendOutput(leftWall.toOutput2());
        output.appendMath(",");
        output.appendOutput(rightWall.toOutput2());
        output.appendMath(")");
        return output;
    }
    
    public PlotOutput plot()
    {
        PlotOutput plotOutput = new PlotOutput();
        plotOutput.addThermograph(this, Color.black, 0);
        return plotOutput;
    }
}