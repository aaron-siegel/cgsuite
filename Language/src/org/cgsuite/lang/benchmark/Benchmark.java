/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.benchmark;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Domain;
import org.cgsuite.lang.parser.CgsuiteLexer;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.cgsuite.lang.parser.CgsuiteTree;
import org.cgsuite.lang.parser.CgsuiteTreeAdaptor;

/**
 *
 * @author asiegel
 */
public class Benchmark
{
    private static String[] inputs =
    {
        "3/4",
        "100 * ^.Pow(2)",
        "game.grid.Domineering(Grid(4,5)).CanonicalForm",
        "game.strip.ToadsAndFrogs(\"ttttttt..fffffff\").CanonicalForm",
        "game.grid.Amazons(\"x.....|o....#\").CanonicalForm",
        "game.grid.NoGo(\"x...|....|....\").CanonicalForm",
    };
    
    public static void main(String[] args) throws Exception
    {
        CgsuitePackage.refreshAll();
        
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream("benchmark.txt")));
        for (String str : inputs)
        {
            benchmark(out, str);
        }
        out.close();
    }
    
    private static void benchmark(PrintWriter out, String str) throws Exception
    {
        
        long t0 = System.currentTimeMillis();
        
        ANTLRStringStream input = new ANTLRStringStream(str);
        CgsuiteLexer lexer = new CgsuiteLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CgsuiteParser parser = new CgsuiteParser(tokens);
        parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
        CgsuiteParser.script_return r = parser.script();
        CgsuiteTree tree = (CgsuiteTree) r.getTree();
        
        long t1 = System.currentTimeMillis();
        CgsuiteObject result = new Domain(null, null).script(tree);
        long t2 = System.currentTimeMillis();
        result.toOutput();
        long t3 = System.currentTimeMillis();
        
        out.println(str);
        out.println("Parsing    : " + timespan(t1-t0));
        out.println("Calculation: " + timespan(t2-t1));
        out.println("To Output  : " + timespan(t3-t2));
        out.flush();
    }
    
    private static String timespan(long t)
    {
        return String.format("%6d.%03ds", t/1000, t%1000);
    }
}
