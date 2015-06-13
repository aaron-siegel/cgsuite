/*
 * CanonicalGame.java
 *
 * Created on October 15, 2002, 4:53 PM
 * $Id: CanonicalGame.java,v 1.42 2007/08/16 20:52:52 asiegel Exp $
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

package org.cgsuite.core;


import org.cgsuite.exception.InputException;

import java.math.BigInteger;
import java.util.*;

// TODO criticalTemperatures

/**
 * A short combinatorial game in canonical
 * form.  Every option of a <code>CanonicalGame</code> is again a
 * <code>CanonicalGame</code>.  In addition, it is guaranteed that:
 * <ul>
 * <li>There are no dominated options.  That is, if G and H are left options
 * of the same <code>CanonicalGame</code>, it is guaranteed that G is not
 * less than or equal to H, and likewise for right options.
 * <li>There are no reversible options.  That is, if H is a left option of a
 * <code>CanonicalGame</code> G, then no right option of H is less than or
 * equal to G.  Likewise, if H is a right option of G, then no left option of
 * H is greater than or equal to G.
 * </ul>
 * As of version 0.5, <code>CanonicalGame</code> objects are merely references
 * to a memory-efficient static back-end.  For large calculations this results
 * in a five- to ten-fold increase in memory efficiency.  As a result, the data
 * will remain in memory even if no references remain to any
 * <code>CanonicalGame</code> objects.  All existing data about canonical games
 * can be destroyed by calling {@link #reinit() reinit}, which restores the
 * <code>CanonicalGame</code> class to its initial settings.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.42 $ $Date: 2007/08/16 20:52:52 $
 */
public final class CanonicalShortGameOps
{
    /*
     * As of version 0.5, all substantive information about canonical games is
     * stored in a single static int array.  This array is accessed in much the
     * same way as the byte array contained in a org.cgsuite.util.Cache object; see
     * the comments for org.cgsuite.util.Cache for more details.  An actual
     * CanonicalGame object contains just a single integer, essentially a
     * "virtual pointer" into the static array.
     *
     * Most of the CanonicalGame methods simply call a static method
     * that acts on virtual pointers into the static array.  This gives the
     * CanonicalGame code a distinctly "C-like" feel (though of course, this is
     * hidden behind a typical OO-style API) with some inevitable loss of code
     * transparency.  For a more object-oriented implementation of
     * CanonicalGame, refer to the source code for cgsuite 0.4.
     *
     * Every effort is made to maximize the memory efficiency of the int array.
     * This leads to a roughly tenfold increase in memory efficiency in
     * exchange for a reasonable performance deficit.  What follows is a
     * description of the structure of the int array ("data array").
     *
     * The data array consists of a series of sequential "records" of varying
     * length.  There are two types of records: option records and NUS records.
     * A "canonical game" is a pointer to an option record (that is, an index
     * into the data array where an option record begins).
     *
     * An option record is simply a list of left and right options, i.e., a
     * list of pointers to other option records.  A NUS record codes a number,
     * an up multiple, and a nimber.  If an option record describes a number-
     * up-star, then the corresponding NUS record *immediately* precedes the
     * option record in the data array.
     *
     * A NUS record consists of exactly three consecutive 32-bit ints:
     *   Index 0 - Hash chain pointer (more on this later)
     *   Index 1 - NUS descriptor
     *   Index 2 - Numerator
     * "Numerator" is just the numerator of the associated number.  The NUS
     * descriptor is broken down bitwise as follows:
     *   Bits 30-31 - Always STD_NUS_RECORD.
     *   Bits 25-29 - The *exponent* of the denominator (between 0 and 30).
     *   Bits 12-24 - The up multiple in twos complement form (between
     *                -4096 and 4095).  See note below.
     *   Bits  0-11 - The nimber (between 0 and 4095).
     * Note that the way the up multiple is stored has changed since version
     * 0.5: It is now stored as a 13-bit integer in twos complement.
     *
     * An option record consists of two 32-bit ints, followed by the lists of
     * left and right options:
     *   Index 0 - Hash chain pointer (more on this later)
     *   Index 1 - Option descriptor
     *   Indices 2+ - Options (32-bit pointers to other option records)
     * The option descriptor is broken down bitwise as follows:
     *   Bits 30-31 - Always STD_OPTIONS_RECORD.
     *   Bit  29    - 1 if this is a NUS, 0 otherwise.
     *   Bit  28    - 1 if this is known to be a NON-uptimal, 0 otherwise.
     *   Bits 14-27 - The number of left options (between 0 and 16383).
     *   Bits  0-13 - The number of right options (between 0 and 16383).
     * Thus the descriptor indicates the length of the record.
     *
     * Combinatorial Game Suite does not support games with more than 16383
     * options for one player.
     */

    ////////////////////////////////////////////////////////////////////////
    // Cache-related constants.

    private final static int
        STD_OPTIONS_RECORD = 0x00000000,
        EXT_OPTIONS_RECORD = 0x80000000,
        STD_NUS_RECORD = 0x40000000,
        EXT_NUS_RECORD = 0xc0000000,
        RECORD_TYPE_MASK = 0xc0000000,
        EXT_RECORD_MASK = 0x80000000,
        NUS_RECORD_MASK = 0x40000000;

    // Standard options record descriptor (still 1 bit free):
    // xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
    //   | |______________||_____________|
    //   |        |               |
    // Nus flag   |        Num right options
    //     Num left options
    private final static int
        IS_NUS_MASK = 0x20000000,
        IS_NON_UPTIMAL_MASK = 0x10000000,
        NUM_LO_MASK = 0x0fffc000,
        NUM_LO_SHIFT = 14,
        NUM_RO_MASK = 0x00003fff;

    // Standard Nus record descriptor:
    // xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
    //   |___||_____________||___________|
    //     |          |            |
    //  Denom.   Up multiple     Nimber
    private final static int
        DENOMINATOR_MASK = 0x3e000000,
        DENOMINATOR_SHIFT = 25,
        UP_MULTIPLE_MASK = 0x01fff000,
        UP_MULTIPLE_LEFTSHIFT = 7,
        UP_MULTIPLE_RIGHTSHIFT = 19,
        NIMBER_MASK = 0x00000fff,
        EXT_DENOMINATOR_MASK = ~RECORD_TYPE_MASK;
    // (Note: the method of extracting the up multiple is slightly different;
    // we do a left shift followed by a signed right shift.  This preserves the
    // twos complement arithmetic properly.)

    private final static int
        SECTOR_BITS = 18,
        SECTOR_SIZE = 1 << SECTOR_BITS,
        SECTOR_MASK = SECTOR_SIZE - 1;

    private final static int
        DEFAULT_INDEX_CAPACITY = 1 << 16,   // 256 KB (64K entries)
        DEFAULT_SECTOR_SLOTS = 16,
        UNUSED_BUCKET = -1;

    ////////////////////////////////////////////////////////////////////////
    // Cache data and initialization.

    private static int indexCapacity, indexMask;
    private static int[] index;
    private static int[][] data;
    private static int nextOffset = 0, nextSector = 1, totalRecords = 0;

    // Performance statistics (gathering these is not terribly costly):
    private static int totalGames = 0, nusGames = 0, largeNusGames = 0, maxChainDepth = 0, totalChainDepth = 0;

    ////////////////////////////////////////////////////////////////////////
    // Operations table.

    private final static int
        DEFAULT_OP_TABLE_SIZE = 1 << 18;

    private final static byte
        OPERATION_NONE = 0,
        OPERATION_SUM = 1,
        OPERATION_NEGATIVE = 2,
        OPERATION_BIRTHDAY = 3,
        OPERATION_ATOMIC_WEIGHT = 4,
        OPERATION_NORTON_MULTIPLY = 5,
        OPERATION_CONWAY_MULTIPLY = 6,
        OPERATION_ORDINAL_SUM = 7
        ;

    private static int opTableSize = DEFAULT_OP_TABLE_SIZE, opTableMask = opTableSize - 1;
    private static byte[] opTableOp;
    private static int[] opTableG, opTableH, opTableResult;

    static int ZERO_ID, STAR_ID, UP_ID, UP_STAR_ID, ONE_ID, TWO_ID, NEGATIVE_ONE_ID, NEGATIVE_TWO_ID;

    static
    {
        reinit();
    }
    
    private static Integer mkInteger(int n)
    {
        return SmallInteger$.MODULE$.apply(n);
    }
    
    private static Integer mkInteger(long n)
    {
        return Integer$.MODULE$.apply(n);
    }
    
    private static Integer mkInteger(BigInteger n)
    {
        return Integer$.MODULE$.apply(new scala.math.BigInt(n));
    }
    
    private static DyadicRationalNumber mkRational(int m, int n)
    {
        return DyadicRationalNumber$.MODULE$.apply(mkInteger(m), mkInteger(n));
    }
    
    private static DyadicRationalNumber mkRational(long m, int n)
    {
        return DyadicRationalNumber$.MODULE$.apply(mkInteger(m), mkInteger(n));
    }
    
    private static DyadicRationalNumber mkRational(BigInteger m, BigInteger n)
    {
        return DyadicRationalNumber$.MODULE$.apply(mkInteger(m), mkInteger(n));
    }
    
    private static DyadicRationalNumber MIN_SMALL = DyadicRationalNumber$.MODULE$.minSmall();
    private static DyadicRationalNumber MAX_SMALL = DyadicRationalNumber$.MODULE$.maxSmall();

    static int compareLike(int gId, int hId)
    {
        if (gId == hId)
            return 0;
        
        int cmp = birthday(gId) - birthday(hId);
        if (cmp != 0)
            return cmp;
        
        cmp = getNumLeftOptions(gId) - getNumLeftOptions(hId);
        if (cmp != 0)
            return cmp;
        
        cmp = getNumRightOptions(gId) - getNumRightOptions(hId);
        if (cmp != 0)
            return cmp;
        
        cmp = compareOptArrays(sortedLeftOptions(gId), sortedLeftOptions(hId));
        if (cmp != 0)
            return cmp;
        
        cmp = compareOptArrays(sortedRightOptions(gId), sortedRightOptions(hId));
        if (cmp != 0)
            return cmp;
        
        assert false : "gId != hId but options are identical";
        throw new RuntimeException("gId != hId but options are identical");
    }
    
    private static int compareOptArrays(java.lang.Integer[] gArray, java.lang.Integer[] hArray)
    {
        assert gArray.length == hArray.length;
        
        for (int i = 0; i < gArray.length; i++)
        {
            int cmp = compareLike(gArray[i], hArray[i]);
            if (cmp != 0)
                return cmp;
        }
        
        return 0;
    }
    
