package org.cgsuite.lang.parser;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

public class CgsuiteTree extends CommonTree
{
    private final static List<CgsuiteTree> EMPTY_LIST = Collections.emptyList();

    public CgsuiteTree()
    {
        super();
    }

    public CgsuiteTree(Token token)
    {
        super(token);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CgsuiteTree> getChildren()
    {
        List<CgsuiteTree> list = super.getChildren();
        return (list == null)? EMPTY_LIST : list;
    }

    @Override
    public CgsuiteTree getChild(int n)
    {
        return (CgsuiteTree) super.getChild(n);
    }

    public String location()
    {
        if (token.getInputStream() == null)
            throw new RuntimeException(toStringTree());
        return token.getInputStream().getSourceName() + ":" + token.getLine() + ":" + token.getCharPositionInLine();
    }
}
