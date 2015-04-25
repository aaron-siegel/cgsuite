/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.output;

import java.text.BreakIterator;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author asiegel
 */
public class CgBreakIteratorTest {
    
    private String str;
    private CharacterIterator it;
    private CgBreakIterator instance;
    
    public CgBreakIteratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp()
    {
        //     01234567890123456789012345
        str = "{6|4,4*,{5|3},{6|4}}+1";
        it = new StringCharacterIterator(str);
        instance = new CgBreakIterator();
        instance.setText(it);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of first method, of class CgBreakIterator.
     */
    @Test
    public void testFirst()
    {
        assertEquals(0, instance.first());
    }

    /**
     * Test of last method, of class CgBreakIterator.
     */
    @Test
    public void testLast()
    {
        assertEquals(str.length(), instance.last());
    }

    /**
     * Test of next method, of class CgBreakIterator.
     */
    @Test(expected=UnsupportedOperationException.class)
    public void testNext_int()
    {
        instance.next(5);
    }

    /**
     * Test of next method, of class CgBreakIterator.
     */
    @Test(expected=UnsupportedOperationException.class)
    public void testNext_0args()
    {
        instance.next();
    }

    /**
     * Test of previous method, of class CgBreakIterator.
     */
    @Test
    public void testPrevious()
    {
        assertEquals(str.length(), instance.last());
        assertEquals(20, instance.previous());
        assertEquals(14, instance.previous());
        assertEquals( 8, instance.previous());
        assertEquals( 5, instance.previous());
        assertEquals( 0, instance.previous());
        assertEquals(BreakIterator.DONE, instance.previous());
    }

    /**
     * Test of following method, of class CgBreakIterator.
     */
    @Test
    public void testFollowing()
    {
        assertEquals(5, instance.following(4));
        assertEquals(8, instance.following(5));
        assertEquals(8, instance.following(7));
        assertEquals(14, instance.following(9));
        assertEquals(20, instance.following(15));
        assertEquals(str.length(), instance.following(20));
        assertEquals(BreakIterator.DONE, instance.following(str.length()));
    }
}
