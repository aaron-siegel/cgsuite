/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

/**
 *
 * @author asiegel
 */
public class CgsuiteErrorNode extends CgsuiteTree
{
    private TokenStream input;
    private Token start;
    private Token stop;
    private RecognitionException re;

    public CgsuiteErrorNode(TokenStream input, Token start, Token stop, RecognitionException re)
    {
        super(start);
        this.input = input;
        this.start = start;
        this.stop = stop;
        this.re = re;
    }

    public TokenStream getInput()
    {
        return input;
    }

    public RecognitionException getRe()
    {
        return re;
    }

    public Token getStart()
    {
        return start;
    }

    public Token getStop()
    {
        return stop;
    }
}
