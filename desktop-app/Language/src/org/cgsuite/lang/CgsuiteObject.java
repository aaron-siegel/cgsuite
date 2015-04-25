package org.cgsuite.lang;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

// TODO Use weak references for crosslinks

public class CgsuiteObject implements Cloneable
{
    private final static Logger log = Logger.getLogger(CgsuiteObject.class.getName());

    public static final List<CgsuiteObject> EMPTY_LIST = Collections.emptyList();
    public static final Nil NIL = new Nil();
    
    public static final Comparator<CgsuiteObject> UNIVERSAL_COMPARATOR = new Comparator<CgsuiteObject>()
    {
        @Override
        public int compare(CgsuiteObject x, CgsuiteObject y)
        {
            return x.universalCompareTo(y);
        }
    };
    
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
    
    public final int universalCompareTo(CgsuiteObject other)
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

                int cmp = x.universalCompareTo(y);
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
        if (!type.isMutableClass())
            return this;
        
        CgsuiteObject clone = this.clone();
        clone.isMutable = true;     // Crosslink is mutable even if this is not
        clone.crosslink = this.crosslink;
        this.crosslink = clone;
        
        if (hasMutableReferent())
        {
            clone.unlink();
        }
        
        return clone;
    }
    
    // Returns true if this object references an object belonging to
    // a mutable CLASS.
    
    protected boolean hasMutableReferent()
    {
        for (CgsuiteObject value : objectNamespace.values())
        {
            if (value.getCgsuiteClass().isMutable())
                return true;
        }
        
        return false;
    }
    
    protected void unlink()
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

    public Output toOutput()
    {
        CgsuiteString str = (CgsuiteString) invokeMethod("ToString$get");
        return new StyledTextOutput(str.toJavaString());
    }

    public CgsuiteClass getCgsuiteClass()
    {
        return type;
    }
    
    public CgsuiteObject resolve(String identifier) throws CgsuiteException
    {
        return resolve(identifier, null, false);
    }

    public CgsuiteObject resolve(String identifier, CgsuiteMethod contextMethod, boolean localAccess) throws CgsuiteException
    {
        CgsuiteMethodGroup getter = type.lookupMethod(identifier + "$get");

        if (getter != null)
        {
            if (getter.isStatic())
                throw new InputException("Cannot reference static property in dynamic context: " + identifier);
            return getter.invoke(this, CgsuiteMethod.EMPTY_PARAM_LIST, CgsuiteMethod.EMPTY_PARAM_MAP);
        }

        CgsuiteMethodGroup method = type.lookupMethod(identifier);

        if (method != null)
        {
            if (method.isStatic())
                throw new InputException("Cannot reference static method in dynamic context: " + identifier);
            return new InstanceMethod(method);
        }
        
        Variable var;
        
        if (localAccess)
        {
            assert contextMethod != null;
            var = contextMethod.getDeclaringClass().lookupVar(identifier);
        }
        else
        {
            var = type.lookupVar(identifier);
        }
        
        if (var != null)
        {
            if (contextMethod == null || !contextMethod.getDeclaringClass().hasAncestor(var.getDeclaringClass()))
                throw new InputException("Cannot access variable from outside class " + var.getDeclaringClass().getQualifiedName() + ": " + identifier);
            
            CgsuiteObject obj = objectNamespace.get(identifier);
            return (obj == null)? CgsuiteObject.NIL : obj;
        }
        
        return null;
    }

    public void assign(String identifier, CgsuiteObject value, CgsuiteMethod contextMethod, boolean localAccess)
    {
        CgsuiteMethodGroup setter = type.lookupMethod(identifier + "$set");
        
        if (setter != null)
        {
            if (setter.isStatic())
                throw new InputException("Cannot reference static property in dynamic context: " + identifier);
            setter.invoke(this, Collections.singletonList(value), CgsuiteMethod.EMPTY_PARAM_MAP);
            return;
        }
        
        Variable var;
        
        if (localAccess)
        {
            assert contextMethod != null;
            var = contextMethod.getDeclaringClass().lookupVar(identifier);
        }
        else
        {
            var = type.lookupVar(identifier);
        }
        
        if (!isMutable)
            throw new InputException("Cannot change member variable of immutable object: " + identifier);
        if (!contextMethod.isMutableMethod())
            throw new InputException("Cannot assign to member variable from inside an immutable method: " + identifier);
        if (var == null)
            throw new InputException("Unknown variable: " + identifier);
        if (var.isStatic())
            throw new InputException("Cannot reference static variable in dynamic context: " + identifier);
        if (contextMethod == null || !contextMethod.getDeclaringClass().hasAncestor(var.getDeclaringClass()))
            throw new InputException("Cannot assign variable from outside class " + var.getDeclaringClass().getQualifiedName() + ": " + identifier);
        objectNamespace.put(identifier, value.createCrosslink());
    }

    public CgsuiteObject invokeMethod(String methodName)
        throws CgsuiteException
    {
        return invokeMethod(methodName, CgsuiteMethod.EMPTY_PARAM_LIST, CgsuiteMethod.EMPTY_PARAM_MAP);
    }

    public CgsuiteObject invokeMethod(String methodName, CgsuiteObject arg)
        throws CgsuiteException
    {
        return invokeMethod(methodName, singletonList(arg), CgsuiteMethod.EMPTY_PARAM_MAP);
    }

    public CgsuiteObject invokeMethod(String methodName, CgsuiteObject arg1, CgsuiteObject arg2)
        throws CgsuiteException
    {
        List<CgsuiteObject> list = new ArrayList<CgsuiteObject>(2);
        list.add(arg1);
        list.add(arg2);
        return invokeMethod(methodName, list, CgsuiteMethod.EMPTY_PARAM_MAP);
    }

    public CgsuiteObject invokeMethod(String methodName, List<CgsuiteObject> arguments)
        throws CgsuiteException
    {
        return invokeMethod(methodName, arguments, CgsuiteMethod.EMPTY_PARAM_MAP);
    }

    public CgsuiteObject invokeMethod(String methodName, List<CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
        throws CgsuiteException
    {
        InstanceMethod method = (InstanceMethod) resolve(methodName);
        if (method == null)
            throw new InputException("No method \"" + methodName + "\" for class " + (type == null ? null : type.getName()) + ".");
        return method.invoke(arguments, optionalArguments);
    }
    
    private static CgsuiteClass INSTANCE_METHOD_TYPE = null;
    
    public static CgsuiteClass instanceMethodType()
    {
        if (INSTANCE_METHOD_TYPE == null)
        {
            INSTANCE_METHOD_TYPE = CgsuitePackage.forceLookupClass("InstanceMethod");
        }
        return INSTANCE_METHOD_TYPE;
    }

    public class InstanceMethod extends CgsuiteObject implements Callable
    {
        private CgsuiteMethodGroup methodGroup;

        InstanceMethod(CgsuiteMethodGroup methodGroup) throws CgsuiteException
        {
            super(instanceMethodType());

            this.methodGroup = methodGroup;
        }
        
        public CgsuiteMethodGroup getMethodGroup()
        {
            return methodGroup;
        }
        
        public CgsuiteObject getObject()
        {
            return CgsuiteObject.this;
        }

        @Override
        public CgsuiteObject invoke(List<? extends CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
            throws CgsuiteException
        {
            return methodGroup.invoke(CgsuiteObject.this, arguments, optionalArguments);
        }
    }
/*
    // TODO This should be obsolete.
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
*/
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
    
    @Override
    public final String toString()
    {
        return toOutput().toString();
    }
}
