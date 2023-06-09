/*
 * DirectSumMonoid.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: DirectSumMonoid.java,v 1.1 2006/10/18 20:52:26 asiegel Exp $
 */

/* ****************************************************************************

    Combinatorial Game Suite - A program to analyze combinatorial games
    Copyright (C) 2003-06  Aaron Siegel (asiegel@users.sourceforge.net)
    http://cgsuite.sourceforge.net/

    Combinatorial Game Suite is free software; you can redistribute it
    and/or modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2 of the
    License, or (at your option) any later version.

    Combinatorial Game Suite is distributed in the hope that it will be
    useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Combinatorial Game Suite; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA

**************************************************************************** */

package org.cgsuite.core.misere.solver;

public class DirectSumMonoid extends CommutativeMonoid
{
    private CommutativeMonoid[] components;
    private int[] multipliers;
    private int[] genShifts;

    public DirectSumMonoid(CommutativeMonoid ... components)
    {
        this.components = new CommutativeMonoid[components.length];
        System.arraycopy(components, 0, this.components, 0, components.length);
        multipliers = new int[components.length+1];
        multipliers[0] = 1;
        genShifts = new int[components.length+1];
        genShifts[0] = 0;
        for (int i = 0; i < components.length; i++)
        {
            multipliers[i+1] = multipliers[i] * components[i].size();
            genShifts[i+1] = genShifts[i] + components[i].getNumGenerators();
        }
    }

    public int project(int x, int i)
    {
        int result = (x % multipliers[i+1]) / multipliers[i];
        assert result < components[i].size();
        return result;
    }

    public int embed(int x, int i)
    {
        return multipliers[i] * x;
    }

    public int size()
    {
        return multipliers[components.length];
    }

    public java.util.List<Word> elements()
    {
        throw new UnsupportedOperationException();
    }

    public int product(int x, int y)
    {
        if (x >= size())
        {
            throw new IllegalArgumentException("" + x + " >= " + size());
        }
        if (y >= size())
        {
            throw new IllegalArgumentException("" + y + " >= " + size());
        }
        int product = 0;
        for (int i = 0; i < components.length; i++)
        {
            product += multipliers[i] * components[i].product(project(x, i), project(y, i));
        }
        return product;
    }

    public CommutativeMonoid getComponent(int index)
    {
        return components[index];
    }

    public int getMultiplierOfComponent(int index)
    {
        return multipliers[index];
    }

    public int getNumGenerators()
    {
        return genShifts[components.length];
    }

    public int getGeneratorElement(int gen)
    {
        for (int i = 0; i < components.length; i++)
        {
            if (gen >= genShifts[i] && gen < genShifts[i+1])
            {
                return embed(components[i].getGeneratorElement(gen - genShifts[i]), i);
            }
        }
        throw new IllegalArgumentException("gen");
    }

    public Word elementToWord(int x)
    {
        Word word = Word.ZERO;
        for (int i = 0; i < components.length; i++)
        {
            word.add(components[i].elementToWord(project(x, i)).rightShift(genShifts[i]));
        }
        return word;
    }

    public int wordToElement(Word word)
    {
        int x = 0;
        for (int i = 0; i < components.length; i++)
        {
            x += embed(components[i].wordToElement(word.leftShift(genShifts[i]).truncateTo(components[i].getNumGenerators())), i);
        }
        return x;
    }

    public @Override java.util.List<Relation> presentation()
    {
        throw new UnsupportedOperationException();
    }
}
