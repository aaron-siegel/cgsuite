/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite;

import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;

/**
 *
 * @author asiegel
 */
public class MultipleGame extends Game
{
    RationalNumber multiplier;
    CgsuiteObject g;

    public MultipleGame(RationalNumber multiplier, CgsuiteObject g)
    {
        super(CgsuitePackage.forceLookupClass("MultipleGame"));

        // TODO Some validation

        this.multiplier = multiplier;
        this.g = g;
    }

    @Override
    public String toString()
    {
        return multiplier.toString() + " * " + g.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MultipleGame other = (MultipleGame) obj;
        if (this.multiplier != other.multiplier && (this.multiplier == null || !this.multiplier.equals(other.multiplier))) {
            return false;
        }
        if (this.g != other.g && (this.g == null || !this.g.equals(other.g))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.multiplier != null ? this.multiplier.hashCode() : 0);
        hash = 43 * hash + (this.g != null ? this.g.hashCode() : 0);
        return hash;
    }

    public CgsuiteObject simplify() throws CgsuiteException
    {
        CgsuiteObject simp = g.invoke("Simplify");

        if (simp instanceof RationalNumber)
        {
            return multiplier.multiply((RationalNumber) simp);
        }
        else if (simp instanceof CanonicalShortGame)
        {
            return new CanonicalShortGame(multiplier).nortonMultiply((CanonicalShortGame) simp);
        }
        else
        {
            return new MultipleGame(multiplier, simp);
        }
    }
}
