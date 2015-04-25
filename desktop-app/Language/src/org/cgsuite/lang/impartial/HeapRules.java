/*
 * HeapRules.java
 *
 * Created on April 5, 2006, 11:16 AM
 * $Id: HeapRules.java,v 1.5 2007/04/09 23:51:51 asiegel Exp $
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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.CgsuiteProcedure;

/**
 * A system of rules for a heap game.
 * <p>
 * Heap games are played with nonempty heaps of tokens.  A
 * move consists of adding or removing tokens from a single heap, and
 * optionally splitting the heap into several smaller heaps.  The
 * rules of the game dictate the number of tokens that can be removed,
 * as well as the circumstances under which a pile may be split.
 * <p>
 * A <code>HeapRules</code> encodes an arbitrary system of such rules.
 * The rules are realized as a mapping <code>f</code> from (non-negative)
 * integers to collections of tuples of (non-negative) integers.  For each
 * value of <code>n</code>, the tuples in <code>f(n)</code> represent the
 * allowable options from a heap of size <code>n</code>.
 * <p>
 * For example, in the rules for Nim, <code>f(n)</code> would consist
 * of all singleton tuples of integers strictly less than <code>n</code>.
 * In the rules for
 * Kayles, <code>f(n)</code> would consist of all pairs of integers
 * <code>(a,b)</code> with <code>n-2 &le; a+b &le; n-1</code>.
 * <p>
 * The minimal implementation of <code>HeapRules</code> requires only the
 * {@link #allOptions(int) allOptions} method, which returns the
 * requisite collection of tuples for each heap size.  When performance is
 * an issue, the {@link #traversal(int) traversal} method should also be
 * implemented, to permit enumeration of options with minimal memory
 * allocation.
 * <p>
 * Many heap games, such as octal and hexadecimal games, are best
 * represented by a sequence of code digits.  In such cases, the
 * {@link TBCode} convenience class can be used to
 * implement <code>HeapRules</code> with almost no effort.
 * <p>
 * The primary intent of <code>HeapRules</code> is to be used in conjunction
 * with a {@link HeapGame}.  However, it can just as easily be used in other
 * contexts as well - such as in a
 * {@link org.cgsuite.examples.PartizanHeapPosition} - without modification.
 * 
 * @author  Aaron Siegel
 * @version $Revision: 1.5 $ $Date: 2007/04/09 23:51:51 $
 */
public abstract class HeapRules extends CgsuiteObject
{
    public final static CgsuiteClass TYPE = CgsuitePackage.lookupPackage("game.heap").forceLookupClassInPackage("HeapRules");
    
    private NimValueSequence nimValues;
    
    /**
     * Constructs a new <code>HeapRules</code>.
     */
    protected HeapRules()
    {
        super(TYPE);
        nimValues = new NimValueSequence(this);
    }
    
    public static HeapRules parse(String str)
    {
        return new TBCode(str);
    }
    
    public static HeapRules custom(CgsuiteProcedure procedure)
    {
        return new CustomHeapRules(procedure);
    }
    
    /**
     * Constructs a <code>Collection</code> containing all options from a
     * heap of the specified size.  Each element of the collection is an
     * <code>int[]</code> listing the sizes of the resulting
     * piles.  If the memory allocated by creating this structure is a
     * problem, see {@link Traversal} for access to low-overhead
     * features.
     *
     * @param   heapSize The size of the heap.
     * @return  All options from a heap of the specified size.
     */
    public abstract Collection<int[]> allOptions(int heapSize);
    
    /**
     * Gets a <code>Traversal</code> that iterates over all options from a
     * heap of the specified size.
     * <p>
     * The default implementation calls {@link #allOptions(int) allOptions}
     * and returns a <code>Traversal</code> of the resulting
     * <code>Collection</code>.  This obviously offers no performance
     * advantage over a direct call to <code>allOptions</code>.  When
     * performance is an issue, <code>traversal</code> should be
     * overridden to return a custom <code>Traversal</code>.
     *
     * @param   heapSize The size of the heap.
     * @return  A <code>Traversal</code> for the specified heap size.
     */
    public Traversal traversal(int heapSize)
    {
        final Iterator<int[]> allOptions = allOptions(heapSize).iterator();
        
        return new Traversal()
        {
            private int[] current;
            
            @Override
            public boolean advance()
            {
                if (allOptions.hasNext())
                {
                    current = allOptions.next();
                    return true;
                }
                else
                {
                    return false;
                }
            }
            
            @Override
            public int currentLength()
            {
                return current.length;
            }
            
            @Override
            public int currentPart(int i)
            {
                return current[i];
            }
        };
    }
    
