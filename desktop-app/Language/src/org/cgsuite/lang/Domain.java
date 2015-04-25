package org.cgsuite.lang;

import static org.cgsuite.lang.parser.CgsuiteLexer.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.Token;
import org.cgsuite.lang.game.CanonicalShortGame;
import org.cgsuite.lang.game.ExplicitGame;
import org.cgsuite.lang.game.Grid;
import org.cgsuite.lang.game.LoopyGame;
import org.cgsuite.lang.game.RationalNumber;
import org.cgsuite.lang.parser.CgsuiteTree;
import org.cgsuite.lang.parser.MalformedParseTreeException;

// TODO Simple enum assignment to grids

public class Domain
{
    private final static Logger log = Logger.getLogger(Domain.class.getName());

    private CgsuiteObject contextObject;
    private CgsuiteMethod contextMethod;
    private Namespace namespace;
    private List<CgsuitePackage> packageImports;
    private Map<String,CgsuiteClass> classImports;
    
    private Mode mode;
    private Token controlToken;
    
    public Domain(CgsuiteObject contextObject, CgsuiteMethod contextMethod)
    {
        this(contextObject, contextMethod, CgsuitePackage.DEFAULT_PACKAGE_IMPORTS, CgsuitePackage.DEFAULT_CLASS_IMPORTS);
    }

    public Domain(CgsuiteObject contextObject, CgsuiteMethod contextMethod, List<CgsuitePackage> packageImports, Map<String,CgsuiteClass> classImports)
    {
        this.contextObject = contextObject;
        this.contextMethod = contextMethod;
        this.packageImports = packageImports;
        this.classImports = classImports;
        this.namespace = new Namespace();
        this.mode = Mode.NORMAL;
        
        if (contextObject != null)
            this.namespace.put("this", contextObject);
    }

    public CgsuiteObject lookup(String str)
    {
        CgsuiteClass type = CgsuitePackage.lookupClass(str, packageImports, classImports);
        
        if (type != null)
        {
            type.ensureLoaded();
            return type;
        }

        if (contextObject != null)
        {
            CgsuiteObject obj = contextObject.resolve(str, contextMethod, true);
            
            if (obj != null)
                return obj;
        }
        
        if (contextMethod != null)
        {
            CgsuiteObject obj = contextMethod.getDeclaringClass().resolveStatic(str);
            
            if (obj != null)
                return obj;
        }
        
        return namespace.get(str);
    }

    public void put(String str, CgsuiteObject object)
    {
        CgsuiteClass type = CgsuitePackage.lookupClass(str, packageImports, classImports);

        if (type != null)
            throw new InputException("Cannot use class name as variable name: " + str);

        // TODO Prohibit other assignments e.g. to method name
        // TODO Static assignment

        if (contextObject != null && contextMethod.getDeclaringClass().lookupVar(str) != null)
        {
            contextObject.assign(str, object, contextMethod, true);
        }
        else
        {
            namespace.put(str, object.createCrosslink());
        }
    }
    
    public CgsuiteObject remove(String str)
    {
        return namespace.remove(str);
    }

    public CgsuiteObject script(CgsuiteTree tree) throws CgsuiteException
    {
        try
        {
            if (tree.getType() != EOF)
                throw new MalformedParseTreeException(tree);

            // Just in case there was a serious error on the previous invocation
            mode = Mode.NORMAL;

            CgsuiteObject retval = statementSequence(tree.getChild(0));

            if (mode != Mode.NORMAL)
            {
                mode = Mode.NORMAL;
                throw new InputException(controlToken, "Unexpected control statement: " + controlToken.getText());
            }

            return retval;
        }
        catch (MalformedParseTreeException exc)
        {
            log.log(Level.SEVERE, tree.toStringTree(), exc);
            throw exc;
        }
    }
    
    public CgsuiteObject procedureInvocation(CgsuiteTree tree) throws CgsuiteException
    {
        mode = Mode.NORMAL;
        
        CgsuiteObject retval = expression(tree);
        
        if (mode != Mode.NORMAL)
        {
            mode = Mode.NORMAL;
            throw new InputException(controlToken, "Unexpected control statement in procedure: " + controlToken.getText());
        }
        
        return retval;
    }

    public CgsuiteObject methodInvocation(CgsuiteTree tree) throws CgsuiteException
    {
        mode = Mode.NORMAL;
        
        statementSequence(tree);
        
        switch (mode)
        {
            case RETURNING:
                mode = Mode.NORMAL;
                return returnValue;
                
            case NORMAL:
                return CgsuiteObject.NIL;
                
            default:
                mode = Mode.NORMAL;
                throw new InputException(controlToken, "Unexpected control statement in method: " + controlToken.getText());
        }
    }

