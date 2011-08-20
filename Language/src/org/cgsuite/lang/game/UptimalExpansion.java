/*
 * UptimalExpansion.java
 *
 * Created on August 4, 2007, 1:36 PM
 * $Id: UptimalExpansion.java,v 1.2 2007/08/16 20:52:52 asiegel Exp $
 */

/* ****************************************************************************

    Combinatorial Game Suite - A program to analyze combinatorial games
    Copyright (C) 2003-07  Aaron Siegel (asiegel@users.sourceforge.net)
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

import java.util.EnumSet;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
import static org.cgsuite.lang.output.StyledTextOutput.Style.*;
import static org.cgsuite.lang.output.StyledTextOutput.Symbol.*;

/**
 * An uptimal expansion, represented as a sequence of uptimal digits.  The
 * uptimal expansion
 * <p>
 * <code>x.d<sub>1</sub>d<sub>2</sub>d<sub>3</sub>...d<sub>n</sub></code>
 * <p>
 * (where <code>x</code> is a number and each <code>d<sub>i</sub></code> is
 * an integer) corresponds to the game
 * <p>
 * <code>x + d<sub>1</sub>&uarr; + d<sub>2</sub>&uarr;<sup>2</sup> + ... + d<sub>n</sub>&uarr;<sup>n</sup></code>
 * <p>
 * A game is said to be an <i>uptimal</i> if it matches an expansion of the
 * form
 * <p>
 * <code>x.d<sub>1</sub>d<sub>2</sub>d<sub>3</sub>...d<sub>n</sub></code>
 * <p>
 * or
 * <p>
 * <code>x.d<sub>1</sub>d<sub>2</sub>d<sub>3</sub>...d<sub>n</sub> + *</code>
 * <p>
 * You can use the {@link CanonicalGame#uptimal(UptimalExpansion) CanonicalGame.uptimal}
 * and {@link CanonicalGame#uptimalExpansion() CanonicalGame.uptimalExpansion} methods
 * to convert between uptimal expansions and the corresponding games.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.2 $ $Date: 2007/08/16 20:52:52 $
 * @since   0.7.1
 */
