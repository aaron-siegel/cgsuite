package org.cgsuite.lang.game;

import org.cgsuite.lang.Game;
import org.cgsuite.lang.CgsuiteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.CgsuiteSet;
import org.cgsuite.lang.output.StyledTextOutput;

public class ExplicitGame extends Game
{
    private CgsuiteSet leftOptions;
    private CgsuiteSet rightOptions;

    public ExplicitGame(CgsuiteSet leftOptions, CgsuiteSet rightOptions)
    {
        super(CgsuitePackage.forceLookupClass("ExplicitGame"));

        this.leftOptions = leftOptions;
        this.rightOptions = rightOptions;
    }

    @Override
    public CgsuiteSet getLeftOptions()
    {
        return leftOptions;
    }

    @Override
    public CgsuiteSet getRightOptions()
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

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("{");
        for (Iterator<CgsuiteObject> it = getLeftOptions().iterator(); it.hasNext();)
        {
            output.appendOutput(it.next().toOutput());
            if (it.hasNext())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("|");
        for (Iterator<CgsuiteObject> it = getRightOptions().iterator(); it.hasNext();)
        {
            output.appendOutput(it.next().toOutput());
            if (it.hasNext())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("}");
        return output;
    }

    @Override
    public Game simplify() throws CgsuiteException
    {
        CgsuiteSet simplifiedLeftOptions = new CgsuiteSet();
        CgsuiteSet simplifiedRightOptions = new CgsuiteSet();
        List<CanonicalShortGame> canonicalLeftOptions = new ArrayList<CanonicalShortGame>();
        List<CanonicalShortGame> canonicalRightOptions = new ArrayList<CanonicalShortGame>();

        boolean allCanonical = true;
        for (CgsuiteObject x : leftOptions)
        {
            CgsuiteObject simp = x.simplify();
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
            CgsuiteObject simp = x.simplify();
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
