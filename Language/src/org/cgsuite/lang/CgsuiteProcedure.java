/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import org.cgsuite.lang.parser.CgsuiteTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public CgsuiteObject invoke(List<CgsuiteObject> arguments, Map<String, CgsuiteObject> optionalArguments) throws CgsuiteException
    {
        // TODO Validate number of arguments etc.

        List<CgsuiteObject> oldValues = new ArrayList<CgsuiteObject>(parameters.size());

        for (int i = 0; i < parameters.size(); i++)
        {
            oldValues.add(domain.lookup(parameters.get(i)));
            domain.put(parameters.get(i), arguments.get(i));
        }

        CgsuiteObject value = domain.expression(tree);
        
        for (int i = 0; i < parameters.size(); i++)
        {
            // TODO If oldValues.get(i) == null, remove from domain
            if (oldValues.get(i) != null)
                domain.put(parameters.get(i), oldValues.get(i));
        }

        return value;
    }
}
