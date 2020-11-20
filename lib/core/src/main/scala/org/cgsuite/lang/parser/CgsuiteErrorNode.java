/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.parser;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author asiegel
 */
public class CgsuiteErrorNode extends CgsuiteTree
{
    private CommonTokenStream input;
    private CommonToken start;
    private CommonToken stop;
    private RecognitionException re;

    public CgsuiteErrorNode(CommonTokenStream input, CommonToken start, CommonToken stop, RecognitionException re)
    {
        super(start, input);
        this.input = input;
        this.start = start;
        this.stop = stop;
        this.re = re;
    }

    public CommonTokenStream getInput()
    {
        return input;
    }

    public RecognitionException getRe()
    {
        return re;
    }

    public CommonToken getStart()
    {
        return start;
    }

    public CommonToken getStop()
    {
        return stop;
    }
}
