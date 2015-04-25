/*
 * TBCode.java
 *
 * Created on February 15, 2006, 9:56 AM
 * $Id: TBCode.java,v 1.20 2008/01/06 21:18:25 haoyuep Exp $
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
import org.cgsuite.lang.output.StyledTextOutput;

/*
 * <p>
 * A <code>TBCode</code> can also represent a renumbering of its heap
 * sizes using <em>ghost tokens</em>, which inflate the heap size
 * but cannot be used.  In a <code>TBCode</code> with <em>g</em> ghost
 * tokens, every heap of size <em>g</em> or less is ignored, and no
 * move may create a heap smaller than <em>g</em>.  Splitting a heap
 * of size <em>n</em> consists of splitting <em>n-g</em> of the tokens
 * using the normal rules, and adding <em>g</em> new ghost tokens to
 * each heap that results.  Ghost tokens are added to the
 * {@link #standardForm() standard form} of a TBCode to make the
 * {@link #ghostlyStandardForm() ghostly standard form}, which is
 * equivalent to the original TBCode, a <em>g</em>th cousin of the
 * standard form.  Textually, a TBCode with <em>g</em> ghosts is
 * represented by suffixing the TBCode's name with
 * <code>_<em>g</em></code>.
*/

/**
 * A code for a take-and-break game.
 * <p>
 * Take-and-break games are played with nonempty heaps of tokens.  A
 * move consists of adding or removing tokens from a single heap, and
 * optionally splitting the heap into several smaller heaps.  The
 * rules of the game dictate the number of tokens that can be removed,
 * as well as the circumstances under which a heap may be split.
 * <p>
 * The rules for a take-and-break game are customarily be represented
 * by a sequence of <i>code digits</i>
 * <code>d<sub>0</sub>.d<sub>1</sub>d<sub>2</sub>d<sub>3</sub>...d<sub>n</sub></code>.
 * The <code>i</code><sup>th</sup> digit <code>d<sub>i</sub></code>
 * codes the circumstances under which it is permissible to remove
 * <code>i</code> tokens, as follows.  If <code>d<sub>i</sub> &amp;
 * 2<sup>k</sup> != 0</code> (where <code>&amp;</code> is bitwise and),
 * then one may remove <code>i</code> tokens and split the remainder
 * of the heap into exactly <code>k</code> nonempty heaps.
 * <p>
 * For example, if <code>d<sub>4</sub> == 9</code>, then four tokens
 * may be removed from a heap, provided that the remainder are split
 * into exactly <i>zero or three</i> heaps (since <code>9 =
 * 2<sup>3</sup> + 2<sup>0</sup></code>).  In particular, it is never
 * permissible to remove four tokens from a heap of size 5 or 6, since
 * this would leave a remainder of 1 or 2 tokens, which could not be
 * split into zero or three heaps.
 * <p>
 * The <code>TBCode</code> class does not incorporate the actual play
 * of take-and-break games.  It merely serves as a convenient
 * interface for translating between representations of take-and-break
 * codes, and for deriving properties of those codes.  For example,
 * <code>TBCode</code> includes methods for parsing a string
 * representation (such as <code>&quot;4.77&quot;</code>), and for
 * determining whether a given code permits removing <code>i</code>
 * tokens while splitting into <code>k</code> heaps, and provides a
 * {@link HeapRules.Traversal} implementation for efficiently
 * examining all possible results of moving in a heap of size
 * <code>n</code>.  The <code>TBCode</code> class is intended to
 * support a variety of applications, including both impartial and
 * partizan take-and-break games.  <code>TBCode</code> may be
 * considered the restriction of the {@link HeapRules} interface to
 * rules that
 * <ul><li>extend to arbitrarily large heaps, determined only by the
 *         number of tokens removed, the number of resulting heaps,
 *         and equality constraints as described below;
 *     <li>are ultimately periodic in the restrictions on removing
 *         arbitrarily large number of tokens; and
 *     <li>do not allow unbounded increase in the number of tokens
 *         resulting from a heap.
 * </ul>
 * <p>
 * The early theory of take-and-break games considered only
 * <i>subtractive</i> games, where tokens are removed from a heap.
 * However, the <code>TBCode</code> class can also be used to code
 * <i>additive</i> games, where tokens may be added to a (non-empty)
 * heap.  These are coded using digits with negative index as in
 * <code>d<sub>-m</sub>...d<sub>-2</sub>d<sub>-1</sub>d<sub>0</sub>.d<sub>1</sub>d<sub>2</sub>...d<sub>n</sub></code>,
 * where digit <code>d<sub>-k</sub></code> describes adding
 * <code>k</code> tokens. For example, in <code>84.07</code>, a player
 * may either
 * <ul><li>remove two tokens, optionally splitting the remainder into
 *         two heaps,
 *     <li>remove no tokens, but (necessarily) split the heap into two
 *         nonempty heaps, or
 *     <li>add one token and split the result into three nonempty
 *         heaps.
 * </ul>
 * The number of strictly additive digits is called the
 * <i>prelength</i> of the code.  If a rule permits adding
 * <code>k&ge;0</code> tokens and leaving the result in fewer than
 * <code>k+2</code> nonempty heaps, the result may be loopy.
 * <p>
 * While a <code>TBCode</code> can have only a finite prelength, the
 * sequence of nonzero digits to the right of the dyadical point can
 * be infinite.  The <code>TBCode</code> class includes support for
 * an infinite trailing periodic sequence.  Such a sequence is
 * indicated by enclosing the repeating portion in
 * <code>[</code>square brackets<code>]</code>.
 * <p>
 * It is also possible to specify more complex constraints on the
 * allowable breaking rules.  This can be achieved by passing
 * instances of the {@link Digit} interface to the constructor.  The
 * default implementation {@link SimpleDigit} provides several common
 * breaking restrictions, including those found in games such as
 * Grundy's game.
 * <p>
 * Textually, constraints on a {@link SimpleDigit} are represented by
 * suffixing an exclamation point (<code>!</code>) for breaking into
 * pairwise unequal heaps or a question mark (<code>?</code>) for
 * breaking into heaps that are not all equal.  Thus Grundy's game is
 * encoded as <code>"4!."</code> (or equivalently <code>"4?."</code>)
 * More complicated syntax represents that not all splits are to be
 * constrained, or that different constraints apply to different
 * splits; see {@link #TBCode(String)} for the details.
 *
 * @author  Aaron Siegel
 * @author  Dan Hoey
 * @version $Revision: 1.20 $ $Date: 2008/01/06 21:18:25 $
 */
public class TBCode extends HeapRules
{
    ////////////////////////////////////////////////////////////////
    // private constants
    
    /*
     * Options for parsing TBCode-style code digits.
     */
    private final static CodeDigit.Options TBCodeDigitOptions
        = new CodeDigit.Options(36, true, "?!", "()", -1, 0, 16);
    
    ////////////////////////////////////////////////////////////////
    // Instance variables
    
    private Digit[] digits;
    
    /**
     * Number of strictly additive digits, also known as the
     * <i>prelength</i> of this code.  <code>nAdditiveDigits==0</code>
     * if there is exactly one digit to the left of the dyadical
     * point.
     */
    private int nAdditiveDigits;
    
    /**
     * Number of digits in the repeating subsequence, or 0 if the
     * sequence is finite.
     */
    private int period;
    
    /**
     * Number of ghost tokens under each heap.  Ghost tokens serve
     * only to renumber the sizes of heaps.  Ghost tokens may not be
     * removed, so a heap of <code>ghosts</code> or fewer tokens acts
     * like a zero heap.  Splits create new sets of ghost tokens under
     * the new heaps.
     */
    private int nGhosts;
    
    /**
     * Standard form, with ghosts, when we compute it
     */
    private TBCode ghostlyStandardForm;

    /**
     * Data for Knuth-Morris-Pratt algorithm for detecting periodicity
     * of a code.
     * <p>
     * <code>suffixPointers[i]</code> is the lowest index <code>k</code>
     * such that arr[i...] consists of repeated copies of arr[i..k];
     */
    private static int[] suffixPointers = new int[20];
    
