/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

/**
 *
 * @author asiegel
 */
public class CgsuiteEnumValue extends CgsuiteObject
{
    private String literal;
    private int ordinal;

    public CgsuiteEnumValue(CgsuiteClass type, String literal, int ordinal)
    {
        super(type);

        this.literal = literal;
        this.ordinal = ordinal;
    }

    public String getLiteral()
    {
        return literal;
    }

    public int getOrdinal()
    {
        return ordinal;
    }

    @Override
    public String toString()
    {
        return literal;
    }

}
