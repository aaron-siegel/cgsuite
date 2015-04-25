package org.cgsuite.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cgsuite.lang.game.RationalNumber;
import org.cgsuite.lang.parser.CgsuiteTree;

public class CgsuiteMethod extends CgsuiteObject implements Callable
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Method");
    
    public final static List<CgsuiteObject> EMPTY_PARAM_LIST = Collections.emptyList();
    public final static Map<String,CgsuiteObject> EMPTY_PARAM_MAP = Collections.emptyMap();
    
    private final static Logger log = Logger.getLogger(CgsuiteMethod.class.getName());

    private CgsuiteClass declaringClass;
    private String name;
    private EnumSet<Modifier> modifiers;
    private List<Parameter> parameters;
    private Set<String> optionalParameterNames;
    private int nRequiredParameters;
    private CgsuiteTree tree;
    private boolean isConstructor;
    private boolean hasVarargParameter;

    private String javaMethodSpec;
    private String javaMethodName;
    private Method javaMethod;
    private Constructor javaConstructor;
    private Class<?>[] javaParameterTypes;

    public CgsuiteMethod(CgsuiteClass declaringClass, String name, EnumSet<Modifier> modifiers, List<Parameter> parameters, CgsuiteTree tree, String javaMethodSpec)
        throws CgsuiteException
    {
        super(TYPE);

        this.declaringClass = declaringClass;
        this.name = name;
        this.modifiers = modifiers;
        this.parameters = parameters;
        this.optionalParameterNames = new HashSet<String>();
        this.tree = tree;
        this.javaMethodSpec = javaMethodSpec;
        this.isConstructor = name.equals(declaringClass.getName());
        
        if (isConstructor)
        {
            // Constructors are always mutable, even if the declaring class is not
            this.modifiers.add(Modifier.MUTABLE);
        }

        for (Parameter p : parameters)
        {
            if (p.optional)
                optionalParameterNames.add(p.name);
            else if (p.vararg)
                hasVarargParameter = true;
            else
                nRequiredParameters++;  // We specifically DON'T count the vararg parameter here
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
        else
        {
            javaMethodName = javaMethodSpec.substring(0, parenpos);
            String[] parameterNames = javaMethodSpec.substring(parenpos+1, javaMethodSpec.length()-1).split(",");
            
            if (javaMethodSpec.charAt(javaMethodSpec.length()-1) != ')')
                throw new InputException("Ill-formed Java spec in method declaration: " + getQualifiedName());
            
            if (parameterNames.length != javaParameterTypes.length)
                throw new InputException("Expecting " + javaParameterTypes.length + " parameters for Java method: " + getQualifiedName());
            
            for (int i = 0; i < parameterNames.length; i++)
            {
                if ("int".equals(parameterNames[i]))
                {
                    javaParameterTypes[i] = int.class;
                }
                else if ("boolean".equals(parameterNames[i]))
                {
                    javaParameterTypes[i] = boolean.class;
                }
                else try
                {
                    javaParameterTypes[i] = Class.forName(parameterNames[i]);
                }
                catch (ClassNotFoundException exc)
                {
                    throw new InputException("Unknown Java class (" + parameterNames[i] + ") in method declaration: " + getQualifiedName());
                }
            }
        }

        if (javaMethodName.equals(declaringClass.getJavaClass().getSimpleName()))
        {
            if (!isConstructor)
            {
                throw new InputException("Java constructor specified, but CGSuite method is not a constructor: " + getQualifiedName());
            }

            try
            {
                javaConstructor = declaringClass.getJavaClass().getConstructor(javaParameterTypes);
            }
            catch (NoSuchMethodException exc)
            {
                throw new InputException("Java constructor not found for method: " + getQualifiedName());
            }
        }
        else
        {
            if (isConstructor)
            {
                throw new InputException("Ordinary Java method specified, but CGSuite method is a constructor: " + getQualifiedName());
            }
            
            try
            {
                javaMethod = declaringClass.getJavaClass().getMethod(javaMethodName, javaParameterTypes);
            }
            catch (NoSuchMethodException exc)
            {
                throw new InputException("Java method not found for method: " + getQualifiedName());
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
    
    public boolean isMutableMethod()
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
    
    public List<Parameter> getParameters()
    {
        return parameters;
    }
    
    public CgsuiteTree getTree()
    {
        return tree;
    }

    @Override
    public CgsuiteObject invoke(List<? extends CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
        throws CgsuiteException
    {
        return invoke((CgsuiteObject) null, arguments, optionalArguments);
    }

    public CgsuiteObject invoke(CgsuiteObject obj, List<? extends CgsuiteObject> arguments, Map<String,CgsuiteObject> optionalArguments)
        throws CgsuiteException
    {
        ensureLoaded();
        
        if (arguments.size() < nRequiredParameters)
            throw new ArgumentValidationException("Expecting at least " + nRequiredParameters + " argument(s) for method call: " + getQualifiedName());

        if (!hasVarargParameter && arguments.size() > parameters.size())
            throw new ArgumentValidationException("Expecting at most " + parameters.size() + " argument(s) for method call: " + getQualifiedName());
        
        // Try to validate the arguments.
        
        boolean valid = true;

        for (int i = 0; valid && i < nRequiredParameters; i++)
        {
            Parameter p = parameters.get(i);
            CgsuiteObject value = arguments.get(i);
            valid &= validate(value, p.type, p.name);
        }

        if (valid && hasVarargParameter)
        {
            Parameter p = parameters.get(nRequiredParameters);
            for (int i = nRequiredParameters; valid && i < arguments.size(); i++)
            {
                CgsuiteObject value = arguments.get(i);
                valid &= validate(value, p.type, p.name);
            }
        }
        
        if (!valid)
        {
            throw new ArgumentValidationException("Invalid arguments in call to " + getQualifiedName() + ".");
        }

        if (isMutableMethod() && obj != null)
        {
            if (!obj.isMutable())
                throw new InputException("Cannot call mutable method on member of immutable object: " + getQualifiedName());

            obj.unlinkIfNecessary();
        }

        CgsuiteObject retval;
        boolean constructing = false;

        if (tree == null)
        {
            Object[] castArguments = new Object[parameters.size()];

            for (int i = 0; i < arguments.size(); i++)
            {
                castArguments[i] = cast(arguments.get(i), javaParameterTypes[i], true);
            }
            for (int i = arguments.size(); i < parameters.size(); i++)
            {
                Parameter p = parameters.get(i);
                if (optionalArguments.containsKey(p.name))
                {
                    castArguments[i] = cast(optionalArguments.get(p.name), javaParameterTypes[i], true);
                }
                else if (arguments.size() > i)
                {
                    castArguments[i] = cast(arguments.get(i), javaParameterTypes[i], true);
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
                if (isConstructor)
                {
                    constructing = true;
                    javaObj = javaConstructor.newInstance(castArguments);
                }
                else
                {
                    Object castObj = (obj == null)? null : cast(obj, getDeclaringClass().getJavaClass(), false);
                    javaObj = javaMethod.invoke(castObj, castArguments);
                }
            }
            catch (InputException exc)
            {
                throw exc;
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
                    String msg = "Java error invoking " + name + ": " + exc.getTargetException().getMessage();
                    log.log(Level.WARNING, msg, exc.getTargetException());
                    throw new InputException(msg, exc.getTargetException());
                }
            }
            catch (Throwable exc)
            {
                throw new InputException("Java exception thrown during a call to " + name + ": " + exc.getMessage(), exc);
            }
            
            retval = castReturn(javaObj);
        }
        else
        {
            if (isConstructor && obj == null)
            {
                constructing = true;
                // Create the object.
                try
                {
                    assert declaringClass.getDefaultJavaConstructor() != null;
                    obj = declaringClass.getDefaultJavaConstructor().newInstance(declaringClass);
                }
                catch (Throwable exc)
                {
                    throw new InputException("Java exception thrown during a call to " + name + ".", exc);
                }
            }
            
            Domain domain = new Domain(obj, this, declaringClass.getPackageImports(), declaringClass.getClassImports());

            for (int i = 0; i < nRequiredParameters; i++)
            {
                domain.put(parameters.get(i).name, arguments.get(i));
            }
            
            if (hasVarargParameter)
            {
                CgsuiteList varargList = new CgsuiteList(arguments.size()-nRequiredParameters);
                for (int i = nRequiredParameters; i < arguments.size(); i++)
                {
                    varargList.add(arguments.get(i));
                }
                domain.put(parameters.get(nRequiredParameters).name, varargList);
            }
                    
            int nOptionalArgumentsProcessed = 0;

            for (int i = nRequiredParameters + (hasVarargParameter ? 1 : 0); i < parameters.size(); i++)
            {
                Parameter p = parameters.get(i);
                assert p.optional;
                CgsuiteObject value;
                if (optionalArguments.containsKey(p.name))
                {
                    if (!hasVarargParameter && arguments.size() > i)
                    {
                        throw new InputException("Duplicate parameter: " + p.name + " (in call to method " + getQualifiedName() + ")");
                    }
                    value = optionalArguments.get(p.name);
                    nOptionalArgumentsProcessed++;
                }
                else if (!hasVarargParameter && arguments.size() > i)
                {
                    value = arguments.get(i);
                }
                else if (p.defaultValue == null)
                {
                    value = CgsuiteObject.NIL;
                }
                else
                {
                    value = domain.expression(p.defaultValue);
                }
                if (!validate(value, p.type, p.name))
                {
                    throw new InputException("Invalid arguments in call to " + getQualifiedName() + ": parameter \"" + p.name + "\" must be of class " + p.type.getQualifiedName() + ".");
                }
                domain.put(p.name, value);
            }
            
            if (nOptionalArgumentsProcessed != optionalArguments.size())
            {
                // There must be an invalid optional argument.  Find it and generate an error.
                for (String str : optionalArguments.keySet())
                {
                    if (!optionalParameterNames.contains(str))
                        throw new InputException("Not a valid optional parameter: " + str + " (in call to method " + getQualifiedName() + ")");
                }
                
                assert false : "All arguments matched, but the count was off";
            }

            retval = domain.methodInvocation(tree);
            
            if (constructing)
            {
                retval = obj;
            }
        }
        
        if (constructing)
        {
            // If the class is not mutable, then the object is
            // marked immutable once constructed.

            if (!declaringClass.isMutableClass())
                retval.markImmutable();
        }

        return retval;
    }
    
    private boolean validate(CgsuiteObject obj, CgsuiteClass expectedClass, String paramName)
    {
        return obj == NIL || obj.getCgsuiteClass().hasAncestor(expectedClass);
        /*if (obj != NIL && !obj.getCgsuiteClass().hasAncestor(expectedClass))
        {
            throw new InputException("Invalid arguments in call to " + getQualifiedName() + ": parameter \"" + paramName + "\" must be of class " + expectedClass.getQualifiedName() + ".");
        }*/
    }
    
    public static Object cast(CgsuiteObject obj, Class<?> javaClass, boolean crosslink)
    {
        try
        {
            // Is the Java class of this object compatible with the ancestor
            // Java class that defined this method?
            if (javaClass.isAssignableFrom(obj.getClass()))
            {
                return crosslink ? obj.createCrosslink() : obj;
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
            else if (int.class.equals(javaClass) && obj instanceof RationalNumber && ((RationalNumber) obj).isSmallInteger())
            {
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
            exc.printStackTrace();
            throw new InputException(
                "No rules for converting " + obj.getClass().getName() + " to " + javaClass.getName() + "."
                );
        }
        catch (InvocationTargetException exc)
        {
            if (exc.getCause() instanceof InputException)
            {
                InputException ie = (InputException) exc.getCause();
                throw ie;
            }
            else
            {
                throw new InputException(
                    "Cannot convert to " + javaClass.getName() + ": " + obj.toString(),
                    exc
                    );
            }
        }
        catch (Exception exc)
        {
            throw new InputException(
                "Cannot convert to " + javaClass.getName() + ": " + obj.toString(),
                exc
                );
        }
    }
    
    public CgsuiteObject castReturn(Object javaObj)
    {
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
        else if (javaObj instanceof Short)
        {
            return new CgsuiteInteger((Short) javaObj);
        }
        else if (javaObj instanceof Long)
        {
            return new CgsuiteInteger((Long) javaObj);
        }
        else if (javaObj instanceof Boolean)
        {
            return CgsuiteBoolean.valueOf((Boolean) javaObj);
        }
        else if (javaObj instanceof String)
        {
            return new CgsuiteString((String) javaObj);
        }
        else if (javaObj instanceof List)
        {
            CgsuiteList list = new CgsuiteList();
            for (Object element : (List<?>) javaObj)
            {
                list.add(castReturn(element));
            }
            return list;
        }
        else if (javaObj instanceof Set)
        {
            CgsuiteSet set = new CgsuiteSet();
            for (Object element : (Set<?>) javaObj)
            {
                set.add(castReturn(element));
            }
            return set;
        }
        else if (javaObj instanceof Object[])
        {
            CgsuiteList list = new CgsuiteList();
            for (Object element : (Object[]) javaObj)
            {
                list.add(castReturn(element));
            }
            return list;
        }
        else if (javaObj instanceof int[])
        {
            CgsuiteList list = new CgsuiteList();
            for (int element : (int[]) javaObj)
            {
                list.add(new CgsuiteInteger(element));
            }
            return list;
        }
        else if (javaObj instanceof short[])
        {
            CgsuiteList list = new CgsuiteList();
            for (short element : (short[]) javaObj)
            {
                list.add(new CgsuiteInteger(element));
            }
            return list;
        }
        else if (javaObj instanceof BigInteger)
        {
            return new RationalNumber((BigInteger) javaObj, BigInteger.ONE);
        }
        else
        {
            throw new InputException("A call to " + name + " returned an incompatible object.");
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
        private boolean vararg;

        public Parameter(String name, CgsuiteClass type, boolean optional, CgsuiteTree defaultValue, boolean vararg)
        {
            this.name = name;
            this.type = type;
            this.optional = optional;
            this.defaultValue = defaultValue;
            this.vararg = vararg;
        }

        @Override
        public String toString()
        {
            return "Parameter[" + name + "," + type.getName() + "," + optional + "]";
        }

        public CgsuiteTree getDefaultValue()
        {
            return defaultValue;
        }

        public String getName()
        {
            return name;
        }

        public boolean isOptional()
        {
            return optional;
        }

        public CgsuiteClass getType()
        {
            return type;
        }
        
        public boolean isVararg()
        {
            return vararg;
        }
    }
}