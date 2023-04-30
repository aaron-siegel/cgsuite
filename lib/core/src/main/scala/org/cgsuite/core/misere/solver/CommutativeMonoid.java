/*
 * CommutativeMonoid.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: CommutativeMonoid.java,v 1.3 2006/12/11 19:57:46 asiegel Exp $
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

/**
 * A commutative monoid.  Currently, all instances of this class are assumed
 * to be finite, but this may change in the future.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.3 $ $Date: 2006/12/11 19:57:46 $
 */
public abstract class CommutativeMonoid
{
    private Map<Integer,Integer> scratch = new HashMap<Integer,Integer>();

    public abstract int size();

    public abstract int product(int x, int y);

    public abstract int getNumGenerators();

    public abstract int getGeneratorElement(int gen);

    public abstract Word elementToWord(int x);

    public abstract int wordToElement(Word word);

    public abstract List<Word> elements();

    public abstract Collection<Relation> presentation();

    public List<Integer> generators()
    {
        List<Integer> generators = new ArrayList<Integer>();
        for (int i = 0; i < getNumGenerators(); i++)
        {
            generators.add(getGeneratorElement(i));
        }
        return generators;
    }

    public int pow(int x, int n)
    {
        if (n < 0)
        {
            throw new IllegalArgumentException();
        }

        int result = 0;

        for (int i = 0; i < n; i++)
        {
            result = product(result, x);
        }

        return result;
    }

	public int indexOf(int x)
	{
        int n = 0, xToN = 0;
        scratch.clear();

        while (!scratch.containsKey(xToN))
        {
            scratch.put(xToN, n);
            n++;
            xToN = product(xToN, x);
        }

        return scratch.get(xToN);
    }

    public int periodOf(int x)
    {
        int n = 0, xToN = 0;
        scratch.clear();

        while (!scratch.containsKey(xToN))
        {
            scratch.put(xToN, n);
            n++;
            xToN = product(xToN, x);
        }

        return n-scratch.get(xToN);
    }

    public boolean closeSubmonoidUnder(BitSet submonoid, int x)
    {
        if (submonoid.get(x))
        {
            return false;
        }
        for (int y = 0; y < size(); y++)
        {
            if (submonoid.get(y))
            {
                int z = product(x, y);
                while (!submonoid.get(z))
                {
                    submonoid.set(z);
                    z = product(x, z);
                }
            }
        }
        return true;
    }

    public static List<Integer> subsetToElementList(BitSet subset)
    {
        List<Integer> elementList = new ArrayList<Integer>();
        for (int x = subset.nextSetBit(0); x != -1; x = subset.nextSetBit(x+1))
        {
            elementList.add(x);
        }
        return elementList;
    }

    public List<Word> subsetToWordList(BitSet subset)
    {
        List<Word> wordList = new ArrayList<Word>();
        for (int x = subset.nextSetBit(0); x != -1; x = subset.nextSetBit(x+1))
        {
            wordList.add(elementToWord(x));
        }
        return wordList;
    }

    public List<Word> elementListToWordList(List<Integer> elementList)
    {
        List<Word> wordList = new ArrayList<Word>();
        for (int x : elementList)
        {
            wordList.add(elementToWord(x));
        }
        return wordList;
    }

    public List<Integer> idempotents()
    {
        List<Integer> idempotents = new ArrayList<Integer>();
        for (int x = 0; x < size(); x++)
        {
            if (product(x, x) == x)
            {
                idempotents.add(x);
            }
        }
        return idempotents;
    }

    public int kernelIdentity()
    {
        int z = 0;
        for (int x : idempotents())
        {
            z = product(z, x);
        }
        return z;
    }

    public List<Integer> idempotentLowerCovers(int idemp)
    {
        if (product(idemp, idemp) != idemp)
        {
            throw new IllegalArgumentException("not an idempotent");
        }
        List<Integer> lc = new ArrayList<Integer>();
        for (int x : idempotents())
        {
            if (x != idemp && product(x, idemp) == x)
            {
                // x < idemp
                boolean cover = true;
                for (Iterator<Integer> iter = lc.iterator(); iter.hasNext();)
                {
                    int y = iter.next();
                    if (product(x, y) == x)
                    {
                        // x < y < idemp
                        cover = false;
                        break;
                    }
                    else if (product(x, y) == y)
                    {
                        // y < x < idemp
                        iter.remove();
                    }
                }
                if (cover)
                {
                    lc.add(x);
                }
            }
        }
        return lc;
    }

    public BitSet mutualDivisibilityClass(int elem)
    {
        if (leastMdElem == null)
        {
            buildMdClasses();
        }

        BitSet md = new BitSet(size());

        for (int x = 0; x < leastMdElem.length; x++)
        {
            if (leastMdElem[x] == leastMdElem[elem])
            {
                md.set(x);
            }
        }

        return md;
    }

    public BitSet kernel()
    {
        return mutualDivisibilityClass(kernelIdentity());
    }

