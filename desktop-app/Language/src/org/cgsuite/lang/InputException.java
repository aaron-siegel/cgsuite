package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.Token;

public class InputException extends CgsuiteException
{
    private static final long serialVersionUID = 1L;

    private String invocationTarget;
    private List<Token> tokenStack;

    public InputException(String message)
    {
        super(message);
        tokenStack = new ArrayList<Token>();
    }

    public InputException(String message, Throwable e)
    {
        super(message, e);
        tokenStack = new ArrayList<Token>();
    }

    public InputException(Token token, String message)
    {
        this(message);
        addToken(token);
    }

    public List<Token> getTokenStack()
    {
        return tokenStack;
    }

    public void addToken(Token token)
    {
        if (token == null)
            throw new IllegalArgumentException("token == null");
        
        tokenStack.add(token);
    }
    
    public String getInvocationTarget()
    {
        return invocationTarget;
    }
    
    public void setInvocationTarget(String invocationTarget)
    {
        this.invocationTarget = invocationTarget;
    }
}
