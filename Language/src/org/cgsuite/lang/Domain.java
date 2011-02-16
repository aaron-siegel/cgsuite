package org.cgsuite.lang;

import java.io.File;
import java.io.IOException;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.cgsuite.lang.CgsuiteLexer.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.runtime.RecognitionException;

import org.cgsuite.CanonicalShortGame;
import org.cgsuite.CgsuiteException;
import org.cgsuite.ExplicitGame;
import org.cgsuite.Game;
import org.cgsuite.RationalNumber;
import org.cgsuite.lang.CgsuiteMethod.Parameter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;

public class Domain
{
    public static final Domain CLASS_DOMAIN = new Domain("C:/Users/asiegel/Documents/NetBeansProjects/CGSuite/cglib/");
    private File[] classpath;
    private FileObject rootFo;
    private Map<String,Capsule> capsules;
    private Map<String,CgsuiteClass> classes;
/*
    static
    {
        try
        {
            CLASS_DOMAIN.load(new FileInputStream("/Users/asiegel/Documents/NetBeansProjects/CGSuite/cglib/classdefs.cgs"));
        }
        catch (Exception exc)
        {
            throw new RuntimeException(exc);
        }
    }
*/
    private Namespace namespace;

    public Domain()
    {
        namespace = new Namespace();
        this.classpath = new File[0];
    }

    public Domain(String cp)
    {
        namespace = new Namespace();
        this.classpath = new File[] { new File(cp) };
        this.capsules = new HashMap<String,Capsule>();
        this.classes = new HashMap<String,CgsuiteClass>();

        init();
    }

    private void init()
    {
        try
        {
            LocalFileSystem fs = new LocalFileSystem();
            fs.setRootDirectory(classpath[0]);
            rootFo = fs.getRoot();
            for (FileObject child : rootFo.getChildren())
            {
                capsules.put(child.getName(), new Capsule(child));
            }
        }
        catch (Exception exc)
        {
        }
    }

    public void refresh()
    {
        rootFo.refresh();
    }

    public CgsuiteObject lookup(String str)
    {
        if (CLASS_DOMAIN.capsules.containsKey(str))
        {
            return CLASS_DOMAIN.lookupClass(str);
        }
        else if (namespace.get(str) != null)
        {
            return namespace.get(str);
        }
        else
        {
            return null;
        }
    }

/*
    public CgsuiteClass lookupClass(String str)
    {
        CgsuiteObject obj = lookup(str);
        if (obj == null || !(obj instanceof CgsuiteClass))
            throw new RuntimeException(str);
        return (CgsuiteClass) obj;
    }
*/
    public void loadDeclarations(CgsuiteClass type)
    {
        for (CgsuiteClass parent : type.getParents())
        {
            parent.ensureFullyLoaded();
        }
        try
        {
            declarations(type, capsules.get(type.getName()).loadParseTree().getChild(0));
        }
        catch (RecognitionException exc)
        {
            throw new CgsuiteException(exc);    // Can't happen
        }
        catch (IOException exc)
        {
            throw new CgsuiteException(exc);    // Can't happen
        }
    }

    public CgsuiteClass lookupClass(String str) throws CgsuiteException
    {
        Capsule capsule = capsules.get(str);
        if (capsule == null)
        {
            throw new CgsuiteException("Class not found: " + str);
        }
        if (!capsule.isLoaded())
        {
            try
            {
                CgsuiteTree tree = capsule.loadParseTree();
                loadClass(tree, 0);
            }
            catch (RecognitionException exc)
            {
                throw new CgsuiteException("Syntax error in file " + str + ".cgs.", exc);
            }
            catch (IOException exc)
            {
                throw new CgsuiteException("I/O error reading file " + str + ".cgs.", exc);
            }
        }
        return classes.get(str);
    }

    public void loadClass(CgsuiteTree tree, int stage) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case EOF:

                classdef(tree.getChild(0));
                return;

            default:

