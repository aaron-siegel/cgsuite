package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.cgsuite.lang.output.StyledTextOutput;
import org.cgsuite.lang.output.StyledTextOutput.Symbol;

public class CgsuiteMap extends CgsuiteObject
{
    // TODO Mark keys immutable!
    // TODO Implement compareLike
    
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Map");
    
    private Map<CgsuiteObject,CgsuiteObject> map;

    public CgsuiteMap()
    {
        super(TYPE);

        this.map = new HashMap<CgsuiteObject,CgsuiteObject>();
    }

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("{");
        
        if (isEmpty())
            output.appendSymbol(Symbol.BIG_RIGHT_ARROW);
        
        List<CgsuiteObject> list = new ArrayList<CgsuiteObject>(size());
        list.addAll(map.keySet());
        Collections.sort(list, UNIVERSAL_COMPARATOR);
        
        for (Iterator<CgsuiteObject> it = list.iterator(); it.hasNext();)
        {
            CgsuiteObject key = it.next();
            output.appendOutput(key.toOutput());
            output.appendMath(" ");
            output.appendSymbol(Symbol.BIG_RIGHT_ARROW);
            output.appendMath(" ");
            output.appendOutput(map.get(key).toOutput());
            if (it.hasNext())
            {
                output.appendMath(", ");
            }
        }
        output.appendMath("}");
        return output;
    }
    
    @Override
    public void unlink()
    {
        super.unlink();
        Map<CgsuiteObject,CgsuiteObject> newMap = new HashMap<CgsuiteObject,CgsuiteObject>(map.size());
        for (Entry<CgsuiteObject,CgsuiteObject> e : map.entrySet())
        {
            newMap.put(e.getKey().createCrosslink(), e.getValue().createCrosslink());
        }
        map = newMap;
    }
    
    @Override
    protected boolean hasMutableReferent()
    {
        for (Entry<CgsuiteObject,CgsuiteObject> e : map.entrySet())
        {
            if (e.getKey().getCgsuiteClass().isMutable() || e.getValue().getCgsuiteClass().isMutable())
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
            for (Entry<CgsuiteObject,CgsuiteObject> e : map.entrySet())
            {
                e.getKey().markImmutable();
                e.getValue().markImmutable();
            }
        }
    }
    
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public CgsuiteObject get(CgsuiteObject key)
    {
        return map.get(key);
    }

    public void put(CgsuiteObject key, CgsuiteObject value)
    {
        map.put(key, value);
    }

    public CgsuiteSet keys()
    {
        CgsuiteSet set = new CgsuiteSet();
        for (CgsuiteObject obj : map.keySet())
        {
            set.add(obj);
        }
        return set;
    }

    public Set<Entry<CgsuiteObject,CgsuiteObject>> entrySet()
    {
        return map.entrySet();
    }

    public int size()
    {
        return map.size();
    }

    public void clear()
    {
        map.clear();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((map == null) ? 0 : map.hashCode());
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
        CgsuiteMap other = (CgsuiteMap) obj;
        if (map == null)
        {
            if (other.map != null)
                return false;
        } else if (!map.equals(other.map))
            return false;
        return true;
    }
}
