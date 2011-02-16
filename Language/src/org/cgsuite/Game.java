package org.cgsuite;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;


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
}
