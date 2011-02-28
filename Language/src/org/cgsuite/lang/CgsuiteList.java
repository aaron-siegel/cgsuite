package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.cgsuite.lang.output.StyledTextOutput;


public class CgsuiteList extends CgsuiteCollection
{
    private ArrayList<CgsuiteObject> objects;

    public CgsuiteList()
    {
        super(CgsuitePackage.forceLookupClass("List"));

        this.objects = new ArrayList<CgsuiteObject>();
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder("[");
        for (int i = 0; i < objects.size(); i++)
        {
            buf.append(objects.get(i));
            if (i < objects.size()-1)
                buf.append(',');
        }
        buf.append("]");
        return buf.toString();
    }

    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        output.appendMath("[");
        for (int i = 1; i <= size(); i++)
        {
            output.appendOutput(get(i).toOutput());
            if (i < size())
            {
                output.appendMath(",");
            }
        }
        output.appendMath("]");
        return output;
    }

    @Override
    public Collection<CgsuiteObject> getUnderlyingCollection()
    {
        return objects;
    }

    public int size()
    {
        return objects.size();
    }

    public void add(CgsuiteObject obj)
    {
        set(obj, objects.size()+1);
    }

    public CgsuiteObject get(int index)
    {
        return objects.get(index-1);
    }

    public CgsuiteObject set(CgsuiteObject value, int index)
    {
        objects.ensureCapacity(index - objects.size());
        while (objects.size() < index)
        {
            objects.add(NIL);
        }
        objects.set(index-1, value);
        return value;
    }

    public void sort(final CgsuiteProcedure comparator)
    {
        final List<CgsuiteObject> arguments = new ArrayList<CgsuiteObject>(2);
        arguments.add(null);
        arguments.add(null);
        Collections.sort(objects, new Comparator<CgsuiteObject>()
        {
            @Override
            public int compare(CgsuiteObject x, CgsuiteObject y)
            {
                arguments.set(0, x);
                arguments.set(1, y);
                return (Integer) CgsuiteMethod.cast(comparator.invoke(arguments, null).simplify(), int.class);
            }
        });
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CgsuiteList other = (CgsuiteList) obj;
        if (this.objects != other.objects && (this.objects == null || !this.objects.equals(other.objects)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + (this.objects != null ? this.objects.hashCode() : 0);
        return hash;
    }

}
