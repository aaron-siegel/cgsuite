package org.cgsuite.lang;

import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import java.util.Collections;
import org.cgsuite.RationalNumber;
import org.cgsuite.lang.CgsuiteMethod.Parameter;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.cgsuite.CgsuiteException;
import org.openide.filesystems.FileObject;

import static org.cgsuite.lang.CgsuiteParser.*;

public class CgsuiteClass extends CgsuiteObject implements FileChangeListener
{
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

    private FileObject fo;
    private String name;
    private boolean loaded;

    private CgsuiteTree parseTree;

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

    public CgsuiteClass(FileObject fo)
    {
        super(CLASS);

        this.name = fo.getName();
        setFileObject(fo);
    }

    public final void setFileObject(FileObject fo)
    {
        if (!this.name.equals(fo.getName()))
            throw new IllegalArgumentException(this.name + " != " + fo.getName());

        if (this.fo != null)
            this.fo.removeFileChangeListener(this);
        
        this.fo = fo;
        this.fo.addFileChangeListener(this);
        this.loaded = false;
    }

    public Collection<CgsuiteClass> getParents()
    {
        ensureLoaded();
        return parents;
    }

    @Override
    public CgsuiteObject resolve(String str)
    {
        ensureLoaded();
        return super.resolve(str);
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

    public CgsuiteObject lookup(String name)
    {
        ensureLoaded();
        return methods.get(name);
    }

    public CgsuiteMethod lookupConstructor()
    {
        ensureLoaded();
        // TODO Enforce class-named vars/methods are constructors
        return methods.get(this.name);
    }

    public Collection<CgsuiteMethod> allMethods()
    {
        ensureLoaded();
        return methods.values();
    }

    public Collection<CgsuiteVar> allVars()
    {
        ensureLoaded();
        return vars.values();
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

        this.parents = new HashSet<CgsuiteClass>();
        this.ancestors = new HashSet<CgsuiteClass>();
        this.methods = new HashMap<String,CgsuiteMethod>();
        this.vars = new HashMap<String,CgsuiteVar>();
        this.descendants = new HashSet<CgsuiteClass>();

        this.declNumber = nextDeclNumber++;

        classdef(parseTree.getChild(0));

        for (CgsuiteClass parent : parents)
        {
            parent.ensureLoaded();
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

        declarations(parseTree.getChild(0));

        this.loaded = true;

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
                else
                    javaClassname = CgsuiteObject.class.getName();

                break;

            case CgsuiteParser.ENUM:

                if (!name.equals(tree.getChild(1).getText()))
                {
                    throw new InputException(tree.getChild(1).getToken(), "Classname in file does not match filename: " + fo.getNameExt());
                }
                parents.add(CgsuitePackage.getRootPackage().forceLookupClassInPackage("Enum"));
                javaClassname = CgsuiteEnumValue.class.getName();
                break;

            default:

                throw new RuntimeException();
        }
    }

    private void extendsClause(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case EXTENDS:

                for (CgsuiteTree node : tree.getChildren())
                {
                    CgsuiteClass parent = CgsuitePackage.getRootPackage().forceLookupClassInPackage(node.getText());
                    assert parent != null : node.getText();
                    parents.add(parent);
                }
                return;

            default:

                throw new RuntimeException();
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

                throw new RuntimeException();
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

                throw new RuntimeException();
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
                    declaration(tree.getChild(i));
                    break;

                default:
                    throw new RuntimeException();
            }
        }
    }

    private void enumDeclarations(CgsuiteTree tree) throws CgsuiteException
    {
        assert tree.getChild(2).getType() == ENUM_ELEMENT_LIST : tree.getChild(2).toStringTree();
        for (int i = 0; i < tree.getChild(2).getChildCount(); i++)
        {
            assert tree.getChild(2).getChild(i).getType() == ENUM_ELEMENT : tree.getChild(2).toStringTree();
            String literal = tree.getChild(2).getChild(i).getChild(0).getText();
            CgsuiteEnumValue value = new CgsuiteEnumValue(this, literal, i);
            assign(literal, value);
            value.assign("Ordinal", new RationalNumber(i, 1));
        }

        for (int i = 3; i < tree.getChildCount(); i++)
        {
            switch (tree.getChild(i).getType())
            {
                case METHOD:
                case PROPERTY:
                    declaration(tree.getChild(i));
                    break;

                default:
                    throw new RuntimeException();
            }
        }
    }

    private void declaration(CgsuiteTree tree) throws CgsuiteException
    {
        String methodName;
        String javaMethodName = null;
        CgsuiteTree body = null;

        switch (tree.token.getType())
        {
            case METHOD:

                // TODO modifiers
                methodName = methodName(tree.getChild(1));
                List<Parameter> parameters = methodParameters(tree.getChild(2));

                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declareMethod(new CgsuiteMethod
                    (this, methodName, parameters, body, javaMethodName));

                return;

            case PROPERTY:

                methodName = tree.getChild(1).getText() + "$" + tree.getChild(2).getText();

                if (tree.getChild(3).getType() == JAVA)
                    javaMethodName = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declareMethod(new CgsuiteMethod
                    (this, methodName, Collections.<Parameter>emptyList(), body, javaMethodName));

                return;

            default:

                throw new RuntimeException();
        }
    }

    private void declareMethod(CgsuiteMethod method)
    {
        this.methods.put(method.getName(), method);
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

                throw new RuntimeException();
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

                throw new RuntimeException();
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

                throw new RuntimeException();
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
        loaded = false;
    }

    @Override
    public void fileDeleted(FileEvent fe)
    {
        loaded = false;
    }

    @Override
    public void fileRenamed(FileRenameEvent fre)
    {
        loaded = false;
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fae)
    {
    }
}
