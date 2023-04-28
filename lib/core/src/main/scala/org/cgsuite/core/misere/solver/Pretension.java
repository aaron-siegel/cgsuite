/*
 * Pretension.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: Pretension.java,v 1.5 2006/12/11 19:57:46 asiegel Exp $
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

public class Pretension
{
    BipartiteMonoid quotient;
    List<Integer> prefn;
    Word[] lexLeastGames;
    SortedMap<Word,Integer> sortedLLGames;

    public Pretension(BipartiteMonoid quotient, List<Integer> prefn)
    {
        this.quotient = quotient;
        this.prefn = prefn;

        buildLexLeastGames();
    }

    public BipartiteMonoid getQuotient()
    {
        return quotient;
    }

    public List<Integer> getPrefn()
    {
        return Collections.unmodifiableList(prefn);
    }

    public List<Word> prefnWords()
    {
        List<Word> prefnWords = new ArrayList<Word>();
        for (int pv : prefn)
        {
            prefnWords.add(quotient.monoid.elementToWord(pv));
        }
        return prefnWords;
    }

    public int phi(Word game)
    {
        int x = 0;
        for (int heap = 0; heap < game.length(); heap++)
        {
            for (int i = 0; i < game.getExponent(heap); i++)
            {
                x = quotient.monoid.product(x, prefn.get(heap));
            }
        }
        return x;
    }

    public Pretension reduceToHeap(int heap)
    {
        List<Integer> partialPrefn = new ArrayList<Integer>();
        partialPrefn.addAll(prefn.subList(0, heap+1));
        BipartiteMonoid reduction = quotient.regenerate(partialPrefn).reduce(partialPrefn);
        return new Pretension(reduction, partialPrefn);
    }

    public Pretension reduce()
    {
        List<Integer> reducedPrefn = new ArrayList<Integer>();
        reducedPrefn.addAll(prefn);
        BipartiteMonoid rbm = quotient.reduce(reducedPrefn).regenerate(reducedPrefn);
        return new Pretension(rbm, reducedPrefn);
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer(quotient.toString());
        buf.append("\n\nPhi =");
        for (int pv : prefn)
        {
            buf.append(' ');
            buf.append(quotient.monoid.elementToWord(pv));
        }
        return buf.toString();
    }

    private void buildLexLeastGames()
    {
        lexLeastGames = new Word[quotient.monoid.size()];
        sortedLLGames = new TreeMap<Word,Integer>();

        int[] heaps = new int[prefn.size()];
        int depth = -1;
        int[] elem = new int[prefn.size()];

        while (depth < prefn.size())
        {
            if (depth == -1)
            {
                // Found one!
                assert lexLeastGames[elem[0]] == null;
                lexLeastGames[elem[0]] = new Word(heaps);
                sortedLLGames.put(lexLeastGames[elem[0]], elem[0]);
                depth++;
            }
            else
            {
                heaps[depth]++;
                if (heaps[depth] > 0)
                {
                    elem[depth] = quotient.monoid.product(elem[depth], prefn.get(depth));
                }
                if (lexLeastGames[elem[depth]] == null)
                {
                    depth--;
                    if (depth >= 0)
                    {
                        elem[depth] = elem[depth+1];
                    }
                }
                else
                {
                    heaps[depth] = -1;
                    depth++;
                }
            }
        }
    }

    // Verification

    public Word lexLeastPFailure(List<int[]> singleHeapTransitions)
    {
        Word leastFailure = null;

        for (int x = 0; x < quotient.monoid.size(); x++)
        {
            for (int heap = 0; heap < prefn.size(); heap++)
            {
                if (quotient.isP(quotient.monoid.product(x, prefn.get(heap))))
                {
                    for (int target : singleHeapTransitions.get(heap))
                    {
                        if (quotient.isP(quotient.monoid.product(x, target)))
                        {
                            Word failure = lexLeastGames[x].addGen(heap);
                            if (leastFailure == null || failure.compareTo(leastFailure) < 0)
                            {
                                leastFailure = failure;
                            }
                        }
                    }
                }
            }
        }

        return leastFailure;
    }

    public Word lexLeastNFailure(List<int[]> singleHeapTransitions)
    {
        return lexLeastNFailure(singleHeapTransitions, Word.ZERO, null);
    }

    public Word lexLeastNFailure(List<int[]> singleHeapTransitions, Word minToCheck, Word maxToCheck)
    {
        List<List<Set<Integer>>> trlbs = new ArrayList<List<Set<Integer>>>(quotient.monoid.size());
        for (int x = 0; x < quotient.monoid.size(); x++)
        {
            trlbs.add(new ArrayList<Set<Integer>>());
        }

        return lexLeastNFailure(singleHeapTransitions, minToCheck, maxToCheck, trlbs);
    }

    public List<List<Set<Integer>>> buildTTLBs(List<int[]> singleHeapTransitions)
    {
        List<List<Set<Integer>>> trlbs = new ArrayList<List<Set<Integer>>>(quotient.monoid.size());
        for (int x = 0; x < quotient.monoid.size(); x++)
        {
            trlbs.add(new ArrayList<Set<Integer>>());
        }
        Word w = lexLeastNFailure(singleHeapTransitions, Word.ZERO, null, trlbs);
        if (w != null)
        {
            throw new IllegalArgumentException(w.toNumericString());
        }
        return trlbs;
    }

    private Word lexLeastNFailure(
        List<int[]> singleHeapTransitions,
        Word minToCheck,
        Word maxToCheck,
        List<List<Set<Integer>>> trlbs
        )
    {
        int[] heaps = new int[prefn.size()];
        int[] elem = new int[prefn.size()];
        List<Set<Integer>> transitions = new ArrayList<Set<Integer>>(prefn.size());
        transitions.addAll(Collections.<Set<Integer>>nCopies(prefn.size(), Collections.<Integer>emptySet()));

        int depth = -1;

        while (depth < prefn.size())
        {
            if (depth == -1)
            {
                //org.cgsuite.Context.getActiveContext().checkKernelState();
                if (!transitions.get(0).isEmpty() && !quotient.isP(elem[0]))
                {
                    boolean ok = false;
                    for (int target : transitions.get(0))
                    {
                        if (quotient.isP(target))
                        {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok)
                    {
                        return new Word(heaps);
                    }
                }
                depth++;
            }
            else
            {
                // "Recurse"
                heaps[depth]++;
                if (heaps[depth] > 0)
                {
                    /*
                    int[] lotsOfHeap = new int[depth+1];
                    lotsOfHeap[depth] = (1 << 16);
                    Word andLotsOfHeap = new Word(lotsOfHeap);
                    if (new Word(heaps).add(andLotsOfHeap).compareTo(minToCheck) < 0)
                    {
                        heaps[depth] = -1;
                        depth++;
                        continue;
                    }
                    */

                    if (singleHeapTransitions.get(depth).length == 0)
                    {
                        heaps[depth] = -1;
                        depth++;
                        continue;
                    }
                    if (maxToCheck != null && new Word(heaps).compareTo(maxToCheck) >= 0)
                    {
                        break;
                    }

                    Set<Integer> newTr = new HashSet<Integer>();
                    for (int target : transitions.get(depth))
                    {
                        newTr.add(quotient.monoid.product(target, prefn.get(depth)));
                    }
                    for (int target : singleHeapTransitions.get(depth))
                    {
                        newTr.add(quotient.monoid.product(elem[depth], target));
                    }

                    transitions.set(depth, newTr);
                    elem[depth] = quotient.monoid.product(elem[depth], prefn.get(depth));

                    if (!incorporateTransitionIntoList(trlbs.get(elem[depth]), newTr))
                    {
                        heaps[depth] = -1;
                        depth++;
                        continue;
                    }
                }

                depth--;
                if (depth >= 0)
                {
                    elem[depth] = elem[depth+1];
                    transitions.set(depth, transitions.get(depth+1));
                }
            }
        }

        return null;
    }

    private List<Integer> unsubsumedHeaps()
    {
        List<Integer> unsubsumedHeaps = new ArrayList<Integer>();
        BitSet alreadyDetected = new BitSet(quotient.monoid.size());
        for (int heap = 0; heap < prefn.size(); heap++)
        {
            if (!alreadyDetected.get(prefn.get(heap)))
            {
                alreadyDetected.set(prefn.get(heap));
                unsubsumedHeaps.add(heap);
            }
        }
        return unsubsumedHeaps;
    }

    private List<Integer> unsubsumedHeaps(List<int[]> singleHeapTransitions)
    {
        List<Integer> unsubsumedHeaps = new ArrayList<Integer>();
        List<List<Set<Integer>>> trlbs = new ArrayList<List<Set<Integer>>>();
        for (int x = 0; x < quotient.monoid.size(); x++)
        {
            trlbs.add(new ArrayList<Set<Integer>>());
        }
        for (int heap = 0; heap < prefn.size(); heap++)
        {
            Set<Integer> tr = new HashSet<Integer>();
            for (int target : singleHeapTransitions.get(heap))
            {
                tr.add(target);
            }
            if (incorporateTransitionIntoList(trlbs.get(prefn.get(heap)), tr))
            {
                unsubsumedHeaps.add(heap);
            }
        }
        return unsubsumedHeaps;
    }

    private static boolean incorporateTransitionIntoList(List<Set<Integer>> list, Set<Integer> tr)
    {
        assert !tr.isEmpty();

        for (int i = 0; i < list.size(); i++)
        {
            Set<Integer> oldTr = list.get(i);
            if (tr.containsAll(oldTr))
            {
                return false;
            }
            else if (oldTr.containsAll(tr))
            {
                list.remove(i);
                i--;
            }
        }
        list.add(tr);
        return true;
    }
}
