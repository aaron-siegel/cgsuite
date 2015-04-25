/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import org.cgsuite.lang.parser.CgsuiteTree;
import java.util.EnumSet;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class Variable extends CgsuiteObject
{
    private CgsuiteClass declaringClass;
    private String name;
    private EnumSet<Modifier> modifiers;
    private CgsuiteTree initializer;
    private int declRank;
    
    public Variable(CgsuiteClass declaringClass, String name, EnumSet<Modifier> modifiers, CgsuiteTree initializer, int declRank)
    {
        super(CgsuitePackage.forceLookupClass("Variable"));
        this.declaringClass = declaringClass;
        this.name = name;
        this.modifiers = modifiers;
        this.initializer = initializer;
        this.declRank = declRank;
    }

    @Override
    public Output toOutput()
    {
        return new StyledTextOutput("<" + Modifier.toString(modifiers) + "var " + name + ">");
    }
    
    public CgsuiteClass getDeclaringClass()
    {
        return declaringClass;
    }

    public boolean isStatic()
    {
        return modifiers.contains(Modifier.STATIC);
    }

    public boolean isEnumValue()
    {
        return modifiers.contains(Modifier.ENUM_VALUE);
    }

    public EnumSet<Modifier> getModifiers()
    {
        return modifiers;
    }

    public String getName()
    {
        return name;
    }

    public CgsuiteTree getInitializer()
    {
        return initializer;
    }

    public int getDeclRank()
    {
        return declRank;
    }

}
