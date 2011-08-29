/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.Token;
import org.cgsuite.lang.parser.CgsuiteTree;

/**
 *
 * @author asiegel
 */
public class CgsuiteProcedure extends CgsuiteObject implements Callable
{
    private List<String> parameters;
    private CgsuiteTree tree;
    private Domain domain;

    public CgsuiteProcedure(List<String> parameters, CgsuiteTree tree, Domain domain)
    {
        super(CgsuitePackage.forceLookupClass("Procedure"));
        this.parameters = parameters;
        this.tree = tree;
        this.domain = domain;
    }
    
    public int getNumParameters()
    {
        return parameters.size();
    }
    
    public Token getToken()
    {
        return tree.getToken();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!super.equals(obj))
            return false;
        
        final CgsuiteProcedure other = (CgsuiteProcedure) obj;
        if (this.parameters != other.parameters && (this.parameters == null || !this.parameters.equals(other.parameters))) {
            return false;
        }
        if (this.tree != other.tree && (this.tree == null || !this.tree.equals(other.tree))) {
            return false;
        }
        if (this.domain != other.domain && (this.domain == null || !this.domain.equals(other.domain))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + super.hashCode();
        hash = 53 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
        hash = 53 * hash + (this.tree != null ? this.tree.hashCode() : 0);
        hash = 53 * hash + (this.domain != null ? this.domain.hashCode() : 0);
        return hash;
    }
    
    public CgsuiteObject invoke(CgsuiteObject ... arguments) throws CgsuiteException
    {
        return invoke(Arrays.asList(arguments), CgsuiteMethod.EMPTY_PARAM_MAP);
    }
    
    @Override
    public CgsuiteObject invoke(List<? extends CgsuiteObject> arguments, Map<String, CgsuiteObject> optionalArguments) throws CgsuiteException
    {
        if (arguments.size() != parameters.size())
        {
            throw new InputException("Expecting " + parameters.size() + " argument(s); found " + arguments.size());
        }
        
        if (!optionalArguments.isEmpty())
        {
            throw new InputException("Invalid optional parameter: " + optionalArguments.keySet().iterator().next());
        }

        List<CgsuiteObject> oldValues = new ArrayList<CgsuiteObject>(parameters.size());

        for (int i = 0; i < parameters.size(); i++)
        {
            oldValues.add(domain.lookup(parameters.get(i)));
            domain.put(parameters.get(i), arguments.get(i));
        }

        try
        {
            return domain.procedureInvocation(tree.getChild(1));
        }
        finally
        {
            for (int i = 0; i < parameters.size(); i++)
            {
                if (oldValues.get(i) == null)
                    domain.remove(parameters.get(i));
                else
                    domain.put(parameters.get(i), oldValues.get(i));
            }
        }
    }
}
