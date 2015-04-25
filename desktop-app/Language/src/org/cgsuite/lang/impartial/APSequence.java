/*
 * APSequence.java
 *
 * Created on October 24, 2006, 1:40 AM
 *
 * $Id: APSequence.java,v 1.4 2007/02/13 22:39:47 asiegel Exp $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A utility class providing representations of arithmetic periodic sequences of integers
 * (including preperiods) together with methods to generate such sequences matching a given
 * sequence of values. APSequence objects are immutable. Constructors are protected because
 * each APSequence is stored in its normal form -- with the minimum possible period and saltus.
 * @author malbert
 * @version $Revision: 1.4 $ $Date: 2007/02/13 22:39:47 $
 */
public class APSequence implements Comparable<APSequence> {
    
    /**
     * The default minimum number of periods (10) required to generate an AP sequence from a
     * given sequence of values (used to determine the maximum possible period length)
     */
    private static final int MIN_PERIODS = 10;
    /**
     * The default maximum fraction of a sequence of integer values that can be part
     * of the preperiod in generating an AP sequence from a sequence of values (default
     * is 4, i.e. 1/4).
     */
    private static final int PREPERIOD_FRACTION = 4;
    
    private static final boolean SAFE = true;
    private static final boolean UNSAFE = false;
   
    
    private int saltus;
    private int[] block;
    private int[] preperiod;
    
    /**
     * Create an AP sequence with the given preperiod, block, and saltus, allows for "safe"
     * construction when the underlying arrays do not need to be copied.
     * @param safe true if it is safe not to create copies of the underlying arrays.
     * @param preperiod The values of the preperiod.
     * @param block The values of the periodic block.
     * @param saltus The saltus.
     */
    protected APSequence(int[] preperiod, int[] block, int saltus, boolean safe) {
        this.saltus = saltus;
        if (safe) {
           this.preperiod = preperiod;
           this.block = block;
        }
        else {
             this.block = new int[block.length];
             System.arraycopy(block, 0, this.block, 0, block.length);
             this.preperiod = new int[preperiod.length];
              System.arraycopy(preperiod, 0, this.preperiod, 0, preperiod.length);    
        }
    }
    
    /**
     * Create an AP sequence with the given preperiod, block, and saltus.
     * @param preperiod The values of the preperiod.
     * @param block The values of the block.
     * @param saltus The saltus.
     */
    protected APSequence(int[] preperiod, int[] block, int saltus) {
        this(preperiod, block, saltus, UNSAFE);
    }
    
    
    /**
     * Creates a "pure" AP sequence (no preperiod) with the given block and saltus.
     * @param block The values of the periodic block.
     * @param saltus The saltus.
     */
    protected APSequence(int[] block, int saltus) {
        this(new int[0], block, saltus);
    }
    
    /**
     * Creates an AP sequence from an array of values given the preperiod length, period length,
     * and saltus.
     * @param values The values used to generate the sequence.
     * @param preperiodLength The preperiod length.
     * @param period The period length.
     * @param saltus The saltus.
     */
    protected APSequence(int[] values, int preperiodLength, int period, int saltus) {
        this.saltus = saltus;
        this.preperiod = new int[preperiodLength];
        System.arraycopy(values, 0, preperiod, 0, preperiodLength);
        this.block = new int[period];
        System.arraycopy(values, preperiodLength, block, 0, period);
    }
    
    /**
     * Creates an AP sequence from a <code>List<Integer></code> of values given the preperiod length, period length,
     * and saltus.
     * @param values The List of values.
     * @param preperiodLength The preperiod length.
     * @param period The period length.
     * @param saltus The saltus.
     */
    protected APSequence(List<Integer> values, int preperiodLength, int period, int saltus) {
        if (values.size() < preperiodLength + period) return;
        this.saltus = saltus;
        this.preperiod = new int[preperiodLength];
        for(int i = 0; i < preperiodLength; ++i) {
            preperiod[i] = values.get(i);
        }
        this.block = new int[period];
        for(int i = 0; i < period; ++i) {
            block[i] = values.get(i + preperiodLength);
        }
    }
    
    /**
     * Return the period of this AP sequence.
     * @return The period of this AP sequence.
     */
    public int getPeriod() {
        return block.length;
    }
    
    /**
     * Return the saltus of this AP sequence.
     * @return The saltus of this AP sequence.
     */
    public int getSaltus() {
        return saltus;
    }
    
    /**
     * Return the preperiod length of this AP sequence.
     * @return The preperiod length of this AP sequence.
     */
    public int getPreperiodLength() {
        return preperiod.length;
    }
    
