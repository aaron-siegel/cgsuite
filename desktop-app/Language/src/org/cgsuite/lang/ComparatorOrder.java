/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import java.util.HashMap;
import java.util.Map;
import org.cgsuite.lang.game.CanonicalShortGame;
import org.cgsuite.lang.game.RationalNumber;

/**
 *
 * @author asiegel
 */
public class ComparatorOrder
{
    private final static CgsuiteClass[] COMPARATOR_ORDER;
    private final static Map<CgsuiteClass,Integer> COMPARATOR_INDEX;
    
    static
    {
        COMPARATOR_ORDER = new CgsuiteClass[]
        {
            CgsuiteClass.NIL_TYPE,
            CgsuiteClass.TYPE,
            CgsuiteBoolean.TYPE,
            CgsuiteString.TYPE,
            CgsuiteInteger.ZERO_TYPE,
            CgsuiteInteger.TYPE,
            CanonicalShortGame.DYADIC_RATIONAL_TYPE,
            CanonicalShortGame.NIMBER_TYPE,
            CanonicalShortGame.TYPE,
            RationalNumber.TYPE,
            CgsuiteList.TYPE,
            CgsuiteSet.TYPE,
            CgsuiteMap.TYPE
        };
        
        COMPARATOR_INDEX = new HashMap<CgsuiteClass,Integer>();
        
        for (int i = 0; i < COMPARATOR_ORDER.length; i++)
        {
            COMPARATOR_INDEX.put(COMPARATOR_ORDER[i], i);
        }
    }
    
    public static int getIndex(CgsuiteClass type)
    {
        if (COMPARATOR_INDEX.containsKey(type))
            return COMPARATOR_INDEX.get(type);
        else
            return COMPARATOR_ORDER.length;
    }
    
    private ComparatorOrder()
    {
    }
}
