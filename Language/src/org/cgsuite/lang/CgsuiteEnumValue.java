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
    public CgsuiteEnumValue(CgsuiteClass type)
    {
        super(type);
    }

    @Override
    public String toString()
    {
        return ((CgsuiteString) resolve("Literal")).toJavaString();
    }
    
    public int getOrdinal()
    {
        return ((CgsuiteInteger) resolve("Ordinal")).intValue();
    }

    @Override
    protected int compareLike(CgsuiteObject other)
    {
        return getOrdinal() - ((CgsuiteEnumValue) other).getOrdinal();
    }

}
