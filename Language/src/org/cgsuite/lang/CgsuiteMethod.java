package org.cgsuite.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cgsuite.CgsuiteException;
import org.cgsuite.RationalNumber;

public class CgsuiteMethod extends CgsuiteObject implements Callable
{
    private String name;
    private List<Parameter> parameters;
    private int nRequiredParameters;
    private CgsuiteTree tree;
    private CgsuiteClass declaringClass;
    private boolean isConstructor;

    private String javaMethodSpec;
    private String javaMethodName;
    private Class<?>[] javaParameterTypes;
    private Method javaMethod;
    private Constructor javaConstructor;

    public CgsuiteMethod(CgsuiteClass declaringClass, String name, List<Parameter> parameters, CgsuiteTree tree, String javaMethodSpec)
        throws CgsuiteException
    {
        super(CgsuitePackage.forceLookupClass("Method"));

        this.name = name;
        this.declaringClass = declaringClass;
        this.parameters = parameters;
        this.javaMethodSpec = javaMethodSpec;
        this.isConstructor = name.equals(declaringClass.getName());

        for (Parameter p : parameters)
        {
            if (p.optional)
            {
                if (tree == null)
                    throw new IllegalArgumentException();
                break;
            }
            nRequiredParameters++;
        }

        this.tree = tree;
    }

