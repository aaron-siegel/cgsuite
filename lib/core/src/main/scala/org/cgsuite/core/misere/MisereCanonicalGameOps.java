/*
 * MisereCanonicalGameOps.java
 *
 * Created on November 4, 2005, 4:29 PM
 * $Id: MisereCanonicalGameOps.java,v 1.21 2007/04/09 23:51:51 asiegel Exp $
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

package org.cgsuite.core.misere;

import java.util.*;

import org.cgsuite.core.impartial.Traversal;
import org.cgsuite.exception.CalculationCanceledException$;
import org.cgsuite.output.Output;
import org.cgsuite.output.StyledTextOutput;
import scala.None$;
import scala.Option;

/** All data about canonical
 * games can be destroyed by calling {@link #reinit() reinit}, which
 * restores the <code>MisereCanonicalGameOps</code> class to its initial
 * settings.
 * <p>
 * For an even game, <code>mid</code> is even, and the data is stored
 * in a record starting at index <code>mid&amp;IXMASK</code> in sector
 * <code>(mid&gt;&gt;SECTOR_BITS)&amp;SECTOR_MASK</code>.
 * <ul>
 * <li>Word <code>(mid&amp;IXMASK)+0</code> is the hash link.</li>
 * <li>Word <code>(mid&amp;IXMASK)+1</code> has four fields:
 *    <ul>
 *    <li>( 3 bits) spare</li>
 *    <li>( 1 bit ) NONCOMPOSITE, 1 if the game is known to have no
 *                  nonzero proper parts.  If this bit is 0, then
 *                  {@link #properParts()} must be called to see
 *                  whether the game is has proper parts.</li>
 *    <li>(14 bits)  G-, 1 if the game is ZERO, and otherwise the  
 *                 MEX of the G- values of its options.  The G- value
 *                 is 0 exactly when the the game is a misere P
 *                 position.</li> 
 *    <li>(14 bits) Birthday.</li>
 *    </ul></li>
 * <li>Word <code>(mid&amp;IXMASK)+2</code> has two fields:
 *    <ul>
 *    <li>( 4 bits)  PHYLUM, ordinal of the game's phylum</li>
 *    <li>(14 bits)  G+, zero if game is a normal play next-player
 *                   loss.  Otherwise the mex of the G+ values of its
 *                   options. </li> 
 *    <li>(14 bits) nOptions, the number of options of this game</li>
 *    </ul></li>
 * <li>Words <code>(mid&amp;IXMASK)+3</code> through
 *     <code>(mid&amp;IXMASK)+2+nOptions</code> contain the
 *     options of game <code>mid&amp;~1</code>.  These options are
 *     listed in canonical order.  Canonical order is defined by the
 *     following keys:
 *     <ol>
 *     <li><code>g.evenPart().birthday()</code>,</li>
 *     <li><code>g.evenPart().numOptions()</code></li>
 *     <li><code>g.evenPart().gPlusValue()</code></li>
 *     <li><code>g.evenPart().gMinusValue()</code></li>
 *     <li><code>g.evenPart().getOptions()</code> lexicographically</li>
 *     <li><code>g.isEven()</code> (<code>true</code> first)</li>
 *     </ol>
 * </li>
 * <li>If <code>nOptions</code> is even, then word
 *     <code>(mid&amp;IXMASK)+3+nOptions</code> is unused, so that
 *     all game records start on an even index.</li>
 * </ul>
 * For an odd game, <code>mid</code> is odd, and
 * <code>g.plus(ONE).mid == mid - 1)</code>.
 * <p>
 * If the last game record in a sector does not completely fill the
 * sector, then the first free word following the last game record
 * will contain <code>NOT_A_HASH</code>, to allow traversal of the
 * entire list of games (e.g., for rehashing).
 */
public final class MisereCanonicalGameOps
{
    ////////////////////////////////////////////////////////////////
    // Private constants for the game table

    // Number of bits for Birthday, GPlus, and GMinus
    private final static int BIRTHDAY_BITS = 14;
    // Number of bits for number of options
    private final static int N_OPTIONS_BITS = 14;
    private final static int N_PHYLUM_BITS = 4;
    
    private final static int HASHLINK_OFFSET = 0;

    private final static int WINDATA_OFFSET = 1;

    private final static int BIRTHDAY_OFFSET = WINDATA_OFFSET;
    private final static int BIRTHDAY_MASK = (1<<BIRTHDAY_BITS)-1;
    private final static int BIRTHDAY_SHIFT = 0;

    private final static int GMINUS_OFFSET = WINDATA_OFFSET;
    private final static int GMINUS_SHIFT = BIRTHDAY_SHIFT+BIRTHDAY_BITS;
    private final static int GMINUS_BITS = N_OPTIONS_BITS;

    private final static int NONCOMPOSITE_OFFSET = WINDATA_OFFSET;
    private final static int NONCOMPOSITE_SHIFT = GMINUS_SHIFT + GMINUS_BITS;//TAME_SHIFT + 1;
    private final static int NONCOMPOSITE_BIT = 1<<NONCOMPOSITE_SHIFT;

    private final static int WINDATA_0_SPARE_SHIFT = NONCOMPOSITE_SHIFT + 1;
    
    private final static int N_OPTIONS_OFFSET = WINDATA_OFFSET+1;
    private final static int N_OPTIONS_SHIFT = 0;
    private final static int N_OPTIONS_MASK = (1<<N_OPTIONS_BITS)-1;

    private final static int GPLUS_OFFSET = WINDATA_OFFSET+1;
    private final static int GPLUS_SHIFT = N_OPTIONS_SHIFT + N_OPTIONS_BITS;
    private final static int GPLUS_BITS = N_OPTIONS_BITS;

    private final static int PHYLUM_OFFSET = WINDATA_OFFSET+1;
    private final static int PHYLUM_SHIFT = GPLUS_SHIFT+GPLUS_BITS;
    private final static int PHYLUM_MASK = (1<<N_PHYLUM_BITS)-1;

    private final static int WINDATA_1_SPARE_SHIFT = PHYLUM_SHIFT + N_PHYLUM_BITS;

    private final static int OPTIONS_OFFSET = WINDATA_OFFSET+2;

    private final static int SECTOR_BITS = 18;
    private final static int SECTOR_SIZE = 1 << SECTOR_BITS;
    private final static int IXMASK = SECTOR_SIZE - 2;

    static {
        assert WINDATA_0_SPARE_SHIFT <= 32: "Too much stuff in WINDATA";
        assert WINDATA_1_SPARE_SHIFT <= 32: "Too much stuff in WINDATA+1";
        assert OPTIONS_OFFSET + N_OPTIONS_MASK < SECTOR_SIZE:
            "Game with max options might not fit in one sector";
        assert BIRTHDAY_MASK == N_OPTIONS_MASK:
            "Nim heaps of maximum size not supported";
    }

    private final static int INITIAL_SECTORS = 1;
    // Want to be able to subtract mids.
    private final static int MAX_SECTORS = 1 << (31 - SECTOR_BITS);
    private final static int SECTOR_MASK = MAX_SECTORS - 1;

    private final static int UNUSED_BUCKET = -1;
    private final static int NOT_A_HASH = -2;

    private final static int HASHBITS=18;
    private final static int HASHSIZE=1<<HASHBITS;
    private final static int HASHMASK=HASHSIZE-1;
    
    private final static byte OPERATION_ADD = 1;
    private final static byte OPERATION_SUB = 2;
    private final static byte OPERATION_MATE = 3;
    private final static byte OPERATION_DISCRIMINATOR = 4;
    private final static byte OPERATION_LINK = 5;

    ////////////////////////////////////////////////////////////////
    // Game table data

    private static int[][] sectors;// The misere game database
    private static int nextIx;// next available mid index in sector
    private static int nextSectorNum;//Sector number of nextSector
    private static int[] nextSector;//Sector for the next mid entry

    private static int[] hashTable;

    ////////////////////////////////////////////////////////////////
    // Part information
    private static Map<Integer,int[]> properParts;
    private static Map<Integer,int[]> partitionsTable;
    private final static int[] NOPARTS = new int[0];
    private final static Integer[] INTEGER_ARRAY = new Integer[0];
    private static boolean subtractByParts = false;

    ////////////////////////////////////////////////////////////////
    // Genus sequence information
    private static Map<Integer,Genus> genera;
    // private static BitSet genusMexBitSet;
    //    private final static int[] NO_GENUS_EXTENSION = new int[0];
    //    private final static int[] FICKLE_ZERO_EXTENSION = new int[]{2};
    //    private final static int[] FICKLE_ONE_EXTENSION = new int[]{3};
    ////////////////////////////////////////////////////////////////
    // Static members for decreasing memory churn

    private static ResizableIntArray nimberCache;

    private static IntArrayStack tempArrayStack;

    private static Integer[] tempIntegers;

    private static ResizableIntArray birthdayHistogram;
    
    private static OpCache opCache;
    
    ////////////////////////////////////////////////////////////////
    // Performance statistics

    private static int DEBUG = 0;
    private static int KEEPSTATS = 1;
    
    ////////////////////////////////////////////////////////////////
    // Public constants
    /**
     * Maximum number of supported options
     */
    private final static int MAX_OPTIONS = N_OPTIONS_MASK;
    /**
     * Maximum supported birthday
     */
    private final static int MAX_BIRTHDAY = BIRTHDAY_MASK;

    final static int ZERO_ID = 0;
    final static int ONE_ID = 1;
    final static int TWO_ID = 4;
    final static int THREE_ID = 5;
    final static int TWO_SHARP_ID = 10;
    final static int THREE_SHARP_ID = 18;
    final static int TWO_PLUS_TWO_ID = 22;

    ////////////////////////////////////////////////////////////////
    // Initialization

    private MisereCanonicalGameOps() {}

    static
    {
        assert Phylum.values().length <= PHYLUM_MASK+1:
                "Phylum ordinals do not fit in PHYLUM_MASK";
        DEBUG=0;
        init();
    }

    static int constructFromNimber(int nimber)
    {
        if (nimber >= nimberCache.data.length)
        {
            calculateNimber(nimberCache.data.length, nimber);
        }
        return nimberCache.data[nimber];
    }

    private static void calculateNimber(int oldSize, int nimber)
    {
        nimberCache.ensureSize(nimber+1);
        for (int i=oldSize; i<nimberCache.data.length; ++i)
        {
            if((i&1) != 0)
            {
                nimberCache.data[i] = nimberCache.data[i-1] + 1;
            }
            else
            {
                int[] opts = tempArrayStack.getArray(i);
                System.arraycopy(nimberCache.data,0,opts,0,i);
                nimberCache.data[i] = constructFromOptions(opts, i);
                opts = tempArrayStack.putAway(opts);
            }
        }
    }

    static int constructFromOptions(int options[], int numOptions)
    {
        if (Thread.interrupted())
        {
            throw CalculationCanceledException$.MODULE$.apply("Calculation canceled by user.", null, (Option) None$.MODULE$);
        }

        // Box <code>options</code> into <code>Integer</code>s, sort
        // them, and unbox.

        if (tempIntegers.length < options.length)
        {
            tempIntegers = new Integer[options.length];
        }
        for (int i=0; i<numOptions; ++i)
        {
            tempIntegers[i] = options[i];
        }
        Arrays.sort(tempIntegers,0,numOptions,midComparator);

        for (int i=0; i < numOptions; ++i)
        {
            options[i] = tempIntegers[i];
        }

        numOptions = removeDups(options, numOptions);

        // Try hashing the whole set
        int ans = findGame(options,numOptions);
        
        if (ans >= 0) return ans;

        assert numOptions != 0: "Optionless game not found";
        
        // Test reducibility to zero
        if (options[0] != 0) 
        {
            zeroReduction:
            for(int i=0; i < numOptions; ++i)
            {
                if (DEBUG>3) 
                {
                    System.out.println("Test zero-reversibility at " +
                                       options[i]);
                }
                int ix = options[i];
                int[] sector =
                    sectors[(ix >> SECTOR_BITS)&SECTOR_MASK];
                int isOdd = ix&1;
                ix &= IXMASK;
                int optNumOpts = numOptions(sector,ix);

                if (isOdd==0)
                {
                    // Need an option of zero.  Since this option
                    // is even, its even part must have at least one
                    // option.
                    assert optNumOpts !=0: "Zero found in option " + i;
                    
                    if (sector[ix+OPTIONS_OFFSET] != 0)
                        break zeroReduction;
                }
                else
                {
                    // Evenpart of odd options can be 0 => ok
                    // or x+ => fail
                    // or {0,x,...} => fail
                    // or {1,x,...} => okay
                    // or {0,1,x...} => okay
                    if (optNumOpts < 2) 
                    {
                        if(DEBUG>3) 
                        {
                            System.out.println("optNumOpts = " +
                                               optNumOpts);
                        }
                        
                        assert optNumOpts != 1 ||
                            sector[ix+OPTIONS_OFFSET] != 1:
                            "options[" + i + "] = " + options[i]
                            + " is unsimplified 1+";

                        if (optNumOpts > 0) break zeroReduction;
                    }
                    else if (sector[ix+OPTIONS_OFFSET] != 1 &&
                             sector[ix+OPTIONS_OFFSET+1] != 1)
                    {
                        break zeroReduction;
                    }
                }
                
                if (DEBUG>3) 
                {
                    System.out.println("gMinus=" +
                                       gMinusValue(sector,ix) +
                                       " isOdd="+isOdd);
                }
                
                if (gMinusValue(sector,ix) == isOdd)
                {
                    for(int j=i+1; j < numOptions; ++j)
                    {
                        ix = options[j];
                        sector =
                            sectors[(ix >> SECTOR_BITS)&SECTOR_MASK];
                        isOdd = ix&1;
                        ix &= IXMASK;
                        optNumOpts = numOptions(sector,ix);
                        if (optNumOpts < 2) break zeroReduction;
                        if (isOdd==0) 
                        {
                            if (sector[ix+OPTIONS_OFFSET] != 0) 
                            {
                                break zeroReduction;
                            }
                        }
                        else if (sector[ix+OPTIONS_OFFSET] != 1 &&
                                 sector[ix+OPTIONS_OFFSET+1] != 1)
                        {
                            break zeroReduction;
                        }
                    }
                    return 0;
                }
            }
        }

        if(DEBUG>3 && numOptions==2) 
        {
            System.out.println("Trying to reduce ["+options[0]+
                               ","+options[1]+"]");
        }
        // Test for reductions to nonzero games
        int oBDay = birthday(options[0]);
        for(int j=1; j < numOptions; ++j)
        {
            int nBDay = birthday(options[j]);
            // Search for a generation gap
            if (nBDay > oBDay + 1)
            {
                // Test for reducibility to options[0..j-1]
                ans = findGame(options,j);
                 if(DEBUG>3 && numOptions==2)
                {
                    System.out.println("Found "+ans);
                }
                
                if (ans >= 0)
                {
                    for (int i=j; i <= numOptions; ++i)
                    {
                        if(DEBUG>3 && numOptions==2)
                        {
                            System.out.println("Reversing through option "+i);
                        }
                        
                        if (i==numOptions) return ans;
                        if (! hasOption(options[i], ans)) break;
                    }
                }
            }
            oBDay = nBDay;
        }

        return makeGameRecord(options,numOptions);
    }


