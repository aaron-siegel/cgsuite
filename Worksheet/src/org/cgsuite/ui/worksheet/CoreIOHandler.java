/*
 * CoreIOHandler.java
 *
 * Created on June 24, 2004, 5:24 PM
 * $Id: CoreIOHandler.java,v 1.22 2008/01/10 22:26:07 haoyuep Exp $
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

package org.cgsuite.ui.worksheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.cgsuite.lang.CgsuiteList;
import org.cgsuite.lang.CgsuiteMap;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuiteSet;
import org.cgsuite.lang.CgsuiteString;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.game.CanonicalShortGame;
import org.cgsuite.lang.game.ExplicitGame;
import org.cgsuite.lang.game.InverseGame;
import org.cgsuite.lang.game.LoopyGame;
import org.cgsuite.lang.game.RationalNumber;
import org.cgsuite.lang.game.SumGame;
import org.cgsuite.lang.game.UptimalExpansion;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
import static org.cgsuite.lang.output.StyledTextOutput.Style.*;
import static org.cgsuite.lang.output.StyledTextOutput.Symbol.*;

/**
 * Creates output for instances of {@link org.cgsuite.CanonicalShortGame} and other
 * core classes.  The output will generally be more sophisticated than that
 * provided by <code>toString</code>.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.22 $ $Date: 2008/01/10 22:26:07 $
 * @since   0.6
 */
public class CoreIOHandler
{
    private EnumSet<DisplayOption> displayOptions = EnumSet.of(
        DisplayOption.NIMBERS,
        DisplayOption.UP_MULTIPLES,
        DisplayOption.TINIES,
        DisplayOption.SWITCHES,
        DisplayOption.UP_POW,
        DisplayOption.ZERO_POW
        );
    private int maxSlashes = 4;
    private RationalNumber infinityCutoff = null;
    
    /**
     * Specifies options that will be used when rendering output.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.22 $ $Date: 2008/01/10 22:26:07 $
     * @since   0.7
     */
    public enum DisplayOption
    {
        /** Indicates that nimbers will be displayed in condensed form.
         * For example, <code>{3|3||3|3}</code> will be displayed as
         * <code>3*2</code>. */
        NIMBERS,
        /** Indicates that multiples of &uarr; will be displayed in
         * condensed form.  For example, <code>{4|4||4}</code> will be
         * displayed as <code>4&darr;</code>. */
        UP_MULTIPLES,
        /** Indicates that tinies and minies will be displayed in
         * condensed form.  For example, <code>{2|0||0}</code> will be
         * displayed as <code>+<sub>2</sub></code>. */
        TINIES,
        SWITCHES,
        UP_POW,
        SUPERSTARS,
        ZERO_POW,
        //POWTO,
        UPTIMALS,
        MEAN_ZERO_MODE,
        ABBREVIATE_INFINITESIMALS
    }
    
    /**
     * Constructs a new <code>CoreIOHandler</code>.
     */
    public CoreIOHandler()
    {
    }
    
    public int getMaxSlashes()
    {
        return maxSlashes;
    }
    
    public void setMaxSlashes(int maxSlashes)
    {
        this.maxSlashes = maxSlashes;
    }
    
    public EnumSet<DisplayOption> getDisplayOptions()
    {
        return displayOptions;
    }
    
    public void setDisplayOptions(EnumSet<DisplayOption> displayOptions)
    {
        this.displayOptions = displayOptions;
    }
    
    public RationalNumber getInfinityCutoff()
    {
        return infinityCutoff;
    }
    
    public void setInfinityCutoff(RationalNumber cutoff)
    {
        this.infinityCutoff = cutoff;
    }
    
    private StyledTextOutput rationalToOutput(RationalNumber rational)
    {
        StyledTextOutput output = new StyledTextOutput();
        
        if (rational.isInfinite() ||
            (infinityCutoff != null && rational.abs().compareTo(infinityCutoff) >= 0))
        {
            if (rational.compareTo(RationalNumber.ZERO) < 0)
            {
                output.appendMath("-");
            }
            output.appendSymbol(INFINITY);
        }
        else if (rational.isInteger())
        {
            output.appendMath(String.valueOf(rational.getNumerator()));
        }
        else
        {
            if (rational.compareTo(RationalNumber.ZERO) < 0)
            {
                output.appendMath("-");
            }
            output.appendText(
                EnumSet.of(FACE_MATH, LOCATION_NUMERATOR),
                String.valueOf(rational.getNumerator().abs())
                );
            output.appendMath("/");
            output.appendText(
                EnumSet.of(FACE_MATH, LOCATION_DENOMINATOR),
                String.valueOf(rational.getDenominator())
                );
        }
        return output;
    }
    
    private int getUpExponent(CanonicalShortGame g)
    {
        if (g.getNumLeftOptions() == 1 && g.getNumRightOptions() == 1 &&
            g.getLeftOption(0).equals(CanonicalShortGame.ZERO))
        {
            int n = getUptoStarExponent(g.getRightOption(0).negate());
            return (n == -1 ? -1 : n + 1);
        }
        else
        {
            return -1;
        }
    }
    
    private int getUptoStarExponent(CanonicalShortGame g)
    {
        if (g.equals(CanonicalShortGame.STAR))
        {
            return 0;
        }
        else if (g.getNumLeftOptions() == 2 && g.getLeftOption(0).equals(CanonicalShortGame.ZERO) &&
                 g.getNumRightOptions() == 1 && g.getRightOption(0).equals(CanonicalShortGame.ZERO))
        {
            int n = getUptoStarExponent(g.getLeftOption(1));
            return (n == -1 ? -1 : n+1);
        }
        else
        {
            return -1;
        }
    }
    
    private GameIntegerPair getPowTo(CanonicalShortGame g)
    {
        if (g.getNumLeftOptions() != 1 || g.getNumRightOptions() != 1)
        {
            return null;
        }
        if (g.getLeftOption(0).equals(CanonicalShortGame.ZERO))
        {
            return new GameIntegerPair(g, 1);
        }
        GameIntegerPair gip = getPowTo(g.getLeftOption(0));
        if (gip == null || !gip.g.getRightOption(0).equals(g.getRightOption(0)))
        {
            return null;
        }
        else
        {
            gip.n++;
            return gip;
        }
    }
    