    public CgsuiteObject statementSequence(CgsuiteTree tree) throws CgsuiteException
    {
        if (Thread.interrupted())
        {
            throw new InputException("Calculation canceled by user.");
        }
        
        switch (tree.token.getType())
        {
            case STATEMENT_SEQUENCE:

                CgsuiteObject retval = CgsuiteObject.NIL;
                for (CgsuiteTree child : tree.getChildren())
                {
                    retval = statement(child);
                    if (mode != Mode.NORMAL)
                        break;
                }
                return retval;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private CgsuiteObject statement(CgsuiteTree tree) throws CgsuiteException
    {
        if (Thread.interrupted())
        {
            throw new InputException("Calculation canceled by user.");
        }
        
        switch (tree.token.getType())
        {
            case SEMI:      return CgsuiteObject.NIL;
            
            case BREAK:     mode = Mode.BREAKING; controlToken = tree.getToken(); return null;

            case CONTINUE:  mode = Mode.CONTINUING; controlToken = tree.getToken(); return null;

            case RETURN:    returnValue = (tree.getChildCount() == 0 ? CgsuiteObject.NIL : expression(tree.getChild(0)));
                            mode = Mode.RETURNING;
                            controlToken = tree.getToken(); 
                            return null;

            case CLEAR:     namespace.clear();  // XXX Clear classes?
                            return CgsuiteObject.NIL;

            case TRY:       return tryStatement(tree);

            default:        return expression(tree);
        }
    }
    
    private CgsuiteObject tryStatement(CgsuiteTree tree) throws CgsuiteException
    {
        assert tree.token.getType() == TRY;
        
        InputException ie = null;
        CgsuiteObject retval = Nil.NIL;
        
        try
        {
            retval = statementSequence(tree.getChild(0));
        }
        catch (InputException exc)
        {
            ie = exc;
        }
        
        statementSequence(tree.getChild(1));
        
        if (ie != null)
        {
            throw ie;
        }
        
        return retval;
    }

    private void assignTo(CgsuiteTree tree, CgsuiteObject x) throws CgsuiteException
    {
        String id;
        CgsuiteObject obj;

        switch (tree.token.getType())
        {
            case IDENTIFIER:

                id = tree.getText();
                try
                {
                    put(id, x.createCrosslink());
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }
                return;

            case ARRAY_REFERENCE:

                obj = expression(tree.getChild(0));
                try
                {
                    List<CgsuiteObject> indexList = arrayIndexList(tree.getChild(1), x);
                    obj.invokeMethod("op []:=", indexList);
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.token);
                    throw exc;
                }
                return;

            case DOT:

                obj = expression(tree.getChild(0));
                id = tree.getChild(1).getText();
                try
                {
                    obj.assign(id, x, contextMethod, false);
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.token);
                    throw exc;
                }
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
                case SUPER:
                case EXPLICIT_LIST:
                case EXPLICIT_MAP:
                case EXPLICIT_SET:
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

    private CgsuiteObject expression2(CgsuiteTree tree) throws CgsuiteException
    {
        boolean cond;
        String id;
        CgsuiteObject x, y;
        int n;
        List<CgsuiteObject> list;
        Map<String,CgsuiteObject> argmap;
        CgsuiteSet lo, ro;
        LoopyGame.Node node;
        
        switch (tree.token.getType())
        {
            case RARROW:

                List<String> parameters = procedureParameters(tree.getChild(0));
                return new CgsuiteProcedure(parameters, tree, this);

            case DO:
                
                return loop(tree);

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

                x = expression(tree.getChild(1)).simplify();
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
                
            case IS:
                
                x = expression(tree.getChild(0)).simplify();
                y = expression(tree.getChild(1)).simplify();
                if (y.getCgsuiteClass() != CgsuiteClass.TYPE)
                    throw new InputException(tree.getToken(), "Right-hand argument must be a class.");
                return CgsuiteBoolean.valueOf(x.getCgsuiteClass().hasAncestor((CgsuiteClass) y));

            case REFEQUALS:
            case REFNEQ:
            case EQUALS:
            case NEQ:
            case LEQ:
            case GEQ:
            case LT:
            case GT:
            case CONFUSED:
            case LCONFUSED:
            case GCONFUSED:
            case COMPARE:
            case DOTDOT:
            case PLUS:
            case MINUS:
            case AST:
            case FSLASH:
            case PERCENT:
            case EXP:
            case COLON:
            case AMPERSAND:

                return binopExpression(tree);

            case PLUSMINUS:

                lo = new CgsuiteSet();
                for (CgsuiteTree child : tree.getChildren())
                    lo.add(expression(child).simplify());
                ro = new CgsuiteSet();
                try
                {
                    for (CgsuiteObject obj : lo)
                        ro.add(obj.invokeMethod("op neg").simplify());
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }
                return new ExplicitGame(lo, ro);

            case UNARY_MINUS:

                x = expression(tree.getChild(0));
                try
                {
                    return x.invokeMethod("op neg");
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }

            case UNARY_PLUS:

                x = expression(tree.getChild(0));
                try
                {
                    return x.invokeMethod("op pos");
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }

            case UNARY_AST:

                if (tree.getChildCount() == 0)
                    n = 1;
                else
                    n = naturalNumber(expression(tree.getChild(0)), tree, "Argument to *");
                return CanonicalShortGame.construct(RationalNumber.ZERO, 0, n);

            case CARET:
            case MULTI_CARET:
            case VEE:
            case MULTI_VEE:

                return upExpression(tree);

            case DOT:

                CgsuitePackage packageValue = null;
                if (contextMethod == null)
                {
                    // If there's no context method (i.e., worksheet scope) we allow classes to be looked up
                    // by qualifying them with the package name.
                    packageValue = findPackage(tree.getChild(0));
                }
                if (packageValue == null)
                {
                    // No package was found - either because we have method scope, or because the lookup was
                    // not successful.
                    
                    x = expression(tree.getChild(0));
                    x = x.simplify();
                    id = tree.getChild(1).getText();
                    
                    CgsuiteObject retval;
                    try
                    {
                        retval = x.resolve(id, contextMethod, false);
                    }
                    catch (InputException exc)
                    {
                        exc.addToken(tree.getToken());
                        throw exc;
                    }
                    if (retval == null)
                    {
                        throw new InputException(tree.getToken(), "Not a member variable, property, or method: " + id + " (in object of type " + x.getCgsuiteClass().getName() + ")");
                    }
                    return retval;
                }
                else
                {
                    CgsuiteClass cls = packageValue.lookupClassInPackage(tree.getChild(1).getText());
                    if (cls == null)
                    {
                        throw new InputException(tree.getToken(), "Could not find class: " + packageValue.getName() + "." + tree.getChild(1).getText());
                    }
                    else
                    {
                        cls.ensureLoaded();
                        return cls;
                    }
                }

            case ARRAY_REFERENCE:

                x = expression(tree.getChild(0));
                list = arrayIndexList(tree.getChild(1), null);
                try
                {
                    return arrayRef(x, list);
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }

            case FUNCTION_CALL:

                x = expression(tree.getChild(0));
                if (tree.getChildCount() == 1)
                {
                    list = CgsuiteMethod.EMPTY_PARAM_LIST;
                    argmap = CgsuiteMethod.EMPTY_PARAM_MAP;
                }
                else
                {
                    list = argumentList(tree.getChild(1));
                    argmap = optionalArgumentMap(tree.getChild(1));
                }
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

            case INTEGER:   return CgsuiteInteger.parseInteger(tree.getText());

            case INF:       return RationalNumber.POSITIVE_INFINITY;

            case STRING:    return new CgsuiteString(tree.getText().substring(1, tree.getText().length()-1));

            case IDENTIFIER:

                id = tree.getText();
                try
                {
                    x = lookup(id);
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }
                if (x == null)
                {
                    throw new InputException(tree.getToken(), "That variable is not defined: " + id);
                }
                return x;

            case THIS:

                try
                {
                    x = lookup("this");
                }
                catch (InputException exc)
                {
                    exc.addToken(tree.getToken());
                    throw exc;
                }
                if (x == null)
                    // We use tree.getText() because this might actually be a reference to "super" (stealth "this")
                    throw new InputException(tree.getToken(), "Reference to \"" + tree.getText() + "\" is not permitted in a static context.");
                return x;

            case STATEMENT_SEQUENCE:    return statementSequence(tree);
                
            case SQUOTE:
                
                // TODO Integrate w/ loopy
                assert tree.getChild(0).getType() == SLASHES;
                lo = gameOptions(tree.getChild(0).getChild(0));
                ro = gameOptions(tree.getChild(0).getChild(1));
                return new ExplicitGame(lo, ro, true);
                
            case NODE_LABEL:
                
                node = loopyNode(tree, new HashMap<String,LoopyGame.Node>());
                return new LoopyGame(node);

            case SLASHES:

                if (hasPass(tree.getChild(0)) || hasPass(tree.getChild(1)))
                {
                    node = loopyNode(tree, new HashMap<String,LoopyGame.Node>());
                    return new LoopyGame(node);
                }
                else
                {
                    lo = gameOptions(tree.getChild(0));
                    ro = gameOptions(tree.getChild(1));
                    return new ExplicitGame(lo, ro);
                }

            case EXPLICIT_MAP:

                CgsuiteMap map = new CgsuiteMap();
                for (CgsuiteTree child : tree.getChildren())
                    map.put(expression(child.getChild(0)).simplify().createCrosslink(), expression(child.getChild(1)).simplify().createCrosslink());
                return map;

            case EXPLICIT_SET:

                CgsuiteSet set = new CgsuiteSet();
                for (CgsuiteTree child : tree.getChildren())
                    set.add(expression(child).simplify().createCrosslink());
                return set;

            case EXPLICIT_LIST:

                CgsuiteList array = new CgsuiteList();
                for (CgsuiteTree child : tree.getChildren())
                    array.add(expression(child).simplify().createCrosslink());
                return array;

            case ERROR:

                String msg = expression(tree.getChild(0)).toString();
                throw new InputException(tree.getToken(), msg);
                
            case PASS:
                
                throw new InputException(tree.getToken(), "Unexpected \"pass\".");

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    public static CgsuitePackage findPackage(CgsuiteTree tree) throws CgsuiteException
    {
        if (tree.checkedPackage())
            return tree.getPackageValue();

        String packageName = stringifyDotSequence(tree);
        CgsuitePackage packageValue = (packageName == null)? null : CgsuitePackage.lookupPackage(packageName);

        tree.setCheckedPackage(true);
        tree.setPackageValue(packageValue);
        return packageValue;
    }

    public static String stringifyDotSequence(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.getType())
        {
            case IDENTIFIER:

                return tree.getText().toString();

            case DOT:

                String parent = stringifyDotSequence(tree.getChild(0));
                if (parent == null)
                    return null;
                else
                    return parent + "." + tree.getChild(1).getText();

            default:

                return null;
        }
    }

    private List<String> procedureParameters(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case PROCEDURE_PARAMETER_LIST:

                List<String> parameters = new ArrayList<String>(tree.getChildCount());
                for (int i = 0; i < tree.getChildCount(); i++)
                    parameters.add(tree.getChild(i).getText());

                return parameters;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private CgsuiteObject loop(CgsuiteTree tree) throws CgsuiteException
    {
        CgsuiteCollection target = null;
        boolean sumTarget = false;
        CgsuiteObject runningSum = null;
        
        CgsuiteTree forTree = null;
        String forId = null;
        Iterator<?> it = null;
        CgsuiteObject fromG = null;
        CgsuiteObject toG = null;
        CgsuiteObject byG = CgsuiteInteger.ONE;
        boolean forward = true;
        CgsuiteTree whileCondition = null;
        CgsuiteTree whereCondition = null;
        CgsuiteTree body = null;

        for (CgsuiteTree child : tree.getChildren())
        {
            switch (child.getToken().getType())
            {
                case SETOF:
                    
                    target = new CgsuiteSet();
                    break;
                    
                case LISTOF:
                    
                    target = new CgsuiteList();
                    break;
                    
                case TABLEOF:
                    
                    target = new Table();
                    break;
                    
                case SUMOF:
                    
                    sumTarget = true;
                    break;
                
                case FOR:

                    forTree = child;
                    forId = child.getChild(0).getText();
                    if (contextMethod != null && contextMethod.getDeclaringClass().lookupVar(forId) != null)
                    {
                        throw new InputException(child.getChild(0).getToken(), "Loop variable name shadows member variable: " + forId);
                    }
                    break;

                case IN:
                    
                    CgsuiteObject obj = expression(child.getChild(0));
                    if (!(obj instanceof CgsuiteCollection) && !(obj instanceof CgsuiteIterator))
                    {
                        throw new InputException(child.getToken(), "Not a valid collection or iterator.");
                    }
                    it = ((Iterable<?>) obj).iterator();
                    break;
                
                case FROM:

                    fromG = expression(child.getChild(0)).simplify();
                    break;

                case TO:

                    toG = expression(child.getChild(0)).simplify();
                    break;

                case BY:

                    byG = expression(child.getChild(0)).simplify();
                    if (leq(byG, CgsuiteInteger.ZERO, child))
                        forward = false;
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
                    
                    throw new MalformedParseTreeException(tree);
            }
        }
        
        CgsuiteObject g = null;

        if (it == null)
        {
            if (fromG != null)
            {
                g = fromG;
            }
            else if (forId != null)
            {
                g = lookup(forId);
                if (g == null)
                {
                    throw new InputException(forTree.getChild(0).getToken(), "Undefined variable in for loop with no \"from\" clause: " + forId);
                }
            }
        }

        CgsuiteObject retval = CgsuiteObject.NIL;

        while (true)
        {
            if (g != null)
            {
                // Assign the forId.
                
                put(forId, g);
                
                // Check the "to" clause.
                
                if (toG != null)
                {
                    boolean ok;
                    
                    if (forward)
                        ok = leq(g, toG, tree);
                    else
                        ok = leq(toG, g, tree);

                    if (!ok)
                        break;
                }
            }
            
            if (it != null)
            {
                // Advance the "in" clause iterator.
            
                if (!it.hasNext())
                    break;
                
                CgsuiteObject obj = (CgsuiteObject) it.next();
                put(forId, obj);
            }
            
            // Test the while condition.

            if (whileCondition != null && !bool(expression(whileCondition), whileCondition))
            {
                break;
            }
            
            // Test the where condition.

            if (whereCondition == null || bool(expression(whereCondition), whereCondition))
            {
                retval = statementSequence(body);
                if (mode == Mode.RETURNING)
                {
                    retval = CgsuiteObject.NIL;
                    break;
                }
                else if (mode == Mode.BREAKING)
                {
                    mode = Mode.NORMAL;
                    retval = CgsuiteObject.NIL;
                    break;
                }
                else if (mode == Mode.CONTINUING)
                {
                    mode = Mode.NORMAL;
                    retval = CgsuiteObject.NIL;
                }
                else
                {
                    assert mode == Mode.NORMAL;
                    
                    // Add to the "of" collection (if appropriate).
                    
                    if (target != null)
                        target.add(retval.simplify());
                    
                    if (sumTarget)
                    {
                        try
                        {
                            runningSum = (runningSum == null)? retval : add(runningSum, retval);
                        }
                        catch (InputException exc)
                        {
                            exc.addToken(tree.getToken());
                            throw exc;
                        }
                    }
                }
            }
            
            // Step the dummy variable.

            if (g != null)
            {
                g = add(g, byG).simplify();
            }
        }

        if (target != null)
            return target;
        else if (sumTarget)
            return runningSum;
        else
            return retval;
    }

    private CgsuiteObject binopExpression(CgsuiteTree tree) throws CgsuiteException
    {
        CgsuiteObject x = expression(tree.getChild(0)).simplify();
        CgsuiteObject y = expression(tree.getChild(1)).simplify();
        
        try
        {
            switch (tree.token.getType())
            {
                case REFEQUALS: return CgsuiteBoolean.valueOf(x.equals(y));
                case REFNEQ:    return CgsuiteBoolean.valueOf(!x.equals(y));
                case EQUALS:    return eq(x, y, tree);
                case NEQ:       return x.invokeMethod("op !=", y);
                case LEQ:       return CgsuiteBoolean.valueOf(leq(x, y, tree));
                case GEQ:       return x.invokeMethod("op >=", y);
                case LT:        return x.invokeMethod("op <", y);
                case GT:        return x.invokeMethod("op >", y);
                case CONFUSED:  return x.invokeMethod("op <>", y);
                case LCONFUSED: return x.invokeMethod("op <|", y);
                case GCONFUSED: return x.invokeMethod("op |>", y);
                case COMPARE:   return x.invokeMethod("op <=>", y);
                case DOTDOT:    return range(x, y, tree);
                case PLUS:      return add(x, y);
                case MINUS:     return subtract(x, y);
                case AST:       return multiply(x, y);
                case FSLASH:    return x.invokeMethod("op /", y);
                case PERCENT:   return x.invokeMethod("op %", y);
                case EXP:       return x.invokeMethod("op ^", y);
                case COLON:     return x.invokeMethod("op :", y);
                case AMPERSAND: return x.invokeMethod("op &", y);
                default:        throw new MalformedParseTreeException(tree);
            }
        }
        catch (InputException exc)
        {
            exc.addToken(tree.getToken());
            throw exc;
        }
    }

    private CgsuiteObject arrayRef(CgsuiteObject x, List<CgsuiteObject> indexList) throws CgsuiteException
    {
        if (x instanceof CgsuiteList && indexList.size() == 1 && indexList.get(0) instanceof CgsuiteInteger)
        {
            int index = ((CgsuiteInteger) indexList.get(0)).intValue();
            CgsuiteObject value = ((CgsuiteList) x).get(index);
            return (value == null)? CgsuiteObject.NIL : value;
        }
        else if (x instanceof Grid && indexList.size() == 2 && indexList.get(0) instanceof CgsuiteInteger && indexList.get(1) instanceof CgsuiteInteger)
        {
            int row = ((CgsuiteInteger) indexList.get(0)).intValue();
            int col = ((CgsuiteInteger) indexList.get(1)).intValue();
            return ((Grid) x).getAt(row, col);
        }
        else
        {
            return x.invokeMethod("op []", indexList);
        }
    }
    /*
    private void arrayAssign(CgsuiteObject x, List<CgsuiteObject> indexList)
    {
        if (x instanceof CgsuiteList && indexList.size() == 2 && indexList.get(0) instanceof CgsuiteInteger)
        {
            int index = ((CgsuiteInteger) indexList.get(0)).intValue();
            ((CgsuiteList) x).set(index, indexList.get(1).createCrosslink());
        }
        else if (x instanceof Grid && indexList.size() == 3 &&
                 indexList.get(0) instanceof CgsuiteInteger && indexList.get(1) instanceof CgsuiteInteger && indexList.get(2) instanceof CgsuiteInteger)
        {
            int row = ((CgsuiteInteger) indexList.get(0)).intValue();
            int col = ((CgsuiteInteger) indexList.get(1)).intValue();
            int value = ((CgsuiteInteger) indexList.get(2)).intValue();
            ((Grid) x).putAt(row, col, value);
        }
        else
        {
            x.invokeMethod("op []:=", indexList);
        }
    }
    */
    private CgsuiteObject range(CgsuiteObject x, CgsuiteObject y, CgsuiteTree tree) throws CgsuiteException
    {
        int m = integer(x, tree, "Start of range");
        int n = integer(y, tree, "End of range");
        
        CgsuiteList list = new CgsuiteList();
        for (int i = m; i <= n; i++)
        {
            list.add(new CgsuiteInteger(i));
        }
        
        return list;
    }

    private CgsuiteObject add(CgsuiteObject x, CgsuiteObject y) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger && y instanceof CgsuiteInteger)
        {
            return ((CgsuiteInteger) x).add((CgsuiteInteger) y);
        }

        return x.invokeMethod("op +", y);
    }

    private CgsuiteObject subtract(CgsuiteObject x, CgsuiteObject y) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger && y instanceof CgsuiteInteger)
        {
            return ((CgsuiteInteger) x).subtract((CgsuiteInteger) y);
        }
        
        return x.invokeMethod("op -", y);
    }

