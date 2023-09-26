/*
 * ExplicitFiniteMonoid.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: ExplicitFiniteMonoid.java,v 1.1 2006/10/18 20:52:26 asiegel Exp $
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

import java.util.*;

public class ExplicitFiniteMonoid extends CommutativeMonoid
{
    public final static ExplicitFiniteMonoid TRIVIAL = new ExplicitFiniteMonoid(new int[0][0]);

    int[][] actions;
    private Word[] lexLeastWords;

    public ExplicitFiniteMonoid(int[][] actions)
    {
        for (int gen = 1; gen < actions.length; gen++)
        {
            if (actions[gen].length != actions[0].length)
            {
                throw new IllegalArgumentException("actions[" + gen + "].length != actions[0].length");
            }
        }

        this.actions = actions;

        if (actions.length == 0)
        {
            lexLeastWords = new Word[] { Word.ZERO };
        }
        else
        {
            lexLeastWords = new Word[actions[0].length];
            buildLexLeastWords(0, Word.ZERO, actions.length-1);
        }
    }

    private void buildLexLeastWords(int currentElem, Word currentWord, int gen)
    {
        if (gen == -1)
        {
            assert lexLeastWords[currentElem] == null;
            lexLeastWords[currentElem] = currentWord;
        }
        else
        {
            do
            {
                buildLexLeastWords(currentElem, currentWord, gen-1);
                currentElem = actions[gen][currentElem];
                currentWord = currentWord.addGen(gen);
            }
            while (lexLeastWords[currentElem] == null);
        }
    }

    public int size()
    {
        return actions.length == 0 ? 1 : actions[0].length;
    }

    public List<Word> elements()
    {
        return Collections.unmodifiableList(Arrays.asList(lexLeastWords));
    }

    public int product(int x, int y)
    {
        return product(x, lexLeastWords[y]);
    }

    public int product(int x, Word word)
    {
        int product = x;
        for (int gen = 0; gen < word.length(); gen++)
        {
            for (int i = 0; i < word.getExponent(gen); i++)
            {
                product = actions[gen][product];
            }
        }
        return product;
    }

    public int getNumGenerators()
    {
        return actions.length;
    }

    public int getGeneratorElement(int gen)
    {
        return actions[gen][0];
    }

    public Word elementToWord(int elem)
    {
        return lexLeastWords[elem];
    }

    public int wordToElement(Word word)
    {
        return product(0, word);
    }

    public @Override List<Relation> presentation()
    {
        Set<Word> equivSet = buildMinimalEquivSet();
        List<Relation> presentation = new ArrayList<Relation>();

        for (Word word : equivSet)
        {
            presentation.add(new Relation(word, lexLeastWords[wordToElement(word)]));
        }

        return presentation;
    }

    private SortedSet<Word> buildMinimalEquivSet()
    {
        SortedSet<Word> equivSet = new TreeSet<Word>();
        for (int x = 0; x < size(); x++)
        {
            for (int gen = 0; gen < actions.length; gen++)
            {
                int gx = actions[gen][x];
                Word word = lexLeastWords[x].addGen(gen);
                if (!word.equals(lexLeastWords[gx]))
                {
                    addToEquivSet(equivSet, word);
                }
            }
        }

        return equivSet;
    }

    private void addToEquivSet(SortedSet<Word> equivSet, Word word)
    {
        for (Iterator<Word> iter = equivSet.iterator(); iter.hasNext();)
        {
            Word other = iter.next();
            if (word.contains(other))
            {
                return;
            }
            if (other.contains(word))
            {
                iter.remove();
            }
        }
        equivSet.add(word);
    }

    public static ExplicitFiniteMonoid cyclicMonoid(int period, int index)
    {
        int[][] actions = new int[1][index+period];
        for (int x = 0; x < index+period-1; x++)
        {
            actions[0][x] = x + 1;
        }
        actions[0][index+period-1] = index;
        return new ExplicitFiniteMonoid(actions);
    }
}
