package org.cgsuite.lang.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cgsuite.lang.CgsuiteException;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.CgsuiteSet;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.output.StyledTextOutput;

public class ExplicitGame extends Game
{
    private boolean literal;
    private CgsuiteSet leftOptions;
    private CgsuiteSet rightOptions;
    
    public ExplicitGame(CgsuiteSet leftOptions, CgsuiteSet rightOptions)
    {
        this(leftOptions, rightOptions, false);
    }
    
    public ExplicitGame(CgsuiteSet leftOptions, CgsuiteSet rightOptions, boolean literal)
    {
        super(CgsuitePackage.forceLookupClass("ExplicitGame"));

        this.leftOptions = leftOptions;
        this.rightOptions = rightOptions;
        this.literal = literal;
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
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        if (literal)
            output.appendMath("'");
        output.appendMath("{");
        for (Iterator<CgsuiteObject> it = getLeftOptions().sortedIterator(); it.hasNext();)
        {
            output.appendOutput(it.next().toOutput());
            if (it.hasNext())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("|");
        for (Iterator<CgsuiteObject> it = getRightOptions().sortedIterator(); it.hasNext();)
        {
            output.appendOutput(it.next().toOutput());
            if (it.hasNext())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("}");
        if (literal)
            output.appendMath("'");
        return output;
    }

    @Override
    public Game simplify() throws CgsuiteException
    {
        CgsuiteSet simplifiedLeftOptions = new CgsuiteSet();
        CgsuiteSet simplifiedRightOptions = new CgsuiteSet();

        boolean allStoppers = true;
        boolean allCanonical = true;
        
        for (CgsuiteObject x : leftOptions)
        {
            CgsuiteObject simp = x.simplify();
            simplifiedLeftOptions.add(simp);
            
            if (!simp.getCgsuiteClass().hasAncestor(CanonicalShortGame.TYPE))
            {
                allCanonical = false;
                if (!simp.getCgsuiteClass().hasAncestor(CanonicalStopperGame.TYPE))
                {
                    allStoppers = false;
                }
            }
        }

        for (CgsuiteObject x : rightOptions)
        {
            CgsuiteObject simp = x.simplify();
            simplifiedRightOptions.add(simp);
            
            if (!simp.getCgsuiteClass().hasAncestor(CanonicalShortGame.TYPE))
            {
                allCanonical = false;
                if (!simp.getCgsuiteClass().hasAncestor(CanonicalStopperGame.TYPE))
                {
                    allStoppers = false;
                }
            }
        }
        
        if (!literal && allCanonical)
        {
            List<CanonicalShortGame> canonicalLeftOptions = new ArrayList<CanonicalShortGame>();
            List<CanonicalShortGame> canonicalRightOptions = new ArrayList<CanonicalShortGame>();
            
            for (CgsuiteObject simp : simplifiedLeftOptions)
            {
                if (simp instanceof CgsuiteInteger)
                    canonicalLeftOptions.add(CanonicalShortGame.construct((CgsuiteInteger) simp));
                else if (simp instanceof CanonicalShortGame)
                    canonicalLeftOptions.add((CanonicalShortGame) simp);
                else
                    assert false;
            }
            
            for (CgsuiteObject simp : simplifiedRightOptions)
            {
                if (simp instanceof CgsuiteInteger)
                    canonicalRightOptions.add(CanonicalShortGame.construct((CgsuiteInteger) simp));
                else if (simp instanceof CanonicalShortGame)
                    canonicalRightOptions.add((CanonicalShortGame) simp);
                else
                    assert false;
            }
            
            return CanonicalShortGame.construct(canonicalLeftOptions, canonicalRightOptions).simplify();
        }
        else if (!literal && allStoppers)
        {
            LoopyGame.Node node = new LoopyGame.Node();
            
            for (CgsuiteObject simp : simplifiedLeftOptions)
            {
                if (simp instanceof CgsuiteInteger)
                    node.addLeftEdge(CanonicalShortGame.construct((CgsuiteInteger) simp));
                else if (simp instanceof CanonicalShortGame)
                    node.addLeftEdge((CanonicalShortGame) simp);
                else if (simp instanceof CanonicalStopperGame)
                    node.addLeftEdge((CanonicalStopperGame) simp);
                else
                    assert false;
            }
            
            for (CgsuiteObject simp : simplifiedRightOptions)
            {
                if (simp instanceof CgsuiteInteger)
                    node.addRightEdge(CanonicalShortGame.construct((CgsuiteInteger) simp));
                else if (simp instanceof CanonicalShortGame)
                    node.addRightEdge((CanonicalShortGame) simp);
                else if (simp instanceof CanonicalStopperGame)
                    node.addRightEdge((CanonicalStopperGame) simp);
                else
                    assert false;
            }
            
            return new LoopyGame(node).simplify();
        }
        else
        {
            return new ExplicitGame(simplifiedLeftOptions, simplifiedRightOptions, literal);
        }
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
