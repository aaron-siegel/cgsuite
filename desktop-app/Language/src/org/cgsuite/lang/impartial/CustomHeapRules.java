/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.impartial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.cgsuite.lang.CgsuiteCollection;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteMethod;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuiteProcedure;
import org.cgsuite.lang.InputException;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class CustomHeapRules extends HeapRules
{
    private CgsuiteProcedure procedure;

    public CustomHeapRules(CgsuiteProcedure procedure)
    {
        this.procedure = procedure;
        
        if (procedure.getNumParameters() != 1)
        {
            throw new InputException("CustomRules requires a one-argument procedure.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomHeapRules other = (CustomHeapRules) obj;
        if (this.procedure != other.procedure && (this.procedure == null || !this.procedure.equals(other.procedure))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.procedure != null ? this.procedure.hashCode() : 0);
        return hash;
    }

    @Override
    public StyledTextOutput toOutput()
    {
        return new StyledTextOutput("<Custom HeapRules>");
    }

    @Override
    public Collection<int[]> allOptions(int heapSize)
    {
        List<int[]> options = new ArrayList<int[]>();
        Object optionsSpec = procedure.invoke(Collections.singletonList(new CgsuiteInteger(heapSize)), CgsuiteMethod.EMPTY_PARAM_MAP);

        if (!(optionsSpec instanceof CgsuiteCollection))
        {
            throw new InputException(procedure.getToken(), "HeapRules procedure did not return a collection of integer collections.");
        }
        
        for (CgsuiteObject optionSpec : (CgsuiteCollection) optionsSpec)
        {
            if (!(optionSpec instanceof CgsuiteCollection))
            {
                throw new InputException(procedure.getToken(), "HeapRules procedure did not return a collection of integer collections.");
            }
            CgsuiteCollection optionSpecAsColl = (CgsuiteCollection) optionSpec;
            int[] option = new int[optionSpecAsColl.size()];
            int i = 0;
            for (CgsuiteObject value : optionSpecAsColl)
            {
                if (!(value instanceof CgsuiteInteger))
                {
                    throw new InputException(procedure.getToken(), "HeapRules procedure did not return a collection of integer collections.");
                }
                option[i] = ((CgsuiteInteger) value).intValue();
                if (option[i] < 0)
                {
                    throw new InputException(procedure.getToken(), "HeapRules procedure did not return a collection of integer collections.");
                }
            }
            options.add(option);
        }
        return options;
    }
}
