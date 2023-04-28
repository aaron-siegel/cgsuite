/*
 * BipartiteMonoid.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: BipartiteMonoid.java,v 1.4 2006/11/10 15:29:59 asiegel Exp $
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

public class BipartiteMonoid
{
    public final static BipartiteMonoid T0, T1, T2;

    static
    {
        T0 = new BipartiteMonoid(ExplicitFiniteMonoid.TRIVIAL, new BitSet());
        int[][] t1Actions = new int[][] { new int[] { 1, 0 } };
        BitSet t1PPortion = new BitSet();
        t1PPortion.set(1);
        T1 = new BipartiteMonoid(new ExplicitFiniteMonoid(t1Actions), t1PPortion);
        int[][] t2Actions = new int[][] { new int[] { 1, 0, 3, 2, 5, 4 }, new int[] { 2, 3, 4, 5, 2, 3 } };
        BitSet t2PPortion = new BitSet();
        t2PPortion.set(1); t2PPortion.set(4);
        T2 = new BipartiteMonoid(new ExplicitFiniteMonoid(t2Actions), t2PPortion);
    }

    CommutativeMonoid monoid;
    BitSet pPortion;
    private List<BitSet> mexCompatibilityMatrix;
    private List<Set<Integer>> meximalSets;

    public BipartiteMonoid(CommutativeMonoid monoid, BitSet pPortion)
    {
        this.monoid = monoid;
        this.pPortion = pPortion;
    }

    public CommutativeMonoid getMonoid()
    {
        return monoid;
    }

    public boolean isP(int x)
    {
        return pPortion.get(x);
    }

    public boolean hasP(BitSet subset)
    {
        return pPortion.intersects(subset);
    }

    public int size()
    {
        return monoid.size();
    }

    public int pPortionSize()
    {
        return pPortion.cardinality();
    }

    public boolean isTame()
    {
        return size() == 1 || size() == 2 || pPortionSize() == 2 && isPowOf2(size()-2);
    }

    private static boolean isPowOf2(int i)
    {
        return i == 1 || (i >= 0 && (i & 1) == 0 && isPowOf2(i >> 1));
    }

    public Set<Integer> meximalSet(int x)
    {
        if (mexCompatibilityMatrix == null)
        {
            buildMexCompatibilityMatrix();
            meximalSets = new ArrayList<Set<Integer>>(monoid.size());
            meximalSets.addAll(Collections.<Set<Integer>>nCopies(monoid.size(), null));
        }
        if (meximalSets.get(x) == null)
        {
            Set<Integer> meximalSet = new HashSet<Integer>();
            for (int y = 0; y < monoid.size(); y++)
            {
                if (mexCompatibilityMatrix.get(Math.max(x, y)).get(Math.min(x, y)))
                {
                    meximalSet.add(y);
                }
            }
            meximalSets.set(x, meximalSet);
        }
        return meximalSets.get(x);
    }

    private void buildMexCompatibilityMatrix()
    {
        List<BitSet> mexCompatibilityMatrix = new ArrayList<BitSet>();
        for (int x = 0; x < monoid.size(); x++)
        {
            mexCompatibilityMatrix.add(new BitSet());
        }
        for (int x = 0; x < monoid.size(); x++)
        {
            //org.cgsuite.Context.getActiveContext().checkKernelState();
            for (int y = 0; y < x; y++)
            {
                if (!mexCompatibilityMatrix.get(x).get(y))
                {
                    // We have to see if they're compatible.
                    boolean compatible = true;
                    for (int z = 0; z < monoid.size(); z++)
                    {
                        if (pPortion.get(monoid.product(x, z)) && pPortion.get(monoid.product(y, z)))
                        {
                            compatible = false;
                            break;
                        }
                    }
                    if (compatible)
                    {
                        for (int z = 0; z < monoid.size(); z++)
                        {
                            int xz = monoid.product(x, z);
                            int yz = monoid.product(y, z);
                            assert xz != yz;
                            mexCompatibilityMatrix.get(Math.max(xz, yz)).set(Math.min(xz, yz));
                        }
                    }
                }
            }
        }
        this.mexCompatibilityMatrix = mexCompatibilityMatrix;
    }

    public BipartiteMonoid reduce(List<Integer> elementsToMap)
    {
        Equivalences eq = findEquivalences();

        int[][] actions = new int[monoid.getNumGenerators()][eq.uniqueElements.length];
        for (int gen = 0; gen < monoid.getNumGenerators(); gen++)
        {
            int oldGenElement = monoid.getGeneratorElement(gen);
            for (int x = 0; x < eq.uniqueElements.length; x++)
            {
                actions[gen][x] = eq.mapElement(monoid.product(eq.uniqueElements[x], oldGenElement));
            }
        }
        BitSet reducedPPortion = new BitSet(eq.uniqueElements.length);
        for (int x = 0; x < eq.uniqueElements.length; x++)
        {
            reducedPPortion.set(x, pPortion.get(eq.uniqueElements[x]));
        }
        if (elementsToMap != null)
        {
            for (int i = 0; i < elementsToMap.size(); i++)
            {
                elementsToMap.set(i, eq.mapElement(elementsToMap.get(i)));
            }
        }
        return new BipartiteMonoid(
            new ExplicitFiniteMonoid(actions),
            reducedPPortion
            );
    }
	
	private Equivalences findEquivalences()
	{
        Equivalences eq = new Equivalences();
		eq.equivalenceMap = new int[monoid.size()];
        eq.fundamentalEquivs = new HashMap<Integer,Integer>();
        Arrays.fill(eq.equivalenceMap, -1);
        int[] possiblyUnique = new int[monoid.size()];

        for (int i = 0; i < possiblyUnique.length; i++)
        {
            possiblyUnique[i] = i;
        }
        int numPU = possiblyUnique.length;

		for (int i = 0; i < numPU; i++)
		{
            //org.cgsuite.Context.getActiveContext().checkKernelState();
            int next = possiblyUnique[i];
            assert eq.equivalenceMap[next] == -1;
            for (int j = 0; j < i; j++)
            {
                assert eq.equivalenceMap[possiblyUnique[j]] == -1;
                if (equivalent(i, j, eq, possiblyUnique, numPU))
                {
                    // Add this equivalence.
                    addEquivalence(eq, next, possiblyUnique[j], possiblyUnique, numPU);
                    eq.fundamentalEquivs.put(next, possiblyUnique[j]);
                    // Update possiblyUnique.
                    for (int k = 0; k < numPU; k++)
                    {
                        if (eq.equivalenceMap[possiblyUnique[k]] != -1)
                        {
                            possiblyUnique[k] = Integer.MAX_VALUE;
                        }
                    }
                    Arrays.sort(possiblyUnique, 0, numPU);
                    for (numPU = 0; possiblyUnique[numPU] != Integer.MAX_VALUE; numPU++);
                    i--;        // (We just got shunted)
                    break;
                }
            }
        }

        // Normalize

        for (int i = 0; i < eq.equivalenceMap.length; i++)
        {
            int value = eq.equivalenceMap[i];
            if (value != -1)
            {
                while (eq.equivalenceMap[value] != -1)
                {
                    value = eq.equivalenceMap[value];
                }
            }
            eq.equivalenceMap[i] = value;
        }

		for (Map.Entry<Integer,Integer> e : eq.fundamentalEquivs.entrySet())
		{
            e.setValue(eq.equivalenceMap[e.getKey()]);
		}

		eq.uniqueElements = new int[numPU];
		System.arraycopy(possiblyUnique, 0, eq.uniqueElements, 0, numPU);
		
		eq.uniqueWordIndices = new HashMap<Integer,Integer>(numPU);
		for (int i = 0; i < numPU; i++)
		{
			eq.uniqueWordIndices.put(eq.uniqueElements[i], i);
		}

        return eq;
	}
	
    private boolean equivalent(int i, int j, Equivalences eq, int[] possiblyUnique, int numPU)
    {
        int next = possiblyUnique[i];
        for (int k = 0; k < numPU; k++)
        {
            assert eq.equivalenceMap[possiblyUnique[k]] == -1;
            int ik = monoid.product(next, possiblyUnique[k]),
                jk = monoid.product(possiblyUnique[j], possiblyUnique[k]);
            if (pPortion.get(ik) != pPortion.get(jk))
            {
                // ik and jk have different outcomes.
                return false;
            }
            // ik and jk have the same outcomes, but they might be known
            // to be inequivalent.  In that case, ikx and jkx have different
            // outcomes for some x; therefore, i and j are inequivalent.
            int trik = eq.traceEquivalence(ik), trjk = eq.traceEquivalence(jk);
            assert trik != -1 && trjk != -1;
            if (trik < next && trjk < next && trik != trjk)
            {
                return false;
            }
        }
        return true;
    }

    private void addEquivalence(Equivalences eq, int w, int x, int[] possiblyUnique, int numPU)
	{
        for (int i = 0; i < numPU; i++)
        {
            int wi = monoid.product(w, possiblyUnique[i]), xi = monoid.product(x, possiblyUnique[i]);
            if (wi != xi)
            {
                int smaller = (wi < xi ? wi : xi), larger = (wi > xi ? wi : xi);
                if (eq.fundamentalEquivs.containsKey(larger))
                {
                    eq.fundamentalEquivs.remove(larger);
                }
                if (eq.equivalenceMap[larger] == -1)
                {
                    eq.equivalenceMap[larger] = smaller;
                }
            }
        }
    }

    private static class Equivalences
    {
        int[] equivalenceMap, uniqueElements;
        Map<Integer,Integer> fundamentalEquivs, uniqueWordIndices;

        public int mapElement(int x)
        {
            if (equivalenceMap[x] != -1)
            {
                x = equivalenceMap[x];
            }
            assert uniqueWordIndices.containsKey(x);
            return uniqueWordIndices.get(x);
        }

        public int traceEquivalence(int x)
        {
            while (equivalenceMap[x] != -1)
            {
                x = equivalenceMap[x];
            }
            return x;
        }
    }

    public BipartiteMonoid regenerate(List<Integer> elementsToMap)
    {
        BitSet submonoid = new BitSet();
        submonoid.set(0);
        List<Integer> newGens = new java.util.ArrayList<Integer>();
        for (int x : elementsToMap)
        {
            if (!submonoid.get(x))
            {
                newGens.add(x);
                monoid.closeSubmonoidUnder(submonoid, x);
            }
        }

        eliminateExtraneousGens(newGens);

        int[] projections = new int[monoid.size()];
        int[] inclusions = new int[submonoid.cardinality()];

        int next = 0;
        for (int x = 0; x < monoid.size(); x++)
        {
            if (submonoid.get(x))
            {
                projections[x] = next;
                inclusions[next] = x;
                next++;
            }
            else
            {
                projections[x] = -1;
            }
        }
        assert next == inclusions.length;

        int[][] newActions = new int[newGens.size()][inclusions.length];

		for (int gen = 0; gen < newActions.length; gen++)
		{
			for (int x = 0; x < inclusions.length; x++)
			{
                newActions[gen][x] = projections[monoid.product(inclusions[x], newGens.get(gen))];
                assert newActions[gen][x] >= 0 : "" + gen + " " + x;
			}
		}

        for (int i = 0; i < elementsToMap.size(); i++)
        {
            assert projections[elementsToMap.get(i)] >= 0;
            elementsToMap.set(i, projections[elementsToMap.get(i)]);
        }

        BitSet newPPortion = new BitSet();
        for (int x = 0; x < inclusions.length; x++)
        {
            newPPortion.set(x, pPortion.get(inclusions[x]));
        }

        return new BipartiteMonoid(new ExplicitFiniteMonoid(newActions), newPPortion);
    }

    private void eliminateExtraneousGens(List<Integer> gens)
    {
        BitSet submonoid = new BitSet();
        for (int i = 0; i < gens.size(); i++)
        {
            submonoid.clear();
            submonoid.set(0);
            int gen = gens.get(i);
            for (int x : gens)
            {
                if (x != gen)
                {
                    monoid.closeSubmonoidUnder(submonoid, x);
                }
            }
            if (submonoid.get(gen))
            {
                gens.remove(i);
                i--;
            }
        }
    }

    public List<Word> pPortion()
    {
        List<Word> pWords = new ArrayList<Word>();
        for (int x = pPortion.nextSetBit(0); x != -1; x = pPortion.nextSetBit(x+1))
        {
            pWords.add(monoid.elementToWord(x));
        }
        Collections.sort(pWords);
        return pWords;
    }

    public boolean isIsomorphicTo(BipartiteMonoid other)
    {
        return monoid.isIsomorphicTo(other.monoid, pPortion, other.pPortion);
    }

    private List<List<Integer>> idDetails;

    public boolean equals(Object obj)
    {
        if (!(obj instanceof BipartiteMonoid))
        {
            return false;
        }
        BipartiteMonoid other = (BipartiteMonoid) obj;
        if (idDetails == null)
        {
            idDetails = monoid.idDetails(pPortion);
        }
        if (other.idDetails == null)
        {
            other.idDetails = other.monoid.idDetails(other.pPortion);
        }
        if (!idDetails.equals(other.idDetails))
        {
            return false;
        }
        assert monoid.size() == other.monoid.size();

        return monoid.isIsomorphicallyPresented(other.monoid, pPortion, other.pPortion);
    }

    public int hashCode()
    {
        if (idDetails == null)
        {
            idDetails = monoid.idDetails(pPortion);
        }
        return idDetails.hashCode();
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Q = ");
        buf.append(monoid.toString());
        buf.append("; P = {");
        List<Word> pWords = pPortion();
        for (Word word : pWords)
        {
            buf.append(word);
            buf.append(',');
        }
        if (!pWords.isEmpty())
        {
            buf.deleteCharAt(buf.length()-1);
        }
        buf.append('}');
        return buf.toString();
    }
}
