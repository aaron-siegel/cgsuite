package org.cgsuite.lang;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

public class CgsuiteTree extends CommonTree
{
    private final static List<CgsuiteTree> EMPTY_LIST = Collections.emptyList();

    private boolean checkedConst;
    private CgsuiteObject constValue;

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

    public CgsuiteObject getConstValue()
    {
        return constValue;
    }

    public void setConstValue(CgsuiteObject constValue)
    {
        this.constValue = constValue;
    }

    public boolean checkedConst()
    {
        return checkedConst;
    }

    public void setCheckedConst(boolean checkedConst)
    {
        this.checkedConst = checkedConst;
    }
}
