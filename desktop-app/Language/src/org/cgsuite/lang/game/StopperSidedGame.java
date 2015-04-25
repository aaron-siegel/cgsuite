/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.game;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class StopperSidedGame extends Game
{
    public static final CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("StopperSidedGame");
    
    private CanonicalStopperGame onside;
    private CanonicalStopperGame offside;
    
    public StopperSidedGame(CgsuiteInteger n)
    {
        this(new CanonicalShortGame(n));
    }
    
    public StopperSidedGame(CanonicalShortGame g)
    {
        this(new CanonicalStopperGame(g));
    }
    
    public StopperSidedGame(CanonicalStopperGame g)
    {
        this(g, g);
    }
    
    public StopperSidedGame(CanonicalStopperGame onside, CanonicalStopperGame offside)
    {
        super(TYPE);
        this.onside = onside;
        this.offside = offside;
    }
    
    public StopperSidedGame add(StopperSidedGame h)
    {
        return new StopperSidedGame(onside.upsum(h.onside), offside.downsum(h.offside));
    }
    
    public StopperSidedGame subtract(StopperSidedGame h)
    {
        return add(h.negate());
    }
    
    @Override
    public StopperSidedGame negate()
    {
        return new StopperSidedGame(offside.negate(), onside.negate());
    }
    
    public boolean leq(StopperSidedGame h)
    {
        return onside.leq(h.onside) && offside.leq(h.offside);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StopperSidedGame other = (StopperSidedGame) obj;
        if (this.onside != other.onside && (this.onside == null || !this.onside.equals(other.onside))) {
            return false;
        }
        if (this.offside != other.offside && (this.offside == null || !this.offside.equals(other.offside))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.onside != null ? this.onside.hashCode() : 0);
        hash = 43 * hash + (this.offside != null ? this.offside.hashCode() : 0);
        return hash;
    }
    
    public CanonicalStopperGame getOnside()
    {
        return onside;
    }
    
    public CanonicalStopperGame getOffside()
    {
        return offside;
    }
    
    @Override
    public Game simplify()
    {
        if (onside.equals(offside))
        {
            return onside.simplify();
        }
        else
        {
            return this;
        }
    }
    
    @Override
    public Output toOutput()
    {
        StyledTextOutput sto = new StyledTextOutput();
        if (onside.isOn() && offside.isOff())
        {
            sto.appendMath("dud");
        }
        else
        {
            sto.appendOutput(onside.toOutput());
            sto.appendMath(" & ");
            sto.appendOutput(offside.toOutput());
        }
        return sto;
    }
}