    private static java.lang.Integer[] sortedLeftOptions(int id)
    {
        java.lang.Integer[] array = new java.lang.Integer[getNumLeftOptions(id)];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = getLeftOption(id, i);
        }
        Arrays.sort(array, 0, array.length, LIKE_COMPARATOR);
        return array;
    }
    
    static java.lang.Integer[] sortedRightOptions(int id)
    {
        java.lang.Integer[] array = new java.lang.Integer[getNumRightOptions(id)];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = getRightOption(id, i);
        }
        Arrays.sort(array, 0, array.length, LIKE_COMPARATOR);
        return array;
    }

    static Comparator<java.lang.Integer> LIKE_COMPARATOR = new Comparator<java.lang.Integer>()
    {
        @Override
        public int compare(java.lang.Integer x, java.lang.Integer y)
        {
            return compareLike(x, y);
        }
    };

    static int getNegative(int id)
    {
        if (isNumberUpStar(id))
        {
            return constructNus(getNumberPart(id).unary_$minus(), -getUpMultiplePart(id), getNimberPart(id));
        }

        int result = lookupOpResult(OPERATION_NEGATIVE, id, -1);
        if (result != -1)
        {
            return result;
        }

        int[] newLeftOptions = new int[getNumRightOptions(id)],
              newRightOptions = new int[getNumLeftOptions(id)];

        for (int i = 0; i < newLeftOptions.length; i++)
        {
            newLeftOptions[i] = getNegative(getRightOption(id, i));
        }
        for (int i = 0; i < newRightOptions.length; i++)
        {
            newRightOptions[i] = getNegative(getLeftOption(id, i));
        }

        result = constructFromCanonicalOptions(newLeftOptions, newRightOptions);
        storeOpResult(OPERATION_NEGATIVE, id, -1, result);
        return result;
    }

    static int getNumLeftOptions(int id)
    {
        return (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & NUM_LO_MASK) >> NUM_LO_SHIFT;
    }

    static int getLeftOption(int id, int n)
    {
        return data[id >> SECTOR_BITS][(id+2+n) & SECTOR_MASK];
    }

    static int getNumRightOptions(int id)
    {
        return data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & NUM_RO_MASK;
    }

    static int getRightOption(int id, int n)
    {
        int numLO = (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & NUM_LO_MASK) >> NUM_LO_SHIFT;
        return data[id >> SECTOR_BITS][(id+2+numLO+n) & SECTOR_MASK];
    }

    static boolean isNumber(int id)
    {
        return isNumberUpStar(id) && getNimberPart(id) == 0 && getUpMultiplePart(id) == 0;
    }

    static boolean isInteger(int id)
    {
        // Integers can only be stored in small nus records, so this is safe.
        // The first clause checks that this is a small record for a NUS; the
        // second clause checks that it's an integer.
        return (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & (RECORD_TYPE_MASK | IS_NUS_MASK))
                == (STD_OPTIONS_RECORD | IS_NUS_MASK)
            && (data[id >> SECTOR_BITS][(id-2) & SECTOR_MASK] &
              (DENOMINATOR_MASK | UP_MULTIPLE_MASK | NIMBER_MASK)) == 0;
    }

    static boolean isNumberUpStar(int id)
    {
        return (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & IS_NUS_MASK) != 0;
    }
    
    private static boolean isKnownNonUptimal(int id)
    {
        return (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & IS_NON_UPTIMAL_MASK) != 0;
    }
    
    private static void setKnownNonUptimal(int id)
    {
        data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] |= IS_NON_UPTIMAL_MASK;
    }

    static boolean isNimber(int id)
    {
        return isNumberUpStar(id) && getNumberPart(id).equals(Values.zero()) && getUpMultiplePart(id) == 0;
    }

    static DyadicRationalNumber getNumberPart(int id)
    {
        if (isExtendedRecord(id))
        {
            // Extended record.
            int offset = getExtendedNusRecordOffset(id);
            int[] sector = data[offset >> SECTOR_BITS];
            int sectorOffset = (offset & SECTOR_MASK);

            byte[] numArray = new byte[sector[sectorOffset + 5]];
            for (int i = 0; i < numArray.length; i++)
            {
                numArray[i] = (byte) ((sector[sectorOffset + 6 + (i/4)] >> 8 * (i % 4)) & 0xff);
            }
            return mkRational(
                new BigInteger(numArray),
                BigInteger.ONE.shiftLeft(sector[sectorOffset + 1] & EXT_DENOMINATOR_MASK)
                );
        }
        else
        {
            // Standard record.
            return mkRational(getSmallNumeratorPart(id), 1 << getDenExpPart(id));
        }
    }

    private static int getSmallNumeratorPart(int id)
    {
        return data[id >> SECTOR_BITS][(id-1) & SECTOR_MASK];
    }

    private static int getDenExpPart(int id)
    {
        if (isExtendedRecord(id))
        {
            int offset = getExtendedNusRecordOffset(id);
            return data[offset >> SECTOR_BITS][(offset+1) & SECTOR_MASK] & EXT_DENOMINATOR_MASK;
        }
        else
        {
            return (data[id >> SECTOR_BITS][(id-2) & SECTOR_MASK] & DENOMINATOR_MASK) >> DENOMINATOR_SHIFT;
        }
    }

    static int getUpMultiplePart(int id)
    {
        if (isExtendedRecord(id))
        {
            int offset = getExtendedNusRecordOffset(id);
            return data[offset >> SECTOR_BITS][(offset+3) & SECTOR_MASK];
        }
        else
        {
            // Do a leftshift followed by a *signed* rightshift to get the
            // signed arithmetic right.
            return (data[id >> SECTOR_BITS][(id-2) & SECTOR_MASK] << UP_MULTIPLE_LEFTSHIFT)
                    >> UP_MULTIPLE_RIGHTSHIFT;
        }
    }

    static int getNimberPart(int id)
    {
        if (isExtendedRecord(id))
        {
            int offset = getExtendedNusRecordOffset(id);
            return data[offset >> SECTOR_BITS][(offset+4) & SECTOR_MASK];
        }
        else
        {
            return data[id >> SECTOR_BITS][(id-2) & SECTOR_MASK] & NIMBER_MASK;
        }
    }

    private static boolean isExtendedRecord(int id)
    {
        return (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & EXT_RECORD_MASK) != 0;
    }

    private static int getExtendedNusRecordOffset(int id)
    {
        return id + 2 + getNumLeftOptions(id) + getNumRightOptions(id);
    }

    static int birthday(int id)
    {
        if (isNumberUpStar(id))
        {
            int denExp = getDenExpPart(id);
            int upMag = Math.abs(getUpMultiplePart(id));
            int nimber = getNimberPart(id);

            int numberBirthday;
            if (isExtendedRecord(id))
            {
                BigInteger numMag = getNumberPart(id).numerator().bigIntValue().bigInteger().abs();
                numberBirthday = (denExp == 0 ? numMag.intValue() : 1 + numMag.shiftRight(denExp).intValue() + denExp);
            }
            else
            {
                int numMag = Math.abs(getSmallNumeratorPart(id));
                numberBirthday = (denExp == 0 ? numMag : 1 + (numMag >> denExp) + denExp);
            }

            int upStarBirthday;

            // Compute the birthday of the up-star part.
            // The birthday B of ^n*k is equal to:
            // * If n > 0 and k = 0, then B = n+1.  Otherwise:
            // * If n is odd and k != 1, then B = n+(k^1).  Otherwise:
            // * B = n+k.

            if (upMag > 0 && nimber == 0)
            {
                upStarBirthday = upMag + 1;
            }
            else if ((upMag & 1) == 1 && nimber != 1)
            {
                upStarBirthday = upMag + (nimber ^ 1);
            }
            else
            {
                upStarBirthday = upMag + nimber;
            }

            return numberBirthday + upStarBirthday;
        }

        int birthday = lookupOpResult(OPERATION_BIRTHDAY, id, -1);
        if (birthday != -1)
        {
            return birthday;
        }

        birthday = 0;
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            birthday = Math.max(birthday, birthday(getLeftOption(id, i)) + 1);
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            birthday = Math.max(birthday, birthday(getRightOption(id, i)) + 1);
        }

        storeOpResult(OPERATION_BIRTHDAY, id, -1, birthday);
        return birthday;
    }

    static int cool(int id, DyadicRationalNumber temperature, int temperatureId)
    {
        if (isInteger(id))
        {
            return id;
        }

        if (temperature(id).compareTo(temperature) < 0)
        {
            return constructNus(mean(id), 0, 0);
        }

        int[] newLeftOptions = new int[getNumLeftOptions(id)],
              newRightOptions = new int[getNumRightOptions(id)];

        for (int i = 0; i < newLeftOptions.length; i++)
        {
            newLeftOptions[i] = subtract
                (cool(getLeftOption(id, i), temperature, temperatureId), temperatureId);
        }
        for (int i = 0; i < newRightOptions.length; i++)
        {
            newRightOptions[i] = add
                (cool(getRightOption(id, i), temperature, temperatureId), temperatureId);
        }

        return constructFromOptions(newLeftOptions, newRightOptions);
    }

    static int heat(int id, int tId)
    {
        if (isNumber(id))
        {
            return id;
        }

        int[] newLeftOptions = new int[getNumLeftOptions(id)],
              newRightOptions = new int[getNumRightOptions(id)];

        for (int i = 0; i < newLeftOptions.length; i++)
        {
            newLeftOptions[i] = add(heat(getLeftOption(id, i), tId), tId);
        }
        for (int i = 0; i < newRightOptions.length; i++)
        {
            newRightOptions[i] = subtract(heat(getRightOption(id, i), tId), tId);
        }

        return constructFromOptions(newLeftOptions, newRightOptions);
    }

    static int overheat(int id, int sId, int tId)
    {
        if (isInteger(id))
        {
            return nortonMultiply(id, sId);        // G copies of s
        }

        int[] newLeftOptions = new int[getNumLeftOptions(id)],
              newRightOptions = new int[getNumRightOptions(id)];

        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            newLeftOptions[i] = add(overheat(getLeftOption(id, i), sId, tId), tId);
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            newRightOptions[i] = subtract(overheat(getRightOption(id, i), sId, tId), tId);
        }

        return constructFromOptions(newLeftOptions, newRightOptions);
    }

    static int diversity(int id)
    {
        return followerIds(id).size();
    }

    static Set<java.lang.Integer> followerIds(int id)
    {
        Set<java.lang.Integer> memory = new HashSet<java.lang.Integer>();
        makeFollowerIds(id, memory);
        return memory;
    }

    // TODO For number-up-stars, is this just birthday+1?
    // How about uptimals???
    static void makeFollowerIds(int id, Set<java.lang.Integer> memory)
    {
        if (!memory.contains(id))
        {
            memory.add(id);
            for (int i = 0; i < getNumLeftOptions(id); i++)
            {
                makeFollowerIds(getLeftOption(id, i), memory);
            }
            for (int i = 0; i < getNumRightOptions(id); i++)
            {
                makeFollowerIds(getRightOption(id, i), memory);
            }
        }
    }

    static int add(int gId, int hId)
    {
        if (isNumberUpStar(gId) && isNumberUpStar(hId))
        {
            if (isExtendedRecord(gId) || isExtendedRecord(hId))
            {
                // At least one of the terms is an extended NUS.  Do the
                // calculations using org.cgsuite.util.Rational.  This is SLOW, but
                // completely general and hopefully rare.
                return constructNus(
                    getNumberPart(gId).$plus(getNumberPart(hId)),
                    getUpMultiplePart(gId) + getUpMultiplePart(hId),
                    getNimberPart(gId) ^ getNimberPart(hId)
                    );
            }
            else
            {
                // Both are small NUS values.  Do the rational simplification
                // inline (rather than using org.cgsuite.util.Rational) for a
                // MASSIVE speed gain.
                int gNum = getSmallNumeratorPart(gId), hNum = getSmallNumeratorPart(hId),
                    gDen = getDenExpPart(gId), hDen = getDenExpPart(hId), sumDen;
                long sumNum;
                if (gDen >= hDen)
                {
                    sumDen = gDen;
                    sumNum = (gNum) + (((long) hNum) << (gDen-hDen));
                }
                else
                {
                    sumDen = hDen;
                    sumNum = (hNum) + (((long) gNum) << (hDen-gDen));
                }
                while ((sumNum & 1L) == 0L && sumDen > 0)
                {
                    sumNum >>= 1L;
                    sumDen -= 1;
                }
                if (sumNum <= java.lang.Integer.MAX_VALUE && sumNum >= java.lang.Integer.MIN_VALUE)
                {
                    return constructNus(
                        (int) sumNum,
                        sumDen,
                        getUpMultiplePart(gId) + getUpMultiplePart(hId),
                        getNimberPart(gId) ^ getNimberPart(hId)
                        );
                }
                else
                {
                    return constructNus(
                        mkRational(sumNum, sumDen),
                        getUpMultiplePart(gId) + getUpMultiplePart(hId),
                        getNimberPart(gId) ^ getNimberPart(hId)
                        );
                }
            }
        }

        int result = lookupOpResult(OPERATION_SUM, gId, hId);
        if (result != -1)
        {
            return result;
        }

        // We want to return { GL+H, G+HL | GR+H, G+HR } .

        int hStartLeftOption = isNumber(gId) ? 0 : getNumLeftOptions(gId),
            hStartRightOption = isNumber(gId) ? 0 : getNumRightOptions(gId);

        int[] newLeftOptions = new int
                [hStartLeftOption + (isNumber(hId) ? 0 : getNumLeftOptions(hId))],
            newRightOptions = new int
                [hStartRightOption + (isNumber(hId) ? 0 : getNumRightOptions(hId))];

        if (!isNumber(gId))    // By the number translation theorem
        {
            for (int i = 0; i < getNumLeftOptions(gId); i++)
            {
                newLeftOptions[i] = add(getLeftOption(gId, i), hId);
            }
            for (int i = 0; i < getNumRightOptions(gId); i++)
            {
                newRightOptions[i] = add(getRightOption(gId, i), hId);
            }
        }
        if (!isNumber(hId))  // By the number translation theorem
        {
            for (int i = 0; i < getNumLeftOptions(hId); i++)
            {
                newLeftOptions[i + hStartLeftOption] = add(gId, getLeftOption(hId, i));
            }
            for (int i = 0; i < getNumRightOptions(hId); i++)
            {
                newRightOptions[i + hStartRightOption] = add(gId, getRightOption(hId, i));
            }
        }

        result = constructFromOptions(newLeftOptions, newRightOptions);
        storeOpResult(OPERATION_SUM, gId, hId, result);
        return result;
    }

    static int subtract(int gId, int hId)
    {
        return add(gId, getNegative(hId));
    }

    static DyadicRationalNumber leftStop(int id)
    {
        if (isNumber(id))
        {
            return getNumberPart(id);
        }

        DyadicRationalNumber stop = MIN_SMALL;
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            stop = stop.max(rightStop(getLeftOption(id, i)));
        }
        return stop;
    }

    static DyadicRationalNumber rightStop(int id)
    {
        if (isNumber(id))
        {
            return getNumberPart(id);
        }

        DyadicRationalNumber stop = MAX_SMALL;
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            stop = stop.min(leftStop(getRightOption(id, i)));
        }
        return stop;
    }

    static int naiveAtomicWeight(int id)
    {
        if (isNumberUpStar(id))
        {
            return constructInteger(getUpMultiplePart(id));
        }

        int result = lookupOpResult(OPERATION_ATOMIC_WEIGHT, id, -1);
        if (result != -1)
        {
            return result;
        }

        int[] newLeftOptions = new int[getNumLeftOptions(id)],
              newRightOptions = new int[getNumRightOptions(id)];
        for (int i = 0; i < newLeftOptions.length; i++)
        {
            newLeftOptions[i] = add(naiveAtomicWeight(getLeftOption(id, i)), NEGATIVE_TWO_ID);
        }
        for (int i = 0; i < newRightOptions.length; i++)
        {
            newRightOptions[i] = add(naiveAtomicWeight(getRightOption(id, i)), TWO_ID);
        }

        // clone() is slow.
        int[] loCopy = new int[newLeftOptions.length],
              roCopy = new int[newRightOptions.length];
        System.arraycopy(newLeftOptions, 0, loCopy, 0, newLeftOptions.length);
        System.arraycopy(newRightOptions, 0, roCopy, 0, newRightOptions.length);
        int g0Id = constructFromOptions(loCopy, roCopy);

        if (isInteger(g0Id))
        {
            int farStarId = constructNus(0, 0, 0, farStar(id));
            boolean leqFS = leq(id, farStarId), geqFS = leq(farStarId, id);
            if (leqFS && !geqFS)
            {
                if (newLeftOptions.length == 0)
                {
                    result = ZERO_ID;
                }
                else
                {
                    // g < farStar.  Find the least integer n such that
                    // n |> every newLeftOption.
                    // Note: This could be optimized a bit.
                    DyadicRationalNumber maxLeastInteger = MIN_SMALL;
                    for (int i = 0; i < newLeftOptions.length; i++)
                    {
                        DyadicRationalNumber leastInteger = rightStop(newLeftOptions[i]).ceiling();
                        if (leq(constructNus(leastInteger, 0, 0), newLeftOptions[i]))
                        {
                            leastInteger = leastInteger.$plus(Values.one());
                        }
                        maxLeastInteger = maxLeastInteger.max(leastInteger);
                    }
                    result = constructNus(maxLeastInteger, 0, 0);
                }
            }
            else if (geqFS && !leqFS)
            {
                if (newRightOptions.length == 0)
                {
                    result = ZERO_ID;
                }
                else
                {
                    // g > farStar.  Now the greatest integer n such that
                    // n <| every newRightOption.
                    DyadicRationalNumber minGreatestInteger = MAX_SMALL;
                    for (int i = 0; i < newRightOptions.length; i++)
                    {
                        DyadicRationalNumber greatestInteger = leftStop(newRightOptions[i]).floor();
                        if (leq(newRightOptions[i], constructNus(greatestInteger, 0, 0)))
                        {
                            greatestInteger = greatestInteger.$minus(Values.one());
                        }
                        minGreatestInteger = minGreatestInteger.min(greatestInteger);
                    }
                    result = constructNus(minGreatestInteger, 0, 0);
                }
            }
            else
            {
                result = g0Id;
            }
        }
        else    // Not an integer
        {
            result = g0Id;
        }

        storeOpResult(OPERATION_ATOMIC_WEIGHT, id, -1, result);
        return result;
    }

    static int farStar(int id)
    {
        if (isNimber(id))
        {
            return getNimberPart(id) + 1;
        }
        int farStar = 1;
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            farStar = Math.max(farStar, farStar(getLeftOption(id, i)));
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            farStar = Math.max(farStar, farStar(getRightOption(id, i)));
        }
        return farStar;
    }

    static int companion(int id)
    {
        if (isNumberUpStar(id) && getNumberPart(id).equals(Values.zero()))
        {
            int nimber = getNimberPart(id);
            if (nimber >= 2)
            {
                return id;
            }
            else
            {
                return constructNus(0, 0, getUpMultiplePart(id), nimber ^ 1);
            }
        }
        boolean leq = leq(id, ZERO_ID);
        boolean geq = leq(ZERO_ID, id);
        int[] leftOptions = new int[getNumLeftOptions(id) + (geq ? 1 : 0)];
        int[] rightOptions = new int[getNumRightOptions(id) + (leq ? 1 : 0)];
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            leftOptions[i] = companion(getLeftOption(id, i));
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            rightOptions[i] = companion(getRightOption(id, i));
        }
        if (geq)
        {
            leftOptions[leftOptions.length-1] = ZERO_ID;
        }
        if (leq)
        {
            rightOptions[rightOptions.length-1] = ZERO_ID;
        }
        return constructFromOptions(leftOptions, rightOptions);
    }

    private static int starProjection(int id)
    {
        if (isNumber(id))
        {
            return id;
        }
        else if (isNumberUpStar(id) && getUpMultiplePart(id) == 0 && getNimberPart(id) == 1)
        {
            return constructNus(getNumberPart(id), 0, 0);
        }

        int[] newLeftOptions = new int[getNumLeftOptions(id)],
              newRightOptions = new int[getNumRightOptions(id)];

        for (int i = 0; i < newLeftOptions.length; i++)
        {
            newLeftOptions[i] = starProjection(getLeftOption(id, i));
        }
        for (int i = 0; i < newRightOptions.length; i++)
        {
            newRightOptions[i] = starProjection(getRightOption(id, i));
        }

        return constructFromOptions(newLeftOptions, newRightOptions);
    }

    static int rcf(int id)
    {
        if (isNumberUpStar(id))
        {
            return constructNus(getNumberPart(id), 0, 0);
        }
        else
        {
            return starProjection(heat(id, STAR_ID));
        }
    }
    
    static boolean leq(int gId, int hId)
    {
        if (gId == hId)
        {
            return true;
        }
        
        if (Thread.interrupted())
        {
            throw new RuntimeException("Calculation canceled by user.");
        }

        int[] gSector = data[gId >> SECTOR_BITS], hSector = data[hId >> SECTOR_BITS];
        int gSectorOffset = gId & SECTOR_MASK, hSectorOffset = hId & SECTOR_MASK;
        int gDescriptor = gSector[gSectorOffset+1], hDescriptor = hSector[hSectorOffset+1];

        if ((gDescriptor & hDescriptor & IS_NUS_MASK) != 0)
        {
            // First compare the numbers.
            int cmp = compareNumberParts(gId, hId);
            if (cmp < 0)
            {
                return true;
            }
            else if (cmp > 0)
            {
                return false;
            }
            else
            {
                // The number parts are equal.
                int gUpMultiple = getUpMultiplePart(gId), hUpMultiple = getUpMultiplePart(hId);
                if (gUpMultiple < hUpMultiple - 1)
                {
                    return true;
                }
                else if (gUpMultiple < hUpMultiple)
                {
                    return (getNimberPart(gId) ^ getNimberPart(hId)) != 1;
                }
                else
                {
                    // Either H has a higher up multiple, or their up multiples are the same.
                    // In the latter case, the nimbers must be different (since G and H are
                    // already known to be distinct), so they must be incomparable.
                    return false;
                }
            }
        }

        // They're not number-up-stars, so compare them the hard way.

        boolean leq = true;

        // Return false if H <= GL for some left option GL of G
        //              or HR <= G for some right option HR of H.
        // Otherwise return true.

        // By the number avoidance theorem, we need only check g's options if
        // g is not a number.
        if (!isNumber(gId))
        {
            int numLO = (gDescriptor & NUM_LO_MASK) >> NUM_LO_SHIFT;
            for (int i = 0; i < numLO; i++)
            {
                if (leq(hId, gSector[gSectorOffset+2+i]))
                {
                    leq = false;
                    break;
                }
            }
        }
        if (leq && !isNumber(hId))  // (Can skip this if leq is already false or h is a number)
        {
            int numLO = (hDescriptor & NUM_LO_MASK) >> NUM_LO_SHIFT,
                numRO = (hDescriptor & NUM_RO_MASK);
            for (int i = 0; i < numRO; i++)
            {
                if (leq(hSector[hSectorOffset+2+numLO+i], gId))
                {
                    leq = false;
                    break;
                }
            }
        }

        return leq;
    }

    static int[] incentives(int id, boolean left, boolean right)
    {
        int numLeftIncentiveIds = (left ? getNumLeftOptions(id) : 0);
        int numRightIncentiveIds = (right ? getNumRightOptions(id) : 0);
        int[] incentiveIds = new int[numLeftIncentiveIds + numRightIncentiveIds];
        for (int i = 0; i < numLeftIncentiveIds; i++)
        {
            incentiveIds[i] = subtract(getLeftOption(id, i), id);
        }
        for (int i = 0; i < numRightIncentiveIds; i++)
        {
            incentiveIds[numLeftIncentiveIds + i] = subtract(id, getRightOption(id, i));
        }
        eliminateDuplicateOptions(incentiveIds);
        eliminateDominatedOptions(incentiveIds, true);
        return pack(incentiveIds);
    }
    
    static int nortonMultiply(int gId, int uId)
    {
        int result = lookupOpResult(OPERATION_NORTON_MULTIPLY, gId, uId);
        if (result != -1)
        {
            return result;
        }

        result = ZERO_ID;

        if (isInteger(gId))
        {
            int multiple = getSmallNumeratorPart(gId),
                positiveMultiple = multiple < 0 ? -multiple : multiple;
            int binarySum = multiple < 0 ? getNegative(uId) : uId;

            // We use a "binary addition" algorithm.
            for (int power2 = 0; positiveMultiple >> power2 != 0; power2++)
            {
                if (power2 > 0)
                {
                    binarySum = add(binarySum, binarySum);
                }
                if ((positiveMultiple & (1 << power2)) != 0)
                {
                    result = add(result, binarySum);
                }
            }
        }
        else
        {
            int[] uPlusIncentives = incentives(uId, true, true);
            for (int i = 0; i < uPlusIncentives.length; i++)
            {
                uPlusIncentives[i] = add(uId, uPlusIncentives[i]);
            }
            int[]
                newLeftOptions = new int[getNumLeftOptions(gId) * uPlusIncentives.length],
                newRightOptions = new int[getNumRightOptions(gId) * uPlusIncentives.length];

            for (int i = 0; i < getNumLeftOptions(gId); i++)
            {
                int glDotU = nortonMultiply(getLeftOption(gId, i), uId);
                for (int j = 0; j < uPlusIncentives.length; j++)
                {
                    newLeftOptions[i * uPlusIncentives.length + j] = add(glDotU, uPlusIncentives[j]);
                }
            }
            for (int i = 0; i < getNumRightOptions(gId); i++)
            {
                int grDotU = nortonMultiply(getRightOption(gId, i), uId);
                for (int j = 0; j < uPlusIncentives.length; j++)
                {
                    newRightOptions[i * uPlusIncentives.length + j] = subtract(grDotU, uPlusIncentives[j]);
                }
            }

            result = constructFromOptions(newLeftOptions, newRightOptions);
        }

        storeOpResult(OPERATION_NORTON_MULTIPLY, gId, uId, result);
        return result;
    }

    static int conwayMultiply(int gId, int hId)
    {
        if (isNimber(gId) && isNimber(hId))
        {
            int nimProduct = mkInteger(getNimberPart(gId)).nimProduct(mkInteger(getNimberPart(hId))).intValue();
            return constructNus(Values.zero(), 0, nimProduct);
        }

        int result = lookupOpResult(OPERATION_CONWAY_MULTIPLY, gId, hId);
        if (result != -1)
        {
            return result;
        }

        int gll = getNumLeftOptions(gId), grl = getNumRightOptions(gId),
            hll = getNumLeftOptions(hId), hrl = getNumRightOptions(hId);
        int newLeftOptions[] = new int[gll * hll + grl * hrl],
            newRightOptions[] = new int[gll * hrl + grl * hll];

        for (int i = 0; i < gll; i++)
        {
            for (int j = 0; j < hll; j++)
            {
                newLeftOptions[i * hll + j] =
                    subtract(
                        add(conwayMultiply(getLeftOption(gId, i), hId),
                             conwayMultiply(gId, getLeftOption(hId, j))),
                        conwayMultiply(getLeftOption(gId, i), getLeftOption(hId, j))
                        );
            }
        }
        for (int i = 0; i < grl; i++)
        {
            for (int j = 0; j < hrl; j++)
            {
                newLeftOptions[gll * hll + i * hrl + j] =
                    subtract(
                        add(conwayMultiply(getRightOption(gId, i), hId),
                             conwayMultiply(gId, getRightOption(hId, j))),
                        conwayMultiply(getRightOption(gId, i), getRightOption(hId, j))
                        );
            }
        }
        for (int i = 0; i < gll; i++)
        {
            for (int j = 0; j < hrl; j++)
            {
                newRightOptions[i * hrl + j] =
                    subtract(
                        add(conwayMultiply(getLeftOption(gId, i), hId),
                             conwayMultiply(gId, getRightOption(hId, j))),
                        conwayMultiply(getLeftOption(gId, i), getRightOption(hId, j))
                        );
            }
        }
        for (int i = 0; i < grl; i++)
        {
            for (int j = 0; j < hll; j++)
            {
                newRightOptions[gll * hrl + i * hll + j] =
                    subtract(
                        add(conwayMultiply(getRightOption(gId, i), hId),
                             conwayMultiply(gId, getLeftOption(hId, j))),
                        conwayMultiply(getRightOption(gId, i), getLeftOption(hId, j))
                        );
            }
        }

        result = constructFromOptions(newLeftOptions, newRightOptions);
        storeOpResult(OPERATION_CONWAY_MULTIPLY, gId, hId, result);
        return result;
    }

    static int ordinalSum(int gId, int hId)
    {
        if (isNumberUpStar(gId) && getUpMultiplePart(gId) == 0 && isNimber(hId))
        {
            return constructNus(getNumberPart(gId), 0, getNimberPart(gId) + getNimberPart(hId));
        }

        int result = lookupOpResult(OPERATION_ORDINAL_SUM, gId, hId);
        if (result != -1)
        {
            return result;
        }

        int gLO = getNumLeftOptions(gId), gRO = getNumRightOptions(gId);
        int[] newLeftOptions = new int[gLO + getNumLeftOptions(hId)],
              newRightOptions = new int[gRO + getNumRightOptions(hId)];

        for (int i = 0; i < gLO; i++)
        {
            newLeftOptions[i] = getLeftOption(gId, i);
        }
        for (int i = 0; i < getNumLeftOptions(hId); i++)
        {
            newLeftOptions[gLO+i] = ordinalSum(gId, getLeftOption(hId, i));
        }
        for (int i = 0; i < gRO; i++)
        {
            newRightOptions[i] = getRightOption(gId, i);
        }
        for (int i = 0; i < getNumRightOptions(hId); i++)
        {
            newRightOptions[gRO+i] = ordinalSum(gId, getRightOption(hId, i));
        }

        result = constructFromOptions(newLeftOptions, newRightOptions);
        storeOpResult(OPERATION_ORDINAL_SUM, gId, hId, result);
        return result;
    }

    static boolean isAllSmall(int id)
    {
        if (isNumberUpStar(id))
        {
            return getNumberPart(id).equals(Values.zero());
        }
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            if (!isAllSmall(getLeftOption(id, i)))
            {
                return false;
            }
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            if (!isAllSmall(getRightOption(id, i)))
            {
                return false;
            }
        }
        return true;
    }

    static boolean isEven(int id)
    {
        if (isNumberUpStar(id))
        {
            return !isExtendedRecord(id) && getUpMultiplePart(id) == 0 && getNimberPart(id) <= 1 && getDenExpPart(id) == 0 &&
                (getSmallNumeratorPart(id) + getNimberPart(id)) % 2 == 0;
        }
        else
        {
            for (int i = 0; i < getNumLeftOptions(id); i++)
            {
                if (!isOdd(getLeftOption(id, i)))
                {
                    return false;
                }
            }
            for (int i = 0; i < getNumRightOptions(id); i++)
            {
                if (!isOdd(getRightOption(id, i)))
                {
                    return false;
                }
            }
            return true;
        }
    }

    static boolean isOdd(int id)
    {
        if (isNumberUpStar(id))
        {
            return !isExtendedRecord(id) && getUpMultiplePart(id) == 0 && getNimberPart(id) <= 1 && getDenExpPart(id) == 0 &&
                (getSmallNumeratorPart(id) + getNimberPart(id)) % 2 == 1;
        }
        else
        {
            for (int i = 0; i < getNumLeftOptions(id); i++)
            {
                if (!isEven(getLeftOption(id, i)))
                {
                    return false;
                }
            }
            for (int i = 0; i < getNumRightOptions(id); i++)
            {
                if (!isEven(getRightOption(id, i)))
                {
                    return false;
                }
            }
            return true;
        }
    }
    
    static boolean isEvenTempered(int id)
    {
        if (isNumberUpStar(id))
        {
            return getUpMultiplePart(id) == 0 && getNimberPart(id) == 0;
        }
        else
        {
            for (int i = 0; i < getNumLeftOptions(id); i++)
            {
                if (!isOddTempered(getLeftOption(id, i)))
                {
                    return false;
                }
            }
            for (int i = 0; i < getNumRightOptions(id); i++)
            {
                if (!isOddTempered(getRightOption(id, i)))
                {
                    return false;
                }
            }
            return true;
        }
    }

    static boolean isOddTempered(int id)
    {
        if (isNumberUpStar(id))
        {
            return getUpMultiplePart(id) == 0 && getNimberPart(id) == 1;
        }
        else
        {
            for (int i = 0; i < getNumLeftOptions(id); i++)
            {
                if (!isEvenTempered(getLeftOption(id, i)))
                {
                    return false;
                }
            }
            for (int i = 0; i < getNumRightOptions(id); i++)
            {
                if (!isEvenTempered(getRightOption(id, i)))
                {
                    return false;
                }
            }
            return true;
        }
    }

    private static void addFollowers(int id, Set<java.lang.Integer> followers)
    {
        if (!followers.contains(id))
        {
            followers.add(id);
            for (int i = 0; i < getNumLeftOptions(id); i++)
            {
                addFollowers(getLeftOption(id, i), followers);
            }
            for (int i = 0; i < getNumRightOptions(id); i++)
            {
                addFollowers(getRightOption(id, i), followers);
            }
        }
    }

    // TODO Caching?
    static BigInteger stopCount(int id)
    {
        // TODO: Quick stopCount for ^n
        if (isNumberUpStar(id) && getUpMultiplePart(id) == 0)
        {
            if (getNimberPart(id) == 0)
            {
                return BigInteger.ONE;
            }
            else
            {
                return BigInteger.valueOf(2).multiply(
                    BigInteger.valueOf(3).pow(getNimberPart(id)-1)
                    );
            }
        }
        BigInteger count = BigInteger.ZERO;
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            count = count.add(stopCount(getLeftOption(id, i)));
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            count = count.add(stopCount(getRightOption(id, i)));
        }
        return count;
    }

    /*
     * Calculates an "orthodox form" for this game.  This is obtained by
     * replacing each option with an orthodox form, and then reducing each
     * player's options to a minimal set of orthodox options.  The orthodox
     * form will therefore have the same thermograph as this game.
     * <p>
     * Note that orthodox forms are <i>not</i> unique.
     *
     * @return  An orthodox form for this game.
    public CanonicalGame orthodoxForm()
    {
        return createFromId(orthodoxForm(id));
    }

    private static int orthodoxForm(int id)
    {
        int[] leftOrthodoxForms = new int[getNumLeftOptions(id)];
        int[] rightOrthodoxForms = new int[getNumRightOptions(id)];
        for (int i = 0; i < leftOrthodoxForms.length; i++)
        {
            leftOrthodoxForms[i] = orthodoxForm(getLeftOption(id, i));
        }
        for (int i = 0; i < rightOrthodoxForms.length; i++)
        {
            rightOrthodoxForms[i] = orthodoxForm(getRightOption(id, i));
        }
        int tempId = constructFromOptions(leftOrthodoxForms, rightOrthodoxForms);
        return constructFromOptions(findOrthodoxOptions(tempId, true), findOrthodoxOptions(tempId, false));
    }
    */
    /*
    private static int[] findOrthodoxOptions(int id, boolean left)
    {
        Thermograph[] therms = new Thermograph[left ? getNumLeftOptions(id) : getNumRightOptions(id)];
        for (int i = 0; i < therms.length; i++)
        {
            therms[i] = (left ? thermograph(getLeftOption(id, i)) : thermograph(getRightOption(id, i)));
        }
        LinkedList<Integer> orthodoxOptions = new LinkedList<Integer>();
        for (int i = therms.length-1; i >= 0; i--)
        {
            // Check to see if option i is orthodox.  It's orthodox if there is
            // at least one "relevant temperature" for which it is strictly
            // better than all options still in the list.  Relevant temperatures
            // include:
            // (i)   the temperature of this game;
            // (ii)  0;
            // (iii) the option's even-indexed critical temperatures < the
            //       temperature of this game.
            if (dominantAtTemperature(therms, left, i, temperature(id)) ||
                dominantAtTemperature(therms, left, i, Values.zero()))
            {
                orthodoxOptions.add(left ? getLeftOption(id, i) : getRightOption(id, i));
            }
            else
            {
                boolean added = false;
                // Look at the *right* wall of the *left* options, and
                // the *left* wall of the *right* options.
                Trajectory wall = (left ? therms[i].getRightWall() : therms[i].getLeftWall());
                for (int k = 0; k < wall.getNumCriticalPoints(); k += 2)
                {
                    if (wall.getCriticalPoint(k).compareTo(temperature(id)) < 0 &&
                        wall.getCriticalPoint(k).compareTo(Values.zero()) > 0 &&
                        dominantAtTemperature(therms, left, i, wall.getCriticalPoint(k)))
                    {
                        orthodoxOptions.add(left ? getLeftOption(id, i) : getRightOption(id, i));
                        added = true;
                        break;
                    }
                }
                if (!added)
                {
                    therms[i] = null;
                }
            }
        }
        int[] orthodoxOptionsArray = new int[orthodoxOptions.size()];
        int index = 0;
        for (int option : orthodoxOptions)
        {
            orthodoxOptionsArray[index++] = option;
        }
        return orthodoxOptionsArray;
    }

    // Checks if the thermograph at index i is dominant at temperature temp.
    // It's dominant provided it's strictly better than all other options still
    // in the list.
    private static boolean dominantAtTemperature
        (Thermograph[] therms, boolean left, int i, DyadicRationalNumber temp)
    {
        for (int j = 0; j < therms.length; j++)
        {
            if (j != i && therms[j] != null &&
                  (left  && (therms[i].getRightWall().valueAt(temp).compareTo
                             (therms[j].getRightWall().valueAt(temp)) <= 0) ||
                   !left && (therms[i].getLeftWall().valueAt(temp).compareTo
                             (therms[j].getLeftWall().valueAt(temp)) >= 0))
                )
            {
                return false;
            }
        }
        return true;
    }
    */

    /**
     * Constructs a superstar with the specified exponents.
     * This is defined as
     * <p>
     * <code>&uarr;<sup>a,b,c,...</sup> = {0,*,...,*m | *a,*b,*c,...}</code>
     * where <code>m = mex{a,b,c,...}</code>.
     *
     * @param   exponents The integers for the superstar exponents.
     * @return  The corresponding superstar.
     * @throws  IllegalArgumentException <code>exponents</code> is empty or
     *          contains a negative integer.
     */
    public static int superstar(int ... exponents)
    {
        if (exponents.length == 0)
        {
            throw new IllegalArgumentException("Exponent set cannot be empty.");
        }
        int[] rightOptions = new int[exponents.length];
        for (int i = 0; i < exponents.length; i++)
        {
            rightOptions[i] = constructNus(0, 0, 0, exponents[i]);
        }
        Arrays.sort(rightOptions);
        int mex = 0;
        for (int j = 0; j < rightOptions.length; j++)
        {
            if (j < rightOptions.length-1 && rightOptions[j] == rightOptions[j+1])
            {
                rightOptions[j] = -1;
            }
            else if (getNimberPart(rightOptions[j]) == mex)
            {
                // Ok to do it this way since options are sorted
                mex++;
            }
        }
        int[] leftOptions = new int[mex+1];
        for (int i = 0; i <= mex; i++)
        {
            leftOptions[i] = constructNus(0, 0, 0, i);
        }
        return constructFromCanonicalOptions(leftOptions, pack(rightOptions));
    }

    public static UptimalExpansion uptimalExpansion(int id)
    {
        if (isKnownNonUptimal(id))
        {
            return null;
        }

        if (isNumberUpStar(id))
        {
            return new UptimalExpansion(getNumberPart(id), getNimberPart(id), getUpMultiplePart(id));
        }

        if (UPTIMAL_MAP.containsKey(id))
        {
            return UPTIMAL_MAP.get(id);
        }

        UptimalExpansion ue = uptimalExpansion2(id);
        if (ue == null)
        {
            ue = uptimalExpansion2(getNegative(id));
            if (ue != null)
                ue = ue.negate();
        }

        if (ue == null)
        {
            setKnownNonUptimal(id);
        }
        else
        {
            UPTIMAL_MAP.put(id, ue);
        }

        return ue;
    }

    private static Map<java.lang.Integer,UptimalExpansion> UPTIMAL_MAP;

    private static UptimalExpansion uptimalExpansion2(int id)
    {

        // We are checking to see whether this has the uptimal expansion
        // G = *m . a_1a_2...a_k (a_k != 0).
        // We assume that a_k > 0. (The a_k < 0 case will be checked separately
        // by examining the negative.)
        // Then all options are uptimals, and there is exactly one Right option,
        // G^R = *(m#1) . a_1-a_2-...a_k-
        // The possibilities are:
        // (a)  If a_k = 1 and any a_i < 0, then Left has exactly two options:
        //      *(m#1) . a_1+a_2+...a_j+    and
        //      *m . a_1a_2...a_{k-1}
        //      where j is the largest i with a_i < 0.
        // (a') If a_k >= 2 and any a_i < 0, then Left has exactly two options:
        //      *(m#1) . a_1+a_2+...a_j+    and
        //      *m . a_1a_2...a_J
        // (b)  If a_k = 1 and a_1 = 0, then Left's options are
        //      0, *, *2, ..., *(m-1)    and
        //      *m . a_1a_2...a_{k-1}
        // (b') If a_k >= 2 and a_1 = 0, then Left's options are
        //      0, *, *2, ..., *(m-1)    and
        //      *m . a_1a_2...a_J
        // (c)  If a_1 = 1, m = 1, and (a_1,a_2,...,a_k) <= (1,1,...,1), then
        //      0    and
        //      *m . a_1a_2...a_{k-1}
        // (c') Just like all the others for a_k >= 2
        // Otherwise, either a_1 > 1, or m != 1, or (a_1,a_2,...,a_k) > (1,1,...,1)
        // (d)  If a_k = 1, then Left's only option is
        //      *m . a_1a_2...a_{k-1}
        // (d') If a_k >= 2, then Left's only option is
        //      *m . a_1a_2...a_J
        //      where J is the largest i with a_i <= 0.
        //      If no such i exists, then Left's option is always 0.

        if (getNumRightOptions(id) > 1)
        {
            return null;
        }
        UptimalExpansion rExp = uptimalExpansion(getRightOption(id, 0));
        if (rExp == null)
        {
            return null;
        }
        int maxLExpLength = 0;
        UptimalExpansion[] lExp = new UptimalExpansion[getNumLeftOptions(id)];
        for (int i = 0; i < lExp.length; i++)
        {
            lExp[i] = uptimalExpansion(getLeftOption(id, i));
            if (lExp[i] == null || !lExp[i].getNumberPart().equals(rExp.getNumberPart()))
            {
                return null;
            }
            maxLExpLength = Math.max(maxLExpLength, lExp[i].length());
        }

        //System.out.println("Validating for " + CanonicalShortGame$.MODULE$.apply(id) + ":");

        if (rExp.length() > 0 && rExp.getCoefficient(rExp.length()) > 0)
        {
            // Try the a_k > 1 case.
            UptimalExpansion putativeExpansion1 = rExp.increment(true, rExp.length());
            if (isValidUptimalExpansion(putativeExpansion1, lExp, rExp))
            {
                //System.out.println("Confirmed " + putativeExpansion1 + " for " + CanonicalShortGame$.MODULE$.apply(id) + ".");
                return putativeExpansion1;
            }
        }

        // Try the a_k = 1 case.
        UptimalExpansion putativeExpansion2 = rExp.increment(true, Math.max(rExp.length(), maxLExpLength) + 1);
        if (isValidUptimalExpansion(putativeExpansion2, lExp, rExp))
        {
            //System.out.println("Confirmed " + putativeExpansion2 + " for " + CanonicalShortGame$.MODULE$.apply(id) + ".");
            return putativeExpansion2;
        }
        else
        {
            return null;
        }

    }

    public static boolean isValidUptimalExpansion(UptimalExpansion exp, UptimalExpansion[] lExp, UptimalExpansion rExp)
    {
        //System.out.println("Trying: " + exp + " with " + Arrays.toString(lExp) + " | " + rExp);

        int loThatMatchesBase = -1;
        int firstNonNimber = lExp.length;

        for (int i = 0; i < lExp.length; i++)
        {
            if (firstNonNimber == lExp.length && (lExp[i].length() > 0 || lExp[i].nimberPart() != i))
            {
                firstNonNimber = i;
            }
            if (lExp[i].nimberPart() == rExp.nimberPart())
            {
                if (loThatMatchesBase >= 0)
                {
                    // In an uptimal, there can only be one left option w/ matching base.
                    return false;
                }
                loThatMatchesBase = i;
            }
        }

        int lastConsecutiveNimber = firstNonNimber - 1;

        int rightmostNegative = exp.rightmostCoefficientLeq(-1);
        int rightmostNonpositive = exp.rightmostCoefficientLeq(0);

        if (rightmostNegative >= 1)
        {
            // Cases (a) and (a').
            //System.out.println("Case (a).");
            if (lExp.length == 2 && loThatMatchesBase >= 0)
            {
                UptimalExpansion loExpected1 = exp.truncateTo(rightmostNegative).increment(true);
                UptimalExpansion loExpected2 = exp.truncateTo(
                    exp.getCoefficient(exp.length()) == 1 ? exp.length() - 1 : rightmostNonpositive
                );
                return lExp[loThatMatchesBase].equals(loExpected1) && lExp[1-loThatMatchesBase].equals(loExpected2);
            }
            else
            {
                return false;
            }
        }
        else if (exp.getCoefficient(1) == 0)
        {
            // Cases (b) and (b').
            //System.out.println("Case (b).");
            return lExp.length == exp.nimberPart() + 1 && lastConsecutiveNimber >= exp.nimberPart() - 1 &&
                lExp[lExp.length-1].equals(exp.truncateTo(
                    exp.getCoefficient(exp.length()) == 1 ? exp.length() - 1 : rightmostNonpositive
                ));
        }
        else if (exp.getCoefficient(1) == 1 && exp.isConfused())
        {
            // Case (c) and (c').
            //System.out.println("Case (c).");
            return lExp.length == 2 && lExp[0].nimberPart() == 0 && lExp[0].length() == 0 &&
                lExp[1].equals(exp.truncateTo(
                        exp.getCoefficient(exp.length()) == 1 ? exp.length() - 1 : rightmostNonpositive
                ));
        }
        else
        {
            // Cases (d) and (d').
            //System.out.println("Case (d).");
            return lExp.length == 1 && lExp[0].equals(exp.sharplyTruncateTo(
                    exp.getCoefficient(exp.length()) == 1 ? exp.length() - 1 : rightmostNonpositive
            ));
        }
    }

    public static int constructUptimal(UptimalExpansion ue)
    {
        int id = CanonicalShortGameOps.constructNus(ue.getNumberPart(), ue.getCoefficient(1), ue.nimberPart());
        for (int n = 2; n <= ue.length(); n++)
        {
            int value = ue.getCoefficient(n);
            if (value == 0)
            {
                continue;
            }
            int pow = pow(UP_ID, n);
            if (value < 0)
            {
                pow = getNegative(pow);
                value = -value;
            }
            for (int i = 0; i < value; i++)
            {
                id = add(id, pow);
            }
        }
        return id;
    }

    private static int pow(int id, int n)
    {
        if (getNumLeftOptions(id) != 1 || getLeftOption(id, 0) != ZERO_ID || getNumRightOptions(id) != 1)
        {
            throw new IllegalArgumentException("This game is not of the form {0|H}.");
        }
        if (n == 0)
        {
            return getNegative(getRightOption(id, 0));
        }
        else
        {
            return constructFromOptions(ZERO_ID, subtract(getRightOption(id, 0), powTo(id, n-1)));
        }
    }

    private static int powTo(int id, int n)
    {
        if (getNumLeftOptions(id) != 1 || getLeftOption(id, 0) != ZERO_ID || getNumRightOptions(id) != 1)
        {
            throw new IllegalArgumentException("This game is not of the form {0|H}.");
        }
        if (n == 0)
        {
            return ZERO_ID;
        }
        else
        {
            return constructFromOptions(powTo(id, n-1), getRightOption(id, 0));
        }
    }

    static DyadicRationalNumber mean(int id)
    {
        if (isNumberUpStar(id))
        {
            return getNumberPart(id);
        }
        else
        {
            return ((DyadicRationalNumber) thermograph(id).getMast());
        }
    }

    static DyadicRationalNumber temperature(int id)
    {
        if (isNumberUpStar(id))
        {
            if (isNumber(id))
            {
                // It's a number k/2^n, so the temperature is -1/2^n
                return mkRational(
                    BigInteger.ONE.negate(),
                    BigInteger.ONE.shiftLeft(getDenExpPart(id))
                    );
            }
            else
            {
                // It's a number plus a nonzero infinitesimal
                return Values.zero();
            }
        }
        else
        {
            return ((DyadicRationalNumber) thermograph(id).getTemperature());
        }
    }

    static Thermograph thermograph(int id)
    {
        if (isInteger(id))
        {
            return new Thermograph(getNumberPart(id));
        }

        Trajectory leftScaffold = Trajectory.NEGATIVE_INFINITY;
        Trajectory rightScaffold = Trajectory.POSITIVE_INFINITY;

        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            leftScaffold = leftScaffold.max
                (thermograph(getLeftOption(id, i)).getRightWall());
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            rightScaffold = rightScaffold.min
                (thermograph(getRightOption(id, i)).getLeftWall());
        }

        leftScaffold = leftScaffold.tilt(Values.negativeOne());
        rightScaffold = rightScaffold.tilt(Values.one());

        return leftScaffold.thermographicIntersection(rightScaffold);
    }

    ////////////////////////////////////////////////////////////////////////
    // Static game construction.

    private static int constructInteger(int integer)
    {
        int numLO = (integer > 0 ? 1 : 0), numRO = (integer < 0 ? 1 : 0);
        int sign = (integer >= 0 ? 1 : -1);
        int lastDefined = integer, offsetAt = UNUSED_BUCKET;

        for (lastDefined = integer;; lastDefined -= sign)
        {
            offsetAt = lookupSmallNusRecord(lastDefined, 0, 0, 0);
            if (offsetAt != UNUSED_BUCKET)
            {
                // Note: If lastDefined == 0, then this will automatically
                // hold, since 0 is explicitly constructed during class
                // initialization.  So there is no risk of an infinite loop.
                break;
            }
        }

        for (int i = lastDefined + sign; i != integer + sign; i += sign)
        {
            int newOffset = writeNusRecordExceptOptions(mkRational(i, 1), 0, 0, numLO, numRO);
            int[] sector = data[newOffset >> SECTOR_BITS];
            int sectorOffset = (newOffset & SECTOR_MASK);
            sector[sectorOffset + 2] = offsetAt;
            writeToIndex(hashOptions(numLO, sector, sectorOffset + 2, numRO, sector, sectorOffset + 2), newOffset);
            offsetAt = newOffset;
        }

        return offsetAt;
    }

    private static int constructRational(DyadicRationalNumber r)
    {
        // We can do this one recursively without fear of a stack overflow
        // (presumably we'll never see rationals with denominator 2^2000).
        int offset = lookupNusRecord(r, 0, 0);
        if (offset != UNUSED_BUCKET)
        {
            return offset;
        }

        if (r.isInteger())
        {
            if (r.numerator().isSmallInteger())
            {
                return constructInteger(((SmallInteger) r.numerator()).intValue());
            }
            else
            {
                throw new IllegalArgumentException
                    ("Integer out of bounds (must be between " + java.lang.Integer.MIN_VALUE + " and " + java.lang.Integer.MAX_VALUE + ")");
            }
        }

        int leftOption = constructRational(r.step(Values.negativeOne()));
        int rightOption = constructRational(r.step(Values.one()));

        offset = writeNusRecordExceptOptions(r, 0, 0, 1, 1);
        int[] sector = data[offset >> SECTOR_BITS];
        int sectorOffset = (offset & SECTOR_MASK);
        sector[sectorOffset + 2] = leftOption;
        sector[sectorOffset + 3] = rightOption;
        writeToIndex(hashOptions(1, sector, sectorOffset + 2, 1, sector, sectorOffset + 3), offset);

        return offset;
    }

    private static int constructNimber(DyadicRationalNumber number, int nimber)
    {
        int lastDefined, offsetAt = UNUSED_BUCKET;
        for (lastDefined = nimber;; lastDefined--)
        {
            offsetAt = lookupNusRecord(number, 0, lastDefined);
            if (offsetAt != UNUSED_BUCKET || lastDefined == 0)
            {
                break;
            }
        }
        if (offsetAt == UNUSED_BUCKET)
        {
            offsetAt = constructRational(number);
        }

        for (int i = lastDefined + 1; i <= nimber; i++)
        {
            int newOffset = writeNusRecordExceptOptions(number, 0, i, i, i);
            int[] sector = data[newOffset >> SECTOR_BITS];
            int sectorOffset = (newOffset & SECTOR_MASK);
            // Copy the options from the previous nimber to this one.
            System.arraycopy(
                data[offsetAt >> SECTOR_BITS],
                (offsetAt & SECTOR_MASK) + 2,
                sector,
                sectorOffset + 2,
                i - 1
                );
            // Set the ith left option.
            sector[sectorOffset + 2 + i - 1] = offsetAt;
            // Copy the left options as right options.
            System.arraycopy(
                sector,
                sectorOffset + 2,
                sector,
                sectorOffset + 2 + i,
                i
                );
            writeToIndex(hashOptions(i, sector, sectorOffset + 2, i, sector, sectorOffset + 2 + i), newOffset);
            offsetAt = newOffset;
        }

        return offsetAt;
    }

    static int constructNus(DyadicRationalNumber r, int upMultiple, int nimber)
    {
        int parity = (upMultiple & 1);
        int sign = (upMultiple >= 0 ? 1 : -1);
        int numberOffset = constructRational(r);
        int lastDefined, offsetAt = UNUSED_BUCKET;

        for (lastDefined = upMultiple;; lastDefined -= sign)
        {
            offsetAt = lookupNusRecord(r, lastDefined, nimber ^ parity ^ (lastDefined & 1));
            if (offsetAt != UNUSED_BUCKET || lastDefined == 0)
            {
                break;
            }
        }

        if (offsetAt == UNUSED_BUCKET)
        {
            offsetAt = constructNimber(r, nimber ^ parity);
        }

        for (int i = lastDefined + sign; i != upMultiple + sign; i += sign)
        {
            int numLO, numRO, starOffset = 0;
            int currentNimber = nimber ^ parity ^ (i & 1);
            if (i == 1 && currentNimber == 1)
            {
                // Special case: n^*.
                starOffset = constructNus(r, 0, 1);
                numLO = 2;
                numRO = 1;
            }
            else if (i == -1 && currentNimber == 1)
            {
                // Special case: nv*.
                starOffset = constructNus(r, 0, 1);
                numLO = 1;
                numRO = 2;
            }
            else
            {
                numLO = 1;
                numRO = 1;
            }
            int newOffset = writeNusRecordExceptOptions(r, i, currentNimber, numLO, numRO);
            int[] sector = data[newOffset >> SECTOR_BITS];
            int sectorOffset = (newOffset & SECTOR_MASK);
            if (i == 1 && currentNimber == 1)
            {
                sector[sectorOffset + 2] = numberOffset;
                sector[sectorOffset + 3] = starOffset;
                sector[sectorOffset + 4] = numberOffset;
            }
            else if (i == -1 && currentNimber == 1)
            {
                sector[sectorOffset + 2] = numberOffset;
                sector[sectorOffset + 3] = numberOffset;
                sector[sectorOffset + 4] = starOffset;
            }
            else if (i > 0)
            {
                sector[sectorOffset + 2] = numberOffset;
                sector[sectorOffset + 3] = offsetAt;
            }
            else
            {
                sector[sectorOffset + 2] = offsetAt;
                sector[sectorOffset + 3] = numberOffset;
            }
            writeToIndex(hashOptions(numLO, sector, sectorOffset + 2, numRO, sector, sectorOffset + 2 + numLO), newOffset);
            offsetAt = newOffset;
        }

        return offsetAt;
    }

    private static int writeNusRecordExceptOptions
        (DyadicRationalNumber number, int upMultiple, int nimber, int numLO, int numRO)
    {
        int optionsOffset;

        if (numLO >= 16384 || numRO >= 16384)
        {
            throw new IllegalArgumentException
                ("Combinatorial game suite does not support games with more than 16383 options for one player.  " +
                 "(If anyone really needs this capability, let me know and I'll try to add it.)");
        }
        else if (isSmallNus(number, upMultiple, nimber))
        {
            // Small NUS.
            ensureSectorSpace(5 + numLO + numRO);
            int[] sector = data[nextOffset >> SECTOR_BITS];
            int sectorOffset = nextOffset & SECTOR_MASK;

            int numerator = ((SmallInteger) number.numerator()).intValue();
            int denExp = number.denominatorExponent();

            sector[sectorOffset] = UNUSED_BUCKET;
            sector[sectorOffset + 1] = getSmallNusDescriptor(denExp, upMultiple, nimber);
            sector[sectorOffset + 2] = numerator;
            sector[sectorOffset + 3] = UNUSED_BUCKET;
            sector[sectorOffset + 4] = STD_OPTIONS_RECORD | IS_NUS_MASK | (numLO << NUM_LO_SHIFT) | numRO;
            writeToIndex(hashSmallNus(numerator, denExp, upMultiple, nimber), nextOffset);

            optionsOffset = nextOffset + 3;
            nextOffset += 5 + numLO + numRO;
        }
        else
        {
            // Large NUS.
            byte[] numArray = number.numerator().bigIntValue().toByteArray();
            int numeratorSlots = (numArray.length + 3) / 4;
            ensureSectorSpace(8 + numeratorSlots + numLO + numRO);

            int[] sector = data[nextOffset >> SECTOR_BITS];
            int sectorOffset = nextOffset & SECTOR_MASK;
            int nusOffset = sectorOffset + 2 + numLO + numRO;

            // For a large NUS, the record appears *after* the options record.
            sector[sectorOffset] = UNUSED_BUCKET;
            sector[sectorOffset + 1] = EXT_OPTIONS_RECORD | IS_NUS_MASK | (numLO << NUM_LO_SHIFT) | numRO;

            sector[nusOffset] = UNUSED_BUCKET;
            sector[nusOffset + 1] = EXT_NUS_RECORD | number.denominatorExponent();
            sector[nusOffset + 2] = nextOffset;
            sector[nusOffset + 3] = upMultiple;
            sector[nusOffset + 4] = nimber;
            sector[nusOffset + 5] = numArray.length;
            for (int i = 0; i < numeratorSlots; i++)
            {
                sector[nusOffset + 6 + i] = 0;
            }
            for (int i = 0; i < numArray.length; i++)
            {
                sector[nusOffset + 6 + (i/4)] |=
                    (  ((numArray[i]) & 0xff)  <<  8 * (i % 4)  );
            }
            writeToIndex(hashNus(number, upMultiple, nimber), nextOffset + 2 + numLO + numRO);

            optionsOffset = nextOffset;
            nextOffset += 8 + numeratorSlots + numLO + numRO;

            largeNusGames++;
        }

        totalGames++;
        nusGames++;
        return optionsOffset;
    }

    private static int constructNus(int numerator, int denExp, int upMultiple, int nimber)
    {
        int offset = lookupSmallNusRecord(numerator, denExp, upMultiple, nimber);
        if (offset == UNUSED_BUCKET)
        {
            return constructNus(mkRational(numerator, 1 << denExp), upMultiple, nimber);
        }
        else
        {
            return offset;
        }
    }

    static int constructFromOptions(int leftOption, int rightOption)
    {
        return constructFromOptions(new int[] { leftOption }, new int[] { rightOption });
    }

    static int constructFromOptions
        (int[] leftOptionArray, int[] rightOptionArray)
    {
        // Do a first pass to eliminate duplicate options (it's fast!)
        eliminateDuplicateOptions(leftOptionArray);
        eliminateDuplicateOptions(rightOptionArray);

        int leftMex = mex(leftOptionArray);
        if (leftMex >= 0)
        {
            int rightMex = mex(rightOptionArray);
            if (leftMex == rightMex)
            {
                return constructNus(Values.zero(), 0, leftMex);
            }
        }

        // Iteratively bypass all reversible moves (this will not add any
        // extra duplicate options.)
        leftOptionArray = bypassReversibleOptionsL(leftOptionArray, rightOptionArray);
        rightOptionArray = bypassReversibleOptionsR(leftOptionArray, rightOptionArray);

        // Now eliminate dominated options.
        eliminateDominatedOptions(leftOptionArray, true);
        eliminateDominatedOptions(rightOptionArray, false);

        return constructFromCanonicalOptions(pack(leftOptionArray), pack(rightOptionArray));
    }

    // If options is an array of game IDs, then this method returns:
    //  -1 if options contains any non-nimber.
    //   m if options contains only nimbers, and m is least such that
    //     *m is not represented.
    private static int mex(int[] options)
    {
        int i, mex = 0;
        for (i = 0; i < options.length; i++)
        {
            if (options[i] != -1)
            {
                if (!isNimber(options[i]))
                {
                    return -1;
                }
                else if (getNimberPart(options[i]) == mex)
                {
                    mex++;
                }
                else
                {
                    // It's a nimber, but exceeds mex.  We've found the true
                    // mex - *provided* everything that remains is a nimber.
                    break;
                }
            }
        }

        // We still must scan the rest of the array to check that everything's
        // a nimber.  (We could have folded this into the previous loop, but
        // it's more efficient to avert extra calls to getNimberPart.)

        for (; i < options.length; i++)
        {
            if (options[i] != -1 && !isNimber(options[i]))
            {
                return -1;
            }
        }

        return mex;
    }

    // Unlike fromOptions(int[], int[]), this method ASSUMES
    // that the supplied arrays contain no dominated or reversible options, and
    // no null entries.  Passing unsimplified arrays to this method will
    // "seriously screw up everything" :)
    private static int constructFromCanonicalOptions(int[] leftOptionArray, int[] rightOptionArray)
    {
        Arrays.sort(leftOptionArray);
        Arrays.sort(rightOptionArray);
        int offset = lookupOptionsRecord(leftOptionArray, rightOptionArray);
        if (offset != UNUSED_BUCKET)
        {
            return offset;
        }

        // It's a new game!
        offset = constructAsNusEntry(leftOptionArray, rightOptionArray);
        if (offset != UNUSED_BUCKET)
        {
            // Successfully constructed as a nus entry.  We're done.
            return offset;
        }

        int entrySize = 2 + leftOptionArray.length + rightOptionArray.length;
        ensureSectorSpace(entrySize);

        offset = nextOffset;
        nextOffset += entrySize;

        int[] sector = data[offset >> SECTOR_BITS];
        int sectorOffset = (offset & SECTOR_MASK);

        // Write the options descriptor.
        sector[sectorOffset] = UNUSED_BUCKET;
        sector[sectorOffset+1] =
            STD_OPTIONS_RECORD |
            (leftOptionArray.length << NUM_LO_SHIFT) |
            rightOptionArray.length;
        // Copy the option arrays.
        System.arraycopy(leftOptionArray, 0, sector, sectorOffset + 2, leftOptionArray.length);
        System.arraycopy(rightOptionArray, 0, sector, sectorOffset + 2 + leftOptionArray.length, rightOptionArray.length);
        writeToIndex(
            hashOptions(leftOptionArray.length, leftOptionArray, 0, rightOptionArray.length, rightOptionArray, 0),
            offset
            );
        totalGames++;
        return offset;
    }

    private static int constructAsNusEntry(int[] leftOptionArray, int[] rightOptionArray)
    {
        DyadicRationalNumber number;
        int upMultiple, nimber;

        if (leftOptionArray.length == 0)
        {
            if (rightOptionArray.length == 0)
            {
                number = Values.zero();
            }
            else
            {
                // We just assume things are properly canonicalized and so right's
                // option list must be of length 1 with the unique element an
                // integer.
                number = getNumberPart(rightOptionArray[0]).$minus(Values.one());
            }
            upMultiple = nimber = 0;
        }
        else if (rightOptionArray.length == 0)
        {
            number = getNumberPart(leftOptionArray[0]).$plus(Values.one());
            upMultiple = nimber = 0;
        }
        else if (leftOptionArray.length == 1 && rightOptionArray.length == 1 &&
                 isNumber(leftOptionArray[0]) && isNumber(rightOptionArray[0]) &&
                 compareNumberParts(leftOptionArray[0], rightOptionArray[0]) < 0)
        {
            // We're a number but not an integer.  Conveniently, since the
            // option lists are canonicalized, the value of this game is the
            // mean of its left & right options.
            number = getNumberPart(leftOptionArray[0]).mean(getNumberPart(rightOptionArray[0]));
            upMultiple = nimber = 0;
        }
        else if (leftOptionArray.length == 2 && rightOptionArray.length == 1 &&
                 isNumber(leftOptionArray[0]) && leftOptionArray[0] == rightOptionArray[0] &&
                 isNumberUpStar(leftOptionArray[1]) &&
                 compareNumberParts(leftOptionArray[0], leftOptionArray[1]) == 0 &&
                 getUpMultiplePart(leftOptionArray[1]) == 0 && getNimberPart(leftOptionArray[1]) == 1)
        {
            // For some number n, the form of this game is {n,n*|n} = n^*.
            number = getNumberPart(leftOptionArray[0]);
            upMultiple = nimber = 1;
        }
        else if (leftOptionArray.length == 1 && rightOptionArray.length == 2 &&
                 isNumber(leftOptionArray[0]) && leftOptionArray[0] == rightOptionArray[0] &&
                 isNumberUpStar(rightOptionArray[1]) &&
                 compareNumberParts(rightOptionArray[0], rightOptionArray[1]) == 0 &&
                 getUpMultiplePart(rightOptionArray[1]) == 0 && getNimberPart(rightOptionArray[1]) == 1)
        {
            number = getNumberPart(rightOptionArray[0]);
            upMultiple = -1;
            nimber = 1;
        }
        else if (leftOptionArray.length == 1 && rightOptionArray.length == 1 &&
                 isNumber(leftOptionArray[0]) && isNumberUpStar(rightOptionArray[0]) &&
                 !isNumber(rightOptionArray[0]) &&
                 compareNumberParts(leftOptionArray[0], rightOptionArray[0]) == 0 &&
                 getUpMultiplePart(rightOptionArray[0]) >= 0)
        {
            // This is of the form n + {0|G} where G is a number-up-star of up multiple >= 0.
            number = getNumberPart(leftOptionArray[0]);
            upMultiple = getUpMultiplePart(rightOptionArray[0]) + 1;
            nimber = getNimberPart(rightOptionArray[0]) ^ 1;
        }
        else if (leftOptionArray.length == 1 && rightOptionArray.length == 1 &&
                 isNumber(rightOptionArray[0]) && isNumberUpStar(leftOptionArray[0]) &&
                 !isNumber(leftOptionArray[0]) &&
                 compareNumberParts(leftOptionArray[0], rightOptionArray[0]) == 0 &&
                 getUpMultiplePart(leftOptionArray[0]) <= 0)
        {
            // This is of the form n + {G|0} where G is a number-up-star of up multiple <= 0.
            number = getNumberPart(leftOptionArray[0]);
            upMultiple = getUpMultiplePart(leftOptionArray[0]) - 1;
            nimber = getNimberPart(leftOptionArray[0]) ^ 1;
        }
        else if (leftOptionArray.length >= 1 && rightOptionArray.length >= 1 &&
                 leftOptionArray.length == rightOptionArray.length &&
                 isNumber(leftOptionArray[0]) && leftOptionArray[0] == rightOptionArray[0])
        {
            // Last we need to check for games of the form n + *k.
            for (int i = 0; i < leftOptionArray.length; i++)
            {
                if (leftOptionArray[i] != rightOptionArray[i] ||
                    !isNumberUpStar(leftOptionArray[i]) ||
                    compareNumberParts(leftOptionArray[0], leftOptionArray[i]) != 0 ||
                    getUpMultiplePart(leftOptionArray[i]) != 0 ||
                    getNimberPart(leftOptionArray[i]) != i)
                {
                    return UNUSED_BUCKET;
                }
            }
            // It's a nimber.
            number = getNumberPart(leftOptionArray[0]);
            upMultiple = 0;
            nimber = leftOptionArray.length;
        }
        else
        {
            return UNUSED_BUCKET;
        }

        // It's a nus, so construct it.
        int offset = writeNusRecordExceptOptions
            (number, upMultiple, nimber, leftOptionArray.length, rightOptionArray.length);
        int[] sector = data[offset >> SECTOR_BITS];
        int sectorOffset = (offset & SECTOR_MASK);
        System.arraycopy(leftOptionArray, 0, sector, sectorOffset + 2, leftOptionArray.length);
        System.arraycopy(rightOptionArray, 0, sector, sectorOffset + 2 + leftOptionArray.length, rightOptionArray.length);
        writeToIndex(hashOptions(
            leftOptionArray.length, sector, sectorOffset + 2,
            rightOptionArray.length, sector, sectorOffset + 2 + leftOptionArray.length
            ), offset);
        return offset;
    }

    private static int compareNumberParts(int gId, int hId)
    {
        if ((data[gId >> SECTOR_BITS][(gId+1) & SECTOR_MASK] & EXT_RECORD_MASK) != 0 ||
            (data[hId >> SECTOR_BITS][(hId+1) & SECTOR_MASK] & EXT_RECORD_MASK) != 0)
        {
            // At least one of the numbers is large.
            return getNumberPart(gId).compareTo(getNumberPart(hId));
        }
        else
        {
            // Both are small.
            int gNum = getSmallNumeratorPart(gId), gDenExp = getDenExpPart(gId),
                hNum = getSmallNumeratorPart(hId), hDenExp = getDenExpPart(hId);
            long cmp;
            if (gDenExp <= hDenExp)
            {
                cmp = (((long) gNum) << (hDenExp-gDenExp)) - (hNum);
            }
            else
            {
                cmp = (gNum) - (((long) hNum) << (gDenExp-hDenExp));
            }
            if (cmp < 0)
            {
                return -1;
            }
            else if (cmp > 0)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // Static simplification routines

    private static boolean leqArrays(int id, int[] leftOptionArray, int[] rightOptionArray)
    {
        if (Thread.interrupted())
        {
            throw new RuntimeException("Calculation canceled by user.");
        }
        
        // Return false if H <= GL for some left option GL of G
        //              or HR <= G for some right option HR of H.
        // Otherwise return true.

        for (int i = 0; i < rightOptionArray.length; i++)
        {
            if (rightOptionArray[i] != -1 && leq(rightOptionArray[i], id))
            {
                return false;
            }
        }

        int[] sector = data[id >> SECTOR_BITS];
        int sectorOffset = (id & SECTOR_MASK);
        int numLO = (sector[sectorOffset+1] & NUM_LO_MASK) >> NUM_LO_SHIFT;

        for (int i = 0; i < numLO; i++)
        {
            if (geqArrays(sector[sectorOffset+2+i], leftOptionArray, rightOptionArray))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean geqArrays(int id, int[] leftOptionArray, int[] rightOptionArray)
    {
        // Return false if GR <= H or G <= HL
        // Otherwise return true.

        for (int i = 0; i < leftOptionArray.length; i++)
        {
            if (leftOptionArray[i] != -1 && leq(id, leftOptionArray[i]))
            {
                return false;
            }
        }

        int[] sector = data[id >> SECTOR_BITS];
        int sectorOffset = (id & SECTOR_MASK);
        int numLO = (sector[sectorOffset+1] & NUM_LO_MASK) >> NUM_LO_SHIFT,
            numRO = sector[sectorOffset+1] & NUM_RO_MASK;

        for (int i = 0; i < numRO; i++)
        {
            if (leqArrays(sector[sectorOffset+2+numLO+i], leftOptionArray, rightOptionArray))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean arrayContains(int[] array, int g)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == g)
            {
                return true;
            }
        }
        return false;
    }

    private static void eliminateDuplicateOptions(int[] options)
    {
        Arrays.sort(options);
        for (int i = 0; i < options.length-1; i++)
        {
            if (options[i] == options[i+1])
            {
                options[i] = -1;
            }
        }
    }

    private static void eliminateDominatedOptions(int[] options, boolean eliminateSmallerOptions)
    {
        for (int i = 0; i < options.length; i++)
        {
            if (options[i] != -1)
            for (int j = 0; j < i; j++)
            {
                if (options[j] != -1)
                {
                    if (eliminateSmallerOptions && leq(options[i], options[j]) ||
                        !eliminateSmallerOptions && leq(options[j], options[i]))
                    {
                        options[i] = -1;
                        break;
                    }
                    else if (eliminateSmallerOptions && leq(options[j], options[i]) ||
                        !eliminateSmallerOptions && leq(options[i], options[j]))
                    {
                        options[j] = -1;
                    }
                }
            }
        }
    }

    private static int[] bypassReversibleOptionsL(int[] leftOptionArray, int[] rightOptionArray)
    {
        // Look for reversible moves for left.
        for (int i = 0; i < leftOptionArray.length; i++)
        {
            if (leftOptionArray[i] != -1)
            for (int j = 0; j < getNumRightOptions(leftOptionArray[i]); j++)
            {
                int gLR = getRightOption(leftOptionArray[i], j);
                if (leqArrays(gLR, leftOptionArray, rightOptionArray))
                {
                    int[] newLeftOptionArray = new int[leftOptionArray.length-1+getNumLeftOptions(gLR)];
                    for (int k = 0; k < i; k++)
                    {
                        newLeftOptionArray[k] = leftOptionArray[k];
                    }
                    for (int k = i+1; k < leftOptionArray.length; k++)
                    {
                        newLeftOptionArray[k-1] = leftOptionArray[k];
                    }
                    for (int k = 0; k < getNumLeftOptions(gLR); k++)
                    {
                        int gLRL = getLeftOption(gLR, k);
                        if (arrayContains(leftOptionArray, gLRL))
                        {
                            newLeftOptionArray[leftOptionArray.length-1+k] = -1;
                        }
                        else
                        {
                            newLeftOptionArray[leftOptionArray.length-1+k] = gLRL;
                        }
                    }
                    leftOptionArray = newLeftOptionArray;
                    i--;
                    break;
                }
            }
        }
        return leftOptionArray;
    }

    private static int[] bypassReversibleOptionsR(int[] leftOptionArray, int[] rightOptionArray)
    {
        for (int i = 0; i < rightOptionArray.length; i++)
        {
            if (rightOptionArray[i] != -1)
            for (int j = 0; j < getNumLeftOptions(rightOptionArray[i]); j++)
            {
                int gRL = getLeftOption(rightOptionArray[i], j);
                if (geqArrays(gRL, leftOptionArray, rightOptionArray))
                {
                    int[] newRightOptionArray = new int[rightOptionArray.length-1+getNumRightOptions(gRL)];
                    for (int k = 0; k < i; k++)
                    {
                        newRightOptionArray[k] = rightOptionArray[k];
                    }
                    for (int k = i+1; k < rightOptionArray.length; k++)
                    {
                        newRightOptionArray[k-1] = rightOptionArray[k];
                    }
                    for (int k = 0; k < getNumRightOptions(gRL); k++)
                    {
                        int gRLR = getRightOption(gRL, k);
                        if (arrayContains(rightOptionArray, gRLR))
                        {
                            newRightOptionArray[rightOptionArray.length-1+k] = -1;
                        }
                        else
                        {
                            newRightOptionArray[rightOptionArray.length-1+k] = gRLR;
                        }
                    }
                    rightOptionArray = newRightOptionArray;
                    i--;
                    break;
                }
            }
        }
        return rightOptionArray;
    }

    private static int[] pack(int[] options)
    {
        int nOptions = 0;
        for (int i = 0; i < options.length; i++)
        {
            if (options[i] != -1)
            {
                nOptions++;
            }
        }
        int[] packedOptions = new int[nOptions];
        for (int i = 0, j = 0; i < options.length; i++)
        {
            if (options[i] != -1)
            {
                packedOptions[j] = options[i];
                j++;
            }
        }
        return packedOptions;
    }

    // Ensures that there is enough room in the current sector to write out
    // records of the specified total size.
    private static void ensureSectorSpace(int slotsNeeded)
    {
        // If either:
        // (i)  nextOffset points to an unallocated sector (this means it must
        //      have landed exactly on the first slot of nextSector), or
        // (ii) nextOffset points to an allocated sector, but there are fewer
        //      slots available than requested,
        // then allocate nextSector and advance nextOffset and nextSector as
        // appropriate.
        if ((nextOffset >> SECTOR_BITS) >= nextSector ||
            SECTOR_SIZE - (nextOffset & SECTOR_MASK) < slotsNeeded)
        {
            // Time to add a new sector.
            if (nextSector >= data.length)
            {
                /*
                Context.getActiveContext().getLogger().finer
                    ("Growing the sector array for the CanonicalGame cache.");
                    */
                // Grow the data array.
                int[][] newData = new int[data.length << 1][];
                System.arraycopy(data, 0, newData, 0, data.length);
                data = newData;
            }
            /*
            Context.getActiveContext().getLogger().finer
                ("Allocating a new sector for the CanonicalGame cache (new size: "
                    + (nextSector + 1) * SECTOR_SIZE / 256 + " KB).");
                    */
            data[nextSector] = new int[SECTOR_SIZE];
            nextOffset = nextSector << SECTOR_BITS;
            nextSector++;
        }
    }

    // Writes a value to the specified cache index.
    private static void writeToIndex(int hashcode, int value)
    {
        if (totalRecords >= indexCapacity)
        {
            // Rehash to maintain a 1.0 load factor.
            totalRecords = 0;
            growIndexAndRehash();
        }

        int bucket = (hashcode & indexMask);
        if (index[bucket] == UNUSED_BUCKET)
        {
            index[bucket] = value;
        }
        else
        {
            int offsetAt = index[bucket], chainDepth = 1;
            while (data[offsetAt >> SECTOR_BITS][offsetAt & SECTOR_MASK] != UNUSED_BUCKET)
            {
                offsetAt = data[offsetAt >> SECTOR_BITS][offsetAt & SECTOR_MASK];
                chainDepth++;
            }
            data[offsetAt >> SECTOR_BITS][offsetAt & SECTOR_MASK] = value;
            // Gather statistics:
            if (chainDepth > maxChainDepth)
            {
                maxChainDepth = chainDepth;
            }
            totalChainDepth += chainDepth;
        }
        totalRecords++;
    }

    private static int lookupNusRecord(DyadicRationalNumber number, int upMultiple, int nimber)
    {
        if (isSmallNus(number, upMultiple, nimber))
        {
            return lookupSmallNusRecord
                (((SmallInteger) number.numerator()).intValue(), number.denominatorExponent(), upMultiple, nimber);
        }
        else
        {
            return lookupLargeNusRecord(number, upMultiple, nimber);
        }
    }

    private static int lookupSmallNusRecord(int numerator, int denExp, int upMultiple, int nimber)
    {
        int offsetAt = index[hashSmallNus(numerator, denExp, upMultiple, nimber) & indexMask];
        if (offsetAt == UNUSED_BUCKET)
        {
            return UNUSED_BUCKET;
        }

        int descriptor = getSmallNusDescriptor(denExp, upMultiple, nimber);
        while (offsetAt != UNUSED_BUCKET)
        {
            int[] sector = data[offsetAt >> SECTOR_BITS];
            int sectorOffset = (offsetAt & SECTOR_MASK);
            if (sector[sectorOffset+1] == descriptor && sector[sectorOffset+2] == numerator)
            {
                // It's a match.
                return offsetAt + 3;
            }
            offsetAt = sector[sectorOffset];
        }
        return UNUSED_BUCKET;
    }

    private static int lookupLargeNusRecord(DyadicRationalNumber number, int upMultiple, int nimber)
    {
        int offsetAt = index[hashNus(number, upMultiple, nimber) & indexMask];
        if (offsetAt == UNUSED_BUCKET)
        {
            return UNUSED_BUCKET;
        }

        int descriptor = EXT_NUS_RECORD | number.denominatorExponent();
        byte[] numArray = number.numerator().bigIntValue().toByteArray();
        while (offsetAt != UNUSED_BUCKET)
        {
            int[] sector = data[offsetAt >> SECTOR_BITS];
            int sectorOffset = (offsetAt & SECTOR_MASK);
            if (sector[sectorOffset+1] == descriptor && sector[sectorOffset+3] == upMultiple &&
                sector[sectorOffset+4] == nimber && sector[sectorOffset+5] == numArray.length)
            {
                boolean match = true;
                for (int i = 0; i < numArray.length; i++)
                {
                    if (numArray[i] != (byte) ((sector[sectorOffset + 6 + i/4] >> 8 * (i % 4)) & 0xff))
                    {
                        match = false;
                        break;
                    }
                }
                if (match)
                {
                    return sector[sectorOffset+2];
                }
            }
            offsetAt = sector[sectorOffset];
        }
        return UNUSED_BUCKET;
    }

    private static int lookupOptionsRecord(int[] leftOptionArray, int[] rightOptionArray)
    {
        int numLO = leftOptionArray.length, numRO = rightOptionArray.length;
        int offsetAt = index
            [hashOptions(numLO, leftOptionArray, 0, numRO, rightOptionArray, 0) & indexMask];
        while (offsetAt != UNUSED_BUCKET)
        {
            int[] sector = data[offsetAt >> SECTOR_BITS];
            int sectorOffset = (offsetAt & SECTOR_MASK);
            int descriptor = sector[sectorOffset + 1];
            if ((descriptor & NUS_RECORD_MASK) == 0)
            {
                // It's an options record; check to see if it matches.
                boolean matches =
                    (numLO == ((descriptor & NUM_LO_MASK) >> NUM_LO_SHIFT) &&
                     numRO == (descriptor & NUM_RO_MASK));
                if (matches)
                {
                    for (int i = 0; i < numLO; i++)
                    {
                        if (leftOptionArray[i] != sector[sectorOffset + 2 + i])
                        {
                            matches = false;
                            break;
                        }
                    }
                    if (matches)
                    {
                        for (int i = 0; i < numRO; i++)
                        {
                            if (rightOptionArray[i] != sector[sectorOffset + 2 + numLO + i])
                            {
                                matches = false;
                                break;
                            }
                        }
                    }
                }
                if (matches)
                {
                    // Found a match!
                    break;
                }
            }
            offsetAt = sector[sectorOffset];
        }
        return offsetAt;
    }

    private static boolean isSmallNus(DyadicRationalNumber r, int upMultiple, int nimber)
    {
        return r.numerator().isSmallInteger() && nimber < 4096 && upMultiple >= -4096 && upMultiple < 4096;
    }

    private static int getSmallNusDescriptor(int denExp, int upMultiple, int nimber)
    {
        return
            STD_NUS_RECORD |
            (denExp << DENOMINATOR_SHIFT) |
            ((upMultiple << UP_MULTIPLE_RIGHTSHIFT) >>> UP_MULTIPLE_LEFTSHIFT) |
            nimber;
    }

    private static int hashNus(DyadicRationalNumber number, int upMultiple, int nimber)
    {
        if (isSmallNus(number, upMultiple, nimber))
        {
            return hashSmallNus(((SmallInteger) number.numerator()).intValue(), number.denominatorExponent(), upMultiple, nimber);
        }
        else
        {
            return hash(number.hashCode() ^ (upMultiple + 65535 * nimber));
        }
    }

    private static int hashSmallNus(int numerator, int denExp, int upMultiple, int nimber)
    {
        return hashSmallNus(getSmallNusDescriptor(denExp, upMultiple, nimber), numerator);
    }

    private static int hashSmallNus(int descriptor, int numerator)
    {
        return hash(descriptor ^ numerator);
    }

    private static int hashOptions(int numLo, int[] loArray, int loOffset,
                                   int numRo, int[] roArray, int roOffset)
    {
        int hashcode = 1;
        for (int i = 0; i < numLo; i++)
        {
            hashcode = 31 * hashcode + loArray[loOffset + i];
        }
        for (int i = 0; i < numRo; i++)
        {
            hashcode = 31 * hashcode + roArray[roOffset + i];
        }
        return hash(hashcode);
    }

    private static int hashRecord(int offset)
    {
        int[] sector = data[offset >> SECTOR_BITS];
        int sectorOffset = offset & SECTOR_MASK;
        switch (sector[sectorOffset+1] & RECORD_TYPE_MASK)
        {
            case STD_NUS_RECORD:
                return hashSmallNus(sector[sectorOffset+1], sector[sectorOffset+2]);

            case EXT_NUS_RECORD:
                int gOffset = sector[sectorOffset+2];
                return hashNus(getNumberPart(gOffset), getUpMultiplePart(gOffset), getNimberPart(gOffset));

            case STD_OPTIONS_RECORD:
            case EXT_OPTIONS_RECORD:
                return hashOptions(
                    getNumLeftOptions(offset), sector, sectorOffset + 2,
                    getNumRightOptions(offset), sector, sectorOffset + 2 + getNumLeftOptions(offset)
                    );

            default:
                throw new RuntimeException();
        }
    }

    private static int hash(int h)
    {
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);

        return h;
    }

    private static void growIndexAndRehash()
    {
        /*
        Context.getActiveContext().getLogger().finer("Rehashing CanonicalGame index.");
        */

        int oldIndexCapacity = indexCapacity, oldIndexMask = indexMask;
        int[] oldIndex = index;
        indexCapacity <<= 1;
        indexMask = indexCapacity - 1;
        index = new int[indexCapacity];
        Arrays.fill(index, UNUSED_BUCKET);
        maxChainDepth = totalChainDepth = 0;

        // Go through each bucket and rehash.
        for (int bucket = 0; bucket < oldIndex.length; bucket++)
        {
            int offsetAt = oldIndex[bucket];
            while (offsetAt != UNUSED_BUCKET)
            {
                int nextEntry = data[offsetAt >> SECTOR_BITS][offsetAt & SECTOR_MASK];
                // Unlink nextEntry
                data[offsetAt >> SECTOR_BITS][offsetAt & SECTOR_MASK] = UNUSED_BUCKET;
                // Hash this entry
                writeToIndex(hashRecord(offsetAt), offsetAt);
                offsetAt = nextEntry;
            }
        }

        /*
        Context.getActiveContext().getLogger().finer
            ("Finished rehashing (new capacity: " + (indexCapacity >> 10) + " K entries).");
            */
    }

    private static int lookupOpResult(byte operation, int gId, int hId)
    {
        int hc = (operation ^ gId ^ hId) & opTableMask;
        if (opTableOp[hc] == operation &&
            (opTableG[hc] == gId && opTableH[hc] == hId ||
             operation == OPERATION_SUM && opTableG[hc] == hId && opTableH[hc] == gId))
        {
            return opTableResult[hc];
        }
        else
        {
            return -1;
        }
    }

    private static void storeOpResult(byte operation, int gId, int hId, int result)
    {
        int hc = (operation ^ gId ^ hId) & opTableMask;
        opTableOp[hc] = operation;
        opTableG[hc] = gId;
        opTableH[hc] = hId;
        opTableResult[hc] = result;
    }

    /**
     * Reinitializes the <code>CanonicalGame</code> class.  This will destroy
     * all existing information about canonical games.
     * <p>
     * <b>Warning</b>: Calling this method will invalidate any existing
     * <code>CanonicalGame</code> objects.  Attempting to use any invalidated
     * objects might cause unpredictable behavior.
     */
    public static void reinit()
    {
        opTableOp = null;
        opTableG = opTableH = opTableResult = null;
        index = null;
        data = null;
        UPTIMAL_MAP = new HashMap<java.lang.Integer,UptimalExpansion>();
        System.gc();

        opTableOp = new byte[opTableSize];
        Arrays.fill(opTableOp, OPERATION_NONE);
        opTableG = new int[opTableSize];
        opTableH = new int[opTableSize];
        opTableResult = new int[opTableSize];

        indexCapacity = DEFAULT_INDEX_CAPACITY;
        indexMask = indexCapacity - 1;
        index = new int[indexCapacity];
        Arrays.fill(index, UNUSED_BUCKET);
        data = new int[DEFAULT_SECTOR_SLOTS][];
        data[0] = new int[SECTOR_SIZE];

        nextOffset = totalRecords = 0;
        nextSector = 1;
        totalGames = nusGames = maxChainDepth = totalChainDepth = 0;

        // Construct 0 directly.  (It's a special case.)
        data[0][0] = UNUSED_BUCKET;
        data[0][1] = STD_NUS_RECORD;
        data[0][2] = 0;
        data[0][3] = UNUSED_BUCKET;
        data[0][4] = STD_OPTIONS_RECORD | IS_NUS_MASK;
        writeToIndex(hashSmallNus(0, 0, 0, 0), 0);
        writeToIndex(hashOptions(0, null, 0, 0, null, 0), 3);
        nextOffset += 5;
        totalGames++;
        nusGames++;

        ZERO_ID = 3;
        STAR_ID = constructNimber(Values.zero(), 1);
        UP_ID = constructNus(0, 0, 1, 0);
        UP_STAR_ID = constructNus(0, 0, 1, 1);
        ONE_ID = constructInteger(1);
        NEGATIVE_ONE_ID = constructInteger(-1);
        TWO_ID = constructInteger(2);
        NEGATIVE_TWO_ID = constructInteger(-2);
    }

    /**
     * Gets the total number of bytes used statically to store and reference
     * canonical games.
     *
     * @return  The number of bytes used.
     * @see     #getBytesAllocated() getBytesAllocated
     */
    public static int getBytesUsed()
    {
        return (indexCapacity + nextOffset) * 4;
    }

    /**
     * Gets the total number of bytes allocated statically to store and
     * reference canonical games.
     *
     * @return  The number of bytes allocated.
     * @see     #getBytesUsed() getBytesUsed
     */
    public static int getBytesAllocated()
    {
        return (indexCapacity + nextSector * SECTOR_SIZE) * 4;
    }

    /**
     * Gets the total number of canonical games that have been computed.
     *
     * @return  The total number of canonical games.
     */
    public static int getNumGames()
    {
        return totalGames;
    }

    /**
     * Gets the number of canonical games of the form
     * <code>m&uarr;n*k</code> that have been computed.
     *
     * @return  The number of games of the form <code>m&uarr;n*k</code>.
     */
    public static int getNumNusGames()
    {
        return nusGames;
    }

    /**
     * Gets the number of canonical games <code>m&uarr;n*k</code> that
     * have been computed, such that the numerator of <code>m</code> is
     * outside the range
     * <code>Integer.MIN_INTEGER <= m <= Integer.MAX_INTEGER</code>.
     *
     * @return  The number of games of the form <code>m&uarr;n*k</code>
     *          where <code>m</code> has large numerator.
     */
    public static int getNumLargeNusGames()
    {
        return largeNusGames;
    }

    /**
     * Gets the maximum depth of a cache chain.  (This is an indicator of how
     * effectively the <code>CanonicalGame</code> hash functions are working;
     * lower is better.)
     */
    public static int getMaxChainDepth()
    {
        return maxChainDepth;
    }

    /**
     * Gets the average depth of a cache chain.  (This is an indicator of how
     * effective the <code>CanonicalGame</code> hash functions are working;
     * lower is better.)
     */
    public static float getAverageChainDepth()
    {
        return ((float) totalChainDepth) / ((float) (totalGames + nusGames));
    }
}