    /**
     * Return the block (period values) of this AP sequence.
     * @return The block (period values) of this AP sequence.
     */
    public int[] getBlock() {
        int[] blockCopy = new int[block.length];
        System.arraycopy(block, 0, blockCopy, 0, block.length);
        return blockCopy;
    }
    
    /**
     * Return the preperiod values of this AP sequence.
     * @return The preperiod values of this AP sequence.
     */
    public int[] getPreperiod() {
        int[] preperiodCopy = new int[preperiod.length];
        System.arraycopy(preperiod, 0, preperiodCopy, 0, preperiod.length);
        return preperiodCopy;
    }
    
    /**
     * Returns the value of this sequence at the given index.
     * @param i The index.
     * @return The value of this sequence at the given index.
     */
    public int at(int i) {
        int offset = i - preperiod.length;
        if (offset < 0) return preperiod[i];
        return (block[offset % block.length] + saltus*(offset/block.length));
    }
    
    /**
     * Returns an <CODE>int[]</CODE> giving the values of an initial segment of this sequence.
     * @param length The length of the initial segment.
     * @return The values of an initial segment of this sequence.
     */
    public int[] toIntArray(int length) {
        int[] result = new int[length];
        for(int i = 0; i < length; ++i) result[i] = this.at(i);
        return result;
    }
    
    /**
     * Returns a string representation of this sequence. This consists of the standard string
     * representation of the preperiod, followed by the block enclosed in ()'s followed by the
     * saltus enclosed in []'s.
     * @return A string representation of this AP sequence.
     */
    public String toString() {
        return Arrays.toString(preperiod) + "(" + Arrays.toString(block) + ")[" + saltus + "]";
    }
    
