package org.cgsuite.lang;

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
import org.cgsuite.lang.game.RationalNumber;
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
    
    public final static CgsuiteClass OBJECT;
    public final static CgsuiteClass CLASS;

    private static int nextDeclNumber;

    static
    {
        OBJECT = new CgsuiteClass();
        CLASS = new CgsuiteClass();

        OBJECT.name = "Object";
        CLASS.name = "Class";
        OBJECT.type = CLASS.type = CLASS;
        CgsuiteObject.NIL.type = OBJECT;
    }

    private CgsuitePackage enclosingPackage;
    private List<CgsuitePackage> imports;
    private FileObject fo;
    private String name;
    private boolean loaded;

    private CgsuiteTree parseTree;

    private int declNumber;
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
        super(CLASS);

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
    public CgsuiteObject resolve(String identifier)
    {
        ensureLoaded();
        
        CgsuiteMethod getter = (CgsuiteMethod) lookupMethod(identifier + "$get");

        if (getter != null && getter.isStatic())
            return getter.invoke((CgsuiteObject) null, CgsuiteObject.EMPTY_LIST, null);

        CgsuiteMethod method = (CgsuiteMethod) lookupMethod(identifier);

        if (method != null && method.isStatic())
            return method;

        return super.resolve(identifier);
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
        ensureLoaded();
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

    public CgsuiteObject lookupMethod(String name)
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
    public void assign(String name, CgsuiteObject object)
    {
        Variable var = lookupVar(name);
        if (var == null)
            throw new InputException("Unknown variable: " + name);
        if (!var.getModifiers().contains(Modifier.STATIC))
            throw new InputException("Cannot reference non-static variable in static context: " + name);
        objectNamespace.put(name, object);
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
        log.info("Loading class: " + name);

        try
        {
            ANTLRInputStream in = new SourcedAntlrInputStream(fo.getInputStream(), fo.getNameExt());
            CgsuiteLexer lexer = new CgsuiteLexer(in);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CgsuiteParser parser = new CgsuiteParser(tokens);
            parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
            CgsuiteParser.compilationUnit_return r = parser.compilationUnit();
            if (parser.getNumberOfSyntaxErrors() == 0)
            {
                this.parseTree = (CgsuiteTree) r.getTree();
            }
            else
            {
                // TODO Improve this message.
                throw new InputException("Syntax error(s) in " + fo.getNameExt() + ": " + parser.getErrorMessageString());
            }
        }
        catch (IOException exc)
        {
            throw new InputException("I/O Error loading class file " + fo.getNameExt() + ".", exc);
        }
        catch (RecognitionException exc)
        {
            throw new InputException("Syntax error(s) in " + fo.getNameExt() + ".", exc);
        }

        this.objectNamespace.clear();
        
        this.imports = new ArrayList<CgsuitePackage>();
        this.imports.add(CgsuitePackage.ROOT_PACKAGE);
        if (!enclosingPackage.equals(CgsuitePackage.ROOT_PACKAGE))
            this.imports.add(enclosingPackage);

        this.parents = new HashSet<CgsuiteClass>();
        this.ancestors = new HashSet<CgsuiteClass>();
        this.methods = new HashMap<String,CgsuiteMethod>();
        this.vars = new HashMap<String,Variable>();
        this.varsInOrder = new ArrayList<Variable>();
        this.descendants = new HashSet<CgsuiteClass>();

        this.declNumber = nextDeclNumber++;

        classdef(parseTree.getChild(0));

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
                // TODO Access check
                if (!var.isPrivate())
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
            this.loaded = true;
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

        this.loaded = true;

        // Populate statics and invoke static initializers

        for (Variable var : varsInOrder)
        {
            if (var.isStatic())
            {
                CgsuiteObject initialValue = NIL;

                if (var.getInitializer() != null)
                    initialValue = new Domain(imports).expression(var.getInitializer());
                
                objectNamespace.put(var.getName(), initialValue);

                if (var.isEnumValue())
                {
                    initialValue.objectNamespace.put("literal", new CgsuiteString(var.getName()));
                    initialValue.objectNamespace.put("ordinal", new RationalNumber(var.getDeclRank(), 1));
                }
            }
        }

        for (CgsuiteClass ancestor : ancestors)
        {
            ancestor.descendants.add(this);
        }

        this.ancestors.add(this);
    }

    private void classdef(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.getToken().getType())
        {
            case CgsuiteParser.CLASS:

                if (!name.equals(tree.getChild(0).getText()))
                {
                    throw new InputException(tree.getChild(0).getToken(), "Classname in file does not match filename: " + fo.getNameExt());
                }
                name = tree.getChild(0).getText();

                int i = 1;

                if (i < tree.getChildCount() && tree.getChild(i).getToken().getType() == EXTENDS)
                    extendsClause(tree.getChild(i++));
                else if (this != OBJECT)
                    parents.add(OBJECT);

                if (i < tree.getChildCount() && tree.getChild(i).getToken().getType() == JAVA)
                    javaClassname = javaref(tree.getChild(i++));

                break;

            case CgsuiteParser.ENUM:

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
                    CgsuiteClass parent = CgsuitePackage.forceLookupClass(node.getText());
                    assert parent != null : node.getText();
                    parents.add(parent);
                }
                return;

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
        for (int i = 0; i < tree.getChildCount(); i++)
        {
            switch (tree.getChild(i).getType())
            {
                case EXTENDS:
                case JAVA:
                case IDENTIFIER:
                    break;

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

    private void enumDeclarations(CgsuiteTree tree) throws CgsuiteException
    {
        assert tree.getChild(2).getType() == ENUM_ELEMENT_LIST : tree.getChild(2).toStringTree();
        for (int i = 0; i < tree.getChild(2).getChildCount(); i++)
        {
            assert tree.getChild(2).getChild(i).getType() == ENUM_ELEMENT : tree.getChild(2).toStringTree();
            String literal = tree.getChild(2).getChild(i).getChild(0).getChild(0).getText();
            declareVar(literal, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.ENUM_VALUE), tree.getChild(2).getChild(i).getChild(0).getChild(1), i+1);
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
                List<Parameter> parameters = methodParameters(tree.getChild(2));

                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                if (this.methods.containsKey(methodName) &&
                    this.methods.get(methodName).getDeclaringClass() == this)
                {
                    throw new InputException(tree.getToken(), "Duplicate method definition: " + methodName);
                }

                declareMethod(methodName, modifiers, parameters, body, javaMethodName);

                return;

            case PROPERTY:

                String propertyName = tree.getChild(1).getText();
                methodName = propertyName + "$" + tree.getChild(2).getText();

                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                if (this.methods.containsKey(methodName) &&
                    this.methods.get(methodName).getDeclaringClass() == this)
                {
                    throw new InputException(tree.getToken(), "Duplicate property definition: " + propertyName);
                }

                declareMethod(methodName, modifiers, Collections.<Parameter>emptyList(), body, javaMethodName);

                return;

            case VAR:

                for (int i = 1; i < tree.getChildCount(); i++)
                {
                    switch (tree.getChild(i).getType())
                    {
                        case IDENTIFIER:
                            String varName = tree.getChild(i).getText();
                            declareVar(varName, modifiers, null, -1);
                            break;
                            
                        case ASSIGN:
                            varName = tree.getChild(i).getChild(0).getText();
                            declareVar(varName, modifiers, tree.getChild(i).getChild(1), -1);
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
                boolean hasAccessModifier = false;
                for (int i = 0; i < tree.getChildCount(); i++)
                {
                    Modifier mod = modifier(tree.getChild(i));

                    if (mods.contains(mod))
                    {
                        throw new InputException(tree.getChild(i).getToken(), "Duplicate modifier.");
                    }

                    if (Modifier.ACCESS_MODIFIERS.contains(mod))
                    {
                        if (hasAccessModifier)
                            throw new InputException(tree.getChild(i).getToken(), "Redundant access modifier.");
                        hasAccessModifier = true;
                    }

                    mods.add(mod);
                }
                if (!hasAccessModifier)
                    throw new InputException("Missing access modifier.");
                return mods;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private Modifier modifier(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case PUBLIC:    return Modifier.PUBLIC;
            case PROTECTED: return Modifier.PROTECTED;
            case PRIVATE:   return Modifier.PRIVATE;
            case STATIC:    return Modifier.STATIC;
            case IMMUTABLE: return Modifier.IMMUTABLE;
            default:        throw new MalformedParseTreeException(tree);
        }
    }

    private void declareMethod(String name, EnumSet<Modifier> modifiers, List<Parameter> parameters, CgsuiteTree body, String javaMethodName)
    {
        this.methods.put(name, new CgsuiteMethod(this, name, modifiers, parameters, body, javaMethodName));
    }

    private void declareVar(String name, EnumSet<Modifier> modifiers, CgsuiteTree initializer, int declRank)
    {
        Variable var = new Variable(this, name, modifiers, initializer, declRank);
        log.info("Declaring var: " + this.name + "." + name);
        this.vars.put(name, var);
        this.varsInOrder.add(var);
    }

    private String methodName(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case IDENTIFIER:

                return tree.getText();

            case OP:

                StringBuilder str = new StringBuilder("op ");
                for (CgsuiteTree child : tree.getChildren())
                    str.append(child.getText());
                return str.toString();

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private List<Parameter> methodParameters(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case METHOD_PARAMETER_LIST:

                List<Parameter> parameters = new ArrayList<Parameter>(tree.getChildCount());
                for (int i = 0; i < tree.getChildCount(); i++)
                    parameters.add(methodParameter(tree.getChild(i)));

                return parameters;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private Parameter methodParameter(CgsuiteTree tree) throws CgsuiteException
    {
        String parameterName;
        CgsuiteClass parameterType;
        CgsuiteTree defaultValue;

        switch (tree.token.getType())
        {
            case IDENTIFIER:

                parameterName = tree.getText();
                parameterType = (tree.getChildCount() > 0)? CgsuitePackage.getRootPackage().forceLookupClassInPackage(tree.getChild(0).getText()) : CgsuiteClass.OBJECT;
                return new Parameter(parameterName, parameterType, false, null);

            case QUESTION:

                CgsuiteTree subt = tree.getChild(0);
                parameterName = subt.getText();
                parameterType = (subt.getChildCount() > 0)? CgsuitePackage.getRootPackage().forceLookupClassInPackage(subt.getChild(0).getText()) : CgsuiteClass.OBJECT;
                defaultValue = (tree.getChildCount() > 1)? tree.getChild(1) : null;
                return new Parameter(parameterName, parameterType, true, defaultValue);

            default:

                throw new MalformedParseTreeException(tree);
        }
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
