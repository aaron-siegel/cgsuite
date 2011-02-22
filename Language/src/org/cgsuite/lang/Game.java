package org.cgsuite.lang;

import org.cgsuite.lang.game.InverseGame;
import org.cgsuite.lang.game.SumGame;


public class Game extends CgsuiteObject
{
    public Game(CgsuiteClass type)
    {
        super(type);
    }

    public InverseGame buildInverse()
    {
        return new InverseGame(this);
    }

    public SumGame buildSum(Game other)
    {
        if (other instanceof SumGame)
            return ((SumGame) other).buildSum(this);
        else
            return new SumGame(this, other);
    }

    public CgsuiteSet getLeftOptions()
    {
        return (CgsuiteSet) invoke("LeftOptions$get");
    }

    public CgsuiteSet getRightOptions()
    {
        return (CgsuiteSet) invoke("RightOptions$get");
    }
}
