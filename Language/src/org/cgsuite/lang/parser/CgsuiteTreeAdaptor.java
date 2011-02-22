package org.cgsuite.lang.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;

public class CgsuiteTreeAdaptor extends CommonTreeAdaptor
{
    @Override
    public CgsuiteTree create(Token token)
    {
        return new CgsuiteTree(token);
    }

    @Override
    public CgsuiteErrorNode errorNode(TokenStream input, Token start, Token end, RecognitionException re)
    {
        return new CgsuiteErrorNode(input, start, end, re);
    }
}
