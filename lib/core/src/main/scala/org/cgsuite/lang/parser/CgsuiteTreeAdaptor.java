package org.cgsuite.lang.parser;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeAdaptor;

public class CgsuiteTreeAdaptor extends CommonTreeAdaptor
{
    private CommonTokenStream tokenStream;

    public CgsuiteTreeAdaptor(CommonTokenStream tokenStream)
    {
        this.tokenStream = tokenStream;
    }

    @Override
    public CgsuiteTree create(Token token)
    {
        return new CgsuiteTree((CommonToken) token, tokenStream);
    }

    @Override
    public CgsuiteErrorNode errorNode(TokenStream input, Token start, Token end, RecognitionException re)
    {
        return new CgsuiteErrorNode((CommonTokenStream) input, (CommonToken) start, (CommonToken) end, re);
    }
}