    private Uptimal getUptimal(CanonicalShortGame g)
    {
        if (uptimalMap.containsKey(g))
        {
            return uptimalMap.get(g);
        }
        if (uptimalMap.containsKey(g.negate()))
        {
            return uptimalMap.get(g.negate()).getInverse();
        }
        if (g.isZero())
        {
            return new Uptimal(0);
        }
        if (g.isStar())
        {
            return new Uptimal(1);
        }
        Uptimal uptimal = getUptimal2(g);
        if (uptimal != null)
        {
            //System.out.println("A " + g + ": " + Arrays.toString(uptimal.indices));
            uptimalMap.put(g, uptimal);
            return uptimal;
        }
        uptimal = getUptimal2(g.negate());
        if (uptimal != null)
        {
            //System.out.println("B " + g + ": " + Arrays.toString(uptimal.indices));
            uptimalMap.put(g.negate(), uptimal);
            return uptimal.getInverse();
        }
        return null;
    }
    
    private Uptimal getUptimal2(CanonicalShortGame g)
    {
        if (g.getNumRightOptions() == 1 && g.getNumLeftOptions() <= 2)
        {
            Uptimal rightUptimal = getUptimal(g.getRightOption(0));
            if (rightUptimal != null)
            {
                // d_k > 1 case:
                if (rightUptimal.increment().toggleStar().matches(g))
                {
                    return rightUptimal.increment().toggleStar();
                }
                // d_k = 1 case:
                Uptimal leftUptimal = getUptimal(g.getLeftOption(0));
                if (leftUptimal == null)
                {
                    return null;
                }
                Uptimal testUptimal = leftUptimal.addOneAtIndex(Math.max(leftUptimal.length(), rightUptimal.length())+1);
                if (testUptimal.matches(g))
                {
                    return testUptimal;
                }
                if (g.getNumLeftOptions() > 1)
                {
                    leftUptimal = getUptimal(g.getLeftOption(1));
                    if (leftUptimal == null)
                    {
                        return null;
                    }
                    testUptimal = leftUptimal.addOneAtIndex(Math.max(leftUptimal.length(), rightUptimal.length())+1);
                    if (testUptimal.matches(g))
                    {
                        return testUptimal;
                    }
                }
            }
        }
        return null;
    }
    
    private static Map<CanonicalShortGame,Uptimal> uptimalMap = new HashMap<CanonicalShortGame,Uptimal>();
    
    private class Uptimal
    {
        int[] indices;
        
        Uptimal(int ... indices)
        {
            this.indices = indices;
            assert indices[0] == 0 || indices[0] == 1 : indices[0];
        }
        
        Uptimal toggleStar()
        {
            int[] newIndices = new int[indices.length];
            System.arraycopy(indices, 0, newIndices, 0, indices.length);
            newIndices[0] = 1 - newIndices[0];
            return new Uptimal(newIndices);
        }
        
        Uptimal increment()
        {
            int k = length();
            int[] newIndices = new int[k+1];
            newIndices[0] = indices[0];
            for (int i = 1; i <= k; i++)
            {
                newIndices[i] = indices[i] + 1;
            }
            return new Uptimal(newIndices);
        }
        
        Uptimal decrement()
        {
            int k = length();
            int[] newIndices = new int[k+1];
            newIndices[0] = indices[0];
            for (int i = 1; i <= k; i++)
            {
                newIndices[i] = indices[i] - 1;
            }
            return new Uptimal(newIndices);
        }
        
        int length()
        {
            for (int i = indices.length-1; i >= 1; i--)
            {
                if (indices[i] != 0)
                {
                    return i;
                }
            }
            return 0;
        }
        
        Uptimal addOneAtIndex(int i)
        {
            int[] newIndices = new int[Math.max(indices.length, i+1)];
            System.arraycopy(indices, 0, newIndices, 0, indices.length);
            newIndices[i]++;
            return new Uptimal(newIndices);
        }
        
        Uptimal getInverse()
        {
            int[] newIndices = new int[indices.length];
            newIndices[0] = indices[0];
            for (int i = 1; i < indices.length; i++)
            {
                newIndices[i] = -indices[i];
            }
            return new Uptimal(newIndices);
        }
        
        Uptimal truncateTo(int n)
        {
            assert n >= 0;
            int[] newIndices = new int[n+1];
            System.arraycopy(indices, 0, newIndices, 0, n+1);
            return new Uptimal(newIndices);
        }
        
        boolean confusedWithZero()
        {
            if (indices[0] == 0)
            {
                return false;
            }
            if (indices.length == 1 || indices[1] == 0)
            {
                return true;
            }
            int sign = (indices[1] > 0 ? 1 : -1);
            for (int i = 1; i < indices.length; i++)
            {
                if (sign == 1 && indices[i] > 1 ||
                    sign == -1 && indices[i] < -1)
                {
                    return false;
                }
                if (sign == 1 && indices[i] < 1 ||
                    sign == -1 && indices[i] > -1)
                {
                    return true;
                }
            }
            return true;
        }
        
