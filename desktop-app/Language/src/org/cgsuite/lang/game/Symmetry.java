/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.game;

import java.util.EnumSet;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuitePackage;

/**
 *
 * @author asiegel
 */
public enum Symmetry
{
    IDENTITY             (false, false, false),
    FLIP_HORIZONTAL      (true,  false, false),
    FLIP_VERTICAL        (false, true,  false),
    FLIP_BOTH            (true,  true,  false),
    ROTATE_CLOCKWISE     (false, true,  true),
    ROTATE_ANTICLOCKWISE (true,  false, true),
    TRANSPOSE            (false, false, true),
    ANTI_TRANSPOSE       (true,  true,  true);
    
    public static final CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Symmetry");
    
    public static final EnumSet<Symmetry> REFLECTION = EnumSet.of(IDENTITY, FLIP_HORIZONTAL, FLIP_VERTICAL, FLIP_BOTH);

    private boolean horizontal;
    private boolean vertical;
    private boolean rotational;

    private Symmetry(boolean horizontal, boolean vertical, boolean rotational)
    {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.rotational = rotational;
    }

    public boolean isHorizontal()
    {
        return horizontal;
    }

    public boolean isRotational()
    {
        return rotational;
    }

    public boolean isVertical()
    {
        return vertical;
    }
}
