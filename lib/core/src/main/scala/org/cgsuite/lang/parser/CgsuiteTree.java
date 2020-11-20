package org.cgsuite.lang.parser;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

public class CgsuiteTree extends CommonTree {

    private CommonTokenStream tokenStream;

    public CgsuiteTree(CommonToken token, CommonTokenStream tokenStream)
    {
        super(token);
        this.tokenStream = tokenStream;
    }

    public CommonTokenStream getTokenStream()
    {
        return tokenStream;
    }

    public CommonToken getPrecedingNonHiddenToken()
    {
        int tokenIndex = getTokenStartIndex() - 1;
        while (tokenIndex >= 0 && tokenStream.get(tokenIndex).getChannel() == CgsuiteParser.HIDDEN)
        {
            tokenIndex--;
        }
        return tokenIndex >= 0 ? (CommonToken) tokenStream.get(tokenIndex) : null;
    }

}
