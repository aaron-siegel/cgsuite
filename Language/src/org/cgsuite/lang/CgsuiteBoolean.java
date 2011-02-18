package org.cgsuite.lang;

public class CgsuiteBoolean extends CgsuiteObject
{
    public final static CgsuiteBoolean TRUE = new CgsuiteBoolean(true);
    public final static CgsuiteBoolean FALSE = new CgsuiteBoolean(false);

    private boolean booleanValue;

    public CgsuiteBoolean(String literal)
    {
        super(CgsuitePackage.forceLookupClass("Boolean"));

        this.booleanValue = "true".equals(literal);
    }

    public CgsuiteBoolean(boolean booleanValue)
    {
        super(CgsuitePackage.forceLookupClass("Boolean"));

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
    public String toString()
    {
        return String.valueOf(booleanValue);
    }
}