    private CgsuiteObject multiply(CgsuiteObject x, CgsuiteObject y) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger && y instanceof CgsuiteInteger)
        {
            return ((CgsuiteInteger) x).multiply((CgsuiteInteger) y);
        }
        
        return x.invokeMethod("op *", y);
    }

    private boolean leq(CgsuiteObject x, CgsuiteObject y, CgsuiteTree tree) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger && y instanceof CgsuiteInteger)
        {
            return ((CgsuiteInteger) x).compareTo((CgsuiteInteger) y) <= 0;
        }
        
        CgsuiteObject obj;
        
        try
        {
            obj = x.invokeMethod("op <=", y);
        }
        catch (InputException exc)
        {
            exc.addToken(tree.getToken());
            throw exc;
        }

        return bool(obj, tree);
    }

    public CgsuiteObject eq(CgsuiteObject x, CgsuiteObject y, CgsuiteTree tree) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger && y instanceof CgsuiteInteger ||
            x instanceof RationalNumber && y instanceof RationalNumber ||
            x instanceof CanonicalShortGame && y instanceof CanonicalShortGame)
        {
            return CgsuiteBoolean.valueOf(x.equals(y));
        }

        return x.invokeMethod("op ==", y);
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
            case MULTI_CARET:
                n = tree.getText().length();
                break;

            case CARET:
                n = (nonStarChild == null)? 1 : integer(expression(nonStarChild), tree, "Argument to ^");
                break;

            case MULTI_VEE:
                n = -tree.getText().length();
                break;

            case VEE:
                n = (nonStarChild == null)? -1 : -integer(expression(nonStarChild), tree, "Argument to v");
                break;

            default:
                throw new MalformedParseTreeException(tree);
        }

        if (starChild == null)
            m = 0;
        else if (starChild.getChildCount() == 0)
            m = 1;
        else
            m = naturalNumber(expression(starChild.getChild(0)), starChild, "Argument to *");

        return CanonicalShortGame.construct(RationalNumber.ZERO, n, m);
    }

    private CgsuiteSet gameOptions(CgsuiteTree tree) throws CgsuiteException
    {
        CgsuiteSet options;

        switch (tree.getToken().getType())
        {
            case SLASHES:

                return CgsuiteSet.singleton(expression(tree));

            case EXPRESSION_LIST:

                options = new CgsuiteSet();
                for (CgsuiteTree child : tree.getChildren())
                {
                    CgsuiteObject obj = expression(child).simplify();
                    if (obj instanceof Game)
                    {
                        options.add(obj);
                    }
                    else if (obj instanceof CgsuiteCollection)
                    {
                        for (CgsuiteObject subObj : (CgsuiteCollection) obj)
                        {
                            subObj = subObj.simplify();
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

                throw new MalformedParseTreeException(tree);
        }
    }

    private boolean hasPass(CgsuiteTree tree)
    {
        for (CgsuiteTree child : tree.getChildren())
        {
            if (child.getType() == PASS)
                return true;
        }

        return false;
    }

    private LoopyGame.Node loopyNode(CgsuiteTree tree, Map<String,LoopyGame.Node> nodeMap) throws CgsuiteException
    {
        LoopyGame.Node curNode = new LoopyGame.Node();

        switch (tree.getToken().getType())
        {
            case NODE_LABEL:

                String nodeName = tree.getChild(0).getText();
                if (nodeMap.containsKey(nodeName))
                    throw new InputException(tree.getChild(0).getToken(), "Duplicate node label: " + nodeName);
                nodeMap.put(nodeName, curNode);
                loopyNode2(tree.getChild(1), nodeMap, curNode);
                break;

            case SLASHES:

                loopyNode2(tree, nodeMap, curNode);
                break;

            default:

                throw new MalformedParseTreeException(tree);

        }

        return curNode;
    }

    @SuppressWarnings("fallthrough")
    private void loopyNode2(CgsuiteTree tree, Map<String,LoopyGame.Node> nodeMap, LoopyGame.Node curNode) throws CgsuiteException
    {
        if (tree.getToken().getType() != SLASHES)
            throw new MalformedParseTreeException(tree);
        
        if (tree.getChild(0).getType() == SLASHES)
        {
            curNode.addLeftEdge(loopyNode(tree.getChild(0), nodeMap));
        }
        else
        {
            if (tree.getChild(0).getType() != EXPRESSION_LIST)
                throw new MalformedParseTreeException(tree);
            
            for (int i = 0; i < tree.getChild(0).getChildCount(); i++)
            {
                switch (tree.getChild(0).getChild(i).getType())
                {
                    case COLON:
                    case SLASHES:

                        curNode.addLeftEdge(loopyNode(tree.getChild(0).getChild(i), nodeMap));
                        break;

                    case PASS:

                        curNode.addLeftEdge(curNode);
                        break;

                    case IDENTIFIER:

                        String id = tree.getChild(0).getChild(i).getText();
                        if (nodeMap.containsKey(id))
                        {
                            curNode.addLeftEdge(nodeMap.get(id));
                            break;
                        }
                        // Else intentional case fallthrough

                    default:

                        CgsuiteObject obj = expression(tree.getChild(0).getChild(i)).simplify();
                        if (obj instanceof CgsuiteCollection)
                        {
                            for (CgsuiteObject x : (CgsuiteCollection) obj)
                                addLeftEdge(tree.getChild(0).getChild(i), curNode, x);
                        }
                        else
                        {
                            addLeftEdge(tree.getChild(0).getChild(i), curNode, obj);
                        }
                        break;
                }
            }
        }
        
        if (tree.getChild(1).getType() == SLASHES)
        {
            curNode.addRightEdge(loopyNode(tree.getChild(1), nodeMap));
        }
        else
        {
            if (tree.getChild(1).getType() != EXPRESSION_LIST)
                throw new MalformedParseTreeException(tree);
            
            for (int i = 0; i < tree.getChild(1).getChildCount(); i++)
            {
                switch (tree.getChild(1).getChild(i).getType())
                {
                    case COLON:
                    case SLASHES:

                        curNode.addRightEdge(loopyNode(tree.getChild(1).getChild(i), nodeMap));
                        break;

                    case PASS:

                        curNode.addRightEdge(curNode);
                        break;

                    case IDENTIFIER:

                        String id = tree.getChild(1).getChild(i).getText();
                        if (nodeMap.containsKey(id))
                        {
                            curNode.addRightEdge(nodeMap.get(id));
                            break;
                        }
                        // Else intentional case fallthrough

                    default:

                        CgsuiteObject obj = expression(tree.getChild(1).getChild(i)).simplify();
                        if (obj instanceof CgsuiteCollection)
                        {
                            for (CgsuiteObject x : (CgsuiteCollection) obj)
                                addRightEdge(tree.getChild(1).getChild(i), curNode, x);
                        }
                        else
                        {
                            addRightEdge(tree.getChild(1).getChild(i), curNode, obj);
                        }
                        break;
                }
            }
        }
    }
    
    private void addLeftEdge(CgsuiteTree tree, LoopyGame.Node node, CgsuiteObject obj)
    {
        if (obj instanceof CgsuiteInteger)
            node.addLeftEdge(CanonicalShortGame.construct((CgsuiteInteger) obj));
        else if (obj instanceof RationalNumber)
            node.addLeftEdge(CanonicalShortGame.construct((RationalNumber) obj));
        else if (obj instanceof CanonicalShortGame)
            node.addLeftEdge((CanonicalShortGame) obj);
        else if (obj instanceof LoopyGame)
            node.addLeftEdge((LoopyGame) obj);
        else
            throw new InputException(tree.getToken(),
                "Object of type " + obj.getCgsuiteClass().getQualifiedName() + " not permitted in a loopy game constructor.");
    }
    
    private void addRightEdge(CgsuiteTree tree, LoopyGame.Node node, CgsuiteObject obj)
    {
        if (obj instanceof CgsuiteInteger)
            node.addRightEdge(CanonicalShortGame.construct((CgsuiteInteger) obj));
        else if (obj instanceof RationalNumber)
            node.addRightEdge(CanonicalShortGame.construct((RationalNumber) obj));
        else if (obj instanceof CanonicalShortGame)
            node.addRightEdge((CanonicalShortGame) obj);
        else if (obj instanceof LoopyGame)
            node.addRightEdge((LoopyGame) obj);
        else
            throw new InputException(tree.getToken(),
                "Object of type " + obj.getCgsuiteClass().getQualifiedName() + " not permitted in a loopy game constructor.");
    }

    private List<CgsuiteObject> arrayIndexList(CgsuiteTree tree, CgsuiteObject value) throws CgsuiteException
    {
        List<CgsuiteObject> list;

        switch (tree.getToken().getType())
        {
            case ARRAY_INDEX_LIST:
                
                list = new ArrayList<CgsuiteObject>(tree.getChildCount() + (value == null ? 0 : 1));
                
                for (CgsuiteTree child : tree.getChildren())
                {
                    list.add(expression(child).simplify());
                }
                
                if (value != null)
                    list.add(value);
                
                return list;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private List<CgsuiteObject> argumentList(CgsuiteTree tree) throws CgsuiteException
    {
        List<CgsuiteObject> list;
        boolean foundOptional = false;

        switch (tree.getToken().getType())
        {
            case FUNCTION_CALL_ARGUMENT_LIST:

                list = new ArrayList<CgsuiteObject>();
                for (CgsuiteTree child : tree.getChildren())
                {
                    if (child.getToken().getType() == BIGRARROW)
                    {
                        foundOptional = true;
                    }
                    else if (foundOptional)
                    {
                        throw new InputException(child.getToken(),
                            "Ordinary arguments must precede optional arguments in function call.");
                    }
                    else
                    {
                        list.add(expression(child).simplify());
                    }
                }
                return list;

            default:

                throw new MalformedParseTreeException(tree);
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
                        map.put(child.getChild(0).getText(), expression(child.getChild(1)).simplify());
                    }
                }
                return (map == null)? CgsuiteMethod.EMPTY_PARAM_MAP : map;

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private Callable invocationTarget(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (x instanceof CgsuiteClass)
        {
            CgsuiteClass type = (CgsuiteClass) x;
            if (type.getScript() != null)
                return new Script(type);
            
            CgsuiteMethodGroup ctor = type.lookupConstructor();
            if (ctor == null)
                throw new InputException(tree.token, "No constructor available: " + type.getQualifiedName());
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

    public static boolean bool(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        return cgsuiteBool(x, tree).booleanValue();
    }

    public static CgsuiteBoolean cgsuiteBool(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (!(x instanceof CgsuiteBoolean))
        {
            x = x.simplify();
            if (!(x instanceof CgsuiteBoolean))
                throw new InputException(tree.token, "Expression is not a boolean.");
        }

        return (CgsuiteBoolean) x;
    }

    private int naturalNumber(CgsuiteObject x, CgsuiteTree tree, String errorMessageAntecedent) throws CgsuiteException
    {
        int intValue = integer(x, tree, errorMessageAntecedent);

        if (intValue < 0)
            throw new InputException(tree.token, errorMessageAntecedent + " is not a natural number.");

        return intValue;
    }

    private int integer(CgsuiteObject x, CgsuiteTree tree, String errorMessageAntecedent) throws CgsuiteException
    {
        if (!(x instanceof CgsuiteInteger))
        {
            x = x.simplify();
            if (!(x instanceof CgsuiteInteger))
                throw new InputException(tree.token, errorMessageAntecedent + " is not an integer.");
        }

        return ((CgsuiteInteger) x).intValue();
    }

    private RationalNumber number(CgsuiteObject x, CgsuiteTree tree, String errorMessageAntecedent) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger)
        {
            x = new RationalNumber((CgsuiteInteger) x);
        }
        else if (!(x instanceof RationalNumber))
        {
            x = x.simplify();
            if (!(x instanceof RationalNumber))
                throw new InputException(tree.token, errorMessageAntecedent + " is not a number.");
        }

        return (RationalNumber) x;
    }

    private CanonicalShortGame canonicalGame(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (!(x instanceof CanonicalShortGame))
        {
            x = x.simplify();
            if (x instanceof CgsuiteInteger)
                x = CanonicalShortGame.construct((CgsuiteInteger) x);
            else if(x instanceof RationalNumber)
                x = CanonicalShortGame.construct((RationalNumber) x);
            if (!(x instanceof CanonicalShortGame))
                throw new InputException(tree.token, "Not a canonical game.");
        }

        return (CanonicalShortGame) x;
    }
    
    private class Script implements Callable
    {
        private CgsuiteClass type;

        public Script(CgsuiteClass type)
        {
            this.type = type;
        }

        @Override
        public CgsuiteObject invoke(List<? extends CgsuiteObject> arguments, Map<String, CgsuiteObject> optionalArguments) throws CgsuiteException
        {
            if (!arguments.isEmpty() || !optionalArguments.isEmpty())
                throw new InputException(type.getQualifiedName() + " is a script, and must be called without arguments.");
            
            statementSequence(type.getScript());
            
            return CgsuiteObject.NIL;
        }
    }

    private enum Mode
    {
        NORMAL,
        BREAKING,
        CONTINUING,
        RETURNING;
    }

    private CgsuiteObject returnValue;
}
