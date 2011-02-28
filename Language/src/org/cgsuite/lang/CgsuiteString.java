package org.cgsuite.lang;

import org.cgsuite.lang.output.StyledTextOutput;

public class CgsuiteString extends CgsuiteObject
{
    private String str;

    public CgsuiteString(String str)
    {
        super(CgsuitePackage.forceLookupClass("String"));

        this.str = str;
    }

    @Override
    public String toString()
    {
        return "\"" + str + "\"";
    }

    @Override
    public StyledTextOutput toOutput()
    {
        return new StyledTextOutput(toString());
    }
    
    public String toJavaString()
    {
        return str;
    }

    public CgsuiteString append(CgsuiteString other)
    {
        return new CgsuiteString(str + other.str);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((str == null) ? 0 : str.hashCode());
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
        CgsuiteString other = (CgsuiteString) obj;
        if (str == null)
        {
            if (other.str != null)
                return false;
        } else if (!str.equals(other.str))
            return false;
        return true;
    }
}
