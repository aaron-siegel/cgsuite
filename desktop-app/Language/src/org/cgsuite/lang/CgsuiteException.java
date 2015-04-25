package org.cgsuite.lang;


public class CgsuiteException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public CgsuiteException()
    {
    }

    public CgsuiteException(String message)
    {
       super(message);
    }

    public CgsuiteException(Throwable e)
    {
        super(e);
    }

    public CgsuiteException(String message, Throwable e)
    {
        super(message, e);
    }
}
