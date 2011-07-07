package org.cgsuite.lang;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.cgsuite.lang.explorer.DefaultEditorPanel;
import org.cgsuite.lang.explorer.EditorPanel;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

public class CgsuiteObject
{
    private final static Logger log = Logger.getLogger(CgsuiteObject.class.getName());

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
                CgsuiteObject obj = x.invokeMethod("Order", y).simplify();
                return ((CgsuiteInteger) obj).intValue();
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

        if (objectNamespace != null)
        {
            CgsuiteObject obj = objectNamespace.get(identifier);

            if (obj != null)
                return obj;
        }

        if (type.lookupVar(identifier) != null)
            return CgsuiteObject.NIL;

        log.info("Unable to locate identifier: " + identifier + " (in object of type " + type.getName() + ")");

        throw new InputException("Not a member variable, property, or method: " + identifier + " (in object of type " + type.getQualifiedName() + ")");
    }

    public void assign(String name, CgsuiteObject object)
    {
        Variable var = type.lookupVar(name);
        if (var == null)
            throw new InputException("Unknown variable: " + name);
        if (var.isStatic())
            throw new InputException("Cannot reference static variable in dynamic context: " + name);
        objectNamespace.put(name, object);
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