                throw new RuntimeException();
        }
    }

    public void classdef(CgsuiteTree tree) throws CgsuiteException
    {
        String name;

        switch (tree.getToken().getType())
        {
            case CLASS:

                name = tree.getChild(0).getText();
                List<CgsuiteClass> parents;
                String javaClassname;

                int i = 1;

                if ("Object".equals(name))
                    parents = emptyList();
                else if (tree.getChild(i).getToken().getType() == EXTENDS)
                    parents = extendsClause(tree.getChild(i++));
                else
                    parents = singletonList(CgsuiteClass.OBJECT);

                if (tree.getChild(i).getToken().getType() == JAVA)
                    javaClassname = javaref(tree.getChild(i++));
                else
                    javaClassname = CgsuiteObject.class.getName();

                CgsuiteClass type;

                if ("Object".equals(name))
                    type = CgsuiteClass.OBJECT;
                else if ("Class".equals(name))
                    type = CgsuiteClass.CLASS;
                else
                    type = new CgsuiteClass(name, parents, javaClassname);
                classes.put(name, type);
                break;

            case ENUM:

                name = tree.getChild(1).getText();
                type = new CgsuiteClass(name, singletonList(lookupClass("Enum")), CgsuiteEnumValue.class.getName());
                classes.put(name, type);
                break;
                
            default:

                throw new RuntimeException();
        }
    }
    /*

                    case 1:

                        type = lookupClass(name);
                        type.load();
                        break;

                    case 2:

                        type = lookupClass(name);
                        for (; i < tree.getChildCount(); i++)
                            declaration(type, tree.getChild(i));
                        break;

                }

                return;

        }
    }*/

    public void put(String str, CgsuiteObject object)
    {
        namespace.put(str, object);
    }