        boolean matches(CanonicalShortGame g)
        {
            int k = length();
            if (k == 0)
            {
                return (indices[0] == 0 ? g.isZero() : g.isStar());
            }
            if (indices[k] < 0)
            {
                return getInverse().matches(g.negate());
            }
            assert indices[k] > 0;
            int j = 0, m = 0;
            for (int i = 1; i <= k; i++)
            {
                if (indices[i] < 0)
                {
                    j = i;
                }
                if (indices[i] <= 0)
                {
                    m = i;
                }
            }
            if (g.getNumRightOptions() != 1)
            {
                return false;
            }
            if (indices[k] == 1)
            {
                m = k-1;
            }
            //System.out.println("k = " + k + ", j = " + j + ", m = " + m);
            Uptimal rightUptimal = getUptimal(g.getRightOption(0));
            if (!decrement().toggleStar().equals(rightUptimal))
            {
                //System.out.println("A Killed " + g);
                return false;
            }
            if (confusedWithZero() && j == 0)
            {
                // Confused-with-0 case.
                //System.out.println("E Killed " + g);
                return g.getNumLeftOptions() == 2 && g.getLeftOption(0).isZero() && truncateTo(m).equals(getUptimal(g.getLeftOption(1)));
            }
            // Special case:
            if (indices[0] == 1 && j == 0 && m == 0 && length() == rightUptimal.length() && !confusedWithZero())
            {
                return g.getNumLeftOptions() == 1 && g.getLeftOption(0).isZero();
            }
            if (!(g.getNumLeftOptions() == 1 && j == 0 || g.getNumLeftOptions() == 2 && j != 0))
            {
                //System.out.println("B Killed " + g);
                return false;
            }
            Uptimal leftUptimal1 = getUptimal(g.getLeftOption(0));
            if (leftUptimal1 == null)
            {
                //System.out.println("C Killed " + g);
                return false;
            }
            if (j == 0)
            {
                if (!truncateTo(m).equals(leftUptimal1))
                {
                    //System.out.println("D Killed " + g);
                    return false;
                }
            }
            else
            {
                Uptimal leftUptimal2;   // The one of different star-parity
                if (leftUptimal1.indices[0] != indices[0])
                {
                    leftUptimal2 = leftUptimal1;
                    leftUptimal1 = getUptimal(g.getLeftOption(1));
                }
                else
                {
                    leftUptimal2 = getUptimal(g.getLeftOption(1));
                }
                if (leftUptimal1 == null || leftUptimal2 == null ||
                    !truncateTo(m).equals(leftUptimal1) ||
                    !truncateTo(j).increment().toggleStar().equals(leftUptimal2))
                {
                    //System.out.println("F Killed " + g);
                    return false;
                }
            }
            return true;
        }
        
        public @Override boolean equals(Object obj)
        {
            if (!(obj instanceof Uptimal))
            {
                return false;
            }
            if (((Uptimal) obj).length() != length())
            {
                return false;
            }
            for (int i = 0; i <= length(); i++)
            {
                if (((Uptimal) obj).indices[i] != indices[i])
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 67 * hash + Arrays.hashCode(this.indices);
            return hash;
        }
    }
    
    private String getSuperstarString(CanonicalShortGame g)
    {
        // A superstar has form
        // {0,*,...,*m | 0,*,...,*(m-1),*a,*b,*c,...}
        // where all of a,b,c,... > m
        int m = g.getNumLeftOptions() - 1;
        if (g.getNumRightOptions() < m)
        {
            return null;
        }
        for (int i = 0; i <= m; i++)
        {
            if (!g.getLeftOption(i).isNimber() || g.getLeftOption(i).getNimberPart() != i ||
                (i < m && !g.getRightOption(i).equals(g.getLeftOption(i))))
            {
                return null;
            }
        }
        for (int i = m; i < g.getNumRightOptions(); i++)
        {
            if (!g.getRightOption(i).isNimber())
            {
                return null;
            }
        }
        if (g.getNumRightOptions() > m && g.getRightOption(m).getNimberPart() == m)
        {
            return null;
        }
        
        String s = "";
        for (int i = 0; i < g.getNumRightOptions(); i++)
        {
            s += String.valueOf(g.getRightOption(i).getNimberPart());
            if (i+1 < g.getNumRightOptions())
            {
                s += ",";
            }
        }
        return s;
    }

    private GameIntegerPair getZeroNth(CanonicalShortGame g)
    {
        if (g.getNumLeftOptions() != 1 || g.getNumRightOptions() != 1 ||
            !g.getLeftOption(0).equals(CanonicalShortGame.ZERO))
        {
            return null;
        }
        CanonicalShortGame h = g.getRightOption(0);
        if (displayAsNumberUpStar(h) || displayAsNumberTiny(h))
        {
            return new GameIntegerPair(h, 1);
        }
        GameIntegerPair gip = getZeroNth(h);
        if (gip == null)
        {
            return new GameIntegerPair(h, 1);
        }
        else
        {
            gip.n++;
            return gip;
        }
    }
    
    private boolean displayAsNumberUpStar(CanonicalShortGame g)
    {
        return g.isNumberUpStar() &&
            (displayOptions.contains(DisplayOption.NIMBERS) || g.getNimberPart() == 0) &&
            (displayOptions.contains(DisplayOption.UP_MULTIPLES) || g.getUpMultiplePart() == 0);
    }
    
    private boolean displayAsNumberTiny(CanonicalShortGame g)
    {
        return displayOptions.contains(DisplayOption.TINIES) && g.isNumberTiny();
    }

    private int canonicalGameToOutput(
        CanonicalShortGame g,
        StyledTextOutput output,
        boolean forceBrackets,
        boolean forceParens
        )
    {
        int rVal;
        StyledTextOutput prefix = null, suffix = null;
        
        if (displayOptions.contains(DisplayOption.ABBREVIATE_INFINITESIMALS))
        {
            CanonicalShortGame rcf = g.rcf();
            if (!g.equals(rcf))
            {
                g = rcf;
                suffix = new StyledTextOutput();
                suffix.appendMath("ish");
            }
        }
        
        if (displayOptions.contains(DisplayOption.MEAN_ZERO_MODE) && !g.isNumber())
        {
            RationalNumber mean = g.mean();
            if (!mean.equals(RationalNumber.ZERO))
            {
                prefix = rationalToOutput(mean);
                g = g.subtract(CanonicalShortGame.construct(mean, 0, 0));
            }
        }
        
        boolean complex = (prefix != null || suffix != null);
        
        if (forceParens && complex)
        {
            output.appendMath("(");
        }
        if (prefix != null)
        {
            output.appendOutput(prefix);
        }
        rVal = canonicalGameToOutput2(g, output, forceBrackets || complex, forceParens && !complex);
        if (suffix != null)
        {
            output.appendOutput(suffix);
        }
        if (forceParens && complex)
        {
            output.appendMath(")");
        }
        return rVal;
    }

