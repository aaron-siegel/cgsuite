package org.cgsuite.lang.game;

import java.math.BigInteger;
import java.util.EnumSet;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.InputException;
import org.cgsuite.lang.output.StyledTextOutput;

public class RationalNumber extends CgsuiteObject implements Comparable<RationalNumber>
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Number");

    public final static RationalNumber ZERO = new RationalNumber(0, 1);
    public final static RationalNumber ONE = new RationalNumber(1, 1);
    public final static RationalNumber TWO = new RationalNumber(2, 1);
    public final static RationalNumber NEGATIVE_ONE = new RationalNumber(-1, 1);
    public final static RationalNumber POSITIVE_INFINITY = new RationalNumber(1, 0);
    public final static RationalNumber NEGATIVE_INFINITY = new RationalNumber(-1, 0);

    private BigInteger numerator;
    private BigInteger denominator;

    public RationalNumber(CgsuiteInteger integer)
    {
        this(integer.bigValue(), BigInteger.ONE);
    }
    
    public RationalNumber(CgsuiteInteger numerator, CgsuiteInteger denominator)
    {
        this(numerator.bigValue(), denominator.bigValue());
    }

    public RationalNumber(BigInteger numerator, BigInteger denominator)
    {
        super(TYPE);
        BigInteger gcd = numerator.gcd(denominator);
        if (denominator.compareTo(BigInteger.ZERO) < 0)
        {
            gcd = gcd.negate();
        }
        this.numerator = numerator.divide(gcd);
        this.denominator = denominator.divide(gcd);
    }

    public RationalNumber(long numerator, long denominator)
    {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }
    
    public RationalNumber(CanonicalShortGame number)
    {
        super(TYPE);
        if (!number.isNumber())
            throw new IllegalArgumentException("Not a number.");
        
        RationalNumber n = number.getNumberPart();
        numerator = n.numerator;
        denominator = n.denominator;
    }
    
    @Override
    public int compareTo(RationalNumber r)
    {
        if (isInfinite() && r.isInfinite())
        {
            // Only special case: Both are infinite
            return numerator.compareTo(r.numerator);
        }

        return numerator.multiply(r.denominator).compareTo(denominator.multiply(r.numerator));
    }

    @Override
    protected int compareLike(CgsuiteObject other)
    {
        return compareTo((RationalNumber) other);
    }

    @Override
    public CgsuiteObject simplify()
    {
        if (isInteger())
            return new CgsuiteInteger(numerator);
        else if (isDyadic())
            return CanonicalShortGame.construct(this);
        else
            return this;
    }

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();

        if (isInfinite())
        {
            if (compareTo(ZERO) < 0)
                output.appendMath("-");

            output.appendSymbol(StyledTextOutput.Symbol.INFINITY);
        }
        else if (isInteger())
        {
            output.appendMath(String.valueOf(getNumerator()));
        }
        else
        {
            if (compareTo(ZERO) < 0)
            {
                output.appendMath("-");
            }
            output.appendText(
                EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_NUMERATOR),
                String.valueOf(getNumerator().abs())
                );
            output.appendMath("/");
            output.appendText(
                EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_DENOMINATOR),
                String.valueOf(getDenominator())
                );
        }
        return output;
    }

    public double doubleValue()
    {
        BigInteger[] div = numerator.divideAndRemainder(denominator);
        return div[0].doubleValue() + div[1].doubleValue() / denominator.doubleValue();
    }

    public float floatValue()
    {
        BigInteger[] div = numerator.divideAndRemainder(denominator);
        return div[0].floatValue() + div[1].floatValue() / denominator.floatValue();
    }

    public int intValue()
    {
        return numerator.divide(denominator).intValue();
    }

    public long longValue()
    {
        return numerator.divide(denominator).longValue();
    }

    public BigInteger getNumerator()
    {
        return numerator;
    }

    public BigInteger getDenominator()
    {
        return denominator;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((denominator == null) ? 0 : denominator.hashCode());
        result = prime * result + ((numerator == null) ? 0 : numerator.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RationalNumber other = (RationalNumber) obj;
        if (denominator == null)
        {
            if (other.denominator != null)
                return false;
        } else if (!denominator.equals(other.denominator))
            return false;
        if (numerator == null)
        {
            if (other.numerator != null)
                return false;
        } else if (!numerator.equals(other.numerator))
            return false;
        return true;
    }

    public RationalNumber negate()
    {
        return new RationalNumber(numerator.negate(), denominator);
    }
    
    public RationalNumber add(RationalNumber r)
    {
        return new RationalNumber(
            numerator.multiply(r.denominator).add(r.numerator.multiply(denominator)),
            denominator.multiply(r.denominator)
            );
    }
    
    public RationalNumber subtract(RationalNumber r)
    {
        return new RationalNumber(
            numerator.multiply(r.denominator).subtract(r.numerator.multiply(denominator)),
            denominator.multiply(r.denominator)
            );
    }

    public RationalNumber multiply(RationalNumber r)
    {
        return new RationalNumber(
            numerator.multiply(r.numerator),
            denominator.multiply(r.denominator)
            );
    }

    public RationalNumber divide(RationalNumber r)
    {
        return new RationalNumber(
            numerator.multiply(r.denominator),
            denominator.multiply(r.numerator)
            );
    }

    public RationalNumber pow(CgsuiteInteger r)
    {
        if (r.compareTo(CgsuiteInteger.ZERO) >= 0)
        {
            return new RationalNumber(
                numerator.pow(r.intValue()),
                denominator.pow(r.intValue())
                );
        }
        else
        {
            r = r.negate();
            return new RationalNumber(
                denominator.pow(r.intValue()),
                numerator.pow(r.intValue())
                );
        }
    }

    public RationalNumber reciprocal()
    {
        return new RationalNumber(denominator, numerator);
    }

    public RationalNumber abs()
    {
        return new RationalNumber(numerator.abs(), denominator);
    }

    public RationalNumber min(RationalNumber r)
    {
        return compareTo(r) <= 0 ? this : r;
    }

    public RationalNumber max(RationalNumber r)
    {
        return compareTo(r) >= 0 ? this : r;
    }

    public RationalNumber mean(RationalNumber r)
    {
        return add(r).divide(TWO);
    }

    public RationalNumber mod(RationalNumber r)
    {
        if (r.compareTo(ZERO) <= 0)
            throw new InputException("The modulus is not positive.");
            
        return new RationalNumber(
            numerator.multiply(r.denominator).mod(r.numerator.multiply(denominator)),
            denominator.multiply(r.denominator)
            );
    }

    public RationalNumber floor()
    {
        if (isInteger() || isInfinite())
        {
            return this;
        }
        else if (numerator.compareTo(BigInteger.ZERO) >= 0)
        {
            return new RationalNumber(numerator.divide(denominator), BigInteger.ONE);
        }
        else
        {
            return new RationalNumber(numerator.divide(denominator).subtract(BigInteger.ONE), BigInteger.ONE);
        }
    }

    public RationalNumber ceiling()
    {
        if (isInteger() || isInfinite())
        {
            return this;
        }
        else if (numerator.compareTo(BigInteger.ZERO) >= 0)
        {
            return new RationalNumber(numerator.divide(denominator).add(BigInteger.ONE), BigInteger.ONE);
        }
        else
        {
            return new RationalNumber(numerator.divide(denominator), BigInteger.ONE);
        }
    }

    public CanonicalShortGame canonicalForm()
    {
        return CanonicalShortGame.construct(this);
    }

    public boolean leq(RationalNumber other)
    {
        return compareTo(other) <= 0;
    }

    /**
     * Returns <code>true</code> if this rational is infinite.
     *
     * @return  <code>true</code> if this rational is either
     * {@link #POSITIVE_INFINITY} or {@link #NEGATIVE_INFINITY}.
     */
    public boolean isInfinite()
    {
        return denominator.equals(BigInteger.ZERO);
    }

    /**
     * Returns <code>true</code> if this rational's denominator is a power of
     * two.
     *
     * @return  <code>true</code> if this is a dyadic rational.
     */
    public boolean isDyadic()
    {
        return denominator.bitCount() == 1;
    }

    /**
     * Returns <code>true</code> if this rational's numerator and denominator
     * can be represented as 32-bit integers.
     *
     * @return  <code>true</code> if this is a small rational.
     */
    public boolean isSmall()
    {
        return numerator.bitLength() <= 31 && denominator.bitLength() <= 31;
    }

    /**
     * Returns <code>true</code> if this rational is an integer.
     *
     * @return  <code>true</code> if this is an integer.
     */
    public boolean isInteger()
    {
        return denominator.equals(BigInteger.ONE);
    }
    
    public boolean isSmallInteger()
    {
        return isInteger() && numerator.bitLength() <= 31;
    }

    /**
     * Returns the largest <code>n</code> for which <code>2<sup>n</sup></code>
     * divides the denominator of this rational.  If this is a dyadic rational,
     * then the return value is equal to the denominator's exponent.  If this
     * is infinite, then the return value is <code>-1</code>.
     *
     * @return  The largest <code>n</code> for which <code>2<sup>n</sup></code>
     *          divides the denominator of this rational, or <code>-1</code> if
     *          this rational is infinite.
     */
    public int getDenominatorExponent()
    {
        return denominator.getLowestSetBit();
    }

    /**
     * Parses a string as a <code>Rational</code>.  Whitespace is trimmed from
     * the beginning and end of the string.  The remainder must consist
     * entirely of decimal digits, except that the first character may be an
     * ASCII minus sign '-', and there may be at most one slash '/'.  The
     * special values <code>Infinity</code> and <code>-Infinity</code> are
     * recognized, along with abbreviations <code>Inf</code> and
     * <code>I</code>, all case-insensitive.
     *
     * @param   s The string to parse.
     * @return  The <code>Rational</code> represented by <code>s</code>.
     * @throws  NumberFormatException <code>s</code> does not represent a
     *          valid <code>Rational</code>.
     */
    public static RationalNumber parseRationalNumber(String s)
    {
        s = s.trim();
        if (s.equalsIgnoreCase("Infinity") || s.equalsIgnoreCase("Inf") || s.equalsIgnoreCase("I"))
        {
            return POSITIVE_INFINITY;
        }
        else if (s.equalsIgnoreCase("-Infinity") || s.equalsIgnoreCase("-Inf") || s.equalsIgnoreCase("-I"))
        {
            return NEGATIVE_INFINITY;
        }
        String[] parts = s.split("/");
        try
        {
            if (parts.length == 1)
            {
                return new RationalNumber(new BigInteger(parts[0]), BigInteger.ONE);
            }
            else if (parts.length == 2)
            {
                return new RationalNumber(new BigInteger(parts[0]), new BigInteger(parts[1]));
            }
            else
            {
                throw new NumberFormatException();
            }
        }
        catch (ArithmeticException exc)
        {
            throw new NumberFormatException();
        }
    }
}