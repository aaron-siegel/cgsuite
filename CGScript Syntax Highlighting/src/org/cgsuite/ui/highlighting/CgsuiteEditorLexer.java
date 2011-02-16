/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import org.cgsuite.lang.CgsuiteLexer;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author asiegel
 */
public class CgsuiteEditorLexer implements Lexer<CgsuiteTokenId>
{
    private LexerRestartInfo<CgsuiteTokenId> info;
    private CgsuiteLexer lexer;

    public CgsuiteEditorLexer(LexerRestartInfo<CgsuiteTokenId> info)
    {
        this.info = info;
        AntlrCharStream charStream = new AntlrCharStream(info.input(), "CgsuiteEditor", false);
        lexer = new CgsuiteLexer(charStream);
    }

    @Override
    public org.netbeans.api.lexer.Token<CgsuiteTokenId> nextToken()
    {
        org.antlr.runtime.Token token = lexer.nextToken();

        Token<CgsuiteTokenId> createdToken = null;

        if (token.getType() != -1){
            CgsuiteTokenId tokenId  = CgsuiteLanguageHierarchy.getToken(token.getType());
            createdToken = info.tokenFactory().createToken(tokenId);
        }  else if(info.input().readLength() > 0){
            CgsuiteTokenId tokenId  = CgsuiteLanguageHierarchy.getToken(CgsuiteLexer.IDENTIFIER);
            createdToken = info.tokenFactory().createToken(tokenId);
        }

        return createdToken;
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
