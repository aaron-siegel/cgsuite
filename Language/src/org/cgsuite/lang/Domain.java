package org.cgsuite.lang;

import static org.cgsuite.lang.parser.CgsuiteLexer.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.cgsuite.lang.game.CanonicalShortGame;
import org.cgsuite.lang.game.ExplicitGame;
import org.cgsuite.lang.game.Grid;
import org.cgsuite.lang.game.LoopyGame;
import org.cgsuite.lang.game.RationalNumber;
import org.cgsuite.lang.parser.CgsuiteTree;
import org.cgsuite.lang.parser.MalformedParseTreeException;

public class Domain
{
    private final static Logger log = Logger.getLogger(Domain.class.getName());

    private Namespace namespace;
    private List<CgsuitePackage> imports;
    private Mode mode;

    public Domain(List<CgsuitePackage> imports)
    {
        this.namespace = new Namespace();
        this.imports = imports;
        this.mode = Mode.NORMAL;
    }

    public CgsuiteObject lookup(String str)
    {
        CgsuiteClass type = CgsuitePackage.lookupClass(str, imports);
        if (type != null)
        {
            return type;
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

    public void put(String str, CgsuiteObject object)
    {
        namespace.put(str, object);
    }

    public CgsuiteObject script(CgsuiteTree tree) throws CgsuiteException
    {
        switch (tree.token.getType())
        {
            case EOF:
                return statementSequence(tree.getChild(0));

            default:
                throw new MalformedParseTreeException(tree);
        }
    }

    public CgsuiteObject invocation(CgsuiteTree tree) throws CgsuiteException
    {
        CgsuiteObject retval = statementSequence(tree);
        if (mode == Mode.RETURNING)
        {
            mode = Mode.NORMAL;
            retval = returnValue;
        }
        return retval;
    }

    private CgsuiteObject statementSequence(CgsuiteTree tree) throws CgsuiteException
    {
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
        switch (tree.token.getType())
        {
            case BREAK:     mode = Mode.BREAKING; return null;

            case CONTINUE:  mode = Mode.CONTINUING; return null;

            case RETURN:    returnValue = expression(tree.getChild(0)); mode = Mode.RETURNING; return null;

            case CLEAR:     namespace.clear();  // XXX Clear classes?
                            return CgsuiteObject.NIL;

            default:        return expression(tree);
        }
    }

    private void assignTo(CgsuiteTree tree, CgsuiteObject x) throws CgsuiteException
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
                obj.invokeMethod("op []:=", arrayIndexList(tree.getChild(1), x));
                return;

            case DOT:

                obj = expression(tree.getChild(0));
                id = tree.getChild(1).getText();
                try
                {
                    obj.assign(id, x);
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
                CgsuiteTree procedureTree = tree.getChild(1);
                return new CgsuiteProcedure(parameters, procedureTree, this);

            case DO:
                
                return doLoop(tree);

            case IN:

                return inLoop(tree);

            case SETOF:

                return setOf(tree);

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

                lo = new CgsuiteSet();
                for (CgsuiteTree child : tree.getChildren())
                    lo.add(expression(child));
                ro = new CgsuiteSet();
                for (CgsuiteObject obj : lo)
                    ro.add(obj.invokeMethod("op neg"));
                return new ExplicitGame(lo, ro);

            case UNARY_MINUS:

                return expression(tree.getChild(0)).invokeMethod("op neg");

            case UNARY_PLUS:

                return expression(tree.getChild(0)).invokeMethod("op pos");

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

                CgsuitePackage packageValue = findPackage(tree.getChild(0));
                if (packageValue == null)
                {
                    x = expression(tree.getChild(0));
                    x = x.simplify();
                    try
                    {
                        return x.resolve(tree.getChild(1).getText());
                    }
                    catch (InputException exc)
                    {
                        exc.addToken(tree.getToken());
                        throw exc;
                    }
                }
                else
                {
                    x = packageValue.lookupClassInPackage(tree.getChild(1).getText());
                    if (x == null)
                    {
                        throw new InputException(tree.getToken(), "Could not find class: " + packageValue.getName() + "." + tree.getChild(1).getText());
                    }
                    else
                    {
                        return x;
                    }
                }

            case ARRAY_REFERENCE:

                x = expression(tree.getChild(0));
                list = arrayIndexList(tree.getChild(1), null);
                return arrayRef(x, list);

            case FUNCTION_CALL:

                x = expression(tree.getChild(0));
                if (tree.getChildCount() == 1)
                {
                    list = Collections.emptyList();
                    argmap = null;
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

            case INTEGER:   return RationalNumber.parseRationalNumber(tree.getText()).simplify();

            case INF:       return RationalNumber.POSITIVE_INFINITY;

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
                    // We use tree.getText() because this might actually be a reference to "super" (stealth "this")
                    throw new InputException(tree.getToken(), "Reference to \"" + tree.getText() + "\" is not permitted in a static context.");
                return x;

            case STATEMENT_SEQUENCE:    return statementSequence(tree);

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
                    map.put(expression(child.getChild(0)).simplify(), expression(child.getChild(1)).simplify());
                return map;

            case EXPLICIT_SET:

                CgsuiteSet set = new CgsuiteSet();
                for (CgsuiteTree child : tree.getChildren())
                    set.add(expression(child).simplify());
                return set;

            case EXPLICIT_LIST:

                CgsuiteList array = new CgsuiteList();
                for (CgsuiteTree child : tree.getChildren())
                    array.add(expression(child).simplify());
                return array;

            case COLON:

                node = loopyNode(tree, new HashMap<String,LoopyGame.Node>());
                return new LoopyGame(node);

            case ERROR:

                String msg = expression(tree.getChild(0)).toString();
                throw new InputException(tree.getToken(), msg);

            default:

                throw new MalformedParseTreeException(tree);
        }
    }

    private CgsuitePackage findPackage(CgsuiteTree tree) throws CgsuiteException
    {
        if (tree.checkedPackage())
            return tree.getPackageValue();

        String packageName = stringifyDotSequence(tree);
        CgsuitePackage packageValue = (packageName == null)? null : CgsuitePackage.lookupPackage(packageName);

        tree.setCheckedPackage(true);
        tree.setPackageValue(packageValue);
        return packageValue;
    }

    private String stringifyDotSequence(CgsuiteTree tree) throws CgsuiteException
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

    private CgsuiteObject doLoop(CgsuiteTree tree) throws CgsuiteException
    {
        String forId = null;
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
                case FOR:

                    forId = child.getChild(0).getText();
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

        if (forId != null && fromG != null)
        {
            put(forId, fromG);
        }

        CgsuiteObject g;

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
            if (g != null && toG != null)
            {
                boolean ok;
                if (forward)
                    ok = leq(g, toG, tree);
                else
                    ok = leq(toG, g, tree);

                if (!ok)
                    break;
            }

            if (whileCondition != null && !bool(expression(whileCondition), whileCondition))
            {
                break;
            }

            if (whereCondition == null || bool(expression(whereCondition), whereCondition))
            {
                retval = statementSequence(body);
                if (mode == Mode.BREAKING)
                {
                    mode = Mode.NORMAL;
                    retval = CgsuiteObject.NIL;
                    break;
                }
                if (mode == Mode.CONTINUING)
                {
                    mode = Mode.NORMAL;
                    retval = CgsuiteObject.NIL;
                }
            }

            if (g != null && forId != null)
            {
                g = add(g, byG);
                put(forId, g);
            }
        }

        return retval;
    }

    private CgsuiteObject inLoop(CgsuiteTree tree) throws CgsuiteException
    {
        String forId = tree.getChild(0).getText();
        CgsuiteObject collection = expression(tree.getChild(1));
        CgsuiteTree whereCondition = null;
        CgsuiteTree body = null;

        if (tree.getChild(2).getType() == WHERE)
        {
            whereCondition = tree.getChild(2).getChild(0);
            body = tree.getChild(3);
        }
        else
        {
            body = tree.getChild(2);
        }

        if (!(collection instanceof CgsuiteCollection))
        {
            throw new InputException(tree.getToken(), "Not a valid collection.");
        }

        Iterator<CgsuiteObject> it = ((CgsuiteCollection) collection).iterator();

        CgsuiteObject retval = CgsuiteObject.NIL;

        while (it.hasNext())
        {
            namespace.put(forId, it.next());
            if (whereCondition == null || bool(expression(whereCondition), whereCondition))
            {
                retval = statementSequence(body);
                if (mode == Mode.BREAKING)
                {
                    mode = Mode.NORMAL;
                    retval = CgsuiteObject.NIL;
                    break;
                }
                if (mode == Mode.CONTINUING)
                {
                    mode = Mode.NORMAL;
                    retval = CgsuiteObject.NIL;
                }
            }
        }

        return retval;
    }

    private CgsuiteObject setOf(CgsuiteTree tree) throws CgsuiteException
    {
        CgsuiteTree expr = tree.getChild(0);

        String forId = tree.getChild(1).getText();
        CgsuiteObject collection = expression(tree.getChild(2));
        CgsuiteTree whereCondition = null;

        if (tree.getChildCount() > 3)
        {
            whereCondition = tree.getChild(3).getChild(0);
        }

        if (!(collection instanceof CgsuiteCollection))
        {
            throw new InputException(tree.getToken(), "Not a valid collection.");
        }

        Iterator<CgsuiteObject> it = ((CgsuiteCollection) collection).iterator();

        CgsuiteSet retval = new CgsuiteSet();

        while (it.hasNext())
        {
            namespace.put(forId, it.next());
            if (whereCondition == null || bool(expression(whereCondition), whereCondition))
            {
                CgsuiteObject obj = expression(expr);
                if (mode == Mode.BREAKING)
                {
                    mode = Mode.NORMAL;
                    break;
                }
                else if(mode == Mode.CONTINUING)
                {
                    mode = Mode.NORMAL;
                }
                else
                {
                    retval.add(obj);
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
                x = x.simplify();
                y = y.simplify();
                break;
        }

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
                case COMPARE:   return x.invokeMethod("op <=>", y);
                case PLUS:      return add(x, y);
                case MINUS:     return subtract(x, y);
                case AST:       return multiply(x, y, tree);
                case FSLASH:    return x.invokeMethod("op /", y);
                case PERCENT:   return x.invokeMethod("op %", y);
                case EXP:       return x.invokeMethod("op **", y);
                default:        throw new MalformedParseTreeException(tree);
            }
        }
        catch (InputException exc)
        {
            exc.addToken(tree.getToken());
            throw exc;
        }
    }

    private CgsuiteObject arrayRef(CgsuiteObject x, List<CgsuiteObject> list) throws CgsuiteException
    {
        if (x instanceof CgsuiteList && list.size() == 1 && list.get(0) instanceof RationalNumber)
        {
            RationalNumber number = (RationalNumber) list.get(0);
            if (number.isInteger() && number.isSmall())
                return ((CgsuiteList) x).get(number.intValue());
        }
        else if (x instanceof Grid && list.size() == 2 && list.get(0) instanceof RationalNumber && list.get(1) instanceof RationalNumber)
        {
            RationalNumber row = (RationalNumber) list.get(0);
            RationalNumber col = (RationalNumber) list.get(1);
            if (row.isInteger() && col.isInteger())
                return new RationalNumber(((Grid) x).getAt(row.intValue(), col.intValue()), 1);
        }

        return x.invokeMethod("op []", list);
    }

    private CgsuiteObject add(CgsuiteObject x, CgsuiteObject y) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger)
        {
            if (y instanceof CgsuiteInteger)
                return ((CgsuiteInteger) x).add((CgsuiteInteger) y);
            else if (y instanceof RationalNumber)
                return ((RationalNumber) y).add(new RationalNumber((CgsuiteInteger) x));
            else if (y instanceof CanonicalShortGame)
                return ((CanonicalShortGame) y).add(new CanonicalShortGame((CgsuiteInteger) x));
        }
        else if (x instanceof RationalNumber)
        {
            if (y instanceof CgsuiteInteger)
                return ((RationalNumber) x).add(new RationalNumber((CgsuiteInteger) y));
            else if (y instanceof RationalNumber)
                return ((RationalNumber) x).add((RationalNumber) y);
            else if (y instanceof CanonicalShortGame && ((RationalNumber) x).isDyadic())
                return new CanonicalShortGame((RationalNumber) x).add((CanonicalShortGame) y);
        }
        else if (x instanceof CanonicalShortGame)
        {
            if (y instanceof CgsuiteInteger)
                return ((CanonicalShortGame) x).add(new CanonicalShortGame((CgsuiteInteger) y));
            else if (y instanceof RationalNumber && ((RationalNumber) y).isDyadic())
                return ((CanonicalShortGame) x).add(new CanonicalShortGame((RationalNumber) y));
            else if (y instanceof CanonicalShortGame)
                return ((CanonicalShortGame) x).add((CanonicalShortGame) y);
        }

