package org.cgsuite.lang;

import org.cgsuite.lang.parser.CgsuiteTree;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.cgsuite.lang.game.RationalNumber;

public class CgsuiteMethod extends CgsuiteObject implements Callable
{
    private final static Logger log = Logger.getLogger(CgsuiteMethod.class.getName());

    private CgsuiteClass declaringClass;
    private String name;
    private EnumSet<Modifier> modifiers;
    private List<Parameter> parameters;
    private int nRequiredParameters;
    private CgsuiteTree tree;
    private boolean isConstructor;

    private String javaMethodSpec;
    private String javaMethodName;
    private Method javaMethod;
    private Constructor javaConstructor;
    private Class<?>[] javaParameterTypes;

    public CgsuiteMethod(CgsuiteClass declaringClass, String name, EnumSet<Modifier> modifiers, List<Parameter> parameters, CgsuiteTree tree, String javaMethodSpec)
        throws CgsuiteException
    {
        super(CgsuitePackage.forceLookupClass("Method"));

        this.declaringClass = declaringClass;
        this.name = name;
        this.modifiers = modifiers;
        this.parameters = parameters;
        this.tree = tree;
        this.javaMethodSpec = javaMethodSpec;
        this.isConstructor = name.equals(declaringClass.getName());

        for (Parameter p : parameters)
        {
            if (!p.optional)
                nRequiredParameters++;
        }
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
                else if ("boolean".equals(parameterNames[i]))
                    javaParameterTypes[i] = boolean.class;
                else
                    javaParameterTypes[i] = Class.forName(parameterNames[i]);
            }
        }
        catch (ClassNotFoundException exc)
        {
            throw new IllegalArgumentException("Unknown Java class: " + exc.getMessage(), exc);
        }

        if (javaMethodName.equals(declaringClass.getName()))
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

    public CgsuiteClass getDeclaringClass()
    {
        return declaringClass;
    }

    public String getName()
    {
        return name;
    }

    public String getQualifiedName()
    {
        return declaringClass.getQualifiedName() + "." + name;
    }

    public EnumSet<Modifier> getModifiers()
    {
        return modifiers;
    }
    
    public boolean isMutable()
    {
        return modifiers.contains(Modifier.MUTABLE);
    }

    public boolean isStatic()
    {
        return modifiers.contains(Modifier.STATIC);
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
        return invoke((CgsuiteObject) null, arguments, optionalArguments);
    }

    public CgsuiteObject invoke(CgsuiteObject obj, List<CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
        throws CgsuiteException
    {
        ensureLoaded();
        
        if (isMutable())
        {
            assert obj != null;
            obj.unlinkIfNecessary();
        }

//        log.info("Invoking: " + declaringClass.getName() + "." + name);

        if (arguments.size() < nRequiredParameters)
            throw new InputException("Call to " + getQualifiedName() + " requires at least " + nRequiredParameters + " parameters.");

        if (arguments.size() > parameters.size())
            throw new InputException("Call to " + getQualifiedName() + " accepts at most " + parameters.size() + " parameters.");

        CgsuiteObject retval;

        if (tree == null)
        {
            Object[] castArguments = new Object[parameters.size()];

            for (int i = 0; i < arguments.size(); i++)
            {
                castArguments[i] = cast(arguments.get(i), javaParameterTypes[i]);
            }
            for (int i = arguments.size(); i < parameters.size(); i++)
            {
                Parameter p = parameters.get(i);
                if (optionalArguments != null && optionalArguments.containsKey(p.name))
                {
                    castArguments[i] = cast(optionalArguments.get(p.name), javaParameterTypes[i]);
                }
                else if (arguments.size() > i)
                {
                    castArguments[i] = cast(arguments.get(i), javaParameterTypes[i]);
                }
                else if (p.defaultValue == null)
                {
                    castArguments[i] = null;
                }
                else
                {
                    throw new InputException("Default values not yet supported for Java methods");
                }
            }

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
                throw new InputException("Type mismatch during a call to " + getQualifiedName() + ".", exc);
            }
            catch (InvocationTargetException exc)
            {
                if (exc.getTargetException() instanceof InputException)
                {
                    // InputException is interpreted as an "expected" error message intended for the user.
                    InputException ie = (InputException) exc.getTargetException();
                    ie.setInvocationTarget(getQualifiedName());
                    throw ie;
                }
                else if (exc.getTargetException() instanceof IllegalArgumentException)
                {
                    throw new InputException(exc.getTargetException().getMessage(), exc.getTargetException());
                }
                else
                {
                    throw new InputException("Java error invoking " + name + ": " + exc.getTargetException().getMessage(), exc.getTargetException());
                }
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
                return new CgsuiteInteger((Integer) javaObj);
            }
            else if (javaObj instanceof Boolean)
            {
                return CgsuiteBoolean.valueOf((Boolean) javaObj);
            }
            else if (javaObj instanceof String)
            {
                return new CgsuiteString((String) javaObj);
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
            else if (javaObj instanceof BigInteger)
            {
                return new RationalNumber((BigInteger) javaObj, BigInteger.ONE);
            }
            else
            {
                throw new RuntimeException();
            }
        }
        else
        {
            Domain domain = new Domain(declaringClass, declaringClass.getImports());
            if (obj != null)
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

            if (isConstructor && obj == null)
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
                domain.invocation(tree);
            }
            else
            {
                retval = domain.invocation(tree);
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
            else if (obj == CgsuiteObject.NIL)
            {
                return null;
            }
            else if (boolean.class.equals(javaClass) && obj instanceof CgsuiteBoolean)
            {
                return ((CgsuiteBoolean) obj).booleanValue();
            }
            else if (int.class.equals(javaClass) && obj instanceof CgsuiteInteger)
            {
                return ((CgsuiteInteger) obj).intValue();
            }
            else if (int.class.equals(javaClass) && obj instanceof RationalNumber)
            {
                // TODO: isInteger, isSmall validation
                return ((RationalNumber) obj).intValue();
            }
            else if (String.class.equals(javaClass) && obj instanceof CgsuiteString)
            {
                return ((CgsuiteString) obj).toJavaString();
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