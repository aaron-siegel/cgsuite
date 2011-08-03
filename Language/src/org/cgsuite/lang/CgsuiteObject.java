package org.cgsuite.lang;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

// TODO Implement setters
// TODO Improve comparator methodology
// TODO Use weak references for crosslinks

public class CgsuiteObject implements Cloneable, Comparable<CgsuiteObject>
{
    private final static Logger log = Logger.getLogger(CgsuiteObject.class.getName());

    public static final List<CgsuiteObject> EMPTY_LIST = Collections.emptyList();
    public static final Nil NIL = new Nil();
    
    protected CgsuiteClass type;
    Namespace objectNamespace;
    
    private CgsuiteObject crosslink;
    private boolean isMutable;

    CgsuiteObject()
    {
        this.objectNamespace = new Namespace();
        this.crosslink = this;
        this.isMutable = true;
    }

    public CgsuiteObject(CgsuiteClass type)
    {
        this();
        this.type = type;
    }
    
    @Override
    protected CgsuiteObject clone()
    {
        try
        {
            return (CgsuiteObject) super.clone();
        }
        catch (CloneNotSupportedException exc)
        {
            throw new RuntimeException(exc);
        }
    }
    
    @Override
    public final int compareTo(CgsuiteObject other)
    {
        if (type == other.type)
            return compareLike(other);
        
        int cmp = ComparatorOrder.getIndex(type) - ComparatorOrder.getIndex(other.type);
        
        if (cmp != 0)
            return cmp;
        
        cmp = type.getQualifiedName().compareTo(other.type.getQualifiedName());
        
        assert cmp != 0 : type.getQualifiedName();
        return cmp;
    }
    
    protected int compareLike(CgsuiteObject other)
    {
        for (Variable var : type.varsInOrder())
        {
            if (var.isStatic())
                continue;
            
            CgsuiteObject x = this.objectNamespace.get(var.getName());
            CgsuiteObject y = other.objectNamespace.get(var.getName());
            
            if (x == null)
            {
                if (y != null)
                    return -1;
            }
            else
            {
                if (y == null)
                    return 1;

                int cmp = x.compareTo(y);
                if (cmp != 0)
                    return cmp;
            }
        }
        
        return 0;
    }
    
    public void markImmutable()
    {
        if (isMutable)
        {
            isMutable = false;
            for (CgsuiteObject member : objectNamespace.values())
            {
                member.markImmutable();
            }
        }
    }
    
    public boolean isMutable()
    {
        return isMutable;
    }
    
    public CgsuiteObject createCrosslink()
    {
        if (!type.isMutable())
            return this;
        
        CgsuiteObject clone = this.clone();
        clone.isMutable = true;     // Crosslink is mutable even if this is not
        clone.crosslink = this.crosslink;
        this.crosslink = clone;
        return clone;
    }
    
    public void unlink()
    {
        this.objectNamespace = objectNamespace.crosslinkedNamespace();
        CgsuiteObject next = crosslink;
        while (next.crosslink != this)
        {
            next = next.crosslink;
        }
        next.crosslink = this.crosslink;
        this.crosslink = this;
    }
    
    public final void unlinkIfNecessary()
    {
        if (this.crosslink != this)
        {
            unlink();
        }
    }

    public CgsuiteObject simplify()
    {
        return this;
    }

    public CgsuiteString toCgsuiteString()
    {
        return (CgsuiteString) invokeMethod("ToString$get");
    }

    public Output toOutput()
    {
        return new StyledTextOutput(toCgsuiteString().toJavaString());
    }

    public CgsuiteClass getCgsuiteClass()
    {
        return type;
    }
    
    public CgsuiteObject resolve(String identifier) throws CgsuiteException
    {
        return resolve(identifier, null);
    }

