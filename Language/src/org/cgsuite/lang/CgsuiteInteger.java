/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import org.cgsuite.lang.game.RationalNumber;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class CgsuiteInteger extends Game implements Comparable<CgsuiteInteger>
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Integer");

    public final static CgsuiteInteger ZERO = new CgsuiteInteger(0);
    public final static CgsuiteInteger ONE = new CgsuiteInteger(1);

    private int value;

    public CgsuiteInteger(int value)
    {
        super(TYPE);
        
        this.value = value;
    }
    
    public CgsuiteInteger negate()
    {
        return new CgsuiteInteger(-value);
    }

    public CgsuiteObject add(CgsuiteInteger other)
    {
        long sum = (long) value + (long) other.value;
        
        if (sum >= Integer.MIN_VALUE && sum <= Integer.MAX_VALUE)
            return new CgsuiteInteger((int) sum);
        else
            return new RationalNumber(sum, 1L);
    }

    public CgsuiteObject subtract(CgsuiteInteger other)
    {
        long diff = (long) value - (long) other.value;

        if (diff >= Integer.MIN_VALUE && diff <= Integer.MAX_VALUE)
            return new CgsuiteInteger((int) diff);
        else
            return new RationalNumber(diff, 1L);
    }

    public CgsuiteObject multiply(CgsuiteInteger other)
    {
        long product = (long) value * (long) other.value;

        if (product >= Integer.MIN_VALUE && product <= Integer.MAX_VALUE)
            return new CgsuiteInteger((int) product);
        else
            return new RationalNumber(product, 1L);
    }

    public int intValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath(toString());
        return output;
    }

    @Override
    public int compareTo(CgsuiteInteger other)
    {
        return value - other.value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CgsuiteInteger other = (CgsuiteInteger) obj;
        if (this.value != other.value)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + this.value;
        return hash;
    }
}
