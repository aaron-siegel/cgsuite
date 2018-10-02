/*
 * Genus.java
 *
 * Created on December 17, 2005, 7:54 PM
 * $Id: Genus.java,v 1.3 2007/02/16 00:43:27 asiegel Exp $
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

import java.util.EnumSet;
import java.util.List;
import java.util.BitSet;

import org.cgsuite.output.Output;
import org.cgsuite.output.OutputTarget;
import org.cgsuite.output.StyledTextOutput;

/**
 * A sequence of nonnegative integers of eventual period 2.
 *
 * @author Dan Hoey
 * @version $Revision: 1.3 $ $Date: 2007/02/16 00:43:27 $
 *
 */
public final class Genus implements OutputTarget
{
    private Phylum phylum;  // Nim-heaps, Tame and Restive phyla are treated
                            // specially.
    private int gBase; // G+(G)
    private int[] gSup; // gamma0, gamma1, ...

    private static BitSet mexBitSet;
    private static int[]  gBuilder;
    private static int[][]  singletons;

    private final static int[] NO_SUP = new int[0];

    static { init(); }

    /*
     * Create a genus for tame or restive games
     */
    public Genus(Phylum p, int gPlus, int gMinus)
    {
        gBase=gPlus;
        phylum=p;
        switch(p)
        {
        case NIMHEAP:
            gSup=NO_SUP;
            break;
        case HEREDITARILY_TAME:
        case REVERSIBLY_TAME:
        case HEREDITARILY_RESTIVE:
        case REVERSIBLY_RESTIVE:
            gSup=getSingleton(gMinus);
            break;
        default: throw new IllegalArgumentException
                     ("Phylum "+p+" requires fancy genus constructor");
        }
    }

    public Genus(List<Genus> gL)
    {
        phylum = Phylum.WILD;
        int maxDiffer = 1;
        mexBitSet.clear();
        for (Genus g : gL)
        {
            mexBitSet.set(g.gBase());
            maxDiffer = Math.max(maxDiffer, g.maxDiffer());
        }
        gBase = mexBitSet.nextClearBit(0);

        maxDiffer += 2;
        if (gBuilder.length < maxDiffer)
        {
            gBuilder = new int[Math.max(maxDiffer+1,((gBuilder.length+5)*3)/2)];
        }
        if (gL.isEmpty())
        {
            throw new IllegalArgumentException
                ("Zero game requires NIMHEAP genus constructor");
        }
        mexBitSet.clear();
        for (Genus g : gL)
        {
            mexBitSet.set(g.supDigit(0));
        }
        gBuilder[0] = mexBitSet.nextClearBit(0);

        for (int i=1; i<=maxDiffer; ++i)
        {
            mexBitSet.clear();
            mexBitSet.set(gBuilder[i-1]);
            mexBitSet.set(gBuilder[i-1]^1);
            for (Genus g : gL)
            {
                mexBitSet.set(g.supDigit(i));
            }
            gBuilder[i] = mexBitSet.nextClearBit(0);
        }
        while (maxDiffer > 1 &&
               gBuilder[maxDiffer] == gBuilder[maxDiffer-2])
        {
            maxDiffer--;
        }
        gSup = new int[maxDiffer+1];
        System.arraycopy(gBuilder,0,gSup,0,maxDiffer+1);
    }

    private int[] getSingleton(int i)
    {
        if(singletons.length <= i) {
            int[][] newSingletons = new int[Math.max(i+1,((singletons.length+5)*3)/2)][];
            System.arraycopy(singletons,0,newSingletons,0,singletons.length);
            for(int ind = singletons.length; ind<newSingletons.length; ++ind)
            {
                newSingletons[ind] = new int[]{ind};
            }
            singletons=newSingletons;
        }
        return singletons[i];
    }

    /**
     * Output
     */
    public Output toOutput()
    {
        StyledTextOutput sto = new StyledTextOutput();
        sto.appendMath(String.valueOf(gBase));
        if (gSup.length > 0)
            sto.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH), EnumSet.of(Output.Mode.PLAIN_TEXT), "^");
        boolean hasBigSup = false;
        for (int i = 0; i < gSup.length; i++)
            if (gSup[i] >= 10)
                hasBigSup = true;
        if (hasBigSup)
            sto.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_SUPERSCRIPT), "(");
        for (int i = 0; i < gSup.length; i++)
        {
            sto.appendText(
                EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_SUPERSCRIPT),
                String.valueOf(gSup[i])
            );
            if (hasBigSup && i < gSup.length - 1)
                sto.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_SUPERSCRIPT), ",");
        }
        if (hasBigSup)
            sto.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_SUPERSCRIPT), ")");
        return sto;
    }

    public String toString()
    {
        return toOutput().toString();
    }
    /**
     * Get the G+ value of a genus.
     * @return G+ value of the genus.
     */
    public int gBase()
    {
        return gBase;
    }

    public int misereNimValue()
    {
        if (phylum == Phylum.NIMHEAP)
            return gBase < 2 ? 1 - gBase : gBase;
        else
            return gSup[0];
    }

    /**
     * Get the number of superscript digits
     *
     * @return number of superscript digits of this genus.
     */
    public int supLength()
    {
        return gSup.length;
    }

    /*
     * least int i such that supDigit(i) != supDigit(i-2);
     */
    private int maxDiffer()
    {
        if (phylum.isGenerallyTame())
        {
            return gBase < 2 && (gSup.length == 0 || gSup[0] != gBase)
                ? 2 : 1;
        }
        if (phylum.isGenerallyRestive())
        {
            return gSup[0] < 4 ? 1 : 2;
        }
        return gSup.length-1;
    }

    /**
     * Get the <code>i</code><sup>th</sup> superscript digit
     * of this genus.  <code>i</code> may be any nonnegative
     * integer.
     * @param i index of the superscript digit.
     * @return <code>i</code><sup>th</sup> superscript digit
     * of this genus.
     */
    public int supDigit(int i)
    {
        if (i < gSup.length)
        {
            return gSup[i];
        }
        if (phylum.isGenerallyTame())
        {
            if (i==0 && gBase < 2) return 1-gBase;
            return gBase ^ ((i&1)<<1);
        }
        if (phylum.isGenerallyRestive())
        {
            return (gSup[0]<4 ? gSup[0] : gBase) ^ ((i&1)<<1);
        }
        return gSup[gSup.length-2 + ((gSup.length ^ i)&1)];
    }

    /*
     * Return the genus's phylum, one of NIMHEAP, TAME, RESTIVE, or WILD;
     * @return Phylum of this genus.
     */
    public Phylum getPhylum()
    {
        return phylum;
    }
    
    private static void init()
    {
        mexBitSet=new BitSet();
        gBuilder = new int[10];
        singletons = new int[0][];
    }
}
