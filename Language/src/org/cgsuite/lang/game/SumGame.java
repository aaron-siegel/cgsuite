package org.cgsuite.lang.game;

import org.cgsuite.lang.Game;
import org.cgsuite.lang.CgsuiteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteList;

import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.output.StyledTextOutput;

public class SumGame extends Game
{
    private List<CgsuiteObject> components;

    public SumGame(CgsuiteObject x, CgsuiteObject y)
    {
        super(CgsuitePackage.forceLookupClass("SumGame"));

        this.components = new ArrayList<CgsuiteObject>(2);
        this.components.add(x);
        this.components.add(y);
    }

    public SumGame(List<CgsuiteObject> components)
    {
        super(CgsuitePackage.forceLookupClass("SumGame"));

        this.components = components;
    }
    
    public SumGame(CgsuiteList components)
    {
        this(components.getUnderlyingCollection());
    }

    public List<CgsuiteObject> getComponents()
    {
        return components;
    }

    @Override
    public SumGame buildSum(Game other)
    {
        List<CgsuiteObject> newComponents = new ArrayList<CgsuiteObject>(components.size()+1);
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
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        if (components.isEmpty())
        {
            output.appendMath("0");
        }
        for (Iterator<? extends CgsuiteObject> it = components.iterator(); it.hasNext();)
        {
            output.appendOutput(it.next().toOutput());
            if (it.hasNext())
                output.appendMath(" + ");
        }
        return output;
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

    @Override
    public Game simplify() throws CgsuiteException
    {
        List<CgsuiteObject> simplified = new ArrayList<CgsuiteObject>();
        boolean allIntegers = true;
        boolean allNumbers = true;
        boolean allCanonical = true;

        for (CgsuiteObject x : components)
        {
            CgsuiteObject simp = x.simplify();
            if (simp instanceof CgsuiteInteger)
            {
            }
            else if (simp instanceof RationalNumber)
            {
                if (!((RationalNumber) simp).isInteger())
                {
                    allIntegers = false;
                    if (!((RationalNumber) simp).isDyadic())
                        allCanonical = false;
                }
            }
            else
            {
                allNumbers = false;
                if (!(simp instanceof CanonicalShortGame))
                    allCanonical = false;
            }
            simplified.add(simp);
        }

        if (allNumbers)
        {
            RationalNumber answer = RationalNumber.ZERO;
            for (CgsuiteObject obj : simplified)
            {
                if (obj instanceof CgsuiteInteger)
                    answer = answer.add(new RationalNumber((CgsuiteInteger) obj));
                else
                    answer = answer.add((RationalNumber) obj);
            }
            return answer;
        }
        else if (allCanonical)
        {
            CanonicalShortGame answer = CanonicalShortGame.ZERO;
            for (CgsuiteObject obj : simplified)
            {
                if (obj instanceof CgsuiteInteger)
                    answer = answer.add(new CanonicalShortGame((CgsuiteInteger) obj));
                else if (obj instanceof RationalNumber)
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