    public BitSet archimedeanComponent(int idemp)
    {
        if (product(idemp, idemp) != idemp)
        {
            throw new IllegalArgumentException();
        }

        if (leastMdElem == null)
        {
            buildMdClasses();
        }

        BitSet ac = new BitSet(size());

        for (int x = 0; x < leastMdElem.length; x++)
        {
            if (leastMdElem[idemp] == leastMdElem[pow(x, indexOf(x))])
            {
                ac.set(x);
            }
        }

        return ac;
    }

    public int countMdClasses()
    {
        if (leastMdElem == null)
        {
            buildMdClasses();
        }

        int count = 0;
        for (int elem = 0; elem < leastMdElem.length; elem++)
        {
            if (leastMdElem[elem] == elem)
            {
                count++;
            }
        }
        return count;
    }

    private void buildMdClasses()
    {
        List<BitSet> targets = new ArrayList<BitSet>();
        for (int x = 0; x < size(); x++)
        {
            targets.add(new BitSet());
        }

        for (int x = 0; x < size(); x++)
        {
            for (int y = 0; y <= x; y++)
            {
                int xy = product(x, y);
                targets.get(x).set(xy);
                targets.get(y).set(xy);
            }
        }

        leastMdElem = new int[size()];
        Arrays.fill(leastMdElem, -1);
        for (int x = 0; x < size(); x++)
        {
            BitSet xSet = targets.get(x);
            for (int y = xSet.nextSetBit(0); y <= x && y != -1; y = xSet.nextSetBit(y+1))
            {
                if (targets.get(y).get(x))
                {
                    leastMdElem[x] = y;
                    break;
                }
            }
            assert leastMdElem[x] != -1;
        }
    }

    public int hashCode()
    {
        return idDetails(null).hashCode();
    }

    public boolean equals(Object obj)
    {
        return obj instanceof CommutativeMonoid &&
            idDetails(null).equals(((CommutativeMonoid) obj).idDetails(null)) &&
            isIsomorphicallyPresented((CommutativeMonoid) obj, null, null);
    }

    int[] leastMdElem;
    List<List<Integer>> idDetails;

    List<List<Integer>> idDetails(BitSet coloring)
    {
        if (coloring == null && this.idDetails != null)
        {
            return this.idDetails;
        }

        List<List<Integer>> idDetails = new ArrayList<List<Integer>>();

        List<Integer> idemps = idempotents();

        for (int i = 0; i < idemps.size(); i++)
        {
            BitSet ac = archimedeanComponent(idemps.get(i));
            List<Integer> localDetails = new ArrayList<Integer>();
            for (int elem = 0; elem < size(); elem++)
            {
                if (leastMdElem[elem] == elem && ac.get(elem))
                {
                    localDetails.add(mutualDivisibilityClass(elem).cardinality());
                    if (coloring != null)
                    {
                        localDetails.add(bicardinality(mutualDivisibilityClass(elem), coloring));
                    }
                }
            }
            Collections.sort(localDetails);
            localDetails.add(-1);
            localDetails.add(mutualDivisibilityClass(idemps.get(i)).cardinality());
            if (coloring != null)
            {
                localDetails.add(bicardinality(mutualDivisibilityClass(idemps.get(i)), coloring));
                localDetails.add(coloring.get(idemps.get(i)) ? 0 : 1);
            }
            localDetails.add(-1);
            idDetails.add(localDetails);
        }

        // Now, for each idempotent, we add the hashcodes of the detail-lists of all
        // smaller idempotents.

        List<List<Integer>> extraDetails = new ArrayList<List<Integer>>();

        for (int i = 0; i < idemps.size(); i++)
        {
            List<Integer> localExtraDetails = new ArrayList<Integer>();
            for (int j = 0; j < idemps.size(); j++)
            {
                if (j != i && product(idemps.get(i), idemps.get(j)) == idemps.get(j))
                {
                    // idemps[j] < idemps[i].
                    int hc = idDetails.get(j).hashCode();
                    localExtraDetails.add(hc == -1 ? -2 : hc);  // Preserve -1 as a delimiter
                }
            }
            Collections.sort(localExtraDetails);
            extraDetails.add(localExtraDetails);
        }

        // Last, consolidate the lists.

        for (int i = 0; i < idemps.size(); i++)
        {
            idDetails.get(i).addAll(extraDetails.get(i));
        }

        Collections.sort(idDetails, LEXICOGRAPHIC_COMPARATOR);

        if (coloring == null)
        {
            this.idDetails = idDetails;
        }

        return idDetails;
    }

    public boolean isIsomorphicTo(CommutativeMonoid other, BitSet coloring, BitSet otherColoring)
    {
        return isIsomorphicTo(other, coloring, otherColoring, false);
    }

    public boolean isIsomorphicallyPresented(CommutativeMonoid other, BitSet coloring, BitSet otherColoring)
    {
        return isIsomorphicTo(other, coloring, otherColoring, true);
    }

