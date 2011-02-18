package org.cgsuite;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;


public class InverseGame extends Game
{
    private Game g;

    public InverseGame(Game g)
    {
        super(CgsuiteClass.lookupClass("InverseGame"));

        this.g = g;
    }

    public Game getG()
    {
        return g;
    }

    public CgsuiteObject simplify() throws CgsuiteException
    {
        CgsuiteObject gSimp = g.invoke("Simplify");
        if (gSimp instanceof RationalNumber)
            return ((RationalNumber) gSimp).negate();
        else if (gSimp instanceof CanonicalShortGame)
            return ((CanonicalShortGame) gSimp).getInverse();
        else if (gSimp instanceof Game)
            return new InverseGame((Game) gSimp);
        else
            return this;
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
    public String toString()
    {
        return "-(" + g.toString() + ")";
    }

}
