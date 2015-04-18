/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author asiegel
 */
public class CgsuiteMethodGroup extends CgsuiteObject implements Callable
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("MethodGroup");

    String name;
    List<CgsuiteMethod> methods;
    
    public CgsuiteMethodGroup(String name)
    {
        super(TYPE);
        this.name = name;
        this.methods = new ArrayList<CgsuiteMethod>();
    }
    
    public boolean isStatic()
    {
        return firstMethod().isStatic();
    }
    
    public int getMethodCount()
    {
        return methods.size();
    }
    
    public List<CgsuiteMethod> getMethods()
    {
        return methods;
    }
    
    public CgsuiteMethod firstMethod()
    {
        return methods.get(0);
    }
    
    void addMethod(CgsuiteMethod method)
    {
        if (!name.equals(method.getName()))
        {
            throw new IllegalArgumentException(name + " != " + method.getName());
        }
        if (!methods.isEmpty() && method.isStatic() != firstMethod().isStatic())
        {
            throw new InputException("Cannot mix static and non-static methods: " + name);
        }

        // If the declaring class of the specified method is a descendant of a class that has already declared
        // this method, then we insert just *before* the ancestor.  Ties are broken by declaration order.
        
        int index;
        for (index = 0; index < methods.size(); index++)
        {
            if (method.getDeclaringClass().hasAncestor(methods.get(index).getDeclaringClass()))
            {
                break;
            }
        }
        
        methods.add(index, method);
    }
    
    void addAllMethods(CgsuiteMethodGroup methodGroup)
    {
        for (CgsuiteMethod method : methodGroup.methods)
        {
            addMethod(method);
        }
    }

    @Override
    public CgsuiteObject invoke(List<? extends CgsuiteObject> arguments, Map<String, CgsuiteObject> optionalArguments) throws CgsuiteException
    {
        for (CgsuiteMethod method : methods)
        {
            try
            {
                return method.invoke(arguments, optionalArguments);
            }
            catch (ArgumentValidationException exc)
            {
            }
        }
        
        throw new InputException("No match for method " + name);   // TODO Improve message
    }
    
    public CgsuiteObject invoke(CgsuiteObject obj, List<? extends CgsuiteObject> arguments, Map<String, CgsuiteObject> optionalArguments) throws CgsuiteException
    {
        for (CgsuiteMethod method : methods)
        {
            try
            {
                return method.invoke(obj, arguments, optionalArguments);
            }
            catch (ArgumentValidationException exc)
            {
            }
        }
        
        throw new InputException("No match for method " + name);   // TODO Improve message
    }
    
}
