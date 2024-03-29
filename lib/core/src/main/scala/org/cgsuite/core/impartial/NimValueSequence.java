/*
 * GrundySequence.java
 *
 * Created on May 17, 2006, 1:18 PM
 * $Id: GrundySequence.java,v 1.10 2008/01/15 20:36:06 haoyuep Exp $
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

package org.cgsuite.core.impartial;

import org.cgsuite.exception.CalculationCanceledException$;
import scala.None$;
import scala.Option;

import java.util.Arrays;
import java.util.BitSet;

/**
 * A sequence of Grundy values corresponding to a fixed <code>HeapRuleset</code>.
 *
 * @author  Michael Albert
 * @version $Revision: 1.10 $ $Date: 2008/01/15 20:36:06 $
 */
public class NimValueSequence
{
    private static final int UNDEFINED_VALUE = -1;
    private static final int DEFAULT_SIZE = 100;
    private static int nCalcs = 0;
    private HeapRuleset rules;
    private int[] nimValues;
    private int maxKnown;

    private NimValueSequence()
    {
    }

    /**
     * Constructs a <code>GrundySequence</code> for the specified <code>HeapRules</code>.
     *
     * @param rules The <code>HeapRules</code> for this sequence.
     */
    public NimValueSequence(HeapRuleset rules)
    {
        this(rules, DEFAULT_SIZE);
    }

    /**
     * Constructs a <code>GrundySequence</code> for the specified <code>HeapRules</code>,
     * with the specified initial capacity.
     *
     * @param rules The <code>HeapRules</code> for this sequence.
     * @param capacity The initial capacity.
     */
    public NimValueSequence(HeapRuleset rules, int capacity)
    {
        this.rules = rules;
        this.nimValues = new int[capacity];
        nimValues[0] = 0;
        Arrays.fill(nimValues, 1, nimValues.length, UNDEFINED_VALUE);
        maxKnown = 1;
    }

    /**
     * Ensures that Grundy values are calculated to the specified heap size.
     *
     * @param   heapSize The minimum heap size for the computation.
     */
    public void calculateNimValues(int heapSize)
    {
        if (heapSize < maxKnown) return;

        ensureCapacity(heapSize);

        BitSet mexSet = new BitSet();

        for(; maxKnown <= heapSize; ++maxKnown)
        {
            calculateNimValue(maxKnown,mexSet,null);
        }

    }

    private void calculateNimValue(int position, BitSet mexSet, BitSet pending)
    {
        BitSet recursiveMexSet = null;

        if (nCalcs++ >= 20)
        {
            if (Thread.interrupted())
            {
                throw CalculationCanceledException$.MODULE$.apply("Calculation canceled by user.", null, (Option) None$.MODULE$);
            }
            nCalcs=0;
        }
        mexSet.clear();
        Traversal t = rules.traversal(position);
        while (t.advance()) {
            int result = 0;
            for (int i = 0; i < t.currentLength(); i++) {
                int part = t.currentPart(i);
                int optVal
                    = part < position ? nimValues[part] : UNDEFINED_VALUE;

                if (optVal == UNDEFINED_VALUE)
                {
                    // Check for uncommon, but supported, case of option
                    // greater than original size.
                    ensureCapacity(part);
                    optVal = nimValues[part];
                    if (optVal == UNDEFINED_VALUE)
                    {
                        if (pending==null)
                        {
                            pending=new BitSet();
                            pending.set(position);
                        }

                        if (pending.get(part))
                        {
                            throw new RuntimeException("Those HeapRules specify a loopy game.");
                        }
                        if (recursiveMexSet == null)
                        {
                            recursiveMexSet = new BitSet();
                        }
                        pending.set(part);
                        calculateNimValue(part, recursiveMexSet, pending);
                        pending.clear(part);
                        optVal = nimValues[part];

                    }
                }

                result ^= optVal;
            }
            mexSet.set(result);
        }

        nimValues[position] = mexSet.nextClearBit(0);
    }

    private void ensureCapacity(int heapSize)
    {
        if (nimValues.length <= heapSize)
        {
            int[] newValues = Arrays.copyOfRange(nimValues, 0, 1 + (3*heapSize) / 2);
            Arrays.fill(newValues, nimValues.length, newValues.length, UNDEFINED_VALUE);
            nimValues = newValues;
        }
    }

    /**
     * Returns the Grundy value for the specified heap size.  This method
     * will call <code>populateTo(heapSize)</code> if it is not available.
     *
     * @param heapSize The heap size.
     * @return The Grundy value for the specified heap size.
     */
    public int nimValue(int heapSize)
    {
        calculateNimValues(heapSize);
        return nimValues[heapSize];
    }

    public int[] nimValues(int maxHeapSize)
    {
        calculateNimValues(maxHeapSize+1);
        return Arrays.copyOfRange(nimValues, 1, maxHeapSize+1);
    }

    /**
     * Gets the size of this sequence (the number of positive-sized
     * heaps that have been computed so far).
     *
     * @return  The number of heaps whose Grundy values are known.
     */
    public int size()
    {
        return maxKnown;
    }

    public Periodicity checkPeriodicity(int maxHeapSize)
    {
        calculateNimValues(maxHeapSize);
        Option<PeriodicityChecker> checker = rules.periodicityChecker();
        if (checker.isDefined())
        {
            return checker.get().checkSequence(nimValues, maxHeapSize);
        }
        else
        {
            return null;
        }
    }
}
