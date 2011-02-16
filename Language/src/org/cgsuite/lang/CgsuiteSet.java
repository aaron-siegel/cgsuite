package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CgsuiteSet extends CgsuiteCollection
{
    private Set<CgsuiteObject> objects;

    public CgsuiteSet()
    {
        super(Domain.CLASS_DOMAIN.lookupClass("Set"));

        objects = new HashSet<CgsuiteObject>();
    }

    public CgsuiteSet(int capacity)
    {
        super(Domain.CLASS_DOMAIN.lookupClass("Set"));

        objects = new HashSet<CgsuiteObject>(capacity);
    }

    @Override
    public String toString()
    {
        Iterator<CgsuiteObject> it = sortedIterator();
        StringBuilder buf = new StringBuilder("{");
        while (it.hasNext())
        {
            buf.append(it.next().toString());
            if (it.hasNext())
                buf.append(',');
        }
        buf.append("}");
        return buf.toString();
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
        Collections.sort(list, CgsuiteObject.SORT_COMPARATOR);
        return list.iterator();
    }

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

    public CgsuiteSet copy()
    {
        CgsuiteSet copy = new CgsuiteSet(objects.size());
        copy.objects.addAll(objects);
        return copy;
    }

    public int size()
    {
        return objects.size();
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
}
