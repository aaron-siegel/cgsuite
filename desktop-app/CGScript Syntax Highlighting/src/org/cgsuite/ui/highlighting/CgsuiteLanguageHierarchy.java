/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.cgsuite.lang.parser.CgsuiteLexer.*;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;
/**
 *
 * @author asiegel
 */
public class CgsuiteLanguageHierarchy extends LanguageHierarchy<CgsuiteTokenId>
{
    private final static Map<Integer, CgsuiteTokenId> ID_TO_TOKEN;

    static
    {
        HashMap<String, int[]> tokenTypes = new HashMap<String, int[]>();
        
        tokenTypes.put("Keyword", new int[]
        {
            AND, AS, BEGIN, BREAK, BY, CLASS, CLEAR, CONTINUE, DEF, DO, ELSE, ELSEIF, END, ENUM, ERROR, EXTENDS,
            EXTERNAL, FALSE, FINALLY, FOR, FOREACH, FROM, IF, IMPORT, IN, IS, LISTOF, MUTABLE,
            NEG, NIL, NOT, OP, OR, OVERRIDE, PASS, POS, RETURN, SETOF, SINGLETON, SQUOTE, STATIC, SUMOF, SUPER, SYSTEM,
            TABLEOF, THEN, THIS, TO, TRUE, TRY, VAR, WHERE, WHILE, YIELD
        });
        tokenTypes.put("Identifier", new int[] { IDENTIFIER });
        tokenTypes.put("Number", new int[] { INTEGER });
        tokenTypes.put("String", new int[] { STRING });
        tokenTypes.put("Comment", new int[] { SL_COMMENT, ML_COMMENT });
        tokenTypes.put("Separator", new int[]
        {
            WHITESPACE, PLUS, MINUS, PLUSMINUS, AST, FSLASH, DOT, PERCENT, LPAREN, RPAREN,
            LBRACKET, RBRACKET, LBRACE, RBRACE, DQUOTE, COMMA, SEMI, COLON, AMPERSAND,
            QUESTION, CARET, MULTI_CARET, VEE, MULTI_VEE, EQUALS, NEQ, LT, GT, LEQ, GEQ,
            CONFUSED, COMPARE, RARROW, BIGRARROW, BACKSLASH, REFEQUALS, REFNEQ, ASSIGN, BAD_ASSIGN,
            DOTDOT, DOTDOTDOT, SLASHES, LCONFUSED, GCONFUSED
        });
        

        ID_TO_TOKEN = new HashMap<Integer, CgsuiteTokenId>();
        Set<Integer> observedTokens = new HashSet<Integer>();

        for (String category : tokenTypes.keySet())
        {
            for (int id : tokenTypes.get(category))
            {
                ID_TO_TOKEN.put(id, new CgsuiteTokenId(CgsuiteParser.tokenNames[id], category, id));
                observedTokens.add(id);
            }
        }
    }
    
    /**
     * Returns an actual CgsuiteTokenId from an id. This essentially allows
     * the syntax highlighter to decide the color of specific words.
     * @param id
     * @return
     */
    static synchronized CgsuiteTokenId getToken(int id)
    {
        if (!ID_TO_TOKEN.containsKey(id))
        {
            throw new IllegalArgumentException("Unexpected token: " + CgsuiteParser.tokenNames[id]);
        }
        return ID_TO_TOKEN.get(id);
    }
    
    /**
     * Initializes the tokens in use.
     *
     * @return
     */
    @Override
    protected synchronized Collection<CgsuiteTokenId> createTokenIds()
    {
        return ID_TO_TOKEN.values();
    }
    
    /**
     * Creates a lexer object for use in syntax highlighting.
     *
     * @param info
     * @return
     */
    
    @Override
    protected synchronized Lexer<CgsuiteTokenId> createLexer(LexerRestartInfo<CgsuiteTokenId> info)
    {
        return new CgsuiteEditorLexer(info);
    }
    
    /**
     * Returns the mime type of this programming language ("text/x-cgsuite"). This
     * allows NetBeans to load the appropriate editors and file loaders when
     * a file with the cm file extension is loaded.
     *
     * @return
     */
    @Override
    protected String mimeType()
    {
        return "text/x-cgscript";
    }
}