    ////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Constructs a new <code>TBCode</code> with the specified
     * unconstrained code digits.  This is the easiest constructor to
     * use for unconstrained take-and-break games.  For example,
     * <p>
     * <code>new TBCode(0, 7, 7)</code>
     * <p>
     * constructs the code for Kayles (0.77).
     *
     * @param   digits The sequence of simple unconstrained code digits.
     */
    public TBCode(int ... digits)
    {
        Digit[] genDigits = new Digit[digits.length];
        for (int i = 0; i < digits.length; i++)
        {
            genDigits[i] = new SimpleDigit(i);
        }
        init(0, 0, 0, genDigits);
    }
    
    /**
     * Constructs a new <code>TBCode</code> with the specified code digits.
     *
     * @param   digits The sequence of code digits.
     */
    public TBCode(Digit ... digits)
    {
        this(0, 0, digits);
    }
    
    /**
     * Constructs a new <code>TBCode</code> with the specified code digits,
     * some of which might be additive.  The entry <code>digits[0]</code> is
     * interpreted as the digit in place <code>-nAdditiveDigits</code> of
     * the take-and-break code.
     * <p>
     * For example, if <code>digits</code> is such that
     * <p>
     * <code>TBCode(digits)</code>
     * <p>
     * represents <code>8.07</code>, then
     * <p>
     * <code>TBCode(1, digits)</code>
     * <p>
     * is <code>80.7</code>.
     * 
     * @param   nAdditiveDigits The number of digits to the left of
     *          the dyadical point.
     * @param   digits The sequence of code digits.
     */
    public TBCode(int nAdditiveDigits, Digit ... digits)
    {
        this(nAdditiveDigits, 0, digits);
    }
    
    /**
     * Constructs a new <code>TBCode</code> with the specified code digits,
     * some of which might be additive, and the specified period.
     * <p>
     * If <code>period > 0</code>, then the sequence of digits is assumed to
     * be infinite, with the last <code>period</code> entries repeated
     * infinitely often.
     * 
     * @param   nAdditiveDigits The number of digits to the left of
     *          the dyadical point.
     * @param   period The period of the infinite part, or zero if the
     *          TBCode is finite.
     * @param   digits The sequence of code digits.
     */
    public TBCode(int nAdditiveDigits, int period, Digit ... digits)
    {
        this(nAdditiveDigits, period, 0, digits);
    }
    
    /**
     * Constructs a new <code>TBCode</code> with the specified code digits,
     * some of which might be additive, the specified period, and the
     * specified number of ghost tokens.
     * 
     * @param   nAdditiveDigits The number of digits to the left of
     *          the dyadical point.
     * @param   period The period of the infinite part, or zero if the
     *          TBCode is finite.
     * @param   nGhosts The number of ghost tokens in each heap.
     * @param   digits The sequence of code digits.
     */
    public TBCode(int nAdditiveDigits, int period, int nGhosts, Digit ... digits)
    {
        init(nAdditiveDigits, period, nGhosts, digits);
    }
    
    private void init(int nAdditiveDigits, int period, int nGhosts, Digit ... digits)
    {
        // System.out.format("TB.init(%d,%d,%d,%s)%n",
        //                   nAdditiveDigits,period,nGhosts,Arrays.toString(digits));
        if (period < 0)
        {
            throw new IllegalArgumentException("period < 0");
        }
        if (period > digits.length)
        {
            throw new IllegalArgumentException("period > digits.length");
        }
        if (nAdditiveDigits < 0)
        {
            throw new IllegalArgumentException("nAdditiveDigits < 0");
        }
        if (nAdditiveDigits > digits.length)
        {
            throw new IllegalArgumentException("nAdditiveDigits > digits.length");
        }
        if (nGhosts < 0)
        {
            throw new IllegalArgumentException("nGhosts < 0");
        }
        
        // Count leading zeroes
        int firstDigit;
        for (firstDigit = 0;
             firstDigit < nAdditiveDigits && digits[firstDigit].maxSplit() < 0;
             firstDigit++);

        // Count trailing zeroes
        int lastDigit = digits.length-1;
        if (period == 0)
        {
            for (;
                 lastDigit > nAdditiveDigits && digits[lastDigit].maxSplit() < 0;
                 lastDigit--);
        }
        
        // Make room for a full period after the point
        int extension = 0;
        if (period > lastDigit - nAdditiveDigits)
        {
            extension = period - lastDigit + nAdditiveDigits;
        }

        this.digits = new Digit[lastDigit-firstDigit+1 + extension];
        System.arraycopy(digits, firstDigit, this.digits, 0, lastDigit-firstDigit+1);

        nAdditiveDigits -= firstDigit;
        this.nAdditiveDigits = nAdditiveDigits;

        // Copy the full period explicitly after the point

        for (int i=0; i<extension; ++i) {
            this.digits[lastDigit - firstDigit+1 + i]
                = digits[lastDigit + 1 + i - period];
        }

        // Remove nonsubtractive actions that result in empty heaps.
        for (int i=0; i <= nAdditiveDigits; ++i) {
            this.digits[i] = this.digits[i].withoutPermissibleSplit(0);
        }

        // Minimize period and preperiod
        if (period > 0) {
            int pStart = this.digits.length - period;
            int newPeriod = findPeriod(this.digits,pStart);
            while (pStart > nAdditiveDigits+1
                   && this.digits[pStart-1].equals(this.digits[pStart+newPeriod-1]))
            {
                pStart--;
            }
            if (pStart + newPeriod < this.digits.length)
            {
                Digit[] newDigits = new Digit[pStart + newPeriod];
                System.arraycopy(this.digits,0,newDigits,0,newDigits.length);
                this.digits = newDigits;
                period = newPeriod;
            }
        }

        this.period = period;
        this.nGhosts = nGhosts;
        this.ghostlyStandardForm = null;
    }
    
    /**
     * Tests <code>arr[beg .. beg+n-1]</code> for periodicity or
     * arithmeto-periodicity.
     * Adapted from Michael Albert's APChecker code.
     *
     * @param arr    The array containing possibly periodic data
     * @param beg    The beginning index of the possibly periodic data
     * @return The least possible subperiod <code>p</code> of this
     *               sequence.  <code>p</code> divides
     *               </code>arr.size - beg</code>.
     */
    private static int findPeriod(Object arr[], int beg)
    {
        int top = arr.length - 1;
        if (suffixPointers.length <= top+1) {
            suffixPointers = new int[(top*5)/4 + 10];
        }
        suffixPointers[top+1]=top+2;
        // System.out.format("beg=%d,top=%d,sp[%d]=%d%n",beg,top,top+1,top+2);
        for(int i = top; i >= beg; --i) {
            int p = suffixPointers[i+1];
            while (p <= top+1)
            {
                if (arr[i].equals(arr[p-1]))
                {
                    break;
                }
                else
                {
                    p = suffixPointers[p];
                }
            }
            suffixPointers[i] = p-1;
            // System.out.format("sp[%d]=%d%n",i,p-1);
        }
        int per = arr.length-beg;
        int nPer = suffixPointers[beg]-beg;
        return per%nPer==0 ? nPer : per;
    }

    ////////////////////////////////////////////////////////////////
    // String constructor and parsing routines
    