    // This should ONLY be called by canonicalGameToOutput.
    private int canonicalGameToOutput2(
        CanonicalShortGame g,
        StyledTextOutput output,
        boolean forceBrackets,
        boolean forceParens
        )
    {
        CanonicalShortGame inverse = g.negate();
        
        // Check for number-up-star.
        if (displayAsNumberUpStar(g))
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
                output.appendOutput(rationalToOutput(g.getNumberPart()));
            }
            if (g.getUpMultiplePart() != 0)
            {
                StyledTextOutput.Symbol upSymbol;
                if (g.getUpMultiplePart() == 2) upSymbol = DOUBLE_UP;
                //else if (g.getUpMultiplePart() == 3) upSymbol = TRIPLE_UP;
                //else if (g.getUpMultiplePart() == 4) upSymbol = QUADRUPLE_UP;
                else if (g.getUpMultiplePart() == -2) upSymbol = DOUBLE_DOWN;
                else if (g.getUpMultiplePart() > 0) upSymbol = UP;
                else upSymbol = DOWN;
                output.appendSymbol(upSymbol);
                if (Math.abs(g.getUpMultiplePart()) > 2)
                {
                    output.appendMath(String.valueOf(Math.abs(g.getUpMultiplePart())));
                }
            }
            if (g.getNimberPart() != 0)
            {
                output.appendSymbol(STAR);
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
        else if (displayOptions.contains(DisplayOption.SWITCHES) && g.isSwitch())
        {
            output.appendSymbol(PLUS_MINUS);
            if (g.getNumLeftOptions() > 1)
            {
                output.appendMath("(");
            }
            for (int i = 0; i < g.getNumLeftOptions(); i++)
            {
                canonicalGameToOutput(g.getLeftOption(i), output, true, g.getNumLeftOptions() == 1);
                if (i < g.getNumLeftOptions() - 1)
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
        else if (displayAsNumberTiny(g))
        {
            if (forceParens)
            {
                output.appendMath("(");
            }
            if (!g.getLeftOption(0).equals(CanonicalShortGame.ZERO))
            {
                output.appendOutput(rationalToOutput(g.getLeftOption(0).getNumberPart()));
                output.appendText(Output.Mode.PLAIN_TEXT, "+");
            }
            // First get a sequence for the subscript.  If that sequence contains any
            // subscripts or superscripts, then we display this as Tiny(G) rather than +_G.
            StyledTextOutput sub = new StyledTextOutput();
            canonicalGameToOutput(
                (g.getRightOption(0).getRightOption(0).negate()).add
                    (g.getLeftOption(0)),
                sub,
                true,
                false
                );
            EnumSet<StyledTextOutput.Style> styles = sub.allStyles();
            styles.retainAll(StyledTextOutput.Style.TRUE_LOCATIONS);
            if (styles.isEmpty())
            {
                output.appendSymbol(EnumSet.noneOf(StyledTextOutput.Style.class), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), TINY);
                output.appendText(Output.Mode.PLAIN_TEXT, "Tiny(");
                output.appendOutput(EnumSet.of(LOCATION_SUBSCRIPT), sub);
                output.appendText(Output.Mode.PLAIN_TEXT, ")");
            }
            else
            {
                output.appendMath("Tiny(");
                output.appendOutput(sub);
                output.appendMath(")");
            }
            if (forceParens)
            {
                output.appendMath(")");
            }
            return 0;
        }
        else if (displayAsNumberTiny(inverse))
        {
            if (forceParens)
            {
                output.appendMath("(");
            }
            if (!g.getRightOption(0).equals(CanonicalShortGame.ZERO))
            {
                output.appendOutput(rationalToOutput(g.getRightOption(0).getNumberPart()));
                output.appendText(Output.Mode.PLAIN_TEXT, "+");
            }
            StyledTextOutput sub = new StyledTextOutput();
            canonicalGameToOutput(
                g.getLeftOption(0).getLeftOption(0).subtract(g.getRightOption(0)),
                sub,
                true,
                false
                );
            EnumSet<StyledTextOutput.Style> styles = sub.allStyles();
            styles.retainAll(StyledTextOutput.Style.TRUE_LOCATIONS);
            if (styles.isEmpty())
            {
                output.appendSymbol(EnumSet.noneOf(StyledTextOutput.Style.class), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), MINY);
                output.appendText(Output.Mode.PLAIN_TEXT, "Miny(");
                output.appendOutput(EnumSet.of(LOCATION_SUBSCRIPT), sub);
                output.appendText(Output.Mode.PLAIN_TEXT, ")");
            }
            else
            {
                output.appendMath("Miny(");
                output.appendOutput(sub);
                output.appendMath(")");
            }
            if (forceParens)
            {
                output.appendMath(")");
            }
            return 0;
        }
        
        if (displayOptions.contains(DisplayOption.UP_POW) ||
            displayOptions.contains(DisplayOption.UPTIMALS))
        {
            UptimalExpansion uptimal = g.uptimalExpansion();
            if (uptimal != null)
            {
                if (displayOptions.contains(DisplayOption.UPTIMALS) ||
                    displayOptions.contains(DisplayOption.UP_POW) && (uptimal.isUnit() || uptimal.isUnitSum()))
                {
                    uptimalToOutput(output, uptimal);
                    return 0;
                }
            }
        }
        
        if (displayOptions.contains(DisplayOption.SUPERSTARS))
        {
            // Check for superstars.
            String str;
            str = getSuperstarString(g);
            if (str != null)
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "Superstar(");
                output.appendSymbol(EnumSet.noneOf(StyledTextOutput.Style.class), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), UP);
                output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), str);
                output.appendText(Output.Mode.PLAIN_TEXT, ")");
                return 0;
            }
            str = getSuperstarString(inverse);
            if (str != null)
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "-Superstar(");
                output.appendSymbol(EnumSet.noneOf(StyledTextOutput.Style.class), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), DOWN);
                output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT), str);
                output.appendText(Output.Mode.PLAIN_TEXT, ")");
                return 0;
            }
        }
        
        // General case.

        StyledTextOutput leftOutput = new StyledTextOutput(), rightOutput = new StyledTextOutput();
        int numSlashes;
        
        // First we build the left & right OS's and calculate the number of slashes.
        // There are several cases.

        GameIntegerPair gip;
        if (displayOptions.contains(DisplayOption.ZERO_POW) && (gip = getZeroNth(g)) != null && gip.n > 1)
        {
            // Case 1: {0^n|h}
            numSlashes = canonicalGameToOutput(gip.g, rightOutput, false, false) + 1;
            leftOutput.appendMath("0");
            leftOutput.appendText(Output.Mode.PLAIN_TEXT, "<");
            leftOutput.appendText(
                EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT),
                String.valueOf(gip.n)
                );
            leftOutput.appendText(Output.Mode.PLAIN_TEXT, ">");
        }
        else if (displayOptions.contains(DisplayOption.ZERO_POW) &&
            (gip = getZeroNth(g.negate())) != null && gip.n > 1)
        {
            // Case 2: {h|0^n}
            numSlashes = canonicalGameToOutput
                (gip.g.negate(), leftOutput, false, false) + 1;
            rightOutput.appendMath("0");
            rightOutput.appendText(Output.Mode.PLAIN_TEXT, "<");
            rightOutput.appendText(
                EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT),
                String.valueOf(gip.n)
                );
            rightOutput.appendText(Output.Mode.PLAIN_TEXT, ">");
        }
        else
        {
            // Case 3: General case
            numSlashes = 1;
            int numLO = g.getNumLeftOptions(), numRO = g.getNumRightOptions();
            for (int i = 0; i < numLO; i++)
            {
                numSlashes = Math.max(
                    numSlashes,
                    canonicalGameToOutput(g.getLeftOption(i), leftOutput, numLO > 1, false) + 1
                    );
                if (i < numLO - 1)
                {
                    leftOutput.appendMath(",");
                }
            }
            for (int i = 0; i < numRO; i++)
            {
                numSlashes = Math.max(
                    numSlashes,
                    canonicalGameToOutput(g.getRightOption(i), rightOutput, numRO > 1, false) + 1
                    );
                if (i < numRO - 1)
                {
                    rightOutput.appendMath(",");
                }
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
    
    private String getSlashString(int n)
    {
        String slashString = "";
        for (int i = 0; i < n; i++) slashString += "|";
        return slashString;
    }
    
    private void uptimalToOutput(StyledTextOutput output, UptimalExpansion uptimal)
    {
        if (!uptimal.getNumberPart().equals(RationalNumber.ZERO))
        {
            output.appendOutput(rationalToOutput(uptimal.getNumberPart()));
        }
        if (displayOptions.contains(DisplayOption.UP_POW) && uptimal.isUnit())
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
                output.appendText(Output.Mode.PLAIN_TEXT, "Pow(");
                output.appendSymbol(UP);
                output.appendText(Output.Mode.PLAIN_TEXT, ",");
                output.appendText(
                    EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT),
                    String.valueOf(n)
                    );
                output.appendText(Output.Mode.PLAIN_TEXT, ")");
            }
            else
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "Pow(");
                output.appendSymbol(DOWN);
                output.appendText(Output.Mode.PLAIN_TEXT, ",");
                output.appendText(
                    EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT),
                    String.valueOf(n)
                    );
                output.appendText(Output.Mode.PLAIN_TEXT, ")");
            }
        }
        else if (displayOptions.contains(DisplayOption.UP_POW) && uptimal.isUnitSum())
        {
            int n = uptimal.length();
            boolean up = (uptimal.getCoefficient(1) > 0);
            if (up)
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "PowTo(");
                output.appendSymbol(UP);
                output.appendText(Output.Mode.PLAIN_TEXT, ",");
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
                output.appendText(Output.Mode.PLAIN_TEXT, "PowTo(");
                output.appendSymbol(DOWN);
                output.appendText(Output.Mode.PLAIN_TEXT, ",");
                output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), "[");
                output.appendText(
                    EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT),
                    String.valueOf(n)
                    );
                output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)), "]");
                output.appendText(Output.Mode.PLAIN_TEXT, ")");
            }
        }
        else
        {
            if (uptimal.getNumberPart().equals(RationalNumber.ZERO))
            {
                output.appendMath("0");
            }
            output.appendMath(".");
            for (int n = 1; n <= uptimal.length(); n++)
            {
                int d = uptimal.getCoefficient(n);
                // TODO: Redo using CodeDigit ?
                boolean negative = false;
                if (d < 0)
                {
                    d = -d;
                    negative = true;
                }
                output.appendMath(String.valueOf(d < 10 ? (char)(d+48) : (char)(d+55)));
                if (negative)
                {
                    output.appendSymbol(EnumSet.of(LOCATION_SUPERSCRIPT), DASH);
                }
            }
        }
        if (uptimal.hasBase())
        {
            output.appendSymbol(STAR);
        }
    }
    
    private String getNthName(int n)
    {
        if (n < 26)
        {
            return String.valueOf((char) (97+n));
        }
        else
        {
            return "N" + String.valueOf(n-25);
        }
    }
    
    private void loopyGameToOutput(
        LoopyGame g,
        StyledTextOutput output,
        boolean forceBrackets
        )
    {
        loopyGameToOutput(g, output, forceBrackets, new HashMap<LoopyGame,String>(), new int[1]);
    }
    
    private int loopyGameToOutput(
        LoopyGame g,
        StyledTextOutput output,
        boolean forceBrackets,
        Map<LoopyGame,String> nodeStack,
        int[] numNamedNodes
        )
    {
        if (nodeStack.containsKey(g))
        {
            String name = nodeStack.get(g);
            if (name == null)
            {
                name = getNthName(numNamedNodes[0]);
                nodeStack.put(g, name);
                numNamedNodes[0]++;
            }
            output.appendMath(name);
            return 0;
        }
        else if (g.isLoopfree())
        {
            return canonicalGameToOutput(g.loopfreeRepresentation(), output, forceBrackets, false);
        }
        else if (g.isOn())
        {
            output.appendMath("on");
            return 0;
        }
        else if (g.isOff())
        {
            output.appendMath("off");
            return 0;
        }
        else if (g.getLeftOptions().size() == 1 && g.getRightOptions().size() == 1)
        {
            LoopyGame gl = (LoopyGame) g.getLeftOptions().iterator().next(),
                      gr = (LoopyGame) g.getRightOptions().iterator().next();
            
            CanonicalShortGame glc = null, grc = null;
            
            if (gl.isLoopfree())
            {
                glc = gl.loopfreeRepresentation();
            }
            if (gr.isLoopfree())
            {
                grc = gr.loopfreeRepresentation();
            }
            
            if (glc != null && glc.isNumber() && gr.equals(g))
            {
                if (!glc.isZero())
                {
                    canonicalGameToOutput(glc, output, false, false);
                }
                output.appendMath("over");
                return 0;
            }
            else if (gl.equals(g) && grc != null && grc.isNumber())
            {
                if (!grc.isZero())
                {
                    canonicalGameToOutput(grc, output, false, false);
                }
                output.appendMath("under");
                return 0;
            }
        }
        
        nodeStack.put(g, null);
        int numSlashes = 1;
        StyledTextOutput leftOutput = new StyledTextOutput(), rightOutput = new StyledTextOutput();
        List<Game>
            leftOptions  = sortLoopyOptions(g.getLeftOptions()),
            rightOptions = sortLoopyOptions(g.getRightOptions());
        for (Iterator i = leftOptions.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (o.equals(g))
            {
                leftOutput.appendMath("pass");
            }
            else if(o instanceof CanonicalShortGame)
            {
                numSlashes = Math.max
                    (numSlashes, canonicalGameToOutput((CanonicalShortGame) o, leftOutput, leftOptions.size() > 1, false) + 1);
            }
            else
            {
                numSlashes = Math.max(numSlashes, loopyGameToOutput(
                    (LoopyGame) o,
                    leftOutput,
                    leftOptions.size() > 1,
                    nodeStack,
                    numNamedNodes
                    ) + 1);
            }
            if (i.hasNext())
            {
                leftOutput.appendMath(",");
            }
        }
        for (Iterator i = rightOptions.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (o.equals(g))
            {
                rightOutput.appendMath("pass");
            }
            else if (o instanceof CanonicalShortGame)
            {
                numSlashes = Math.max
                    (numSlashes, canonicalGameToOutput((CanonicalShortGame) o, rightOutput, rightOptions.size() > 1, false) + 1);
            }
            else
            {
                numSlashes = Math.max(numSlashes, loopyGameToOutput(
                    (LoopyGame) o,
                    rightOutput,
                    rightOptions.size() > 1,
                    nodeStack,
                    numNamedNodes
                    ) + 1);
            }
            if (i.hasNext())
            {
                rightOutput.appendMath(",");
            }
        }
        String name = nodeStack.remove(g);
        
        if (name != null)
        {
            output.appendMath(name + ":");
            forceBrackets = true;
        }
        if (leftOptions.isEmpty() || rightOptions.isEmpty())
        {
            // Force brackets for clarity.
            forceBrackets = true;
        }
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
    
    private List<Game> sortLoopyOptions(CgsuiteSet options)
    {
        List<Game> sortedOptions = new ArrayList<Game>();
        for (CgsuiteObject obj : options)
        {
            LoopyGame g = (LoopyGame) obj;
            if (g.isLoopfree())
            {
                sortedOptions.add(g.loopfreeRepresentation());
            }
            else
            {
                sortedOptions.add(g);
            }
        }
//        Collections.sort(sortedOptions, Context.getActiveContext().getGameComparator());
        return sortedOptions;
    }
    /*
    private Output trajectoryToOutput(Trajectory t)
    {
        StyledTextOutput output = rationalToOutput(t.getMastValue());
        output.appendMath(",[");
        for (int i = 0; i < t.getNumCriticalPoints(); i++)
        {
            output.appendOutput(rationalToOutput(t.getCriticalPoint(i)));
            if (i < t.getNumCriticalPoints() - 1)
            {
                output.appendMath(",");
            }
        }
        output.appendMath("],[");
        for (int i = 0; i <= t.getNumCriticalPoints(); i++)
        {
            output.appendOutput(rationalToOutput(t.getSlope(i)));
            if (i < t.getNumCriticalPoints())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("]");
        return output;
    }
    
    private void canonicalMisereGameToOutput(CanonicalMisereGame g, StyledTextOutput output, boolean parens, int[] subscripted)
    {
        if (subscripted==null)
        {
            subscripted=new int[]{0};
            canonicalMisereGameToOutput(g,output,parens,subscripted);
            if(subscripted[0]>0)
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "]");
            }
            return;
        }
        
        CodeDigit.Options codeDigitOptions = CodeDigit.decimalOptions;
        
        if (g.isNimber())
        {
            output.appendMath(new CodeDigit(g.birthday(),codeDigitOptions).toString());
            if (subscripted[0]>0)
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "[");
            }
            return;
        }
        
        int birthday = g.birthday();
        int parity = (g.isEven() ? 0 : 1);
        
        for (int i = 2 + parity; i < birthday; i += 2)
        {
            CanonicalMisereGame nimber = new CanonicalMisereGame(i);
            CanonicalMisereGame diff = g.subtract(nimber);
            if (diff != null && diff.birthday() < g.birthday())
            {
                subscripted[0]=1;
                canonicalMisereGameToOutput(diff, output, true, subscripted);
                output.appendText(
                    EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT),
                    new CodeDigit(i, codeDigitOptions).toString()
                    );
                return;
            }
            
            CanonicalMisereGame sum = g.add(nimber);
            
            if (sum.birthday() < g.birthday())
            {
                subscripted[0]=1;
                canonicalMisereGameToOutput(sum, output, true, subscripted);
                output.appendText(
                    EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT),
                    "-" + new CodeDigit(i, codeDigitOptions)
                    );
                return;
            }
        }
        
        if (parity==1) 
        {
            subscripted[0]=1;
            canonicalMisereGameToOutput(g.add(CanonicalMisereGame.ONE),
                                        output, true,subscripted);
            output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), "1");
            return;
        }

        List<CanonicalMisereGame> options = g.getOptions();

        if (options.size() == 1)
        {
            canonicalMisereGameToOutput(options.get(0),
                                        output, true, subscripted);
            output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), "/");
            return;
        }
        
        Collections.reverse(options);
        int subSave=subscripted[0];
        
        // Can't happen
        // parens = (parens && !g.isNimber() && g.numOptions() > 1);
        if (parens)
        {
            output.appendMath("(");
        }
        for (CanonicalMisereGame opt : options)
        {
            subscripted[0]= 0;

            canonicalMisereGameToOutput(opt, output, true, subscripted);
            if (subscripted[0]>0)
            {
                output.appendText(Output.Mode.PLAIN_TEXT, "]");
            }
        }
        if (parens)
        {
            output.appendMath(")");
        }
        subscripted[0]=subSave;
        if (subscripted[0]>0)
        {
            output.appendText(Output.Mode.PLAIN_TEXT, "[");
        }
    }
*/
    public Output createOutput(CgsuiteObject obj)
    {
        StyledTextOutput output = new StyledTextOutput();
        if (obj instanceof CanonicalShortGame)
        {
            canonicalGameToOutput((CanonicalShortGame) obj, output, true, false);
        }/*
        else if (obj instanceof CanonicalMisereGame)
        {
            output.appendSymbol(STAR);
            output.appendMath("[");
            canonicalMisereGameToOutput((CanonicalMisereGame) obj, output, false, null);
            output.appendMath("]");
        }
        else if (obj instanceof Character)
        {
            output.appendText("'" + obj + "'");
        }
        else if (obj instanceof CooledGame)
        {
            CooledGame g = (CooledGame) obj;
            output.appendMath("(");
            output.appendOutput(createOutput(g.getG()));
            output.appendMath(")");
            if (g.getT() == null)
            {
                output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUBSCRIPT), "Fr");
            }
            else
            {
                output.appendOutput(EnumSet.of(LOCATION_SUBSCRIPT), rationalToOutput(g.getT()));
            }
        }*/
        else if (obj instanceof CgsuiteList)
        {
            CgsuiteList list = (CgsuiteList) obj;
            output.appendMath("[");
            for (int i = 1; i <= list.size(); i++)
            {
                output.appendOutput(createOutput(list.get(i)));
                if (i < list.size())
                {
                    output.appendMath(",");
                }
            }
            output.appendMath("]");
        }
        else if (obj instanceof CgsuiteMap)
        {
            CgsuiteMap map = (CgsuiteMap) obj;
            output.appendMath("{");
            if (map.isEmpty())
                output.appendSymbol(RIGHT_ARROW);
            for (Iterator<Entry<CgsuiteObject,CgsuiteObject>> it = map.entrySet().iterator(); it.hasNext();)
            {
                Entry<CgsuiteObject,CgsuiteObject> e = it.next();
                output.appendOutput(createOutput(e.getKey()));
                output.appendMath(" ");
                output.appendSymbol(RIGHT_ARROW);
                output.appendMath(" ");
                output.appendOutput(createOutput(e.getValue()));
                if (it.hasNext())
                {
                    output.appendMath(",");
                }
            }
            output.appendMath("}");
        }
        else if (obj instanceof CgsuiteSet)
        {
            CgsuiteSet set = (CgsuiteSet) obj;
            output.appendMath("{");
            for (Iterator<CgsuiteObject> it = set.sortedIterator(); it.hasNext();)
            {
                output.appendOutput(createOutput(it.next()));
                if (it.hasNext())
                {
                    output.appendMath(",");
                }
            }
            output.appendMath("}");
        }
        else if (obj instanceof ExplicitGame)
        {
            ExplicitGame g = (ExplicitGame) obj;
            output.appendMath("{");
            for (Iterator<CgsuiteObject> i = g.getLeftOptions().iterator(); i.hasNext();)
            {
                output.appendOutput(createOutput(i.next()));
                if (i.hasNext())
                {
                    output.appendMath(",");
                }
            }
            output.appendMath("|");
            for (Iterator<CgsuiteObject> i = g.getRightOptions().iterator(); i.hasNext();)
            {
                output.appendOutput(createOutput(i.next()));
                if (i.hasNext())
                {
                    output.appendMath(",");
                }
            }
            output.appendMath("}");
        }/*
        else if (obj instanceof Genus)
        {
            Genus g = (Genus) obj;
            output.appendMath(String.valueOf(g.gBase()));
            if (g.supLength() > 0) {
                output.appendText(Output.Mode.PLAIN_TEXT, "^");
                for (int i=0; i<g.supLength(); ++i)
                {
                    output.appendText(EnumSet.of(FACE_MATH, LOCATION_SUPERSCRIPT),
                                      new CodeDigit(g.supDigit(i),
                                                    CodeDigit.base36Options).toString());
                }
            }
        }
        else if (obj instanceof GrundySequence)
        {
            GrundySequence s = (GrundySequence) obj;
            output.appendText("[GrundySequence(" + s.getRules() + ") with " + s.size() + " cached values]");
        }
        else if (obj instanceof HeatedGame)
        {
            HeatedGame g = (HeatedGame) obj;
            output.appendSymbol(INTEGRAL);
            output.appendOutput(EnumSet.of(LOCATION_UPPER_LIMIT), createOutput(g.getT()));
            if (g.getS() != null)
            {
                output.appendText(" ");
                output.appendOutput(EnumSet.of(LOCATION_LOWER_LIMIT), createOutput(g.getS()));
            }
            output.appendText(" ");
            output.appendOutput(createOutput(g.getG()));
        }*/
        else if (obj instanceof InverseGame)
        {
            output.appendMath("-");
            output.appendOutput(createOutput(((InverseGame) obj).getG()));
        }/*
        else if (obj instanceof Kernel.Operation)
        {
            Kernel.Operation op = (Kernel.Operation) obj;
            output.appendText("Operation \"" + op.name + "\" (" + op.methods.size() + " method" +
                (op.methods.size() == 1 ? "" : "s") + ")");
        }
        else if (obj instanceof Kernel.Procedure)
        {
            Kernel.Procedure procedure = (Kernel.Procedure) obj;

            String[] argNames = procedure.getArgNames();
            if (argNames != null)
            {
                // Display the procedure header.
                output.appendText("proc(");
                for (int i = 0; i < argNames.length; i++)
                {
                    output.appendText(argNames[i]);
                    if (i < argNames.length-1)
                    {
                        output.appendText(",");
                    }
                }
                output.appendText(")");
                String[] locals = procedure.getLocals();
                if (locals != null)
                {
                    output.appendText(" local ");
                    for (int i = 0; i < locals.length; i++)
                    {
                        output.appendText(locals[i]);
                        if (i < locals.length-1)
                        {
                            output.appendText(",");
                        }
                    }
                    output.appendText(";");
                }
            }
            
            // Display the explicit values.
            if (procedure.hasRememberTable())
            {
                if (argNames != null)
                {
                    output.appendText(" ");
                }
                Map explicitValues = procedure.getAllExplicitValues();
                if (explicitValues.size() > 0 || argNames == null)
                {
                    output.appendMath("[ ");
                    for (Iterator i = explicitValues.entrySet().iterator(); i.hasNext();)
                    {
                        if (procedure.getNArgs() != 1)
                        {
                            output.appendMath("(");
                        }
                        Map.Entry e = (Map.Entry) i.next();
                        AssignableList al = (AssignableList) e.getKey();
                        for (Iterator j = al.iterator(); j.hasNext();)
                        {
                            output.appendOutput(createOutput(j.next()));
                            if (j.hasNext())
                            {
                                output.appendMath(",");
                            }
                        }
                        if (procedure.getNArgs() != 1)
                        {
                            output.appendMath(")");
                        }
                        output.appendMath(" ");
                        output.appendSymbol(RIGHT_ARROW);
                        output.appendMath(" ");
                        output.appendOutput(createOutput(e.getValue()));
                        if (i.hasNext())
                        {
                            output.appendMath(" , ");
                        }
                    }
                    output.appendMath(" ]");
                }
                if (procedure.getAST() != null)
                {
                    output.appendText(" (" + procedure.getRememberTableSize() + (explicitValues.size() > 0 ? " additional" : "")
                        + " value" + (procedure.getRememberTableSize() == 1 ? "" : "s") + " cached)");
                }
            }
        }
        else if (obj instanceof Kernel.Type)
        {
            output.appendText("Type \"" + ((Kernel.Type) obj).typeInfo.name + "\"");
        }
        */
        else if (obj instanceof LoopyGame)
        {
            loopyGameToOutput((LoopyGame) obj, output, true);
        }
        /*
        else if (obj instanceof OrdinalSumGame)
        {
            OrdinalSumGame g = (OrdinalSumGame) obj;
            output.appendOutput(createOutput(g.getG()));
            output.appendMath(":");
            output.appendOutput(createOutput(g.getH()));
        }
        else if (obj instanceof ProductGame)
        {
            ProductGame g = (ProductGame) obj;
            output.appendOutput(createOutput(g.getG()));
            output.appendMath(".");
            output.appendOutput(createOutput(g.getU()));
        }*/
        else if (obj instanceof RationalNumber)
        {
            output = rationalToOutput((RationalNumber) obj);
        }/*
        else if (obj instanceof StopperSidedGame)
        {
            loopyGameToOutput(((StopperSidedGame) obj).getOnside(), output, true);
            output.appendMath(" & ");
            loopyGameToOutput(((StopperSidedGame) obj).getOffside(), output, true);
        }*/
        else if (obj instanceof CgsuiteString)
        {
            output.appendText(obj.toString());
        }
        else if (obj instanceof SumGame)
        {
            CgsuiteList components = ((SumGame) obj).getComponents();
            boolean gotAny = false;
            for (Iterator<? extends CgsuiteObject> i = components.iterator(); i.hasNext();)
            {
                if (gotAny)
                {
                    output.appendMath(" + ");
                }
                output.appendOutput(createOutput(i.next()));
                gotAny = true;
            }
            if (! gotAny)
            {
                output.appendMath("0");
            }
        }/*
        else if (obj instanceof Table)
        {
            return ((Table) obj).getOutput(context);
        }
        else if (obj instanceof Thermograph)
        {
            Thermograph t = (Thermograph) obj;
            output.appendMath("Thermograph(");
            output.appendOutput(trajectoryToOutput(t.getLeftWall()));
            output.appendMath(",");
            output.appendOutput(trajectoryToOutput(t.getRightWall()));
            output.appendMath(")");
        }
        else if (obj instanceof Trajectory)
        {
            output.appendMath("Trajectory(");
            output.appendOutput(trajectoryToOutput((Trajectory) obj));
            output.appendMath(")");
        }
        else if (obj instanceof UnsimplifiedGame)
        {
            output.appendSymbol(LANGLE);
            output.appendOutput(createOutput(((UnsimplifiedGame) obj).getG()));
            output.appendSymbol(RANGLE);
        }*/
        else
        {
            //output.appendText(obj.toCgsuiteString().toJavaString());
        }
        return output;
    }
    
    private class GameIntegerPair
    {
        CanonicalShortGame g;
        int n;
        
        GameIntegerPair(CanonicalShortGame g, int n)
        {
            this.g = g;
            this.n = n;
        }
    }
}
