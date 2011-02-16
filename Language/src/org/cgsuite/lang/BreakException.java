package org.cgsuite.lang;

import org.cgsuite.CgsuiteException;

public class BreakException extends CgsuiteException
{
    private static final long serialVersionUID = 1L;

    boolean isContinue;

    public boolean isContinue()
    {
        return isContinue;
    }

    public BreakException(boolean isContinue)
    {
        this.isContinue = isContinue;
    }
}
