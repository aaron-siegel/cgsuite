package org.cgsuite.lang;

public class ReturnException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    private CgsuiteObject retval;
    
    public ReturnException(CgsuiteObject retval)
    {
        this.retval = retval;
    }

    public CgsuiteObject getRetval()
    {
        return retval;
    }
}
