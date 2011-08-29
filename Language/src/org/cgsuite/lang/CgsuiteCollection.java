/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author asiegel
 */
public abstract class CgsuiteCollection extends CgsuiteObject implements Iterable<CgsuiteObject>
{
    protected CgsuiteCollection(CgsuiteClass type)
    {
        super(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<CgsuiteObject> iterator()
    {
        return (Iterator<CgsuiteObject>) getUnderlyingCollection().iterator();
    }
    
    public abstract int size();
    
    public abstract void add(CgsuiteObject obj);
    
    public boolean contains(CgsuiteObject obj)
    {
        return getUnderlyingCollection().contains(obj);
    }
    
    public boolean isEmpty()
    {
        return size() == 0;
    }

    public abstract Collection<? extends CgsuiteObject> getUnderlyingCollection();
}
