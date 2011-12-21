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

package org.cgsuite.lang.game;


import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.CgsuiteSet;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.InputException;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
import org.cgsuite.lang.output.StyledTextOutput.Symbol;

import static org.cgsuite.lang.output.StyledTextOutput.Style.*;
import static org.cgsuite.lang.output.StyledTextOutput.Symbol.*;

// TODO criticalTemperatures
// TODO cool by negative temp

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
public final class CanonicalShortGame extends Game
{
    /*
     * As of version 0.5, all substantive information about canonical games is
     * stored in a single static int array.  This array is accessed in much the
     * same way as the byte array contained in a org.cgsuite.util.Cache object; see
     * the comments for org.cgsuite.util.Cache for more details.  An actual
     * CanonicalGame object contains just a single integer, essentially a
     * "virtual pointer" into the static array.
     *
     * Most of the CanonicalGame methods simply call a private static method
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
        OPERATION_INVERSE = 2,
        OPERATION_BIRTHDAY = 3,
        OPERATION_ATOMIC_WEIGHT = 4,
        OPERATION_NORTON_MULTIPLY = 5,
        OPERATION_CONWAY_MULTIPLY = 6,
        OPERATION_ORDINAL_SUM = 7
        ;

    private static int opTableSize = DEFAULT_OP_TABLE_SIZE, opTableMask = opTableSize - 1;
    private static byte[] opTableOp;
    private static int[] opTableG, opTableH, opTableResult;

    ////////////////////////////////////////////////////////////////////////
    // Constants.
    
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("CanonicalShortGame"); 
    public final static CgsuiteClass DYADIC_RATIONAL_TYPE = CgsuitePackage.forceLookupClass("DyadicRational");
    public final static CgsuiteClass NIMBER_TYPE = CgsuitePackage.forceLookupClass("Nimber");

    /**
     * A static reference to the game 0.
     */
    public final static CanonicalShortGame ZERO = new CanonicalShortGame(DYADIC_RATIONAL_TYPE);
    /**
     * A static reference to the game *.
     */
    public final static CanonicalShortGame STAR = new CanonicalShortGame(NIMBER_TYPE);
    /**
     * A static reference to the game &uarr;.
     */
    public final static CanonicalShortGame UP = new CanonicalShortGame(TYPE);
    /**
     * A static reference to the game &uarr;*.
     */
    public final static CanonicalShortGame UP_STAR = new CanonicalShortGame(TYPE);

    public final static CanonicalShortGame
        ONE = new CanonicalShortGame(DYADIC_RATIONAL_TYPE),
        TWO = new CanonicalShortGame(DYADIC_RATIONAL_TYPE),
        MINUS_ONE = new CanonicalShortGame(DYADIC_RATIONAL_TYPE),
        MINUS_TWO = new CanonicalShortGame(DYADIC_RATIONAL_TYPE);

    static
    {
        reinit();
    }

    ////////////////////////////////////////////////////////////////////////
    // Member data.

    // A unique integer identifier for this game.
    private int id;

    ////////////////////////////////////////////////////////////////////////
    // Constructors.
    
    private CanonicalShortGame(CgsuiteClass type)
    {
        super(type);
    }

    private CanonicalShortGame(CgsuiteClass type, int id)
    {
        super(type);
        this.id = id;
    }
    
    public CanonicalShortGame(CgsuiteInteger n)
    {
        super(DYADIC_RATIONAL_TYPE);
        if (!n.isSmall())
            throw new InputException("Overflow.");
        this.id = constructInteger(n.intValue());
    }

    /**
     * Constructs a <code>CanonicalGame</code> equal to the specified integer.
     *
     * @param   n The integer value of the <code>CanonicalGame</code>.
     */
    public static CanonicalShortGame construct(int n)
    {
        return createFromId(constructInteger(n));
    }

    public static CanonicalShortGame construct(CgsuiteInteger n)
    {
        if (!n.isSmall())
            throw new InputException("Overflow error.");
        return createFromId(constructInteger(n.intValue()));
    }

    /**
     * Constructs a <code>CanonicalGame</code> equal to the specified number.
     *
     * @param   number The number that corresponds to this
     *          <code>CanonicalGame</code>.
     * @throws  IllegalArgumentException <code>number</code> is not dyadic.
     */
    public static CanonicalShortGame construct(RationalNumber number)
    {
        return construct(number, 0, 0);
    }

    /**
     * Constructs a <code>CanonicalGame</code> equal to the sum of a number, a
     * multiple of up, and a nimber.  Specifically, the return
     * value is equal to the sum of:
     * <ul>
     * <li>The dyadic rational <code>number</code>,
     * <li><code>upMultiple</code> copies of up, and
     * <li>The nimber of order <code>nimber</code>.
     * </ul>
     * For example, <code>CanonicalGame(Rational.ZERO, -2, 1)</code>
     * would construct double-down-star.
     *
     * @param   number The number
     *          component of this <code>CanonicalGame</code>.
     * @param   upMultiple An integer specifying the number of copies of up
     *          in this <code>CanonicalGame</code>.
     * @param   nimber A non-negative integer specifying the order of the
     *          nimber component of this <code>CanonicalGame</code>.
     * @throws  IllegalArgumentException <code>number</code> is not dyadic.
     * @throws  IllegalArgumentException <code>nimber</code> is negative.
     */
    public static CanonicalShortGame construct(RationalNumber number, int upMultiple, int nimber)
    {
        if (!number.isDyadic())
        {
            throw new IllegalArgumentException("number must be dyadic.");
        }
        if (nimber < 0)
        {
            throw new IllegalArgumentException("nimber must be non-negative.");
        }
        return createFromId(constructNus(number, upMultiple, nimber));
    }

    /**
     * Constructs a <code>CanonicalGame</code> given exactly one option for
     * each player.  This is a convenience method equivalent to
     * <code>CanonicalGame(Collections.singleton(leftOption),
     * Collections.singleton(rightOption))</code>.
     *
     * @param   leftOption The left option of this game.
     * @param   rightOption The right option of this game.
     */
    public static CanonicalShortGame construct(CanonicalShortGame leftOption, CanonicalShortGame rightOption)
    {
        return createFromId(constructFromOptions(
            new int[] { leftOption.id },
            new int[] { rightOption.id }
            ));
    }

    /**
     * Constructs a <code>CanonicalGame</code> given collections of options.
     * It is permissible for the collections to contain <code>null</code>
     * elements; these will be ignored when constructing this game.
     *
     * @param   leftOptions The left options of this game.
     * @param   rightOptions The right options of this game.
     */
    public static CanonicalShortGame construct(Collection<CanonicalShortGame> leftOptions, Collection<CanonicalShortGame> rightOptions)
    {
        int[] leftOptionArray = new int[leftOptions.size()],
              rightOptionArray = new int[rightOptions.size()];

        int index = 0;
        for (CanonicalShortGame g : leftOptions)
        {
            leftOptionArray[index++] = (g == null ? -1 : g.id);
        }
        index = 0;
        for (CanonicalShortGame g : rightOptions)
        {
            rightOptionArray[index++] = (g == null ? -1 : g.id);
        }

        return createFromId(constructFromOptions(leftOptionArray, rightOptionArray));
    }

    /**
     * Constructs a <code>CanonicalGame</code> given arrays of options.
     * It is permissible for the arrays to contain <code>null</code>
     * elements; these will be ignored when constructing this game.
     *
     * @param   leftOptions The left options of this game.
     * @param   rightOptions The right options of this game.
     */
    public static CanonicalShortGame construct(CanonicalShortGame[] leftOptions, CanonicalShortGame[] rightOptions)
    {
        int[] leftOptionArray = new int[leftOptions.length],
              rightOptionArray = new int[rightOptions.length];

        for (int i = 0; i < leftOptionArray.length; i++)
        {
            leftOptionArray[i] = (leftOptions[i] == null ? -1 : leftOptions[i].id);
        }

        for (int i = 0; i < rightOptionArray.length; i++)
        {
            rightOptionArray[i] = (rightOptions[i] == null ? -1 : rightOptions[i].id);
        }

        return createFromId(constructFromOptions(leftOptionArray, rightOptionArray));
    }

    @Override
    protected int compareLike(CgsuiteObject other)
    {
        return compareLike(id, ((CanonicalShortGame) other).id);
    }
    
    private static int compareLike(int gId, int hId)
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
    
    private static int compareOptArrays(Integer[] gArray, Integer[] hArray)
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
    
