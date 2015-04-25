/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.math.BigInteger;
import java.util.Random;
import org.cgsuite.lang.game.CanonicalShortGame;
import org.cgsuite.lang.game.MultipleGame;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class CgsuiteInteger extends Game implements Comparable<CgsuiteInteger>
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Integer");
    public final static CgsuiteClass ZERO_TYPE = CgsuitePackage.forceLookupClass("Zero");

    public final static CgsuiteInteger ZERO = new CgsuiteInteger(0);
    public final static CgsuiteInteger ONE = new CgsuiteInteger(1);
    public final static CgsuiteInteger NEGATIVE_ONE = new CgsuiteInteger(-1);
    
    private final static Random RANDOM = new Random();

    private int value;
    private BigInteger bigValue;

    public CgsuiteInteger(long value)
    {
        super((value == 0)? ZERO_TYPE : TYPE);
        
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE)
            this.value = (int) value;
        else
            this.bigValue = BigInteger.valueOf(value);
    }
    
    public CgsuiteInteger(BigInteger bigValue)
    {
        super(bigValue.equals(BigInteger.ZERO)? ZERO_TYPE : TYPE);
        
        if (bigValue.bitLength() <= 31)
            this.value = bigValue.intValue();
        else
            this.bigValue = bigValue;
    }
    
    @Override
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
    
    public Game multiply(Game g)
    {
        if (this.equals(ZERO))
            return ZERO;
        else if (this.equals(ONE))
            return g;
        else if (this.equals(NEGATIVE_ONE))
            return g.negate();
        
        if (g instanceof CgsuiteInteger)
            return multiply((CgsuiteInteger) g);
        else if (g instanceof CanonicalShortGame)
            return CanonicalShortGame.construct(this).nortonMultiply((CanonicalShortGame) g);
        else
            return new MultipleGame(this, g);
    }

    public CgsuiteInteger multiply(CgsuiteInteger other)
    {
        if (bigValue == null && other.bigValue == null)
            return new CgsuiteInteger((long) value * (long) other.value);
        else
            return new CgsuiteInteger(bigValue().multiply(other.bigValue()));
    }
    
    public CgsuiteInteger div(CgsuiteInteger other)
    {
        if (other.equals(ZERO))
            throw new InputException("Integer divide by zero.");
        
        if (bigValue == null && other.bigValue == null)
            return new CgsuiteInteger((long) value / (long) other.value);
        else
            return new CgsuiteInteger(bigValue().divide(other.bigValue()));
    }
    
    public CgsuiteInteger abs()
    {
        if (bigValue == null)
            return new CgsuiteInteger(Math.abs((long) value));
        else
            return new CgsuiteInteger(bigValue.abs());
    }

    public CgsuiteInteger nimSum(CgsuiteInteger other)
    {
        if (bigValue == null && other.bigValue == null)
            return new CgsuiteInteger((long) value ^ (long) other.value);
        else
            return new CgsuiteInteger(bigValue().xor(other.bigValue()));
    }
    
    public CgsuiteInteger nimProduct(CgsuiteInteger other)
    {
        return new CgsuiteInteger(nimProduct(bigValue(), other.bigValue()));
    }
    
    public static BigInteger nimProduct(BigInteger x, BigInteger y)
    {
        BigInteger val = BigInteger.ZERO;
        
        for (int m = 0; m < x.bitLength(); m++)
        {
            if (x.testBit(m))
            {
                for (int n = 0; n < y.bitLength(); n++)
                {
                    if (y.testBit(n))
                    {
                        val = val.xor(pow2NimProduct(m, n));
                    }
                }
            }
        }
        
        return val;
    }
    
    private static BigInteger pow2NimProduct(int xExp, int yExp)
    {
        BigInteger val = TWO.pow(xExp ^ yExp);
        
        int dup = xExp & yExp;
        
        for (int n = 0; n < 32-Integer.numberOfLeadingZeros(dup); n++)
        {
            int intersect = (dup & (1 << n));
            if (intersect != 0)
            {
                val = nimProduct(val, THREE.multiply(TWO.pow(intersect)).divide(TWO));
            }
        }
        
        return val;
    }
    
    private final static BigInteger TWO = BigInteger.valueOf(2);
    private final static BigInteger THREE = BigInteger.valueOf(3);
    
    public boolean isSmall()
    {
        return bigValue == null;
    }
    
    public boolean is2Power()
    {
        if (bigValue == null)
            return value >= 1 && Integer.bitCount(value) == 1;
        else
            return bigValue.compareTo(BigInteger.ONE) >= 0 && bigValue.bitCount() == 1;
    }
    
    public CgsuiteInteger lb()
    {
        if (compareTo(ZERO) <= 0)
            throw new InputException("Integer.Lb is only applicable to positive integers.");
                    
        if (bigValue == null)
            return new CgsuiteInteger(31-Integer.numberOfLeadingZeros(value));
        else
            return new CgsuiteInteger(bigValue.bitLength()-1);
    }
    
    public static CgsuiteInteger random(CgsuiteInteger max)
    {
        if (max.bigValue == null) {
            return new CgsuiteInteger(1 + RANDOM.nextInt(max.intValue()));
        }
        
        if (max.bigValue.signum() <= 0) {
            return ZERO;
        }
        
        int bits = max.bigValue.bitLength();
        while (true) {
            BigInteger result = new BigInteger(bits, RANDOM);
            if (result.compareTo(max.bigValue) < 0) {
                return new CgsuiteInteger(result);
            }
        }
        
    }
    
    public static void setSeed(CgsuiteInteger seed) 
    {
        if (seed.bigValue == null)
            RANDOM.setSeed(seed.value);
        else
            RANDOM.setSeed(seed.bigValue.hashCode());
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
    public CgsuiteSet getLeftOptions()
    {
        if (compareTo(ZERO) > 0)
            return CgsuiteSet.singleton(subtract(ONE));
        else
            return new CgsuiteSet(0);
    }
    
    @Override
    public CgsuiteSet getRightOptions()
    {
        if (compareTo(ZERO) < 0)
            return CgsuiteSet.singleton(add(ONE));
        else
            return new CgsuiteSet(0);
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
    public int compareTo(CgsuiteInteger other)
    {
        if (bigValue == null && other.bigValue == null)
            return value - other.value;
        else
            return bigValue().compareTo(other.bigValue());
    }

    @Override
    protected int compareLike(CgsuiteObject obj)
    {
        return compareTo((CgsuiteInteger) obj);
    }
    
    public static CgsuiteInteger parseInteger(String str)
    {
        if (str.length() <= 9)
            return new CgsuiteInteger(Integer.parseInt(str));
        else
            return new CgsuiteInteger(new BigInteger(str));
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
