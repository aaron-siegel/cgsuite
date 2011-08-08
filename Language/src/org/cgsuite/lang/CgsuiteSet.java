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
    public void unlink()
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
    public Collection<CgsuiteObject> getUnderlyingCollection()
    {
        return objects;
    }

    public Iterator<CgsuiteObject> sortedIterator()
    {
        List<CgsuiteObject> list = new ArrayList<CgsuiteObject>(objects.size());
        list.addAll(objects);
        Collections.sort(list);
        return list.iterator();
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

    public void remove(CgsuiteObject object)
    {
        objects.remove(object);
    }

    public void removeAll(CgsuiteCollection other)
    {
        objects.removeAll(other.getUnderlyingCollection());
    }

    public void clear()
    {
        objects.clear();
    }
    
    public boolean contains(CgsuiteObject obj)
    {
        return objects.contains(obj);
    }

    @Override
    public int size()
    {
        return objects.size();
    }

    public boolean isEmpty()
    {
        return objects.isEmpty();
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