    public short nimValue(int heapSize)
    {
        return nimValues.nimValue(heapSize);
    }
    
    public short[] nimValues(int maxHeapSize)
    {
        return nimValues.nimValues(maxHeapSize);
    }
    
    public APChecker getAPChecker()
    {
        return null;
    }
    
    public APInfo checkPeriodicity(int maxHeapSize)
    {
        return nimValues.checkPeriodicity(maxHeapSize);
    }
    
    /**
     * A low-overhead traversal of the options specified by a
     * <code>HeapRules</code>.  When performance is an issue,
     * a <code>Traversal</code> can be used to iterate over the options
     * of a heap game in order
     * to minimize the amount of memory allocation during the iteration.
     *
     * <p>A <code>Traversal</code> iterates over a set of int[]
     * arrays without necessarily keeping more than one element of set in
     * memory at a time.  In addition, the one element does not need to be
     * exposed to the user, so that it may be reused to store new values
     * of the set without risk of modification or aliasing.
     *
     * <p>The {@link #advance() advance} method moves to the next array in
     * the collection.  The {@link #currentLength() currentLength} method
     * returns the length of the current array.
     * The {@link #currentPart(int) currentPart(i)} method returns the
     * <code>i</code><sup>th</sup> element of the current array.
     *
     * <p>
     * The following example shows how a <code>Traversal</code> might be used
     * to calculate the Grundy value of a heap, given access to the Grundy
     * values of all smaller heaps.
     *
     * <p>
     * <pre>int[] gValues = new int[1 << 16];
     * HeapRules myRules = getRules();  // (getRules() returns my custom HeapRules)
     * java.util.BitSet mexSet = new java.util.BitSet();
     *
     * void computeHeap(int heap)
     * {
     *     Traversal t = myRules.traversal(heap);
     *     mexSet.clear();
     *
     *     while (t.advance())
     *     {
     *         int result = 0;
     *         for (int i = 0; i < t.currentLength(); i++)
     *         {
     *             result ^= gValues[t.currentPart(i)];
     *         }
     *         mexSet.set(result);
     *     }
     *
     *     gValues[heap] = mexSet.nextClearBit(0);
     * }
     * </pre>
     *
     * <p> The amount of memory allocation performed is constant in the
     * size of the heap.
     *
     * @author  Aaron Siegel
     * @author  Dan Hoey
     * @version $Revision: 1.5 $ $Date: 2007/04/09 23:51:51 $
     */
    public interface Traversal
    {
        /**
         * Advances to the next int[] set element.
         *
         * @return <code>true</code> if this <code>Traversal</code>
         *          advanced to an element; <code>false</code> if the
         *          traversal is complete.
         */
        public boolean advance();
    
        /**
         * Gets the length of the current int[] element
         *
         * @return  The length of the current element.
         * @throws  NoSuchElementException if {@link #advance() advance} has
         *          not yet been called, or if the last call to
         *          <code>advance</code> returned <code>false</code>.
         */
        public int currentLength();
            
        /**
         * Gets the value of the <code>i</code><sup>th</sup> component of
         * the current int[] element.
         *
         * @param   i indiex into the current element
         * @return  The value of the <code>i</code><sup>th</sup> component
         *          of the current element.
         * @throws  NoSuchElementException if {@link #advance() advance} has
         *          not yet been called, or if the last call to
         *          <code>advance</code> returned <code>false</code>.
         * @throws  IndexOutOfBoundsException if the index <code>i</code>
         *          is negative or greater than <code>currentLength()</code>.
         */
        public int currentPart(int i);
    }
}