    /**
     * Static class method that returns a list of indices where the given array of values
     * fails to equal the values of an AP sequence which would have the given periodic block
     * and saltus.
     * @param values The values to be checked.
     * @param block The proposed block.
     * @param saltus The proposed saltus.
     * @return The list of indices at which the given array differs from the predicted values.
     */
    public static ArrayList<Integer> exceptionList(int[] values, int[] block, int saltus) {
        int period = block.length;
        ArrayList<Integer> exceptions = new ArrayList<Integer>();
        for(int i = 0; i < values.length; ++i) {
            if (values[i] != block[i % period] + saltus*(i/period)) {
                exceptions.add(i);
            }
        }
        return exceptions;
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input array of values up to
     * the specified length, subject to given bounds on the period and preperiod length.
     * If no such sequence exists, then returns a null sequence. The method is brute force
     * search from smallest possible period upwards, so the returned result (if non null) will
     * always have the shortest possible period.
     * @return The AP sequence of least period matching the given values and bounds.
     * <CODE>null</CODE> if no such sequence exists.
     * @param values The given sequence of values.
     * @param length The length of the initial segment of values to examine.
     * @param maxPeriod The maximum allowed period.
     * @param maxPreperiod The maximum allowed preperiod length.
     */
    public static APSequence findAPSequence(int[] values, int length, int maxPeriod, int maxPreperiod) {
        
        for(int period = 1; period <= maxPeriod; ++period) {
            int k = length -1;
            int j = k - period;
            if (j < 0) return null;
            int saltus = values[k]-values[j];
            while (j >= 0 && values[k] - values[j] == saltus) {
                --k; --j;
            }
            if (j < maxPreperiod) {
                return new APSequence(values, j+1, period, saltus);
            }
        }
        return null;
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input array of values up to
     * the specified length, subject to given bounds on the period and preperiod length.
     * If no such sequence exists, then returns a null sequence. The method is brute force
     * search from smallest possible period upwards, so the returned result (if non null) will
     * always have the shortest possible period.
     * @return The AP sequence of least period matching the given values and bounds.
     * <CODE>null</CODE> if no such sequence exists.
     * @param values The given sequence of values.
     * @param length The length of the initial segment of values to examine.
     * @param maxPeriod The maximum allowed period.
     * @param maxPreperiod The maximum allowed preperiod length.
     */
    public static APSequence findAPSequence(short[] values, int length, int maxPeriod, int maxPreperiod) {
        
        for(int period = 1; period <= maxPeriod; ++period) {
            int k = length -1;
            int j = k - period;
            if (j < 0) return null;
            int saltus = values[k]-values[j];
            while (j >= 0 && values[k] - values[j] == saltus) {
                --k; --j;
            }
            if (j < maxPreperiod) {
                int[] intValues = new int[j+1+period];
                for(int i = 0; i < j+1+period; ++i) intValues[i] = values[i];
                return new APSequence(intValues, j+1, period, saltus);
            }
        }
        return null;
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input array of values, subject
     * to given bounds on the period and preperiod length. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @param maxPeriod The maximum allowed period.
     * @param maxPreperiod The maximum allowed preperiod length.
     * @return The AP sequence of least period matching the given values and bounds.
     * <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findAPSequence(int[] values, int maxPeriod, int maxPreperiod) {
        return findAPSequence(values, values.length, maxPeriod, maxPreperiod);
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input array of values, subject
     * to given bounds on the period and preperiod length. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @param maxPeriod The maximum allowed period.
     * @param maxPreperiod The maximum allowed preperiod length.
     * @return The AP sequence of least period matching the given values and bounds.
     * <CODE>null</CODE> if no such sequence exists.
     */
    
    public static APSequence findAPSequence(short[] values, int maxPeriod, int maxPreperiod) {
        return findAPSequence(values, values.length, maxPeriod, maxPreperiod);
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input sequence of values, subject
     * to the default bounds on the period and preperiod length. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @return The AP sequence of least period matching the given values and default bounds.
     * <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findAPSequence(int[] values) {
        int maxPeriod = values.length/MIN_PERIODS;
        int maxPreperiod = values.length/PREPERIOD_FRACTION;
        return findAPSequence(values, values.length, maxPeriod, maxPreperiod);
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input sequence of values, subject
     * to the default bounds on the period and preperiod length. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @return The AP sequence of least period matching the given values and default bounds.
     * <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findAPSequence(short[] values) {
        int maxPeriod = values.length/MIN_PERIODS;
        int maxPreperiod = values.length/PREPERIOD_FRACTION;
        return findAPSequence(values, values.length, maxPeriod, maxPreperiod);
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input sequence of values, up
     * to the specified length subject
     * to the default bounds on the period and preperiod length. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @param length The length of the initial segment of values to be considered.
     * @return The AP sequence of least period matching the given values and default bounds.
     * <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findAPSequence(int[] values, int length) {
        int maxPeriod = values.length/MIN_PERIODS;
        int maxPreperiod = values.length/PREPERIOD_FRACTION;
        return findAPSequence(values, length, maxPeriod, maxPreperiod);
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input sequence of values, up
     * to the specified length subject
     * to the default bounds on the period and preperiod length. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @param length The length of the initial segment of values to be considered.
     * @return The AP sequence of least period matching the given values and default bounds.
     * <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findAPSequence(short[] values, int length) {
        int maxPeriod = values.length/MIN_PERIODS;
        int maxPreperiod = values.length/PREPERIOD_FRACTION;
        return findAPSequence(values, length, maxPeriod, maxPreperiod);
    }
    
    /**
     * Computes an AP sequence (if possible) matching the given input List of values, subject
     * to given bounds on the period and preperiod length. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @param maxPeriod The maximum allowed period.
     * @param maxPreperiod The maximum allowed preperiod length.
     * @return The AP sequence of least period matching the given values and bounds.
     * <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findAPSequence(List<Integer> values, int maxPeriod, int maxPreperiod) {
        for(int period = 1; period <= maxPeriod; ++period) {
            int k = values.size() -1;
            int j = k - period;
            if (j < 0) return null;
            int saltus = values.get(k) - values.get(j);
            while (j >= 0 && values.get(k) - values.get(j) == saltus) {
                --k; --j;
            }
            if (j < maxPreperiod) {
                return new APSequence(values, j+1, period, saltus);
            }
        }
        return null;
    }
    
    /**
     * Computes a pure AP sequence (if possible) matching the given input sequence of values, up to
     * the given length subject to the default bound on the period. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @param length The length of the initial segment of values to be considered.
     * @return The pure AP sequence of least period matching the given values and bound on
     * period length. <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findPureAPSequence(int[] values, int length) {
        int maxPeriod = values.length/MIN_PERIODS;
        return findAPSequence(values, length, maxPeriod, 0);
    }
    
    /**
     * Computes a pure AP sequence (if possible) matching the given input sequence of values, up to
     * the given length subject to the default bound on the period. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @param length The length of the initial segment of values to be considered.
     * @return The pure AP sequence of least period matching the given values and bound on
     * period length. <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findPureAPSequence(short[] values, int length) {
        int maxPeriod = values.length/MIN_PERIODS;
        return findAPSequence(values, length, maxPeriod, 0);
    }
    
    /**
     * Computes a pure AP sequence (if possible) matching the given input sequence of values, subject
     * to the default bound on the period. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @return The pure AP sequence of least period matching the given values subject to the
     * default bound on period length. <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findPureAPSequence(int[] values) {
        return findPureAPSequence(values, values.length);
    }
    
    /**
     * Computes a pure AP sequence (if possible) matching the given input sequence of values, subject
     * to the default bound on the period. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The given sequence of values.
     * @return The pure AP sequence of least period matching the given values subject to the
     * default bound on period length. <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findPureAPSequence(short[] values) {
        return findPureAPSequence(values, values.length);
    }
    
    /**
     * Computes a pure AP sequence (if possible) matching the given input sequence of values, subject
     * to the default bound on the period. If no such sequence exists, then returns
     * a null sequence. The method is brute force search from smallest possible period upwards, so
     * the returned result (if non null) will always have the shortest possible period.
     * @param values The sequence of values.
     * @return The pure AP sequence of least period matching the given values subject to the
     * default bound on period length. <CODE>null</CODE> if no such sequence exists.
     */
    public static APSequence findPureAPSequence(List<Integer> values) {
        int maxPeriod = values.size()/MIN_PERIODS;
        return findAPSequence(values, maxPeriod, 0);
    }
    
    /**
     * Constructs an AP sequence from a notional preperiod and period together with a saltus. The
     * object constructed will have the minimum possible period length, and preperiod length, which
     * match the given data.
     * @param prep A presumptive preperiod.
     * @param per A presumptive period.
     * @param saltus A given saltus.
     * @return An AP sequence whose values match those supplied, having the minimum possible
     * preperiod and period.
     */
    public static APSequence constructAPSequence(int[] prep, int[] per, int saltus) {
        
        // Try to shorten the period
        int periodLength = findReducedPeriod(per, saltus);
        int newSaltus = (saltus*periodLength)/per.length;
        
        int[] block = new int[periodLength];
        // Try to shorten the preperiod too
        int j = periodLength - 1;
        int k = prep.length - 1;
        while(j >= 0 && k >= 0 && per[j] - prep[k] == newSaltus) {
            --j; --k;
        }
        if (j > 0) {
            // New period consists of last (periodLength - j-1) elements of prep, and first j+1 elements of per
            System.arraycopy(prep, prep.length - periodLength + j+1,block, 0, periodLength - j-1);
            System.arraycopy(per, 0, block,periodLength-j-1, block.length - periodLength + j+1);
            // New preperiod consists of first k+1 elements of prep
            int[] preperiod = new int[k+1];
            System.arraycopy(prep, 0, preperiod, 0, k+1);
            return new APSequence(preperiod, block, newSaltus, SAFE);
        } else {
            // New period is entirely within prep
            j = prep.length-1;
            k = prep.length - periodLength - 1;
            while(k >= 0 && prep[j] - prep[k] == newSaltus) {
                --j; --k;
            }
            // First k+1 elements form preperiod
            return new APSequence(prep, k+1, periodLength, newSaltus);
        }
    }
    
    protected static int findReducedPeriod(int[] per, int saltus) {
        int newSaltus = 0;
        int periodLength = 0;
        for(periodLength = 1; periodLength <= per.length/2; ++periodLength) {
            if (per.length % periodLength == 0 && saltus % (per.length/periodLength) == 0) {
                newSaltus = (saltus*periodLength)/per.length;
                int i = per.length - 1;
                while(i >= periodLength && per[i] - per[i - periodLength] == newSaltus) --i;
                if (i < periodLength) {
                    return periodLength;
                }
            }
        }
        
       return per.length;
    }
    
    /**
     * Tests for equality with another object. If the other object is an AP sequence then the result
     * will be true provided that the saltus, block, and preperiod of the two sequences agree. Since
     * APSequences can only be constructed in normal form, this is (intended to be) the same as equality
     * as sequences of values.
     * @param o The object to test for equality with.
     * @return <CODE>true</CODE> if (and only if) <CODE>o</CODE> is an <CODE>APSequence</CODE>
     * with the same parameters as <CODE>this</CODE>.
     */
    public boolean equals(Object o) {
        if  (o instanceof APSequence) {
            APSequence other = (APSequence) o;
            return other.saltus == this.saltus &&
                    Arrays.equals(other.block, this.block) &&
                    Arrays.equals(other.preperiod, this.preperiod);
        } else {
            return false;
        }
    }
    
    
    /**
     * Compares two AP sequences lexicographically.
     * @param other The sequence to compare to.
     * @return The result of the comparison.
     */
    public int compareTo(APSequence other) {
        int checkingBound = Math.max(this.preperiod.length + 2*this.block.length,
                other.preperiod.length + 2*other.block.length);
        for(int i = 0; i < checkingBound; ++i) {
            int ti = this.at(i);
            int oi = other.at(i);
            if (ti != oi) return (ti-oi);
        }
        return 0;
    }
    
    /**
     * Computes a hash code for an AP sequence.
     * @return The hash code.
     */
    public @Override int hashCode() {
        int hc = saltus + 3*preperiod.length + 15*block.length;
        for(int i = 0; i < preperiod.length; ++i) {
            hc *= 7; hc += preperiod[i];
        }
        for(int i = 0; i < block.length; ++i) {
            hc *= 11; hc += block[i];
        }
        return hc;
    }
}

