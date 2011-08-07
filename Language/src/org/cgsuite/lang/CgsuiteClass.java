package org.cgsuite.lang;

import org.antlr.runtime.Token;
import java.util.logging.Logger;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.cgsuite.lang.CgsuiteMethod.Parameter;
import org.cgsuite.lang.parser.CgsuiteLexer;
import org.cgsuite.lang.parser.CgsuiteParser;
import org.cgsuite.lang.parser.CgsuiteTree;
import org.cgsuite.lang.parser.CgsuiteTreeAdaptor;
import org.cgsuite.lang.parser.MalformedParseTreeException;
import org.cgsuite.lang.parser.SourcedAntlrInputStream;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import static org.cgsuite.lang.parser.CgsuiteParser.*;

public class CgsuiteClass extends CgsuiteObject implements FileChangeListener
{
    private static final Logger log = Logger.getLogger(CgsuiteClass.class.getName());
    
    public final static CgsuiteClass OBJECT_TYPE;
    public final static CgsuiteClass TYPE;
    public final static CgsuiteClass NIL_TYPE;

    static
    {
        OBJECT_TYPE = new CgsuiteClass();
        TYPE = new CgsuiteClass();
        NIL_TYPE = new CgsuiteClass();

        OBJECT_TYPE.name = "Object";
        TYPE.name = "Class";
        NIL_TYPE.name = "Nil";
        OBJECT_TYPE.type = TYPE.type = NIL_TYPE.type = TYPE;
        NIL.type = NIL_TYPE;
    }

    private CgsuitePackage enclosingPackage;
    private List<CgsuitePackage> imports;
    private FileObject fo;
    private String name;
    private boolean loaded;

    private CgsuiteTree parseTree;
    private CgsuiteTree script;

    private EnumSet<Modifier> classModifiers;
    private Set<CgsuiteClass> parents;
    private Set<CgsuiteClass> ancestors;
    private Map<String,CgsuiteMethod> methods;
    private Map<String,Variable> vars;
    private List<Variable> varsInOrder;

    private Set<CgsuiteClass> descendants;

    private String javaClassname;
    private Class<? extends CgsuiteObject> javaClass;
    private Constructor<? extends CgsuiteObject> defaultJavaConstructor;

    private CgsuiteClass()
    {
    }

    public CgsuiteClass(FileObject fo, CgsuitePackage enclosingPackage)
    {
        super(TYPE);

        this.name = fo.getName();
        setFileObject(fo, enclosingPackage);
    }

    public final void setFileObject(FileObject fo, CgsuitePackage enclosingPackage)
    {
        if (!this.name.equals(fo.getName()))
            throw new IllegalArgumentException(this.name + " != " + fo.getName());

        if (this.fo != null)
            this.fo.removeFileChangeListener(this);
        
        this.fo = fo;
        this.fo.addFileChangeListener(this);
        this.enclosingPackage = enclosingPackage;
        this.loaded = false;
    }

    public Collection<CgsuiteClass> getParents()
    {
        ensureLoaded();
        return parents;
    }

