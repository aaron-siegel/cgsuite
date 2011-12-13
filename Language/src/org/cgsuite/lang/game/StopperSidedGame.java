/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.game;

import org.cgsuite.lang.CgsuiteClass;
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