    /**
     * Constructs a <code>TBCode</code> from the specified string.
     * <p>
     * The string must be a string of generalized digits with exactly
     * one dyadical point <code>.</code> appearing somewhere in the
     * string.
     * <p>
     * The allowed unconstrained generalized digits are:
     * <ul><li><code>'0'</code> through <code>'9'</code>, representing
     *         those digits,
     *     <li><code>'A'</code> through <code>'Z'</code>, representing
     *         digits 10 through 35 (<i>not</i> case-sensitive),
     *     <li><code>&amp;#</code><i>&lt;digits&gt;</i><code>;</code>,
     *         representing a digit whose value is specified by the
     *         <i>&lt;digits&gt;</i>, interpreted as a number in base
     *         10, or
     *     <li><code>&amp;#x</code><i>&lt;hex-digits&gt;</i><code>;</code>,
     *         representing a digit whose value is specified by the
     *         <i>&lt;hex-digits&gt;</i>, interpreted as a number in
     *         base 16.</ul>
     * <p>
     * A split into two or more parts may be constrained to produce
     * unequal parts, in various ways.
     * <ul><li><code>d!</code> constrains all splits permitted by
     *         digit <code>d</code> to produce pairwise unequal
     *         parts,</li>
     *     <li><code>d?</code> constrains all splits permitted by
     *         digit <code>d</code> to produce parts that are not all
     *         equal,</li>
     *     <li><code>(d!a)</code> constrains all splits specified by
     *         <code>a</code> to produce parts that are pairwise
     *         unequal.  Digit <code>a</code> must be a multiple of
     *         four, since constraints apply only to splits into two
     *         or more heaps.</li>
     *     <li><code>(d?b)</code> constrains all splits specified by
     *         <code>b</code> to produce parts that are not all equal.
     *         Digit <code>b</code> must be a multiple of four, since
     *         constraints apply only to splits into two or more
     *         heaps.</li>
     *     <li><code>(d?b!a)</code>
     *         imposes different constraints on different splits.
     *         If <code>a&amp;b != 0</code> the result is
     *         undefined.</li>
     *     <li><code>(d?b!)</code> constrains all splits permitted by
     *         <code>d</code> that are not constrained by
     *         <code>b</code>.  This is equivalent to
     *         <code>(d?b!a)</code> where <code>a ==
     *         d&amp;~(b|3)</code>.</li>
     *     <li><code>(d?!a)</code> constrains all splits permitted by
     *         <code>d</code> that are not constrained by
     *         <code>a</code>.</li></ul>
     * In the parenthesized forms, it is customary for <code>a</code>
     * and <code>b</code> to be disjoint submasks of <code>d</code>,
     * so that <code>d</code> specifies all the possible splits, while
     * <code>a</code> or <code>b</code> specifies a constraint on a
     * subset of those splits, but this is not enforced.   Thus, if
     * <code>c = d|a</code>, then <code>(c!a)</code> is the customary
     * equivalent of <code>(d!a)</code>.
     * <p>
     * A trailing sequence of (possibly constrained) digits may be
     * enclosed in <code>[</code> square brackets <code>]</code>,
     * indicating that the enclosed sequence repeats indefinitely.
     * <p>
     * Following the digits and square brackets, a suffix of the form
     * <code>_</code><i>&lt;digits&gt;</i> may appear, to indicate that
     * this code operates on heaps with the specified number of ghost
     * tokens.
     * <p>
     * Here are some examples:
     * <ul><li><code>0.[3]</code> - Nim (remove any number of tokens from
     *         a heap)
     *     <li><code>0.77</code> - Kayles (remove one or two tokens from
     *         a heap, optionally splitting the remainder into two heaps)
     *     <li><code>4!.</code> - Grundy's Game (split any heap into
     *         two unequal heaps)
     *     <li><code>20.017</code> - Either add a token to a non-empty heap;
     *         or remove a heap of size 2 completely; or remove three tokens
     *         from a heap, optionally splitting the remainder into two heaps
     *     <li><code>0.[37]</code> - Remove any (positive) number of tokens
     *         from a heap.  If the number of tokens removed is even, then
     *         the heap may optionally be split into two heaps
     *     <li><code>8!.</code> - Split any heap into exactly three pairwise
     *         unequal heaps
     *     <li><code>8?.</code> - Split any heap into exactly three heaps,
     *         which cannot all be equal
     *     <li><code>0.(r?8!g)</code> - Remove one token from a heap.
     *         The remainder may either be left as a single heap; or split
     *         into exactly three heaps, not all the same; or split into
     *         exactly four pairwise unequal heaps.  (Note that
     *         <code>'r' = 27 = (1 | 2 | 8 | 16)</code> and
     *         <code>'g' = 16</code>)
     *     <li><code>0.&amp;127;!&amp;64;</code> - Either: remove one
     *         token from a heap, splitting the remainder into <i>at most
     *         six</i> pairwise unequal heaps; or remove two tokens from
     *         a heap, splitting the remainder into <i>exactly six</i>
     *         heaps (with no restrictions on heap size)
     * </ul>
     *
     * @param   str <code>String</code> that represents a
     *          take-and-break code. 
     * @throws  NumberFormatException if <code>str</code> is not a valid
     *          take-and-break code.
     * @throws  ArrayIndexOutOfBoundsException if no digits are specified.
     */
    public TBCode(String str)
    {
        List<Digit> digits = new ArrayList<Digit>();
            
        int nAdditiveDigits = -1;
        int periodStart = -1;
        int periodEnd = -1;
        int nGhosts = 0;

        for (int chrPos = 0; chrPos < str.length(); chrPos++)
        {
            char ch = str.charAt(chrPos);

            if (ch == '.')
            {
                if (nAdditiveDigits >= 0)
                {
                    throw new NumberFormatException
                        ("Unexpected character: '.'");
                }
                if (digits.isEmpty())
                {
                    digits.add(SimpleDigit.ZERO);
                }
                nAdditiveDigits = digits.size() - 1;
            }
            else if (ch == '[')
            {
                if (periodStart >= 0)
                {
                    throw new NumberFormatException
                        ("Unexpected character: '['");
                }
                periodStart = digits.size();
            }
            else if (ch == ']')
            {
                if (periodStart < 0 || periodEnd > 0) 
                {
                    throw new NumberFormatException
                        ("Unexpected character: ']'");
                }
                periodEnd = digits.size();
            }
            else if (ch == '_') 
            {
                nGhosts = Integer.parseInt(str.substring(chrPos+1));
                break;
            }
            else
            {
                CodeDigit d = new CodeDigit(str,chrPos,TBCodeDigitOptions);
                digits.add(new SimpleDigit(str.substring(chrPos, d.breakPosition)));
                chrPos = d.breakPosition - 1;
            }
        }

        if (periodStart >= 0) 
        {
            if(periodEnd < digits.size()) 
            {
                throw new NumberFormatException
                    ("Brackets must end at end of string");
            }
            if(periodStart == periodEnd)
            {
                throw new NumberFormatException
                    ("Kindly do not waste our brackets on empty repetends.");
            }
        }
        if (nAdditiveDigits == -1)
        {
            throw new NumberFormatException("Code must contain a '.'");
        }
        
        init(nAdditiveDigits, periodEnd-periodStart, nGhosts, digits.toArray(new Digit[digits.size()]));
    }
    
    ////////////////////////////////////////////////////////////////
    // toString()
    
    @Override
    public StyledTextOutput toOutput()
    {
        return new StyledTextOutput(appendToStringBuilder(new StringBuilder()).toString());
    }
    
    public StringBuilder appendToStringBuilder(StringBuilder sb)
    {
        for (int i = 0; i <= digits.length; i++)
        {
            if (i == nAdditiveDigits + 1)
            {
                sb.append('.');
            }
            if (period > 0 && i == digits.length - period)
            {
                sb.append('[');
            }
            if (i < digits.length)
            {
                if (digits[i] instanceof SimpleDigit) 
                {
                    ((SimpleDigit)digits[i]).appendToStringBuilder(sb);
                }
                else
                {
                    sb.append(digits[i].toString());
                }
            }
        }
        if (period > 0)
        {
            sb.append(']');
        }
        if (nGhosts !=0)
        {
            sb.append('_');
            sb.append(nGhosts);
        }
        return sb;
    }

    ////////////////////////////////////////////////////////////////
    // Standard methods for hashing and equality testing.
    
    public boolean equals(Object obj)
    {
        return obj instanceof TBCode &&
            Arrays.equals(digits, ((TBCode) obj).digits) &&
            nAdditiveDigits==((TBCode) obj).nAdditiveDigits &&
            period == ((TBCode) obj).period &&
            nGhosts == ((TBCode) obj).nGhosts;
    }
    
    public int hashCode()
    {
        return Arrays.hashCode(digits) ^
            (period << 16) ^
            (nAdditiveDigits << 8);
    }
    
    ////////////////////////////////////////////////////////////////
    // Public utilities for reporting useful stuff about this code.
    /**
     * Gets the index of the first non-zero digit of this code.
     * (This will be negative if more than one digit to the left of
     * the dyadical point was specified.)
     *
     * @return  The index of the first non-zero digit of this code.
     * @see     #prelength() prelength
     */
    public int firstDigit()
    {
        return -nAdditiveDigits;
    }
    
    /**
     * Gets the prelength of this code.  This is always equal to
     * <code>Math.max(-firstDigit(), 0)</code>.
     *
     * @return  The prelength of this code.
     * @see     #firstDigit() firstDigit
     */
    public int prelength()
    {
        return Math.max(-firstDigit(), 0);
    }
    
