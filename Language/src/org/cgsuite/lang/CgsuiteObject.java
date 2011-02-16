package org.cgsuite.lang;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.cgsuite.CgsuiteException;
import org.cgsuite.RationalNumber;

public class CgsuiteObject
{
    public static final List<CgsuiteObject> EMPTY_LIST = Collections.emptyList();
    public static final CgsuiteObject NIL = new CgsuiteObject()
    {
        @Override
        public String toString()
        {
            return "";
        }
    };

    public static final Comparator<CgsuiteObject> SORT_COMPARATOR = new Comparator<CgsuiteObject>()
    {
        @Override
        public int compare(CgsuiteObject x, CgsuiteObject y)
        {
            try
            {
                CgsuiteObject obj = x.invoke("Order", y);
                return ((RationalNumber) obj).intValue();
            }
            catch (CgsuiteException exc)
            {
                throw new RuntimeException(exc);
            }
        }
    };

    protected CgsuiteClass type;
    protected Namespace objectNamespace;

    CgsuiteObject()
    {
        objectNamespace = new Namespace();
    }

    public CgsuiteObject(CgsuiteClass type)
    {
        this.type = type;
        this.objectNamespace = new Namespace();
    }

    public CgsuiteClass getCgsuiteClass()
    {
        return type;
    }

    public CgsuiteObject resolve(String name) throws CgsuiteException
    {
        type.ensureFullyLoaded();

        CgsuiteMethod getter = (CgsuiteMethod) type.lookup(name + "$get");

        if (getter != null)
            return getter.invoke(castForMethodCall(getter), CgsuiteObject.EMPTY_LIST, null);

        CgsuiteObject obj = type.lookup(name);

        if (obj != null)
            return new InstanceMethod((CgsuiteMethod) obj);

        obj = objectNamespace.get(name);

        if (obj != null)
            return obj;

        throw new InputException("Not a member variable, property, or method: " + name);
    }

    public void assign(String name, CgsuiteObject object)
    {
        objectNamespace.put(name, object);
    }

    public CgsuiteObject invoke(String methodName)
        throws CgsuiteException
    {
        return invoke(methodName, CgsuiteObject.EMPTY_LIST, null);
    }

    public CgsuiteObject invoke(String methodName, CgsuiteObject arg)
        throws CgsuiteException
    {
        return invoke(methodName, singletonList(arg), null);
    }

    public CgsuiteObject invoke(String methodName, CgsuiteObject arg1, CgsuiteObject arg2)
        throws CgsuiteException
    {
        List<CgsuiteObject> list = new ArrayList<CgsuiteObject>(2);
        list.add(arg1);
        list.add(arg2);
        return invoke(methodName, list, null);
    }

    public CgsuiteObject invoke(String methodName, List<CgsuiteObject> arguments)
        throws CgsuiteException
    {
        return invoke(methodName, arguments, null);
    }

    public CgsuiteObject invoke(String methodName, List<CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
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
            super(Domain.CLASS_DOMAIN.lookupClass("InstanceMethod"));

            this.method = method;
            this.thisCast = castForMethodCall(method);
        }

        @Override
        public CgsuiteObject invoke(List<CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
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
            return (CgsuiteObject) CgsuiteMethod.cast(this, method.getJavaMethod().getDeclaringClass());
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
