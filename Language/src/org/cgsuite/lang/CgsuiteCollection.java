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
    public Iterator<CgsuiteObject> iterator()
    {
        return getUnderlyingCollection().iterator();
    }
    
    public abstract int size();
    
    public abstract void add(CgsuiteObject obj);

    public abstract Collection<CgsuiteObject> getUnderlyingCollection();
}
