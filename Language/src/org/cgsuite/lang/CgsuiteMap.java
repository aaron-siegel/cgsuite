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
    private Map<CgsuiteObject,CgsuiteObject> map;

    public CgsuiteMap()
    {
        super(CgsuitePackage.forceLookupClass("Map"));

        this.map = new HashMap<CgsuiteObject,CgsuiteObject>();
    }

    @Override
    public String toString()
    {
        if (map.isEmpty())
            return "{=>}";

        List<CgsuiteObject> list = new ArrayList<CgsuiteObject>(map.size());
        list.addAll(map.keySet());
        Collections.sort(list, CgsuiteObject.SORT_COMPARATOR);
        StringBuilder buf = new StringBuilder("{");
        Iterator<CgsuiteObject> it = list.iterator();
        while (it.hasNext())
        {
            CgsuiteObject key = it.next();
            buf.append(key);
            buf.append("=>");
            buf.append(map.get(key));
            if (it.hasNext())
                buf.append(',');
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public StyledTextOutput toOutput()
    {
        // TODO Sort by keys
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("{");
        if (isEmpty())
            output.appendSymbol(Symbol.RIGHT_ARROW);
        for (Iterator<Entry<CgsuiteObject,CgsuiteObject>> it = map.entrySet().iterator(); it.hasNext();)
        {
            Entry<CgsuiteObject,CgsuiteObject> e = it.next();
            output.appendOutput(e.getKey().toOutput());
            output.appendMath(" ");
            output.appendSymbol(Symbol.RIGHT_ARROW);
            output.appendMath(" ");
            output.appendOutput(e.getValue().toOutput());
            if (it.hasNext())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("}");
        return output;
    }
    
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public CgsuiteObject get(CgsuiteObject key)
    {
        return map.get(key);
    }

    public void put(CgsuiteObject value, CgsuiteObject key)
    {
        map.put(key, value);
    }

    public Set<Entry<CgsuiteObject,CgsuiteObject>> entrySet()
    {
        return map.entrySet();
    }

    public int size()
    {
        return map.size();
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
