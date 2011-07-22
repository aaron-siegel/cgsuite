package org.cgsuite.lang;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public final class Namespace
{
    private HashMap<String,CgsuiteObject> objects;

    public Namespace()
    {
        this.objects = new HashMap<String,CgsuiteObject>();
    }

    @Override
    public String toString()
    {
        return "Namespace" + objects.toString();
    }
    
    public Namespace crosslinkedNamespace()
    {
        Namespace copy = new Namespace();
        for (Entry<String,CgsuiteObject> e : objects.entrySet())
        {
            copy.objects.put(e.getKey(), e.getValue().createCrosslink());
        }
        return copy;
    }
    
    public Set<String> keys()
    {
        return objects.keySet();
    }

    public void clear()
    {
        objects.clear();
    }

    public CgsuiteObject get(String str)
    {
        CgsuiteObject obj = objects.get(str);
        return obj;
    }

    public void put(String str, CgsuiteObject object)
    {
        objects.put(str, object);
    }
    
    public CgsuiteObject remove(String str)
    {
        return objects.remove(str);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((objects == null) ? 0 : objects.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Namespace other = (Namespace) obj;
        if (objects == null)
        {
            if (other.objects != null)
                return false;
        } else if (!objects.equals(other.objects))
            return false;
        return true;
    }
}