    /**
     * Gets the length of this code.  This is always equal to
     * <code>Math.max(0, lastDigit())</code>.
     *
     * @return  The length of this code.
     * @see     #lastDigit() lastDigit
     */
    public int length()
    {
        return Math.max(0, lastDigit());
    }
    
    /**
     * Gets the index of the last non-zero digit of this code.
     *
     * @return  The index of the last non-zero digit of this code,
     *          meaning the maximum number of tokens that can be
     *          removed from a heap. If this is an infinite code, then
     *          {@link java.lang.Integer#MAX_VALUE} will be returned.
     * @see     #length() length
     */
    public int lastDigit()
    {
        if (period > 0)
        {
            return Integer.MAX_VALUE;
        }
        else
        {
            return digits.length - nAdditiveDigits - 1;
        }
    }
    
    /**
     * Returns <code>true</code> if this is a simple code; that is, if every
     * digit is a {@link SimpleDigit}.
     *
     * @return  <code>true</code> if this is a simple code.
     */
    public boolean isSimple()
    {
        for (Digit digit : digits)
        {
            if (!(digit instanceof SimpleDigit))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns <code>true</code> if this is an unconstrained simple code; that
     * is, every every digit is an unconstrained {@link SimpleDigit}.
     *
     * @return  <code>true</code> if this is an unconstrained simple code.
     * @see     SimpleDigit#hasConstraints() SimpleDigit.hasConstraints
     */
    public boolean isSimpleAndUnconstrained()
    {
        for (Digit digit : digits)
        {
            if (!(digit instanceof SimpleDigit) ||
                ((SimpleDigit) digit).hasConstraints())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets the maximum number of heaps that a heap may be split
     * into.  This is equal to the maximum value returned by the
     * {@link Digit#maxSplit() maxSplit} method of any of this code's
     * {@link Digit}s.
     *
     * @return Maximum number of heaps that can result from
     *         breaking a heap according to this code.
     */
    public int maxSplit()
    {
        int max = 0;
        for (Digit digit : digits)
        {
            max = Math.max(max, digit.maxSplit());
        }
        return max;
    }
    
    /**
     * Returns <code>true</code> if this is a quaternary code
     * (with all <code>d<sub>i</sub> &lt; 4</code>).
     *
     * @return  <code>true</code> if this is a quaternary code.
     */
    public boolean isQuaternary()
    {
        return isSimpleAndUnconstrained() && maxSplit() <= 1;
    }
    
    /**
     * Returns <code>true</code> if this is an unconstrained octal code
     * (with all <code>d<sub>i</sub> &lt; 8</code>).
     *
     * @return  <code>true</code> if this is an unconstrained octal code.
     */
    public boolean isOctal()
    {
        return isSimpleAndUnconstrained() && maxSplit() <= 2;
    }
    
    /**
     * Returns <code>true</code> if this is a (possibly constrained)
     * octal code (with all moves to at most two heaps).
     *
     * @return  <code>true</code> if this is a possibly constrained
     * octal code. 
     */
    public boolean isGeneralizedOctal()
    {
        return isSimple() && maxSplit() <= 2;
    }
    
    /**
     * Returns <code>true</code> if this is an unconstrained hexadecimal
     * code (with all <code>d<sub>i</sub> &lt; 16</code>).
     *
     * @return  <code>true</code> if this is a unconstrained hexadecimal
     *          code. 
     */
    public boolean isHexadecimal()
    {
        return isSimpleAndUnconstrained() && maxSplit() <= 3;
    }
    
    /**
     * Returns <code>true</code> if this is a (possibly constrained)
     * hexadecimal code (with all moves producing at most three heaps).
     *
     * @return  <code>true</code> if this is a unconstrained hexadecimal
     *          code. 
     */
    public boolean isGeneralizedHexadecimal()
    {
        return isSimple() && maxSplit() <= 3;
    }
    
    /**
     * Returns <code>true</code> if this is a finite code (with
     * <code>d<sub>i</sub> == 0</code> for all sufficiently large
     * <code>i</code>). 
     *
     * @return  <code>true</code> if this is a finite code.
     */
    public boolean isFinite()
    {
        return period == 0;
    }
    
    /**
     * Returns <code>true</code> if this codes a short game.  This is
     * true provided that adding <code>i</code> tokens always requires
     * splitting into <code>i+2</code> or more heaps (for all
     * <code>i&ge;0</code>).
     *
     * @return <code>true</code> if this codes a short game.
     */
    public boolean isShort()
    {
        for (int nAdded = 0; nAdded <= nAdditiveDigits; nAdded++)
        {
            if (nAdded < 2 || ! (digitAt(-nAdded) instanceof SimpleDigit))
            {
                for (int nHeaps = 1; nHeaps <= nAdded + 1; nHeaps++)
                {
                    if (digitAt(-nAdded).hasPermissibleSplit(nHeaps))
                    {
                        return false;
                    }
                }
                continue;
            }
            SimpleDigit s = (SimpleDigit)digitAt(-nAdded);
            {
                for (int nHeaps = 1; nHeaps <= nAdded+1; nHeaps++)
                {
                    if (s.hasPermissibleSplit(nHeaps)) 
                    {
                        int puc=s.getPairwiseUnequalConstraints() & (1<<nHeaps);
                        if (puc==0) return false;
                        // Add nAdded tokens, remove (nHeaps-1)
                        // pairwise unequal heaps which must include
                        // nHeaps*(nHeaps-1)/2 tokens, does the heap
                        // shrink?
                        if (((nHeaps-1)*nHeaps)/2 < nAdded) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets the digit triple at the specified index, specifying the
     * splits allowed if <code>index</code> tokens are removed from a
     * heap.  If <code>index</code> is positive, this will be to the
     * right of the decimal point.  If <code>index</code> is zero or
     * negative, the digit triple comes from the left of the decimal
     * point, indicating the splits allowed if <code>-index</code>
     * tokens are added to a heap.
     * <p>
     * The <code>null</code> value is retuned if the digit triple is
     * <code>{0,0,0}</code>, specifying that no move is possible with
     * the removal or addition of that many tokens, or if
     * <code>index</code> to falls outside the range of the digits
     * specified for this code.
     *
     * @param   index The index of the digit.
     * @return  The digit triple at the specified index, or
     *          <code>null</code>. 
     */
    public Digit digitAt(int index)
    {
        index += nAdditiveDigits;
        if (index < 0) 
        {
            return null;
        }
        if (index >= digits.length)
        {
            if (period == 0)
            {
                return null;
            }
            index = digits.length
                - period
                + ((index-digits.length)%period);
        }
        return digits[index];
    }
    
    ////////////////////////////////////////////////////////////////
    // Standard form stuff, first hack
    
    /**
     * Test for standard form TBCode.
     *
     * A TBCode is in standard form if it cannot be reduced by taking
     * {@link #cousin()}.  This occurs if <ul>
     * <li>The game allows removing a single-token heap,</li>
     * <li>The game is the zero game, or</li>
     * <li>The game has no subtractive digits, and no additive digits
     *     involving splits (thus ensuring that heaps cannot be
     *     reduced in size).</li></ul>
     *
     * @return <code>true</code> if the {@link #cousin()} operation is
     *         undefined.
     */
     /*
    public boolean isInStandardForm()
    {
        if (ghostlyStandardForm != null)
        {
            return this == ghostlyStandardForm;
        }

        int[] dt1 = digitTripleAt(1);
        
        if ((dt1 == null || (dt1[INDEX_UNCONSTRAINED] & 1) == 0)
            && maxDigitCode() != 0)
        {
            if (period > 0
                || nAdditiveDigits < digitTriples.length-1
                || maxGeneralizedDigit() > 3)
            {
                return false;
            }
        }

        ghostlyStandardForm = this;
        return true;
    }*/

    /**
     * Find first cousin of a TBCode.
     *
     * @return first cousin of this TBCode
     * @throws IllegalArgumentException if the TBCode is already in
     *         standard form.
     */
     /*
    public TBCode cousin()
    {
        if (isInStandardForm()) {
            throw new IllegalArgumentException
                ("Attempt to take cousin of " + this);
        }
        int[][] newDigitTriples =
            new int[digitTriples.length + maxSplit() + 1][3];
        for (int i=0; i<digitTriples.length; ++i)
        {
            Arrays.fill(newDigitTriples[i],0);
        }
        
        for (int i=0; i<newDigitTriples.length; ++i)
        {
            int[] dT = digitTripleAt(i - nAdditiveDigits);

            for (int cType=0; cType<3; ++cType)
            {
                int d = dT==null ? 0 : dT[cType];
                for (int nParts=0;
                     d!=0 && i+nParts < newDigitTriples.length;
                     ++nParts)
                {
                    if ((d&1) != 0)
                    {
                        int[] nD = newDigitTriples[i+nParts];
                        addPossibility(nD,cType,nParts);

                        int lowUnconstrained;
                        switch (cType)
                        {
                        case INDEX_UNCONSTRAINED:
                            lowUnconstrained = 0;
                            break;
                        case INDEX_NOT_ALL_EQUAL:
                            lowUnconstrained = 1;
                            break;
                        case INDEX_PAIRWISE_UNEQUAL:
                            addPossibility(nD,
                                           (nParts==2 ?
                                            INDEX_UNCONSTRAINED :
                                            cType),
                                           nParts-1);
                            lowUnconstrained = nParts;
                            break;
                        default:
                            throw new AssertionError
                                ("Bad constraint "+cType);
                        }

                        for (int k=lowUnconstrained; k<nParts; ++k)
                        {
                            addPossibility(nD,INDEX_UNCONSTRAINED,k);
                        }
                    
                        newDigitTriples[i+nParts] = nD;
                    }
                    d = d >> 1;
                }
            }
        }
        return new TBCode(nAdditiveDigits + 1,
                          newDigitTriples,
                          period,
                          ghosts+1);
    }
    */
    
    /**
     * Computes the standard form of this code.
     *
     * @return The standard form of this code.
     */
    public TBCode standardForm()
    {
        return this;
    }
     /*
    public TBCode standardForm()
    {
        return ghostlyStandardForm().unGhosted();
    }
    */
    
    ////////////////////////////////////////////////////////////////
    // Tests for possible moves.
    /*
     * Tests whether it is permissible to remove the specified number
     * of tokens, leaving the specified number of heaps, assuming any
     * equality constraints are satisfied (as by having pairwise
     * unequal parts).
     *
     * @param   tokensRemoved The number of tokens to be removed.
     * @param   heapsLeft The number of heaps to leave.
     * @return  <code>true</code> if this <code>TBCode</code> permits
     *          the specified action.
     * @throws  IllegalArgumentException if the number of heaps is not
     *          in the range 0 through {@link #MAX_RESULTING_HEAPS}.
     */
     /*
    public boolean allowed(int tokensRemoved, int heapsLeft)
    {
        return allowed(tokensRemoved,heapsLeft,heapsLeft);
    }*/

    /*
     * Tests whether it is permissible to remove the specified number
     * of tokens, leaving the specified number of heaps, given that
     * there may be a pair of equal resulting heaps.  If there is
     * a pair that is equal, it is assumed that all the parts are
     * equal, so that a <code>NOT_ALL_EQUAL</code> constraint is also
     * unsatisfied.  (This is compatible with the most common splits,
     * into two parts.)
     *
     * @param   tokensRemoved The number of tokens to be removed.
     * @param   heapsLeft     The number of heaps to leave.
     * @param   equalParts    <code>true</code> if splitting into
     *                        parts, some of which are equal.
     * @return  <code>true</code> if this <code>TBCode</code> permits
     *          the  specified action.
     * @throws  IllegalArgumentException if the number of heaps is not
     *          in the range 0 through {@link #MAX_RESULTING_HEAPS}.
     *//*
    public boolean allowed(int tokensRemoved,
                           int heapsLeft,
                           boolean equalParts)
    {
        return allowed(tokensRemoved,
                       heapsLeft,
                       equalParts ? 1 : heapsLeft);
    }*/

    /*
     * Tests whether it is permissible to remove the specified number
     * of tokens, leaving the specified number of heaps, of the
     * specified number of distinct sizes.
     * <p>
     * The number of distinct sizes <code>distinctSizes</code> is
     * considered only if <code>heapsLeft &ge; 2</code>, and is used
     * to specify whether inequality constraints are satisfied.
     *
     * <ul><li>If <code>distinctSizes==heapsLeft</code> the split is
     *         into pairwise unequal parts.</li>
     *     <li>If <code>distinctSizes==1</code>, the split is into all
     *         equal parts.</li>
     *     <li>If <code>1 &lt; distinctSizes &lt; heapsLeft</code>,
     *         the split is into parts that are not all equal but not
     *         all distinct.  In this case the actual value of
     *         <code>distinctSizes</code> in this range is
     *         irrelevant.</li></ul>
     *
     * @param   tokensRemoved The number of tokens to be removed.
     * @param   heapsLeft     The number of heaps to leave.
     * @param   distinctSizes The number of distinct sizes of
     *                        resulting parts.
     * @return  <code>true</code> if this <code>TBCode</code> permits
     *          the specified action.
     * @throws  IllegalArgumentException if the <code>heapsLeft</code>
     *          is not in the range 0 through {@link
     *          #MAX_RESULTING_HEAPS}.
     */
     /*
    public boolean allowed(int tokensRemoved,
                           int heapsLeft,
                           int distinctSizes)
    {
        if (heapsLeft < 0 || heapsLeft > MAX_RESULTING_HEAPS)
        {
            throw new IllegalArgumentException
                ("heapsLeft < 0 || heapsLeft >= 16");
        }
        int digitTriple[] = digitTripleAt(tokensRemoved);

        if (digitTriple == null) return false;

        int mask = 1 << heapsLeft;
        
        if ((mask & digitTriple[INDEX_UNCONSTRAINED]) != 0)
        {
            return true;
        }

        if (heapsLeft < 2 || distinctSizes == 1) return false;

        if ((mask & digitTriple[INDEX_NOT_ALL_EQUAL]) != 0)
        {
            return true;
        }

        return (distinctSizes == heapsLeft
                && (mask & digitTriple[INDEX_PAIRWISE_UNEQUAL]) != 0);
    }*/

    @Override
    public List<int[]> allOptions(int heapSize)
    {
        ArrayList<int[]> allOptions = new ArrayList<int[]>();
        
        HeapRules.Traversal t = new TBTraversal(heapSize);
        while (t.advance())
        {
            int[] opt = new int[t.currentLength()];
            for (int i = 0; i < opt.length; i++)
            {
                opt[i] = t.currentPart(i);
            }
            allOptions.add(opt);
        }
        
        return allOptions;
    }
    
    @Override
    public HeapRules.Traversal traversal(int heapSize)
    {
        return new TBTraversal(heapSize);
    }
    
    @Override
    public APChecker getAPChecker()
    {
        APChecker apchecker = new APChecker();
        apchecker.setLinearCriteria(maxSplit(), maxSplit(), lastDigit() + maxSplit());
        apchecker.setMaxSaltus(0);
        return apchecker;
    }
    
    /**
     * Generalization of an individual digit of a take-and-break code.
     * A <code>Digit</code> specifies the possible 
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.20 $ $Date: 2008/01/06 21:18:25 $
     */
    public static interface Digit
    {
        /**
         * Gets the maximum number of heaps into which any number of
         * tokens may be split, according to this <code>Digit</code>.
         * A return of zero indicates that the move is legal only if
         * the heap is empty.  A return of -1 indicates that no move
         * is possible.
         * <p>
         * The return value may be higher than the actual maximum, but it
         * should never be lower.
         */
        public int maxSplit();
        
        /**
         * Gets the maximum number of heaps into which the specified
         * number of tokens may be split.  A return of zero indicates
         * that the move is legal only if the heap is empty.  A return
         * of -1 indicates that no move is possible.
         * <p>
         * The return value may be higher than the actual maximum, but
         * it should never be lower.  In particular, a return values
         * larger than <code>nTokens</code> is implicitly equivalent
         * to <code>nTokens</code>.
         */
        public int maxSplit(int nTokens);
        /**
         * Returns <code>true</code> if it is ever permissible to split
         * some number of tokens into the specified number of heaps.
         */
        public boolean hasPermissibleSplit(int nTargetHeaps);
        /**
         * Returns <code>true</code> if it is ever permissible to split the
         * specified number of tokens into the specified number of heaps.
         * <p>
         * It is ok for this method to return false positives, but it should
         * never return false negatives.  If it returns <code>true</code>,
         * then combinations of <code>nTargetHeaps</code> adding to
         * <code>nTokens</code> will be considered.  If it returns
         * <code>false</code>, they will be rejected without further
         * consideration.
         */
        public boolean hasPermissibleSplit(int nTokens, int nTargetHeaps);
        
        /**
         * Returns copy of this digit without the indicated permissible split.
         */
        public Digit withoutPermissibleSplit(int nTargetHeaps);

        /**
         * Returns <code>true</code> if there are constraints on splitting the
         * specified number of tokens into the specified number of heaps.
         * <p>
         * It is ok for this method to return false positives, but it should
         * never return false negatives.  If it returns <code>true</code>, then
         * every individual combination of <code>nTargetHeaps</code> adding to
         * <code>nTokens</code> will be checked by calling
         * {@link #isPermissibleSplit(int,int...) isPermissibleSplit}.  If it
         * returns <code>false</code>, then every such combination will be
         * assumed permissible.
         */
        public boolean hasConstraints(int nTokens, int nTargetHeaps);
        
        /**
         * Returns <code>true</code> if it is permissible to split the
         * specified number of tokens into the specified array of heaps.
         * The length of
         * <code>targetHeaps</code> may be greater than the actual number of
         * heaps represented; this allows larger-sized arrays to be reused
         * for efficiency.  Excess array entries may be padded with zeroes.
         * <p>
         * It will always be guaranteed that <code>nTokens</code> is exactly
         * equal to the sum of the entries of <code>targetHeaps</code>.
         * <p>
         * <b>Warning:</b> It is assumed that <code>targetHeaps</code> is
         * <i>nonincreasing</i>: that is, <code>targetHeaps[i] >= targetHeaps[j]</code>
         * whenever <code>i <= j</code>.  If <code>targetHeaps</code> fails to
         * satisfy this constraint, then the behavior of this method is
         * undefined.
         */
        public boolean isPermissibleSplit(int nTokens, int ... targetHeaps);
    }
    
    /**
     * A default implementation of {@link Digit} that models certain basic
     * types of constraints.  The currently supported constraints are:
     * <p>
     * <ul>
     *   <li>Unequal constraints - the heaps resulting from a split must
     *       not all be the same;
     *   <li>Pairwise unequal constraints - the heaps resulting from a split
     *       must be pairwise distinct.
     * </ul>
     * <p>
     * Each type of constraint may be independently applied to every possible
     * number of target heaps permitted by a <code>SimpleDigit</code>.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.20 $ $Date: 2008/01/06 21:18:25 $
     */
    public final static class SimpleDigit implements Digit
    {
        public final static SimpleDigit ZERO = new SimpleDigit(0, 0, 0);
        
        private int value, unequalConstraints, pairwiseUnequalConstraints;
        // maximum number of heaps that can result from a move, or -1
        // if no move is possible.
        private int maxSplit;
        
        private final static CodeDigit.Options SIMPLE_DIGIT_OPTIONS
            = new CodeDigit.Options(36, true, "?!", "()", -1, 0, 16);
        
        /**
         * Constructs a new <code>SimpleDigit</code> with the specified
         * value and no constraints.  This is equivalent to
         * <code>new SimpleDigit(value, 0, 0)</code>.
         */
        public SimpleDigit(int value)
        {
            this(value, 0, 0);
        }
        
        /**
         * Constructs a new <code>SimpleDigit</code> with the specified
         * value and the specified constraints.
         * <p>
         * If the <code>n<sup>th</sup></code> bit of <code>value</code> is 1
         * (that is, if <code>(value & (1 << n)) == 1</code>) then a split
         * into <code>n</code> heaps is permitted, unless restricted as
         * described below.
         * <p>
         * If the <code>n<sup>th</sup></code> bit of <code>unequalConstraints</code>
         * is 1, then splitting into <code>n</code> heaps is only permitted
         * if the heaps are not all equal.
         * <p>
         * If the <code>n<sup>th</sup></code> bit of <code>pairwiseUnequalConstraints</code>
         * is 1, then splitting into <code>n</code> heaps is only permitted
         * if the heaps are pairwise unequal.
         */
        public SimpleDigit(int value, int unequalConstraints, int pairwiseUnequalConstraints)
        {
            if (value < 0 || unequalConstraints < 0 || pairwiseUnequalConstraints < 0)
            {
                throw new IllegalArgumentException("All arguments must be positive.");
            }
            init(value, unequalConstraints, pairwiseUnequalConstraints);
        }
        
        public SimpleDigit(String str)
        {
            CodeDigit d = new CodeDigit(str, 0, SIMPLE_DIGIT_OPTIONS);
            init(
                d.digitVal[0],
                d.digitVal[1] == -1 ? d.digitVal[0] : d.digitVal[1],
                d.digitVal[2] == -1 ? d.digitVal[0] : d.digitVal[2]
                );
        }
        
        private void init(int value, int unequalConstraints, int pairwiseUnequalConstraints)
        {
            this.value = value | unequalConstraints | pairwiseUnequalConstraints;
            this.unequalConstraints = unequalConstraints & ~3 & ~pairwiseUnequalConstraints;
            this.pairwiseUnequalConstraints = pairwiseUnequalConstraints & ~3;
            if (this.unequalConstraints==4 || this.pairwiseUnequalConstraints != 0)
            {
                this.pairwiseUnequalConstraints |= (this.unequalConstraints & 4);
                this.unequalConstraints &= ~4;
            }
            // Cache the maxSplit
            maxSplit = -1;
            for (int i = value; i != 0; i >>= 1)
            {
                maxSplit++;
            }
        }

        public @Override boolean equals(Object obj)
        {
            return obj instanceof SimpleDigit &&
                value == ((SimpleDigit) obj).value &&
                unequalConstraints == ((SimpleDigit) obj).unequalConstraints &&
                pairwiseUnequalConstraints == ((SimpleDigit) obj).pairwiseUnequalConstraints
                ;
        }
        
        public @Override int hashCode()
        {
            return value + unequalConstraints * 197 + pairwiseUnequalConstraints * 291;
        }
        
        public @Override String toString()
        {
            return appendToStringBuilder(new StringBuilder()).toString();
        }

        public StringBuilder appendToStringBuilder(StringBuilder sb)
        {
            int[] params = new int[]
            {
                value,
                unequalConstraints != 0 && unequalConstraints == (value & ~3)
                ? -1 : unequalConstraints,
                pairwiseUnequalConstraints != 0 && pairwiseUnequalConstraints == (value & ~3)
                ? -1 : pairwiseUnequalConstraints
            };
            if (params[1]>0 && params[2]==4)
            {
                params[1]|=4;
                params[2]=0;
                if (params[1] == (value&~3)) params[1]=-1;
            }
            return new CodeDigit(params, SIMPLE_DIGIT_OPTIONS).appendToStringBuilder(sb);
        }

        public boolean isZero()
        {
            return maxSplit < 0;
        }

        /**
         * Gets the underlying value of this digit.
         */
        public int getValue()
        {
            return value;
        }
        
        /**
         * Gets the constraints indicating when at least two target heaps must
         * be unequal.
         */
        public int getUnequalConstraints()
        {
            return unequalConstraints;
        }
        
        /**
         * Gets the constraints indicating when all of the target heaps must
         * be pairwise unequal.
         */
        public int getPairwiseUnequalConstraints()
        {
            return pairwiseUnequalConstraints;
        }
        
        public int maxSplit()
        {
            return maxSplit;
        }
        
        public int maxSplit(int nTokens)
        {
            return maxSplit;
        }
        
        public boolean hasPermissibleSplit(int nTargetHeaps)
        {
            return (value & (1 << nTargetHeaps)) != 0;
        }
        
        public boolean hasPermissibleSplit(int nTokens, int nTargetHeaps)
        {
            return (value & (1 << nTargetHeaps)) != 0;
        }
        
        public SimpleDigit withoutPermissibleSplit(int nTargetHeaps)
        {
            if (hasPermissibleSplit(nTargetHeaps))
            {
                return new SimpleDigit(value & ~ (1 << nTargetHeaps),
                                       unequalConstraints & ~ (1 << nTargetHeaps),
                                       pairwiseUnequalConstraints & ~ (1 << nTargetHeaps));
            }
            return this;
        }

        public boolean hasConstraints()
        {
            return unequalConstraints != 0 || pairwiseUnequalConstraints != 0;
        }
        
        public boolean hasConstraints(int nTokens, int nTargetHeaps)
        {
            return ((unequalConstraints | pairwiseUnequalConstraints
                     ) & (1 << nTargetHeaps)
                    ) != 0;
        }
        
        public boolean isPermissibleSplit(int nTokens, int ... targetHeaps)
        {
            if (targetHeaps.length==0 || targetHeaps[0]==0) return (value&1) != 0;

            boolean existsUnequal = false, pairwiseUnequal = true;
            
            for (int nParts = 1; true; nParts++)
            {
                if (nParts >= targetHeaps.length || targetHeaps[nParts]==0)
                {
                    int bit = (1 << nParts);
            
                    if ((bit & value) == 0)
                    {
                        return false;
                    }
                    if (!pairwiseUnequal && (bit & pairwiseUnequalConstraints) != 0)
                    {
                        return false;
                    }

                    assert (unequalConstraints&3)==0:
                    "Zero or one heap might be prohibited by unequalConstraints";

                    if (!existsUnequal && (bit & unequalConstraints) != 0)
                    {
                        return false;
                    }
                    return true;
                }
                if (targetHeaps[nParts] == targetHeaps[nParts-1])
                {
                    pairwiseUnequal = false;
                }
                else
                {
                    existsUnequal = true;
                }
            }
        }
    }
    
    private class TBTraversal implements HeapRules.Traversal
    {
        private int heapSize;
        private int minTokensToRemove, maxTokensToRemove;
        
        private int tokensToRemove, tokensToRemain;
        private Digit currentDigit;
        private int currentMaxNumHeaps;
        private int currentNumHeaps;
        private boolean currentlyConstraintFree;
        private int[] currentHeaps;
        
        public TBTraversal(int heapSize)
        {
            this.heapSize = heapSize;
            this.maxTokensToRemove = Math.min(heapSize, lastDigit());
            this.tokensToRemove = firstDigit() - 1;
            this.currentMaxNumHeaps = -1;
            this.currentNumHeaps = -1;
            this.currentHeaps = new int[Math.min(maxSplit(), heapSize)];
        }
        
        public boolean advance()
        {
            while (tokensToRemove <= maxTokensToRemove)
            {
                if (tokensToRemove >= firstDigit())
                while (true)        // Loop for currentNumHeaps
                {
                    if (currentNumHeaps > 0)
                    while (true)    // Loop for currentHeaps
                    {
                        // Advance currentHeaps
                        
                        int tokensToDistribute = currentHeaps[currentNumHeaps-1];
                        int indexToIncrement;
                        
                        for (indexToIncrement = currentNumHeaps-2; indexToIncrement >= 0; indexToIncrement--)
                        {
                            tokensToDistribute += currentHeaps[indexToIncrement];
                            if (currentHeaps[indexToIncrement] < tokensToDistribute - currentNumHeaps + indexToIncrement + 1 &&
                                (indexToIncrement == 0 || currentHeaps[indexToIncrement] < currentHeaps[indexToIncrement-1]))
                            {
                                break;
                            }
                        }
                        
                        if (indexToIncrement == -1)
                        {
                            break;
                        }
                        
                        currentHeaps[indexToIncrement]++;
                        distributeTokensEvenly(tokensToDistribute - currentHeaps[indexToIncrement], currentHeaps, indexToIncrement+1, currentNumHeaps-indexToIncrement-1);
                        
                        if (currentlyConstraintFree || currentDigit.isPermissibleSplit(tokensToRemain, currentHeaps))
                        {
                            return true;
                        }
                    }
                    
                    // We reached the end of the possibilities for currentNumHeaps.
                    
                    do
                    {
                        currentNumHeaps++;
                    }
                    while (currentNumHeaps <= currentMaxNumHeaps &&
                           !currentDigit.hasPermissibleSplit(tokensToRemain, currentNumHeaps));
                    
                    if (currentNumHeaps > currentMaxNumHeaps)
                    {
                        break;
                    }
                    
                    if (currentNumHeaps == 0)
                    {
                        if (tokensToRemain == 0 && currentDigit.isPermissibleSplit(tokensToRemain, currentHeaps))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        // Set-up for new # of heaps
                        
                        currentlyConstraintFree = !currentDigit.hasConstraints(tokensToRemain, currentNumHeaps);
                        if (currentNumHeaps > currentHeaps.length)
                        {
                            currentHeaps = new int[currentNumHeaps * 3 / 2];
                        }
                        distributeTokensEvenly(tokensToRemain, currentHeaps, 0, currentNumHeaps);
                        
                        // See if the initial set-up is permissible
                        
                        if (currentlyConstraintFree || currentDigit.isPermissibleSplit(tokensToRemain, currentHeaps))
                        {
                            return true;
                        }
                    }
                }
                
                // We reached the end of the possibilities for tokensToRemove.
                
                tokensToRemove++;
                if (tokensToRemove <= maxTokensToRemove)
                {
                    tokensToRemain = heapSize - tokensToRemove;
                    currentDigit = digitAt(tokensToRemove);
                    currentMaxNumHeaps = Math.min(currentDigit.maxSplit(tokensToRemain), tokensToRemain);
                    currentNumHeaps = -1;
                    Arrays.fill(currentHeaps, 0);
                }
            }
            
            return false;
        }
        
        private void distributeTokensEvenly(int nTokens, int[] buckets, int firstIndex, int nBuckets)
        {
            for (int i = 0; i < nBuckets; i++)
            {
                buckets[firstIndex + i] = nTokens / nBuckets;
            }
            for (int i = 0; i < nTokens % nBuckets; i++)
            {
                buckets[firstIndex + i]++;
            }
        }
        
        public int currentLength()
        {
            return currentNumHeaps;
        }
        
        public int currentPart(int i)
        {
            return currentHeaps[i];
        }
    }
    
    public static void main(String[] args)
    {
        TBCode code = new TBCode(args[0]);
        System.out.println("Testing Heap(\"" + code +"\","+args[1]+")");
        for (int[] opt : code.allOptions(Integer.parseInt(args[1])))
        {
            System.out.println(Arrays.toString(opt));
        }
    }
    
    /**
     * For testing routine; a measure of how much work to do when
     * traversing options.
     */
    private static enum effort
    {
        TRAVERSE, // just traverse the options
        MEX,      // calculate MEXes
        PRINT     // print table of MEXes
    }

    /**
     * Testing routine.
     *
     * <ul><li>The first argument is the string representation of the
     *         <code>TBCode</code>. The TBCode is created and printed.
     *     <li>The second argument is the number of tokens to remove
     *         (or, if negative, the number to add).  If provided, the
     *         possible numbers of resulting heaps is  printed, along
     *         with the possible numbers of distinct sizes of those
     *         heaps, if constrained.
     *     <li>The third argument is the size of the heap to remove
     *         tokens from.  If supplied, the set of possible
     *         resulting heaps is printed.
     * </ul>
     * Alternatively, a performance test mode is available.  In this
     * case, 
     * <ul><li>The first argument is a string representation of a
     *         <code>TBCode</code>,
     *     <li>The second argument is the string <code>UPTO</code>,
     *         and 
     *     <li>The third argument is the maximum size heap to
     *         calculate.
     *     <li>The fourth argument is <code>PRINT</code>,
     *         <code>MEX</code>, or missing. 
     * </ul>
     * In the performance test mode, the iterator is called for all
     * heaps up to the maximum size.  If the fourth argument is
     * present, the Nim values of these heaps are calculated.  If
     * the fourth argument is <code>PRINT</code>, the Nim values are
     * printed in tabular form.
     *
     */
     /*
    public static void main(String[] args)
    {
        if (args.length == 0) {
            System.out.println
                ("Usage 1: code [ tokensRemoved [ from ] ]");
            System.out.println
                ("Usage 2: code UPTO maxHeap [ | MEX | PRINT ]");
            return;
        }
        
        TBCode code = new TBCode(args[0]);

        if (args.length > 2 && args[1].equals("UPTO")) 
        {
            effort effortLevel =
                (args.length==3)
                ? effort.TRAVERSE
                : (args[3].equals("PRINT")
                   ? effort.PRINT
                   : effort.MEX);
            int maxHeap = new Integer(args[2]).intValue();
            int gVals[] = new int[1+maxHeap];
            byte mexSet[] = new byte[16];

            StringBuilder buf = new StringBuilder(80);
            
            for (int heapSize=1; heapSize<=maxHeap; ++heapSize)
            {
                TBTraversal it
                    = new TBTraversal (heapSize, code);
                if (effortLevel==effort.PRINT && heapSize % 10 == 1) 
                {
                    if (buf.length() > 0) {
                        System.out.println(buf);
                        buf.setLength(0);
                    }
                    buf.append(String.format("%5d:",heapSize));
                }
            
                if (effortLevel != effort.TRAVERSE)
                    Arrays.fill(mexSet, (byte)0);

                int curMex=0;
                
                while (it.advance())
                {
                    int gVal=0;
                    for (int i=it.currentLength()-1; i>=0; --i)
                    {
                        gVal ^= gVals[it.currentPart(i)];
                    }
                    if (effortLevel!=effort.TRAVERSE)
                    {
                        if (gVal > curMex) 
                        {
                            mexSet[gVal]=1;
                        }
                        else if (gVal == curMex)
                        {
                            while (mexSet[++curMex]!=(byte)0)
                            //     continue 
                            ;
                        }
                    }
                }

                gVals[heapSize] = curMex;
                if (mexSet.length <= curMex*2) {
                    mexSet = new byte[2*mexSet.length];
                }
                if (effortLevel==effort.PRINT)
                    buf.append(String.format(" %5d",curMex));
            }
            if (effortLevel==effort.PRINT) System.out.println(buf);
            return;
        }

        System.out.println(code);
        System.out.print("nAdditiveDigits "+code.nAdditiveDigits+
                         " digitTriples [");
        String sep="";

        for (int i=0; i<code.digitTriples.length; ++i) 
        {
            System.out.print(sep +
                             Arrays.toString(code.digitTriples[i]));
            sep=",";
        }
        System.out.println("]");
        
        System.out.println("digits " + code.firstDigit()
                           + " through " + code.lastDigit()
                           + " ghosts " + code.ghosts);
        System.out.println(" maxcode " + code.maxDigitCode()
                           + " maxgd " + code.maxGeneralizedDigit()
                           + (code.isShort() ? " short" : " loopy" )
                           + (code.isInStandardForm()
                              ? " already in standard form"
                              : " gSF " + code.ghostlyStandardForm()));
        if (args.length == 1) {
            return;
        }
        int tokensRemoved = new Integer(args[1]).intValue();
        if (code.digitTripleAt(tokensRemoved)==null) {
            System.out.println("Can't "
                               +(tokensRemoved<0?"add ":"remove ")
                               +Math.abs(tokensRemoved)+" token"
                               +(Math.abs(tokensRemoved)==1?".":"s."));
            return;
        }
        System.out.println(((tokensRemoved < 0)?"Adding ":"Removing ")
                           + Math.abs(tokensRemoved) + " token"
                           + ((Math.abs(tokensRemoved)==1)?"":"s")
                           + " may result in");
        String lastStr=null;
        for (int heaps=0; heaps<MAX_RESULTING_HEAPS; ++heaps) {
            if (code.allowed(tokensRemoved, heaps)) {
                if(lastStr != null)
                {
                    System.out.println(lastStr+" or ");
                }
                lastStr=""+heaps+" heap"+(heaps==1?"":"s");
                if (! code.allowed(tokensRemoved,heaps,1)) {
                    String xlast=new String(" of ");
                    for (int dp=2;dp<heaps;++dp) {
                        if(code.allowed(tokensRemoved,heaps,dp))
                        {
                            lastStr += xlast+dp;
                            xlast=new String(", ");
                        }
                    }
                    lastStr += (xlast.equals(", ") ? " or " : xlast);

                    if (! code.allowed(tokensRemoved,heaps,heaps)) {
                        lastStr += "?!NOT ";
                    }
                    lastStr += heaps+" distinct sizes";
                }
            }
        }
        
        System.out.println(lastStr);
        if ( args.length == 2)
        {
            return;
        }
        
        int heapSize = new Integer(args[2]).intValue();
        
        System.out.println("Results for heap size " + heapSize);
        
        StringBuilder buf = new StringBuilder();
        
        int optNumber = 0;

        List<int[]> opl = code.allOptions(heapSize);

        TBTraversal trav
            = new TBTraversal (heapSize, code);

        for (int[] itOpt : new TBTraversal(heapSize, code))
        {
            if (! Arrays.equals(itOpt,opl.get(optNumber)))
            {
                flushBuf(buf,",");
                System.out.println("itOpt = "
                                   + Arrays.toString(itOpt));
                System.out.println("opl.get(" + optNumber + ")  = "
                                   + Arrays.toString
                                   (opl.get (optNumber)));
            }
            if (! trav.advance()) 
            {
                flushBuf(buf,",");
                System.out.println("!trav.advance() but itOpt = "
                                   + Arrays.toString(itOpt));
            }
            if (trav.currentLength() != itOpt.length) 
            {
                flushBuf(buf,",");
                System.out.println("trav.currentLength = "
                                   + trav.currentLength()
                                   + " but itOpt = "
                                   + Arrays.toString(itOpt));
            }
            
            for (int i=0; i< itOpt.length; ++i) 
            {
                if (itOpt[i] != trav.currentPart(i)) 
                {
                    flushBuf(buf,",");
                    System.out.println("itOpt["+i+"]="+itOpt[i]
                                       + " but trav.curp = "
                                       + trav.currentPart(i));
                }
            }

            appendBuf(buf, ", ", Arrays.toString(itOpt));

            optNumber++;
        }
        flushBuf(buf,"");
        if (optNumber != opl.size()) 
        {
            System.out.println("opl.size() = " + opl.size());
        }
        if (trav.advance()) 
        {
            System.out.println("advance() still returned true");
        }
        int ghostable = code.ghostlyStandardForm().nGhosts();
        if (ghostable > code.nGhosts())
        {
            System.out.println("Reduced results, ignoring unusable"+
                               " heaps smaller than "+(ghostable+1));
            List<int[]> deGhostedOpl =
                code.ghostlyStandardForm().allOptions(heapSize);

            for (int i=0; i<opl.size(); ++i) 
            {
                int[] opt = opl.get(i);
                
                appendBuf(buf,"",
                          "(processing "
                          + Arrays.toString(opt)
                          +")");
                
                for (int j=0; j<opt.length; ++j)
                {
                    if (opt[j] <= ghostable)
                    {
                        for (int k = j+1; k<opt.length; ++k)
                        {
                            if(opt[k] > ghostable)
                            {
                                opt[j++]=opt[k];
                            }
                        }
                        int[] nopt = new int[j];
                        System.arraycopy(opt,0,nopt,0,j);
                        opl.set(i,nopt);
                        opt = nopt;
                        
                        appendBuf(buf,"",
                                  "(reduced to "
                                  + Arrays.toString(opt)
                                  +")");
                        
                        break;
                    }
                }
                for (int j=0; j<=i; ++j)
                {
                    if(j==i)
                    {
                        appendBuf(buf, ", ", Arrays.toString(opt));

                        boolean missing=true;

                        for (int[] deGhostedOpt : deGhostedOpl)
                        {
                            if (Arrays.equals(opt, deGhostedOpt)) {
                                deGhostedOpl.remove(deGhostedOpt);
                                missing=false;
                                break;
                            }
                        }
                        if (missing)
                        {
                            appendBuf(buf, " ", "(missing)");
                        }
                    }
                    else if (Arrays.equals(opt,opl.get(j)))
                    {
                        break;
                    }
                }
            }
            flushBuf(buf,"");
            if (! deGhostedOpl.isEmpty())
            {
                System.out.println("Omitted "+deGhostedOpl.size()
                                   +" options of "+code.ghostlyStandardForm());
                for (int[] opt : deGhostedOpl) {
                    appendBuf(buf, ", ", Arrays.toString(opt));
                }
                flushBuf(buf,"");
            }
        }
    }
    
    private static void flushBuf(StringBuilder buf, String sep) 
    {
        if (buf.length() > 0) 
        {
            buf.append(sep);
            System.out.println(buf);
            buf.setLength(0);
        }
    }

    private static void appendBuf(StringBuilder buf, String sep, String dat)
    {
        final int LINELENGTH=72;
        
        if(buf.length() > 0) 
        {
            buf.append(sep);
            if (buf.length() + dat.length() > LINELENGTH) 
            {
                flushBuf(buf, "");
            }
        }
        buf.append(dat);
    }
    */
}