    private static Integer[] sortedLeftOptions(int id)
    {
        Integer[] array = new Integer[getNumLeftOptions(id)];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = getLeftOption(id, i);
        }
        Arrays.sort(array, 0, array.length, LIKE_COMPARATOR);
        return array;
    }
    
    private static Integer[] sortedRightOptions(int id)
    {
        Integer[] array = new Integer[getNumRightOptions(id)];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = getRightOption(id, i);
        }
        Arrays.sort(array, 0, array.length, LIKE_COMPARATOR);
        return array;
    }
    
    private static Comparator<Integer> LIKE_COMPARATOR = new Comparator<Integer>()
    {
        @Override
        public int compare(Integer x, Integer y)
        {
            return compareLike(x, y);
        }
    };

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof CanonicalShortGame && id == ((CanonicalShortGame) obj).id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public Game simplify()
    {
        if (isInteger())
        {
            if (isExtendedRecord(id))
                return new CgsuiteInteger(getNumberPart().getNumerator());
            else
                return new CgsuiteInteger(getSmallNumeratorPart(id));
        }
        else
        {
            return this;
        }
    }

    private static boolean abbreviateInfinitesimals = false, meanZeroMode = false;
    private static int maxSlashes = 4;

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        toOutput(output, true, false);
        return output;
    }
    
    int toOutput(
        StyledTextOutput output,
        boolean forceBrackets,
        boolean forceParens
        )
    {
        if (Thread.interrupted())
        {
            throw new InputException("Calculation canceled by user.");
        }
        
        CanonicalShortGame g = this;
        CanonicalShortGame inverse = g.negate();

        // Check for number-up-star.
        if (g.isNumberUpStar())
        {
            if (forceParens && !g.isNumber() && !g.isNimber() &&
                !(g.getNumberPart().equals(RationalNumber.ZERO) && g.getNimberPart() == 0))
            {
                // Not a number, nimber, or up multiple.  Force parens to clarify.
                output.appendMath("(");
            }
            if (g.isZero() || !g.getNumberPart().equals(RationalNumber.ZERO))
            {
                // Display the number part if either g is ZERO or its number part is nonzero.
                output.appendOutput(g.getNumberPart().toOutput());
            }
            if (g.getUpMultiplePart() != 0)
            {
                StyledTextOutput.Symbol upSymbol;
                if (g.getUpMultiplePart() == 2) upSymbol = DOUBLE_UP;
                //else if (g.getUpMultiplePart() == 3) upSymbol = TRIPLE_UP;
                //else if (g.getUpMultiplePart() == 4) upSymbol = QUADRUPLE_UP;
                else if (g.getUpMultiplePart() == -2) upSymbol = DOUBLE_DOWN;
                else if (g.getUpMultiplePart() > 0) upSymbol = Symbol.UP;
                else upSymbol = DOWN;
                output.appendSymbol(upSymbol);
                if (Math.abs(g.getUpMultiplePart()) > 2)
                {
                    output.appendMath(String.valueOf(Math.abs(g.getUpMultiplePart())));
                }
            }
            if (g.getNimberPart() != 0)
            {
                output.appendSymbol(Symbol.STAR);
                if (g.getNimberPart() > 1)
                {
                    output.appendMath(String.valueOf(g.getNimberPart()));
                }
            }
            if (forceParens && !g.isNumber() && !g.isNimber() &&
                !(g.getNumberPart().equals(RationalNumber.ZERO) && g.getNimberPart() == 0))
            {
                // Not a number, nimber, or up multiple.  Force parens to clarify.
                output.appendMath(")");
            }
            return 0;
        }
        // Check for a switch.
        else if (g.isSwitch())
        {
            output.appendSymbol(PLUS_MINUS);
            if (g.getNumLeftOptions() > 1)
            {
                output.appendMath("(");
            }
            Integer[] sortedLeftOptions = sortedLeftOptions(id);
            for (int i = 0; i < sortedLeftOptions.length; i++)
            {
                createFromId(sortedLeftOptions[i]).toOutput(output, true, g.getNumLeftOptions() == 1);
                if (i < sortedLeftOptions.length - 1)
                {
                    output.appendMath(",");
                }
            }
            if (g.getNumLeftOptions() > 1)
            {
                output.appendMath(")");
            }
            return 0;
        }
        else if (g.isNumberTiny() || inverse.isNumberTiny())
        {
            boolean tiny = g.isNumberTiny();
            
            String str;
            CanonicalShortGame translate;
            CanonicalShortGame subscript;
            
            if (tiny)
            {
                str = "Tiny";
                translate = g.getLeftOption(0);
                subscript = g.getRightOption(0).getRightOption(0).negate().add(translate);
            }
            else
            {
                str = "Miny";
                translate = g.getRightOption(0);
                subscript = g.getLeftOption(0).getLeftOption(0).subtract(translate);
            }
            
            if (forceParens)
            {
                output.appendMath("(");
            }
            if (!translate.equals(CanonicalShortGame.ZERO))
            {
                output.appendOutput(translate.getNumberPart().toOutput());
                output.appendText(Output.Mode.PLAIN_TEXT, "+");
            }
            // First get a sequence for the subscript.  If that sequence contains any
            // subscripts or superscripts, then we display this as Tiny(G) rather than +_G.
            StyledTextOutput sub = new StyledTextOutput();
            subscript.toOutput(sub, true, true);
            EnumSet<StyledTextOutput.Style> styles = sub.allStyles();
            styles.retainAll(StyledTextOutput.Style.TRUE_LOCATIONS);
            if (styles.isEmpty())
            {
                if (subscript.isNumber() && !subscript.isInteger())
                {
                    output.appendText(Output.Mode.PLAIN_TEXT, "(");
                }
                output.appendSymbol(
                    EnumSet.noneOf(StyledTextOutput.Style.class),
                    EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)),
                    tiny ? TINY : MINY
                    );
                output.appendOutput(EnumSet.of(LOCATION_SUBSCRIPT), sub);
                if (subscript.isNumber() && !subscript.isInteger())
                {
                    output.appendText(Output.Mode.PLAIN_TEXT, ")");
                }
                output.appendText(Output.Mode.PLAIN_TEXT, "." + str);
            }
            else
            {
                output.appendOutput(sub);
                output.appendMath("." + str);
            }
            if (forceParens)
            {
                output.appendMath(")");
            }
            return 0;
        }
        
        UptimalExpansion uptimal = uptimalExpansion(id);
        if (uptimal != null && (uptimal.isUnit() || uptimal.isUnitSum()))
        {
            if (!uptimal.getNumberPart().equals(RationalNumber.ZERO))
            {
                output.appendOutput(uptimal.getNumberPart().toOutput());
            }
        
            if (uptimal.isUnit())
            {
                int n = 0;
                boolean up = false;
                for (n = 1; n <= uptimal.length(); n++)
                {
                    if (uptimal.getCoefficient(n) != 0)
                    {
                        up = (uptimal.getCoefficient(n) == 1);
                        break;
                    }
                }
                if (up)
                {
                    output.appendSymbol(Symbol.UP);
                    output.appendText(Output.Mode.PLAIN_TEXT, ".Pow(");
                    output.appendText(
                        EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT),
                        String.valueOf(n)
                        );
                    output.appendText(Output.Mode.PLAIN_TEXT, ")");
                }
                else
                {
                    output.appendSymbol(Symbol.DOWN);
                    output.appendText(Output.Mode.PLAIN_TEXT, ".Pow(");
                    output.appendText(
                        EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT),
                        String.valueOf(n)
                        );
                    output.appendText(Output.Mode.PLAIN_TEXT, ")");
                }
            }
            else if (uptimal.isUnitSum())
            {
                int n = uptimal.length();
                boolean up = (uptimal.getCoefficient(1) > 0);
                if (up)
                {
                    output.appendSymbol(Symbol.UP);
                    output.appendText(Output.Mode.PLAIN_TEXT, ".PowTo(");
                    output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), "[");
                    output.appendText(
                        EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT),
                        String.valueOf(n)
                        );
                    output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), "]");
                    output.appendText(Output.Mode.PLAIN_TEXT, ")");
                }
                else
                {
                    output.appendSymbol(Symbol.DOWN);
                    output.appendText(Output.Mode.PLAIN_TEXT, ".PowTo(");
                    output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), "[");
                    output.appendText(
                        EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT),
                        String.valueOf(n)
                        );
                    output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), "]");
                    output.appendText(Output.Mode.PLAIN_TEXT, ")");
                }
            }
            
            if (uptimal.hasBase())
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "+");
                output.appendSymbol(Symbol.STAR);
            }
            
            return 0;
        }

        // General case.

        StyledTextOutput leftOutput = new StyledTextOutput(), rightOutput = new StyledTextOutput();

        // First we build the left & right OS's and calculate the number of slashes.
        // There are several cases.

        int numSlashes = 1;
        
        Integer[] sortedLeftOptions = sortedLeftOptions(id);
        int numLO = sortedLeftOptions.length;
        
        for (int i = 0; i < numLO; i++)
        {
            numSlashes = Math.max(
                numSlashes,
                createFromId(sortedLeftOptions[i]).toOutput(leftOutput, numLO > 1, false) + 1
                );
            if (i < numLO - 1)
            {
                leftOutput.appendMath(",");
            }
        }
        
        Integer[] sortedRightOptions = sortedRightOptions(id);
        int numRO = sortedRightOptions.length;
        
        for (int i = 0; i < numRO; i++)
        {
            numSlashes = Math.max(
                numSlashes,
                createFromId(sortedRightOptions[i]).toOutput(rightOutput, numRO > 1, false) + 1
                );
            if (i < numRO - 1)
            {
                rightOutput.appendMath(",");
            }
        }

        // Now we build our output sequence.

        if (forceBrackets || numSlashes == maxSlashes)
        {
            output.appendMath("{");
        }
        output.appendOutput(leftOutput);
        output.appendMath(getSlashString(numSlashes));
        output.appendOutput(rightOutput);
        if (forceBrackets || numSlashes == maxSlashes)
        {
            output.appendMath("}");
            return 0;
        }
        else
        {
            return numSlashes;
        }
    }

    static String getSlashString(int n)
    {
        String slashString = "";
        for (int i = 0; i < n; i++) slashString += "|";
        return slashString;
    }

    ////////////////////////////////////////////////////////////////////////
    // Implementation of Game

    @Override
    public CgsuiteSet getLeftOptions()
    {
        CgsuiteSet options = new CgsuiteSet();
        for (int i = 0; i < getNumLeftOptions(); i++)
        {
            options.add(getLeftOption(i).simplify());
        }
        return options;
    }

    @Override
    public CgsuiteSet getRightOptions()
    {
        CgsuiteSet options = new CgsuiteSet();
        for (int i = 0; i < getNumRightOptions(); i++)
        {
            options.add(getRightOption(i).simplify());
        }
        return options;
    }

    @Override
    public CanonicalShortGame negate()
    {
        return createFromId(getInverse(id));
    }

    private static int getInverse(int id)
    {
        if (isNumberUpStar(id))
        {
            return constructNus(getNumberPart(id).negate(), -getUpMultiplePart(id), getNimberPart(id));
        }

        int result = lookupOpResult(OPERATION_INVERSE, id, -1);
        if (result != -1)
        {
            return result;
        }

        int[] newLeftOptions = new int[getNumRightOptions(id)],
              newRightOptions = new int[getNumLeftOptions(id)];

        for (int i = 0; i < newLeftOptions.length; i++)
        {
            newLeftOptions[i] = getInverse(getRightOption(id, i));
        }
        for (int i = 0; i < newRightOptions.length; i++)
        {
            newRightOptions[i] = getInverse(getLeftOption(id, i));
        }

        result = constructFromCanonicalOptions(newLeftOptions, newRightOptions);
        storeOpResult(OPERATION_INVERSE, id, -1, result);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////
    // Information extraction.

    /**
     * Gets the number of left options of this game.
     *
     * @return  The number of left options of this game.
     */
    public int getNumLeftOptions()
    {
        return getNumLeftOptions(id);
    }

    private static int getNumLeftOptions(int id)
    {
        return (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & NUM_LO_MASK) >> NUM_LO_SHIFT;
    }

    /**
     * Gets the <code>n</code><sup>th</sup> left option of this game.
     *
     * @param   n The index of the option to get.
     * @return  The <code>n</code><sup>th</sup> left option of this game.
     * @throws  IndexOutOfBoundsException <code>n < 0</code> or
     *          <code>n >= getNumLeftOptions()</code>.
     */
    public CanonicalShortGame getLeftOption(int n)
    {
        return createFromId(getLeftOption(id, n));
    }

    private static int getLeftOption(int id, int n)
    {
        return data[id >> SECTOR_BITS][(id+2+n) & SECTOR_MASK];
    }

    /**
     * Gets the number of right options of this game.
     *
     * @return  The number of right options of this game.
     */
    public int getNumRightOptions()
    {
        return getNumRightOptions(id);
    }

    private static int getNumRightOptions(int id)
    {
        return data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & NUM_RO_MASK;
    }

    /**
     * Gets the <code>n</code><sup>th</sup> right option of this game.
     *
     * @param   n The index of the option to get.
     * @return  The <code>n</code><sup>th</sup> right option of this game.
     * @throws  IndexOutOfBoundsException <code>n < 0</code> or
     *          <code>n >= getNumRightOptions()</code>.
     */
    public CanonicalShortGame getRightOption(int n)
    {
        return createFromId(getRightOption(id, n));
    }

    private static int getRightOption(int id, int n)
    {
        int numLO = (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & NUM_LO_MASK) >> NUM_LO_SHIFT;
        return data[id >> SECTOR_BITS][(id+2+numLO+n) & SECTOR_MASK];
    }

    /**
     * Tests whether this game is equal to {@link #ZERO}.
     *
     * @return  <code>true</code> if this game is equal to <code>ZERO</code>.
     */
    public boolean isZero()
    {
        return id == ZERO.id;
    }

    /**
     * Tests whether this game is equal to {@link #STAR}.
     *
     * @return  <code>true</code> if this game is equal to <code>STAR</code>.
     */
    public boolean isStar()
    {
        return id == STAR.id;
    }

    /**
     * Tests whether this game is positive.
     *
     * @return  <code>true</code> if this game is positive.
     */
    public boolean isPositive()
    {
        return id != ZERO.id && leq(ZERO.id, id);
    }

    /**
     * Tests whether this game is negative.
     *
     * @return  <code>true</code> if this game is negative.
     */
    public boolean isNegative()
    {
        return id != ZERO.id && leq(id, ZERO.id);
    }

    /**
     * Tests whether this game is fuzzy.
     *
     * @return  <code>true</code> if this game is fuzzy.
     */
    public boolean isFuzzy()
    {
        return !leq(id, ZERO.id) && !leq(ZERO.id, id);
    }

    /**
     * Tests whether this game is a number.
     *
     * @return  <code>true</code> if this game is a number.
     */
    public boolean isNumber()
    {
        return isNumber(id);
    }

    private static boolean isNumber(int id)
    {
        return isNumberUpStar(id) && getNimberPart(id) == 0 && getUpMultiplePart(id) == 0;
    }

    /**
     * Tests whether this game is an integer.
     *
     * @return  <code>true</code> if this game is an integer.
     */
    public boolean isInteger()
    {
        return isInteger(id);
    }
    
    private static boolean isInteger(int id)
    {
        // Integers can only be stored in small nus records, so this is safe.
        // The first clause checks that this is a small record for a NUS; the
        // second clause checks that it's an integer.
        return (data[id >> SECTOR_BITS][(id+1) & SECTOR_MASK] & (RECORD_TYPE_MASK | IS_NUS_MASK))
                == (STD_OPTIONS_RECORD | IS_NUS_MASK)
            && (data[id >> SECTOR_BITS][(id-2) & SECTOR_MASK] &
              (DENOMINATOR_MASK | UP_MULTIPLE_MASK | NIMBER_MASK)) == 0;
    }

    /**
     * Tests whether this game is the sum of a number, a nimber, and a multiple
     * of up.
     *
     * @return  <code>true</code> if this game is the sum of a number, a nimber,
     * and a multiple of up.
     */
    public boolean isNumberUpStar()
    {
        return isNumberUpStar(id);
    }

    private static boolean isNumberUpStar(int id)
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

    /**
     * Tests whether this game is a nimber.
     *
     * @return  <code>true</code> if this game is a nimber.
     */
    public boolean isNimber()
    {
        return isNimber(id);
    }

    private static boolean isNimber(int id)
    {
        // TODO: Make this more efficient for small records?
        return isNumberUpStar(id) && getNumberPart(id).equals(RationalNumber.ZERO) && getUpMultiplePart(id) == 0;
    }

    /**
     * Tests whether this game is an infinitesimal.
     *
     * @return  <code>true</code> if this game is an infinitesimal.
     */
    public boolean isInfinitesimal()
    {
        if (isNumberUpStar())
        {
            return getNumberPart(id).equals(RationalNumber.ZERO);
        }
        else
        {
            return leftStop().equals(RationalNumber.ZERO) && rightStop().equals(RationalNumber.ZERO);
        }
    }

    /**
     * Tests whether this game is numberish.
     *
     * @return  <code>true</code> if this game is numberish.
     */
    public boolean isNumberish()
    {
        if (isNumberUpStar())
        {
            return true;
        }
        else
        {
            return leftStop().equals(rightStop());
        }
    }

    /**
     * Tests whether this game is equal to a number plus a tiny.
     *
     * @return  <code>true</code> if this game is a number plus a tiny.
     */
    public boolean isNumberTiny()
    {
        return isNumberTiny(id);
    }

    private static boolean isNumberTiny(int id)
    {
        if (getNumLeftOptions(id) != 1 || getNumRightOptions(id) != 1 ||
            !isNumber(getLeftOption(id, 0)) ||
            getNumLeftOptions(getRightOption(id, 0)) != 1 ||
            getNumRightOptions(getRightOption(id, 0)) != 1 ||
            getLeftOption(id, 0) != getLeftOption(getRightOption(id, 0), 0))
        {
            return false;
        }
        return leftStop(getRightOption(getRightOption(id, 0), 0)).compareTo
            (getNumberPart(getLeftOption(id, 0))) < 0;
    }

    /**
     * Tests whether this game is a switch.  A <i>switch</i> is a game
     * equal to its own inverse.
     *
     * @return  <code>true</code> if this game is a switch.
     */
    public boolean isSwitch()
    {
        return id == getInverse(id);
    }

    /**
     * Gets the number part of a number-up-star.  Note that
     * <code>isNumberUpStar()</code> must be <code>true</code> in order to
     * call this method.
     *
     * @return  The number part of this game.
     * @throws  UnsupportedOperationException This game is not a number-up-star.
     */
    public RationalNumber getNumberPart()
    {
        if (isNumberUpStar())
        {
            return getNumberPart(id);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    private static RationalNumber getNumberPart(int id)
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
            return new RationalNumber(
                new BigInteger(numArray),
                BigInteger.ONE.shiftLeft(sector[sectorOffset + 1] & EXT_DENOMINATOR_MASK)
                );
        }
        else
        {
            // Standard record.
            return new RationalNumber(getSmallNumeratorPart(id), 1 << getDenExpPart(id));
        }
    }
    
    public CgsuiteInteger getNumeratorPart()
    {
        if (isNumberUpStar(id))
        {
            if (isExtendedRecord(id))
            {
                return new CgsuiteInteger(getNumberPart(id).getNumerator());
            }
            else
            {
                return new CgsuiteInteger(getSmallNumeratorPart(id));
            }
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    public CgsuiteInteger getDenominatorPart()
    {
        int denExp = getDenExpPart(id);
        if (denExp <= 62)
        {
            return new CgsuiteInteger(1 << denExp);
        }
        else
        {
            return new CgsuiteInteger(BigInteger.ONE.shiftLeft(denExp));
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

    /**
     * Gets the up multiple part of a number-up-star.  Note that
     * <code>isNumberUpStar()</code> must be <code>true</code> in order to
     * call this method.
     *
     * @return  The up multiple part of this game.
     * @throws  UnsupportedOperationException This game is not a number-up-star.
     */
    public int getUpMultiplePart()
    {
        if (!isNumberUpStar())
        {
            throw new UnsupportedOperationException();
        }
        return getUpMultiplePart(id);
    }

    private static int getUpMultiplePart(int id)
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

    /**
     * Gets the nimber part of a number-up-star.  Note that
     * <code>isNumberUpStar()</code> must be <code>true</code> in order to
     * call this method.
     *
     * @return  The nimber part of this game.
     * @throws  UnsupportedOperationException This game is not a number-up-star.
     */
    public int getNimberPart()
    {
        if (!isNumberUpStar())
        {
            throw new UnsupportedOperationException();
        }
        return getNimberPart(id);
    }

    private static int getNimberPart(int id)
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

    ////////////////////////////////////////////////////////////////////////
    // Canonical operations.

    /**
     * Gets a unique integer identifier for this game.  The integer identifier
     * is valid until the next time {@link #reinit() reinit} is called.
     *
     * @return  A unique integer identifier for this game.
     */
    public int getID()
    {
        return id;
    }

    /**
     * Calculates the birthday of this game.  The birthday of G is defined
     * recursively by
     * <p>
     * <code>birthday(G) = Max(birthday(G<sup>L</sup>) + 1, birthday(G<sup>R</sup>) + 1)</code>.
     *
     * @return  The birthday of this game.
     */
    public int birthday()
    {
        return birthday(id);
    }

    private static int birthday(int id)
    {
        if (isNumberUpStar(id))
        {
            int denExp = getDenExpPart(id);
            int upMag = Math.abs(getUpMultiplePart(id));
            int nimber = getNimberPart(id);

            int numberBirthday;
            if (isExtendedRecord(id))
            {
                BigInteger numMag = getNumberPart(id).getNumerator().abs();
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

    /**
     * Cools this game by <code>temperature</code> and returns the resulting
     * <code>CanonicalGame</code>.
     *
     * @param   temperature The temperature to cool by.
     * @return  This game cooled by <code>temperature</code>.
     * @throws  IllegalArgumentException <code>temperature</code> is
     *          negative or infinite.
     */
    public CanonicalShortGame cool(RationalNumber temperature)
    {
        if (temperature.compareTo(RationalNumber.ZERO) < 0 ||
            !temperature.isDyadic() ||
            !temperature.isSmall())
        {
            throw new IllegalArgumentException
                ("Temperature must be a non-negative dyadic rational.");
        }
        else if (temperature.equals(RationalNumber.ZERO))
        {
            return this;
        }
        else
        {
            return createFromId(cool(id, temperature, constructNus(temperature, 0, 0)));
        }
    }

    private static int cool(int id, RationalNumber temperature, int temperatureId)
    {
        if (isNumber(id))
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

    /**
     * Cools this game by its temperature and returns the resulting
     * <code>CanonicalGame</code>.  If this game's temperature is zero or
     * negative, then the return value is <code>this</code>.
     *
     * @return  This game cooled by its temperature, or <code>this</code>
     *          if the temperature is zero or negative.
     */
    public CanonicalShortGame freeze()
    {
        RationalNumber t = temperature();
        if (t.compareTo(RationalNumber.ZERO) <= 0)
        {
            return this;
        }
        else
        {
            return cool(t);
        }
    }

    /**
     * Heats this game by <code>t</code> and returns the resulting
     * <code>CanonicalGame</code>.
     * <p>
     * Heating by an arbitrary <code>CanonicalGame</code> is permitted.
     * Note that heating a game by a negative number corresponds to the
     * "unheating" operation.
     *
     * @param   t The game to heat by.
     * @return  This game heated by <code>t</code>.
     * @see     #overheat(CanonicalGame, CanonicalGame) overheat
     */
    public CanonicalShortGame heat(CanonicalShortGame t)
    {
        return createFromId(heat(id, t.id));
    }

    private static int heat(int id, int tId)
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

    /**
     * Overheats this game from <code>s</code> to <code>t</code> and returns
     * the resulting <code>CanonicalGame</code>.
     * <p>
     * Overheating by arbitrary <code>CanonicalGame</code>s is permitted.
     *
     * @param   s The "lower limit of integration."
     * @param   t The "upper limit of integration."
     * @return  This game overheated from <code>s</code> to </code>t</code>.
     * @see     #heat(CanonicalGame) heat
     */
    public CanonicalShortGame overheat(CanonicalShortGame s, CanonicalShortGame t)
    {
        return createFromId(overheat(id, s.id, t.id));
    }

    private static int overheat(int id, int sId, int tId)
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
    
    /**
     * Calculates the sum of this game and <code>h</code> and returns the resulting
     * <code>CanonicalGame</code>.
     *
     * @param   h The game to add to this game.
     * @return  The sum of this game and <code>h</code>.
     */
    public CanonicalShortGame add(CanonicalShortGame h)
    {
        return createFromId(add(id, h.id));
    }

    private static int add(int gId, int hId)
    {
        if (isNumberUpStar(gId) && isNumberUpStar(hId))
        {
            if (isExtendedRecord(gId) || isExtendedRecord(hId))
            {
                // At least one of the terms is an extended NUS.  Do the
                // calculations using org.cgsuite.util.Rational.  This is SLOW, but
                // completely general and hopefully rare.
                return constructNus(
                    getNumberPart(gId).add(getNumberPart(hId)),
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
                if (sumNum <= Integer.MAX_VALUE && sumNum >= Integer.MIN_VALUE)
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
                        new RationalNumber(sumNum, sumDen),
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
    
    /**
     * Calculates the difference of this game and <code>h</code> and returns the
     * resulting <code>CanonicalGame</code>.
     * <p>
     * <code>subtract(h)</code> is equivalent to
     * <code>add((CanonicalGame) h.getInverse())</code>.
     *
     * @param   h The game to subtract from this game.
     * @return  The difference of this game and <code>h</code>.
     */
    public CanonicalShortGame subtract(CanonicalShortGame h)
    {
        return createFromId(subtract(id, h.id));
    }

    private static int subtract(int gId, int hId)
    {
        return add(gId, getInverse(hId));
    }

    /**
     * Calculates the left stop of this game.
     *
     * @return  The left stop of this game.
     */
    public RationalNumber leftStop()
    {
        return leftStop(id);
    }

    private static RationalNumber leftStop(int id)
    {
        if (isNumber(id))
        {
            return getNumberPart(id);
        }

        RationalNumber stop = RationalNumber.NEGATIVE_INFINITY;
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            stop = stop.max(rightStop(getLeftOption(id, i)));
        }
        return stop;
    }

    /**
     * Calculates the right stop of this game.
     *
     * @return  The right stop of this game.
     */
    public RationalNumber rightStop()
    {
        return rightStop(id);
    }

    private static RationalNumber rightStop(int id)
    {
        if (isNumber(id))
        {
            return getNumberPart(id);
        }

        RationalNumber stop = RationalNumber.POSITIVE_INFINITY;
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            stop = stop.min(leftStop(getRightOption(id, i)));
        }
        return stop;
    }

    /**
     * Calculates the atomic weight of this game.  If this game is all small,
     * the standard definition of atomic weight is used.  Otherwise, we use the
     * following algorithm, suggested by David Wolfe:
     * <p>
     * Apply the standard definition of atomic weight, even if the game is not
     * all small.  Then check that the difference between <code>g</code> and
     * <code>G.&uarr;</code> is sufficiently small.  Specifically, check that
     * <p>
     * <code>g-e <= G.^ <= g+e</code>
     * <p>
     * where e is the difference between <code>&uarr;*</code> and a long kite.
     * The value <code>G</code> will be returned regardless, and a warning
     * will be generated if the check fails.
     *
     * @return  The atomic weight of this game.
     * @throws  UnsupportedOperationException This game is not an
     *          infinitesimal.
     */
    public CanonicalShortGame atomicWeight()
    {
        if (isAllSmall())
        {
            return createFromId(naiveAtomicWeight(id));
        }
        else if (!isInfinitesimal())
        {
            throw new InputException("That game is not atomic.");
        }

        // We use the following algorithm suggested by David Wolfe:
        // Calculate the "naive atomic weight" using the standard method.
        // Then check that g-e <= G.^ <= g+e
        // where e is the difference of ^* and a long kite.

        CanonicalShortGame g = createFromId(naiveAtomicWeight(id));
        CanonicalShortGame difference = this.subtract(g.nortonMultiply(UP));

        int farStar = farStar(id), nextPow2 = 2;
        while (nextPow2 < farStar)
        {
            nextPow2 <<= 1;
        }

        int redKite = ordinalSum(constructNus(0, 0, 0, nextPow2), constructInteger(-1));
        CanonicalShortGame e = createFromId(add(UP_STAR.id, redKite));

        if (!(difference.leq(e) && (e.negate()).leq(difference)))
        {
            throw new InputException("That game is not atomic.");
        }

        return g;
    }

    private static int naiveAtomicWeight(int id)
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
            newLeftOptions[i] = add(naiveAtomicWeight(getLeftOption(id, i)), MINUS_TWO.id);
        }
        for (int i = 0; i < newRightOptions.length; i++)
        {
            newRightOptions[i] = add(naiveAtomicWeight(getRightOption(id, i)), TWO.id);
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
                    result = ZERO.id;
                }
                else
                {
                    // g < farStar.  Find the least integer n such that
                    // n |> every newLeftOption.
                    // Note: This could be optimized a bit.
                    RationalNumber maxLeastInteger = RationalNumber.NEGATIVE_INFINITY;
                    for (int i = 0; i < newLeftOptions.length; i++)
                    {
                        RationalNumber leastInteger = rightStop(newLeftOptions[i]).ceiling();
                        if (leq(constructNus(leastInteger, 0, 0), newLeftOptions[i]))
                        {
                            leastInteger = leastInteger.add(RationalNumber.ONE);
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
                    result = ZERO.id;
                }
                else
                {
                    // g > farStar.  Now the greatest integer n such that
                    // n <| every newRightOption.
                    RationalNumber minGreatestInteger = RationalNumber.POSITIVE_INFINITY;
                    for (int i = 0; i < newRightOptions.length; i++)
                    {
                        RationalNumber greatestInteger = leftStop(newRightOptions[i]).floor();
                        if (leq(newRightOptions[i], constructNus(greatestInteger, 0, 0)))
                        {
                            greatestInteger = greatestInteger.subtract(RationalNumber.ONE);
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

    private static int farStar(int id)
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

    /**
     * Calculates the companion of this game.
     *
     * @return  The companion of this game.
     */
    public CanonicalShortGame companion()
    {
        return createFromId(companion(id));
    }

    private static int companion(int id)
    {
        if (isNumberUpStar(id) && getNumberPart(id).equals(RationalNumber.ZERO))
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
        boolean leq = leq(id, ZERO.id);
        boolean geq = leq(ZERO.id, id);
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
            leftOptions[leftOptions.length-1] = ZERO.id;
        }
        if (leq)
        {
            rightOptions[rightOptions.length-1] = ZERO.id;
        }
        return constructFromOptions(leftOptions, rightOptions);
    }

    /**
     * Gets the star-projection of this game.  This is defined as
     * <code>p(G) = x</code> if <code>G = x</code> or <code>x*</code> for
     * some number <code>x</code>, and
     * <code>p(G) = {p(G<sup>L</sup>)|p(G<sup>R</sup>)</code> otherwise.
     *
     * @return  The star-projection of this game.
     */
    public CanonicalShortGame starProjection()
    {
        return createFromId(starProjection(id));
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

    /**
     * Calculates the reduced canonical form of this game.  The reduced
     * canonical form of <code>G</code> is the simplest game infinitesimally
     * close to <code>G</code>.
     *
     * @return  The reduced canonical form of this game.
     */
    public CanonicalShortGame rcf()
    {
        return createFromId(rcf(id));
    }

    private static int rcf(int id)
    {
        if (isNumberUpStar(id))
        {
            return constructNus(getNumberPart(id), 0, 0);
        }
        else
        {
            return starProjection(heat(id, STAR.id));
        }
    }

    /**
     * Compares this game with another <code>CanonicalGame</code>.
     *
     * @param   h The game to compare this game with.
     * @return  <code>true</code> if this game is less than or equal to
     * <code>h</code>.
     */
    public boolean leq(CanonicalShortGame h)
    {
        return leq(id, h.id);
    }

    private static boolean leq(int gId, int hId)
    {
        if (gId == hId)
        {
            return true;
        }
        
        if (Thread.interrupted())
        {
            throw new InputException("Calculation canceled by user.");
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

    /**
     * Calculates the left incentives of this game.
     *
     * @return  The left incentives of this game.
     */
    public CgsuiteSet leftIncentives()
    {
        int[] incentiveIds = new int[getNumLeftOptions()];
        for (int i = 0; i < incentiveIds.length; i++)
        {
            incentiveIds[i] = subtract(getLeftOption(id, i), id);
        }
        eliminateDuplicateOptions(incentiveIds);
        eliminateDominatedOptions(incentiveIds, true);
        return intArrayToCgSet(pack(incentiveIds));
    }

    /**
     * Calculates the right incentives of this game.
     *
     * @return  The right incentives of this game.
     */
    public CgsuiteSet rightIncentives()
    {
        int[] incentiveIds = new int[getNumRightOptions()];
        for (int i = 0; i < incentiveIds.length; i++)
        {
            incentiveIds[i] = subtract(id, getRightOption(id, i));
        }
        eliminateDuplicateOptions(incentiveIds);
        eliminateDominatedOptions(incentiveIds, true);
        return intArrayToCgSet(pack(incentiveIds));
    }

    /**
     * Calculates the incentives of this game.
     *
     * @return  The incentives of this game.
     */
    public CgsuiteSet incentives()
    {
        return intArrayToCgSet(incentives(id));
    }

    private static int[] incentives(int id)
    {
        int[] incentiveIds = new int[getNumLeftOptions(id) + getNumRightOptions(id)];
        for (int i = 0; i < getNumLeftOptions(id); i++)
        {
            incentiveIds[i] = subtract(getLeftOption(id, i), id);
        }
        for (int i = 0; i < getNumRightOptions(id); i++)
        {
            incentiveIds[getNumLeftOptions(id) + i] = subtract(id, getRightOption(id, i));
        }
        eliminateDuplicateOptions(incentiveIds);
        eliminateDominatedOptions(incentiveIds, true);
        return pack(incentiveIds);
    }

    private static CanonicalShortGame[] intArrayToCgArray(int[] intArray)
    {
        CanonicalShortGame[] cgArray = new CanonicalShortGame[intArray.length];
        for (int i = 0; i < intArray.length; i++)
        {
            cgArray[i] = createFromId(intArray[i]);
        }
        return cgArray;
    }

    private static CgsuiteSet intArrayToCgSet(int[] intArray)
    {
        CgsuiteSet set = new CgsuiteSet();
        for (int i = 0; i < intArray.length; i++)
        {
            set.add(createFromId(intArray[i]));
        }
        return set;
    }

    /**
     * Calculates the Norton product of this game by the unit <code>u</code>.
     * <p>
     * If this game is an integer <code>n</code>, the result is equal to
     * the sum of <code>n</code> copies of <code>u</code>.  A binary sum
     * algorithm is used for efficient calculation when <code>n</code> is
     * large.
     * <p>
     * If this game is not an integer, the result is equal to...
     *
     * @param   u The unit of the Norton product.
     * @return  The Norton product of this game by <code>u</code>.
     */
    public CanonicalShortGame nortonMultiply(CanonicalShortGame u)
    {
        return createFromId(nortonMultiply(id, u.id));
    }

    private static int nortonMultiply(int gId, int uId)
    {
        int result = lookupOpResult(OPERATION_NORTON_MULTIPLY, gId, uId);
        if (result != -1)
        {
            return result;
        }

        result = ZERO.id;

        if (isInteger(gId))
        {
            int multiple = getNumberPart(gId).intValue(),
                positiveMultiple = multiple < 0 ? -multiple : multiple;
            int binarySum = multiple < 0 ? getInverse(uId) : uId;

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
            int[] uPlusIncentives = incentives(uId);
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

    /**
     * Calculates the Conway product of this game and <code>h</code>.
     * The Conway product is defined and discussed in ONAG.
     *
     * @param   h The game by which to multiply this game.
     * @return  The Conway product of this game and <code>h</code>.
     */
    public CanonicalShortGame conwayMultiply(CanonicalShortGame h)
    {
        return createFromId(conwayMultiply(id, h.id));
    }

    private static int conwayMultiply(int gId, int hId)
    {
        if (isNimber(gId) && isNimber(hId))
        {
            BigInteger m = BigInteger.valueOf(getNimberPart(gId));
            BigInteger n = BigInteger.valueOf(getNimberPart(hId));
            return constructNus(RationalNumber.ZERO, 0, CgsuiteInteger.nimProduct(m, n).intValue());
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

    /**
     * Calculates the ordinal sum of this game and <code>h</code>.
     * The ordinal sum of <code>G</code> and <code>H</code> is defined by:
     * <p>
     * <code>G:H = { G<sup>L</sup>, G:H<sup>L</sup> | G<sup>R</sup>, G:H<sup>R</sup> }</code>
     *
     * @param   h The right component of the ordinal sum.
     * @return  The ordinal sum of this game and <code>h</code>.
     */
    public CanonicalShortGame ordinalSum(CanonicalShortGame h)
    {
        return createFromId(ordinalSum(id, h.id));
    }

    private static int ordinalSum(int gId, int hId)
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

    /**
     * Returns <code>true</code> if this game is all small.
     *
     * @return  <code>true</code> if this game is all small, false otherwise.
     */
    public boolean isAllSmall()
    {
        return isAllSmall(id);
    }

    private static boolean isAllSmall(int id)
    {
        if (isNumberUpStar(id))
        {
            return getNumberPart(id).equals(RationalNumber.ZERO);
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

    /**
     * Tests whether this game is even.  A game is even if all its options
     * are odd.
     *
     * @return  <code>true</code> if this game is even.
     * @see     #isOdd() isOdd
     */
    public boolean isEven()
    {
        return isEven(id);
    }

    private static boolean isEven(int id)
    {
        if (isNumberUpStar(id))
        {
            return !isExtendedRecord(id) && getUpMultiplePart(id) == 0 && getNimberPart(id) <= 1 &&
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

    /**
     * Tests whether this game is odd.  A game is odd if it is nonzero and
     * all its options are even.
     *
     * @return  <code>true</code> if this game is odd.
     * @see     #isEven() isEven
     */
    public boolean isOdd()
    {
        return isOdd(id);
    }

    private static boolean isOdd(int id)
    {
        if (isNumberUpStar(id))
        {
            return !isExtendedRecord(id) && getUpMultiplePart(id) == 0 && getNimberPart(id) <= 1 &&
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

    /**
     * Tests whether this game is even-tempered.  A game is even-tempered if it
     * is a number or all its options are odd-tempered.
     *
     * @return  <code>true</code> if this game is even-tempered.
     * @see     #isOddTempered() isOddTempered
     */
    public boolean isEvenTempered()
    {
        return isEvenTempered(id);
    }

    private static boolean isEvenTempered(int id)
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

    /**
     * Tests whether this game is odd-tempered.  A game is odd-tempered if it
     * is not a number and all its options are even-tempered.
     *
     * @return  <code>true</code> if this game is odd-tempered.
     * @see     #isEvenTempered() isEvenTempered
     */
    public boolean isOddTempered()
    {
        return isOddTempered(id);
    }

    private static boolean isOddTempered(int id)
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

    /**
     * Tests whether this game is well-tempered.  A game is well-tempered if
     * it is even-tempered or odd-tempered.
     */
    public boolean isWellTempered()
    {
        return isEvenTempered(id) || isOddTempered(id);
    }

    /**
     * Calculates the diversity of this game.  The diversity is equal to the
     * number of <i>canonically distinct</i> subpositions.
     *
     * @return  The diversity of this game.
     * @see     #stopCount() stopCount
     */
    public int diversity()
    {
        Set<Integer> followers = new HashSet<Integer>(1024);
        addFollowers(id, followers);
        return followers.size();
    }

    private static void addFollowers(int id, Set<Integer> followers)
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

    /**
     * Calculates the number of distinct stops in this game.  This is equal
     * to the number of canonical lines of play, assuming play ceases when
     * the position reaches a number.
     * <p>
     * Note that this is usually different from
     * {@link #diversity() diversity}.  For example, <code>*10</code> has
     * diversity 11, but stop count 39366.
     *
     * @return  The number of distinct stops in this game.
     * @see     #diversity() diversity
     */
    public BigInteger stopCount()
    {
        return stopCount(id);
    }

    // TODO Caching?
    private BigInteger stopCount(int id)
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

    /**
     * Calculates a minimal set of orthodox left options for this game.  That
     * is, for any temperature <code>t</code>, there is a Left option among the
     * return values which is orthodox at <code>t</code>, and the set of return
     * values is minimal with this property.
     *
     * @return  A minimal set of orthodox Left options for this game.
     */
    public CanonicalShortGame[] orthodoxLeftOptions()
    {
        return intArrayToCgArray(findOrthodoxOptions(id, true));
    }

    /**
     * Calculates a minimal set of orthodox right options for this game.  That
     * is, for any temperature <code>t</code>, there is a Right option among the
     * return values which is orthodox at <code>t</code>, and the set of return
     * values is minimal with this property.
     *
     * @return  A minimal set of orthodox Right options for this game.
     */
    public CanonicalShortGame[] orthodoxRightOptions()
    {
        return intArrayToCgArray(findOrthodoxOptions(id, false));
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
                dominantAtTemperature(therms, left, i, RationalNumber.ZERO))
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
                        wall.getCriticalPoint(k).compareTo(RationalNumber.ZERO) > 0 &&
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
        (Thermograph[] therms, boolean left, int i, RationalNumber temp)
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

    /**
     * Returns the canonical form of <code>{0||0|-G}</code>, where
     * <code>G</code> is this game.  If <code>G > e</code> for some positive
     * number <code>e</code>, then the return value is
     * <code>+<sub>G</sub></code>
     *
     * @return  The canonical form of <code>{0||0|-G}</code>.
     * @see     #miny() miny
     */
    public CanonicalShortGame tiny()
    {
        return CanonicalShortGame.construct(ZERO, CanonicalShortGame.construct(ZERO, negate()));
    }

    /**
     * Returns the canonical form of <code>{G|0||0}</code>, where
     * <code>G</code> is this game.  If <code>G > e</code> for some positive
     * number <code>e</code>, then the return value is
     * <code>-<sub>G</sub></code>.
     *
     * @return  The canonical form of <code>{G|0||0}</code>.
     * @see     #tiny() tiny
     */
    public CanonicalShortGame miny()
    {
        return CanonicalShortGame.construct(CanonicalShortGame.construct(this, ZERO), ZERO);
    }

    /**
     * Returns the canonical form of <code>G<sup>n</sup></code>, where
     * <code>G</code> is this game.  This game must be of the form
     * <code>{0|H}</code>.
     * <p>
     * <code>G<sup>n</sup></code> is defined as follows:
     * <p>
     * <code>G<sup>0</sup> = -H</code><br>
     * <code>G<sup>n</sup> = {0|H-G<sup>&rarr;n-1</sup>} =
     * {0|-G<sup>0</sup>-G<sup>1</sup>-...-G<sup>n-1</sup>}</code>
     *
     * @param   n The exponent.
     * @return  The canonical form of <code>G<sup>n</sup></code>.
     * @throws  IllegalArgumentException This game is not of the form
     *          <code>{0|H}</code>.
     * @see     #powTo(int) powTo
     */
    public CanonicalShortGame pow(int n)
    {
        if (getNumLeftOptions() != 1 || !getLeftOption(0).equals(ZERO) ||
            getNumRightOptions() != 1)
        {
            throw new IllegalArgumentException("This game is not of the form {0|H}.");
        }
        if (n == 0)
        {
            return getRightOption(0).negate();
        }
        else
        {
            return CanonicalShortGame.construct(ZERO, getRightOption(0).subtract(powTo(n-1)));
        }
    }

    /**
     * Returns the canonical form of <code>G<sup>&rarr;n</sup></code>, where
     * <code>G</code> is this game.  This game must be of the form
     * <code>{0|H}</code>.
     * <p>
     * <code>G<sup>&rarr;n</sup></code> is defined as follows:
     * <p>
     * <code>G<sup>&rarr;0</sup> = 0</code><br>
     * <code>G<sup>&rarr;n</sup> = {G<sup>&rarr;n-1</sup>|H} =
     * G<sup>1</sup>+G<sup>2</sup>+...+G<sup>n-1</sup></code>
     *
     * @param   n The exponent.
     * @return  The canonical form of <code>G<sup>&rarr;n</sup></code>.
     * @throws  IllegalArgumentException This game is not of the form
     *          <code>{0|H}</code>.
     * @see     #pow(int) pow
     */
    public CanonicalShortGame powTo(int n)
    {
        if (getNumLeftOptions() != 1 || !getLeftOption(0).equals(ZERO) ||
            getNumRightOptions() != 1)
        {
            throw new IllegalArgumentException("This game is not of the form {0|H}.");
        }
        if (n == 0)
        {
            return ZERO;
        }
        else
        {
            return CanonicalShortGame.construct(powTo(n-1), getRightOption(0));
        }
    }

    /**
     * Constructs an uptimal with the specified expansion.  This is equivalent
     * to
     * <p>
     * <code>CanonicalGame.uptimal(ue, {@link #UP})</code>
     *
     * @param   ue the uptimal expansion
     * @return  A <code>CanonicalGame</code> matching the specified expansion.
     * @see     #uptimalExpansion() uptimalExpansion
     * @since   0.7.1
     */
    public static CanonicalShortGame uptimal(UptimalExpansion ue)
    {
        return uptimal(ue, UP);
    }

    /**
     * Constructs an uptimal with the specified expansion and the specified
     * base.  The base must be of the form <code>{0|H}</code>.
     *
     * @param   ue the uptimal expansion
     * @param   base the base for the expansion
     * @return  A <code>CanonicalGame</code> matching the specified expansion
     *          to the specified base.
     * @throws  IllegalArgumentException This game is not of the form
     *          <code>{0|H}</code>.
     * @see     #uptimalExpansion() uptimalExpansion
     * @since 0.7.1
     */

    public static CanonicalShortGame uptimal(UptimalExpansion ue, CanonicalShortGame base)
    {
        if (base.getNumLeftOptions() != 1 || base.getLeftOption(0).id != ZERO.id ||
            base.getNumRightOptions() != 1)
        {
            throw new IllegalArgumentException("The specified base is not of the form {0|H}.");
        }
        int id = CanonicalShortGame.construct(ue.getNumberPart(), 0, 0).id;
        if (ue.hasBase())
        {
            id = add(id, base.getRightOption(0).id);
        }
        for (int n = 1; n <= ue.length(); n++)
        {
            int value = ue.getCoefficient(n);
            if (value == 0)
            {
                continue;
            }
            int pow = base.pow(n).id;
            if (value < 0)
            {
                pow = getInverse(pow);
                value = -value;
            }
            for (int i = 0; i < value; i++)
            {
                id = add(id, pow);
            }
        }
        return createFromId(id);
    }

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
    public static CanonicalShortGame superstar(CgsuiteClass type, int ... exponents)
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
        return createFromId(constructFromCanonicalOptions(leftOptions, pack(rightOptions)));
    }

    /**
     * Tests whether this game is an uptimal.
     * <p>
     * @return  <code>true</code> if this game is an uptimal.
     * @see     #uptimalExpansion() uptimalExpansion
     * @since   0.7.1
     */
    public boolean isUptimal()
    {
        return uptimalExpansion(id) != null;
    }

    /**
     * Calculates the uptimal expansion of this game to base {@link #UP}.  The
     * uptimal expansion <code>ue</code> will satisfy
     * <p>
     * <code>CanonicalGame.uptimal(ue).equals(this)</code>
     *
     * @return  the uptimal expansion of this game, or <code>null</code> if
     *          none exists.
     * @see     #uptimal(UptimalExpansion) uptimal
     * @since   0.7.1
     */
    public UptimalExpansion uptimalExpansion()
    {
        UptimalExpansion ue = uptimalExpansion(id);
        if (ue == null)
            throw new InputException("That game is not an uptimal.");
        else
            return ue;
    }

    private static UptimalExpansion uptimalExpansion(int id)
    {
        if (isKnownNonUptimal(id))
        {
            return null;
        }
        
        if (isNumberUpStar(id) && getUpMultiplePart(id) == 0 && getNimberPart(id) <= 1)
        {
            return new UptimalExpansion(getNumberPart(id), getNimberPart(id) == 1);
        }
        
        if (UPTIMAL_MAP.containsKey(id))
        {
            return UPTIMAL_MAP.get(id);
        }
        
        UptimalExpansion ue = uptimalExpansion2(id);
        if (ue == null)
        {
            ue = uptimalExpansion2(getInverse(id));
            if (ue != null)
                ue = ue.getInverse();
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
    
    private static Map<Integer,UptimalExpansion> UPTIMAL_MAP;

    private static UptimalExpansion uptimalExpansion2(int id)
    {
        if (getNumRightOptions(id) > 1 || getNumLeftOptions(id) > 2)
        {
            return null;
        }
        UptimalExpansion rExp = uptimalExpansion(getRightOption(id, 0));
        if (rExp == null)
        {
            return null;
        }
        UptimalExpansion lExp1 = uptimalExpansion(getLeftOption(id, 0));
        if (lExp1 == null || !lExp1.getNumberPart().equals(rExp.getNumberPart()))
        {
            return null;
        }
        UptimalExpansion lExp2 = null;
        if (getNumLeftOptions(id) > 1)
        {
            lExp2 = uptimalExpansion(getLeftOption(id, 1));
            if (lExp2 == null || !lExp2.getNumberPart().equals(rExp.getNumberPart()))
            {
                return null;
            }
        }

        if (lExp1.hasBase() == rExp.hasBase() && lExp2 != null)
        {
            UptimalExpansion swap = lExp2;
            lExp2 = lExp1;
            lExp1 = swap;
        }

        // d_k > 1 case:
        if (rExp.length() > 0 && rExp.getCoefficient(rExp.length()) > 0 &&
            checkUptimalExpansion(rExp.increment(true), rExp, lExp1, lExp2))
        {
            return rExp.increment(true);
        }

        // d_k = 1 case:
        if (checkUptimalExpansion(lExp1.addToCoefficient(Math.max(lExp1.length() + 1, rExp.length() + 1), 1), rExp, lExp1, lExp2))
        {
            return lExp1.addToCoefficient(Math.max(lExp1.length() + 1, rExp.length() + 1), 1);
        }

        return null;
    }

    private static boolean checkUptimalExpansion
        (UptimalExpansion exp, UptimalExpansion rExp, UptimalExpansion lExp1, UptimalExpansion lExp2)
    {
        //System.out.println("Checking: " + exp + ", " + lExp1 + ", " + lExp2 + ", " + rExp);
        assert exp.length() > 0;
        assert exp.getCoefficient(exp.length()) > 0;
        if (!exp.decrement(true).equals(rExp))
        {
            return false;
        }
        int lastNeg = 0, lastZeroOrNeg = 0;
        for (int n = 1; n <= exp.length(); n++)
        {
            if (exp.getCoefficient(n) < 0)
            {
                lastNeg = n;
            }
            if (exp.getCoefficient(n) <= 0)
            {
                lastZeroOrNeg = n;
            }
        }
        if (exp.getCoefficient(exp.length()) == 1)
        {
            lastZeroOrNeg = exp.length() - 1;
        }
        if (exp.isConfused() && lastNeg == 0)
        {
            // Special case.
            return lExp2 != null && lExp2.length() == 0 && !lExp2.hasBase() && exp.truncateTo(lastZeroOrNeg).equals(lExp1);
        }
        if (exp.hasBase() && lastZeroOrNeg == 0 && exp.length() == rExp.length() && !exp.isConfused())
        {
            // Special case.
            return lExp2 == null && lExp1.length() == 0 && !lExp1.hasBase();
        }
        if (lastNeg == 0)
        {
            return lExp2 == null && exp.truncateTo(lastZeroOrNeg).equals(lExp1);
        }
        if (lExp2 == null || lExp1.hasBase() != exp.hasBase() || lExp2.hasBase() == exp.hasBase())
        {
            return false;
        }
        return exp.truncateTo(lastZeroOrNeg).equals(lExp1) &&
               exp.truncateTo(lastNeg).increment(true).equals(lExp2);
    }

    /**
     * Calculates the mean value of this game.
     *
     * @return  The mean value of this game.
     */
    public RationalNumber mean()
    {
        return mean(id);
    }

    private static RationalNumber mean(int id)
    {
        if (isNumberUpStar(id))
        {
            return getNumberPart(id);
        }
        else
        {
            return thermograph(id).getMast();
        }
    }

    /**
     * Calculates the temperature of this game.
     *
     * @return  The temperature of this game.
     */
    public RationalNumber temperature()
    {
        return temperature(id);
    }

    private static RationalNumber temperature(int id)
    {
        if (isNumberUpStar(id))
        {
            if (isNumber(id))
            {
                // It's a number k/2^n, so the temperature is -1/2^n
                return new RationalNumber(
                    BigInteger.ONE.negate(),
                    BigInteger.ONE.shiftLeft(getDenExpPart(id))
                    );
            }
            else
            {
                // It's a number plus a nonzero infinitesimal
                return RationalNumber.ZERO;
            }
        }
        else
        {
            return thermograph(id).getTemperature();
        }
    }

    /**
     * Calculates the thermograph of this game.
     *
     * @return  The thermograph of this game.
     */
    public Thermograph thermograph()
    {
        return thermograph(id);
    }

    private static Thermograph thermograph(int id)
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

        leftScaffold = leftScaffold.tilt(RationalNumber.NEGATIVE_ONE);
        rightScaffold = rightScaffold.tilt(RationalNumber.ONE);

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
            int newOffset = writeNusRecordExceptOptions(new RationalNumber(i, 1), 0, 0, numLO, numRO);
            int[] sector = data[newOffset >> SECTOR_BITS];
            int sectorOffset = (newOffset & SECTOR_MASK);
            sector[sectorOffset + 2] = offsetAt;
            writeToIndex(hashOptions(numLO, sector, sectorOffset + 2, numRO, sector, sectorOffset + 2), newOffset);
            offsetAt = newOffset;
        }

        return offsetAt;
    }

    private static int constructRational(RationalNumber r)
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
            if (r.isSmall())
            {
                return constructInteger(r.intValue());
            }
            else
            {
                throw new IllegalArgumentException
                    ("Integer out of bounds (must be between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE + ")");
            }
        }

        int leftOption = constructRational
                (new RationalNumber(r.getNumerator().subtract(BigInteger.ONE), r.getDenominator())),
            rightOption = constructRational
                (new RationalNumber(r.getNumerator().add(BigInteger.ONE), r.getDenominator()));

        offset = writeNusRecordExceptOptions(r, 0, 0, 1, 1);
        int[] sector = data[offset >> SECTOR_BITS];
        int sectorOffset = (offset & SECTOR_MASK);
        sector[sectorOffset + 2] = leftOption;
        sector[sectorOffset + 3] = rightOption;
        writeToIndex(hashOptions(1, sector, sectorOffset + 2, 1, sector, sectorOffset + 3), offset);

        return offset;
    }

    private static int constructNimber(RationalNumber number, int nimber)
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

    private static int constructNus(RationalNumber r, int upMultiple, int nimber)
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
        (RationalNumber number, int upMultiple, int nimber, int numLO, int numRO)
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

            int numerator = number.getNumerator().intValue();
            int denExp = number.getDenominatorExponent();

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
            byte[] numArray = number.getNumerator().toByteArray();
            int numeratorSlots = (numArray.length + 3) / 4;
            ensureSectorSpace(8 + numeratorSlots + numLO + numRO);

            int[] sector = data[nextOffset >> SECTOR_BITS];
            int sectorOffset = nextOffset & SECTOR_MASK;
            int nusOffset = sectorOffset + 2 + numLO + numRO;

            // For a large NUS, the record appears *after* the options record.
            sector[sectorOffset] = UNUSED_BUCKET;
            sector[sectorOffset + 1] = EXT_OPTIONS_RECORD | IS_NUS_MASK | (numLO << NUM_LO_SHIFT) | numRO;

            sector[nusOffset] = UNUSED_BUCKET;
            sector[nusOffset + 1] = EXT_NUS_RECORD | number.getDenominatorExponent();
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
            return constructNus(new RationalNumber(numerator, 1 << denExp), upMultiple, nimber);
        }
        else
        {
            return offset;
        }
    }

    private static int constructFromOptions
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
                return constructNus(RationalNumber.ZERO, 0, leftMex);
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
        RationalNumber number;
        int upMultiple, nimber;

        if (leftOptionArray.length == 0)
        {
            if (rightOptionArray.length == 0)
            {
                number = RationalNumber.ZERO;
            }
            else
            {
                // We just assume things are properly canonicalized and so right's
                // option list must be of length 1 with the unique element an
                // integer.
                number = getNumberPart(rightOptionArray[0]).subtract(RationalNumber.ONE);
            }
            upMultiple = nimber = 0;
        }
        else if (rightOptionArray.length == 0)
        {
            number = getNumberPart(leftOptionArray[0]).add(RationalNumber.ONE);
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
            throw new InputException("Calculation canceled by user.");
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

    ////////////////////////////////////////////////////////////////////////
    // Static cache management, hash functions, etc.

    private static CanonicalShortGame createFromId(int id)
    {
        CgsuiteClass type;
        if (isNumber(id))
            type = DYADIC_RATIONAL_TYPE;
        else if (isNimber(id))
            type = NIMBER_TYPE;
        else
            type = TYPE;
        return new CanonicalShortGame(type, id);
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

    private static int lookupNusRecord(RationalNumber number, int upMultiple, int nimber)
    {
        if (isSmallNus(number, upMultiple, nimber))
        {
            return lookupSmallNusRecord
                (number.getNumerator().intValue(), number.getDenominatorExponent(), upMultiple, nimber);
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

    private static int lookupLargeNusRecord(RationalNumber number, int upMultiple, int nimber)
    {
        int offsetAt = index[hashNus(number, upMultiple, nimber) & indexMask];
        if (offsetAt == UNUSED_BUCKET)
        {
            return UNUSED_BUCKET;
        }

        int descriptor = EXT_NUS_RECORD | number.getDenominatorExponent();
        byte[] numArray = number.getNumerator().toByteArray();
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

    private static boolean isSmallNus(RationalNumber r, int upMultiple, int nimber)
    {
        return r.isSmall() && nimber < 4096 && upMultiple >= -4096 && upMultiple < 4096;
    }

    private static int getSmallNusDescriptor(int denExp, int upMultiple, int nimber)
    {
        return
            STD_NUS_RECORD |
            (denExp << DENOMINATOR_SHIFT) |
            ((upMultiple << UP_MULTIPLE_RIGHTSHIFT) >>> UP_MULTIPLE_LEFTSHIFT) |
            nimber;
    }

    private static int hashNus(RationalNumber number, int upMultiple, int nimber)
    {
        if (isSmallNus(number, upMultiple, nimber))
        {
            return hashSmallNus(number.getNumerator().intValue(), number.getDenominatorExponent(), upMultiple, nimber);
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
        UPTIMAL_MAP = new HashMap<Integer,UptimalExpansion>();
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

        ZERO.id = 3;
        STAR.id = constructNimber(RationalNumber.ZERO, 1);
        UP.id = constructNus(0, 0, 1, 0);
        UP_STAR.id = constructNus(0, 0, 1, 1);
        ONE.id = constructInteger(1);
        MINUS_ONE.id = constructInteger(-1);
        TWO.id = constructInteger(2);
        MINUS_TWO.id = constructInteger(-2);
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