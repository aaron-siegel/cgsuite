/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.math.BigInteger;
import java.util.Random;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class CgsuiteInteger extends Game
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Integer");

    public final static CgsuiteInteger ZERO = new CgsuiteInteger(0);
    public final static CgsuiteInteger ONE = new CgsuiteInteger(1);
    
    private static Random random = new Random();

    private int value;
    private BigInteger bigValue;

    public CgsuiteInteger(long value)
    {
        super(TYPE);
        
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE)
            this.value = (int) value;
        else
            this.bigValue = BigInteger.valueOf(value);
    }
    
    public CgsuiteInteger(BigInteger bigValue)
    {
        super(TYPE);
        
        if (bigValue.bitLength() <= 31)
            this.value = bigValue.intValue();
        else
            this.bigValue = bigValue;
    }
    
    public CgsuiteInteger negate()
    {
        if (bigValue == null)
            return new CgsuiteInteger(-(long) value);
        else
            return new CgsuiteInteger(bigValue.negate());
    }

    public CgsuiteInteger add(CgsuiteInteger other)
    {
        if (bigValue == null && other.bigValue == null)
            return new CgsuiteInteger((long) value + (long) other.value);
        else
            return new CgsuiteInteger(bigValue().add(other.bigValue()));
    }

    public CgsuiteInteger subtract(CgsuiteInteger other)
    {
        if (bigValue == null && other.bigValue == null)
            return new CgsuiteInteger((long) value - (long) other.value);
        else
            return new CgsuiteInteger(bigValue().subtract(other.bigValue()));
    }

    public CgsuiteInteger multiply(CgsuiteInteger other)
    {
        if (bigValue == null && other.bigValue == null)
            return new CgsuiteInteger((long) value * (long) other.value);
        else
            return new CgsuiteInteger(bigValue().multiply(other.bigValue()));
    }

    public CgsuiteInteger nimSum(CgsuiteInteger other)
    {
        if (bigValue == null && other.bigValue == null)
            return new CgsuiteInteger((long) value ^ (long) other.value);
        else
            return new CgsuiteInteger(bigValue().xor(other.bigValue()));
    }
    
    public boolean isSmall()
    {
        return bigValue == null;
    }
    
    public static CgsuiteObject random(CgsuiteInteger max)
    {
        // TODO Implement if max is a big value?
        return new CgsuiteInteger(1 + random.nextInt(max.intValue()));
    }
    
    public static void setSeed(CgsuiteInteger seed) 
    {
        random.setSeed(seed.value);
    }

    public int intValue()
    {
        if (bigValue != null)
            throw new InputException("Overflow.");
        return value;
    }
    
    public BigInteger bigValue()
    {
        return (bigValue == null)? BigInteger.valueOf(value) : bigValue;
    }

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        if (bigValue == null)
            output.appendMath(String.valueOf(value));
        else
            output.appendMath(bigValue.toString());
        return output;
    }

    @Override
    protected int compareLike(CgsuiteObject obj)
    {
        CgsuiteInteger other = (CgsuiteInteger) obj;
        if (bigValue == null && other.bigValue == null)
            return value - other.value;
        else
            return bigValue().compareTo(other.bigValue());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CgsuiteInteger other = (CgsuiteInteger) obj;
        if (this.value != other.value) {
            return false;
        }
        if (this.bigValue != other.bigValue && (this.bigValue == null || !this.bigValue.equals(other.bigValue))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 79 * hash + this.value;
        hash = 79 * hash + (this.bigValue != null ? this.bigValue.hashCode() : 0);
        return hash;
    }

}
