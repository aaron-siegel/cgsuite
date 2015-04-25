package org.cgsuite.lang;

import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

public class CgsuiteBoolean extends CgsuiteObject
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Boolean");
    
    public final static CgsuiteBoolean TRUE = new CgsuiteBoolean(true);
    public final static CgsuiteBoolean FALSE = new CgsuiteBoolean(false);

    private boolean booleanValue;

    public CgsuiteBoolean(String literal)
    {
        super(TYPE);

        this.booleanValue = "true".equals(literal);
    }

    public CgsuiteBoolean(boolean booleanValue)
    {
        super(TYPE);

        this.booleanValue = booleanValue;
    }

    public boolean booleanValue()
    {
        return booleanValue;
    }

    public CgsuiteBoolean not()
    {
        return booleanValue ? FALSE : TRUE;
    }

    public static CgsuiteBoolean valueOf(boolean b)
    {
        return b ? TRUE : FALSE;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (booleanValue ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CgsuiteBoolean other = (CgsuiteBoolean) obj;
        if (booleanValue != other.booleanValue)
            return false;
        return true;
    }

    @Override
    public Output toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendText(String.valueOf(booleanValue));
        return output;
    }

    @Override
    protected int compareLike(CgsuiteObject other)
    {
        return booleanValue ? (((CgsuiteBoolean) other).booleanValue ? 0 : 1) : (((CgsuiteBoolean) other).booleanValue ? -1 : 0);
    }
}