        return x.invokeMethod("op +", y);
    }

    private CgsuiteObject subtract(CgsuiteObject x, CgsuiteObject y) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger)
        {
            if (y instanceof CgsuiteInteger)
                return ((CgsuiteInteger) x).subtract((CgsuiteInteger) y);
            else if (y instanceof RationalNumber)
                return ((RationalNumber) y).subtract(new RationalNumber((CgsuiteInteger) x));
            else if (y instanceof CanonicalShortGame)
                return ((CanonicalShortGame) y).subtract(new CanonicalShortGame((CgsuiteInteger) x));
        }
        else if (x instanceof RationalNumber)
        {
            if (y instanceof CgsuiteInteger)
                return ((RationalNumber) x).subtract(new RationalNumber((CgsuiteInteger) y));
            else if (y instanceof RationalNumber)
                return ((RationalNumber) x).subtract((RationalNumber) y);
            else if (y instanceof CanonicalShortGame && ((RationalNumber) x).isDyadic())
                return new CanonicalShortGame((RationalNumber) x).subtract((CanonicalShortGame) y);
        }
        else if (x instanceof CanonicalShortGame)
        {
            if (y instanceof CgsuiteInteger)
                return ((CanonicalShortGame) x).subtract(new CanonicalShortGame((CgsuiteInteger) y));
            else if (y instanceof RationalNumber && ((RationalNumber) y).isDyadic())
                return ((CanonicalShortGame) x).subtract(new CanonicalShortGame((RationalNumber) y));
            else if (y instanceof CanonicalShortGame)
                return ((CanonicalShortGame) x).subtract((CanonicalShortGame) y);
        }

        return x.invokeMethod("op -", y);
    }

    private CgsuiteObject multiply(CgsuiteObject x, CgsuiteObject y, CgsuiteTree tree) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger && y instanceof CgsuiteInteger)
        {
            return ((CgsuiteInteger) x).multiply((CgsuiteInteger) y);
        }

