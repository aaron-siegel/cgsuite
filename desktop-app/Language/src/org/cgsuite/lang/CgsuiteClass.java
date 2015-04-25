package org.cgsuite.lang;

import static org.cgsuite.lang.parser.CgsuiteParser.*;

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
import java.util.logging.Logger;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.cgsuite.lang.CgsuiteMethod.Parameter;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
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
    private List<CgsuitePackage> packageImports;
    private Map<String,CgsuiteClass> classImports;
    private FileObject fo;
    private String name;
    private boolean loaded;

    private CgsuiteTree parseTree;
    private CgsuiteTree script;

    private EnumSet<Modifier> classModifiers;
    private List<CgsuiteClass> parents;
    private Set<CgsuiteClass> ancestors;
    private Map<String,CgsuiteMethodGroup> ancestorMethods;
    private Map<String,CgsuiteMethodGroup> methods;
    private Map<String,Variable> vars;
    private List<Variable> varsInOrder;
    private CgsuiteTree staticBlock;

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
        
        CgsuiteObject staticResolution = resolveStatic(identifier);
        
        if (staticResolution != null)
            return staticResolution;
        
        return super.resolve(identifier, contextMethod, localAccess);
    }
    
    public CgsuiteObject resolveStatic(String identifier)
    {
        CgsuiteMethodGroup getter = lookupMethod(identifier + "$get");

        if (getter != null && getter.isStatic())
            return getter.invoke(CgsuiteMethod.EMPTY_PARAM_LIST, CgsuiteMethod.EMPTY_PARAM_MAP);

        CgsuiteMethodGroup method = lookupMethod(identifier);

        if (method != null && method.isStatic())
            return method;
        
        Variable var = lookupVar(identifier);

        if (var != null && var.isStatic())
        {
            CgsuiteObject obj = objectNamespace.get(identifier);
            return (obj == null)? NIL : obj;
        }

        return null;
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
    public Output toOutput()
    {
        ensureLoaded();
        StyledTextOutput sto = new StyledTextOutput();
        sto.appendText(StyledTextOutput.Mode.GRAPHICAL, "\u00AB");
        sto.appendText(name);
        sto.appendText(StyledTextOutput.Mode.GRAPHICAL, "\u00BB");
        return sto;
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

    public List<CgsuitePackage> getPackageImports()
    {
        ensureLoaded();
        return packageImports;
    }
    
    public Map<String,CgsuiteClass> getClassImports()
    {
        ensureLoaded();
        return classImports;
    }

    public CgsuiteMethodGroup lookupMethod(String name)
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

    public CgsuiteMethodGroup lookupConstructor()
    {
        ensureLoaded();
        return methods.get(this.name);
    }

    public CgsuiteMap allMethods()
    {
        ensureLoaded();
        CgsuiteMap map = new CgsuiteMap();
        for (Entry<String,CgsuiteMethodGroup> e : methods.entrySet())
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
            map.put(new CgsuiteString(e.getKey()), e.getValue());
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

    public void load()
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
        
        this.packageImports = new ArrayList<CgsuitePackage>();
        this.packageImports.addAll(CgsuitePackage.DEFAULT_PACKAGE_IMPORTS);
        if (!CgsuitePackage.DEFAULT_PACKAGE_IMPORTS.contains(enclosingPackage))
            this.packageImports.add(enclosingPackage);
        this.classImports = new HashMap<String,CgsuiteClass>();

        this.parents = new ArrayList<CgsuiteClass>();
        this.ancestors = new HashSet<CgsuiteClass>();
        this.ancestorMethods = new HashMap<String,CgsuiteMethodGroup>();
        this.methods = new HashMap<String,CgsuiteMethodGroup>();
        this.vars = new HashMap<String,Variable>();
        this.varsInOrder = new ArrayList<Variable>();
        this.descendants = new HashSet<CgsuiteClass>();
        this.staticBlock = null;
        
        if (parseTree.getChild(0).getType() == PREAMBLE)
        {
            preamble(parseTree.getChild(0));
            classdef(parseTree.getChild(1));
        }
        else
        {
            assert parseTree.getChild(0).getType() == STATEMENT_SEQUENCE : parseTree.toStringTree();
            script = parseTree.getChild(0);
            loaded = true;
            return;
        }

        for (CgsuiteClass parent : parents)
        {
            parent.ensureLoaded();
            this.ancestors.addAll(parent.ancestors);
            for (Entry<String,CgsuiteMethodGroup> e : parent.methods.entrySet())
            {
                String nameInParent = e.getKey();
                if (nameInParent.startsWith("super$") || nameInParent.startsWith("static$"))
                    continue;
                
                CgsuiteMethodGroup methodInParent = e.getValue();
                
                if (!ancestorMethods.containsKey(nameInParent))
                    ancestorMethods.put(nameInParent, new CgsuiteMethodGroup(nameInParent));
                if (!ancestorMethods.containsKey("super$" + nameInParent))
                    ancestorMethods.put("super$" + nameInParent, new CgsuiteMethodGroup(nameInParent));
                
                ancestorMethods.get(nameInParent).addAllMethods(methodInParent);
                ancestorMethods.get("super$" + nameInParent).addAllMethods(methodInParent);
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
        {
            // Java class is inherited from parent(s).  Use the *most specific* among all parents.
            for (CgsuiteClass parent : parents)
            {
                if (javaClass == null || javaClass.isAssignableFrom(parent.javaClass))
                {
                    javaClassname = parent.javaClassname;
                    javaClass = parent.javaClass;
                }
                else if (!parent.javaClass.isAssignableFrom(javaClass))
                {
                    throw new InputException(parseTree.getChild(1).getToken(),
                        getQualifiedName() + ": Multiple ancestors have incompatible Java classes.  Consider disambiguating by declaring Java class explicitly.");
                }
            }
        }
        else
        {
            try
            {
                this.javaClass = Class.forName(javaClassname).asSubclass(CgsuiteObject.class);
            }
            catch (ClassNotFoundException exc)
            {
                throw new InputException("Could not locate Java class for " + fo.getNameExt() + ": " + javaClassname);
            }
            catch (ClassCastException exc)
            {
                throw new InputException("The Java class for " + fo.getNameExt() + " is not a subclass of CgsuiteObject: " + javaClassname);
            }
        }
        
        try
        {
            this.defaultJavaConstructor = this.javaClass.getConstructor(CgsuiteClass.class);
        }
        catch (NoSuchMethodException exc)
        {
            this.defaultJavaConstructor = null;
        }
        
        methods.put("static$init", new CgsuiteMethodGroup("static$init"));
        methods.get("static$init").addMethod(new CgsuiteMethod(this, "static$init", EnumSet.of(Modifier.STATIC), Collections.<Parameter>emptyList(), null, null));

        declarations(parseTree.getChild(1));
        
        for (Entry<String,CgsuiteMethodGroup> e : ancestorMethods.entrySet())
        {
            if (methods.containsKey(e.getKey()))
                methods.get(e.getKey()).addAllMethods(e.getValue());
            else
                methods.put(e.getKey(), e.getValue());
        }
    
        // Mark loaded

        this.loaded = true;

        for (CgsuiteClass ancestor : ancestors)
        {
            ancestor.descendants.add(this);
        }

        this.ancestors.add(this);
        
        // Populate statics and invokeMethod static initializers

        for (Variable var : varsInOrder)
        {
            if (var.isStatic())
            {
                CgsuiteObject initialValue = NIL;

                if (var.getInitializer() != null)
                {
                    log.info("Static init  : " + getQualifiedName() + "." + var.getName());
                    initialValue = new Domain(this, methods.get("static$init").firstMethod(), packageImports, classImports).expression(var.getInitializer());
                }
                
                objectNamespace.put(var.getName(), initialValue);
                
                if (var.isEnumValue())
                {
                    initialValue.objectNamespace.put("literal", new CgsuiteString(var.getName()));
                    initialValue.objectNamespace.put("ordinal", new CgsuiteInteger(var.getDeclRank()));
                }
            }
        }
        
        if (staticBlock != null)
        {
            new Domain(this, methods.get("static$init").firstMethod(), packageImports, classImports).statementSequence(staticBlock);
        }
        
        log.info("Loaded class : " + getQualifiedName());
    }
    
    private void preamble(CgsuiteTree tree) throws CgsuiteException
    {
        assert tree.getType() == PREAMBLE;
        
        for (CgsuiteTree child : tree.getChildren())
        {
            switch (child.getType())
            {
                case IMPORT:
                    
                    declareImport(child.getChild(0));
                    break;
                    
                default:
                    
                    throw new MalformedParseTreeException(tree);
            }
        }
        
        // Protect the imports
        
        packageImports = Collections.unmodifiableList(packageImports);
        classImports = Collections.unmodifiableMap(classImports);
    }
    
    private void declareImport(CgsuiteTree tree) throws CgsuiteException
    {
        String packageName = Domain.stringifyDotSequence(tree.getChild(0));
        CgsuitePackage pkg = CgsuitePackage.forceLookupPackage(packageName);
        
        switch (tree.getChild(1).getType())
        {
            case AST:
                
                packageImports.add(pkg);
                break;
                
            case IDENTIFIER:
                
                String className = tree.getChild(1).getText();
                CgsuiteClass importClass = pkg.forceLookupClassInPackage(className);
                if (classImports.containsKey(className))
                    throw new InputException(tree.getToken(), "Duplicate import name: " + className);
                classImports.put(className, importClass);
                break;
                
            default:
                
                throw new MalformedParseTreeException(tree);
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
                
                return CgsuitePackage.forceLookupClass(tree.getText(), packageImports, classImports);
                
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
            case CLASS:

                classDeclarations(tree);
                break;

            case ENUM:

                enumDeclarations(tree);
                break;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private void classDeclarations(CgsuiteTree tree) throws CgsuiteException
    {
        // Declare vars and statics first.
        
        for (int i = 0; i < tree.getChildCount(); i++)
        {
            if (tree.getChild(i).getType() == VAR)
            {
                declaration(tree.getChild(i));
            }
            else if (tree.getChild(i).getType() == STATIC)
            {
                declareStatic(tree.getChild(i));
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
            CgsuiteTree element = tree.getChild(2).getChild(i);
            assert element.getType() == ENUM_ELEMENT : tree.toStringTree();
            String literal = element.getChild(0).getChild(0).getText();
            declareVar(element.getChild(0).getChild(0), literal, EnumSet.of(Modifier.STATIC, Modifier.ENUM_VALUE), element.getChild(0).getChild(1), i+1);
        }
        
        classDeclarations(tree);
        
        // Implicit constructor declaration for enum.
        
        if (!methods.containsKey(name))
        {
            methods.put(name, new CgsuiteMethodGroup(name));
            methods.get(name).addMethod(new CgsuiteMethod(this, name, EnumSet.noneOf(Modifier.class), Collections.<Parameter>emptyList(), new CgsuiteTree(new CommonToken(STATEMENT_SEQUENCE)), null));
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
        
        List<Parameter> parameters;
        
        switch (tree.getType())
        {
            case METHOD:

                methodName = methodName(tree.getChild(1));
                parameters = methodParameters(tree.getChild(2), methodName.equals(name));

                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declareMethod(tree, methodName, modifiers, parameters, body, javaMethodName);

                break;

            case PROPERTY:

                String propertyName = tree.getChild(1).getText();
                methodName = propertyName + "$" + tree.getChild(2).getText();
                
                if (tree.getChild(2).getType() == GET)
                {
                    parameters = Collections.emptyList();
                }
                else
                {
                    if (!modifiers.contains(Modifier.MUTABLE))
                        throw new InputException(tree.getToken(), "Setter must be declared mutable.");
                    
                    parameters = methodParameters(tree.getChild(2).getChild(0), false);
                }
                
                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declareMethod(tree, methodName, modifiers, parameters, body, javaMethodName);

                break;

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

                break;

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
        /*
        if (this.methods.containsKey(name) &&
            this.methods.get(name).getDeclaringClass() == this)
        {
            // tree.getToken().getText() is "method" or "property" depending on the declaration type
            throw new InputException(tree.getToken(), "Duplicate " + tree.getToken().getText() + " definition: " + name);
        }
        else
         */
        if (name.equals(this.name) && modifiers.contains(Modifier.STATIC))
        {
            throw new InputException(tree.getToken(), "Constructor is marked \"static\".");
        }
        else if (name.equals(this.name) && javaMethodName == null && this.defaultJavaConstructor == null)
        {
            throw new InputException(tree.getToken(), "Class declares a constructor, but its underlying Java class (" + javaClassname + ") does not provide a default constructor.");
        }
        else if (this.ancestorMethods.containsKey(name) && !modifiers.contains(Modifier.OVERRIDE))
        {
            throw new InputException(tree.getToken(), "Declaration overrides an ancestor " + tree.getToken().getText() +
                " but is not marked \"override\": " + name);
        }
        else if (!this.ancestorMethods.containsKey(name) && modifiers.contains(Modifier.OVERRIDE))
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
        
        if (!this.methods.containsKey(name))
        {
            this.methods.put(name, new CgsuiteMethodGroup(name));
        }
        this.methods.get(name).addMethod(new CgsuiteMethod(this, name, modifiers, parameters, body, javaMethodName));
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
        log.info("Declaring var: " + getQualifiedName() + "." + name);
        vars.put(name, var);
        varsInOrder.add(var);
    }
    
    private void declareStatic(CgsuiteTree tree)
    {
        if (staticBlock != null)
        {
            throw new InputException(tree.getToken(), "Duplicate static block.");
        }
        staticBlock = tree.getChild(0);
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
        boolean foundOptional = false;
        boolean foundVararg = false;
        
        switch (tree.token.getType())
        {
            case METHOD_PARAMETER_LIST:

                List<Parameter> parameters = new ArrayList<Parameter>(tree.getChildCount());
                
                for (int i = 0; i < tree.getChildCount(); i++)
                {
                    Parameter parameter = methodParameter(tree.getChild(i), isConstructor);
                    
                    if (parameter.isOptional())
                    {
                        foundOptional = true;
                    }
                    else if (foundOptional)
                    {
                        throw new InputException(tree.getChild(i).getToken(),
                            "Ordinary parameters must precede optional parameters in method declaration.");
                    }
                    
                    if (parameter.isVararg())
                    {
                        foundVararg = true;
                    }
                    else if (foundVararg && !parameter.isOptional())
                    {
                        throw new InputException(tree.getChild(i).getToken(),
                            "Vararg parameter must be the last ordinary parameter in method declaration.");
                    }
                    
                    parameters.add(parameter);
                }

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
        boolean vararg;

        switch (tree.token.getType())
        {
            case IDENTIFIER:

                parameterToken = tree.getToken();
                
                if (tree.getChildCount() > 0 && tree.getChild(0).getType() == IDENTIFIER)
                    parameterType = CgsuitePackage.forceLookupClass(tree.getChild(0).getText(), packageImports, classImports);
                else
                    parameterType = OBJECT_TYPE;
                
                vararg = (tree.getChildCount() > 0 && tree.getChild(tree.getChildCount()-1).getType() == DOTDOTDOT);
                
                isOptional = false;
                defaultValue = null;
                break;

            case QUESTION:

                CgsuiteTree subt = tree.getChild(0);
                parameterToken = subt.getToken();
                parameterType = (subt.getChildCount() > 0)? CgsuitePackage.forceLookupClass(subt.getChild(0).getText(), packageImports, classImports) : CgsuiteClass.OBJECT_TYPE;
                isOptional = true;
                defaultValue = (tree.getChildCount() > 1)? tree.getChild(1) : null;
                vararg = false;
                break;

            default:

                throw new MalformedParseTreeException(tree);
        }
        
        String parameterName = parameterToken.getText();
        if (!isConstructor && vars.containsKey(parameterName))
        {
            throw new InputException(parameterToken, "Method parameter name shadows member variable: " + parameterName);
        }
        
        return new Parameter(parameterName, parameterType, isOptional, defaultValue, vararg);
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

    void markUnloaded()
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
