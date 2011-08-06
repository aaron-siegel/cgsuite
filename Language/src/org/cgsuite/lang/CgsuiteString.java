package org.cgsuite.lang;

import org.cgsuite.lang.output.StyledTextOutput;

public class CgsuiteString extends CgsuiteObject
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("String");
    
    private String str;

    public CgsuiteString(String str)
    {
        super(TYPE);

        this.str = str;
    }

    @Override
    public StyledTextOutput toOutput()
    {
        return new StyledTextOutput("\"" + str + "\"");
    }
    
    public String toJavaString()
    {
        return str;
    }

    public CgsuiteString append(CgsuiteString other)
    {
        return new CgsuiteString(str + other.str);
    }
    
    public int length() 
    {
        return str.length();
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

    @Override
    protected int compareLike(CgsuiteObject other)
    {
        return str.compareTo(((CgsuiteString) other).str);
    }
}
