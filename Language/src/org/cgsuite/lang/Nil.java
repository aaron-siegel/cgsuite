/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class Nil extends CgsuiteObject
{
    Nil()
    {
    }
    
    @Override
    public String toString()
    {
        return "nil";
    }

    @Override
    public CgsuiteString toCgsuiteString()
    {
        return new CgsuiteString("nil");
    }

    @Override
    public Output toOutput()
    {
        return new StyledTextOutput("nil");
    }
}