    @Override
    public CgsuiteObject resolve(String identifier, CgsuiteMethod contextMethod, boolean localAccess)
    {
        ensureLoaded();
        
        CgsuiteMethod getter = lookupMethod(identifier + "$get");

        if (getter != null && getter.isStatic())
            return getter.invoke((CgsuiteObject) null, CgsuiteObject.EMPTY_LIST, null);

        CgsuiteMethod method = lookupMethod(identifier);

        if (method != null && method.isStatic())
            return method;
        
        Variable var = lookupVar(identifier);
        
        if (var != null && var.isStatic())
        {
            CgsuiteObject obj = objectNamespace.get(identifier);
            return (obj == null)? NIL : obj;
        }

        return super.resolve(identifier, contextMethod, localAccess);
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public String getName()
    {
        return name;
    }

    public String getQualifiedName()
    {
        String pkgName = enclosingPackage.getName();
        if (pkgName.isEmpty())
            return name;
        else
            return pkgName + "." + name;
    }
    
    public boolean isMutableClass()
    {
        ensureLoaded();
        return classModifiers.contains(Modifier.MUTABLE);
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

    @Override
    protected int compareLike(CgsuiteObject other)
    {
        return getQualifiedName().compareTo(((CgsuiteClass) other).getQualifiedName());
    }
    
    public boolean hasAncestor(CgsuiteClass ancestor)
    {
        ensureLoaded();
        return ancestors.contains(ancestor);
    }

    public Class<? extends CgsuiteObject> getJavaClass()
    {
        ensureLoaded();
        return javaClass;
    }

    public Constructor<? extends CgsuiteObject> getDefaultJavaConstructor()
    {
        ensureLoaded();
        return defaultJavaConstructor;
    }

    public List<CgsuitePackage> getImports()
    {
        ensureLoaded();
        return imports;
    }

    public CgsuiteMethod lookupMethod(String name)
    {
        ensureLoaded();
        return methods.get(name);
    }

    public Variable lookupVar(String name)
    {
        ensureLoaded();
        return vars.get(name);
    }

    @Override
    public void assign(String name, CgsuiteObject object, CgsuiteMethod contextMethod, boolean localAccess)
    {
        Variable var = lookupVar(name);
        if (var == null)
            throw new InputException("Unknown variable: " + name);
        if (!var.getModifiers().contains(Modifier.STATIC))
            throw new InputException("Cannot reference non-static variable in static context: " + name);
        if (var.isEnumValue())
            throw new InputException("Enum constants are read-only: " + name);
        objectNamespace.put(name, object.createCrosslink());
    }

    public CgsuiteMethod lookupConstructor()
    {
        ensureLoaded();
        // TODO Enforce class-named vars/methods are constructors
        return methods.get(this.name);
    }

    public CgsuiteMap allMethods()
    {
        ensureLoaded();
        CgsuiteMap map = new CgsuiteMap();
        for (Entry<String,CgsuiteMethod> e : methods.entrySet())
        {
            map.put(new CgsuiteString(e.getKey()), e.getValue());
        }
        return map;
    }

    public CgsuiteMap allVars()
    {
        ensureLoaded();
        CgsuiteMap map = new CgsuiteMap();
        for (Entry<String,Variable> e : vars.entrySet())
        {
            map.put(e.getValue(), new CgsuiteString(e.getKey()));
        }
        return map;
    }
    
    public List<Variable> varsInOrder()
    {
        ensureLoaded();
        return varsInOrder;
    }
    
    public CgsuiteTree getScript()
    {
        ensureLoaded();
        return script;
    }

    /////////////////////////////////
    // Loader Logic

    public void ensureLoaded()
    {
        if (!loaded)
        {
            load();
        }
    }

    private void load()
    {
        log.info("Loading class: " + getQualifiedName());

        try
        {
            ANTLRInputStream in = new SourcedAntlrInputStream(fo.getInputStream(), fo.getNameExt());
            CgsuiteLexer lexer = new CgsuiteLexer(in);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CgsuiteParser parser = new CgsuiteParser(tokens);
            parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
            CgsuiteParser.compilationUnit_return r = parser.compilationUnit();
            
            if (!lexer.getErrors().isEmpty())
            {
                throw new CgsuiteClassLoadException(fo, lexer.getErrors());
            }
            else if (!parser.getErrors().isEmpty())
            {
                throw new CgsuiteClassLoadException(fo, parser.getErrors());
            }
            else
            {
                this.parseTree = (CgsuiteTree) r.getTree();
            }
        }
        catch (RecognitionException exc)
        {
            throw new CgsuiteClassLoadException(fo, Collections.singletonList(new SyntaxError(exc, exc.getMessage())));
        }
        catch (IOException exc)
        {
            throw new CgsuiteClassLoadException(fo, exc);
        }

        this.objectNamespace.clear();
        
        this.imports = new ArrayList<CgsuitePackage>();
        this.imports.addAll(CgsuitePackage.DEFAULT_IMPORT);
        if (!CgsuitePackage.DEFAULT_IMPORT.contains(enclosingPackage))
            this.imports.add(enclosingPackage);

        this.parents = new HashSet<CgsuiteClass>();
        this.ancestors = new HashSet<CgsuiteClass>();
        this.methods = new HashMap<String,CgsuiteMethod>();
        this.vars = new HashMap<String,Variable>();
        this.varsInOrder = new ArrayList<Variable>();
        this.descendants = new HashSet<CgsuiteClass>();

        classdef(parseTree.getChild(0));
        
        if (script != null)
        {
            loaded = true;
            return;
        }

        for (CgsuiteClass parent : parents)
        {
            parent.ensureLoaded();
            this.ancestors.addAll(parent.ancestors);
            for (Entry<String,CgsuiteMethod> e : parent.methods.entrySet())
            {
                String nameInParent = e.getKey();
                if (nameInParent.startsWith("super$"))
                    continue;
                CgsuiteMethod method = e.getValue();
                if (methods.containsKey(method.getName()))
                {
                    if (methods.get(method.getName()) != method)
                        throw new InputException(parseTree.getChild(0).getToken(),
                            "Multiple ancestor classes declare the same non-private method: " + method.getName());
                }
                else
                {
                    methods.put(method.getName(), method);
                    methods.put("super$" + method.getName(), method);
                }
            }
            for (Variable var : parent.vars.values())
            {
                if (!vars.containsKey(var.getName()))
                {
                    vars.put(var.getName(), var);
                    varsInOrder.add(var);
                }
            }
        }

        if (javaClassname == null)
            javaClassname = parents.iterator().next().javaClassname;    // XXX Improve this.

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
        }
        catch (ClassNotFoundException exc)
        {
            throw new InputException("Could not locate Java class for " + fo.getNameExt() + ": " + javaClassname);
        }
        catch (ClassCastException exc)
        {
            throw new InputException("The Java class for " + fo.getNameExt() + " is not a subclass of CgsuiteObject: " + javaClassname);
        }

        declarations(parseTree.getChild(0));
        
        // Mark loaded

        this.loaded = true;

        for (CgsuiteClass ancestor : ancestors)
        {
            ancestor.descendants.add(this);
        }

        this.ancestors.add(this);
        
        log.info("Loaded class : " + getQualifiedName());
        
        // Populate memory var
        
        this.objectNamespace.put("memory", new CgsuiteMap());

        // Populate statics and invokeMethod static initializers

        for (Variable var : varsInOrder)
        {
            if (var.isStatic())
            {
                CgsuiteObject initialValue = NIL;

                if (var.getInitializer() != null)
                    initialValue = new Domain(this, null, imports).expression(var.getInitializer());
                
                objectNamespace.put(var.getName(), initialValue);

                if (var.isEnumValue())
                {
                    initialValue.objectNamespace.put("literal", new CgsuiteString(var.getName()));
                    initialValue.objectNamespace.put("ordinal", new CgsuiteInteger(var.getDeclRank()));
                }
            }
        }
    }

    private void classdef(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.getToken().getType())
        {
            case CLASS:
                
                classModifiers = modifiers(tree.getChild(0));

                if (!name.equals(tree.getChild(1).getText()))
                {
                    throw new InputException(tree.getChild(1).getToken(), "Classname in file does not match filename: " + fo.getNameExt());
                }

                int i = 2;

                if (i < tree.getChildCount() && tree.getChild(i).getToken().getType() == EXTENDS)
                    extendsClause(tree.getChild(i++));
                else if (this != OBJECT_TYPE)
                    parents.add(OBJECT_TYPE);

                if (i < tree.getChildCount() && tree.getChild(i).getToken().getType() == JAVA)
                    javaClassname = javaref(tree.getChild(i++));

                break;

            case ENUM:

                classModifiers = EnumSet.noneOf(Modifier.class);
                
                if (!name.equals(tree.getChild(1).getText()))
                {
                    throw new InputException(tree.getChild(1).getToken(), "Classname in file does not match filename: " + fo.getNameExt());
                }
                parents.add(CgsuitePackage.forceLookupClass("Enum"));
                javaClassname = CgsuiteEnumValue.class.getName();
                break;
                
            case STATEMENT_SEQUENCE:
                
                // This "class" is actually a script.
                
                script = tree;
                break;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private void extendsClause(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case EXTENDS:

                for (CgsuiteTree node : tree.getChildren())
                {
                    CgsuiteClass parent = extendsItem(node);
                    
                    if (parent.isMutableClass() && !classModifiers.contains(Modifier.MUTABLE))
                        throw new InputException(node.getToken(), "Class is not marked \"mutable\", but has a mutable parent: " + parent.getQualifiedName());
                    
                    parents.add(parent);
                }
                return;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }
    
    private CgsuiteClass extendsItem(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case IDENTIFIER:
                
                return CgsuitePackage.forceLookupClass(tree.getText(), imports);
                
            case DOT:
                
                CgsuitePackage pkg = Domain.findPackage(tree.getChild(0));
                return pkg.forceLookupClassInPackage(tree.getChild(1).getText());
                
            default:
                
                throw new MalformedParseTreeException(tree);
        }
    }

    private String javaref(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case JAVA:

                String literal = tree.getChild(0).getText();
                return literal.substring(1, literal.length()-1);

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private void declarations(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.getType())
        {
            case CgsuiteParser.CLASS:

                classDeclarations(tree);
                break;

            case CgsuiteParser.ENUM:

                enumDeclarations(tree);
                break;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private void classDeclarations(CgsuiteTree tree) throws CgsuiteException
    {
        // Declare vars first.
        
        for (int i = 0; i < tree.getChildCount(); i++)
        {
            if (tree.getChild(i).getType() == VAR)
            {
                declaration(tree.getChild(i));
            }
        }
        
        // Now declare methods and properties.
        
        for (int i = 0; i < tree.getChildCount(); i++)
        {
            if (tree.getChild(i).getType() == METHOD ||
                tree.getChild(i).getType() == PROPERTY)
            {
                declaration(tree.getChild(i));
            }
        }
    }

    private void enumDeclarations(CgsuiteTree tree) throws CgsuiteException
    {
        assert tree.getChild(2).getType() == ENUM_ELEMENT_LIST : tree.getChild(2).toStringTree();
        for (int i = 0; i < tree.getChild(2).getChildCount(); i++)
        {
            assert tree.getChild(2).getChild(i).getType() == ENUM_ELEMENT : tree.getChild(2).toStringTree();
            String literal = tree.getChild(2).getChild(i).getChild(0).getChild(0).getText();
            declareVar(tree.getChild(2).getChild(i).getChild(0).getChild(0), literal, EnumSet.of(Modifier.STATIC, Modifier.ENUM_VALUE), tree.getChild(2).getChild(i).getChild(0).getChild(1), i+1);
//            CgsuiteEnumValue value = new CgsuiteEnumValue(this, literal, i);
//            assign(literal, value);
//            value.assign("Ordinal", new RationalNumber(i, 1));
        }

        for (int i = 3; i < tree.getChildCount(); i++)
        {
            switch (tree.getChild(i).getType())
            {
                case METHOD:
                case PROPERTY:
                case VAR:
                    declaration(tree.getChild(i));
                    break;

                default:
                    throw new MalformedParseTreeException(tree);
            }
        }
    }

    private void declaration(CgsuiteTree tree) throws CgsuiteException
    {
        String methodName;
        String javaMethodName = null;
        CgsuiteTree body = null;

        EnumSet<Modifier> modifiers;
        
        try
        {
            modifiers = modifiers(tree.getChild(0));
        }
        catch (InputException exc)
        {
            if (exc.getTokenStack().isEmpty())
                exc.addToken(tree.token);
            throw exc;
        }
        
        switch (tree.token.getType())
        {
            case METHOD:

                methodName = methodName(tree.getChild(1));
                List<Parameter> parameters = methodParameters(tree.getChild(2), methodName.equals(name));

                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declareMethod(tree, methodName, modifiers, parameters, body, javaMethodName);

                return;

            case PROPERTY:

                String propertyName = tree.getChild(1).getText();
                methodName = propertyName + "$" + tree.getChild(2).getText();

                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declareMethod(tree, methodName, modifiers, Collections.<Parameter>emptyList(), body, javaMethodName);

                return;

            case VAR:

                for (int i = 1; i < tree.getChildCount(); i++)
                {
                    switch (tree.getChild(i).getType())
                    {
                        case IDENTIFIER:
                            String varName = tree.getChild(i).getText();
                            declareVar(tree.getChild(i), varName, modifiers, null, -1);
                            break;
                            
                        case ASSIGN:
                            varName = tree.getChild(i).getChild(0).getText();
                            declareVar(tree.getChild(i).getChild(0), varName, modifiers, tree.getChild(i).getChild(1), -1);
                            break;
                            
                        default:
                            throw new MalformedParseTreeException(tree.getChild(i));
                    }
                }

                return;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private EnumSet<Modifier> modifiers(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case MODIFIERS:

                EnumSet<Modifier> mods = EnumSet.noneOf(Modifier.class);

                for (int i = 0; i < tree.getChildCount(); i++)
                {
                    Modifier mod = modifier(tree.getChild(i));

                    if (mods.contains(mod))
                    {
                        throw new InputException(tree.getChild(i).getToken(), "Duplicate modifier.");
                    }

                    mods.add(mod);
                }

                return mods;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private Modifier modifier(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case MUTABLE:   return Modifier.MUTABLE;
            case OVERRIDE:  return Modifier.OVERRIDE;
            case STATIC:    return Modifier.STATIC;
            default:        throw new MalformedParseTreeException(tree);
        }
    }

    private void declareMethod(CgsuiteTree tree, String name, EnumSet<Modifier> modifiers, List<Parameter> parameters, CgsuiteTree body, String javaMethodName)
    {
        if (this.methods.containsKey(name) &&
            this.methods.get(name).getDeclaringClass() == this)
        {
            // tree.getToken().getText() is "method" or "property" depending on the declaration type
            throw new InputException(tree.getToken(), "Duplicate " + tree.getToken().getText() + " definition: " + name);
        }
        else if (this.methods.containsKey(name) && !modifiers.contains(Modifier.OVERRIDE))
        {
            throw new InputException(tree.getToken(), "Declaration overrides an ancestor " + tree.getToken().getText() +
                " but is not marked \"override\": " + name);
        }
        else if (!this.methods.containsKey(name) && modifiers.contains(Modifier.OVERRIDE))
        {
            throw new InputException(tree.getToken(), "Declaration is marked \"override\" but does not override an ancestor " +
                tree.getToken().getText() + ": " + name);
        }
        else if (modifiers.contains(Modifier.MUTABLE) && !classModifiers.contains(Modifier.MUTABLE))
        {
            throw new InputException(tree.getToken(), "Declaration is marked \"mutable\" but enclosing class is not: " + name);
        }
        else if (modifiers.contains(Modifier.MUTABLE) && modifiers.contains(Modifier.STATIC))
        {
            throw new InputException(tree.getToken(), "Static method cannot be marked \"mutable\": " + name);
        }
                
        // It's a legit method declaration.
        
        this.methods.put(name, new CgsuiteMethod(this, name, modifiers, parameters, body, javaMethodName));
    }

    private void declareVar(CgsuiteTree tree, String name, EnumSet<Modifier> modifiers, CgsuiteTree initializer, int declRank)
    {
        if (vars.containsKey(name))
        {
            CgsuiteClass decl = vars.get(name).getDeclaringClass();
            if (decl == this)
            {
                throw new InputException(tree.getToken(), "Duplicate variable: " + name);
            }
            else
            {
                throw new InputException(tree.getToken(), "Variable shadows superclass var: " + name + " (from class " + decl.getQualifiedName() + ")");
            }
        }
        Variable var = new Variable(this, name, modifiers, initializer, declRank);
        log.info("Declaring var: " + this.name + "." + name);
        vars.put(name, var);
        varsInOrder.add(var);
    }

    private String methodName(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case IDENTIFIER:

                return tree.getText();

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private List<Parameter> methodParameters(CgsuiteTree tree, boolean isConstructor) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case METHOD_PARAMETER_LIST:

                List<Parameter> parameters = new ArrayList<Parameter>(tree.getChildCount());
                for (int i = 0; i < tree.getChildCount(); i++)
                    parameters.add(methodParameter(tree.getChild(i), isConstructor));

                return parameters;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private Parameter methodParameter(CgsuiteTree tree, boolean isConstructor) throws CgsuiteException
    {
        Token parameterToken;
        CgsuiteClass parameterType;
        boolean isOptional;
        CgsuiteTree defaultValue;

        switch (tree.token.getType())
        {
            case IDENTIFIER:

                parameterToken = tree.getToken();
                parameterType = (tree.getChildCount() > 0)? CgsuitePackage.forceLookupClass(tree.getChild(0).getText(), imports) : CgsuiteClass.OBJECT_TYPE;
                isOptional = false;
                defaultValue = null;
                break;

            case QUESTION:

                CgsuiteTree subt = tree.getChild(0);
                parameterToken = subt.getToken();
                parameterType = (subt.getChildCount() > 0)? CgsuitePackage.forceLookupClass(subt.getChild(0).getText(), imports) : CgsuiteClass.OBJECT_TYPE;
                isOptional = true;
                defaultValue = (tree.getChildCount() > 1)? tree.getChild(1) : null;
                break;

            default:

                throw new MalformedParseTreeException(tree);
        }
        
        String parameterName = parameterToken.getText();
        if (!isConstructor && vars.containsKey(parameterName))
        {
            throw new InputException(parameterToken, "Method parameter name shadows member variable: " + parameterName);
        }
        
        return new Parameter(parameterName, parameterType, isOptional, defaultValue);
    }
    
    @Override
    public void fileFolderCreated(FileEvent fe)
    {
    }

    @Override
    public void fileDataCreated(FileEvent fe)
    {
    }

    @Override
    public void fileChanged(FileEvent fe)
    {
        markUnloaded();
    }

    @Override
    public void fileDeleted(FileEvent fe)
    {
        markUnloaded();
    }

    @Override
    public void fileRenamed(FileRenameEvent fre)
    {
        markUnloaded();
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fae)
    {
    }

    private void markUnloaded()
    {
        loaded = false;
        if (descendants != null)
        {
            for (CgsuiteClass descendant : descendants)
            {
                descendant.loaded = false;
            }
        }
    }
}
