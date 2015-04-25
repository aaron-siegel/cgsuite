package org.cgsuite.lang.game;

import org.cgsuite.lang.Game;
import org.cgsuite.lang.CgsuiteException;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.output.StyledTextOutput;


public class InverseGame extends Game
{
    private Game g;

    public InverseGame(Game g)
    {
        super(CgsuitePackage.forceLookupClass("InverseGame"));

        this.g = g;
    }

    public Game getG()
    {
        return g;
    }
    
    @Override
    protected int compareLike(CgsuiteObject obj)
    {
        InverseGame other = (InverseGame) obj;
        return g.universalCompareTo(other.g);
    }

    @Override
    public Game simplify() throws CgsuiteException
    {
        return g.simplify().negate();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((g == null) ? 0 : g.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        InverseGame other = (InverseGame) obj;
        if (g == null)
        {
            if (other.g != null)
                return false;
        } else if (!g.equals(other.g))
            return false;
        return true;
    }

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("-");
        output.appendOutput(g.toOutput());
        return output;
    }
}
