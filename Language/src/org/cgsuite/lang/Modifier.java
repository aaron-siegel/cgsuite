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
public enum Modifier
{
    PUBLIC,
    PROTECTED,
    PRIVATE,
    STATIC,
    IMMUTABLE,
    ENUM_VALUE;

    public static final EnumSet<Modifier> ACCESS_MODIFIERS = EnumSet.of(PUBLIC, PROTECTED, PRIVATE);

    public static String toString(EnumSet<Modifier> modifiers)
    {
        StringBuilder str = new StringBuilder();
        for (Modifier modifier : modifiers)
        {
            str.append(modifier.name().toLowerCase());
            str.append(' ');
        }
        return str.toString();
    }
}
