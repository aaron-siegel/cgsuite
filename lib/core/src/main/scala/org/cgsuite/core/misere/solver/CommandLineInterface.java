/*
 * CommandLineInterface.java
 *
 * Created on October 19, 2006, 11:47 AM
 * $Id: CommandLineInterface.java,v 1.6 2007/04/09 23:51:51 asiegel Exp $
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

import org.cgsuite.core.ImpartialGame;
import org.cgsuite.core.impartial.*;
import org.cgsuite.lang.CgscriptClass;
import scala.Option;
import scala.collection.mutable.AnyRefMap;
import scala.jdk.CollectionConverters;

import java.util.ArrayList;
import java.util.List;

public class CommandLineInterface
{
    private static long timeoutTime = 0L;

    private CommandLineInterface()
    {
    }

    public static void main(String[] args) throws Exception
    {
        Thread.currentThread().setPriority(3);
        CommandLineOptions clo = createCLO();

        try
        {
            clo.parse(args);
        }
        catch (IllegalArgumentException exc)
        {
            System.out.println("Illegal argument: " + exc.getMessage());
            System.out.println("For help type:  java -jar misere.jar -h");
            System.exit(-1);
            return;
        }

        if (clo.getFreeArguments().size() == 0 || clo.isSpecified("help"))
        {
            printHelpMessage(clo);
        }

        CgscriptClass.Object().ensureInitialized();

        long time = System.nanoTime();

        if (clo.isSpecified("search4D"))
        {
            search4D(clo.getFreeArguments().get(0), clo.isSpecified("persist"), Integer.parseInt(clo.getOptionArguments("search4D").get(0)));
        }
        else
        {
            HeapRuleset ruleset = stringsToRuleset(clo.getFreeArguments());
            if (ruleset == null) {
                System.out.println("There were errors evaluating the command-line arguments.");
                System.out.println("MisereSolver expects one of the following:");
                System.out.println("    A single take-and-break code; or");
                System.out.println("    A single CGScript expression that evaluates to a `HeapRuleset`; or");
                System.out.println("    A sequence of CGScript expressions that evaluate to `ImpartialGame`s.");
                System.out.println("For help type:  java -jar misere.jar -h");
                System.exit(-1);
                return;
            }
            try
            {
                runMisereSolver(
                    ruleset,
                    false,
                    clo.isSpecified("analyze"),
                    clo.isSpecified("persist"),
                    clo.isSpecified("pd2only"),
                    clo.isSpecified("maxheap") ? Integer.parseInt(clo.getOptionArguments("maxheap").get(0)) : Integer.MAX_VALUE,
                    clo.isSpecified("timeout") ? Integer.parseInt(clo.getOptionArguments("timeout").get(0)) : 0
                    );
            }
            catch (TimeoutException exc)
            {
                System.out.println("\n\nTimeout!");
            }
        }

        System.out.println(
            "\n=== Performance Data ===\n\n" +
            timeInSeconds(System.nanoTime() - time) + " sec\n" +
            timeInSeconds(MisereSolver.timeCVerifying) + " sec c-verifying\n" +
            timeInSeconds(MisereSolver.timePVerifying) + " sec p-verifying\n" +
            timeInSeconds(MisereSolver.timeNVerifying) + " sec n-verifying\n" +
            timeInSeconds(MisereSolver.timeCertifying) + " sec certifying\n" +
            timeInSeconds(MisereSolver.timeRecalibrating) + " sec recalibrating\n"
            );
        System.gc(); System.gc(); System.gc(); System.gc();
        System.out.println("Used " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + " MB of memory.\n");
    }

    private static HeapRuleset stringsToRuleset(List<String> strings)
    {
        // If it's just one string, first try parsing as a take-and-break code.
        if (strings.size() == 1) {
            try {
                return TakeAndBreak.apply(strings.get(0));
            } catch (Exception exc) {
            }
        }

        // Now parse each argument as a CGScript expression.
        boolean success = true;

        List<ImpartialGame> games = new ArrayList<>();
        for (String str : strings)
        {
            Option<Object> result = null;
            try {
                result = org.cgsuite.lang.System.evaluateObjOrException(str, new AnyRefMap<>());
            } catch (Exception exc) {
                System.out.println("Invalid CGScript expression: " + str);
                exc.printStackTrace();
                success = false;
            }
            if (result != null) {
                if (strings.size() == 1 && result.isDefined() && result.get() instanceof HeapRuleset) {
                    return (HeapRuleset) result.get();
                } else if (result.isDefined() && result.get() instanceof ImpartialGame) {
                    games.add((ImpartialGame) result.get());
                } else {
                    if (strings.size() == 1) {
                        System.out.println("CGScript expression does not evaluate to a HeapRuleset or ImpartialGame: " + str);
                    } else {
                        System.out.println("CGScript expression does not evaluate to an ImpartialGame: " + str);
                    }
                    success = false;
                }
            }
        }

        if (success) {
            return Linearization.apply(CollectionConverters.ListHasAsScala(games).asScala().toSeq());
        } else {
            return null;
        }
    }

    private static MisereSolver runMisereSolver
        (HeapRuleset rules, boolean quiet, boolean analyze, boolean writeMSV, boolean pd2Only, int maxHeap, int timeoutInSec)
        throws Exception
    {
        if (rules instanceof Linearization)
        {
            maxHeap = Math.min(maxHeap, ((Linearization) rules).positions().size() - 1);
        }
        TBCode code = (rules instanceof TakeAndBreak ? ((TakeAndBreak) rules).tbCode() : null);
        PeriodicityChecker apchecker = (code == null ? null : code.periodicityChecker());
        MisereSolver ms = null;
        if (code != null && writeMSV)
        {
            // See if the MSV file exists already.
            java.io.File msvFile = new java.io.File("q-" + code + ".msv");
            if (msvFile.exists())
            {
                if (!quiet)
                {
                    System.out.println("\nFound MSV File: " + msvFile);
                    System.out.print("Loading . . . ");
                }
                java.io.FileInputStream in = new java.io.FileInputStream(msvFile);
                ms = new MisereSolver(in);
                in.close();
                if (!quiet)
                {
                    System.out.println("Done!\n\n=== Loaded Presentation at Heap " + (ms.p.prefn.size()-1) + " ===\n");
                    printPresentation(ms, analyze);
                }
            }
        }
        if (ms == null)
        {
            ms = new MisereSolver(rules);
        }
        if (!quiet)
        {
            ms.logs.add(System.out);
        }
        ms.writeXML = writeMSV;
        ms.pd2Only = pd2Only;

        if (timeoutInSec == 0)
        {
            timeoutTime = 0L;
        }
        else
        {
            timeoutTime = System.currentTimeMillis() + 1000L * timeoutInSec;
        }

        int currSize = ms.p.quotient.monoid.size();
        Periodicity apinfo = null;

        try
        {
            while (ms.p.prefn.size() <= maxHeap)
            {
                int heap = ms.p.prefn.size();
                if (apchecker != null)
                {
                    apinfo = apchecker.checkSequence(ms.p.prefn);
                    if (apinfo != null && apinfo.saltus() == 0)
                    {
                        break;
                    }
                }
                ms.next();
                CommutativeMonoid monoid = ms.p.quotient.monoid;
                if (!quiet)
                {
                    if (monoid.size() == currSize)
                    {
                        if (ms.getRules() instanceof TakeAndBreak)
                        {
                            System.out.print(monoid.elementToWord(ms.p.prefn.get(heap)));
                            System.out.print(' ');
                        }
                        else
                        {
                            System.out.printf(
                                "\n%10s <- %s",
                                monoid.elementToWord(ms.p.prefn.get(heap)),
                                ((Linearization) ms.getRules()).positions().apply(heap)
                                );
                        }
                        System.out.flush();
                    }
                    else
                    {
                        System.out.println("\n\n=== Presentation Changed at Heap " + heap + " ===\n");
                        printPresentation(ms, analyze);
                    }
                }
                currSize = monoid.size();
            }
        }
        catch (LargeQuotientException exc)
        {
            System.out.print("\n\nQuotient size exceeded!  Size: " + exc.size);
        }
        catch (TimeoutException exc)
        {
            if (writeMSV)
            {
                ms.writeXML();
            }
            throw exc;
        }
        if (!quiet)
        {
            System.out.println("\n");
        }
        if (writeMSV)
        {
            ms.writeXML();
            if (!quiet)
            {
                System.out.println();
            }
        }
        if (!quiet && code != null)
        {
            if (apinfo == null || apinfo.saltus() != 0)
            {
                System.out.printf("=== No Solution Found by Heap %d ===\n\n", ms.p.prefn.size()-1);
            }
            else
            {
                System.out.printf(
                    "=== Solution Found at Heap %d ===\n\n" +
                    "Period    : %6d\n" +
                    "Preperiod : %6d\n" +
                    "Q-Size    : %6d\n" +
                    "P-Size    : %6d\n" +
                    "Tame?     : %6s\n",
                    ms.p.prefn.size() - 1,
                    apinfo.period(),
                    apinfo.preperiod(),
                    ms.p.quotient.monoid.size(),
                    ms.p.quotient.pPortionSize(),
                    ms.p.quotient.isTame() ? "Yes" : "No"
                    );
            }
            System.out.printf("StdForm   : %6s\n", code.standardForm());
            java.util.BitSet kernel = ms.p.quotient.monoid.kernel();
            for (int heap = ms.p.prefn.size() - 1; heap >= 0; heap--)
            {
                if (!kernel.get(ms.p.prefn.get(heap)))
                {
                    System.out.printf("LastRogue : %6d\n", heap);
                    break;
                }
            }
        }

        return ms;
    }

    private static void printHelpMessage(CommandLineOptions clo)
    {
        System.out.println();
        System.out.println("MisereSolver " + org.cgsuite.lang.System.version());
        System.out.println("Usage: java -jar misere.jar [options] code");
        System.out.println("\ncode may be either: a take-and-break code (such as \"0.77\"); or");
        System.out.println("                    a canonical form (such as \"(2//321)/\")");
        System.out.println("\nCommand-line options:");
        clo.printHelpMessage(System.out);
        System.out.println();
        System.exit(0);
    }

    private static void printPresentation(MisereSolver ms, boolean analyze)
    {
        CommutativeMonoid monoid = ms.p.quotient.monoid;
        System.out.println(ms.p.quotient.isTame() ? ("(Q,P) = T" + log2(ms.p.quotient.size())) : ms.p.quotient);
        System.out.println("\n|Q| = " + monoid.size() + "; |P| = " + ms.p.quotient.pPortionSize());
        if (analyze)
        {
            System.out.println("\n" + monoid.countMdClasses() + " mutual divisibility classes.\n");
            System.out.println("Idempotent  |MaxSG| |Arch|  Lower Covers              MaxSG P-Pos");
            System.out.println("----------- ------- ------- ------------------------- -------------------------");
            for (int idemp : monoid.idempotents())
            {
                java.util.BitSet maxSG = monoid.mutualDivisibilityClass(idemp);
                int maxSGSize = maxSG.cardinality();
                maxSG.and(ms.p.quotient.pPortion);
                System.out.printf(
                    "%-11s %6d  %6d  %-25s %-25s\n",
                    monoid.elementToWord(idemp).toString() + (idemp == monoid.kernelIdentity() ? " *" : ""),
                    maxSGSize,
                    monoid.archimedeanComponent(idemp).cardinality(),
                    compactStringForWordList(monoid.elementListToWordList(monoid.idempotentLowerCovers(idemp))),
                    compactStringForWordList(monoid.subsetToWordList(maxSG))
                    );
            }
        }
        if (ms.getRules() instanceof TakeAndBreak)
        {
            System.out.print("\nPhi = ");
            for (int pv : ms.p.prefn)
            {
                System.out.print(monoid.elementToWord(pv));
                System.out.print(' ');
            }
        }
        else
        {
            assert ms.getRules() instanceof Linearization;
            for (int i = 0; i < ms.p.prefn.size(); i++)
            {
                System.out.printf(
                    "\n%10s <- %s",
                    monoid.elementToWord(ms.p.prefn.get(i)),
                    ((Linearization) ms.getRules()).positions().apply(i)
                    );
            }
        }
        System.out.flush();
    }

    private static String compactStringForWordList(java.util.List<Word> list)
    {
        StringBuffer buf = new StringBuffer();
        for (Word w : list)
        {
            buf.append(w);
            buf.append(' ');
        }
        if (buf.length() > 0)
        {
            buf.deleteCharAt(buf.length()-1);
        }
        return buf.toString();
    }

    private static int log2(int x)
    {
        if (x <= 0)
        {
            throw new IllegalArgumentException("x <= 0");
        }
        else if (x == 1)
        {
            return 0;
        }
        else
        {
            return 1 + log2(x >> 1);
        }
    }

    private static String timeInSeconds(long nanos)
    {
        return String.format("%9d.%03d", nanos / 1000000000L, (nanos % 1000000000L) / 1000000L);
    }

    private static CommandLineOptions createCLO()
    {
        CommandLineOptions clo = new CommandLineOptions(Integer.MAX_VALUE);
        clo.addOption("analyze", "a", 0, "Print monoid analysis as each quotient is generated");
        clo.addOption("help", "h", 0, "Print this message and exit");
        clo.addOption("persist", "p", 0, "Read and write .MSV file for this quotient");
        clo.addOption("maxheap", 1, "Stop computing after specified heap");
        clo.addOption("pd2only", 0, "Assume all quotient elements have period at most 2\n" +
                                    "(This can speed up MisereSolver but will cause it to fail\n" +
                                    "on quotients with large-period elements)");
        clo.addOption("search4D", 1, "Search the space of four-digit octals, starting at\n" +
                                     "the specified octal code");
        clo.addOption("timeout", 1, "Timeout after specified duration (in seconds)");
        return clo;
    }

    private static void search4D(String first, boolean writeMSV, int timeoutInSec) throws Exception
    {
        java.util.Set<TBCode> stdForms = getStdFormsForThreeDigitOctals();
        search4D("0.", first, writeMSV, timeoutInSec, stdForms, 4);
        search4D("4.", first, writeMSV, timeoutInSec, stdForms, 3);
    }

    private static java.util.Set<TBCode> getStdFormsForThreeDigitOctals()
    {
        java.util.Set<TBCode> stdForms = new java.util.HashSet<TBCode>();
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                for (int z = (x + y == 0 ? 1 : 0); z < 8; z++)
                {
                    stdForms.add(TakeAndBreak.apply("0." + x + y + z).tbCode().standardForm());
                }
                stdForms.add(TakeAndBreak.apply("4." + x + y).tbCode().standardForm());
            }
        }
        return stdForms;
    }
    private static void search4D(String str, String first, boolean writeMSV, int timeoutInSec, java.util.Set<TBCode> stdForms, int n) throws Exception
    {
        if (n == 0)
        {
            TBCode code = TBCode.apply(str);
            if (str.compareTo(first) >= 0 && !stdForms.contains(code.standardForm()))
            {
                searchSolve(code, writeMSV, timeoutInSec);
            }
            stdForms.add(code.standardForm());
        }
        else
        {
            for (int d = (n == 1 ? 1 : 0); d < 8; d++)
            {
                search4D(str + d, first, writeMSV, timeoutInSec, stdForms, n-1);
            }
        }
    }

    private static void searchSolve(TBCode code, boolean writeMSV, int timeoutInSec) throws Exception
    {
        System.out.print(code.toString() + " ... ");
        long time = System.currentTimeMillis();
        PeriodicityChecker apchecker = code.periodicityChecker();
        try
        {
            MisereSolver ms = runMisereSolver(TakeAndBreak.apply(code.toString()), true, false, writeMSV, false, Integer.MAX_VALUE, timeoutInSec);
            Periodicity apinfo = apchecker.checkSequence(ms.p.prefn);
            System.out.println(apinfo.period() + " / " + apinfo.preperiod() + " / " +
                               ms.p.quotient.size() + " / " + ms.p.quotient.pPortionSize());
        }
        catch (TimeoutException exc)
        {
            System.out.println("Timeout (" + (System.currentTimeMillis() - time) + " ms)");
        }
        catch (Throwable e)
        {
            System.out.println(e.getMessage());
        }
    }

}

class TimeoutException extends RuntimeException
{
}
