package org.cgsuite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cgsuite.lang.CgsuiteClass;

import org.cgsuite.lang.CgsuiteObject;

public class SumGame extends Game
{
    private List<CgsuiteObject> components;

    public SumGame(CgsuiteObject x, CgsuiteObject y)
    {
        super(CgsuiteClass.lookupClass("SumGame"));

        this.components = new ArrayList<CgsuiteObject>();
        this.components.add(x);
        this.components.add(y);
    }

    public SumGame(List<CgsuiteObject> components)
    {
        super(CgsuiteClass.lookupClass("SumGame"));

        this.components = components;
    }

    public List<CgsuiteObject> getComponents()
    {
        return components;
    }

    @Override
    public SumGame buildSum(Game other)
    {
        List<CgsuiteObject> newComponents = new ArrayList<CgsuiteObject>();
        newComponents.addAll(components);
        if (other instanceof SumGame)
            newComponents.addAll(((SumGame) other).components);
        else
            newComponents.add(other);
        return new SumGame(newComponents);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        Iterator<CgsuiteObject> it = components.iterator();
        while (it.hasNext())
        {
            buf.append(it.next().toString());
            if (it.hasNext())
                buf.append(" + ");
        }
        return buf.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((components == null) ? 0 : components.hashCode());
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
        SumGame other = (SumGame) obj;
        if (components == null)
        {
            if (other.components != null)
                return false;
        } else if (!components.equals(other.components))
            return false;
        return true;
    }

    public CgsuiteObject simplify() throws CgsuiteException
    {
        List<CgsuiteObject> simplified = new ArrayList<CgsuiteObject>();
        boolean allNumbers = true;
        boolean allCanonical = true;

        for (CgsuiteObject x : components)
        {
            CgsuiteObject simp = x.invoke("Simplify");
            if (simp instanceof RationalNumber)
            {
                if (!((RationalNumber) simp).isDyadic())
                {
                    allCanonical = false;
                }
            }
            else
            {
                allNumbers = false;
                if (!(simp instanceof CanonicalShortGame))
                {
                    allCanonical = false;
                }
            }
            simplified.add(simp);
        }

        if (allNumbers)
        {
            RationalNumber answer = RationalNumber.ZERO;
            for (CgsuiteObject obj : simplified)
            {
                answer = answer.add((RationalNumber) obj);
            }
            return answer;
        }
        else if (allCanonical)
        {
            CanonicalShortGame answer = CanonicalShortGame.ZERO;
            for (CgsuiteObject obj : simplified)
            {
                if (obj instanceof RationalNumber)
                    answer = answer.add(new CanonicalShortGame((RationalNumber) obj));
                else
                    answer = answer.add((CanonicalShortGame) obj);
            }
            return answer.simplify();
        }
        else
        {
            return new SumGame(simplified);
        }
    }
}
