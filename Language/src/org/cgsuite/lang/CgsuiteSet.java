package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.cgsuite.lang.output.StyledTextOutput;

public class CgsuiteSet extends CgsuiteCollection
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Set");
    
    private Set<CgsuiteObject> objects;

    public CgsuiteSet()
    {
        super(TYPE);

        objects = new HashSet<CgsuiteObject>();
    }

    public CgsuiteSet(int capacity)
    {
        super(TYPE);

        objects = new HashSet<CgsuiteObject>(capacity);
    }
    
    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("{");
        for (Iterator<CgsuiteObject> it = sortedIterator(); it.hasNext();)
        {
            output.appendOutput(it.next().toOutput());
            if (it.hasNext())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("}");
        return output;
    }
    
    @Override
    protected void unlink()
    {
        super.unlink();
        Set<CgsuiteObject> newObjects = new HashSet<CgsuiteObject>(objects.size());
        for (CgsuiteObject obj : objects)
        {
            newObjects.add(obj.createCrosslink());
        }
        objects = newObjects;
    }
    
    @Override
    protected boolean hasMutableReferent()
    {
        for (CgsuiteObject obj : objects)
        {
            if (obj.getCgsuiteClass().isMutable())
                return true;
        }
        
        return false;
    }
    
    @Override
    public void markImmutable()
    {
        if (isMutable())
        {
            super.markImmutable();
            for (CgsuiteObject obj : objects)
            {
                obj.markImmutable();
            }
        }
    }

    @Override
    public Collection<CgsuiteObject> getUnderlyingCollection()
    {
        return objects;
    }

    public Iterator<CgsuiteObject> sortedIterator()
    {
        List<CgsuiteObject> list = new ArrayList<CgsuiteObject>(objects.size());
        list.addAll(objects);
        Collections.sort(list, UNIVERSAL_COMPARATOR);
        return list.iterator();
    }
    
    public CgsuiteObject anyElement()
    {
        if (isEmpty())
            return null;
        else
            return iterator().next();
    }
    
    public CgsuiteObject randomElement()
    {
        int n = CgsuiteInteger.random(new CgsuiteInteger(size())).intValue();
        CgsuiteObject value = null;
        Iterator<CgsuiteObject> it = iterator();
        
        for (int i = 0; i < n; i++)
        {
            value = it.next();
        }
        
        return value;
    }

    @Override
    public void add(CgsuiteObject object)
    {
        objects.add(object);
    }

    public void addAll(CgsuiteCollection other)
    {
        objects.addAll(other.getUnderlyingCollection());
    }

    public boolean remove(CgsuiteObject object)
    {
        return objects.remove(object);
    }

    public boolean removeAll(CgsuiteCollection other)
    {
        return objects.removeAll(other.getUnderlyingCollection());
    }

    public void clear()
    {
        objects.clear();
    }
    
    @Override
    public boolean contains(CgsuiteObject obj)
    {
        return objects.contains(obj);
    }

    @Override
    public int size()
    {
        return objects.size();
    }

    @Override
    public boolean isEmpty()
    {
        return objects.isEmpty();
    }
    
    @Override
    public int compareLike(CgsuiteObject obj)
    {
        CgsuiteSet other = (CgsuiteSet) obj;
        
        Iterator<CgsuiteObject> itThis = sortedIterator();
        Iterator<CgsuiteObject> itOther = other.sortedIterator();
        
        while (itThis.hasNext() && itOther.hasNext())
        {
            int cmp = itThis.next().universalCompareTo(itOther.next());
            if (cmp != 0)
                return cmp;
        }
        
        return (itThis.hasNext() ? 0 : 1) - (itOther.hasNext() ? 0 : 1);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((objects == null) ? 0 : objects.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CgsuiteSet other = (CgsuiteSet) obj;
        if (objects == null)
        {
            if (other.objects != null)
                return false;
        } else if (!objects.equals(other.objects))
            return false;
        return true;
    }

    public static CgsuiteSet singleton(CgsuiteObject obj)
    {
        CgsuiteSet set = new CgsuiteSet(1);
        set.add(obj);
        return set;
    }
}