//        log.info(x.getClass() + " " + y.getClass() + tree.toStringTree());

        return x.invokeMethod("op *", y);
    }

    private boolean leq(CgsuiteObject x, CgsuiteObject y, CgsuiteTree tree) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger && y instanceof CgsuiteInteger)
        {
            return ((CgsuiteInteger) x).compareTo((CgsuiteInteger) y) <= 0;
        }

        return bool(x.invokeMethod("op <=", y), tree);
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
                throw new MalformedParseTreeException(tree);
        }

        if (starChild == null)
            m = 0;
        else if (starChild.getChildCount() == 0)
            m = 1;
        else
            m = naturalNumber(expression(starChild.getChild(0)), starChild);

        return new CanonicalShortGame(RationalNumber.ZERO, n, m);
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
            case COLON:

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
                    if (obj instanceof RationalNumber)
                        curNode.addLeftEdge(new CanonicalShortGame((RationalNumber) obj));
                    else if(obj instanceof CanonicalShortGame)
                        curNode.addLeftEdge((CanonicalShortGame) obj);
                    else if (obj instanceof LoopyGame)
                        curNode.addLeftEdge((LoopyGame) obj);
                    else
                        throw new InputException(tree.getChild(0).getChild(i).getToken(), "Expression not permitted in a loopy game constructor.");
                    break;
            }
        }

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
                    if (obj instanceof RationalNumber)
                        curNode.addRightEdge(new CanonicalShortGame((RationalNumber) obj));
                    else if (obj instanceof CanonicalShortGame)
                        curNode.addRightEdge((CanonicalShortGame) obj);
                    else if (obj instanceof LoopyGame)
                        curNode.addRightEdge((LoopyGame) obj);
                    else
                        throw new InputException(tree.getChild(1).getChild(i).getToken(), "Expression not permitted in a loopy game constructor.");
                    break;
            }
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
                    list.add(expression(child).simplify());
                return list;

            default:

                throw new MalformedParseTreeException(tree);
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
                        list.add(expression(child).simplify());
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
                return map;

            default:

                throw new MalformedParseTreeException(tree);
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
            x = x.simplify();
            if (!(x instanceof CgsuiteBoolean))
                throw new InputException(tree.token, "Expression is not a boolean.");
        }

        return (CgsuiteBoolean) x;
    }

    private int naturalNumber(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        int intValue = integer(x, tree);

        if (intValue < 0)
            throw new InputException(tree.token, "Argument to * is not a natural number.");

        return intValue;
    }

    private int integer(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (!(x instanceof CgsuiteInteger))
        {
            x = x.simplify();
            if (!(x instanceof CgsuiteInteger))
                throw new InputException(tree.token, "Argument to * is not an integer.");
        }

        return ((CgsuiteInteger) x).intValue();
    }

    private RationalNumber number(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (x instanceof CgsuiteInteger)
        {
            x = new RationalNumber((CgsuiteInteger) x);
        }
        else if (!(x instanceof RationalNumber))
        {
            x = x.simplify();
            if (!(x instanceof RationalNumber))
                throw new InputException(tree.token, "Argument to * is not a number.");
        }

        return (RationalNumber) x;
    }

    private CanonicalShortGame canonicalGame(CgsuiteObject x, CgsuiteTree tree) throws CgsuiteException
    {
        if (!(x instanceof CanonicalShortGame))
        {
            x = x.simplify();
            if (x instanceof CgsuiteInteger)
                x = new CanonicalShortGame(((CgsuiteInteger) x).intValue());
            else if(x instanceof RationalNumber)
                x = new CanonicalShortGame((RationalNumber) x);
            if (!(x instanceof CanonicalShortGame))
                throw new InputException(tree.token, "Not a canonical game.");
        }

        return (CanonicalShortGame) x;
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