/*
    public void loadFile(String filename) throws IOException, CgsuiteException, RecognitionException
    {
        load(new FileInputStream(filename));
    }

    public void load(InputStream in) throws IOException, CgsuiteException, RecognitionException
    {
        ANTLRInputStream input = new ANTLRInputStream(in);
        CgsuiteLexer lexer = new CgsuiteLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CgsuiteParser parser = new CgsuiteParser(tokens);
        parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
        CgsuiteParser.compilationUnit_return r = parser.compilationUnit();
        CgsuiteTree t = (CgsuiteTree) r.getTree();
        classfile(t, 0);
        classfile(t, 1);
        classfile(t, 2);
    }

    public void classfile(CgsuiteTree tree, int stage) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case EOF:

                // TODO There should be only one.
                for (CgsuiteTree child : tree.getChildren())
                    classdef(child, stage);
                return;

            default:

                throw new RuntimeException();
        }
    }

    public void classdef(CgsuiteTree tree, int stage) throws CgsuiteException
    {
        switch (tree.getToken().getType())
        {
            case CLASS:

                String name = tree.getChild(0).getText();
                List<CgsuiteClass> parents;
                String javaClassname;

                int i = 1;

                if ("Object".equals(name))
                    parents = emptyList();
                else if (tree.getChild(i).getToken().getType() == EXTENDS)
                    parents = extendsClause(tree.getChild(i++));
                else
                    parents = singletonList(CgsuiteClass.OBJECT);

                if (tree.getChild(i).getToken().getType() == JAVA)
                    javaClassname = javaref(tree.getChild(i++));
                else
                    javaClassname = CgsuiteObject.class.getName();

                CgsuiteClass type;

                switch (stage)
                {
                    case 0:

                        if ("Object".equals(name))
                            type = CgsuiteClass.OBJECT;
                        else if ("Class".equals(name))
                            type = CgsuiteClass.CLASS;
                        else
                            type = new CgsuiteClass(name, parents, javaClassname);
                        namespace.put(name, type);
                        break;

                    case 1:

                        type = lookupClass(name);
                        type.load();
                        break;

                    case 2:

                        type = lookupClass(name);
                        for (; i < tree.getChildCount(); i++)
                            declaration(type, tree.getChild(i));
                        break;

                }

                return;

            default:

                throw new RuntimeException();
        }
    }
*/
    public List<CgsuiteClass> extendsClause(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case EXTENDS:

                List<CgsuiteClass> parents = new ArrayList<CgsuiteClass>(tree.getChildCount());
                for (CgsuiteTree child : tree.getChildren())
                {
                    CgsuiteClass parent = lookupClass(child.getText());
                    assert parent != null : child.getText();
                    parents.add(parent);
                }
                return parents;

            default:

                throw new RuntimeException();
        }
    }

    public String javaref(CgsuiteTree tree) throws CgsuiteException
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

    private void declarations(CgsuiteClass declaringClass, CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.getType())
        {
            case CLASS:

                classDeclarations(declaringClass, tree);
                break;

            case ENUM:

                enumDeclarations(declaringClass, tree);
                break;

            default:

                throw new RuntimeException();
        }
    }

    private void classDeclarations(CgsuiteClass declaringClass, CgsuiteTree tree) throws CgsuiteException
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
                    declaration(declaringClass, tree.getChild(i));
                    break;

                default:
                    throw new RuntimeException();
            }
        }
    }

    private void enumDeclarations(CgsuiteClass declaringClass, CgsuiteTree tree) throws CgsuiteException
    {
        int ordinal = 0;
        for (int i = 2; i < tree.getChildCount(); i++)
        {
            String literal = tree.getChild(i).getText();
            CgsuiteEnumValue value = new CgsuiteEnumValue(declaringClass, literal, ordinal);
            declaringClass.assign(literal, value);
            value.assign("Ordinal", new RationalNumber(ordinal, 1));
            ordinal++;
        }
    }

    public void declaration(CgsuiteClass declaringClass, CgsuiteTree tree) throws CgsuiteException
    {
        String name;
        String javaClassname = null;
        CgsuiteTree body = null;

        switch (tree.token.getType())
        {
            case METHOD:

                // TODO modifiers
                name = methodName(tree.getChild(1));
                List<Parameter> parameters = methodParameters(tree.getChild(2));

                if (tree.getChild(3).getType() == JAVA)
                    javaClassname = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declaringClass.declareMethod(new CgsuiteMethod
                    (declaringClass, name, parameters, body, javaClassname));

                return;

            case PROPERTY:

                name = tree.getChild(1).getText() + "$" + tree.getChild(2).getText();

                if (tree.getChild(3).getType() == JAVA)
                    javaClassname = javaref(tree.getChild(3));
                else
                    body = tree.getChild(3);

                declaringClass.declareMethod(new CgsuiteMethod
                    (declaringClass, name, Collections.<Parameter>emptyList(), body, javaClassname));

                return;

            default:

                throw new RuntimeException();
        }
    }

    public String methodName(CgsuiteTree tree) throws CgsuiteException
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

    public List<Parameter> methodParameters(CgsuiteTree tree) throws CgsuiteException
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

    public Parameter methodParameter(CgsuiteTree tree) throws CgsuiteException
    {
        String name;
        CgsuiteClass type;
        CgsuiteTree defaultValue;

        switch (tree.token.getType())
        {
            case IDENTIFIER:

                name = tree.getText();
                type = (tree.getChildCount() > 0)? lookupClass(tree.getChild(0).getText()) : CgsuiteClass.OBJECT;
                return new Parameter(name, type, false, null);

            case QUESTION:

                CgsuiteTree subt = tree.getChild(0);
                name = subt.getText();
                type = (subt.getChildCount() > 0)? lookupClass(subt.getChild(0).getText()) : CgsuiteClass.OBJECT;
                defaultValue = (tree.getChildCount() > 1)? tree.getChild(1) : null;
                return new Parameter(name, type, true, defaultValue);

            default:

                throw new RuntimeException();
        }
    }

    public CgsuiteObject script(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case EOF:
                return statementSequence(tree.getChild(0));

            default:
                throw new RuntimeException();
        }
    }

    public CgsuiteObject statementSequence(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case CgsuiteLexer.STATEMENT_SEQUENCE:

                CgsuiteObject retval = CgsuiteObject.NIL;
                for (CgsuiteTree child : tree.getChildren())
                    retval = statement(child);
                return retval;

            default:

                throw new RuntimeException(tree.toStringTree());
        }
    }

    public CgsuiteObject statement(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case BREAK:     throw new BreakException(false);

            case CONTINUE:  throw new BreakException(true);

            case RETURN:    throw new ReturnException(expression(tree.getChild(0)));

            case CLEAR:     Domain.CLASS_DOMAIN.classes.clear();
                            Domain.CLASS_DOMAIN.init();
                            return CgsuiteObject.NIL;

            default:        return expression(tree);
        }
    }

    public void assignTo(CgsuiteTree tree, CgsuiteObject x) throws CgsuiteException
    {
        String id;
        CgsuiteObject obj;

        switch (tree.token.getType())
        {
            case IDENTIFIER:

                id = tree.getText();
                namespace.put(id, x);
                return;

            case ARRAY_REFERENCE:

                obj = expression(tree.getChild(0));
                obj.invoke("op []:=", arrayIndexList(tree.getChild(1), x));
                return;

            case DOT:

                obj = expression(tree.getChild(0));
                id = tree.getChild(1).getText();
                obj.assign(id, x);
                return;

            default:

                throw new InputException(tree.getToken(), "Not a valid assignment antecedent.");

        }
    }

    public CgsuiteObject expression(CgsuiteTree tree) throws CgsuiteException
    {
        CgsuiteObject obj = tree.getConstValue();

        if (obj != null)
            return obj;
        
        obj = expression2(tree);

        if (!tree.checkedConst())
        {
            CgsuiteObject constValue;
            switch (tree.token.getType())
            {
                case ASSIGN:
                case IDENTIFIER:
                case THIS:
                case EXPLICIT_LIST:
                case EXPLICIT_SET:
                case EXPLICIT_MAP:
                    constValue = null;
                    break;

                default:
                    constValue = obj;
                    for (CgsuiteTree child : tree.getChildren())
                    {
                        if (child.getConstValue() == null)
                        {
                            constValue = null;
                            break;
                        }
                    }
                    break;
            }
            tree.setCheckedConst(true);
            tree.setConstValue(constValue);
        }

        return obj;
    }

    public CgsuiteObject expression2(CgsuiteTree tree) throws CgsuiteException
    {
        boolean cond;
        String id;
        CgsuiteObject x, y;
        int n;
        List<CgsuiteObject> list;
        Map<String,CgsuiteObject> argmap;
        Set<CgsuiteObject> lo, ro;

        switch (tree.token.getType())
        {
            case RARROW:

                List<String> parameters = procedureParameters(tree.getChild(0));
                CgsuiteTree procedureTree = tree.getChild(1);
                return new CgsuiteProcedure(parameters, procedureTree, this);

            case DO:
                
                return doLoop(tree);

            case IN:

                return inLoop(tree);

            case IF:
            case ELSEIF:

                cond = bool(expression(tree.getChild(0)), tree.getChild(0));

                if (cond)
                    return statementSequence(tree.getChild(1));
                else if (tree.getChildCount() > 2) // Next elseif clause
                    return expression(tree.getChild(2));
                else
                    return CgsuiteObject.NIL;

            case ELSE:

                return statementSequence(tree.getChild(0));

            case ASSIGN:

                x = expression(tree.getChild(1)).invoke("Simplify");
                assignTo(tree.getChild(0), x);
                return x;

            case OR:

                if (bool(expression(tree.getChild(0)), tree))
                    return CgsuiteBoolean.TRUE;
                else
                    return cgsuiteBool(expression(tree.getChild(1)), tree);

            case AND:

                if (!bool(expression(tree.getChild(0)), tree))
                    return CgsuiteBoolean.FALSE;
                else
                    return cgsuiteBool(expression(tree.getChild(1)), tree);

            case NOT:

                return cgsuiteBool(expression(tree.getChild(0)), tree).not();

            case REFEQUALS:
            case REFNEQ:
            case EQUALS:
            case NEQ:
            case LEQ:
            case GEQ:
            case LT:
            case GT:
            case CONFUSED:
            case COMPARE:
            case PLUS:
            case MINUS:
            case AST:
            case FSLASH:
            case PERCENT:
            case EXP:

                return binopExpression(tree);

            case PLUSMINUS:

                lo = new HashSet<CgsuiteObject>();
                for (CgsuiteTree child : tree.getChildren())
                    lo.add(expression(child));
                ro = new HashSet<CgsuiteObject>();
                for (CgsuiteObject obj : lo)
                    ro.add(obj.invoke("op neg"));
                return new ExplicitGame(lo, ro);

            case UNARY_MINUS:

                return expression(tree.getChild(0)).invoke("op neg");

            case UNARY_PLUS:

                return expression(tree.getChild(0)).invoke("op pos");

            case UNARY_AST:

                if (tree.getChildCount() == 0)
                    n = 1;
                else
                    n = naturalNumber(expression(tree.getChild(0)), tree);
                return new CanonicalShortGame(RationalNumber.ZERO, 0, n);

            case CARET:
            case CARETCARET:
            case VEE:
            case VEEVEE:

                return upExpression(tree);

            case DOT:

                x = expression(tree.getChild(0));
                x = x.invoke("Simplify");
                try
                {
                    return x.resolve(tree.getChild(1).getText());
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }

            case ARRAY_REFERENCE:

                x = expression(tree.getChild(0));
                list = arrayIndexList(tree.getChild(1), null);
                return x.invoke("op []", list);

            case FUNCTION_CALL:

                x = expression(tree.getChild(0));
                list = argumentList(tree.getChild(1));
                argmap = optionalArgumentMap(tree.getChild(1));
                Callable target = invocationTarget(x, tree);
                try
                {
                    return target.invoke(list, argmap);
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }

            case NIL:       return CgsuiteObject.NIL;

            case TRUE:      return CgsuiteBoolean.TRUE;

            case FALSE:     return CgsuiteBoolean.FALSE;

            case INTEGER:   return RationalNumber.parseRationalNumber(tree.getText());

            case STRING:    return new CgsuiteString(tree.getText().substring(1, tree.getText().length()-1));

            case IDENTIFIER:

                id = tree.getText();
                x = lookup(id);
                if (x == null)
                    throw new InputException(tree.getToken(), "That variable is not defined: " + id);
                return x;

            case THIS:

                x = lookup("this");
                if (x == null)
                    throw new InputException(tree.getToken(), "Reference to \"this\" is not permitted in a static context.");
                return x;

            case STATEMENT_SEQUENCE:    return statementSequence(tree);

            case SLASHES:

                lo = gameOptions(tree.getChild(0));
                ro = gameOptions(tree.getChild(1));
                return new ExplicitGame(lo, ro);

            case EXPLICIT_MAP:

                CgsuiteMap map = new CgsuiteMap();
                for (CgsuiteTree child : tree.getChildren())
                    map.put(expression(child.getChild(0)).invoke("Simplify"), expression(child.getChild(1)).invoke("Simplify"));
                return map;

            case EXPLICIT_SET:

                CgsuiteSet set = new CgsuiteSet();
                for (CgsuiteTree child : tree.getChildren())
                    set.add(expression(child).invoke("Simplify"));
                return set;

            case EXPLICIT_LIST:

                CgsuiteList array = new CgsuiteList();
                for (CgsuiteTree child : tree.getChildren())
                    array.add(expression(child).invoke("Simplify"));
                return array;

            default:

                throw new RuntimeException(tree.getText());
        }
    }

    public List<String> procedureParameters(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case PROCEDURE_PARAMETER_LIST:

                List<String> parameters = new ArrayList<String>(tree.getChildCount());
                for (int i = 0; i < tree.getChildCount(); i++)
                    parameters.add(tree.getChild(i).getText());

                return parameters;

            default:

                throw new RuntimeException();
        }
    }

    private CgsuiteObject doLoop(CgsuiteTree tree) throws CgsuiteException
    {
        String forId = null;
        CanonicalShortGame fromG = null;
        CanonicalShortGame toG = null;
        CanonicalShortGame byG = CanonicalShortGame.ONE;
        CgsuiteTree whileCondition = null;
        CgsuiteTree whereCondition = null;
        CgsuiteTree body = null;

        for (CgsuiteTree child : tree.getChildren())
        {
            switch (child.getToken().getType())
            {
                case FOR:

                    forId = child.getChild(0).getText();
                    break;

                case FROM:

                    fromG = canonicalGame(expression(child.getChild(0)), child);
                    break;

                case TO:

                    toG = canonicalGame(expression(child.getChild(0)), child);
                    break;

                case BY:

                    byG = canonicalGame(expression(child.getChild(0)), child);
                    break;

                case WHILE:

                    whileCondition = child.getChild(0);
                    break;

                case WHERE:

                    whereCondition = child.getChild(0);
                    break;

                case STATEMENT_SEQUENCE:

                    body = child;
                    break;

                default:
                    
                    throw new RuntimeException();
            }
        }

        if (forId != null && fromG != null)
        {
            put(forId, fromG);
        }

        CanonicalShortGame g;

        if (fromG != null)
        {
            g = fromG;
        }
        else if (forId != null)
        {
            // TODO Lookup validation; tree-> from tree
            CgsuiteObject forObj = lookup(forId);
            if (forObj == null)
            {
                throw new InputException(tree.getChild(0).getChild(0).getToken(),
                    "Undefined variable in for loop with no \"from\" clause: " + forId);
            }
            g = canonicalGame(forObj, tree);
        }
        else
        {
            g = null;
        }

        CgsuiteObject retval = CgsuiteObject.NIL;

        while (true)
        {
            if (g != null && toG != null && !g.leq(toG))
            {
                break;
            }

            if (whileCondition != null && !bool(expression(whileCondition), whileCondition))
            {
                break;
            }

            if (whereCondition == null || bool(expression(whereCondition), whereCondition))
            {
                try
                {
                    retval = statementSequence(body);
                }
                catch (BreakException exc)
                {
                    retval = CgsuiteObject.NIL;
                    if (!exc.isContinue())
                    {
                        break;
                    }
                }
            }

            if (g != null && forId != null)
            {
                g = g.add(byG);
                put(forId, g);
            }
        }

        return retval;
    }

    private CgsuiteObject inLoop(CgsuiteTree tree) throws CgsuiteException
    {
        String forId = tree.getChild(0).getText();
        CgsuiteObject collection = expression(tree.getChild(1));
        CgsuiteTree body = tree.getChild(2);

        Iterator<CgsuiteObject> it;

        if (collection instanceof CgsuiteList)
        {
            it = ((CgsuiteList) collection).iterator();
        }
        else if (collection instanceof CgsuiteSet)
        {
            it = ((CgsuiteSet) collection).iterator();
        }
        else
        {
            throw new InputException(tree.getToken(), "Not a valid collection.");
        }

        CgsuiteObject retval = CgsuiteObject.NIL;

        while (it.hasNext())
        {
            namespace.put(forId, it.next());
            try
            {
                retval = statementSequence(body);
            }
            catch (BreakException exc)
            {
                retval = CgsuiteObject.NIL;
                if (!exc.isContinue())
                {
                    break;
                }
            }
        }

        return retval;
    }

    private CgsuiteObject binopExpression(CgsuiteTree tree) throws CgsuiteException
    {
        CgsuiteObject x = expression(tree.getChild(0));
        CgsuiteObject y = expression(tree.getChild(1));

        switch (tree.token.getType())
        {
            case REFEQUALS:
            case REFNEQ:
            case EQUALS:
            case NEQ:
            case LEQ:
            case GEQ:
            case LT:
            case GT:
            case CONFUSED:
            case COMPARE:
                x = x.invoke("Simplify");
                y = y.invoke("Simplify");
                break;
        }

        try
        {
            switch (tree.token.getType())
            {
                case REFEQUALS: return CgsuiteBoolean.valueOf(x.equals(y));
                case REFNEQ:    return CgsuiteBoolean.valueOf(!x.equals(y));
                case EQUALS:    return x.invoke("op ==", y);
                case NEQ:       return x.invoke("op !=", y);
                case LEQ:       return x.invoke("op <=", y);
                case GEQ:       return x.invoke("op >=", y);
                case LT:        return x.invoke("op <", y);
                case GT:        return x.invoke("op >", y);
                case CONFUSED:  return x.invoke("op <>", y);
                case COMPARE:   return x.invoke("op <=>", y);
                case PLUS:      return x.invoke("op +", y);
                case MINUS:     return x.invoke("op -", y);
                case AST:       return x.invoke("op *", y);
                case FSLASH:    return x.invoke("op /", y);
                case PERCENT:   return x.invoke("op %", y);
                case EXP:       return x.invoke("op **", y);
                default:        throw new RuntimeException();
            }
        }
        catch (InputException exc)
        {
            exc.addToken(tree.getToken());
            throw exc;
        }
    }

    private CgsuiteObject upExpression(CgsuiteTree tree) throws CgsuiteException
    {
        int m, n;
        CgsuiteTree starChild = null;
        CgsuiteTree nonStarChild = null;

        for (CgsuiteTree child : tree.getChildren())
        {
            if (child.getToken().getType() == UNARY_AST)
                starChild = child;
            else
                nonStarChild = child;
        }

        switch (tree.getToken().getType())
        {
            case CARETCARET:
                n = 2;
                break;

            case CARET:
                n = (nonStarChild == null)? 1 : integer(expression(nonStarChild), nonStarChild);
                break;

            case VEEVEE:
                n = -2;
                break;

            case VEE:
                n = (nonStarChild == null)? -1 : -integer(expression(nonStarChild), nonStarChild);
                break;

            default:
                throw new RuntimeException();
        }

        if (starChild == null)
            m = 0;
        else if (starChild.getChildCount() == 0)
            m = 1;
        else
            m = naturalNumber(expression(starChild.getChild(0)), starChild);

        return new CanonicalShortGame(RationalNumber.ZERO, n, m);
    }

    private Set<CgsuiteObject> gameOptions(CgsuiteTree tree) throws CgsuiteException
    {
        Set<CgsuiteObject> options;

        switch (tree.getToken().getType())
        {
            case SLASHES:

                return singleton(expression(tree));

            case EXPRESSION_LIST:

                options = new HashSet<CgsuiteObject>();
                for (CgsuiteTree child : tree.getChildren())
                {
                    CgsuiteObject obj = expression(child);
                    if (obj instanceof Game)
                    {
                        options.add(obj);
                    }
                    else if (obj instanceof CgsuiteCollection)
                    {
                        for (CgsuiteObject subObj : (CgsuiteCollection) obj)
                        {
                            if (!(subObj instanceof Game))
                                throw new InputException(child.getToken(), "All specified options must be games or collections of games.");
                            else
                                options.add(subObj);
                        }
                    }
                    else
                    {
                        throw new InputException(child.getToken(), "All specified options must be games or collections of games.");
                    }
                }
                return options;

            default:

                throw new RuntimeException(tree.getText());
        }
    }

    private List<CgsuiteObject> arrayIndexList(CgsuiteTree tree, CgsuiteObject prepend) throws CgsuiteException
    {
        List<CgsuiteObject> list;

        switch (tree.getToken().getType())
        {
            case ARRAY_INDEX_LIST:

                list = new ArrayList<CgsuiteObject>();
                if (prepend != null)
                    list.add(prepend);
                for (CgsuiteTree child : tree.getChildren())
                    list.add(expression(child).invoke("Simplify"));
                return list;

            default:

                throw new RuntimeException(tree.getText());
        }
    }

    private List<CgsuiteObject> argumentList(CgsuiteTree tree) throws CgsuiteException
    {
        List<CgsuiteObject> list;

        switch (tree.getToken().getType())
        {
            case FUNCTION_CALL_ARGUMENT_LIST:

                list = new ArrayList<CgsuiteObject>();
                for (CgsuiteTree child : tree.getChildren())
                {
                    if (child.getToken().getType() != BIGRARROW)
                        list.add(expression(child).invoke("Simplify"));
                }
                return list;

            default:

                throw new RuntimeException(tree.getText());
        }
    }

    private Map<String,CgsuiteObject> optionalArgumentMap(CgsuiteTree tree) throws CgsuiteException
    {
        Map<String,CgsuiteObject> map = null;

        switch (tree.getToken().getType())
        {
            case FUNCTION_CALL_ARGUMENT_LIST:

                for (CgsuiteTree child : tree.getChildren())
                {
                    if (child.getToken().getType() == BIGRARROW)
                    {
                        if (map == null)
                            map = new HashMap<String,CgsuiteObject>();
                        map.put(child.getChild(0).getText(), expression(child.getChild(1)).invoke("Simplify"));
                    }
                }
                return map;

            default:

                throw new RuntimeException(tree.getText());
        }
    }

    private Callable invocationTarget(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (x instanceof CgsuiteClass)
        {
            CgsuiteClass type = (CgsuiteClass) x;
            CgsuiteMethod ctor = type.lookupConstructor();
            if (ctor == null)
                throw new InputException(tree.token, "No constructor available: " + type);
            else
                return ctor;
        }
        else if (x instanceof Callable)
        {
            return (Callable) x;
        }
        else
        {
            throw new InputException(tree.token, "Antecedent is not a function.");
        }
    }

    private boolean bool(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        return cgsuiteBool(x, tree).booleanValue();
    }

    private CgsuiteBoolean cgsuiteBool(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (!(x instanceof CgsuiteBoolean))
        {
            x = x.invoke("Simplify");
            if (!(x instanceof CgsuiteBoolean))
                throw new InputException(tree.token, "Expression is not a boolean.");
        }

        return (CgsuiteBoolean) x;
    }

    private int naturalNumber(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        RationalNumber r = number(x, tree);

        if (!r.isInteger() || !r.isSmall() || r.intValue() < 0)
            throw new InputException(tree.token, "Argument to * is not a natural number.");

        return r.intValue();
    }

    private int integer(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        RationalNumber r = number(x, tree);

        if (!r.isInteger() || !r.isSmall())
            throw new InputException(tree.token, "Argument to * is not an integer.");

        return r.intValue();
    }

    private RationalNumber number(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (!(x instanceof RationalNumber))
        {
            x = x.invoke("Simplify");
            if (!(x instanceof RationalNumber))
                throw new InputException(tree.token, "Argument to * is not a number.");
        }

        return (RationalNumber) x;
    }

    private CanonicalShortGame canonicalGame(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (!(x instanceof CanonicalShortGame))
        {
            x = x.invoke("Simplify");
            if (x instanceof RationalNumber)
                x = new CanonicalShortGame((RationalNumber) x);
            if (!(x instanceof CanonicalShortGame))
                throw new InputException(tree.token, "Not a canonical game.");
        }

        return (CanonicalShortGame) x;
    }
}
