/*
 * MisereSolver.java
 *
 * Created on October 18, 2006, 4:50 PM
 * $Id: MisereSolver.java,v 1.6 2006/12/11 19:57:46 asiegel Exp $
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

import org.cgsuite.core.impartial.HeapRuleset;
import org.cgsuite.core.impartial.TakeAndBreak;
import scala.collection.immutable.IndexedSeq;
import scala.jdk.CollectionConverters;

import java.io.*;
import java.util.*;

public class MisereSolver
{
    private HeapRuleset rules;
    List<Integer> expansionHeaps;
    boolean writeXML, pd2Only;
    int normalPeriod, normalPpd;

    Pretension p;
    private List<int[]> singleHeapTransitions;
    private SortedSet<Word> certifiedLexLeast;
    private List<List<Set<Integer>>> ttLowerBounds;

    List<PrintStream> logs = new ArrayList<PrintStream>();

    static long timeCVerifying = 0, timePVerifying = 0, timeNVerifying = 0, timeRecalibrating = 0, timeCertifying = 0;

    public MisereSolver(HeapRuleset rules)
    {
        this.rules = rules;
        expansionHeaps = new ArrayList<Integer>();
        expansionHeaps.add(0);
        List<Integer> prefn = new ArrayList<Integer>();
        prefn.add(0);
        p = new Pretension(BipartiteMonoid.T0, prefn);
        singleHeapTransitions = new ArrayList<int[]>();
        singleHeapTransitions.add(new int[0]);
        certifiedLexLeast = new TreeSet<Word>();
    }

    public MisereSolver(InputStream msvInputStream) throws org.xml.sax.SAXException, IOException
    {
        MsvLoader loader = new MsvLoader(msvInputStream, true);
        this.normalPeriod = loader.normalPeriod;
        this.normalPpd = loader.normalPpd;
        this.rules = TakeAndBreak.apply(loader.code);
        this.expansionHeaps = loader.expansionHeaps;
        this.p = loader.pretension;
        singleHeapTransitions = buildAllTransitions(p.quotient.monoid, p.prefn);

        // Sanity check.
        if (p.lexLeastPFailure(singleHeapTransitions) != null ||
            p.lexLeastNFailure(singleHeapTransitions, Word.ZERO, null) != null)
        {
            throw new IOException("MSV File contains inconsistent data.");
        }
    }

    public HeapRuleset getRules()
    {
        return rules;
    }

    public Pretension currentPretension()
    {
        return p;
    }

    public Pretension partialPretension(int heap)
    {
        if (heap < 0)
        {
            throw new IllegalArgumentException("Invalid heap size!");
        }
        if (heap >= p.prefn.size())
        {
            throw new IllegalArgumentException("Computations only exist to heap " + heap + "!");
        }
        return p.reduceToHeap(heap);
    }

    public List<int[]> heapOptions(int heapSize) {
        List<IndexedSeq<org.cgsuite.core.Integer>> options =
                CollectionConverters.SeqHasAsJava(rules.heapOptions(org.cgsuite.core.Integer.apply(heapSize)).toSeq()).asJava();
        List<int[]> result = new ArrayList<>();
        for (IndexedSeq<org.cgsuite.core.Integer> option : options) {
            int[] array = new int[option.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = option.apply(i).intValue();
            }
            result.add(array);
        }
        return result;
    }

    public void next()
    {
        if (Thread.interrupted()) throw new TimeoutException();

        long time;
        int nextHeap = p.prefn.size();
        List<int[]> options = heapOptions(nextHeap);

        if (options.size() == 0)
        {
            // No options!  This is easy.
            p.prefn.add(0);
            singleHeapTransitions.add(new int[0]);
            return;
        }

        {
            time = System.nanoTime();
            int x = checkConservativeExtension(nextHeap);
            timeCVerifying += (System.nanoTime() - time);
            if (x != -1)
            {
                p.prefn.add(x);
                singleHeapTransitions.add(buildTransitionsForHeap(p.quotient.monoid, p.prefn, nextHeap));
                return;
            }
        }

        logln("\n\n--- Presentation Changing at Heap " + nextHeap + " ---\n");

        if (writeXML)
        {
            writeXML();
        }

        List<Integer> trialPrefn = new ArrayList<Integer>();
        trialPrefn.addAll(p.prefn);
        trialPrefn.add(0);
        trialPrefn.set(nextHeap, p.quotient.monoid.size());
        singleHeapTransitions.add(buildTransitionsForHeap(p.quotient.monoid, trialPrefn, nextHeap));

        Pretension trialP;

        {
            CommutativeMonoid trialMonoid = new DirectSumMonoid(p.quotient.monoid, ExplicitFiniteMonoid.cyclicMonoid(2, 2));
            BitSet trialPPortion = new BitSet(trialMonoid.size());
            markPPortion(trialMonoid, trialPrefn, singleHeapTransitions, null, trialPPortion);
            trialP = new Pretension(new BipartiteMonoid(trialMonoid, trialPPortion), trialPrefn).reduce();
        }

        Word leastFailure = Word.delta(nextHeap);

        while (leastFailure != null)
        {
            time = System.nanoTime();
            Word pFailure = trialP.lexLeastPFailure(singleHeapTransitions);
            timePVerifying += (System.nanoTime() - time);
            time = System.nanoTime();
            Word nFailure = trialP.lexLeastNFailure(singleHeapTransitions, leastFailure, pFailure);
            timeNVerifying += (System.nanoTime() - time);

            // Since we took maxToCheck = pFailure, we know that IF nFailure is non-null,
            // then it's less than pFailure.
            leastFailure = (nFailure == null ? pFailure : nFailure);
            log("Tried: |Q| = " + trialP.quotient.monoid.size() + ", |P| = " + trialP.quotient.pPortionSize() + "; ");
            if (leastFailure == null)
            {
                logln("Success!");
            }
            else
            {
                logln("Failure = " + leastFailure.toNumericString());
                if (Thread.interrupted()) throw new TimeoutException();
                /*
                int lb = certifiedLowerBound(trialP, leastFailure);
                timeCertifying += (System.nanoTime() - time);
                if (lb > 800)
                {
                    throw new LargeQuotientException(lb);
                }
                */
                // Recalibrate.
                time = System.nanoTime();
                trialP = recalibrate(trialP, nextHeap, leastFailure);
                timeRecalibrating += (System.nanoTime() - time);
                singleHeapTransitions = buildAllTransitions(trialP.quotient.monoid, trialP.prefn);
            }
        }

        // Just in case:
        assert trialP.lexLeastPFailure(singleHeapTransitions) == null;
        assert trialP.lexLeastNFailure(singleHeapTransitions, Word.ZERO, null) == null;

        expansionHeaps.add(nextHeap);
        p = trialP;

        if (writeXML)
        {
            writeXML();
        }
    }

    private Set<Integer> buildTransitionSetForHeap(CommutativeMonoid monoid, List<Integer> prefn, int heap)
    {
        List<int[]> options = heapOptions(heap);
        Set<Integer> transitionSet = new HashSet<Integer>();
        for (int[] option : options)
        {
            int x = 0;
            for (int component : option)
            {
                x = monoid.product(x, prefn.get(component));
            }
            transitionSet.add(x);
        }
        return transitionSet;
    }

    private int[] buildTransitionsForHeap(CommutativeMonoid monoid, List<Integer> prefn, int heap)
    {
        Set<Integer> transitionSet = buildTransitionSetForHeap(monoid, prefn, heap);
        int index = 0;
        int[] transitions = new int[transitionSet.size()];
        for (int x : transitionSet)
        {
            transitions[index++] = x;
        }
        return transitions;
    }

    private List<int[]> buildAllTransitions(CommutativeMonoid monoid, List<Integer> prefn)
    {
        List<int[]> allTransitions = new ArrayList<int[]>();
        for (int heap = 0; heap < prefn.size(); heap++)
        {
            allTransitions.add(buildTransitionsForHeap(monoid, prefn, heap));
        }
        return allTransitions;
    }

    private int checkConservativeExtension(int nextHeap)
    {
        if (ttLowerBounds == null)
        {
            ttLowerBounds = p.buildTTLBs(singleHeapTransitions);
        }

        Set<Integer> tr = buildTransitionSetForHeap(p.quotient.monoid, p.prefn, nextHeap);
        assert !tr.isEmpty();
        boolean nextHeapIsP = true;
        for (int z : tr)
        {
            if (p.quotient.isP(z))
            {
                nextHeapIsP = false;
                break;
            }
        }

        // Lightning-fast initial check, using the Mex Interpolation Principle.

        for (int x = 0; x < p.quotient.monoid.size(); x++)
        {
            if (Thread.interrupted()) throw new TimeoutException();
            if (p.quotient.isP(x) == nextHeapIsP &&
                p.quotient.meximalSet(x).containsAll(tr))
            {
                for (Set<Integer> trlb : ttLowerBounds.get(x))
                {
                    if (tr.containsAll(trlb))
                    {
                        return x;
                    }
                }
            }
        }

        // Broader, slower check.

        List<Integer> trialPrefn = new ArrayList<Integer>(nextHeap+1);
        trialPrefn.addAll(p.prefn);
        trialPrefn.add(0);
        List<int[]> trialSHTs = new ArrayList<int[]>(nextHeap+1);
        trialSHTs.addAll(singleHeapTransitions);
        trialSHTs.add(buildTransitionsForHeap(p.quotient.monoid, p.prefn, nextHeap));
        Pretension trialP = new Pretension(p.quotient, trialPrefn);

        for (int x = 0; x < p.quotient.monoid.size(); x++)
        {
            trialPrefn.set(nextHeap, x);
            if (p.quotient.isP(x) == nextHeapIsP &&
                p.quotient.meximalSet(x).containsAll(tr) &&
                trialP.lexLeastNFailure(trialSHTs, Word.delta(nextHeap), null) == null)
            {
                ttLowerBounds = null;
                return x;
            }
        }

        ttLowerBounds = null;
        return -1;
    }

    private static void markPPortion(CommutativeMonoid monoid, List<Integer> prefn, List<int[]> singleHeapTransitions, Pretension defaultMarker, BitSet pPortion)
    {
        BitSet marked = new BitSet();
        int[] heaps = new int[prefn.size()];
        int[] elem = new int[prefn.size()];
        List<Set<Integer>> transitions = new ArrayList<Set<Integer>>(prefn.size());
        transitions.addAll(Collections.<Set<Integer>>nCopies(prefn.size(), Collections.<Integer>emptySet()));

        int depth = -1;

        while (depth < prefn.size())
        {
            if (depth == -1)
            {
                assert !marked.get(elem[0]);
                if (transitions.get(0).isEmpty())
                {
                    // Special case: terminal!
                    assert elem[0] == 0 : elem[0];
                }
                else
                {
                    boolean p = true;
                    for (int target : transitions.get(0))
                    {
                        assert marked.get(target);
                        if (pPortion.get(target))
                        {
                            p = false;
                            break;
                        }
                    }
                    if (p)
                    {
                        pPortion.set(elem[0]);
                    }
                }
                marked.set(elem[0]);
                depth++;
            }
            else
            {
                // "Recurse"
                heaps[depth]++;
                if (heaps[depth] > 0)
                {
                    int newElem = monoid.product(elem[depth], prefn.get(depth));
                    if (marked.get(newElem))
                    {
                        heaps[depth] = -1;
                        depth++;
                        continue;
                    }
                    else
                    {
                        Set<Integer> newTr = new HashSet<Integer>();
                        for (int target : transitions.get(depth))
                        {
                            newTr.add(monoid.product(target, prefn.get(depth)));
                        }
                        for (int target : singleHeapTransitions.get(depth))
                        {
                            newTr.add(monoid.product(elem[depth], target));
                        }

                        elem[depth] = newElem;
                        transitions.set(depth, newTr);
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
    }

    private static void markPPortion(
        CommutativeMonoid monoid,
        List<Integer> prefn,
        List<int[]> singleHeapTransitions,
        Pretension defaultMarker,
        BitSet pPortion,
        BitSet marked,
        Word currentWord,
        int currentElem,
        Set<Integer> tr,
        int depth
        )
    {
        if (depth == 0)
        {
            assert !marked.get(currentElem);

            if (currentWord.equals(Word.ZERO))
            {
                // Special case: terminal!
                assert currentElem == 0 : currentElem;
            }
            else
            {
                boolean p = true;
                for (int target : tr)
                {
                    assert marked.get(target);
                    if (pPortion.get(target))
                    {
                        p = false;
                        break;
                    }
                }
                if (p)
                {
                    pPortion.set(currentElem);
                }
            }

            marked.set(currentElem);
        }
        else
        {
            while (true)
            {
                markPPortion(monoid, prefn, singleHeapTransitions, defaultMarker, pPortion, marked, currentWord, currentElem, tr, depth-1);

                if (singleHeapTransitions.get(depth).length == 0)
                {
                    break;
                }

                currentWord = currentWord.addGen(depth);

                Set<Integer> newTr = new HashSet<Integer>();
                for (int target : tr)
                {
                    newTr.add(monoid.product(target, prefn.get(depth)));
                }
                for (int target : singleHeapTransitions.get(depth))
                {
                    newTr.add(monoid.product(currentElem, target));
                }

                tr = newTr;
                currentElem = monoid.product(currentElem, prefn.get(depth));

                if (marked.get(currentElem))
                {
                    break;
                }
            }
        }
    }
    /*
    private Pretension recalibrate(Pretension trialP, int nextHeap, Word leastFailure)
    {
        boolean shouldBeP = !trialP.quotient.isP(trialP.prefn.get(nextHeap));

        System.out.println("Recalibrating: " + leastFailure.toNumericString() + " (should be " + shouldBeP + ")");

        // This can be improved, of course.
        List<Integer> withoutNextHeap = new ArrayList<Integer>();
        withoutNextHeap.addAll(trialP.prefn);
        withoutNextHeap.remove(nextHeap);
        Pretension expandedP = new Pretension(trialP.quotient.regenerate(withoutNextHeap), withoutNextHeap);
        System.out.println("X: " + expandedP);

        int oldGens = expandedP.quotient.monoid.getNumGenerators();
        int oldSize = expandedP.quotient.monoid.size();

        Word leastFailureWONextHeap;
        for (leastFailureWONextHeap = leastFailure;
             leastFailureWONextHeap.getExponent(nextHeap) > 0;
             leastFailureWONextHeap = leastFailureWONextHeap.removeGen(nextHeap));

        List<Integer> heapsInvolved = leastFailureWONextHeap.nonzeroGens();
        List<Word> subwords = leastFailureWONextHeap.subwords();
        int[][] recalActions = new int[oldGens + heapsInvolved.size()][oldSize + subwords.size()];
        for (int i = 0; i < heapsInvolved.size(); i++)
        {
            int heap = heapsInvolved.get(i);
            int oldPre = expandedP.prefn.get(heap);
            for (int x = 0; x < subwords.size(); x++)
            {
                Word gx = subwords.get(x).addGen(heap);
                if (leastFailureWONextHeap.contains(gx))
                {
                    recalActions[i][x] = subwords.indexOf(gx);
                    assert recalActions[i][x] != -1;
                }
                else
                {
                    recalActions[i][x] = subwords.size() + expandedP.phi(gx);
                }
            }
            for (int x = 0; x < oldSize; x++)
            {
                recalActions[i][subwords.size() + x] = subwords.size() + expandedP.quotient.monoid.product(oldPre, x);
            }
        }
        for (int gen = 0; gen < oldGens; gen++)
        {
            int g = expandedP.quotient.monoid.getGeneratorElement(gen);
            for (int x = 0; x < subwords.size(); x++)
            {
                recalActions[heapsInvolved.size() + gen][x] = subwords.size() + expandedP.quotient.monoid.product(g, expandedP.phi(subwords.get(x)));
            }
            for (int x = 0; x < oldSize; x++)
            {
                recalActions[heapsInvolved.size() + gen][subwords.size() + x] = subwords.size() + expandedP.quotient.monoid.product(g, x);
            }
        }

        ExplicitFiniteMonoid recalMonoid = new ExplicitFiniteMonoid(recalActions);
        BitSet recalPPortion = new BitSet();
        for (int x = 0; x < subwords.size(); x++)
        {
            recalPPortion.set(x, expandedP.quotient.isP(expandedP.phi(subwords.get(x))));
        }
        for (int x = 0; x < oldSize; x++)
        {
            recalPPortion.set(subwords.size() + x, expandedP.quotient.isP(x));
        }
        List<Integer> recalPrefn = new ArrayList<Integer>();
        for (int heap = 0; heap < nextHeap; heap++)
        {
            if (rules.allOptions(heap).isEmpty())
            {
                recalPrefn.add(0);
            }
            else if (heapsInvolved.contains(heap))
            {
                recalPrefn.add(subwords.indexOf(Word.delta(heap)));
            }
            else
            {
                recalPrefn.add(subwords.size() + expandedP.prefn.get(heap));
            }
        }
        BipartiteMonoid recalBM = new BipartiteMonoid(recalMonoid, recalPPortion).regenerate(recalPrefn);

        System.out.println("Recal: " + recalBM);
        System.out.println(recalPrefn);
        assert recalBM.monoid.verifyCommutative();
        assert recalBM.monoid.verifyAssociative();

        CommutativeMonoid dsMonoid = new DirectSumMonoid(recalBM.monoid, ExplicitFiniteMonoid.cyclicMonoid(2, 4));
        recalPrefn.add(recalBM.monoid.size());
        List<int[]> allTransitions = buildAllTransitions(dsMonoid, recalPrefn);
        BitSet dsPPortion = new BitSet(dsMonoid.size());
        markPPortion(dsMonoid, recalPrefn, allTransitions, null, null, dsPPortion);

        Pretension recalP = new Pretension(new BipartiteMonoid(dsMonoid, dsPPortion), recalPrefn).reduce();

        System.out.println("Ready to Return: " + recalP);
        assert recalP.quotient.isP(recalP.phi(leastFailure)) == shouldBeP : "Recalibration failed!";

        return recalP;
    }
    */
    private Pretension recalibrate(Pretension trialP, int nextHeap, Word leastFailure)
    {
        Pretension recalP = null;
        boolean shouldBeP = !trialP.quotient.isP(trialP.phi(leastFailure));

        Word lexLeast = trialP.lexLeastGames[trialP.phi(leastFailure)];
        List<Integer> heapsInvolved = new ArrayList<Integer>();
        for (int heap = 0; heap <= nextHeap; heap++)
        {
            if (leastFailure.getExponent(heap) > 0)//lexLeast.getExponent(heap))
            {
                heapsInvolved.add(heap);
            }
        }
        assert !heapsInvolved.isEmpty() : "" + leastFailure.toNumericString() + " " + lexLeast.toNumericString();

        boolean success = false;
        recalibrationLoop: for (int nHeaps = 1; nHeaps <= heapsInvolved.size(); nHeaps++)
        {
            List<List<Integer>> combinations = combinations(heapsInvolved, nHeaps);
            for (List<Integer> heaps : combinations)
            {
                if (Thread.interrupted()) throw new TimeoutException();
                List<Integer> recalPrefn = new ArrayList<Integer>();
                recalPrefn.addAll(trialP.prefn);
                for (int heap : heaps)
                {
                    recalPrefn.set(heap, 0);
                }

                BipartiteMonoid regen = trialP.quotient.regenerate(recalPrefn);
                CommutativeMonoid[] components = new CommutativeMonoid[1 + heaps.size()];
                components[0] = regen.monoid;
                for (int i = 0; i < heaps.size(); i++)
                {
                    int index = Math.max(trialP.quotient.monoid.indexOf(trialP.prefn.get(heaps.get(i))), leastFailure.getExponent(heaps.get(i))-1);
                    int period = (pd2Only ? 2 : Math.max(2, 2 * ((index-4) / 2)));
                    components[i+1] = ExplicitFiniteMonoid.cyclicMonoid(period, index);
                }
                DirectSumMonoid dsMonoid = new DirectSumMonoid(components);

                for (int i = 0; i < heaps.size(); i++)
                {
                    recalPrefn.set(heaps.get(i), dsMonoid.getMultiplierOfComponent(i+1));
                }

                List<int[]> allTransitions = buildAllTransitions(dsMonoid, recalPrefn);
                BitSet dsPPortion = new BitSet(dsMonoid.size());
                markPPortion(dsMonoid, recalPrefn, allTransitions, trialP, dsPPortion);

                Pretension testRecalP = new Pretension(new BipartiteMonoid(dsMonoid, dsPPortion), recalPrefn).reduce();

                if (testRecalP.quotient.isP(testRecalP.phi(leastFailure)) == shouldBeP)
                {
                    // It worked!
                    recalP = testRecalP;
                    break recalibrationLoop;
                }
            }
        }

        if (recalP == null)
        {
            throw new RuntimeException("Failed to recalibrate!\n\n" + trialP + "\n\n" + leastFailure.toNumericString());
        }
        return recalP;
    }

    private static <T> List<List<T>> combinations(List<T> values, int size)
    {
        List<List<T>> combinations = new ArrayList<List<T>>();
        buildCombinations(combinations, values, size, values.size()-1, new ArrayList<T>(size));
        return combinations;
    }

    private static <T> void buildCombinations(List<List<T>> subsets, List<T> values, int size, int depth, List<T> included)
    {
        if (size-included.size() <= depth)
        {
            // Continue without adding this element.
            buildCombinations(subsets, values, size, depth-1, included);
        }
        included.add(values.get(depth));
        if (included.size() == size)
        {
            List<T> copy = new ArrayList<T>(size);
            copy.addAll(included);
            Collections.reverse(copy);
            subsets.add(copy);
        }
        else
        {
            buildCombinations(subsets, values, size, depth-1, included);
        }
        included.remove(included.size()-1);
    }

    // This is a slow ugly n^3 algorithm
    private int certifiedLowerBound(Pretension p, Word leastFailure)
    {
        //System.out.print("[");
        for (Map.Entry<Word,Integer> a : p.sortedLLGames.headMap(leastFailure).entrySet())
        {
            if (!certifiedLexLeast.contains(a.getKey()))
            {
                boolean allCertified = true;
                for (Map.Entry<Word,Integer> b : p.sortedLLGames.headMap(a.getKey()).entrySet())
                {
                    // Make sure a is distinguishable from b.
                    boolean certified = false;
                    for (Map.Entry<Word,Integer> x : p.sortedLLGames.headMap(leastFailure.subtract(a.getKey())).entrySet())
                    {
                        //assert a.getKey().add(x.getKey()).compareTo(leastFailure) < 0;
                        if (p.quotient.isP(p.quotient.monoid.product(a.getValue(), x.getValue())) !=
                            p.quotient.isP(p.quotient.monoid.product(b.getValue(), x.getValue())))
                        {
                            certified = true;
                            break;
                        }
                    }
                    if (!certified)
                    {
                        allCertified = false;
                        break;
                    }
                }
                if (allCertified)
                {
                    certifiedLexLeast.add(a.getKey());
                }
            }
        }
        //System.out.print("" + certifiedLexLeast.size() + "/" + p.quotient.monoid.size() + "] ");
        return certifiedLexLeast.size();
    }

    void writeXML()
    {
        try
        {
            File file = new File
                ("q-" + rules.toString() + ".msv");
            log("Writing MSV File " + file + " . . . ");
            java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(new FileOutputStream(file));
            zos.setLevel(9);
            zos.putNextEntry(new java.util.zip.ZipEntry("quotient.xml"));
            PrintStream out = new PrintStream(zos);
            out.println("<MisereSolver version=\"1.0\">");
            out.println("  <Presentation rules=\"" + rules.toString() + "\" toHeap=\"" + (p.prefn.size() - 1) + "\">");
            out.println("    <Quotient numGens=\"" + p.quotient.monoid.getNumGenerators() + "\">");
            out.println("      <Actions>" + Arrays.deepToString(((ExplicitFiniteMonoid) p.quotient.monoid).actions) + "</Actions>");
            out.println("      <PPortion>" + p.quotient.pPortion + "</PPortion>");
            out.println("    </Quotient>");
            out.println("    <PretendingFunction>" + p.prefn + "</PretendingFunction>");
            out.println("  </Presentation>");
            out.println("  <ExpansionHeaps>" + expansionHeaps + "</ExpansionHeaps>");
            if (normalPeriod != 0)
            {
                out.println("  <NormalPeriod>" + normalPeriod + "</NormalPeriod>");
                out.println("  <NormalPpd>" + normalPpd + "</NormalPpd>");
            }
            out.println("</MisereSolver>");
            out.close();
            logln("Done!");
        }
        catch (IOException exc)
        {
        }
    }

    private void log(String str)
    {
        for (PrintStream log : logs)
        {
            log.print(str);
        }
    }

    private void logln(String str)
    {
        for (PrintStream log : logs)
        {
            log.println(str);
        }
    }

    private void logln()
    {
        for (PrintStream log : logs)
        {
            log.println();
        }
    }
}

class LargeQuotientException extends RuntimeException
{
    int size;

    LargeQuotientException(int size)
    {
        this.size = size;
    }
}