    /**
     * Make a game record corresponding to a canonical set of
     * options.
     */
    private static int makeGameRecord(int[] options, int numOptions) 
    {
        assert numOptions <= MAX_OPTIONS:
            "Number of options " + numOptions +
            " > max supported " + MAX_OPTIONS;
        if (nextIx + OPTIONS_OFFSET + numOptions >= SECTOR_SIZE)
        {
            nextSector[nextIx] = NOT_A_HASH;
            getNextSector();
        }
        int ans = (nextSectorNum << SECTOR_BITS) + nextIx;
        int hash = calcHash(options,numOptions);
        nextSector[nextIx + HASHLINK_OFFSET] = hashTable[hash];
        hashTable[hash] = ans;
        BitSet gMinus = new BitSet();
        BitSet gPlus = new BitSet();
        int birthday = -1;
        Phylum wildestOptionPhylum = Phylum.NIMHEAP;
        
        for(int i=0; i<numOptions; ++i)
        {
            int ix = options[i];
            int[] sector =
                sectors[(ix >> SECTOR_BITS)&SECTOR_MASK];
            int isOdd = ix&1;
            ix &= IXMASK;
            int winData = sector[ix + BIRTHDAY_OFFSET];
            assert BIRTHDAY_OFFSET==GMINUS_OFFSET;
            birthday = Math.max(((winData>>BIRTHDAY_SHIFT) & BIRTHDAY_MASK) + isOdd,
                                birthday);
            int optGM = (winData>>GMINUS_SHIFT)&N_OPTIONS_MASK;
            winData = sector[ix + N_OPTIONS_OFFSET];
            assert N_OPTIONS_OFFSET==GPLUS_OFFSET && N_OPTIONS_OFFSET==PHYLUM_OFFSET;
            int optGP = (winData>>GPLUS_SHIFT)&N_OPTIONS_MASK;
            
            gMinus.set(optGM^isOdd);
            gPlus.set(optGP^isOdd);

            if (wildestOptionPhylum.isGenerallyTame())
            {
                Phylum optPhylum = getPhylumFromInt(winData);
                if (optPhylum.ordinal() > wildestOptionPhylum.ordinal()) {
                    wildestOptionPhylum = optPhylum;
                }
            }
        }

        if (birthday >= MAX_BIRTHDAY)
        {
            throw new IllegalArgumentException
                ("Birthday "+(birthday+1)+
                 " > MAX_BIRTHDAY " + MAX_BIRTHDAY);
        }
        
        if (numOptions > MAX_OPTIONS)
        {
            throw new IllegalArgumentException
                ("numOptions "+numOptions+
                 " > MAX_OPTIONS " + MAX_OPTIONS);
        }

        int gP = gPlus.nextClearBit(0);
        int gM = gMinus.isEmpty()?1:gMinus.nextClearBit(0);
        
        assert gP <= birthday+1 && gM <= Math.max(1,birthday+1) :
        "How can G+ or G- exceed the birthday, except for G-(0)=1?";

        Phylum myPhylum=(gP == gM || ((gP|gM)&~1) == 0
                         ? Phylum.WILD_OF_TAME_GENUS
                         : (gP < 2
                            ? Phylum.WILD_OF_RESTIVE_GENUS
                            : (gM < 2
                               ? Phylum.WILD_OF_RESTLESS_GENUS
                               : Phylum.WILD)));
        OuterSwitch:
        switch (wildestOptionPhylum)
        {
        case NIMHEAP:
            /*
            System.out.println("myPhylum "+myPhylum+" gP "+gP+" gM "+gM);
            */
            if (myPhylum==Phylum.WILD_OF_TAME_GENUS && (gP>1||gP != gM))
            {
                myPhylum=Phylum.NIMHEAP;
                /*
                System.out.println(""+myPhylum);
                */
                break;
            }
            // Fall through
        case HEREDITARILY_TAME:
            switch (myPhylum)
            {
            case WILD_OF_TAME_GENUS:
                myPhylum = Phylum.HEREDITARILY_TAME;
                break OuterSwitch;
            case WILD_OF_RESTIVE_GENUS:
                myPhylum = Phylum.HEREDITARILY_RESTIVE;
                break OuterSwitch;
            }
            // Fall through
        case REVERSIBLY_TAME:
            switch (myPhylum)
            {
            case WILD_OF_TAME_GENUS:
                myPhylum = Phylum.REVERSIBLY_TAME;
                break OuterSwitch;
            case WILD_OF_RESTIVE_GENUS:
                myPhylum = Phylum.REVERSIBLY_RESTIVE;
                break OuterSwitch;
            case WILD_OF_RESTLESS_GENUS:
                myPhylum = Phylum.RESTLESS;
                break OuterSwitch;
            }
        }
        switch (myPhylum)
        {
        case WILD_OF_TAME_GENUS:
        case WILD_OF_RESTIVE_GENUS:
            gMinus.clear();
            gPlus.clear();
            BitSet tameableGMinus=new BitSet();
            BitSet tameableGPlus=new BitSet();
            int[] secondOptions = null;
            EnumSet<Phylum> possiblePhyla =
                myPhylum==Phylum.WILD_OF_TAME_GENUS
                ? EnumSet.of(Phylum.REVERSIBLY_TAME,Phylum.TAMEABLE)
                : EnumSet.of(Phylum.REVERSIBLY_RESTIVE);
            for(int i=0; i<numOptions; i++)
            {
                int optId = options[i];
                int[] sector =
                    sectors[(optId >> SECTOR_BITS)&SECTOR_MASK];
                int isOdd = optId&1;
                int ix = optId & IXMASK;
                int optGM = (sector[ix + GMINUS_OFFSET]>>GMINUS_SHIFT)&N_OPTIONS_MASK;
                int winData = sector[ix + N_OPTIONS_OFFSET];
                assert GPLUS_OFFSET==PHYLUM_OFFSET && GPLUS_OFFSET==N_OPTIONS_OFFSET;
                int optGP = (winData>>GPLUS_SHIFT)&N_OPTIONS_MASK;
                Phylum optPhylum = getPhylumFromInt(winData);
                if (optPhylum.isGenerallyTame())
                {
                    gPlus.set(optGP^isOdd);
                    gMinus.set(optGM^isOdd);
                    /*
                    System.out.println("Exclude P "+ optGP + " to " + gPlus.nextClearBit(0));
                    System.out.println("Exclude M "+ optGM + " to " + gMinus.nextClearBit(0));
                    */
                }
                else
                {
                    boolean gMTameableFound = false;
                    boolean gPTameableFound = false;

                    if (possiblePhyla.contains(Phylum.TAMEABLE)
                        && optPhylum==Phylum.TAMEABLE)
                    {
                        tameableGMinus.set(optGM);
                        tameableGPlus.set(optGP);
                        gMTameableFound = true;
                        gPTameableFound = true;
                    }
                    int nSecondOptions = ((winData>>N_OPTIONS_SHIFT)&N_OPTIONS_MASK)+isOdd;
                    if (secondOptions==null)
                    {
                        secondOptions=tempArrayStack.getArray(nSecondOptions);
                    }
                    else
                    {
                        secondOptions=tempArrayStack.ensureSize(secondOptions,nSecondOptions);
                    }
                    fillOptions(optId,sector,ix,secondOptions);
                    boolean gMFound=false;
                    boolean gPFound=false;

                    for (int j=0; j<nSecondOptions; j++)
                    {
                        Phylum secondOptionPhylum=getPhylum(secondOptions[j]);
                        if (secondOptionPhylum.isGenerallyTame()
                            || (possiblePhyla.contains(Phylum.REVERSIBLY_RESTIVE)
                                && secondOptionPhylum.isRestiveOrTame())
                            || (possiblePhyla.contains(Phylum.TAMEABLE)
                                && secondOptionPhylum.isTameable()))
                        {
                            int secondGM = gMinusValue(secondOptions[j]);
                            int secondGP = gPlusValue(secondOptions[j]);
                            if (secondOptionPhylum.isGenerallyTame()
                                && myPhylum == Phylum.WILD_OF_TAME_GENUS)
                            {
                                gMFound |= (gM == secondGM);
                                gPFound |= (gP == secondGP);
                            }
                            else if (secondOptionPhylum.isRestiveOrTame()
                                     && myPhylum == Phylum.WILD_OF_RESTIVE_GENUS)
                            {
                                /*
                                System.out.println("gM "+gM+" gP "+gP+
                                                   " secondGM "+secondGM+
                                                   " secondGP "+secondGP);
                                */
                                gMFound |= (gM == secondGM
                                            && ((secondGP&~1)==0
                                                || ((secondGP^gM)&~1)==0));
                                gPFound |= (gP == secondGP
                                            && ((secondGM&~1)==0
                                                || ((secondGM^gM)&~1)==0));
                                /*
                                System.out.println("gMFound "+gMFound+" gPFound "+gPFound);
                                */
                            }
                            else if (Phylum.TAMEABLE == secondOptionPhylum
                                     && possiblePhyla.contains(Phylum.TAMEABLE))
                            {
                                gMTameableFound |= (gM == secondGM);
                                gPTameableFound |= (gP == secondGP);
                            }
                        }
                    }
                    if (!(gMFound && gPFound))
                    {
                        possiblePhyla.remove(Phylum.REVERSIBLY_TAME);
                        possiblePhyla.remove(Phylum.REVERSIBLY_RESTIVE);
                        if (!(gMTameableFound && gPTameableFound)) {
                            possiblePhyla.remove(Phylum.TAMEABLE);
                        }
                        if(possiblePhyla.isEmpty()) break;
                    }
                }
            }
            if (secondOptions != null)
            {
                secondOptions = tempArrayStack.putAway(secondOptions);
            }

            if (! possiblePhyla.isEmpty())
            {
                if (gP != gPlus.nextClearBit(0)
                    || gM != gMinus.nextClearBit(0))
                {
                    possiblePhyla.remove(Phylum.REVERSIBLY_TAME);
                    possiblePhyla.remove(Phylum.REVERSIBLY_RESTIVE);
                }
                if (possiblePhyla.contains(Phylum.TAMEABLE))
                {
                    gPlus.or(tameableGPlus);
                    gMinus.or(tameableGMinus);
                    if (gP != gPlus.nextClearBit(0)
                        || gM != gMinus.nextClearBit(0))
                    {
                        if ((!gPlus.isEmpty()) || gP>0)
                        {
                            possiblePhyla.remove(Phylum.TAMEABLE);
                        }
                    }
                }
                for (Phylum p : possiblePhyla)
                {
                    myPhylum = p; // if REVERSIBLY_TAME and TAMEABLE, use the first.
                    break;
                }
            }
        }
        if (myPhylum.isWild())
        {
            myPhylum = Phylum.WILD;
        }

        assert BIRTHDAY_OFFSET == WINDATA_OFFSET &&
            GMINUS_OFFSET == WINDATA_OFFSET:
        "Setting wrong WINDATA_OFFSET bits";
        
        nextSector[nextIx + WINDATA_OFFSET] =
            ((birthday+1)<<BIRTHDAY_SHIFT) +
            (gM << GMINUS_SHIFT);

        assert N_OPTIONS_OFFSET == WINDATA_OFFSET+1 &&
            GPLUS_OFFSET == WINDATA_OFFSET+1 &&
            PHYLUM_OFFSET == WINDATA_OFFSET+1:
        "Setting wrong WINDATA_OFFSET+1 bits";

        nextSector[nextIx + WINDATA_OFFSET+1] =
            (numOptions << N_OPTIONS_SHIFT) +
            (gP << GPLUS_SHIFT) +
            (myPhylum.ordinal() << PHYLUM_SHIFT);

        nextIx += OPTIONS_OFFSET;
        for(int i=0; i<numOptions;++i)
        {
            nextSector[nextIx++] = options[i];
        }
        nextIx += (nextIx&1);
        if (nextIx == SECTOR_SIZE)
        {
            getNextSector();
        }
        nextSector[nextIx] = NOT_A_HASH;

        if (DEBUG > 0 && birthday < 3)
        {
            System.out.println("hello "+ans);
            //dumpGame(ans&IXMASK, (ans>>SECTOR_BITS)&SECTOR_MASK, -1);
        }

        if(KEEPSTATS > 0)
        {
            birthdayHistogram.ensureSize(birthday+2);
            birthdayHistogram.data[birthday+1]++;
            if(DEBUG>0)
            {
                int bc=birthdayHistogram.data[birthday+1];
                if((bc%1000000)==0) 
                {
                    System.out.println("b["+(birthday+1)+"]="+bc);
                }
            }
        }
        return ans;
    }

    /**
     * Advance to next sector.
     */
    private static void getNextSector()
    {
        if (++nextSectorNum >= sectors.length)
        {
            assert nextSectorNum <= MAX_SECTORS:
                "Ran out of space for MisereCanonicalGameOps storage";
            int[][] newSectors =
                new int[Math.min(MAX_SECTORS,
                                 (sectors.length * 3 + 4)/2)][];
            System.arraycopy(sectors,0,newSectors,0,sectors.length);
            Arrays.fill(newSectors,sectors.length,newSectors.length,null);
            sectors = newSectors;
        }
        if (sectors[nextSectorNum] == null)
        {
            sectors[nextSectorNum] = new int[SECTOR_SIZE];
        }
        nextSector=sectors[nextSectorNum];
        nextIx=0;
    }

    /**
     * Find game with this canonical set of options, if it exists.
     */
    private static int findGame(int options[], int numOptions)
    {
        if (numOptions == 0) return 0;
        int hiOption=options[numOptions-1];
        if ((hiOption&1)==0
            && sameOptions(hiOption+1,options,numOptions))
        {
            return hiOption+1;
        }
        for (int id = hashTable[calcHash(options,numOptions)];
             id != UNUSED_BUCKET;
             id=sectors[(id >> SECTOR_BITS)&SECTOR_MASK][id&IXMASK])
        {
            if (sameOptions(id,options,numOptions)) return id;
        }
        return -1;
    }

    /**
     * True if game <code>id</code> has option <code>opt</code>.
     */
    private static boolean hasOption(int id, int opt)
    {
        int[] sector = sectors[(id >> SECTOR_BITS)&SECTOR_MASK];
        int isOdd = id&1;
        int ix = id & IXMASK;
        if (isOdd != 0 && opt == (id&~1)) return true;
        int topOpt = ix +
            OPTIONS_OFFSET +
            ((sector[ix+N_OPTIONS_OFFSET]>>N_OPTIONS_SHIFT)
             &N_OPTIONS_MASK) -
            1;
        ix += OPTIONS_OFFSET;
        opt ^= isOdd;
        while (ix <= topOpt) {
            int mid = (ix + topOpt)/2;
            int comp = midComparator.compare(opt, sector[mid]);
            if (comp==0) return true;
            if (comp<0)
            {
                topOpt = mid - 1;
            }
            else
            {
                ix = mid + 1;
            }
        }
        return false;
    }

