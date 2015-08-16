/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.output;

import java.text.BreakIterator;
import java.text.CharacterIterator;

/**
 *
 * @author asiegel
 */
public class CgBreakIterator extends BreakIterator
{
    private CharacterIterator text;
            
    public CgBreakIterator()
    {
    }

    @Override
    public int first()
    {
        return 0;
    }

    @Override
    public int last()
    {
        text.setIndex(text.getEndIndex());
        return text.getEndIndex();
    }

    @Override
    public int next(int n)
    {
        int result = current();
        
        for (int i = 0; i < n; i++)
        {
            if (result == CharacterIterator.DONE)
                return result;
            
            result = next();
        }
        
        return result;
    }

    @Override
    public int next()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int previous()
    {
        char c = text.previous();
        if (c == CharacterIterator.DONE)
            return DONE;
        
        char nextC;
        do
        {
            nextC = c;
            c = text.previous();
            if (c == CharacterIterator.DONE)
                return 0;
        }
        while (!isBreak(c, nextC));
        
        text.next();
        return text.getIndex();
    }

    @Override
    public int following(int offset)
    {
        if (this.text == null || this.text.getEndIndex() == 0)
            return DONE;
        
        text.setIndex(offset);
        
        char nextC = text.current();
        if (nextC == CharacterIterator.DONE)
            return DONE;
        
        char prevC;
        
        do
        {
            prevC = nextC;
            nextC = text.next();
            if (nextC == CharacterIterator.DONE)
                return text.getEndIndex();
        }
        while (!isBreak(prevC, nextC));
        
        return text.getIndex();
    }

    @Override
    public int current()
    {
        return text.getIndex();
    }

    @Override
    public CharacterIterator getText()
    {
        return text;
    }

    @Override
    public void setText(CharacterIterator newText)
    {
        this.text = newText;
    }
    
    private boolean isBreak(char prevC, char nextC)
    {
        return prevC == ','
            || (isCloseParen(prevC) && !isCloseParen(nextC) && nextC != ',')
            || (Character.isWhitespace(prevC) && !Character.isWhitespace(nextC));
    }
    
    private boolean isCloseParen(char c)
    {
        // TODO We should include ']' here and explicitly exclude superscripts
        return c == ')' || c == '}';
    }
}
