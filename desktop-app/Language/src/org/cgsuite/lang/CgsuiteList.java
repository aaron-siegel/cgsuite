package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.Collection;
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
    
    public CgsuiteList(Collection<? extends CgsuiteObject> initialValues)
    {
        this();
        objects.addAll(initialValues);
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
    protected void unlink()
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
        set(objects.size()+1, obj);
    }
    
    public void addAll(CgsuiteCollection collection)
    {
        for (CgsuiteObject obj : collection)
        {
            add(obj);
        }
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

    public CgsuiteObject set(int index, CgsuiteObject value)
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
            set(index, value);
        else
            objects.add(index-1, value);
    }
    
    public void clear()
    {
        objects.clear();
    }

    public void sort(final CgsuiteProcedure comparator)
    {
        if (comparator == null)
        {
            Collections.sort(objects, UNIVERSAL_COMPARATOR);
        }
        else
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
                    return (Integer) CgsuiteMethod.cast(comparator.invoke(arguments, CgsuiteMethod.EMPTY_PARAM_MAP).simplify(), int.class, false);
                }
            });
        }
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
    public int compareLike(CgsuiteObject obj)
    {
        CgsuiteList other = (CgsuiteList) obj;
        
        for (int i = 1; i <= Math.min(size(), other.size()); i++)
        {
            int cmp = this.get(i).universalCompareTo(other.get(i));
            if (cmp != 0)
                return cmp;
        }
        
        return size() - other.size();
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
