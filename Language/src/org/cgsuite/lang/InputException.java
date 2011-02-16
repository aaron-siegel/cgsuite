package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.Token;
import org.cgsuite.CgsuiteException;

public class InputException extends CgsuiteException
{
    private static final long serialVersionUID = 1L;

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
        tokenStack.add(token);
    }

    public List<Token> getTokenStack()
    {
        return tokenStack;
    }

    public void addToken(Token token)
    {
        tokenStack.add(token);
    }
}
