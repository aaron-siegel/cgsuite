package org.cgsuite.lang;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CgsuiteClass extends CgsuiteObject
{
    public final static CgsuiteClass OBJECT;
    public final static CgsuiteClass CLASS;

    private static int nextDeclNumber;

    static
    {
        OBJECT = new CgsuiteClass();
        CLASS = new CgsuiteClass();

        OBJECT.type = CLASS.type = CLASS;
        CgsuiteObject.NIL.type = OBJECT;

        OBJECT.init("Object", Collections.<CgsuiteClass>emptySet(), CgsuiteObject.class.getName());
        CLASS.init("Class", Collections.singleton(OBJECT), CgsuiteClass.class.getName());
    }

    private String name;
    private boolean loaded;
    private boolean declarationsLoaded;
    private int declNumber;
    private Set<CgsuiteClass> parents;
    private Set<CgsuiteClass> ancestors;
    private Map<String,CgsuiteMethod> methods;
    private Map<String,CgsuiteVar> vars;

    private Set<CgsuiteClass> descendants;

    private String javaClassname;
    private Class<? extends CgsuiteObject> javaClass;
    private Constructor<? extends CgsuiteObject> defaultJavaConstructor;

    private CgsuiteClass()
    {
    }

    public CgsuiteClass(String name, Collection<CgsuiteClass> parents, String javaClassname)
    {
        super(CLASS);

        init(name, parents, javaClassname);
    }

    private void init(String name, Collection<CgsuiteClass> parents, String javaClassname)
    {
        this.name = name;
        this.loaded = false;
        this.declarationsLoaded = false;
        this.declNumber = nextDeclNumber++;
        this.parents = new HashSet<CgsuiteClass>();
        this.ancestors = new HashSet<CgsuiteClass>();
        this.methods = new HashMap<String,CgsuiteMethod>();
        this.vars = new HashMap<String,CgsuiteVar>();
        this.descendants = new HashSet<CgsuiteClass>();

        this.parents.addAll(parents);
        this.methods = new HashMap<String,CgsuiteMethod>();

        for (CgsuiteClass parent : parents)
        {
            this.ancestors.addAll(parent.ancestors);
            for (CgsuiteMethod method : parent.methods.values())
            {
                if (methods.containsKey(method.getName()))
                {
                    if (methods.get(method.getName()) != method)
                        throw new RuntimeException(); // XXX Refine
                }
                else
                {
                    methods.put(method.getName(), method);
                }
            }
        }

        for (CgsuiteClass ancestor : ancestors)
        {
            ancestor.descendants.add(this);
        }

        this.ancestors.add(this);

        this.javaClassname = javaClassname;
    }

    public Collection<CgsuiteClass> getParents()
    {
        return parents;
    }

    @Override
    public CgsuiteObject resolve(String str)
    {
        ensureFullyLoaded();
        return super.resolve(str);
    }

    public void load()
    {
        if (loaded)
            return;
        
        try
        {
            this.javaClass = Class.forName(javaClassname).asSubclass(CgsuiteObject.class);
            try
            {
                this.defaultJavaConstructor = this.javaClass.getConstructor(CgsuiteClass.class);
            }
            catch (NoSuchMethodException exc)
            {
                this.defaultJavaConstructor = null;
            }
            this.loaded = true;
        }
        catch (ClassNotFoundException exc)
        {
            throw new IllegalArgumentException(exc);
        }
    }

    public void ensureFullyLoaded()
    {
        load();
        if (!declarationsLoaded)
        {
            Domain.CLASS_DOMAIN.loadDeclarations(this);
            this.declarationsLoaded = true;
        }
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public String getName()
    {
        return name;
    }

    public int getDeclNumber()
    {
        return declNumber;
    }

    @Override
    public boolean equals(Object obj)
    {
        return this == obj;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return "Class[" + name + "]";
    }

    public Class<? extends CgsuiteObject> getJavaClass()
    {
        load();
        return javaClass;
    }

    public Constructor<? extends CgsuiteObject> getDefaultJavaConstructor()
    {
        return defaultJavaConstructor;
    }

    public void declareMethod(CgsuiteMethod method)
    {
        this.methods.put(method.getName(), method);

        for (CgsuiteClass descendant : descendants)
        {
            descendant.declareMethod(method);
        }
    }

    public CgsuiteObject lookup(String name)
    {
        ensureFullyLoaded();
        return methods.get(name);
    }

    public CgsuiteMethod lookupConstructor()
    {
        ensureFullyLoaded();
        // TODO Enforce class-named vars/methods are constructors
        return methods.get(this.name);
    }

    public Collection<CgsuiteMethod> allMethods()
    {
        return methods.values();
    }

    public Collection<CgsuiteVar> allVars()
    {
        return vars.values();
    }
}
