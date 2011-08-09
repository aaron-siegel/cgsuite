package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.cgsuite.lang.output.StyledTextOutput;


public class CgsuiteList extends CgsuiteCollection
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("List");
    
    private ArrayList<CgsuiteObject> objects;

    public CgsuiteList()
    {
        super(TYPE);

        this.objects = new ArrayList<CgsuiteObject>();
    }

    public CgsuiteList(int capacity)
    {
        super(TYPE);

        this.objects = new ArrayList<CgsuiteObject>(capacity);
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
    public void unlink()
    {
        super.unlink();
        ArrayList<CgsuiteObject> newObjects = new ArrayList<CgsuiteObject>(objects.size());
        for (CgsuiteObject obj : objects)
        {
            newObjects.add(obj.createCrosslink());
        }
        objects = newObjects;
    }

    @Override
    public ArrayList<CgsuiteObject> getUnderlyingCollection()
    {
        return objects;
    }

    @Override
    public int size()
    {
        return objects.size();
    }

    @Override
    public void add(CgsuiteObject obj)
    {
        set(obj, objects.size()+1);
    }

    public CgsuiteObject get(int index)
    {
        if (index <= 0)
            throw new InputException("Invalid list index: " + index);
        else if (index > objects.size())
            return null;
        else
            return objects.get(index-1);
    }

    public CgsuiteObject set(CgsuiteObject value, int index)
    {
        if (index <= 0)
            throw new InputException("Invalid list index: " + index);
        objects.ensureCapacity(index - objects.size());
        while (objects.size() < index)
        {
            objects.add(NIL);
        }
        objects.set(index-1, value);
        return value;
    }
    
    public CgsuiteList subList(int from, int to)
    {
        CgsuiteList list = new CgsuiteList();
        list.objects.addAll(this.objects.subList(from-1, to));
        return list;
    }
    
    public boolean remove(CgsuiteObject obj)
    {
        return objects.remove(obj);
    }
    
    public CgsuiteObject removeAt(int index)
    {
        if (index <= 0)
            throw new InputException("Invalid list index: " + index);
        else if (index > objects.size())
            return CgsuiteObject.NIL;
        else
            return objects.remove(index-1);
    }
    
    public void insertAt(int index, CgsuiteObject value)
    {
        if (index <= 0)
            throw new InputException("Invalid list index: " + index);
        else if (index > objects.size())
            set(value, index);
        else
            objects.add(index-1, value);
    }
    
    public void clear()
    {
        objects.clear();
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
                return (Integer) CgsuiteMethod.cast(comparator.invoke(arguments, null).simplify(), int.class, false);
            }
        });
    }
    
    public Table periodicTable(int period)
    {
        Table table = new Table();
        for (int i = 1; i <= size(); i += period)
        {
            table.add(subList(i, Math.min(size(), i+period-1)));
        }
        return table;
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
