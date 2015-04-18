/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.game;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.CgsuiteException;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class MultipleGame extends Game
{
    private final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("MultipleGame");

    CgsuiteInteger multiplier;
    Game g;

    public MultipleGame(CgsuiteInteger multiplier, Game g)
    {
        super(TYPE);
        
        this.multiplier = multiplier;
        this.g = g;
    }
    
    @Override
    public MultipleGame negate()
    {
        return new MultipleGame(multiplier.negate(), g);
    }
    
    @Override
    public Game add(Game other)
    {
        if (other instanceof MultipleGame && ((MultipleGame) other).g.equals(g))
        {
            return new MultipleGame(multiplier.add(((MultipleGame) other).multiplier), g);
        }
        else
        {
            return super.add(other);
        }
    }
    
    public CgsuiteInteger getMultiplier()
    {
        return multiplier;
    }
    
    public Game getMultiplicand()
    {
        return g;
    }
    
    @Override
    public Output toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendOutput(multiplier.toOutput());
        output.appendMath(" * ");
        output.appendOutput(g.toOutput());
        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MultipleGame other = (MultipleGame) obj;
        if (this.multiplier != other.multiplier && (this.multiplier == null || !this.multiplier.equals(other.multiplier))) {
            return false;
        }
        if (this.g != other.g && (this.g == null || !this.g.equals(other.g))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.multiplier != null ? this.multiplier.hashCode() : 0);
        hash = 43 * hash + (this.g != null ? this.g.hashCode() : 0);
        return hash;
    }

    public Game expandAndSimplify()
    {
        if (multiplier.intValue() < 0)
            return negate().expandAndSimplify();
        
        Game sum = g;
        Game result = CanonicalShortGame.ZERO;
        
        // We use a "binary addition" algorithm.
        for (int power2 = 0; multiplier.intValue() >> power2 != 0; power2++)
        {
            if (power2 > 0)
            {
                sum = (Game) sum.invokeMethod("op +", sum).simplify();
            }
            if ((multiplier.intValue() & (1 << power2)) != 0)
            {
                result = (Game) result.invokeMethod("op +", sum).simplify();
            }
        }
        
        return result;
    }
    
    @Override
    public Game simplify() throws CgsuiteException
    {
        Game simp = multiplier.multiply(g.simplify());
        if (simp instanceof MultipleGame)
        {
            Game simpG = ((MultipleGame) simp).g;
            if (simpG instanceof CanonicalStopperGame || simpG instanceof StopperSidedGame)
            {
                return ((MultipleGame) simp).expandAndSimplify();
            }
        }
        return simp;
    }
}
