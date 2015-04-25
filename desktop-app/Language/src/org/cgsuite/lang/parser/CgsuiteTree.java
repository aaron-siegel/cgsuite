package org.cgsuite.lang.parser;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;

public class CgsuiteTree extends CommonTree
{
    private final static List<CgsuiteTree> EMPTY_LIST = Collections.emptyList();

    private boolean checkedConst;
    private CgsuiteObject constValue;

    private boolean checkedPackage;
    private CgsuitePackage packageValue;

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

    public CgsuitePackage getPackageValue()
    {
        return packageValue;
    }

    public void setPackageValue(CgsuitePackage packageValue)
    {
        this.packageValue = packageValue;
    }

    public boolean checkedConst()
    {
        return checkedConst;
    }

    public void setCheckedConst(boolean checkedConst)
    {
        this.checkedConst = checkedConst;
    }

    public boolean checkedPackage()
    {
        return checkedPackage;
    }

    public void setCheckedPackage(boolean checkedPackage)
    {
        this.checkedPackage = checkedPackage;
    }
}