    private void ensureLoaded()
    {
        if (tree != null || javaMethod != null)
            return;

        javaParameterTypes = new Class<?>[parameters.size()];
        int parenpos = javaMethodSpec.indexOf('(');
        if (parenpos == -1)
        {
            // Implicit parameter types.
            javaMethodName = javaMethodSpec;
            for (int i = 0; i < javaParameterTypes.length; i++)
            {
                javaParameterTypes[i] = parameters.get(i).type.getJavaClass();
            }
        }
        else try
        {
            // TODO Validate closing paren
            javaMethodName = javaMethodSpec.substring(0, parenpos);
            String[] parameterNames = javaMethodSpec.substring(parenpos+1, javaMethodSpec.length()-1).split(",");
            // TODO Validate number of parameters
            for (int i = 0; i < parameterNames.length; i++)
            {
                if ("int".equals(parameterNames[i]))
                    javaParameterTypes[i] = int.class;
                else
                    javaParameterTypes[i] = Class.forName(parameterNames[i]);
            }
        }
        catch (ClassNotFoundException exc)
        {
            throw new IllegalArgumentException("Unknown Java class: " + exc.getMessage(), exc);
        }
        if (javaMethodSpec.equals(declaringClass.getName()))
        {
            try
            {
                javaConstructor = declaringClass.getJavaClass().getConstructor(javaParameterTypes);
            }
            catch (NoSuchMethodException exc)
            {
                throw new IllegalArgumentException("Java constructor not found: " + exc.getMessage(), exc);
            }
        }
        else
        {
            try
            {
                javaMethod = declaringClass.getJavaClass().getMethod(javaMethodName, javaParameterTypes);
            }
            catch (NoSuchMethodException exc)
            {
                throw new IllegalArgumentException("Java method not found: " + exc.getMessage(), exc);
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public void setDeclaringClass(CgsuiteClass declaringClass)
    {
        this.declaringClass = declaringClass;
    }

    public CgsuiteClass getDeclaringClass()
    {
        return declaringClass;
    }

    public Method getJavaMethod()
    {
        ensureLoaded();
        return javaMethod;
    }

    @Override
    public CgsuiteObject invoke(List<CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
        throws CgsuiteException
    {
        return invoke(CgsuiteObject.NIL, arguments, optionalArguments);
    }

    public CgsuiteObject invoke(CgsuiteObject obj, List<CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
        throws CgsuiteException
    {
        ensureLoaded();

        if (arguments.size() < nRequiredParameters || arguments.size() > parameters.size())
            throw new IllegalArgumentException();

        CgsuiteObject retval;

        if (tree == null)
        {
            Object[] castArguments = new Object[arguments.size()];

            for (int i = 0; i < arguments.size(); i++)
            {
                castArguments[i] = cast(arguments.get(i), javaParameterTypes[i]);
            }
            // TODO Optional arguments
            Object javaObj;

            try
            {
                if (javaMethod == null)
                {
                    javaObj = javaConstructor.newInstance(castArguments);
                }
                else
                {
                    javaObj = javaMethod.invoke(obj, castArguments);
                }
            }
            catch (IllegalArgumentException exc)
            {
                throw new InputException("Type mismatch during a call to " + name + ".", exc);
            }
            catch (InvocationTargetException exc)
            {
                if (exc.getTargetException() instanceof IllegalArgumentException)
                    throw new InputException(exc.getTargetException().getMessage(), exc.getTargetException());
                else
                    throw new InputException("Java error invoking " + name + ": " + exc.getTargetException().getMessage(), exc.getTargetException());
            }
            catch (Exception exc)
            {
                throw new InputException("Java exception thrown during a call to " + name + ".", exc);
            }

            // TODO Other casts
            if (javaObj == null)
            {
                return CgsuiteObject.NIL;
            }
            else if (javaObj instanceof CgsuiteObject)
            {
                return (CgsuiteObject) javaObj;
            }
            else if (javaObj instanceof Integer)
            {
                return new RationalNumber((Integer) javaObj, 1);
            }
            else if (javaObj instanceof Boolean)
            {
                return CgsuiteBoolean.valueOf((Boolean) javaObj);
            }
            else if (javaObj instanceof Set)
            {
                CgsuiteSet set = new CgsuiteSet();
                for (Object element : (Set<?>) javaObj)
                {
                    set.add((CgsuiteObject) element);
                }
                return set;
            }
            else
            {
                throw new RuntimeException();
            }
        }
        else
        {
            Domain domain = new Domain();
            domain.put("this", obj);

            // TODO Validation

            for (int i = 0; i < nRequiredParameters; i++)
            {
                domain.put(parameters.get(i).name, arguments.get(i));
            }

            for (int i = nRequiredParameters; i < parameters.size(); i++)
            {
                Parameter p = parameters.get(i);
                if (optionalArguments != null && optionalArguments.containsKey(p.name))
                {
                    domain.put(p.name, optionalArguments.get(p.name));
                }
                else if (arguments.size() > i)
                {
                    domain.put(p.name, arguments.get(i));
                }
                else if (p.defaultValue == null)
                {
                    domain.put(p.name, CgsuiteObject.NIL);
                }
                else
                {
                    CgsuiteObject defaultValue = domain.expression(p.defaultValue);
                    domain.put(p.name, defaultValue);
                }
            }

            if (isConstructor)
            {
                CgsuiteObject newObj;
                try
                {
                    newObj = declaringClass.getDefaultJavaConstructor().newInstance(declaringClass);
                }
                catch (Exception exc)
                {
                    throw new RuntimeException(exc);
                }
                domain.put("this", newObj);
                retval = newObj;
                try
                {
                    domain.statementSequence(tree);
                }
                catch (ReturnException exc)
                {
                }
            }
            else
            {
                try
                {
                    retval = domain.statementSequence(tree);
                }
                catch (ReturnException exc)
                {
                    retval = exc.getRetval();
                }
            }
        }

        return retval;
    }
    
    public static Object cast(CgsuiteObject obj, Class<?> javaClass) throws CgsuiteException
    {
        try
        {
            // Is the Java class of this object compatible with the ancestor
            // Java class that defined this method?
            if (javaClass.isAssignableFrom(obj.getClass()))
            {
                return obj;
            }
            else if (int.class.equals(javaClass) && obj instanceof RationalNumber)
            {
                // TODO: isInteger, isSmall validation
                return ((RationalNumber) obj).intValue();
            }
            else
            {
                // Do a constructor cast.
                // TODO configurable casts
                // TODO cache constructors for faster reflection
                Constructor<?> ctor = javaClass.getConstructor(obj.getClass());
                return (CgsuiteObject) ctor.newInstance(obj);
            }
        }
        catch (NoSuchMethodException exc)
        {
            throw new InputException(
                "No rules for converting " + obj.getClass().getName() + " to " + javaClass.getName() + "."
                );
        }
        catch (Exception exc)
        {
            throw new InputException(
                "Cannot convert to " + javaClass.getName() + ": " + obj.toString(),
                exc
                );
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return this == obj;
    }

    @Override
    public int hashCode()
    {
        return declaringClass.hashCode() ^ name.hashCode();
    }

    public static class Parameter
    {
        private String name;
        private CgsuiteClass type;
        private boolean optional;
        private CgsuiteTree defaultValue;

        public Parameter(String name, CgsuiteClass type, boolean optional, CgsuiteTree defaultValue)
        {
            this.name = name;
            this.type = type;
            this.optional = optional;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString()
        {
            return "Parameter[" + name + "," + type.getName() + "," + optional + "]";
        }
    }
}