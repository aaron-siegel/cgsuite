/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.game;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class Nimber extends Game
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Nimber");
    
    private int nimValue;
    
    public Nimber(int nimValue)
    {
        super(TYPE);
        
        this.nimValue = nimValue;
    }
    
    public int getNimValue()
    {
        return nimValue;
    }
    
    public Nimber add(Nimber other)
    {
        return new Nimber(nimValue ^ other.nimValue);
    }
    
    @Override
    public Game simplify()
    {
        if (nimValue == 0)
            return CgsuiteInteger.ZERO;
        else
            return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Nimber other = (Nimber) obj;
        if (this.nimValue != other.nimValue) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 43 * hash + this.nimValue;
        return hash;
    }
    
    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        if (nimValue != 0)
            output.appendMath("*");
        if (nimValue != 1)
            output.appendMath(String.valueOf(nimValue));
        return output;
    }
}