    /**
     * True if the canonical options of game <code>id</code> are
     * <code>options[0..numOptions-1]</code>.
     */
    private static boolean sameOptions(int id, int options[], int numOptions)
    {
        int[] sector = sectors[(id >> SECTOR_BITS)&SECTOR_MASK];
        int isOdd = id&1;
        int ix = id & IXMASK;
        if (isOdd == 0)
        {
            if ((sector[ix+N_OPTIONS_OFFSET] & N_OPTIONS_MASK)
                != numOptions) return false;
            ix += OPTIONS_OFFSET;
            for(int i=0; i<numOptions; ++i)
            {
                if (sector[ix++] != options[i]) return false;
            }
        }
        else
        {
            if(DEBUG>3) 
            {
                System.out.println("Trying to recognize "+id+
                                   " with numOptions "+
                                   numOptions);
                if(numOptions>0) 
                {
                    System.out.println("options[0]="+
                                       options[0]);
                }
            }
            if ((sector[ix+N_OPTIONS_OFFSET] & N_OPTIONS_MASK)
                != (--numOptions)) return false;
            if(DEBUG>3)
            {
                System.out.println("OK numOptions "+numOptions);
            }
            if(options[numOptions] != (id&~1)) return false;
            if(DEBUG>3)
            {
                System.out.println("OK topopt");
            }
            ix += OPTIONS_OFFSET;
            // Compare <code>options[0..numOptions-1]</code>
            // with <code>sector[ix .. ix+numOptions-1]</code>
            // xored with 1.  Because parity is the least significant
            // sort key, the only difference in order is when
            // two adjacent items x,x+1 appear in both lists.
            boolean pairSeen = false;

            for (int i=0; i<numOptions; ++i)
            {
                if (pairSeen) 
                {
                    pairSeen=false;
                }
                else if (options[i] != (sector[ix+i]^1)) 
                {
                    int op=options[i];
                    if ((op&1) != 0 ||
                        op != sector[ix+i] ||
                        i == numOptions-1 ||
                        op+1 != options[i+1] ||
                        op+1 != sector[ix+i+1])
                    {
                        return false;
                    }
                    pairSeen=true;
                }
            }
        }
        return true;
    }

    /**
     * Calculate a hash value for a sorted list of ids.
     */
    private static int calcHash(int[] options, int numOptions)
    {
        if (numOptions==0) return 0;
        int ans=options[0]+1;
        for(int i=1; i<numOptions; ++i) 
        {
            ans=(ans*69 + options[i]) ^ 0x5aa555aa;
        }
        return ((ans ^ 0xa5a5) + (ans >> HASHBITS)) & HASHMASK;
    }

    ////////////////////////////////////////////////////////////////
    // Private subclasses

    private static final class ResizableIntArray
    {
        private final static int DEFAULT_INITIAL_SIZE = 10;
        private int ensures=0, resizes=0;
        public int[] data;
        public ResizableIntArray()
        {
            this.data=new int[DEFAULT_INITIAL_SIZE];
        }
        public ResizableIntArray(int minInitialSize)
        {
            this.data=new int[Math.max(minInitialSize,DEFAULT_INITIAL_SIZE)];
        }
        public void ensureSize(int n)
        {
            ensures+=1;
            if (data.length < n)
            {
                resizes+=1;
                int[] newdata = new int[(3*n+6)/2];
                System.arraycopy(data, 0, newdata, 0, data.length);
                Arrays.fill(newdata, data.length, newdata.length, 0);
                data=newdata;
            }
        }
        public String getStats()
        {
            return "(e"+ensures+",r"+resizes+",s"+data.length+")";
        }
    }

    private static final class IntArrayStack
    {
        private final static int DEFAULT_INITIAL_SIZE = 5;
        private int depth=0;
        private int gets=0;
        private ResizableIntArray[] stack = null;
        
        public IntArrayStack()
        {
            resizeStack(DEFAULT_INITIAL_SIZE);
            depth = 0;
        }

        public int[] getArray(int minSize) {
            gets +=1;

            if (depth >= stack.length) {
                resizeStack((3*stack.length+4)/2);
            }
            if (stack[depth] == null)
            {
                stack[depth] = new ResizableIntArray(minSize);
            }
            else
            {
                stack[depth].ensureSize(minSize);
            }
            return stack[depth++].data;
        }

        public int[] putAway(int[] oldArray) {
            checkStack("putAway", oldArray);
            depth -= 1;
            return null;
        }

        public int[] ensureSize (int[] oldArray, int needSize) {
            checkStack("ensureSize", oldArray);
            stack[depth-1].ensureSize(needSize);
            return stack[depth-1].data;
        }

        private void checkStack(String why, int[] oldArray) {
            if (depth == 0 || stack[depth-1].data != oldArray)
            {
                throw new IllegalArgumentException
                    ("Stack protocol violated by " + why + "at depth " + depth);
            }
        }
        private void resizeStack(int newSize) {
            ResizableIntArray[] newStack = new ResizableIntArray[newSize];
            int oldSize = 0;
            if (stack != null) oldSize = stack.length;

            if (oldSize > 0) System.arraycopy(stack,0,newStack,0,oldSize);
           
            Arrays.fill(newStack, oldSize, newSize, null);
            stack = newStack;
        }

        public String getStats() {
            StringBuilder ans=new StringBuilder();
            if (depth > 0) {
                ans.append("Depth " + depth + "! ");
            }
            ans.append("Gets "+ gets);
            if (stack != null)
            {
                for (ResizableIntArray r : stack)
                {
                    if (r != null) ans.append(" " + r.getStats());
                }
            }
            ans.append(System.getProperty("line.separator","\n"));
            return ans.toString();
        }
    }

    /**
     *  Comparison for sorting <code>Integer(id)</code>s in canonical
     *  order.  Canonical order is defined by the following keys:
     *     <ol>
     *     <li><code>g.evenPart().birthday()</code>,</li>
     *     <li><code>g.evenPart().numOptions()</code></li>
     *     <li><code>g.evenPart().gPlusValue()</code></li>
     *     <li><code>g.evenPart().gMinusValue()</code></li>
     *     <li><code>g.evenPart().getOptions()</code> lexicographically</li>
     *     <li><code>g.isEven()</code> (<code>true</code> first)</li>
     *     </ol>
     */
    static final class MidComparator implements Comparator<Integer>
    {
        public int compare(Integer iMid1, Integer iMid2)
        {
            int mid1=iMid1, mid2=iMid2; // Don't trust them boxes

            // If either is early, or they differ by ONE, comparison is direct
            if((mid1 & ~(IXMASK|1))==0 ||
               (mid2 & ~(IXMASK|1))==0 ||
               ((mid1^mid2)&~1)==0)
            {
                return mid1 - mid2;
            }
            int p1=mid1&1, ix1=mid1&IXMASK,
                p2=mid2&1, ix2=mid2&IXMASK;
            int[]
                s1=sectors[(mid1>>SECTOR_BITS)&SECTOR_MASK],
                s2=sectors[(mid2>>SECTOR_BITS)&SECTOR_MASK];
            // Compare birthdays
            int ans =
                (s1[ix1+BIRTHDAY_OFFSET]&(BIRTHDAY_MASK<<BIRTHDAY_SHIFT)) -
                (s2[ix2+BIRTHDAY_OFFSET]&(BIRTHDAY_MASK<<BIRTHDAY_SHIFT));
            if(ans != 0) return ans;
            // Compare number of options
            ans =
                (s1[ix1+N_OPTIONS_OFFSET]&(N_OPTIONS_MASK<<N_OPTIONS_SHIFT)) -
                (s2[ix2+N_OPTIONS_OFFSET]&(N_OPTIONS_MASK<<N_OPTIONS_SHIFT));
            if(ans != 0) return ans;
            // Compare G+
            ans =
                (s1[ix1+GPLUS_OFFSET]&(BIRTHDAY_MASK<<GPLUS_SHIFT)) -
                (s2[ix2+GPLUS_OFFSET]&(BIRTHDAY_MASK<<GPLUS_SHIFT));
            if(ans != 0) return ans;
            // Compare G-
            ans =
                (s1[ix1+GMINUS_OFFSET]&(BIRTHDAY_MASK<<GMINUS_SHIFT)) -
                (s2[ix2+GMINUS_OFFSET]&(BIRTHDAY_MASK<<GMINUS_SHIFT));
            if(ans != 0) return ans;
            // Compare lex order of options
            int lim = numOptions(s1,ix1) + OPTIONS_OFFSET;
            for (int i=OPTIONS_OFFSET; i < lim; ++i)
            {
                ans = compare(s1[ix1 + i], s2[ix2 + i]);
                if(ans != 0) return ans;
            }
            assert false:
                "Duplicate canonical indices " + mid1 + " and " + mid2;
            return 0;
        }
    }

    final static MidComparator midComparator = new MidComparator();

    /**
     * Append string version with part separation
     */
    static void appendMidToOutput(int id, StyledTextOutput out)
    {
        appendMidToOutput(id, out, true);
    }

    /**
     * Append string version with part separation optional
     */
    private static void appendMidToOutput(int id, StyledTextOutput out, boolean tryParts)
    {
        /*
        if (DEBUG>0) {
            System.out.println  (midToString(id, false));
        }
        */
        rAppendMidToOutput(id, out, tryParts, false, null);
    }

