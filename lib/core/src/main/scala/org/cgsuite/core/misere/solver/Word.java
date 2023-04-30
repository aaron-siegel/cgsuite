/*
 * Word.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: Word.java,v 1.1 2006/10/18 20:52:26 asiegel Exp $
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

public class Word implements Comparable<Word>
{
    public final static Word ZERO = new Word(new int[0]);

    private int[] exponents;

    public Word(int[] exponents)
    {
        int lastNonzeroGen = -1;

        for (int gen = 0; gen < exponents.length; gen++)
        {
            if (exponents[gen] != 0)
            {
                lastNonzeroGen = gen;
            }
        }

        this.exponents = new int[lastNonzeroGen+1];
        System.arraycopy(exponents, 0, this.exponents, 0, this.exponents.length);
    }

    private Word()
    {
    }

    public static Word delta(int gen)
    {
        Word word = new Word();
        word.exponents = new int[gen+1];
        word.exponents[gen] = 1;
        return word;
    }

    public int length()
    {
        return exponents.length;
    }

    public int getExponent(int gen)
    {
        if (gen < 0)
        {
            throw new IllegalArgumentException("gen < 0");
        }
        else if (gen < exponents.length)
        {
            return exponents[gen];
        }
        else
        {
            return 0;
        }
    }

    public Word add(Word other)
    {
        Word sum = new Word();
        sum.exponents = new int[Math.max(exponents.length, other.exponents.length)];
        System.arraycopy(exponents, 0, sum.exponents, 0, exponents.length);
        for (int gen = 0; gen < other.exponents.length; gen++)
        {
            sum.exponents[gen] += other.exponents[gen];
        }
        return sum;
    }

    public Word addGen(int gen)
    {
        Word sum = new Word();
        sum.exponents = new int[Math.max(exponents.length, gen+1)];
        System.arraycopy(exponents, 0, sum.exponents, 0, exponents.length);
        sum.exponents[gen]++;
        return sum;
    }

    public Word subtract(Word other)
    {
        int[] newExponents = new int[Math.max(exponents.length, other.exponents.length)];
        System.arraycopy(exponents, 0, newExponents, 0, exponents.length);
        for (int gen = 0; gen < other.exponents.length; gen++)
        {
            newExponents[gen] -= other.exponents[gen];
        }
        return new Word(newExponents);
    }

    public Word removeGen(int gen)
    {
        if (getExponent(gen) == 0)
        {
            throw new IllegalArgumentException("gen == 0");
        }
        int[] newExponents = new int[exponents.length];
        System.arraycopy(exponents, 0, newExponents, 0, exponents.length);
        newExponents[gen]--;
        return new Word(newExponents);
    }

    public Word leftShift(int len)
    {
        Word shift = new Word();
        shift.exponents = new int[Math.max(0, exponents.length-len)];
        if (shift.exponents.length > 0)
        {
            System.arraycopy(exponents, len, shift.exponents, 0, shift.exponents.length);
        }
        return shift;
    }

    public Word rightShift(int len)
    {
        Word shift = new Word();
        shift.exponents = new int[exponents.length+len];
        System.arraycopy(exponents, 0, shift.exponents, len, exponents.length);
        return shift;
    }

    public Word truncateTo(int len)
    {
        int[] truncated = new int[len];
        System.arraycopy(exponents, 0, truncated, 0, Math.min(exponents.length, len));
        return new Word(truncated);
    }

    public boolean contains(Word other)
    {
        if (exponents.length < other.exponents.length)
        {
            return false;
        }

        for (int gen = 0; gen < other.exponents.length; gen++)
        {
            if (exponents[gen] < other.exponents[gen])
            {
                return false;
            }
        }

        return true;
    }

    public List<Integer> nonzeroGens()
    {
        List<Integer> nonzeroGens = new ArrayList<Integer>();
        for (int gen = 0; gen < exponents.length; gen++)
        {
            if (exponents[gen] != 0)
            {
                nonzeroGens.add(gen);
            }
        }
        return nonzeroGens;
    }

    public List<Word> subwords()
    {
        List<Word> subwords = new ArrayList<Word>();
        buildSubwords(subwords, Word.ZERO, length()-1);
        return subwords;
    }

    private void buildSubwords(List<Word> subwords, Word currentWord, int depth)
    {
        if (depth == -1)
        {
            subwords.add(currentWord);
        }
        else
        {
            while (contains(currentWord))
            {
                buildSubwords(subwords, currentWord, depth-1);
                currentWord = currentWord.addGen(depth);
            }
        }
    }

    public boolean equals(Object obj)
    {
        return obj instanceof Word && Arrays.equals(exponents, ((Word) obj).exponents);
    }

    public int hashCode()
    {
        return Arrays.hashCode(exponents);
    }

    public String toAlphabeticString()
    {
        StringBuffer buf = new StringBuffer();
        for (int gen = 0; gen < exponents.length; gen++)
        {
            if (exponents[gen] > 0)
            {
                buf.append(gen < 26 ? ((char)(gen+97)) : (gen < 52 ? ((char)(gen-26+65)) : '?'));
                if (exponents[gen] > 1)
                {
                    buf.append(exponents[gen]);
                }
            }
        }

        if (buf.length() == 0)
        {
            return "1";
        }
        else
        {
            return buf.toString();
        }
    }

    public String toNumericString()
    {
        StringBuffer buf = new StringBuffer("[");
        for (int gen = 0; gen < exponents.length; gen++)
        {
            for (int i = 0; i < exponents[gen]; i++)
            {
                buf.append(gen);
                buf.append(",");
            }
        }

        if (buf.length() > 1)
        {
            buf.deleteCharAt(buf.length()-1);
        }

        buf.append("]");
        return buf.toString();
    }

    public String toString()
    {
        return toAlphabeticString();
    }

    public int compareTo(Word other)
    {
        if (exponents.length != other.exponents.length)
        {
            return exponents.length - other.exponents.length;
        }

        for (int gen = exponents.length-1; gen >= 0; gen--)
        {
            if (exponents[gen] != other.exponents[gen])
            {
                return exponents[gen] - other.exponents[gen];
            }
        }

        return 0;
    }

    public static Word parseWord(String str)
    {
        if (str.equals("1"))
        {
            return Word.ZERO;
        }

        int index = 0;
        Word word = Word.ZERO;
        while (index < str.length())
        {
            char ch = str.charAt(index);
            int gen;
            if (ch >= 'a' && ch <= 'z')
            {
                gen = (ch-'a');
            }
            else if (ch >= 'A' && ch <= 'Z')
            {
                gen = (ch-'A'+26);
            }
            else
            {
                throw new NumberFormatException("That is not a valid word.");
            }
            index++;
            int exponent = 1;
            if (index < str.length() && str.charAt(index) >= '0' && str.charAt(index) <= '9')
            {
                exponent = 0;
                while (index < str.length() && str.charAt(index) >= '0' && str.charAt(index) <= '9')
                {
                    exponent = 10 * exponent + (str.charAt(index)-'0');
                    index++;
                }
            }
            for (int i = 0; i < exponent; i++)
            {
                word = word.add(delta(gen));
            }
        }
        return word;
    }
}
