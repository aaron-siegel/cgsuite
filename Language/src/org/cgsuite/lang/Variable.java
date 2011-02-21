/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.util.EnumSet;

/**
 *
 * @author asiegel
 */
public class Variable
{
    private CgsuiteClass declaringClass;
    private String name;
    private EnumSet<Modifier> modifiers;
    
    public Variable(CgsuiteClass declaringClass, String name, EnumSet<Modifier> modifiers)
    {
        this.declaringClass = declaringClass;
        this.name = name;
        this.modifiers = modifiers;
    }

    public EnumSet<Modifier> getModifiers()
    {
        return modifiers;
    }

    public String getName()
    {
        return name;
    }

}
