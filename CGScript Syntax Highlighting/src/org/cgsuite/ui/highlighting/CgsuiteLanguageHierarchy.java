/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import java.util.HashSet;
import java.util.Set;
import org.cgsuite.lang.parser.CgsuiteParser;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.lexer.Language;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

import static org.cgsuite.lang.parser.CgsuiteLexer.*;
/**
 *
 * @author asiegel
 */
public class CgsuiteLanguageHierarchy extends LanguageHierarchy<CgsuiteTokenId>
{
    private static HashMap<String, int[]> tokenTypes = new HashMap<String, int[]>();
    private static List<CgsuiteTokenId> tokens;
    private static Map<Integer, CgsuiteTokenId> idToToken;
    private static final Language<CgsuiteTokenId> language = new CgsuiteLanguageHierarchy().language();

    public static Language<CgsuiteTokenId> getLanguage()
    {
        return language;
    }

    /**
     * Initializes the list of tokens with IDs generated from the ANTLR
     * token file.
     */
    private static void init()
    {
        tokenTypes.put("keyword", new int[]
        {
            AND, BEGIN, BREAK, BY, CLASS, CONTINUE, DO, ELSE, ELSEIF, END, ENUM, ERROR, EXTENDS,
            FALSE, FOR, FOREACH, FROM, GET, IF, IN, JAVA, LISTOF, METHOD, NEG,
            NIL, NOT, OP, OR, OVERRIDE, POS, PROPERTY, RETURN,
            SET, SETOF, SUPER, STATIC, TABLEOF, THEN, THIS, TO, TRUE, VAR, WHERE, WHILE
        });

        tokenTypes.put("identifier", new int[] { IDENTIFIER });
        tokenTypes.put("number", new int[] { INTEGER, INF });
        tokenTypes.put("string", new int[] { STRING });
        tokenTypes.put("comment", new int[] { SL_COMMENT, ML_COMMENT });

        idToToken = new HashMap<Integer, CgsuiteTokenId>();
        Set<Integer> observedTokens = new HashSet<Integer>();

        for (String category : tokenTypes.keySet())
        {
            for (int id : tokenTypes.get(category))
            {
                idToToken.put(id, new CgsuiteTokenId(CgsuiteParser.tokenNames[id], category, id));
                observedTokens.add(id);
            }
        }

        for (int id = 0; id < CgsuiteParser.tokenNames.length; id++)
        {
            if (!observedTokens.contains(id))
            {
                idToToken.put(id, new CgsuiteTokenId(CgsuiteParser.tokenNames[id], "separator", id));
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
        if (idToToken == null) {
            init();
        }
        return idToToken.get(id);
    }
    /**
     * Initializes the tokens in use.
     *
     * @return
     */
    @Override
    protected synchronized Collection<CgsuiteTokenId> createTokenIds()
    {
        if (tokens == null) {
            init();
        }
        return idToToken.values();
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
     * Returns the mime type of this programming language ("text/x-cm). This
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
