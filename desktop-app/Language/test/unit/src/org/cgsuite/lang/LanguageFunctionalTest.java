/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
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
    private final static Logger log = Logger.getLogger(LanguageFunctionalTest.class.getName());
    
    public final static Domain TEST_DOMAIN = new Domain(null, null);
    
    @Parameters
    public static Collection<Object[]> data() throws Exception
    {
        List<Object[]> data = new ArrayList<Object[]>();
        
        InputStream in = LanguageFunctionalTest.class.getResourceAsStream("test-instances.txt");
        FunctionalTestReader reader = new FunctionalTestReader(in);
        
        int testNumber = 0;
        
        while (true)
        {
            String[] test = reader.nextTestInstance();

            if (test == null)
                break;
            
            Object[] run = new Object[1+test.length];
            System.arraycopy(test, 0, run, 1, test.length);
            run[0] = testNumber++;
            
            data.add(run);
        }
        
        return data;
    }
    
    private int testNumber;
    private String description;
    private String input;
    private String expected;
    
    public LanguageFunctionalTest(int testNumber, String description, String input, String expected)
    {
        this.testNumber = testNumber;
        this.description = description;
        this.input = input;
        this.expected = expected;
    }
    
    @Test(timeout=10000L)
    public void testLanguage() throws Exception
    {
        log.info("Running functional test " + testNumber + ": " + description);
        
        ANTLRStringStream inputStream = new ANTLRStringStream(input);
        CgsuiteLexer lexer = new CgsuiteLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CgsuiteParser parser = new CgsuiteParser(tokens);
        parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
        CgsuiteParser.script_return r = parser.script();
        CgsuiteTree tree = (CgsuiteTree) r.getTree();

        String result;
        
        if (!lexer.getErrors().isEmpty())
        {
            result = "!!Syntax error: " + lexer.getErrors().get(0).getMessage();
        }
        else if (!parser.getErrors().isEmpty())
        {
            result = "!!Syntax error: " + parser.getErrors().get(0).getMessage();
        }
        else try
        {
            Output output = TEST_DOMAIN.script(tree).simplify().toOutput();
            result = output.toString().trim();
        }
        catch (InputException exc)
        {
            result = "!!" + exc.getMessage();
        }
        catch (CgsuiteClassLoadException exc)
        {
            result = "!!" + exc.getMessage();
            exc.printStackTrace(System.err);
        }
        
        Assert.assertEquals("[" + description + "] Incorrect output.", expected, result);
    }
}
