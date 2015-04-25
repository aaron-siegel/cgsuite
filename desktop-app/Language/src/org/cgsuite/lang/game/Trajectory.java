/*
 * Trajectory.java
 *
 * Created on March 16, 2004, 1:48 PM
 * $Id: Trajectory.java,v 1.14 2007/05/28 16:57:30 asiegel Exp $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 * A continuous piecewise linear trajectory with rational slopes and critical
 * points.  Each trajectory is defined for all rational numbers on the interval
 * <code>-1 &lt;= x &lt; Infinity</code>.  A <i>critical point</i> is a point
 * at which the trajectory changes slope, and must be strictly between
 * <code>-1</code> and <code>Infinity</code>.
 * <p>
 * This class includes methods for taking the max, min, or (generalized)
 * thermographic intersection of two trajectories.
 *
 * @version $Revision: 1.14 $ $Date: 2007/05/28 16:57:30 $
 * @author  Aaron Siegel
 * @since   0.6
 */
public class Trajectory extends CgsuiteObject
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Trajectory");
    
    private final static RationalNumber[]
        EMPTY_ARRAY = { },
        SINGLE_ZERO_ARRAY = { RationalNumber.ZERO };

    /** The trajectory with constant value {@link RationalNumber#ZERO}. */
    public final static Trajectory ZERO = new Trajectory(RationalNumber.ZERO);
    /** The trajectory with constant value {@link RationalNumber#POSITIVE_INFINITY}. */
    public final static Trajectory POSITIVE_INFINITY = new Trajectory(RationalNumber.POSITIVE_INFINITY);
    /** The trajectory with constant value {@link RationalNumber#NEGATIVE_INFINITY}. */
    public final static Trajectory NEGATIVE_INFINITY = new Trajectory(RationalNumber.NEGATIVE_INFINITY);
    /** The trajectory consisting of a single segment with slope 1 and intercept 0. */
    public final static Trajectory T = ZERO.tilt(RationalNumber.ONE);

    // The critical points.
    private RationalNumber[] criticalPoints;
    // slopes[i] is the slope of the line segment above criticalPoints[i]
    // (so slopes[0] is always the mast slope).
    private RationalNumber[] slopes;
    // xIntercepts[i] is the x-intercept of the line above criticalPoints[i].
    // This is redundant information but is cached to streamline calculations.
    // Notice that if the mast is vertical then xIntercepts[i] is its value.
    private RationalNumber[] xIntercepts;

    private Trajectory()
    {
        super(TYPE);
    }

    /**
     * Constructs a new <code>Trajectory</code> with constant value
     * <code>r</code>.
     *
     * @param   r The constant value of this trajectory.
     */
    public Trajectory(RationalNumber r)
    {
        this();
        criticalPoints = EMPTY_ARRAY;
        slopes = SINGLE_ZERO_ARRAY;
        xIntercepts = new RationalNumber[] { r };
    }

    /**
     * Constructs a new <code>Trajectory</code> with the specified mast,
     * critical points, and slopes.
     *
     * @param   mast The <i>v-intercept</i> of the mast.  This is equal to the
     *          value that the mast would have at <code>t = 0</code> if its
     *          segment were extended down to temperature zero.  If the mast is
     *          vertical, then this is just equal to its value.
     * @param   criticalPoints The critical points of this trajectory.
     * @param   slopes The slopes of this trajectory's components.  The
     *          trajectory is assumed to have slope <code>slopes[i]</code>
     *          between <code>criticalPoints[i]</code> and
     *          <code>criticalPoints[i+1]</code>.
     * @throws  IllegalArgumentException
     *          <code>slopes.length != criticalPoints.length + 1</code>.
     * @throws  IllegalArgumentException The <code>criticalPoints</code> are
     *          not strictly decreasing.
     * @throws  IllegalArgumentException <code>criticalPoints[0] < -1</code>.
     */
    public Trajectory(RationalNumber mast, RationalNumber[] criticalPoints, RationalNumber[] slopes)
    {
        this();
        // Validate the arguments.
        if (slopes.length != criticalPoints.length + 1)
        {
            throw new IllegalArgumentException("slopes must have length one greater than criticalPoints.");
        }
        for (int i = 0; i < criticalPoints.length-1; i++)
        {
            if (criticalPoints[i].compareTo(criticalPoints[i+1]) <= 0)
            {
                throw new IllegalArgumentException("The critical points must be strictly decreasing.");
            }
        }
        if (criticalPoints.length > 0 && criticalPoints[criticalPoints.length-1].compareTo(RationalNumber.NEGATIVE_ONE) <= 0)
        {
            throw new IllegalArgumentException("All critical points must be strictly greater than -1.");
        }

        this.criticalPoints = criticalPoints.clone();
        this.slopes = slopes.clone();
        xIntercepts = new RationalNumber[slopes.length];
        if (criticalPoints.length == 0)
        {
            xIntercepts[0] = mast;
        }
        else
        {
            RationalNumber value = mast;
            int i;
            for (i = 0; i < criticalPoints.length; i++)
            {
                if (i > 0)
                {
                    value = value.subtract(criticalPoints[i-1].subtract(criticalPoints[i]).multiply(slopes[i]));
                }
                xIntercepts[i] = value.subtract(criticalPoints[i].multiply(slopes[i]));
            }
            xIntercepts[i] = value.subtract(criticalPoints[i-1].multiply(slopes[i]));
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Trajectory))
        {
            return false;
        }
        Trajectory t = (Trajectory) o;
        if (criticalPoints.length != t.criticalPoints.length ||
            !xIntercepts[0].equals(t.xIntercepts[0]))
        {
            return false;
        }
        for (int i = 0; i < criticalPoints.length; i++)
        {
            if (!criticalPoints[i].equals(t.criticalPoints[i]))
            {
                return false;
            }
        }
        for (int i = 0; i < slopes.length; i++)
        {
            if (!slopes[i].equals(t.slopes[i]))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hc = 1;
        for (int i = 0; i < criticalPoints.length; i++)
        {
            hc = 7 * hc + criticalPoints[i].hashCode();
        }
        for (int i = 0; i < slopes.length; i++)
        {
            hc = 7 * hc + slopes[i].hashCode();
        }
        hc = 7 * hc + xIntercepts[0].hashCode();
        return hc;
    }

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("Trajectory(");
        output.appendOutput(toOutput2());
        output.appendMath(")");
        return output;
    }
    
    public StyledTextOutput toOutput2()
    {
        StyledTextOutput output = getMastValue().toOutput();
        output.appendMath(",[");
        for (int i = 0; i < getNumCriticalPoints(); i++)
        {
            output.appendOutput(getCriticalPoint(i).toOutput());
            if (i < getNumCriticalPoints() - 1)
            {
                output.appendMath(",");
            }
        }
        output.appendMath("],[");
        for (int i = 0; i <= getNumCriticalPoints(); i++)
        {
            output.appendOutput(getSlope(i).toOutput());
            if (i < getNumCriticalPoints())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("]");
        return output;
    }

    /**
     * Gets the number of critical points on this trajectory.  This is equal to
     * the number of times the trajectory changes slope.
     *
     * @return  The number of critical points on this trajectory.
     */
    public int getNumCriticalPoints()
    {
        return criticalPoints.length;
    }

    /**
     * Gets the critical point at the specified index.  The critical points are
     * indexed highest-first, so <code>getCriticalPoint(0)</code> returns the
     * largest critical point of this trajectory.
     *
     * @param   index The index of the critical point.
     * @return  The critical point at the specified index.
     */
    public RationalNumber getCriticalPoint(int index)
    {
        return criticalPoints[index];
    }

    /**
     * Gets the slope at the specified index.  If
     * <code>r = getCriticalPoint(i)</code>, then <code>getSlope(i)</code> is
     * the slope of the trajectory immediately <i>above</i> <code>r</code>,
     * while <code>getSlope(i+1)</code> is the slope of the trajectory
     * immediately <i>below</i> <code>r</code>.  Thus
     * <code>getSlope(getNumCriticalPoints())</code> returns the
     * <i>trailing slope</i> of this trajectory.
     *
     * @param   index The index of the slope.
     * @return  The slope at the specified index.
     */
    public RationalNumber getSlope(int index)
    {
        return slopes[index];
    }

    /**
     * Gets all the critical points on this trajectory as an unmodifiable
     * <code>List</code>.
     *
     * @return  An unmodifiable <code>List</code> of all the critical points
     *          on this trajectory.
     */
    public List<RationalNumber> getAllCriticalPoints()
    {
        return Collections.unmodifiableList(Arrays.asList(criticalPoints));
    }

    /**
     * Gets the mast value of this trajectory.  This is equal to the value
     * of the trajectory at the highest critical point.  In particular, if
     * the mast is vertical, this is equal to the value of the mast.
     *
     * @return  The mast value of this trajectory.
     */
    public RationalNumber getMastValue()
    {
        if (criticalPoints.length == 0)
        {
            return xIntercepts[0];
        }
        else
        {
            return valueAt(criticalPoints[0]);
        }
    }

    /**
     * Gets the x-intercept of this trajectory's mast.
     *
     * @return  The x-intercept of this trajectory's mast.
     */
    public RationalNumber getMastXIntercept()
    {
        return xIntercepts[0];
    }

    /**
     * Gets the value of this trajectory at the specified point.
     *
     * @param   r The point at which to evaluate this trajectory.
     * @return  The value of this trajectory at <code>r</code>.
     */
    public RationalNumber valueAt(RationalNumber r)
    {
        int i;
        for (i = 0; i < criticalPoints.length && r.compareTo(criticalPoints[i]) < 0; i++);
        if (r.isInfinite() && slopes[i].equals(RationalNumber.ZERO))
        {
            return xIntercepts[i];
        }
        else
        {
            return r.multiply(slopes[i]).add(xIntercepts[i]);
        }
    }

    /**
     * Gets the slope of this trajectory immediately above the specified point.
     * This is equal to the slope of the trajectory <i>at</i> <code>r</code>
     * unless <code>r</code> is a critical point (in which case the slope is
     * undefined at <code>r</code>).
     *
     * @param   r The point above which to evaluate the slope of this
     *          trajectory.
     * @return  The slope of this trajectory immediately above <code>r</code>.
     */
    public RationalNumber slopeAbove(RationalNumber r)
    {
        int i;
        for (i = 0; i < criticalPoints.length && r.compareTo(criticalPoints[i]) < 0; i++);
        return slopes[i];
    }

    RationalNumber interceptAbove(RationalNumber r)
    {
        int i;
        for (i = 0; i < criticalPoints.length && r.compareTo(criticalPoints[i]) < 0; i++);
        return xIntercepts[i];
    }

    /**
     * Gets the slope of this trajectory immediately below the specified point.
     * This is equal to the slope of the trajectory <i>at</i> <code>r</code>
     * unless <code>r</code> is a critical point (in which case the slope is
     * undefined at <code>r</code>).
     *
     * @param   r The point below which to evaluate the slope of this
     *          trajectory.
     * @return  The slope of this trajectory immediately below <code>r</code>.
     */
    public RationalNumber slopeBelow(RationalNumber r)
    {
        int i;
        for (i = 0; i < criticalPoints.length && r.compareTo(criticalPoints[i]) <= 0; i++);
        return slopes[i];
    }

    RationalNumber interceptBelow(RationalNumber r)
    {
        int i;
        for (i = 0; i < criticalPoints.length && r.compareTo(criticalPoints[i]) <= 0; i++);
        return xIntercepts[i];
    }

    /**
     * Returns <code>true</code> if this trajectory is strictly positive.
     * That is, returns <code>true</code> if <code>valueAt(t) > 0</code> for all
     * <code>t</code>.
     *
     * @return  <code>true</code> if this trajectory is strictly positive.
     * @since   0.7
     */
    public boolean isStrictlyPositive()
    {
        if (valueAt(RationalNumber.NEGATIVE_ONE).compareTo(RationalNumber.ZERO) <= 0 ||
            valueAt(RationalNumber.POSITIVE_INFINITY).compareTo(RationalNumber.ZERO) <= 0)
        {
            return false;
        }
        for (RationalNumber cp : criticalPoints)
        {
            if (valueAt(cp).compareTo(RationalNumber.ZERO) <= 0)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether this trajectory is less than or equal to <code>t</code>.
     * If <code>a(x)</code> denotes the value of this trajectory
     * at <code>x</code>, and <code>b(x)</code> denotes the value of
     * <code>t</code> at <code>x</code>, then this method returns
     * <code>true</code> just if <code>a(x) &lt;= b(x)</code> for all
     * <code>x</code> with <code>-1 &lt;= x &lt; Infinity</code>.
     *
     * @param   t The trajectory to compare this one to.
     * @return  <code>true</code> if this trajectory is less than or equal to
     *          <code>t</code>.
     */
    public boolean leq(Trajectory t)
    {
        if (isInfinite() || t.isInfinite())
        {
            return xIntercepts[0].compareTo(t.xIntercepts[0]) <= 0;
        }

        // Check at -1 and infinity
        if (   slopes[0].compareTo(t.slopes[0]) > 0
            || slopes[0].equals(t.slopes[0]) && xIntercepts[0].compareTo(t.xIntercepts[0]) > 0
            || valueAt(RationalNumber.NEGATIVE_ONE).compareTo(t.valueAt(RationalNumber.NEGATIVE_ONE)) > 0)
        {
            return false;
        }
        // Check all the critical points.
        for (int i = 0; i < criticalPoints.length; i++)
        {
            if (valueAt(criticalPoints[i]).compareTo(t.valueAt(criticalPoints[i])) > 0)
            {
                return false;
            }
        }
        for (int i = 0; i < t.criticalPoints.length; i++)
        {
            if (valueAt(t.criticalPoints[i]).compareTo(t.valueAt(t.criticalPoints[i])) > 0)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the pointwise minimum of this trajectory and <code>t</code>.
     *
     * @param   t The value with which the minimum is to be computed.
     * @return  The pointwise minimum of this trajectory and <code>t</code>.
     */
    public Trajectory min(Trajectory t)
    {
        return minmax(t, false);
    }

    /**
     * Calculates the pointwise maximum of this trajectory and <code>t</code>.
     *
     * @param   t The value with which the maximum is to be computed.
     * @return  The pointwise maximum of this trajectory and <code>t</code>.
     */
    public Trajectory max(Trajectory t)
    {
        return minmax(t, true);
    }

    private Trajectory minmax(Trajectory t, boolean max)
    {
        int maxMultiplier = max ? -1 : 1;

        // We scan down through the critical points.  We keep track of which
        // trajectory was dominant at the previous critical point:
        // <0 = this, 0 = both (equal), >0 = t.
        int nextCPthis = 0, nextCPt = 0;
        int dominantAtPrevCP = 0;
        List<RationalNumber> newCriticalPoints = new ArrayList<RationalNumber>();
        List<RationalNumber> newSlopes = new ArrayList<RationalNumber>();
        List<RationalNumber> newXIntercepts = new ArrayList<RationalNumber>();
        // First handle the masts.  We set dominantAtPrevCP to equal the
        // trajectory that dominates at infinity.  This is the one with the
        // lower mast slope (for min); if the mast slopes are equal, it's the one
        // with the lower mast x-intercept.
        // Note that if either mast is infinite, then we consider only the
        // x-intercepts, *not* the slopes.
        if (!isInfinite() && !t.isInfinite())
        {
            dominantAtPrevCP = maxMultiplier * slopes[0].compareTo(t.slopes[0]);
        }
        if (dominantAtPrevCP == 0)
        {
            dominantAtPrevCP = maxMultiplier * xIntercepts[0].compareTo(t.xIntercepts[0]);
        }

        while (true)
        {
            // Determine the next critical point
            int currentCPowner;
            RationalNumber currentCP;
            if (nextCPthis == criticalPoints.length && nextCPt == t.criticalPoints.length)
            {
                // We've reached the end of the "real" critical points.  Now we
                // need to consider -1 as an "artificial" critical point in case
                // there was a tail-end crossover.
                currentCPowner = 0;
                currentCP = RationalNumber.NEGATIVE_ONE;
            }
            else
            {
                if (nextCPthis == criticalPoints.length) currentCPowner = 1;
                else if (nextCPt == t.criticalPoints.length) currentCPowner = -1;
                else currentCPowner = t.criticalPoints[nextCPt].compareTo(criticalPoints[nextCPthis]);
                currentCP = (currentCPowner <= 0 ? criticalPoints[nextCPthis] : t.criticalPoints[nextCPt]);
            }

            int dominantAtCurrentCP = maxMultiplier * valueAt(currentCP).compareTo(t.valueAt(currentCP));
            if (dominantAtCurrentCP < 0 && dominantAtPrevCP > 0 ||
                dominantAtCurrentCP > 0 && dominantAtPrevCP < 0)
            {
                // The dominant trajectory has changed.  This means there
                // must have been a crossover since the last critical point.
                // The crossover occurs at the intersection of the two line
                // segments above this critical point.
                RationalNumber crossoverPoint = t.xIntercepts[nextCPt].subtract(xIntercepts[nextCPthis]).divide
                    (slopes[nextCPthis].subtract(t.slopes[nextCPt]));
                newCriticalPoints.add(crossoverPoint);
                newSlopes.add(dominantAtPrevCP < 0 ? slopes[nextCPthis] : t.slopes[nextCPt]);
                newXIntercepts.add(dominantAtPrevCP < 0 ? xIntercepts[nextCPthis] : t.xIntercepts[nextCPt]);
            }

            if (currentCP.equals(RationalNumber.NEGATIVE_ONE))
            {
                break;
            }

            // Now we need to determine whether currentCP is a critical point
            // of the new trajectory.  There are several ways this can happen:

            if (dominantAtCurrentCP < 0 && currentCPowner <= 0)
            {
                // This trajectory is dominant at currentCP and its slope
                // changes there.
                newCriticalPoints.add(currentCP);
                newSlopes.add(slopes[nextCPthis]);
                newXIntercepts.add(xIntercepts[nextCPthis]);
            }
            else if (dominantAtCurrentCP > 0 && currentCPowner >= 0)
            {
                // t is dominant at currentCP and its slope changes there.
                newCriticalPoints.add(currentCP);
                newSlopes.add(t.slopes[nextCPt]);
                newXIntercepts.add(t.xIntercepts[nextCPt]);
            }
            else if (dominantAtCurrentCP == 0)
            {
                // The trajectories meet at currentCP.  In this case we check
                // which *slope* dominates above and below currentCP, and add
                // currentCP if they differ.
                // If we're finding the min, then the dominant slope is the
                // *smaller* slope above, *larger* slope below.
                int dominantSlopeAboveCurrentCP =
                    maxMultiplier * slopes[nextCPthis].compareTo(t.slopes[nextCPt]);
                RationalNumber slopeAboveCurrentCP =
                    dominantSlopeAboveCurrentCP < 0 ? slopes[nextCPthis] : t.slopes[nextCPt];
                RationalNumber thisSlopeBelowCurrentCP =
                    (currentCPowner <= 0 ? slopes[nextCPthis+1] : slopes[nextCPthis]);
                RationalNumber tSlopeBelowCurrentCP =
                    (currentCPowner >= 0 ? t.slopes[nextCPt+1] : t.slopes[nextCPt]);
                RationalNumber slopeBelowCurrentCP =
                    max ? thisSlopeBelowCurrentCP.min(tSlopeBelowCurrentCP)
                        : thisSlopeBelowCurrentCP.max(tSlopeBelowCurrentCP);
                if (!slopeAboveCurrentCP.equals(slopeBelowCurrentCP))
                {
                    newCriticalPoints.add(currentCP);
                    newSlopes.add(slopeAboveCurrentCP);
                    newXIntercepts.add(dominantSlopeAboveCurrentCP < 0 ? xIntercepts[nextCPthis] : t.xIntercepts[nextCPt]);
                }
            }

            if (currentCPowner <= 0)
            {
                nextCPthis++;
            }
            if (currentCPowner >= 0)
            {
                nextCPt++;
            }
            dominantAtPrevCP = dominantAtCurrentCP;
        }

        // For the final slope / x-intercept, we use whichever dominates at -1.  If they're
        // equal at -1, then it's the one whose slope dominates just *above* -1 (the one with
        // the lower final slope in the case of min).
        int dominantAtTail = maxMultiplier * valueAt(RationalNumber.NEGATIVE_ONE).compareTo(t.valueAt(RationalNumber.NEGATIVE_ONE));
        if (dominantAtTail == 0)
        {
            dominantAtTail = maxMultiplier * slopes[slopes.length-1].compareTo(t.slopes[t.slopes.length-1]);
        }
        newSlopes.add(dominantAtTail < 0 ? slopes[slopes.length-1] : t.slopes[t.slopes.length-1]);
        newXIntercepts.add(dominantAtTail < 0 ? xIntercepts[slopes.length-1] : t.xIntercepts[t.slopes.length-1]);

        Trajectory result = new Trajectory();
        result.criticalPoints = newCriticalPoints.toArray(new RationalNumber[newCriticalPoints.size()]);
        result.slopes = newSlopes.toArray(new RationalNumber[newSlopes.size()]);
        result.xIntercepts = newXIntercepts.toArray(new RationalNumber[newXIntercepts.size()]);
        return result;
    }

    /**
     * Returns <code>true</code> if this trajectory is infinite.
     *
     * @return  <code>true</code> if this trajectory is infinite.
     */
    public boolean isInfinite()
    {
        return xIntercepts[0].isInfinite();
    }

    /**
     * Tilts this trajectory by <code>r</code>.  If this trajectory
     * has value <code>a(x)</code> at <code>x</code>, then the tilted
     * trajectory has value <code>a(x) + rx</code>.
     *
     * @param   r The factor by which to tilt this trajectory.
     * @return  This trajectory tilted by <code>r</code>.
     */
    public Trajectory tilt(RationalNumber r)
    {
        if (isInfinite())
        {
            return this;
        }
        Trajectory result = new Trajectory();
        result.criticalPoints = criticalPoints;
        result.slopes = new RationalNumber[slopes.length];
        result.xIntercepts = xIntercepts;
        for (int i = 0; i < slopes.length; i++)
        {
            result.slopes[i] = slopes[i].add(r);
        }
        return result;
    }

    /**
     * Calculates the sum of this trajectory and <code>t</code>.
     *
     * @param   t The trajectory to add to this one.
     * @return  The sum of this trajectory and <code>t</code>.
     * @since   0.7
     */
    public Trajectory add(Trajectory t)
    {
        if (isInfinite() || t.isInfinite())
        {
            return new Trajectory(getMastXIntercept().add(t.getMastXIntercept()));
        }

        List<RationalNumber> newCriticalPoints = new ArrayList<RationalNumber>(),
                       newSlopes = new ArrayList<RationalNumber>(),
                       newXIntercepts = new ArrayList<RationalNumber>();
        int nextCPt = 0, nextCPthis = 0;

        newSlopes.add(slopes[0].add(t.slopes[0]));
        newXIntercepts.add(xIntercepts[0].add(t.xIntercepts[0]));

        while (nextCPthis < criticalPoints.length || nextCPt < t.criticalPoints.length)
        {
            RationalNumber cp;
            if (nextCPthis < criticalPoints.length &&
                (nextCPt == t.criticalPoints.length || criticalPoints[nextCPthis].compareTo(t.criticalPoints[nextCPt]) >= 0))
            {
                cp = criticalPoints[nextCPthis];
            }
            else
            {
                cp = t.criticalPoints[nextCPt];
            }
            extendTrajectory(
                false,
                newCriticalPoints,
                newSlopes,
                newXIntercepts,
                cp,
                slopeBelow(cp).add(t.slopeBelow(cp)),
                interceptBelow(cp).add(t.interceptBelow(cp))
                );
            if (nextCPthis < criticalPoints.length && cp.equals(criticalPoints[nextCPthis]))
            {
                nextCPthis++;
            }
            if (nextCPt < t.criticalPoints.length && cp.equals(t.criticalPoints[nextCPt]))
            {
                nextCPt++;
            }
        }

        Trajectory sum = new Trajectory();
        sum.criticalPoints = newCriticalPoints.toArray(new RationalNumber[newCriticalPoints.size()]);
        sum.slopes = newSlopes.toArray(new RationalNumber[newSlopes.size()]);
        sum.xIntercepts = newXIntercepts.toArray(new RationalNumber[newXIntercepts.size()]);
        assert sum.validate() : toString() + "\n" + t + "\n" + sum;
        return sum;
    }

    /**
     * Calculates the difference of this trajectory and <code>t</code>.
     *
     * @param   t The trajectory to subtract from this one.
     * @return  The difference of this trajectory and <code>t</code>.
     * @since   0.7
     */
    public Trajectory subtract(Trajectory t)
    {
        return add(t.negate());
    }

    /**
     * Calculates the inverse of this trajectory.
     *
     * @return  The inverse of this trajectory.
     * @since   0.7
     */
    public Trajectory negate()
    {
        Trajectory inverse = new Trajectory();
        inverse.criticalPoints = criticalPoints;
        inverse.slopes = negate(slopes);
        inverse.xIntercepts = negate(xIntercepts);
        assert inverse.validate();
        return inverse;
    }

    private static RationalNumber[] negate(RationalNumber[] array)
    {
        RationalNumber[] inverse = new RationalNumber[array.length];
        for (int i = 0; i < inverse.length; i++)
        {
            inverse[i] = array[i].negate();
        }
        return inverse;
    }

    /**
     * Translates this trajectory by <code>r</code>.  If this trajectory
     * has value <code>a(x)</code> at <code>x</code>, then the translated
     * trajectory has value <code>a(x) + r</code>.
     *
     * @param   r The factor by which to translate this trajectory.
     * @return  This trajectory translated by <code>r</code>.
     * @since   0.7
     */
    public Trajectory translate(RationalNumber r)
    {
        if (isInfinite() || r.isInfinite())
        {
            return new Trajectory(getMastXIntercept().add(r));
        }
        Trajectory translation = new Trajectory();
        translation.slopes = slopes;
        translation.criticalPoints = criticalPoints;
        translation.xIntercepts = new RationalNumber[xIntercepts.length];
        for (int i = 0; i < xIntercepts.length; i++)
        {
            translation.xIntercepts[i] = xIntercepts[i].add(r);
        }
        return translation;
    }

    /**
     * Calcuates the first point of intersection between this trajectory and
     * <code>t</code> below the upper bound <code>theta</code>.  The result
     * is the largest <code>x <= theta</code> such that
     * <code>this(x) == t(x)</code>, or <code>null</code> if none exists.
     *
     * @param   t The trajectory to intersect with this one.
     * @param   theta The upper bound for the search.
     * @return  The largest <code>x <= theta</code> such that
     *          <code>this(x) == t(x)</code>.
     * @since   0.7
     */
    public RationalNumber firstIntersectionBelow(Trajectory t, RationalNumber theta)
    {
        int nextCPthis = 0, nextCPt = 0;
        int dominantTrajectory;
        if (theta.equals(RationalNumber.POSITIVE_INFINITY))
        {
            // Determine which trajectory is bigger at infinity.
            // dominantTrajectory = -1 if this is bigger, 1 if other is bigger.
            dominantTrajectory = slopes[0].compareTo(t.slopes[0]);
            if (dominantTrajectory == 0)
            {
                dominantTrajectory = xIntercepts[0].compareTo(t.xIntercepts[0]);
            }
            if (dominantTrajectory == 0)
            {
                // They coincide.
                return RationalNumber.POSITIVE_INFINITY;
            }
        }
        else
        {
            dominantTrajectory = valueAt(theta).compareTo(t.valueAt(theta));
            if (dominantTrajectory == 0)
            {
                return theta;
            }
            while (nextCPthis < criticalPoints.length && criticalPoints[nextCPthis].compareTo(theta) >= 0)
            {
                nextCPthis++;
            }
            while (nextCPt < t.criticalPoints.length && t.criticalPoints[nextCPt].compareTo(theta) >= 0)
            {
                nextCPt++;
            }
        }

        while (nextCPthis <= criticalPoints.length && nextCPt <= t.criticalPoints.length)
        {
            RationalNumber valThis = valueAt
                (nextCPthis == criticalPoints.length ? RationalNumber.NEGATIVE_ONE : criticalPoints[nextCPthis]);
            RationalNumber valOther = t.valueAt
                (nextCPt == t.criticalPoints.length ? RationalNumber.NEGATIVE_ONE : t.criticalPoints[nextCPt]);
            int newDominantTrajectory = valThis.compareTo(valOther);
            if (newDominantTrajectory != dominantTrajectory)
            {
                // There's a crossover.  Find it.
                return t.xIntercepts[nextCPt].subtract(xIntercepts[nextCPthis]).divide
                    (slopes[nextCPthis].subtract(t.slopes[nextCPt]));
            }
            // Increment the CP-pointers.
            if (nextCPthis == criticalPoints.length)
            {
                nextCPt++;
            }
            else if (nextCPt == t.criticalPoints.length)
            {
                nextCPthis++;
            }
            else if (criticalPoints[nextCPthis].compareTo(t.criticalPoints[nextCPt]) >= 0)
            {
                nextCPthis++;
            }
            else
            {
                nextCPt++;
            }
        }
        return RationalNumber.NEGATIVE_INFINITY;
    }

    /**
     * Pivots this trajectory at <code>t</code>.  The new trajectory
     * <code>p</code> will satisfy the following constraints:
     * <ul>
     * <li><code>p(x) == this(x)</code> for all <code>x >= t</code>;
     * <li><code>p</code> has slope <code>newSlope</code> at all
     * points below <code>t</code>.
     * </ul>
     *
     * @param   t The temperature at which to pivot this trajectory.
     * @param   newSlope The slope of the new trajectory below
     *          the pivot point.
     * @return  This trajectory pivoted at <code>t</code>.
     * @since   0.7
     */
    public Trajectory pivot(RationalNumber t, RationalNumber newSlope)
    {
        if (t.compareTo(RationalNumber.NEGATIVE_ONE) < 0)
        {
            throw new IllegalArgumentException("t");
        }

        if (slopeBelow(t).equals(newSlope))
        {
            return this;
        }

        int firstCPAtOrBelowT = criticalPoints.length;
        for (int i = 0; i < criticalPoints.length; i++)
        {
            if (criticalPoints[i].compareTo(t) <= 0)
            {
                firstCPAtOrBelowT = i;
                break;
            }
        }

        Trajectory newTrajectory = new Trajectory();
        newTrajectory.criticalPoints = new RationalNumber[firstCPAtOrBelowT + 1];
        newTrajectory.slopes = new RationalNumber[firstCPAtOrBelowT + 2];
        newTrajectory.xIntercepts = new RationalNumber[firstCPAtOrBelowT + 2];
        System.arraycopy(criticalPoints, 0, newTrajectory.criticalPoints, 0, firstCPAtOrBelowT);
        newTrajectory.criticalPoints[firstCPAtOrBelowT] = t;
        System.arraycopy(slopes, 0, newTrajectory.slopes, 0, firstCPAtOrBelowT + 1);
        newTrajectory.slopes[firstCPAtOrBelowT+1] = newSlope;
        System.arraycopy(xIntercepts, 0, newTrajectory.xIntercepts, 0, firstCPAtOrBelowT + 1);
        newTrajectory.xIntercepts[firstCPAtOrBelowT+1] = valueAt(t).subtract(newSlope.multiply(t));
        assert newTrajectory.validate();
        return newTrajectory;
    }

    private boolean validate()
    {
        for (int i = 0; i < criticalPoints.length; i++)
        {
            if (!criticalPoints[i].multiply(slopes[i]).add(xIntercepts[i]).equals
                (criticalPoints[i].multiply(slopes[i+1]).add(xIntercepts[i+1])))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this trajectory to <code>other</code> at the specified
     * temperature.  This is identical to
     * <p>
     * <code>valueAt(t).compareTo(other.valueAt(t))</code>
     * <p>
     * <i>except</i> when <code>t</code> is infinite.  In that case, the
     * trajectories are compared at infinity.
     *
     * @param   other The trajectory to compare this to.
     * @param   t The temperature at which to compare the trajectories.
     * @return  Negative if <code>this(t) < other(t)</code>,
     *          <code>0</code> if <code>this(t) = other(t)</code>,
     *          positive if <code>this(t) > other(t)</code>
     */
    public int compareToAt(Trajectory other, RationalNumber t)
    {
        if (t.compareTo(RationalNumber.NEGATIVE_ONE) < 0)
        {
            throw new IllegalArgumentException("t < -1");
        }
        if (t.equals(RationalNumber.POSITIVE_INFINITY))
        {
            return slopes[0].equals(other.slopes[0])
                ? xIntercepts[0].compareTo(other.xIntercepts[0])
                : slopes[0].compareTo(other.slopes[0]);
        }
        else
        {
            return valueAt(t).compareTo(other.valueAt(t));
        }
    }

    /**
     * Calculates the thermographic intersection of this trajectory with the
     * specified right scaffold.  Thermographic intersections are defined
     * in <i>Winning Ways</i> for trajectories with slopes of
     * <code>-1</code>, <code>0</code> and <code>1</code>.  This method also
     * supports generalized thermographic intersections, as defined by
     * Berlekamp in &quot;The Economist's View of Combinatorial Games.&quot;
     *
     * @param   rightScaffold The trajectory of the thermograph's right
     *          scaffold.
     * @return  The resulting <code>Thermograph</code>.
     */
    public Thermograph thermographicIntersection(Trajectory rightScaffold)
    {
        if (equals(POSITIVE_INFINITY) || rightScaffold.equals(NEGATIVE_INFINITY))
        {
            return new Thermograph(this, rightScaffold);
        }

        List<RationalNumber>
            leftWallCPs = new ArrayList<RationalNumber>(),
            leftWallSlopes = new ArrayList<RationalNumber>(),
            leftWallXIntercepts = new ArrayList<RationalNumber>(),
            rightWallCPs = new ArrayList<RationalNumber>(),
            rightWallSlopes = new ArrayList<RationalNumber>(),
            rightWallXIntercepts = new ArrayList<RationalNumber>();

        RationalNumber lsAtBase = valueAt(RationalNumber.NEGATIVE_ONE),
                 rsAtBase = rightScaffold.valueAt(RationalNumber.NEGATIVE_ONE);
        int valueCmp = lsAtBase.compareTo(rsAtBase);
        RationalNumber previousCaveValue;
        if (valueCmp < 0 ||
            valueCmp == 0 && slopes[slopes.length-1].compareTo(rightScaffold.slopes[rightScaffold.slopes.length-1]) < 0)
        {
            // The left scaffold is smaller than the right scaffold immediately
            // above the base.  So we start in a cave region.
            // The value of this cave region is 0 if 0 lies between the left
            // and right scaffolds at the base.  Otherwise it's the value of
            // the scaffold that lies *closer* to 0 at the base.
            if (lsAtBase.compareTo(RationalNumber.ZERO) > 0)
            {
                previousCaveValue = lsAtBase;
            }
            else if (rsAtBase.compareTo(RationalNumber.ZERO) < 0)
            {
                previousCaveValue = rsAtBase;
            }
            else
            {
                previousCaveValue = RationalNumber.ZERO;
            }
        }
        else
        {
            // We start in a hill region.
            previousCaveValue = null;
        }

        // We work bottom-up and reverse the lists at the end.
        int nextCPleft = criticalPoints.length - 1;
        int nextCPright = rightScaffold.criticalPoints.length - 1;
        while (nextCPleft >= -1 || nextCPright >= -1)
        {
            // <0 for left, 0 for both, >0 for Right
            int currentCPowner;
            RationalNumber currentCP;
            if (nextCPleft == -1 && nextCPright == -1)
            {
                // We've reached the end of the "real" critical points.  Now we
                // need to consider infinity as an "artificial" critical point.
                currentCPowner = 0;
                currentCP = RationalNumber.POSITIVE_INFINITY;
            }
            else
            {
                if (nextCPleft == -1) currentCPowner = 1;
                else if (nextCPright == -1) currentCPowner = -1;
                else currentCPowner = criticalPoints[nextCPleft].compareTo(rightScaffold.criticalPoints[nextCPright]);
                currentCP = (currentCPowner <= 0 ? criticalPoints[nextCPleft] : rightScaffold.criticalPoints[nextCPright]);
            }

            boolean nowInHillRegion = (compareToAt(rightScaffold, currentCP) >= 0);
            if (previousCaveValue == null && !nowInHillRegion)
            {
                // We were previously in a hill region, but just entered a cave region.
                // Extend the hill to the crossover point.
                RationalNumber crossoverPoint = intersectionPoint(
                    slopes[nextCPleft+1], xIntercepts[nextCPleft+1],
                    rightScaffold.slopes[nextCPright+1], rightScaffold.xIntercepts[nextCPright+1]
                    );
                assert valueAt(crossoverPoint).equals(rightScaffold.valueAt(crossoverPoint))
                    : "(" + nextCPleft + "/" + nextCPright + ") " + crossoverPoint + "\n" + this + "\n" + rightScaffold;
                extendTrajectory(
                    leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                    crossoverPoint, slopes[nextCPleft+1], xIntercepts[nextCPleft+1]
                    );
                extendTrajectory(
                    rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                    crossoverPoint, rightScaffold.slopes[nextCPright+1], rightScaffold.xIntercepts[nextCPright+1]
                    );
                // Now add the cave mast.
                RationalNumber caveMastSlope, caveMastIntercept;
                if (valueAt(currentCP).compareTo(valueAt(crossoverPoint)) > 0)
                {
                    // The left scaffold moves to the left above the crossover point.
                    // The cave mast follows the left scaffold.
                    caveMastSlope = slopes[nextCPleft+1];
                    caveMastIntercept = xIntercepts[nextCPleft+1];
                    previousCaveValue = valueAt(currentCP);
                }
                else if (rightScaffold.valueAt(currentCP).compareTo(rightScaffold.valueAt(crossoverPoint)) < 0)
                {
                    // The right scaffold moves to the right above the crossover point.
                    // The cave mast follows the right scaffold.
                    caveMastSlope = rightScaffold.slopes[nextCPright+1];
                    caveMastIntercept = rightScaffold.xIntercepts[nextCPright+1];
                    previousCaveValue = rightScaffold.valueAt(currentCP);
                }
                else
                {
                    // Neither of the above.
                    // The cave mast extends vertically above the crossover point.
                    caveMastSlope = RationalNumber.ZERO;
                    previousCaveValue = caveMastIntercept = valueAt(crossoverPoint);
                }
                // Extend the trajectories according to the cave mast/intercept.
                extendTrajectory(
                    leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                    currentCP, caveMastSlope, caveMastIntercept
                    );
                extendTrajectory(
                    rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                    currentCP, caveMastSlope, caveMastIntercept
                    );
            }
            else if (previousCaveValue != null)
            {
                // We were previously in a cave region.  There are three cases:
                // (i)   The left scaffold moves to the left of the previous cave value,
                // (ii)  The right scaffold moves to the right of the previous cave value,
                // (iii) The previous cave value remains between the left and right
                //       scaffolds.
                // If both scaffolds move past the previous cave value, then we favor
                // case (i) or (ii) depending on which happens *first*.

                // First determine which crossing points exist and find their values.
                RationalNumber leftScaffoldCrossingPoint =
                    valueAt(currentCP).compareTo(previousCaveValue) > 0
                    ? previousCaveValue.subtract(xIntercepts[nextCPleft+1]).divide(slopes[nextCPleft+1])
                    : null;
                RationalNumber rightScaffoldCrossingPoint =
                    rightScaffold.valueAt(currentCP).compareTo(previousCaveValue) < 0
                    ? previousCaveValue.subtract(rightScaffold.xIntercepts[nextCPright+1])
                        .divide(rightScaffold.slopes[nextCPright+1])
                    : null;

                if (leftScaffoldCrossingPoint != null && (
                         rightScaffoldCrossingPoint == null ||
                         leftScaffoldCrossingPoint.compareTo(rightScaffoldCrossingPoint) <= 0))
                {
                    // We are in case (i).  First add the truncated vertical mast.
                    extendTrajectory(
                        leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                        leftScaffoldCrossingPoint, RationalNumber.ZERO, previousCaveValue
                        );
                    extendTrajectory(
                        rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                        leftScaffoldCrossingPoint, RationalNumber.ZERO, previousCaveValue
                        );
                    // Now add the tilted mast for the left wall.  (The left
                    // wall follows the left scaffold up to currentCP even if
                    // the scaffolds enter a hill region.)
                    extendTrajectory(
                        leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                        currentCP, slopes[nextCPleft+1], xIntercepts[nextCPleft+1]
                        );
                    // To handle the right wall we need to know whether we've
                    // re-entered a hill region or not.
                    RationalNumber newRightCP;
                    if (nowInHillRegion)
                    {
                        // A hill region is indeed re-entered.  So the tilted
                        // mast for Right extends just up to the scaffolds'
                        // next point of intersection.
                        newRightCP = intersectionPoint(
                            slopes[nextCPleft+1], xIntercepts[nextCPleft+1],
                            rightScaffold.slopes[nextCPright+1], rightScaffold.xIntercepts[nextCPright+1]
                            );
                    }
                    else
                    {
                        // We stay in a cave region.  So the tilted mast for
                        // Right extends all the way up to currentCP.
                        newRightCP = currentCP;
                        previousCaveValue = valueAt(currentCP);
                    }
                    // Extend the right trajectory.
                    extendTrajectory(
                        rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                        newRightCP, slopes[nextCPleft+1], xIntercepts[nextCPleft+1]
                        );
                }
                else if (rightScaffoldCrossingPoint != null)
                {
                    // We are in case (ii).  First add the truncated vertical mast.
                    extendTrajectory(
                        leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                        rightScaffoldCrossingPoint, RationalNumber.ZERO, previousCaveValue
                        );
                    extendTrajectory(
                        rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                        rightScaffoldCrossingPoint, RationalNumber.ZERO, previousCaveValue
                        );
                    // Now add the tilted mast for the right wall.  (The right
                    // wall follows the right scaffold up to currentCP even if
                    // the scaffolds enter a hill region.)
                    extendTrajectory(
                        rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                        currentCP, rightScaffold.slopes[nextCPright+1], rightScaffold.xIntercepts[nextCPright+1]
                        );
                    // To handle the left wall we need to know whether we've
                    // re-entered a hill region or not.
                    RationalNumber newLeftCP;
                    if (nowInHillRegion)
                    {
                        // A hill region is indeed re-entered.  So the tilted
                        // mast for Left extends just up to the scaffolds'
                        // next point of intersection.
                        newLeftCP = intersectionPoint(
                            slopes[nextCPleft+1], xIntercepts[nextCPleft+1],
                            rightScaffold.slopes[nextCPright+1], rightScaffold.xIntercepts[nextCPright+1]
                            );
                    }
                    else
                    {
                        // We stay in a cave region.  So the tilted mast for
                        // Right extends all the way up to currentCP.
                        newLeftCP = currentCP;
                        previousCaveValue = rightScaffold.valueAt(currentCP);
                    }
                    // Extend the left trajectory.
                    extendTrajectory(
                        leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                        newLeftCP, rightScaffold.slopes[nextCPright+1], rightScaffold.xIntercepts[nextCPright+1]
                        );
                }
                else
                {
                    // We are in case (iii).
                    extendTrajectory(
                        leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                        currentCP, RationalNumber.ZERO, previousCaveValue
                        );
                    extendTrajectory(
                        rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                        currentCP, RationalNumber.ZERO, previousCaveValue
                        );
                }
            }
            if (nowInHillRegion)
            {
                // We're in a hill region, so we need to add the critical
                // point(s) for the hill, regardless of what region we were in
                // previously.
                if (currentCPowner <= 0)
                {
                    extendTrajectory(
                        leftWallCPs, leftWallSlopes, leftWallXIntercepts,
                        currentCP, slopes[nextCPleft+1], xIntercepts[nextCPleft+1]
                        );
                }
                if (currentCPowner >= 0)
                {
                    extendTrajectory(
                        rightWallCPs, rightWallSlopes, rightWallXIntercepts,
                        currentCP, rightScaffold.slopes[nextCPright+1], rightScaffold.xIntercepts[nextCPright+1]
                        );
                }
                previousCaveValue = null;
            }
            if (currentCPowner <= 0)
            {
                nextCPleft--;
            }
            if (currentCPowner >= 0)
            {
                nextCPright--;
            }
        }
        // Now remove the "infinite" critical point from the end.
        leftWallCPs.remove(leftWallCPs.size()-1);
        rightWallCPs.remove(rightWallCPs.size()-1);

        Trajectory leftWall = new Trajectory(), rightWall = new Trajectory();
        leftWall.criticalPoints = reverse(leftWallCPs);
        leftWall.slopes = reverse(leftWallSlopes);
        leftWall.xIntercepts = reverse(leftWallXIntercepts);
        rightWall.criticalPoints = reverse(rightWallCPs);
        rightWall.slopes = reverse(rightWallSlopes);
        rightWall.xIntercepts = reverse(rightWallXIntercepts);

        assert rightWall.leq(leftWall) : "\n" + this + "\n" + rightScaffold + "\n" + leftWall + "\n" + rightWall;

        return new Thermograph(leftWall, rightWall);
    }

    private static void extendTrajectory
        (List<RationalNumber> cps, List<RationalNumber> slopes, List<RationalNumber> xIntercepts, RationalNumber newCP, RationalNumber newSlope, RationalNumber newXIntercept)
    {
        extendTrajectory(true, cps, slopes, xIntercepts, newCP, newSlope, newXIntercept);
    }

    private static void extendTrajectory
        (boolean upwards, List<RationalNumber> cps, List<RationalNumber> slopes, List<RationalNumber> xIntercepts, RationalNumber newCP, RationalNumber newSlope, RationalNumber newXIntercept)
    {
        if (newCP.equals(RationalNumber.NEGATIVE_ONE) ||
            cps.size() > 0 && newCP.equals(cps.get(cps.size()-1)))
        {
            return;
        }
        else if (slopes.size() > 0 && newSlope.equals(slopes.get(slopes.size()-1)))
        {
            // The x-intercept must also be the same (since the trajectory is
            // connected).  So just set the critical point higher.
            assert newXIntercept.equals(xIntercepts.get(slopes.size()-1));
            if (upwards)
            {
                cps.set(cps.size()-1, newCP);
            }
        }
        else
        {
            cps.add(newCP);
            slopes.add(newSlope);
            xIntercepts.add(newXIntercept);
        }
    }

    private RationalNumber[] reverse(List<RationalNumber> rationals)
    {
        int size = rationals.size();
        RationalNumber[] reversedList = new RationalNumber[size];
        for (int i = 0; i < size; i++)
        {
            reversedList[i] = rationals.get(size-i-1);
        }
        return reversedList;
    }

    private RationalNumber intersectionPoint
        (RationalNumber slope1, RationalNumber xIntercept1, RationalNumber slope2, RationalNumber xIntercept2)
    {
        return xIntercept2.subtract(xIntercept1).divide(slope1.subtract(slope2));
    }
}