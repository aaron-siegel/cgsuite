/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import java.util.Iterator;

/**
 *
 * @author asiegel
 */
public class CgsuiteIterator extends CgsuiteObject implements Iterable<CgsuiteObject>
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Iterator");
    
    public CgsuiteIterator(CgsuiteClass type)
    {
        super(type);
    }
    
    @Override
    public Iterator<CgsuiteObject> iterator()
    {
        invokeMethod("Reset");
        return new JavaIterator();
    }
    
    private class JavaIterator implements Iterator<CgsuiteObject>
    {
        @Override
        public boolean hasNext()
        {
            return Domain.bool(invokeMethod("HasNext$get"), type.lookupMethod("HasNext$get").firstMethod().getTree());
        }

        @Override
        public CgsuiteObject next()
        {
            return invokeMethod("Next$get");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Removal not supported.");
        }
    }
    
}
