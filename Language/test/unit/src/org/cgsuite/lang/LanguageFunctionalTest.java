/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.parser.CgsuiteLexer;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.cgsuite.lang.parser.CgsuiteTree;
import org.cgsuite.lang.parser.CgsuiteTreeAdaptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author asiegel
 */
@RunWith(Parameterized.class)
public class LanguageFunctionalTest
{
    public static Domain domain = new Domain(null, CgsuitePackage.DEFAULT_IMPORT);
    
    @Parameters
    public static Collection<Object[]> data() throws Exception
    {
        List<Object[]> data = new ArrayList<Object[]>();
        
        InputStream in = LanguageFunctionalTest.class.getResourceAsStream("test-instances.txt");
        FunctionalTestReader reader = new FunctionalTestReader(in);
        
        while (true)
        {
            String[] test = reader.nextTestInstance();

            if (test == null)
                break;
            
            data.add(test);
        }
        
        return data;
    }
    
    private String description;
    private String input;
    private String expected;
    
    public LanguageFunctionalTest(String description, String input, String expected)
    {
        this.description = description;
        this.input = input;
        this.expected = expected;
    }
    
    @Test
    public void testLanguage() throws Exception
    {
        ANTLRStringStream inputStream = new ANTLRStringStream(input);
        CgsuiteLexer lexer = new CgsuiteLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CgsuiteParser parser = new CgsuiteParser(tokens);
        parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
        CgsuiteParser.script_return r = parser.script();
        CgsuiteTree tree = (CgsuiteTree) r.getTree();

        Assert.assertTrue("[" + description + "] Lexer errors: " + lexer.getErrors(), lexer.getErrors().isEmpty());
        Assert.assertTrue("[" + description + "] Parser errors: " + parser.getErrors(), parser.getErrors().isEmpty());
        
        Output output = domain.script(tree).simplify().toOutput();
        StringWriter sw = new StringWriter();
        output.write(new PrintWriter(sw), Output.Mode.PLAIN_TEXT);
        
        Assert.assertEquals("[" + description + "] Incorrect output.", expected, sw.toString());
    }
}