public class UptimalExpansion extends CgsuiteObject
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("UptimalExpansion");
    
    public final static UptimalExpansion ZERO = new UptimalExpansion(RationalNumber.ZERO, false);
    public final static UptimalExpansion BASE = new UptimalExpansion(RationalNumber.ZERO, true);

    private int[] coefficients;
    private boolean includeBase;
    private RationalNumber numberPart;

    /**
     * Constructs an <code>UptimalExpansion</code> with the specified coefficients.
     * This returns the expansion
     * <p>
     * <code>0.d<sub>1</sub>d<sub>2</sub>d<sub>3</sub>...d<sub>n</sub></code>
     * <p>
     * where <code>d<sub>i+1</sub> = coefficients[i]</code>.
     *
     * @param   coefficients the coefficients for this expansion
     *
     */
    public UptimalExpansion(int ... coefficients)
    {
        this(RationalNumber.ZERO, false, coefficients);
    }

    /**
     * Constructs an <code>UptimalExpansion</code> with the specified coefficients and
     * the specified number part, optionally include a <code>*</code>.  This returns
     * the expansion
     * <p>
     * <code>x.d<sub>1</sub>d<sub>2</sub>d<sub>3</sub>...d<sub>n</sub></code>
     * <p>
     * if <code>includeBase</code> is false, and
     * <p>
     * <code>x.d<sub>1</sub>d<sub>2</sub>d<sub>3</sub>...d<sub>n</sub> + *</code>
     * <p>
     * if <code>includeBase</code> is true,
     * where <code>d<sub>i+1</sub> = coefficients[i]</code>
     * and <code>x = numberPart</code>.
     *
     * @param   numberPart the number part of this expansion
     * @param   includeBase <code>true</code> to include a <code>*</code>
     * @param   coefficients the coefficients for this expansion
     *
     */
    public UptimalExpansion(RationalNumber numberPart, boolean includeBase, int ... coefficients)
    {
        super(TYPE);
        this.numberPart = numberPart;
        this.includeBase = includeBase;
        int length = 0;
        for (int i = coefficients.length-1; i >= 0; i--)
        {
            if (coefficients[i] != 0)
            {
                length = i + 1;
                break;
            }
        }
        this.coefficients = new int[length];
        System.arraycopy(coefficients, 0, this.coefficients, 0, length);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof UptimalExpansion))
        {
            return false;
        }
        return ((UptimalExpansion) obj).numberPart.equals(numberPart) &&
            ((UptimalExpansion) obj).includeBase == includeBase &&
            java.util.Arrays.equals(((UptimalExpansion) obj).coefficients, coefficients);
    }

    @Override
    public int hashCode()
    {
        return numberPart.hashCode() ^ (includeBase ? 0x55555555 : 0) ^ java.util.Arrays.hashCode(coefficients);
    }

    /**
     * Returns the length of this expansion.  This is equal to the largest
     * <code>i</code> for which <code>d<sub>i</sub> != 0</code>.
     *
     * @return  the length of this expansion
     */
    public int length()
    {
        return coefficients.length;
    }

    /**
     * Gets a coefficient for this expansion.  The coefficients are
     * ``one-based,'' so <code>getCoefficient(i)</code> returns
     * <code>d<sub>i</sub></code>, not <code>d<sub>i+1</sub></code>.
     *
     * @param   n the index of the coefficient
     * @return  the coefficient at index <code>n</code>
     */
    public int getCoefficient(int n)
    {
        if (n > coefficients.length)
        {
            return 0;
        }
        else
        {
            return coefficients[n-1];
        }
    }

    /**
     * Returns <code>true</code> if a <code>*</code> is included with this expansion.
     */
    public boolean hasBase()
    {
        return includeBase;
    }

    /**
     * Gets the number part of this expansion.
     */
    public RationalNumber getNumberPart()
    {
        return numberPart;
    }

    public UptimalExpansion increment(boolean toggleBase)
    {
        int[] newCoefficients = new int[coefficients.length];
        for (int i = 0; i < newCoefficients.length; i++)
        {
            newCoefficients[i] = coefficients[i] + 1;
        }
        return new UptimalExpansion(numberPart, toggleBase ? !includeBase : includeBase, newCoefficients);
    }

    public UptimalExpansion decrement(boolean toggleBase)
    {
        int[] newCoefficients = new int[coefficients.length];
        for (int i = 0; i < newCoefficients.length; i++)
        {
            newCoefficients[i] = coefficients[i] - 1;
        }
        return new UptimalExpansion(numberPart, toggleBase ? !includeBase : includeBase, newCoefficients);
    }

    public UptimalExpansion getInverse()
    {
        int[] newCoefficients = new int[coefficients.length];
        for (int i = 0; i < newCoefficients.length; i++)
        {
            newCoefficients[i] = -coefficients[i];
        }
        return new UptimalExpansion(numberPart.negate(), includeBase, newCoefficients);
    }

    public UptimalExpansion truncateTo(int n)
    {
        n = Math.min(n, coefficients.length);
        int[] newCoefficients = new int[n];
        System.arraycopy(coefficients, 0, newCoefficients, 0, n);
        return new UptimalExpansion(numberPart, includeBase, newCoefficients);
    }

    public UptimalExpansion addToCoefficient(int n, int value)
    {
        int[] newCoefficients = new int[Math.max(n, coefficients.length)];
        System.arraycopy(coefficients, 0, newCoefficients, 0, coefficients.length);
        newCoefficients[n-1] += value;
        return new UptimalExpansion(numberPart, includeBase, newCoefficients);
    }

    public boolean isConfused()
    {
        if (!includeBase)
        {
            return false;
        }
        if (coefficients.length == 0 || coefficients[0] == 0)
        {
            return true;
        }
        boolean positive = (coefficients[0] > 0);
        for (int n = 0; n < coefficients.length; n++)
        {
            if (!positive && coefficients[n] < -1 || positive && coefficients[n] > 1)
            {
                return false;
            }
            if (!positive && coefficients[n] > -1 || positive && coefficients[n] < 1)
            {
                return true;
            }
        }
        return true;
    }

    public boolean isUnit()
    {
        boolean found = false;
        for (int n = 0; n < coefficients.length; n++)
        {
            if (coefficients[n] != 0)
            {
                if (found || coefficients[n] > 1 || coefficients[n] < -1)
                {
                    return false;
                }
                else
                {
                    found = true;
                }
            }
        }
        return true;
    }

    public boolean isUnitSum()
    {
        if (coefficients.length == 0)
        {
            return true;
        }
        if (coefficients[0] != 1 && coefficients[0] != -1)
        {
            return false;
        }
        for (int n = 1; n < coefficients.length; n++)
        {
            if (coefficients[n] != coefficients[0])
            {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Output toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        
        if (!hasBase() || !getNumberPart().equals(RationalNumber.ZERO))
        {
            output.appendOutput(getNumberPart().toOutput());
        }
        
        if (hasBase())
        {
            output.appendSymbol(STAR);
        }

        output.appendMath(".");
        
        if (length() == 0)
        {
            output.appendMath("0");
        }
        
        for (int n = 1; n <= length(); n++)
        {
            int d = getCoefficient(n);

            boolean negative = false;
            
            if (d < 0)
            {
                d = -d;
                negative = true;
            }
            
            String str;
            
            if (d < 10)
                str = String.valueOf((char)(d+48));
            else if (d < 36)
                str = String.valueOf((char)(d+55));
            else
                str = "(" + String.valueOf(d) + ")";
            
            output.appendMath(str);
            
            if (negative)
            {
                output.appendSymbol(EnumSet.of(LOCATION_SUPERSCRIPT), DASH);
            }
        }
        
        return output;
    }
}
