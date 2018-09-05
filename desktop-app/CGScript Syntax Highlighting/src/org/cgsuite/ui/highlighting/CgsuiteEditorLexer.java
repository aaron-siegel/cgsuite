/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import java.util.HashMap;
import java.util.Map;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.cgsuite.lang.parser.CgsuiteLexer;
import static org.cgsuite.lang.parser.CgsuiteLexer.*;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.cgsuite.lang.parser.CgsuiteTreeAdaptor;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author asiegel
 */
public class CgsuiteEditorLexer implements Lexer<CgsuiteTokenId>
{
    private LexerRestartInfo<CgsuiteTokenId> info;
    private LexerInput input;
    private CgsuiteLexer lexer;
    private Map<Integer,CommonToken> tokenTypes = new HashMap<Integer,CommonToken>();

    public CgsuiteEditorLexer(LexerRestartInfo<CgsuiteTokenId> info)
    {
        this.info = info;
        this.input = info.input();
    }
    
    @Override
    public org.netbeans.api.lexer.Token<CgsuiteTokenId> nextToken()
    {
        if (lexer == null)
        {
            instantiateLexer();
        }
        
        CommonToken token = (CommonToken) lexer.nextToken();
        
        Token<CgsuiteTokenId> createdToken = null;

        if (token.getType() != -1) {
            CommonToken resolvedToken = tokenTypes.get(token.getStartIndex());
            int resolvedType = resolvedToken == null ? token.getType() : resolvedToken.getType();
            CgsuiteTokenId tokenId = CgsuiteLanguageHierarchy.getToken(resolvedType);
            createdToken = info.tokenFactory().createToken(tokenId);
        } else if (info.input().readLength() > 0) {
            CgsuiteTokenId tokenId  = CgsuiteLanguageHierarchy.getToken(CgsuiteLexer.IDENTIFIER);
            createdToken = info.tokenFactory().createToken(tokenId);
        }

        return createdToken;
    }
    
    private void instantiateLexer()
    {
        AntlrCharStream charStream = new AntlrCharStream(input, "CgsuiteEditor", false);
        lexer = new CgsuiteLexer(charStream);
        
        // We do an initial parse of the char stream, storing parse info for future use.
        // This enables us to use parser context to spoof certain tokens with other (context-
        // aware) tokens, e.g., IDENTIFIER vs. DECL_ID.
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CgsuiteParser parser = new CgsuiteParser(tokens);
        parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
        try
        {
            CommonTree tree = parser.compilationUnit().getTree();
            addAllTokens(tree);
        }
        catch (Exception exc)
        {
        }
        lexer.reset();
    }

    private void addAllTokens(CommonTree tree)
    {
        CommonToken token = (CommonToken) tree.getToken();
        
        switch (token.getType())
        {
            case CLASS_VAR:
            case DECL_BEGIN:
            case DECL_END:
            case DECL_ID:
            case INFIX_OP:
                tokenTypes.put(token.getStartIndex(), token);
                break;
            default:
                break;
        }

        if (tree.getChildren() != null)
        {
            for (Object child : tree.getChildren())
            {
                addAllTokens((CommonTree) child);
            }
        }
    }

    @Override
    public Object state()
    {
        return null;
    }

    @Override
    public void release()
    {
    }

}