    public CgsuiteObject resolve(String identifier, CgsuiteObject context) throws CgsuiteException
    {
        CgsuiteMethod getter = type.lookupMethod(identifier + "$get");

        if (getter != null)
        {
            if (getter.isStatic())
                throw new InputException("Cannot reference static property in dynamic context: " + identifier);
            return getter.invoke(castForMethodCall(getter), CgsuiteObject.EMPTY_LIST, null);
        }

        CgsuiteMethod method = type.lookupMethod(identifier);

        if (method != null)
        {
            if (method.isStatic())
                throw new InputException("Cannot reference static method in dynamic context: " + identifier);
            return new InstanceMethod(method);
        }
        
        Variable var = type.lookupVar(identifier);
        
        if (var != null)
        {
            if (context == null || !context.getCgsuiteClass().hasAncestor(var.getDeclaringClass()))
                throw new InputException("Cannot access variable from outside class " + var.getDeclaringClass().getQualifiedName() + ": " + identifier);
            
            CgsuiteObject obj = objectNamespace.get(identifier);
            return (obj == null)? CgsuiteObject.NIL : obj;
        }
        
        return null;
    }

    public void assign(String name, CgsuiteObject value, CgsuiteObject context)
    {
        Variable var = type.lookupVar(name);
        if (!isMutable)
            throw new InputException("Cannot change member variable of immutable object: " + name);
        if (var == null)
            throw new InputException("Unknown variable: " + name);
        if (var.isStatic())
            throw new InputException("Cannot reference static variable in dynamic context: " + name);
        if (context == null || !context.getCgsuiteClass().hasAncestor(var.getDeclaringClass()))
            throw new InputException("Cannot access variable from outside class " + var.getDeclaringClass().getQualifiedName() + ": " + context.getCgsuiteClass().getQualifiedName());
        objectNamespace.put(name, value.createCrosslink());
    }

    public CgsuiteObject invokeMethod(String methodName)
        throws CgsuiteException
    {
        return invokeMethod(methodName, CgsuiteObject.EMPTY_LIST, null);
    }

    public CgsuiteObject invokeMethod(String methodName, CgsuiteObject arg)
        throws CgsuiteException
    {
        return invokeMethod(methodName, singletonList(arg), null);
    }

    public CgsuiteObject invokeMethod(String methodName, CgsuiteObject arg1, CgsuiteObject arg2)
        throws CgsuiteException
    {
        List<CgsuiteObject> list = new ArrayList<CgsuiteObject>(2);
        list.add(arg1);
        list.add(arg2);
        return invokeMethod(methodName, list, null);
    }

    public CgsuiteObject invokeMethod(String methodName, List<CgsuiteObject> arguments)
        throws CgsuiteException
    {
        return invokeMethod(methodName, arguments, null);
    }

    public CgsuiteObject invokeMethod(String methodName, List<CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
        throws CgsuiteException
    {
        InstanceMethod method = (InstanceMethod) resolve(methodName);
        if (method == null)
            throw new InputException("No method \"" + methodName + "\" for class " + (type == null ? null : type.getName()) + ".");
        return method.invoke(arguments, optionalArguments);
    }

    public class InstanceMethod extends CgsuiteObject implements Callable
    {
        private CgsuiteMethod method;
        private CgsuiteObject thisCast;

        InstanceMethod(CgsuiteMethod method) throws CgsuiteException
        {
            super(CgsuitePackage.forceLookupClass("InstanceMethod"));

            this.method = method;
            this.thisCast = castForMethodCall(method);
        }

        @Override
        public CgsuiteObject invoke(List<? extends CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
            throws CgsuiteException
        {
            return method.invoke(thisCast, arguments, optionalArguments);
        }
    }

    private CgsuiteObject castForMethodCall(CgsuiteMethod method)
    {
        if (method.getJavaMethod() == null)
        {
            return this;
        }
        else
        {
            return (CgsuiteObject) CgsuiteMethod.cast(this, method.getJavaMethod().getDeclaringClass(), false);
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((objectNamespace == null) ? 0 : objectNamespace.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        CgsuiteObject other = (CgsuiteObject) obj;
        if (objectNamespace == null)
        {
            if (other.objectNamespace != null)
                return false;
        } else if (!objectNamespace.equals(other.objectNamespace))
            return false;
        if (type == null)
        {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