    private boolean isIsomorphicTo(CommutativeMonoid other, BitSet coloring, BitSet otherColoring, boolean forceGens)
    {
        if ((coloring == null) != (otherColoring == null))
        {
            throw new IllegalArgumentException("(coloring == null) != (otherColoring == null)");
        }
        if (size() != other.size() ||
            forceGens && getNumGenerators() != other.getNumGenerators())
        {
            return false;
        }
        int[][] partialMaps = new int[getNumGenerators()+1][size()];
        int[][] partialInverses = new int[getNumGenerators()+1][size()];
        Arrays.fill(partialMaps[0], -1);
        Arrays.fill(partialInverses[0], -1);
        partialMaps[0][0] = 0;
        partialInverses[0][0] = 0;
        return seekIsomorphism(other, coloring, otherColoring, forceGens, partialMaps, partialInverses, 0);
    }

    private boolean seekIsomorphism(CommutativeMonoid other, BitSet coloring, BitSet otherColoring, boolean forceGens, int[][] partialMaps, int[][] partialInverses, int depth)
    {
        if (depth == getNumGenerators())
        {
            for (int elem = 0; elem < partialMaps[depth].length; elem++)
            {
                if (partialMaps[depth][elem] == -1 || partialInverses[depth][elem] == -1)
                {
                    return false;
                }
            }
            return true;
        }
        else if (partialMaps[depth][getGeneratorElement(depth)] != -1)
        {
            // This generator is already assigned.
            if (forceGens && partialMaps[depth][getGeneratorElement(depth)] != other.getGeneratorElement(depth))
            {
                return false;
            }
            System.arraycopy(partialMaps[depth], 0, partialMaps[depth+1], 0, size());
            System.arraycopy(partialInverses[depth], 0, partialInverses[depth+1], 0, size());
            return seekIsomorphism(other, coloring, otherColoring, forceGens, partialMaps, partialInverses, depth+1);
        }
        else
        {
            int firstOElem = (forceGens ? other.getGeneratorElement(depth) : 0);
            int lastOElem = (forceGens ? other.getGeneratorElement(depth)+1 : partialInverses[depth].length);
            for (int oelem = firstOElem; oelem < lastOElem; oelem++)
            {
                if (partialInverses[depth][oelem] == -1 &&
                    (coloring == null || coloring.get(getGeneratorElement(depth)) == otherColoring.get(oelem)))
                {
                    // Try assigning this generator to oelem.
                    System.arraycopy(partialMaps[depth], 0, partialMaps[depth+1], 0, size());
                    System.arraycopy(partialInverses[depth], 0, partialInverses[depth+1], 0, size());
                    if (applyAssignment(other, coloring, otherColoring, partialMaps[depth+1], partialInverses[depth+1], getGeneratorElement(depth), oelem) &&
                        seekIsomorphism(other, coloring, otherColoring, forceGens, partialMaps, partialInverses, depth+1))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean applyAssignment(CommutativeMonoid other, BitSet coloring, BitSet otherColoring, int[] map, int[] inverse, int a, int b)
    {
        assert map[a] == -1 && inverse[b] == -1;
        for (int x = 0; x < size(); x++)
        {
            if (map[x] != -1)
            {
                int ax = product(a, x);
                int bx = other.product(b, map[x]);
                while (map[ax] == -1)
                {
                    if (inverse[bx] != -1 ||
                        coloring != null && coloring.get(ax) != otherColoring.get(bx))
                    {
                        return false;
                    }
                    map[ax] = bx;
                    inverse[bx] = ax;
                    ax = product(a, x);
                    bx = other.product(b, map[x]);
                }
                if (bx != map[ax])
                {
                    return false;
                }
                assert ax == inverse[bx];
            }
        }
        return true;
    }

    public boolean verify()
    {
        return verifyCommutative() && verifyAssociative();
    }

    public boolean verifyCommutative()
    {
        for (int x = 0; x < size(); x++)
        {
            for (int y = 0; y < size(); y++)
            {
                if (product(x, y) != product(y, x))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean verifyAssociative()
    {
        for (int x = 0; x < size(); x++)
        {
            for (int y = 0; y < size(); y++)
            {
                for (int z = 0; z < size(); z++)
                {
                    if (product(product(x, y), z) != product(x, product(y, z)))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append('<');
        for (int gen = 0; gen < getNumGenerators(); gen++)
        {
            buf.append(gen < 26 ? ((char) (gen+97)) : ((char) (gen+65-26)));
            buf.append(',');
        }
        buf.deleteCharAt(buf.length()-1);
        buf.append(" | ");
        for (Relation r : presentation())
        {
            buf.append(r);
            buf.append(',');
        }
        buf.deleteCharAt(buf.length()-1);
        buf.append('>');
        return buf.toString();
    }

    private static int bicardinality(BitSet x, BitSet y)
    {
        int card = 0;
        for (int i = 0; i < x.length() && i < y.length(); i++)
        {
            if (x.get(i) && y.get(i))
            {
                card++;
            }
        }
        return card;
    }

    private static final Comparator<List<Integer>> LEXICOGRAPHIC_COMPARATOR =
    new Comparator<List<Integer>>()
    {
        public int compare(List<Integer> x, List<Integer> y)
        {
            int len = Math.min(x.size(), y.size());
            for (int i = 0; i < len; i++)
            {
                int cmp = x.get(i) - y.get(i);
                if (cmp != 0)
                {
                    return cmp;
                }
            }
            return x.size() - y.size();
        }
    };
}