    /**
     * Recursive form, possibly parenthesized.
     * <code>subscripted</code> is
     * <ul><li>[1] on entry if a subscript must be opened</li>
     *     <li>[1] on exit if a subscript was opened</li>
     *     <li>null if a subscript is not required but must be
     *         closed</li></ul>
     */
    private static void rAppendMidToOutput
        (int id, StyledTextOutput out, boolean tryParts, boolean parenthesize, int[] subscripted)
    {
        if (Thread.interrupted())
        {
            throw CalculationCanceledException$.MODULE$.apply("Calculation canceled by user.", null, (Option) None$.MODULE$);
        }

        if (subscripted == null)
        {
            subscripted = new int[] {0};
            rAppendMidToOutput(id, out, tryParts, parenthesize, subscripted);
            if (subscripted[0] > 0) out.appendMath(EnumSet.of(Output.Mode.PLAIN_TEXT), "]");
            return;
        }

        if (nimberCache == null)
        {
            // For printing before nimberCache is ready;
            out.appendText("MisereCanonicalGameOps@");
            out.appendText(String.valueOf(id));
            return;
        }

        int b = birthday(id);

        if (id == constructFromNimber(b))
        {
            out.appendMath(digitToString(b));
            if (subscripted[0] > 0)
            {
                out.appendMath(EnumSet.of(Output.Mode.PLAIN_TEXT), "[");
            }
            return;
        }

        if (tryParts)
        {
            for (int i = (id & 1) + 2; i < b; i += 2)
            {
                int bdiff = subtract(id, constructFromNimber(i));
                assert bdiff != 0 : "" + id + "-" + constructFromNimber(i) + "=" + bdiff;
                if (bdiff != -1 && birthday(bdiff) < birthday(id)) {
                    subscripted[0] = 1;
                    rAppendMidToOutput(bdiff, out, tryParts, true, subscripted);
                    out.appendText(EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT, StyledTextOutput.Style.FACE_MATH), digitToString(i));
                    return;
                }
                int bsum = add(id, constructFromNimber(i));
                assert bsum != 0: id + "+" + constructFromNimber(i) + "=" + bsum;
                if (birthday(bsum) < birthday(id)) {
                    subscripted[0] = 1;
                    rAppendMidToOutput(bsum, out, tryParts, true, subscripted);
                    out.appendText(EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT, StyledTextOutput.Style.FACE_MATH), "-");
                    out.appendText(EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT, StyledTextOutput.Style.FACE_MATH), digitToString(i));
                    return;
                }
            }
            
            if ((id & 1) > 0)
            {
                subscripted[0] = 1;
                rAppendMidToOutput(id & ~1, out, tryParts, true, subscripted);
                out.appendText(EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT, StyledTextOutput.Style.FACE_MATH), "1");
                return;
            }
        }

        int isOdd = id & 1;
        int[] s = sectors[(id >> SECTOR_BITS) & SECTOR_MASK];
        int nOpts = isOdd + numOptions(s, id & IXMASK);
        int[] opts = tempArrayStack.getArray(nOpts);
        fillOptions(id, s, id & IXMASK, opts);

        if (nOpts < 2) 
        {
            assert isOdd == 0 && nOpts > 0 : "Odd singleton > 1 at " + id;

            rAppendMidToOutput(opts[0], out, tryParts, true, subscripted);
            out.appendText(EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT, StyledTextOutput.Style.FACE_MATH), "#");
            tempArrayStack.putAway(opts);
            return;
        }

        int oSub = subscripted[0];
        if (parenthesize) out.appendMath("(");
        for (int i = nOpts - 1; i >= 0; i--)
        {
            subscripted[0] = 0;
            rAppendMidToOutput(opts[i], out, tryParts, true, subscripted);
            if (subscripted[0] > 0) out.appendMath(EnumSet.of(Output.Mode.PLAIN_TEXT), "]");
        }
        tempArrayStack.putAway(opts);
        if (parenthesize) out.appendMath(")");
        subscripted[0] = oSub;
        if (subscripted[0] > 0)
        {
            out.appendMath(EnumSet.of(Output.Mode.PLAIN_TEXT), "[");
        }
    }

    private static String digitToString(int digit)
    {
        if (digit < 10)
            return String.valueOf(digit);
        else
            return "&" + digit + ";";
    }

    ////////////////////////////////////////////////////////////////
    // Game operations

    static int numOptions(int id)
    {
        return (id&1) +
            numOptions(sectors[(id >> SECTOR_BITS)&SECTOR_MASK],
                          id&IXMASK);
    }

    /**
     * Internal numOptions.
     */
    private static int numOptions(int[] sector, int ix) {
        return N_OPTIONS_MASK &
            (sector[ix + N_OPTIONS_OFFSET]>>N_OPTIONS_SHIFT);
    }

    static int[] getOptions(int id)
    {
        int[] opts = new int[numOptions(id)];
        fillOptions(id, opts);
        return opts;
    }

    /**
     * Fill an int[] array with the options of game id
     */
    private static void fillOptions(int id, int[] opts)
    {
        fillOptions(id,
                    sectors[(id >> SECTOR_BITS)&SECTOR_MASK],
                    id&IXMASK,
                    opts);
    }
    /**
     * Store options of game <code>id</code> at
     * <code>sector</code> and <code>ix</code> into
     * <code>opts[0,1,...]</code>.
     * 
     * If <code>id</code> is even, its other bits need not be correct.
     *
     */
    private static void fillOptions(int id, int[]sector, int ix, int[]opts)
    {
        int isOdd = id&1;
        int offset = ix+OPTIONS_OFFSET;
        int intOpts = numOptions(sector,ix);

        if (isOdd > 0) 
        {
            boolean pairSeen = false;
            for (int i=0; i < intOpts; ++i)
            {
                if (pairSeen) 
                {
                    pairSeen = false;
                }
                else 
                {
                    int opt = sector[offset+i];
                    if ((opt&1)==0 &&
                        i < intOpts-1 &&
                        sector[offset+i+1] == opt+1)
                    {
                        opts[i] = opt;
                        opts[i+1] = opt+1;
                        pairSeen = true;
                    }
                    else opts[i] = opt^1;
                }
            }
            opts[intOpts] = id-1;
        }
        else
        {
            for(int i=0; i<intOpts;++i)
            {
                opts[i] = sector[offset+i];
            }
        }
    }

    static int add(int id1, int id2)
    {
        if (Thread.interrupted())
        {
            throw CalculationCanceledException$.MODULE$.apply("Calculation canceled by user.", null, (Option) None$.MODULE$);
        }

        int isOdd = (id1^id2)&1;
        id1 &= ~1;
        id2 &= ~1;
        
        if(id1 == 0 || id2 == 0) return  isOdd+id1+id2;

        int ans = opCache.lookupCommutative(OPERATION_ADD,
                                            id1, id2);

        if (ans != -1) return ans ^ isOdd;

        int s1[] = sectors[(id1>>SECTOR_BITS) & SECTOR_MASK],
            s2[] = sectors[(id2>>SECTOR_BITS) & SECTOR_MASK];

        int ix1 = id1&IXMASK, ix2 = id2&IXMASK;
        
        int nOpts1 = numOptions(s1,ix1),
            nOpts2 = numOptions(s2,ix2);
        
        int[] o1 = tempArrayStack.getArray(nOpts1 + nOpts2);
        int[] o2 = tempArrayStack.getArray(nOpts2);
        
        fillOptions(0,s1,ix1,o1);
        fillOptions(0,s2,ix2,o2);
        
        for(int i=0; i<nOpts1; ++i) 
        {
            o1[i] = add(o1[i],id2);
        }
        
        for(int i=0; i<nOpts2; ++i)
        {
            o1[nOpts1+i] = add(o2[i],id1);
        }

        ans = constructFromOptions(o1,nOpts1+nOpts2);
        
        o2 = tempArrayStack.putAway(o2);
        o1 = tempArrayStack.putAway(o1);

        return isOdd ^
            opCache.storeCommutative(OPERATION_ADD,id1,id2,ans);
    }

    static int subtract(int id1, int id2)
    {
        int[] partitions = get2Partitions(id1);
        int index = indexOf(partitions, id2 & ~1);
        if (index == -1)
        {
            return -1;
        }
        else
        {
            return partitions[index ^ 1] | ((id1 ^ id2) & 1);
        }
    }

    static int mate(int id)
    {
        if (id < 2) return id ^ 1;
        int ans=opCache.lookupUnary(OPERATION_MATE,id);
        if (ans != -1) return ans;
        int nOpts = numOptions(id);
        int opts[] = tempArrayStack.getArray(nOpts);
        fillOptions(id,opts);
        for (int i=0; i<nOpts; ++i) {
            opts[i] = mate(opts[i]);
        }
        ans = constructFromOptions(opts, nOpts);
        opts = tempArrayStack.putAway(opts);
        return opCache.storeUnary(OPERATION_MATE, id, ans);
    }

    static boolean isPPosition(int id)
    {
        return gMinusValue(id) == 0;
    }

    static boolean isNimHeap(int mid)
    {
        return mid == constructFromNimber(birthday(mid));
    }

    static boolean isTame(int id)
    {
        return getPhylum(id).isHereditarilyTame();
    }

    static boolean isGenerallyTame(int id)
    {
        return getPhylum(id).isGenerallyTame();
    }

    private static Phylum getPhylum(int id)
    {
        int[] sector = sectors[(id>>SECTOR_BITS) & SECTOR_MASK];
        int ix = id&IXMASK;
        return getPhylumFromInt(sector[ix+PHYLUM_OFFSET]);
    }

    private static Phylum getPhylumFromInt(int i)
    {
        return Phylum.values()[(i>>PHYLUM_SHIFT)&PHYLUM_MASK];
    }

    static boolean isRestive(int id)
    {
        return getPhylum(id).isHereditarilyRestive();
    }

    static boolean isGenerallyRestive(int id)
    {
        return getPhylum(id).isGenerallyRestive();
    }

    static boolean isRestless(int id)
    {
        return getPhylum(id)==Phylum.RESTLESS;
    }

    static boolean isHalfTame(int id)
    {
        if (isGenerallyTame(id))
            return false;
        int doubleGame = add(id, id);
        return isGenerallyTame(doubleGame) && gPlusValue(doubleGame) == 0 && gMinusValue(doubleGame) == 0;
    }

    static boolean isTameable(int id)
    {
        return getPhylum(id).isTameable();
    }

    static boolean isEven(int id)
    {
        return (id&1)==0;
    }

    static boolean isOdd(int id)
    {
        return (id&1)==1;
    }
    
    /**
     * Returns the even part of this game.  This is equivalent to
     * <p>
     * <code>isEven() ? this : this.add(ONE)</code>
     *
     * @return The even part of this game.
     */
    static int evenPart(int id)
    {
        return (id & 1) == 0 ? id : (id & ~1);
    }

    /**
     * Internal birthday calculation
     */
    static int birthday(int id)
    {
        if (DEBUG>1)
        {
            int sn = ((id>>SECTOR_BITS) & SECTOR_MASK);
            System.out.println("Id " + id + " sector " + sn +
                               " of " + sectors.length);
            System.out.println("Index "+(id&IXMASK)+ " of " +
                               sectors[sn].length);
        }
        return (id&1) +
            birthday(sectors[(id>>SECTOR_BITS) & SECTOR_MASK],
                     id&IXMASK);
    }

    private static int birthday(int[]sector, int ix)
    {
        return BIRTHDAY_MASK &
            (sector[ix+BIRTHDAY_OFFSET] >> BIRTHDAY_SHIFT);
    }

    /**
     * Internal gPlusValue calculation
     */
    static int gPlusValue(int id)
    {
        return (id&1) ^
            gPlusValue(sectors[(id>>SECTOR_BITS) & SECTOR_MASK],
                       id&IXMASK);
    }

    private static int gPlusValue(int[]sector, int ix)
    {
        return BIRTHDAY_MASK &
            (sector[ix+GPLUS_OFFSET] >> GPLUS_SHIFT);
    }

    /**
     * Internal gMinusValue calculation
     */
    static int gMinusValue(int id)
    {
        return (id&1) ^
            gMinusValue(sectors[(id>>SECTOR_BITS) & SECTOR_MASK],
                     id&IXMASK);
    }
    private static int gMinusValue(int[]sector, int ix)
    {
        return BIRTHDAY_MASK &
            (sector[ix+GMINUS_OFFSET] >> GMINUS_SHIFT);
    }

    static Genus genus(int id)
    {
        Phylum p =getPhylum(id);
        if(p.isRestiveOrTame())
        {
            return new Genus(p, gPlusValue(id), gMinusValue(id));
        }
        Genus ans = genera.get(id);
        if (ans != null) return ans;
        
        int nOpts = numOptions(id);
        Genus[] optGenera = new Genus[nOpts];
        
        int[] opts = tempArrayStack.getArray(nOpts);
        fillOptions(id,opts);
        for (int i = 0; i<nOpts; ++i)
        {
            optGenera[i] = genus(opts[i]);
        }
        opts = tempArrayStack.putAway(opts);
        ans = new Genus(Arrays.asList(optGenera));
        genera.put(id,ans);
        return ans;
    }

    /**
     * A P-N discriminator between <code>G</code> and <code>H</code> is a
     * game <code>T</code> such that <code>G+T</code> is a P-position, but
     * <code>H+T</code> is an N-position.
     *
     * @return A P-N discriminator between this game and <code>g</code>.
     * @throws IllegalArgumentException if <code>this.equals(g)</code>.
     */
    static int discriminatorPN(int id1, int id2)
    {
        int ans = discriminator(id1,id2);
        if (ans==-1 || isPPosition(add(ans,id1))) return ans;
        int nOpts=numOptions(id1);
        int[] opts = tempArrayStack.getArray(nOpts+1);
        fillOptions(id1,opts);
        for (int i=0; i<nOpts; ++i)
        {
            opts[i] = mate(opts[i]);
        }
        opts[nOpts]=ans;
        ans=constructFromOptions(opts,nOpts+1);
        opts=tempArrayStack.putAway(opts);
        assert isPPosition(add(id1,ans)) && !isPPosition(add(id2,ans)):
            "Discriminator reversal between "+id1+" and "+id2+" failed Lemma75";
        return ans;
    }
    /**
     * Finds a discriminator between this game and <code>g</code>.
     * A discriminator between <code>G</code> and <code>H</code> is a
     * game <code>T</code> such that <code>G+T</code> and <code>H+T</code>
     * have distinct outcomes.
     * <p>
     * This method will in general be faster than
     * {@link #discriminatorPN(int, int) discriminatorPN}, at the
     * cost of a slightly more vague result.
     *
     * @return A discriminator between this game and <code>g</code>.
     * @throws IllegalArgumentException if <code>this.equals(g)</code>.
     */
    static int discriminator(int id1,int id2)
    {
        if(id1==id2) return -1;
        if(id1==0 && isPPosition(id2)) return 0;
        if(id2==0 && isPPosition(id1)) return 0;
        int ans = opCache.lookupCommutative(OPERATION_DISCRIMINATOR,id1,id2);
        if (ans != -1) return ans;
        int nOpts1 = numOptions(id1);
        int nOpts2 = numOptions(id2);
        int opts[] = tempArrayStack.getArray(Math.max(nOpts1,nOpts2));
        fillOptions(id1,opts);
        for(int i=0;i<nOpts1;++i)
        {
            ans = findLink(id2,opts[i]);
            if (ans != -1)
            {
                opts=tempArrayStack.putAway(opts);
                return opCache.storeCommutative(OPERATION_DISCRIMINATOR,id1,id2,ans);
            }
        }
        fillOptions(id2,opts);
        for(int i=0;i<nOpts2;++i)
        {
            ans = findLink(id1,opts[i]);
            if (ans != -1)
            {
                opts=tempArrayStack.putAway(opts);
                return opCache.storeCommutative(OPERATION_DISCRIMINATOR,id1,id2,ans);
            }
        }
        assert false:
            id1+" and "+id2+" failed Thm 75";
        return 0;
    }


    static boolean isLinked(int id1,int id2)
    {
        if (opCache.lookupCommutative(OPERATION_LINK, id1, id2)
            != -1) {
            return true;
        }
        if (hasOption(id1,id2)) return false;
        if (hasOption(id2,id1)) return false;
        return true;
    }

    static int findLink(int id1,int id2)
    {
        int ans = opCache.lookupCommutative(OPERATION_LINK, id1, id2);
        if (ans != -1) {
            if (ans==-2) return -1;
            return ans;
        }
        if (id1==0 && id2==0) return 1;
        if (hasOption(id1,id2)) return -1;
        if (hasOption(id2,id1)) return -1;
        int nOpts1 = numOptions(id1);
        int nOpts2 = numOptions(id2);
        int[] opts = tempArrayStack.getArray(nOpts1+nOpts2);
        fillOptions(id1, opts);

        for (int i=nOpts1-1,j=nOpts1+nOpts2; i>=0; --i) {
            opts[--j] = discriminatorPN(opts[i],id2);
        }
        fillOptions(id2, opts);
        for (int i=0; i<nOpts2;++i) {
            opts[i] = discriminatorPN(opts[i],id1);
        }
        ans = constructFromOptions(opts,nOpts1+nOpts2);
        assert isPPosition(add(ans,id1)) && isPPosition(add(ans,id2)):
            "Theorem 76 failed for "+id1+" , "+id2;
        opts=tempArrayStack.putAway(opts);
        return ans;
    }

    /**
     * Returns <code>true</code> if this game is extraverted.  <code>G</code> is
     * <em>extraverted</em> if each of its
     * options is a part of <code>G</code>.
     * <p>
     * By the <em>Extraversion-Introversion Theorem</em>, the following
     * are equivalent:
     * <ul><li><code>G</code> is extraverted.</li>
     *     <li><code>G</code> is <em>divine</em>, meaning that if
     *         <code>G</code> is a part of every option of some game
     *         <code>H</code>, then <code>G</code> is a part of
     *         <code>H</code> itself.</li>
     *     <li><code>G</code> is a part of <code>G<sub>/</sub></code>.</li></ul>
     *
     * @return <code>true</code> if this game is extraverted (equivalently, divine).
     */
    static boolean isExtraverted(int id)
    {
        int[] opt = {id};
        int idat = constructFromOptions(opt,1);
        return subtract(idat,id) != -1;
    }

    /**
     * Returns <code>true</code> if this game is introverted.  <code>G</code> is
     * <em>introverted</em> if it is a part of each of its options.
     *
     * By the <em>Extraversion-Introversion Theorem</em>, the only
     * introverted games are 0 and 1.
     *
     * @return <code>true</code> if this game is introverted.
     */
    static boolean isIntroverted(int id)
    {
        return id < 2;
    }

    /**
     * Returns <code>true</code> if this game is prime.
     * <p>
     * A game <code>G</code> is <em>prime</em> if it has exactly two
     * even parts.
     *
     * @return <code>true</code> if <code>G</code> is prime.
     */
    static boolean isPrime(int id)
    {
        return id > 1 && isNoncomposite(id);
    }

    /**
     * Returns <code>true</code> if this game is composite.  A game
     * is composite if it has at least two prime parts.
     *
     * @return <code>true</code> if <code>G</code> is composite.
     */
    static boolean isComposite(int id)
    {
        return !isNoncomposite(id);
    }
    private static boolean isNoncomposite(int id)
    {
        return (sectors[(id>>SECTOR_BITS) & SECTOR_MASK][(id&IXMASK)+NONCOMPOSITE_OFFSET]
                & NONCOMPOSITE_BIT) !=0
            || get2Partitions(id & ~1).length == 2;
    }

    /**
     * Gets the nonzero even proper parts of this game.
     *
     * @return A <code>List</code> of all nonzero even proper parts of this game.
     */
     /*
     * <ul><li>If 0 or 1 is an option of <code>G</code>, then
     *         <code>G</code> is ONE or prime (from ONaG).</li>
     *     <li>If <code>G</code> is not ZERO, ONE, 2/, 3/, or 2+2, and
     *     all options of <code>G</code> are prime, then
     *     <code>G</code> is or prime (from ONaG).</li>
     *     <li>if <code>G</code> has more than two prime options
     *         (counting pairs <code>H, H+1</code> as a single prime),
     *         then <code>G</code> is prime (from Aaron Siegel).
     * </ul>
     *
     * <p>If nonzero <code>G</code> is not found to be prime by these
     * criteria, then the following algorithm, due to Aaron Siegel, is
     * used.  Let <code>U</code> denote the union of all nonzero even
     * parts of options of G, and let <code>N</code> denote the
     * intersection of the nonzero even parts of options of
     * <code>G</code>.
     *
     * <p>For all pairs <code>H, K</code> in <code>U</code>, if
     * <code>H+K=G</code>, then these parts are recorded and removed
     * from further consideration.
     *
     * <p>For all option parts <code>H</code> remaining in
     * <code>N</code> calculate the difference <code>G-H</code>.  If
     * the difference exists, <code>H</code> and <code>G-H</code> are
     * recorded as parts of <code>G</code>.
     *
     * <p><code>G</code> has no other even proper parts.
     *
     */

    static int[] parts(int id)
    {
        return parts(id, false, false);
    }

    static int[] primeParts(int id)
    {
        return parts(id, false, true);
    }

    static int[] properParts(int id)
    {
        return parts(id, true, false);
    }

    static int[] parts(int id, boolean proper, boolean prime)
    {
        List<Integer> parts = new ArrayList<>();
        int[] partitions = get2Partitions(id & ~1);
        for (int i = (proper ? 2 : 0); i < partitions.length; i += 2)
        {
            if (!prime || isPrime(partitions[i]))
            {
                parts.add(partitions[i]);
            }
            if (partitions[i+1] != partitions[i])
            {
                if (!prime || isPrime(partitions[i+1]))
                {
                    parts.add(partitions[i+1]);
                }
            }
        }
        int[] partsArray = new int[parts.size()];
        for (int i = 0; i < partsArray.length; i++)
        {
            partsArray[i] = parts.get(i);
        }
        return partsArray;
    }
    
    private static int[] get2Partitions(int id)
    {
        // TODO This assertion appears to be unnecessary and fires in several cases...
        // I should try to better understand those cases at some point.
        //assert (id & 1) == 0 : "getPartitions called on odd id: " + id;
        id = (id & ~1);     // Just in case
        
        if (partitionsTable.containsKey(id))
        {
            return partitionsTable.get(id);
        }
        
        List<Integer> partitions = new ArrayList<Integer>();
        partitions.add(ZERO_ID);
        partitions.add(id);
        
        int nOpts = numOptions(id);
        int[] opts = tempArrayStack.getArray(nOpts);
        fillOptions(id, opts);
        
        int[][] optPartitions = new int[nOpts][];
        int nPrimeOptions = 0;
        for (int i = 0; i < nOpts; i++)
        {
            if ((opts[i] & ~1) == 0)
            {
                // Unit option.
                nPrimeOptions = Integer.MAX_VALUE;
                break;
            }
            optPartitions[i] = get2Partitions(opts[i] & ~1);
            if (optPartitions[i].length == 2 &&
                ((opts[i] & 1) == 0 || indexOf(opts, nOpts, opts[i] & ~1) == -1))
            {
                nPrimeOptions++;
                if (nPrimeOptions >= 3)
                {
                    break;
                }
            }
        }
        
        SortedSet<Integer> optParts = null;
        List<Integer> type1Options = null, type2Candidates = null;
        boolean changed = false;
        
        if (nPrimeOptions < 3)
        {
            optParts = new TreeSet<Integer>(midComparator);
            for (int i = 0; i < nOpts; i++)
            {
                for (int part : optPartitions[i])
                {
                    optParts.add(part);
                }
            }
            
            type1Options = new ArrayList<Integer>();
            type2Candidates = new ArrayList<Integer>();
            changed = true;
        }
        
        while (changed)
        {
            changed = false;
            for (int x : optParts)
            {
                if (partitions.contains(x))
                {
                    continue;   // Already found this one
                }
                
                int nXOpts = numOptions(x);
                int[] xOpts = tempArrayStack.getArray(nXOpts);
                fillOptions(x, xOpts);
                
                boolean isType1 = true;
                type1Options.clear();
                type2Candidates.clear();
                
                for (int i = 0; i < nXOpts; i++)
                {
                    int index = partitions.indexOf(xOpts[i] & ~1);
                    if (index == -1)
                    {
                        isType1 = false;
                        // Find all G' for which this X' is a part.
                        for (int j = 0; j < nOpts; j++)
                        {
                            int xOptIndex = indexOf(optPartitions[j], xOpts[i] & ~1);
                            if (xOptIndex != -1)
                            {
                                type2Candidates.add(optPartitions[j][xOptIndex ^ 1]);
                            }
                        }
                        break;
                    }
                    else
                    {
                        type1Options.add(partitions.get(index ^ 1) | (xOpts[i] & 1));
                    }
                }
                if (isType1)
                for (int i = 0; i < nOpts; i++)
                {
                    int xIndex = indexOf(optPartitions[i], x);
                    if (xIndex == -1)
                    {
                        isType1 = false;
                        // Find all X' that are parts of this G'.
                        for (int j = 0; j < nXOpts; j++)
                        {
                            int xOptIndex = indexOf(optPartitions[i], xOpts[j] & ~1);
                            if (xOptIndex != -1)
                            {
                                type2Candidates.add(optPartitions[i][xOptIndex ^ 1]);
                            }
                        }
                        break;
                    }
                    else
                    {
                        type1Options.add(optPartitions[i][xIndex ^ 1] | (opts[i] & 1));
                    }
                }
                
                if (isType1)
                {
                    // Put Y = {G'-X,G-X'}
                    int[] cpOptionsArray = tempArrayStack.getArray(type1Options.size());
                    int i = 0;
                    for (int cpOpt : type1Options)
                    {
                        cpOptionsArray[i++] = cpOpt;
                    }
                    int counterpart = constructFromOptions(cpOptionsArray, type1Options.size());
                    tempArrayStack.putAway(cpOptionsArray);
                    partitions.add(x);
                    partitions.add(counterpart);
                    changed = true;
                }
                else for (int y : type2Candidates)
                {
                    int nYOpts = numOptions(y);
                    int[] yOpts = tempArrayStack.getArray(nYOpts);
                    fillOptions(y, yOpts);
                    boolean ok = true;
                    
                    // We might be a type 2 part.
                    for (int i = 0; i < nOpts; i++)
                    {
                        int xIndex = indexOf(optPartitions[i], x);
                        if (xIndex != -1 && indexOf(yOpts, nYOpts, optPartitions[i][xIndex ^ 1] | (opts[i] & 1)) != -1)
                        {
                            continue;
                        }
                        int yIndex = indexOf(optPartitions[i], y);
                        if (yIndex != -1 && indexOf(xOpts, nXOpts, optPartitions[i][yIndex ^ 1] | (opts[i] & 1)) != -1)
                        {
                            continue;
                        }
                        ok = false;
                        break;
                    }
                    if (ok)
                    xLoop: for (int i = 0; i < nXOpts; i++)
                    {
                        int xOptIndex = partitions.indexOf(xOpts[i] & ~1);
                        if (xOptIndex != -1 && indexOf(yOpts, nYOpts, partitions.get(xOptIndex ^ 1) | (xOpts[i] & 1)) != -1)
                        {
                            continue xLoop;
                        }
                        for (int j = 0; j < nOpts; j++)
                        {
                            int yIndex = indexOf(optPartitions[j], y);
                            if (yIndex != -1 && xOpts[i] == (optPartitions[j][yIndex ^ 1] | (opts[j] & 1)))
                            {
                                continue xLoop;
                            }
                        }
                        ok = false;
                        break xLoop;
                    }
                    if (ok)
                    yLoop: for (int i = 0; i < nYOpts; i++)
                    {
                        int yOptIndex = partitions.indexOf(yOpts[i] & ~1);
                        if (yOptIndex != -1 && parityInvariantIndexOf(xOpts, nXOpts, partitions.get(yOptIndex ^ 1) | (yOpts[i] & 1)) != -1)
                        {
                            continue yLoop;
                        }
                        for (int j = 0; j < nOpts; j++)
                        {
                            int xIndex = indexOf(optPartitions[j], x);
                            if (xIndex != -1 && yOpts[i] == (optPartitions[j][xIndex ^ 1] | (opts[j] & 1)))
                            {
                                continue yLoop;
                            }
                        }
                        ok = false;
                        break yLoop;
                    }
                    tempArrayStack.putAway(yOpts);
                    
                    if (ok)
                    {
                        partitions.add(x);
                        partitions.add(y);
                        changed = true;
                        break;
                    }
                }
                
                tempArrayStack.putAway(xOpts);
            }
        }
        
        tempArrayStack.putAway(opts);
        
        int[] partitionsArray = new int[partitions.size()];
        int i = 0;
        for (int part : partitions)
        {
            partitionsArray[i++] = part;
        }
        partitionsTable.put(id, partitionsArray);
        return partitionsArray;
    }
    
    private static int indexOf(int[] array, int value)
    {
        return indexOf(array, array.length, value);
    }
    
    private static int indexOf(int[] array, int length, int value)
    {
        for (int i = 0; i < length; i++)
        {
            if (array[i] == value)
            {
                return i;
            }
        }
        return -1;
    }
    
    private static int parityInvariantIndexOf(int[] array, int length, int value)
    {
        for (int i = 0; i < length; i++)
        {
            if ((array[i] & ~1) == (value & ~1))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets all possible partitions of this game into even primes,
     * optionally including references to {@link #ONE_ID} if this game is odd.
     * <p>
     * If <code>includeUnit</code> is <code>true</code> and this game is
     * odd, then each partition will also include a reference to
     * {@link #ONE_ID}.  This guarantees that each partition actually
     * sums to this game.  If <code>includeUnit</code> is
     * <code>false</code>, then only even primes will be included in the
     * partitions.
     *
     * @param includeUnit <code>true</code> if a reference to
     *        {@link #ONE_ID} should be included in each odd partition.
     *
     * @return All possible partitions of this game into even primes.
     */
    static int[][] partitions(int id, boolean includeUnit)
    {
        List<List<Integer>> partitions = new ArrayList<>();
        buildPartitions(
            id,
            partitions,
            new LinkedList<>(),
            includeUnit
            );
        int[][] partitionsArray = new int[partitions.size()][];
        for (int i = 0; i < partitions.size(); i++)
        {
            partitionsArray[i] = new int[partitions.get(i).size()];
            for (int j = 0; j < partitions.get(i).size(); j++)
            {
                partitionsArray[i][j] = partitions.get(i).get(j);
            }
        }
        return partitionsArray;
    }
    
    private static void buildPartitions(
        int id,
        List<List<Integer>> partitions,
        LinkedList<Integer> partial,
        boolean includeUnit
        )
    {
        if (id <= 1)
        {
            List<Integer> copy = new ArrayList<>();
            copy.addAll(partial);
            if (includeUnit && id == 1)
            {
                copy.add(ONE_ID);
            }
            partitions.add(copy);
        }
        else
        {
            for (int prime : primeParts(id))
            {
                if (partial.isEmpty() || midComparator.compare(partial.getLast(), prime) <= 0)
                {
                    partial.add(prime);
                    buildPartitions(
                        subtract(id, prime),
                        partitions,
                        partial,
                        includeUnit
                    );
                    partial.removeLast();
                }
            }
        }
    }
    
    //////////////////////////////////////////////////////////////////
    // Private utilities for cache management, etc.
    
    /**
     * Remove duplicate ints from data[0..oldNo-1].
     * Return number of remaining ints
     */
    private static int removeDups(int[] data, int oldNo)
    {
        for (int i=1; i<oldNo; ++i)
        {
            if (data[i] == data[i-1])
            {
                int newNo=i-1;
                for (int j = i+1; j < oldNo; ++j)
                {
                    if (data[newNo] != data[j])
                    {
                        data[++newNo] = data[j];
                    }
                }
                return newNo+1;
            }
        }
        return oldNo;
    }

    /**
     * Reinitializes the <code>MisereCanonicalGameOps</code> class.  This
     * will destroy all existing information about canonical misere
     * games.
     * <p>
     * <b>Warning</b>: Calling this method will invalidate any existing
     * <code>MisereCanonicalGameOps</code> objects.  Attempting to use any
     * invalidated objects might cause unpredictable behavior.
     */
    public static void reinit()
    {
        sectors = null;
        nextSector = null;
        hashTable = null;
        genera = null;
        //        genusMexBitSet = null;
        nimberCache = null;
        tempArrayStack = null;
        tempIntegers = null;
        birthdayHistogram = null;
        opCache = null;
        
        System.gc();
        init();
    }

    private static void init()
    {
        sectors = new int[INITIAL_SECTORS][];
        hashTable = new int[HASHSIZE];
        genera = new HashMap<Integer,Genus>();
        //        genusMexBitSet = new BitSet();
        nimberCache = null; // Until valid database is ready.
        tempArrayStack = new IntArrayStack();
        tempIntegers = new Integer[10];
        birthdayHistogram = new ResizableIntArray();
        opCache = new OpCache(1<<20);
        properParts = new HashMap<Integer,int[]>();
        partitionsTable = new HashMap<Integer,int[]>();

        sectors[0]=new int[185]; // The early games sector is short
        nextIx=0;
        nextSectorNum=0;
        nextSector=sectors[nextSectorNum];
        Arrays.fill(hashTable,0,HASHSIZE,UNUSED_BUCKET);

        DEBUG=0;
        
        int[][] earlyData =        // ix name    bd no g+ g- off
            { { },                 //  0:0        0  0  0  1 (4)
              { 0, 1 },            //  2:2        2  2  2  2 (10)
              { 2 },               //  4:2[/]     3  1  0  0 (14)
              { 3 },               //  6:3[/]     4  1  0  0 (18)
              { 4 },               //  8:2[//]    4  1  1  1 (22)
              { 2, 3 },            // 10:2[2]     4  2  0  0 (28)
              { 2, 4 },            // 12:2[/]2    4  2  1  1 (34)
              { 0, 4 },            // 14:2[/]0    4  2  1  2 (40)
              { 1, 4 },            // 16:2[/]1    4  2  2  1 (46)
              { 2, 3, 4 },         // 18:2[/]32   4  3  1  1 (52)
              { 0, 3, 4 },         // 20:2[/]30   4  3  1  2 (58)
              { 0, 2, 4 },         // 22:2[/]20   4  3  1  3 (64)
              { 1, 3, 4 },         // 24:2[/]31   4  3  2  1 (70)
              { 1, 2, 4 },         // 26:2[/]21   4  3  3  1 (76)
              { 0, 2, 3, 4 },      // 28:2[/]320  4  4  1  4 (84)
              { 0, 1, 2, 4 },      // 30:2[/]210  4  4  3  3 (92)
              { 1, 2, 3, 4 },      // 32:2[/]321  4  4  4  1 (100)
              { 0, 1, 2, 3 },      // 34:4        4  4  4  4 (108)
              { 0, 1, 2, 3, 4 },   // 36:2[/]3210 4  5  4  4 (116)
              { 5 },          // 38:2[/1/]        5  1  0  0 (120)
              { 8 },          // 40:2[///]        5  1  0  0 (124)
              {12 },          // 42:(2[/]2)[/]    5  1  0  0 (128)
              {14 },          // 44:(2[/]0)[/]    5  1  0  0 (132)
              {16 },          // 46:(2[/]1)[/]    5  1  0  0 (136)
              {18 },          // 48:(2[/]32)[/]   5  1  0  0 (140)
              {20 },          // 50:(2[/]30)[/]   5  1  0  0 (144)
              {22 },          // 52:(2[/]20)[/]   5  1  0  0 (148)
              {24 },          // 54:(2[/]31)[/]   5  1  0  0 (152)
              {26 },          // 56:(2[/]21)[/]   5  1  0  0 (156)
              {28 },          // 58:(2[/]320)[/]  5  1  0  0 (160)
              {30 },          // 60:(2[/]210)[/]  5  1  0  0 (164)
              {32 },          // 62:(2[/]321)[/]  5  1  0  0 (168)
              {34 },          // 64:4[/]          5  1  0  0 (172)
              {36 },          // 66:(2[/]3210)[/] 5  1  0  0 (176)
              { 6 },          // 68:3[//]         5  1  1  1 (180)
              {10 } };        // 70:2[2/]         5  1  1  1 (184)

        int earlyGames[] = new int[36];
        int earlyOpts[] = new int[5];

        // earlyGames[0]=makeGameRecord(earlyOpts,0);
        // nextSector[0+GMINUS_OFFSET] += (1<<GMINUS_SHIFT);
        for (int i=0; i < earlyData.length; ++i)
        {
            for (int j=0; j < earlyData[i].length; ++j) 
            {
                earlyOpts[j] = earlyGames[earlyData[i][j]>>1]
                    + (earlyData[i][j]&1);
            }
            earlyGames[i] =
                makeGameRecord(earlyOpts, earlyData[i].length);
        }
        
        getNextSector();
        nextSector[0] = NOT_A_HASH;

        nimberCache = new ResizableIntArray();
        calculateNimber(0,nimberCache.data.length-1);

        earlyOpts[0]=nimberCache.data[2];
        earlyOpts[0]=nimberCache.data[3];
    }

    /**
     * Reports on statistics of the MisereCanonicalGameOps system.
     *
     * @return A string of results.
     */
    public static String getStats() 
    {
        if (KEEPSTATS==0) 
        {
            return "No statistics recorded";
        }
        else
        {
            StringBuilder ans=new StringBuilder("Total games stored: ");
            String newLine = System.getProperty("line.separator","\n");
            
            int total=0;
            for(int i=0; i<birthdayHistogram.data.length; ++i) 
            {
                total += birthdayHistogram.data[i];
            }
            ans.append(total);
            
            String sep = " (";
            int oo=0;
            for(int i=0; i<birthdayHistogram.data.length; ++i)
            {
                if (birthdayHistogram.data[i]>0 || oo>0)
                {
                    ans.append(sep + "b" + i + ":");
                    if (i > 0) oo = birthdayHistogram.data[i-1];
                    int ee =  birthdayHistogram.data[i];
                    if (oo>0) ans.append(oo + "o");
                    if (oo>0 && ee>0) ans.append("+");
                    if (ee>0) ans.append(ee + "e");
                    if (oo>0 && ee>0) ans.append("=" + (oo+ee));
                    sep=",";
                    oo=ee;
                }
            }
            ans.append(")"+newLine);
            ans.append((nextSectorNum + 1) + " sectors in use, ");
            float h2l = 0;
            
            for (int i = 0; i < HASHSIZE; ++i)
            {
                int hl=0;
                for (int j = hashTable[i];
                     j != UNUSED_BUCKET;
                     j=sectors
                         [(j >> SECTOR_BITS)&SECTOR_MASK]
                         [j&IXMASK])
                {
                    hl += 1;
                }
                h2l += hl * hl;
            }
            ans.append(Math.sqrt(h2l/total) + " rms hash length");
            ans.append(newLine);

            ans.append("tempArrayStack " + tempArrayStack.getStats());

            ans.append("opCache " + opCache.getStats());

            return ans.toString();
        }
    }

    /**
     * Enum describing features of the games to be returned from
     * EarlyGamesTraversal.  These features are supplied as a set,
     * all of which must all be true for a game to be returned in the
     * traversal
     */
    private static enum GameFilter
    {
        /**
         * True if the game is in canonical form, with reducible
         * options removed.
         */
        CANONICAL,
            /**
             * True if the game is _not_ in canonical form.
             */
        REDUCIBLE,
            /**
             * True if the game is even.
             */
        EVEN,
            /**
             * True if the game is odd.
             */
        ODD,
            /**
             * True if the game's birthday is the maximum returned by
             * this traversal
             */
        MAXBIRTHDAY,
            /**
             * True if the game has no parts other than 0, 1, G, and
             * G+1
             */
        NONCOMPOSITE,
            /*
             * True if the game is not 0, 1, or PRIME 
             */
        COMPOSITE,
            /*
             * True if the game has more than two prime parts.
             */
        MULTIFACTORS,
            /*
             * Never true -- used for skipping
             */
        NEVER
    };


    private static enum State
    {
        NEEDNEXT, // after setupNext or advance is called 
        GOTNEXT,  // after hasNext is called
        NONEXT    // after all results are in
    }
    
    /**
     * A low-overhead iteration superclass.  When performance is an issue,
     * a subclass of <code>ArrayTraversal</code> can be used to iterate
     * over a set of <code>int[]</code>s to minimize the amount of memory
     * allocation during the iteration.  <p><code>ArrayTraversal</code>
     * implements parts of the <code>Traversal</code> interface.
     *
     * <p>The {@link #advance() advance} method moves to the next array in
     * the collection.  The {@link #currentLength() currentLength} method
     * returns the length of the current array.  The
     * {@link #currentPart(int) currentPart(i)} method returns the
     * <code>i</code><sup>th</sup> component of the current element.
     *
     * <p>In order to make a usable <code>ArrayTraversal</code> subclass,
     * the user must write a constructor method and override the
     * <code>setupNext()</code> method.  These methods work by initializing the
     * <code>current</code>  and <code>currentLength</code> variables and
     * by setting the <code>currentState</code> variable to indicate
     * the state of the iteration.
     *
     * <p>The constructor method sets up any required user instance
     * variables to control the traversal.  The constructor may either
     * <ul><li>construct the first element of the set and set
     *         <code>currentState</code> to
     *         <code>State.GOTNEXT</code>,</li>
     *     <li>set <code>currentState</code> to
     *         <code>State.NEEDNEXT</code> to allow the
     *         <code>setupNext()</code> method to construct the first element
     *         of the set, or</li>
     *     <li>leave <code>currentState</code> with the default value of
     *         <code>State.NONEXT</code> to indicate that
     *         the set is empty.</li></ul>
     *
     * <p>The <code>setupNext()</code> method either advances to the next
     * element of the set and returns <code>true</code>, or returns
     * <code>false</code> to indicate that there are no further elements
     * in the set.
     *
     * The following is an example of how an <code>ArrayTraversal</code>
     * subclass might implement traversal over the power set of a set of
     * integers.
     *
     * <pre>public class PowerSet extends ArrayTraversal
     * {
     *     private long currentIndex;
     *     private int[] baseSet;
     *   
     *     public PowerSet(int[] baseSet)
     *     {
     *         this.baseSet = baseSet.clone();  // protect against modification
     *         current = new int[baseSet.length];
     *         currentIndex = 0;
     *         currentState = State.NEEDNEXT;
     *     }
     *   
     *     protected boolean setupNext()
     *     {
     *         if (currentIndex >= (1L << baseSet.length)) return false;
     *         currentLength=0;
     *         for (int i=0; i<baseSet.length; ++i)
     *         {
     *             if ((currentIndex & (1L << i)) != 0)
     *             {
     *                 current[currentLength++] = baseSet[i];
     *             }
     *         }
     *         currentIndex++;
     *         return true;
     *     }
     * }</pre>
     *
     * @author  Aaron Siegel
     * @author  Dan Hoey
     * @version $Revision: 1.21 $ $Date: 2007/04/09 23:51:51 $
     * @since   1.0
     */
    private static class ArrayTraversal implements Traversal, java.lang.Iterable<int[]>
    {
        protected int[] current;
        protected int currentLength;
        protected State currentState = State.NONEXT;
        
    
        /* Uninstantiable superclass.  Users should make their own
         * constructors that set (at least) currentState;
         */
        protected ArrayTraversal()
        {
        }
        
        /**
         * Advances to the next int[] element.
         *
         * @return  <code>true</code> if this <code>Traversal</code> now points
         *          to an option; <code>false</code> if the traversal is
         *          complete.
         */
        public boolean advance()
        {
            if (currentState == State.NEEDNEXT)
            {
                if (! setupNext()) currentState = State.NONEXT;
            }
            else if (currentState == State.GOTNEXT)
            {
                currentState = State.NEEDNEXT;
            }
            return currentState != State.NONEXT;
        }
    
        /**
         * Allows use of an <code>OptionIterator</code> as the target
         * of the "foreach" statement
         *
         * @return <code>this</code> as an <code>Iterator</code>.
         */
        public java.util.Iterator<int[]> iterator()
        {
            return new java.util.Iterator<int[]>()
            {
                public boolean hasNext()
                {
                    if (currentState == State.NEEDNEXT)
                    {
                        currentState =
                            advance()
                            ? State.GOTNEXT
                            : State.NONEXT;
                    }
                    return currentState == State.GOTNEXT;
                }
    
                public int[] next()
                {
                    if (hasNext())
                    {
                        int[] ans = new int[currentLength];
                        System.arraycopy(current,0,ans,0,currentLength);
                        currentState = State.NEEDNEXT;
                        return ans;
                    }
                    throw new java.util.NoSuchElementException
                        ("next() called when !hasNext()");
                }
    
                /**
                 * Throws <code>UnsupportedOperationException</code>.  It is
                 * not possible to remove a result from a
                 * <code>TBCode.OptionIterator</code>.
                 *
                 * @throws UnsupportedOperationException always.
                 */
                public void remove()
                {
                    throw new UnsupportedOperationException
                        ("No removing results from a OptionIterator");
                }
            };
        }
        
        /*
         * This should be overridden by an object that sets the next value
         * into the current and currentLength arrays and returns true, or
         * returns false if no next value is available.
         */
        protected boolean setupNext()
        {
            return false;
        }
    
        /**
         * Gets the length of the current option
         *
         * @return  The length of the current option.
         * @throws  NoSuchElementException if {@link #advance() advance} has
         *          not yet been called, or if the last call to
         *          <code>advance</code> returned <code>false</code>.
         */
        public int currentLength()
        {
            if (currentState != State.NEEDNEXT)
            {
                throw new NoSuchElementException
                    ("Calling currentLength without advance");
            }
            return currentLength;
        }
            
        /**
         * Gets the value of the <code>i</code><sup>th</sup> component of
         * the current int[] element
         *
         * @param   ind index into the current option.
         * @return  The value of the <code>i</code><sup>th</sup> part of the
         *          current option.
         * @throws  NoSuchElementException if {@link #advance() advance} has
         *          not yet been called, or if the last call to
         *          <code>advance</code> returned <code>false</code>.
         * @throws  IndexOutOfBoundsException if <code>i</code>
         *          is out of bounds.
         */
        public int currentPart(int ind)
        {
            if (currentState != State.NEEDNEXT)
            {
                throw new UnsupportedOperationException
                    ("Calling currentPart without advance");
            }
            if (ind < 0 || ind >= currentLength)
            {
                throw new IndexOutOfBoundsException
                    ("Index "+ind+"not between 0 and "+(currentLength - 1));
            }
            return current[ind];
        }
    }
    /**
     * Traversal class for games.  The traversal will return games
     * ordered by birthday, and within birthday ordered by the number
     * of options.
     */
    private final static class EarlyGamesTraversal extends ArrayTraversal
    {
        private int maxGames,maxSubsets,maxBirthday;
        private int subsetsFound=0,gamesFound=0;
        private Integer[] candidateOptions;
        private int[] currentIndices;
        private int currentId;
        private int maxCanonizations=-1;
        private int thisBirthdayStart=0;
        private int nextBirthdayStart=1;
        private Set<GameFilter> filters
            = EnumSet.of(GameFilter.EVEN, GameFilter.CANONICAL);
        private boolean reallyDone= false;

        /**
         * Traverse games returning at most <code>maxGames</code>
         * games, of birthday at most <code>maxBirthday</code>.
         * The number of games returned will have at most
         * <code>lastMaxOptions</code>, and its proper positions will
         * have at most <code>hereditaryMaxOptions</code> positions.
         *
         * <p><code>maxGames, lastMaxOptions,</code> and
         * <code>hereditaryMaxOptions</code> may be given as -1 to
         * avoid limiting these features.
         *
         * @param maxGames maximum number of games to return in the
         *                 traversal, or -1 for no limit.
         * @param maxBirthday maximum birthday of games to return in
         *                 the traversal.  This parameter is required.
         * @param lastMaxOptions maximum number of options of games
         *                 returned in the traversal, or -1 if the
         *                 number of options is unlimited.
         * @param hereditaryMaxOptions maximum number of options of
         *                 positions of games returned in the
         *                 traversal, or -1 if the number of options
         *                 of positions is unlimited.
         */
        public EarlyGamesTraversal(int maxGames,
                                   int maxBirthday,
                                   int lastMaxOptions,
                                   int hereditaryMaxOptions)
        {
            this(maxGames,maxBirthday,lastMaxOptions,
                 hereditaryMaxOptions,-1,null);
        }

        /**
         * Traverse games returning at most <code>maxGames</code>
         * games, of birthday at most <code>maxBirthday</code>.
         * The number of games returned will have at most
         * <code>lastMaxOptions</code>, and its proper positions will
         * have at most <code>hereditaryMaxOptions</code> positions.
         * At most <code>maxSubsets</code> subsets of games with
         * birthday less than <code>maxBirthday</code> will be
         * considered.  Games will be returned from the traversal only
         * if they satsfy the set of <code>filters</code>.
         *
         * <code>maxGames, lastMaxOptions, hereditaryMaxOptions</code>
         * and <code>maxSubsets</code> may be given as -1 to avoid
         * limiting these features.  The <code>filters</code> paameter
         * may be <code>null</code>, defaulting to returning even
         * canonical games.
         *
         * @param maxGames maximum number of games to return in the
         *                 traversal, or -1 for no limit.
         * @param maxBirthday maximum birthday of games to return in
         *                 the traversal.  This parameter is required.
         * @param lastMaxOptions maximum number of options of games
         *                 returned in the traversal, or -1 if the
         *                 number of options is unlimited.
         * @param hereditaryMaxOptions maximum number of options of
         *                 positions of games returned in the
         *                 traversal, or -1 if the number of options
         *                 of positions is unlimited.
         * @param maxSubsets maximum number of subsets of games to be
         *                 considered as options, or -1 for no limit.
         * @param filters set of
         * {@link org.cgsuite.core.misere.MisereCanonicalGameOps.GameFilter} 
         *                 conditions that must be satisfied in order
         *                 to return games from this traversal, or
         *                 <code>null<code> to return even canonical
         *                 games.
         */
        public EarlyGamesTraversal(int maxGames,
                                   int maxBirthday,
                                   int lastMaxOptions,
                                   int hereditaryMaxOptions,
                                   int maxSubsets,
                                   Set<GameFilter> filters) 
        {
            if (maxGames==-1) maxGames = Integer.MAX_VALUE;
            if (lastMaxOptions==-1) lastMaxOptions = Integer.MAX_VALUE;
            if (hereditaryMaxOptions==-1)
                hereditaryMaxOptions = Integer.MAX_VALUE;
            if (maxSubsets==-1) maxSubsets = Integer.MAX_VALUE;

            this.maxGames = maxGames;
            this.maxBirthday = maxBirthday;
            if (filters != null) this.filters = filters;
            this.maxSubsets = maxSubsets;

            List<Integer> candidateList = new ArrayList<Integer>();
            
            if (maxBirthday > 0)
            {
                if (maxBirthday > 1)
                {
                    EarlyGamesTraversal oTrav =
                        new EarlyGamesTraversal
                        (-1,
                         maxBirthday-2,
                         hereditaryMaxOptions,
                         hereditaryMaxOptions,
                         -1,
                         EnumSet.of(GameFilter.CANONICAL));
                    while (oTrav.advance())
                    {
                        candidateList.add(oTrav.currentId());
                    }
                }
                EarlyGamesTraversal oTrav =
                    new EarlyGamesTraversal
                    (maxGames,
                     maxBirthday-1,
                     hereditaryMaxOptions,
                     hereditaryMaxOptions,
                     -1,
                     EnumSet.of(GameFilter.CANONICAL,
                                GameFilter.MAXBIRTHDAY));
                while (oTrav.advance())
                {
                    candidateList.add(oTrav.currentId());
                }
                candidateOptions =
                    candidateList.toArray
                    (new Integer[candidateList.size()]);
                Arrays.sort(candidateOptions,midComparator);
            }
            else if (maxBirthday == 0)
            {
                candidateOptions = new Integer[0];
            }
            else
            {
                currentState = State.NONEXT;
                reallyDone=true;
                return;
            }
            int maxOptions = Math.min(candidateOptions.length,
                                      lastMaxOptions);
            current = new int[maxOptions];
            currentIndices = new int[maxOptions];
            currentLength = -1;
            currentState = State.NEEDNEXT;
        }

        /**
         * Change some restrictions on the games returned by
         * this traversal.  This may be called during a traversal, and
         * even after the {@link #advance()} method has return false,
         * to permit continuation of a traversal with different
         * restrictions.
         *
         * @param maxGames maximum number of (including games already
         *                 returned) to return in the traversal, or -1
         *                 for no limit.
         * @param maxSubsets maximum number of subsets of games to be
         *                 considered as options, or -1 for no limit.
         * @param filters set of
         * {@link org.cgsuite.core.misere.MisereCanonicalGameOps.GameFilter}
         *                 conditions that must be satisfied in order
         *                 to return games from this traversal, or
         *                 null to leave the filter conditions
         *                 unchanged.
         */
        public void changeParams(int maxGames, int maxSubsets,
                                 Set<GameFilter> filters)
        {
            if (maxGames==-1) maxGames=Integer.MAX_VALUE;
            this.maxGames = maxGames;
            if (maxSubsets==-1) maxSubsets=Integer.MAX_VALUE;
            this.maxSubsets = maxSubsets;
            if (filters != null) this.filters = filters;
            if (currentState == State.NONEXT &&
                ! reallyDone)
            {
                currentState = State.NEEDNEXT;
            }
        }

        protected boolean setupNext()
        {
            if (maxGames <= gamesFound) return false;

            nextSubset:
            while (maxSubsets > subsetsFound && nextSubset())
            {
                subsetsFound++;

                if (filters.contains(GameFilter.NEVER)) continue;

                if (filters.isEmpty())
                {
                    currentId = -1;
                }
                else
                {
                    currentId = constructFromOptions(current,currentLength);
                    // restore current in case constructFromOptions
                    // modified it.
                    for(int i=0; i<currentLength; ++i)
                    {
                        current[i] = candidateOptions[currentIndices[i]];
                    }
                }
                
                for (GameFilter f : filters)
                {
                    switch(f)
                    {
                    case CANONICAL:
                        if(numOptions(currentId) != currentLength)
                        {
                            continue nextSubset;
                        }
                        break;
                    case REDUCIBLE:
                        if(numOptions(currentId) == currentLength)
                        {
                            continue nextSubset;
                        }
                        break;
                    case EVEN:
                        if((currentId & 1) != 0) continue nextSubset;
                        break;
                    case ODD:
                        if((currentId & 1) != 1) continue nextSubset;
                        break;
                    case MAXBIRTHDAY:
                        if(birthday(currentId) != maxBirthday)
                        {
                            continue nextSubset;
                        }
                        break;
                    case NONCOMPOSITE:
                        if(!isNoncomposite(currentId)) continue nextSubset;
                        break;
                    case COMPOSITE:
                        if(isNoncomposite(currentId)) continue nextSubset;
                        break;
                    case MULTIFACTORS:
                        if(get2Partitions(currentId).length < 5)
                        {
                            continue nextSubset;
                        }
                    }
                }
                gamesFound++;
                return true;
            }
            if (maxSubsets > subsetsFound) reallyDone = true;
            return false;
        }

        private boolean nextSubset()
        {
            // advance first advanceable element, moving others to start
            for (int i=0; i<currentLength; i++) 
            {
                if (currentIndices[i]+1 <
                    (i == currentLength-1
                     ? nextBirthdayStart
                     : currentIndices[i+1]))
                {
                    currentIndices[i]++;
                    current[i] = candidateOptions[currentIndices[i]];
                    return true;
                }
                else if (currentIndices[i] != i)
                {
                    currentIndices[i]=i;
                    current[i] = candidateOptions[i];
                }
            }

            if (currentLength == -1) {
                currentLength=0;
                return true;
            }

            // increase length by one
            if (currentLength < current.length &&
                (currentLength == 0 ||
                 currentIndices[currentLength-1] < nextBirthdayStart-1))
            {
                if (currentLength > thisBirthdayStart) thisBirthdayStart++;
                currentIndices[currentLength] = thisBirthdayStart;
                current[currentLength] = candidateOptions[thisBirthdayStart];
                currentLength++;
                return true;
            }

            // advance to next birthday
            if (nextBirthdayStart < candidateOptions.length)
            {
                thisBirthdayStart = nextBirthdayStart;
                int thisBirthday = birthday(candidateOptions[thisBirthdayStart]);
                while (++nextBirthdayStart < candidateOptions.length)
                {
                    if (birthday(candidateOptions[nextBirthdayStart])
                        !=thisBirthday) break;
                }
                currentLength = 1;
                currentIndices[0] = thisBirthdayStart;
                current[0] = candidateOptions[thisBirthdayStart];
                return true;
            }
            return false;
        }

        public int currentId()
        {
            currentLength(); // ensure current is valid.
            if (currentId == -1)
            {
                currentId = constructFromOptions(current, currentLength);
                // restore current in case constructFromOptions
                // modified it.
                for(int i=0; i<currentLength; ++i)
                {
                    current[i] = candidateOptions[currentIndices[i]];
                }
            }
            return currentId;
        }

        /**
         * Return statistics about the traversal
         *
         * @return string describing the results of the traversal.
         */
        public String stats()
        {
            return "For maxBD " + maxBirthday+
                " Found "+gamesFound+" games from "
                +subsetsFound+" subsets";
        }
    }

    /**
     * Creates an array of even games with low birthday.  This method allows control of the
     * maximum number of games, the maximum birthday, the maximum
     * number of options for games with the maximum birthday, and the
     * maximum number of options of positions of the games.  The
     * results may optionally be sorted in canonical order.
     *
     * @param maxGames     The maximum number of games to be returned.
     * @param maxBirthday  The maximum birthday of a game returned.
     * @param lastMaxOpts   For games of birthday
     *         <code>maxBirthday</code>, the maximum number of options
     *         to of the games returned.
     * @param hereditaryMaxOptions    The maximum number of options of
     *        the positions of the games returned.
     * @param sorted       Returns the games in sorted order.
     * @return Largest array of CanonicalMisereGames consistent with
     *         the paramaters.
     */

    static Integer[] getEarlyVals (int maxGames,
                                           int maxBirthday,
                                           int lastMaxOpts,
                                           int hereditaryMaxOptions,
                                           boolean sorted)
    {
        ArrayList<Integer> earlyVals = new ArrayList<Integer>();
        EarlyGamesTraversal eT =
            new EarlyGamesTraversal(maxGames,
                                    maxBirthday,
                                    lastMaxOpts,
                                    hereditaryMaxOptions,
                                    -1,
                                    null);
        while (eT.advance()) earlyVals.add(eT.currentId);
        Integer[] earlyArr = earlyVals.toArray(new Integer[earlyVals.size()]);
        if (sorted)
        {
            Arrays.sort(earlyArr,midComparator);
        }
        return earlyArr;
    }

    /*
     * Testing routine
     * @param args Vector of string args supplied by user.
     *
    public static void main(String[] args)
    {
        DEBUG=0;
        
        MisereCanonicalGameOps pot =
            new MisereCanonicalGameOps(ONE);
        
        if (! ZERO.equals(pot))
        {
            System.out.println("pot didn't reverse");
            dumpGame(pot, ZERO.mid);
        }

        pot = new MisereCanonicalGameOps(ZERO);
        if (! ONE.equals(pot))
        {
            System.out.println("pot didn't recognize");
            dumpGame(pot, ZERO.mid);
        }

        pot = new MisereCanonicalGameOps(ZERO,TWO);
        if(! ONE.equals(pot)) 
        {
            System.out.println("[0,2] didn't reverse");
        }
        
        else if (args.length == 1 && args[0].equals("DUMP"))
        {
            System.out.println("Nimbers " +
                               Arrays.toString(nimberCache.data));
            int oldId = -1;
            int[] sector = sectors[0];
            int sectn = 0;
            int ix = 0;
            while (sector != null) 
            {
                if (ix >= SECTOR_SIZE || sector[ix]==NOT_A_HASH) 
                {
                    sector = sectors[++sectn];
                    ix = 0;
                }
                else
                {
                    oldId = dumpGame(ix, sectn, oldId);
                    oldId = dumpGame(ix+1, sectn, oldId);
                    ix += numOptions(sector,ix) + OPTIONS_OFFSET;
                    ix += ix&1;
                }
            }
        }
        else if (args.length == 1 && args[0].equals("B5")) 
        {
            MisereCanonicalGameOps b4[] = new MisereCanonicalGameOps[22];
            int i = 0;
            for (int ix=0 ;
                 birthday(ix) < 5;
                 ix += 2*((numOptions(ix)+OPTIONS_OFFSET+1)/2))
            {
                b4[i++] = createFromId(ix);
                if (birthday(ix) < 4) b4[i++] = createFromId(ix+1);
            }
            assert i==22:
            "Found "+i+" games by day 4, not 22";
            java.util.ArrayList<MisereCanonicalGameOps> opts =
                new java.util.ArrayList<MisereCanonicalGameOps>();
            for(int gi=0; gi<(1<<22); ++gi) 
            {
                if((gi & (gi-1))==0) 
                {
                    System.out.println("gi = "+gi);
                }
                opts.clear();
                for(int j=21; j>=0; --j)
                {
                    if((gi&(1<<j))!=0) opts.add(b4[j]);
                }
                MisereCanonicalGameOps ign =
                    new MisereCanonicalGameOps(opts);
            }
        }
        else if (args.length == 1 && args[0].equals("B5a"))
        {
            Integer[] B5 = getEarlyVals(8000000, 5, 22, 22, false);
            System.out.println("B5 games "+B5.length);
        }
        else if (args.length == 3 && args[0].equals("TESTSTRINGS"))
        {
            int nEarlyGames = Integer.valueOf(args[1]);
            int nNimHeaps = Integer.valueOf(args[2]);
            int [] testData = makeTestData(nEarlyGames, nNimHeaps);
            int nDone=0;
            
            for (int i : testData)
            {
                int newId = (new MisereCanonicalGameOps
                             (midToString(i))
                             ).mid;
                if (i != newId)
                {
                    System.out.println("id "+i+" string "+
                                       midToString(i)+
                                       " comes back as "+
                                       newId);
                }
                if ((nDone++)%100000==0)System.out.print(".");
            }
        }
        else if (args.length == 3 && args[0].equals("SUMS"))
        {
            int nEarlyGames = Integer.valueOf(args[1]);
            int nNimHeaps = Integer.valueOf(args[2]);
            int [] testData = makeTestData(nEarlyGames, nNimHeaps);
            for(int i=0; i<testData.length; ++i)
            {
                for(int j=0; j < testData.length; ++j)
                {
                    System.out.println(midToString(testData[i]) +
                                       "    +    " +
                                       midToString(testData[j]) +
                                       "    =    " +
                                       midToString(add(testData[i],
                                                       testData[j])));
                }
            }
        }
        else if (args.length == 5 && args[0].equals("FINDEARLY"))
        {
            int maxGames = Integer.valueOf(args[1]);
            int maxBd = Integer.valueOf(args[2]);
            int lastMaxOpts =  Integer.valueOf(args[3]);
            int heredMaxOpts = Integer.valueOf(args[4]);
            MisereCanonicalGameOps[] earlyGames
                = getEarlyGames(maxGames, maxBd, lastMaxOpts, heredMaxOpts,
                                true);

            int off=0;
            for (int i = 0; i < earlyGames.length; ++i)
            {
                int id = earlyGames[i].mid;
                String line="              {";
                String sep="";
                int nOpts = earlyGames[i].getNumOptions();
                int [] opts = tempArrayStack.getArray(nOpts);

                fillOptions(id, opts);

                for (int oi = 0; oi < nOpts; ++oi)
                {
                    int argn
                        = earlyIndex(earlyGames,createFromId(opts[oi]));
                    line += sep + String.format("%1$2d",argn);
                    sep=",";
                }

                opts=tempArrayStack.putAway(opts);

                int nameReserve=6;
                if (birthday(id)==5)
                {
                    nameReserve=9;
                    if (nOpts > 1) nameReserve=17;
                }

                off += 2*((nOpts + 4)/2);

                String fmt=" },%" + Math.max(1,
                                             23 +
                                             Math.min(id,1)
                                             - nameReserve
                                             - 3*nOpts
                                             ) +
                    "s// %2d:%-" +
                    nameReserve +
                    "s %s%3s%3s%3s (%d)";

                line += String.format(fmt,
                                      "",
                                      2*i,
                                      midToString(id),
                                      birthday(id),
                                      nOpts,
                                      gPlusValue(id),
                                      gMinusValue(id),
                                      off);
                System.out.println(line);
            }
        }
        else if (args.length == 2 && args[0].equals("G"))
        {
            MisereCanonicalGameOps g = new MisereCanonicalGameOps(args[1]);
            System.out.print(g.toString() + " = [");
            String pre="";
            for (MisereCanonicalGameOps gi : g.getOptions()) {
                System.out.print(pre+gi.toString());
                pre=",";
            }
            System.out.println("]");
            dumpGame(g,-1);
        }
        else if (args.length == 3 && args[0].equals("LM"))
        {
            MisereCanonicalGameOps
                g1 = new MisereCanonicalGameOps(args[1]),
                g2 = new MisereCanonicalGameOps(args[2]);
            if (g1.equals(g2))
            {
                System.out.println("Both "+args[1]+" and "+args[2]+" are "+
                                   g1.toString());
                dumpGame(g1,-1);
            }
            dumpGame(g1,-1);
            dumpGame(g2,-1);
            testLink(findLink(g1.mid,g2.mid),g1.mid,g2.mid);
            if(g1.isLinked(g2)) {
                MisereCanonicalGameOps link=g1.findLink(g2);
                System.out.println("linked by "+link.toString());
                dumpGame(link,-1);
            } else {
                System.out.println("not linked");
            }
            testDiscriminator(discriminatorPN(g1.mid,g2.mid),g1.mid,g2.mid);
            testDiscriminator(discriminatorPN(g2.mid,g1.mid),g2.mid,g1.mid);
            System.out.println("discriminators:");
            dumpGame(g1.discriminatorPN(g2),-1);
            dumpGame(g2.discriminatorPN(g1),-1);
        }
        else if (args.length == 3 && args[0].equals("T77")) {
            int nEarlyGames = Integer.valueOf(args[1]);
            int nNimHeaps = Integer.valueOf(args[2]);
            int [] testData = makeTestData(nEarlyGames, nNimHeaps);
            for(int i : testData)
            {
                for (int j : testData)
                {
                    testDiscriminator(discriminatorPN(i,j),i,j);
                    testLink(findLink(i,j),i,j);
                }
            }
        }
        else if (args.length == 7 && 
                 (args[0].equals("PARTS")
                  || args[0].equals("FACTORIZATIONS")))
            
        {
            EnumSet<GameFilter> filters = EnumSet.of(GameFilter.CANONICAL,GameFilter.EVEN);
            filters.add(args[0].equals("PARTS")
                        ? GameFilter.COMPOSITE
                        : GameFilter.MULTIFACTORS);
            int maxGames = Integer.valueOf(args[1]);
            int maxBd = Integer.valueOf(args[2]);
            int lastMaxOpts =  Integer.valueOf(args[3]);
            int heredMaxOpts = Integer.valueOf(args[4]);
            int skip = Integer.valueOf(args[5]);
            int maxSubsets = Integer.valueOf(args[6]);
            int found=0;
            
            EarlyGamesTraversal partsTraversal =
                new EarlyGamesTraversal (-1, maxBd,
                                         lastMaxOpts, heredMaxOpts, skip,
                                         EnumSet.of(GameFilter.NEVER));
            partsTraversal.advance();
            partsTraversal.changeParams(maxGames, skip+maxSubsets, filters);

            while (partsTraversal.advance())
            {
                int id = partsTraversal.currentId();
                int[] idProperParts = getProperPartsOld(id);
                StringBuilder s = new StringBuilder("Game ");
                appendMidToStringBuilder(id,s);
                if (filters.contains(GameFilter.COMPOSITE))
                {
                    s.append(" Proper Parts ");
                    appendGameListToStringBuilder(idProperParts,-1,s,false);
                }
                else
                {
                    List<List<MisereCanonicalGameOps>> fs = createFromId(id).partitions(false);
                    s.append(" Factorization");
                    s.append(fs.size()>1 ? "s" : "");
                    s.append(": ");
                    for (List<MisereCanonicalGameOps> f : fs)
                    {
                        appendFactorizationToStringBuilder(f,s);
                    }
                }
                System.out.println(s);
            }
        }
        else if (args.length == 6 && args[0].equals("FACTORSEARCH"))
        {
            int maxBd = Integer.valueOf(args[1]);
            int lastMaxOpts = Integer.valueOf(args[2]);
            if (lastMaxOpts==-1) lastMaxOpts=Integer.MAX_VALUE;
            int heredMaxOpts =  Integer.valueOf(args[3]);
            if (heredMaxOpts==-1) heredMaxOpts=Integer.MAX_VALUE;
            int skip = Integer.valueOf(args[4]);
            int chunkSize = Integer.valueOf(args[5]);

            skipLoop: for (int sk = skip; true; sk += chunkSize)
            {
                System.out.println("[skipping "+sk+"]");
                List<Integer> nonPrimeIds = new ArrayList<Integer>();
                EarlyGamesTraversal tr =
                    new EarlyGamesTraversal (-1, maxBd-1,
                                             heredMaxOpts, heredMaxOpts, -1,
                                             EnumSet.of(GameFilter.CANONICAL,
                                                        GameFilter.COMPOSITE));
                while (tr.advance())
                {
                    nonPrimeIds.add(tr.currentId());
                }

                lastMaxOpts = Math.min(lastMaxOpts, nonPrimeIds.size());

                int[] opts = new int[lastMaxOpts];
                int[] optInds = new int[lastMaxOpts];
                int toSkip = sk;
                int nPossThisNOpts = 1;
                for (int nOpts=1; nOpts <=lastMaxOpts; ++nOpts)
                {
                    nPossThisNOpts *= nonPrimeIds.size()+1-nOpts;
                    nPossThisNOpts /= nOpts;
                    
                    if (toSkip >= nPossThisNOpts) 
                    {
                        toSkip -= nPossThisNOpts;
                        continue;
                    }
                    optInds[0]=-1;
                    fixedNOpts: while (true)
                    {
                        if (optInds[0] == -1)
                        {
                            for (int i=0; i<nOpts; ++i) 
                            {
                                optInds[i] = i;
                            }
                        }
                        else
                        {
                            for (int i=0; i<nOpts; ++i)
                            {
                                if (optInds[i] + 1 <
                                    (i < nOpts-1
                                     ? optInds[i+1]
                                     : nonPrimeIds.size()))
                                {
                                    optInds[i]++;
                                    break;
                                }
                                if (i == nOpts-1) break fixedNOpts;
                                optInds[i]=i;
                            }
                        }

                        if (--toSkip < 0)
                        {
                            if (toSkip + chunkSize < 0) 
                            {
                                reinit();
                                continue skipLoop;
                            }
                            
                            for (int i=0; i<nOpts; ++i)
                            {
                                opts[i] = nonPrimeIds.get(optInds[i]);
                            }
                            int id = constructFromOptions(opts,nOpts);
                            
                            if (numOptions(id) == nOpts &&
                                getProperPartsOld(id).length > 3)
                            {
                                dumpGame(id,-1);
                            }
                        }
                    }
                }
                break;
            }
        }
        else
        {
            System.out.println("Usage: DUMP (prints basic game table)");
            System.out.println("       B5   (generates Birthday 5 games)");
            System.out.println("       B5a  (generates Sorted list of birthday 5 games)");
            System.out.println("       TESTSTRINGS #early #heaps (tests string conversion)");
            System.out.println("       SUMS #early #heaps (prints sums of test data)");
            System.out.println("       FINDEARLY maxgames maxbday maxopts heredmaxopts" +
                               " (prints table of early games)");
            System.out.println("       G game (prints info about a game)");
            System.out.println("       LM g1 g2 (prints info about links and " +
                               "discriminators between two games)");
            System.out.println("       T77 #early #heaps (tests theorem 77 on test data)");
            System.out.println("       (PARTS|FACTORIZATIONS) maxgames maxbday maxopts heredmaxopts skip maxsubsets (finds composite games or factorizations)");
            System.out.println("       FACTORSEARCH maxbday maxopts heredmaxopts skip chunksize (searches for multiple factorizations)");
        }
        System.out.println(getStats());
    }

    private static void testLink(int link, int i, int j)
    {
        if (link != -1 && (!isPPosition(add(link,i)) || !isPPosition(add(link,j))))
        {
            System.out.println("Link failure");
            System.out.print("i ");
            dumpGame(i,-1);
            System.out.print("j ");
            dumpGame(j,-1);
            System.out.print("link ");
            dumpGame(link,-1);
            System.out.print("i+link ");
            dumpGame(add(i,link),-1);
            System.out.print("j+link ");
            dumpGame(add(j,link),-1);
        }
    }
    private static void testDiscriminator(int d, int i, int j)
    {
        if (i == j)
        {
            if (d != -1)
            {
                System.out.println("self-discriminator "+
                                   midToString(d)+
                                   " for "+
                                   midToString(i));
                dumpGame(i,-1);
                dumpGame(d,-1);
                dumpGame(add(i,d),-1);
            }
        }
        else if (d == -1)
        {
            System.out.println(midToString(i)+
                               ","+midToString(j)+
                               " not discriminated.");
        }
        else if (!isPPosition(add(d,i)) || isPPosition(add(d,j)))
        {
            System.out.println("Discriminator failure");
            System.out.print("i ");
            dumpGame(i,-1);
            System.out.print("j ");
            dumpGame(j,-1);
            System.out.print("d ");
            dumpGame(d,-1);
            System.out.print("i+d ");
            dumpGame(add(i,d),-1);
            System.out.print("j+d ");
            dumpGame(add(j,d),-1);
        }
    }

    private static int dumpGame(MisereCanonicalGameOps g, int oldId)
    {
        if (g==null)
        {
            System.out.println("g is null");
            return -1;
        }
        return dumpGame(g.mid, oldId);
    }
    private static int dumpGame(int id, int oldId)
    {
        return dumpGame(id & (IXMASK|1),
                        (id >> SECTOR_BITS)&SECTOR_MASK,
                        oldId);
    }

    private static int dumpGame(int ixp, int sectn, int oldId)
    {
        int id = ixp + (sectn << SECTOR_BITS);
        String comparison = new String();
        if (oldId >= 0)
        {
            if (0 <=
                Math.min(1,Math.max(-1,midComparator.compare(id, oldId))) *
                Math.min(1,Math.max(-1,midComparator.compare(oldId, id))))
            {
                comparison += " ??(" +
                    midComparator.compare(id, oldId) + ":" +
                    midComparator.compare(oldId, id) + ")";
            }
            else if (midComparator.compare(id, oldId) < 0 ) {
                comparison += " <";
            }
            else
            {
                assert midComparator.compare(id, oldId) > 0:
                    "midComparator("+id+","+oldId+") acting up";
                comparison += " >";
            }
            comparison += " " + midToString(oldId);
        }

        int nOpts=numOptions(id);

        System.out.println("Game " + midToString(id) +
                           "@"+ sectn + "." + ixp +
                           " Bday " + birthday(id) +
                           " Nopts " + nOpts);
        System.out.print("     Genus "+genus(id).toString());
        System.out.println(" PHYLUM " + getPhylum(id) +
                           (isHalfTame(id)?" halftame":"") +
                           " MATE " + midToString(mate(id)) +
                           comparison );

        int[] opts = tempArrayStack.getArray(nOpts);
        fillOptions(id, opts);
        StringBuilder optsStr = new StringBuilder("Opts ");

        appendGameListToStringBuilder(opts, nOpts, optsStr, false);

        opts=tempArrayStack.putAway(opts);

        if (isNoncomposite(id))
        {
            optsStr.append(" Prime");
            int[] pp = getProperPartsOld(id);
            if (pp!=NOPARTS) {
                optsStr.append("? but proper parts ");
                appendGameListToStringBuilder(getProperPartsOld(id),-1,optsStr,true);
            }
        }
        else
        {
            optsStr.append(" Proper Parts ");
            appendGameListToStringBuilder(getProperPartsOld(id),-1,optsStr,true);
        }
        System.out.println(optsStr);

        optsStr.setLength(0);
        optsStr.append("Factorization");
        List<List<MisereCanonicalGameOps>> fs = createFromId(id).partitions(false);
        optsStr.append(fs.size() < 2 ? ":" : "s:");
        for (List<MisereCanonicalGameOps> f : fs)
        {
            optsStr.append(" ");
            appendFactorizationToStringBuilder(f, optsStr);
        }
        System.out.println(optsStr);
        System.out.println("");
        return id;
    }

    private static StringBuilder appendFactorizationToStringBuilder
        (List<MisereCanonicalGameOps> f, StringBuilder s)
    {
        int[] opts = tempArrayStack.getArray(f.size());
        for(int i=0;i<f.size();i++)
        {
            opts[i]=f.get(i).mid;
        }
        appendGameListToStringBuilder(opts,f.size(),s,false);
        opts = tempArrayStack.putAway(opts);
        return s;
    }

    private static StringBuilder appendGameListToStringBuilder
        (int[] games, int len, StringBuilder s, boolean flagPrimes)
    {
        s.append("[");
        String sep="";
        for (int i=0; i<(len==-1?games.length:len); ++i)
        {
            s.append(sep);
            if (flagPrimes && isNoncomposite(games[i])) s.append("p");
            appendMidToStringBuilder(games[i], s);
            sep=",";
        }
        s.append("]");
        return s;
    }
    */
    private static int earlyIndex(Integer[] earlyArr, int id) 
    {
        int ans = Arrays.binarySearch(earlyArr,
                                      id,
                                      midComparator);
        if (ans >= 0) return ans * 2;
        return -3 - ans * 2;
    }

    private static int[] makeTestData(int nEarlyGames, int nNimHeaps) 
    {
        Integer[] earlyArr = getEarlyVals((nEarlyGames+1)/2,
                                          nEarlyGames > 4171780 ? 6 : 5,
                                          22, 22, true);

        constructFromNimber(nNimHeaps);

        int[] ans = new int[nEarlyGames + nNimHeaps];

        for (int i=0; i < nEarlyGames; ++i) 
        {
            ans[i] = earlyArr[i >> 1] + (i&1);
        }

        for (int i=0; i<nNimHeaps; ++i)
        {
            ans[i+nEarlyGames] = constructFromNimber(i);
        }
        
        return ans;
    }
}
