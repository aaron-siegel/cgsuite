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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int last()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int next(int n)
    {
        throw new UnsupportedOperationException("Not supported yet.");
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
                return DONE;
        }
        while (!isBreakChar(c, nextC));
        
        text.next();
        return text.getIndex();
    }

    @Override
    public int following(int offset)
    {
        if (this.text == null)
            return DONE;
        
        text.setIndex(offset);
        
        char c = text.next();
        if (c == CharacterIterator.DONE)
            return DONE;
        
        char nextC = text.next();
        if (nextC == CharacterIterator.DONE)
            return DONE;
        
        while (!isBreakChar(c, nextC))
        {
            nextC = c;
            c = text.next();
            if (c == CharacterIterator.DONE)
                return DONE;
        }
        
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
    
    private boolean isBreakChar(char c, char nextC)
    {
        return c == ',' || (isCloseParen(c) && !isCloseParen(nextC) && nextC != ',') || (Character.isWhitespace(c) && !Character.isWhitespace(nextC));
    }
    
    private boolean isCloseParen(char c)
    {
        return c == ')' || c == '}' || c == ']';
    }
}
