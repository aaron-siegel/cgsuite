package org.cgsuite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.cgsuite.lang.CgsuiteClass;

import org.cgsuite.lang.CgsuiteObject;

public class ExplicitGame extends Game
{
    private Set<CgsuiteObject> leftOptions;
    private Set<CgsuiteObject> rightOptions;

    public ExplicitGame(Set<CgsuiteObject> leftOptions, Set<CgsuiteObject> rightOptions)
    {
        super(CgsuiteClass.lookupClass("ExplicitGame"));

        this.leftOptions = leftOptions;
        this.rightOptions = rightOptions;
    }

    public Set<CgsuiteObject> getLeftOptions()
    {
        return leftOptions;
    }

    public Set<CgsuiteObject> getRightOptions()
    {
        return rightOptions;
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        Iterator<CgsuiteObject> it = leftOptions.iterator();
        while (it.hasNext())
        {
            buf.append(it.next().toString());
            if (it.hasNext())
                buf.append(',');
        }
        buf.append('|');
        it = rightOptions.iterator();
        while (it.hasNext())
        {
            buf.append(it.next().toString());
            if (it.hasNext())
                buf.append(',');
        }
        buf.append('}');
        return buf.toString();
    }

    public CgsuiteObject simplify() throws CgsuiteException
    {
        Set<CgsuiteObject> simplifiedLeftOptions = new HashSet<CgsuiteObject>();
        Set<CgsuiteObject> simplifiedRightOptions = new HashSet<CgsuiteObject>();
        List<CanonicalShortGame> canonicalLeftOptions = new ArrayList<CanonicalShortGame>();
        List<CanonicalShortGame> canonicalRightOptions = new ArrayList<CanonicalShortGame>();

        boolean allCanonical = true;
        for (CgsuiteObject x : leftOptions)
        {
            CgsuiteObject simp = x.invoke("Simplify");
            simplifiedLeftOptions.add(simp);

            if (simp instanceof RationalNumber && ((RationalNumber) simp).isDyadic())
                canonicalLeftOptions.add(new CanonicalShortGame((RationalNumber) simp));
            else if (simp instanceof CanonicalShortGame)
                canonicalLeftOptions.add((CanonicalShortGame) simp);
            else
                allCanonical = false;
        }
        for (CgsuiteObject x : rightOptions)
        {
            CgsuiteObject simp = x.invoke("Simplify");
            simplifiedRightOptions.add(simp);

            if (simp instanceof RationalNumber && ((RationalNumber) simp).isDyadic())
                canonicalRightOptions.add(new CanonicalShortGame((RationalNumber) simp));
            else if (simp instanceof CanonicalShortGame)
                canonicalRightOptions.add((CanonicalShortGame) simp);
            else
                allCanonical = false;
        }

        if (allCanonical)
            return new CanonicalShortGame(canonicalLeftOptions, canonicalRightOptions).simplify();
        else
            return new ExplicitGame(simplifiedLeftOptions, simplifiedRightOptions);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((leftOptions == null) ? 0 : leftOptions.hashCode());
        result = prime * result + ((rightOptions == null) ? 0 : rightOptions.hashCode());
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
        ExplicitGame other = (ExplicitGame) obj;
        if (leftOptions == null)
        {
            if (other.leftOptions != null)
                return false;
        } else if (!leftOptions.equals(other.leftOptions))
            return false;
        if (rightOptions == null)
        {
            if (other.rightOptions != null)
                return false;
        } else if (!rightOptions.equals(other.rightOptions))
            return false;
        return true;
    }
}
