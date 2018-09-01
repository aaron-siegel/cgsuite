/*
 * Phylum.java
 *
 * Created on December 17, 2005, 7:41 AM
 * $Id: Phylum.java,v 1.2 2007/04/09 23:51:51 asiegel Exp $
 */

/* ****************************************************************************

    Combinatorial Game Suite - A program to analyze combinatorial games
    Copyright (C) 2003-06  Aaron Siegel (asiegel@users.sourceforge.net)
    http://cgsuite.sourceforge.net/

    Combinatorial Game Suite is free software; you can redistribute it
    and/or modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2 of the
    License, or (at your option) any later version.

    Combinatorial Game Suite is distributed in the hope that it will be
    useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Combinatorial Game Suite; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA

**************************************************************************** */

package org.cgsuite.core.misere;

import java.util.*;

/**
 * Enum classifying the simplicity of the theory of a game and
 * associated constants.
 *
 * <p><code>NIMHEAP &lt; HEREDITARILY_TAME &lt; REVERSIBLY_TAME
 * &lt; TAMEABLE &lt; WILD_OF_TAME_GENUS</code>
 *
 * <p><code>HEREDITARILY_RESTIVE &lt; REVERSIBLY_RESTIVE &lt;
 * WILD_OF_RESTIVE_GENUS</code>
 *
 * <p><code>RESTLESS &lt; WILD_OF_RESTLESS_GENUS</code>
 *
 * <p><code>WILD.</code>
 *
 * @author Dan Hoey
 * @version $Revision: 1.2 $ $Date: 2007/04/09 23:51:51 $
 *
 */

enum Phylum
{
    /**
     * Games that are actual Nim heaps.
     */
    NIMHEAP,
    /**
     * Games that act like Nim heaps (with each other), all of whose options
     * are Nim heaps or hereditarily tame.
     */
    HEREDITARILY_TAME,
    /**
     * Games that act like Nim heaps with other Tame games via
     * tame-reversible options.
     */
    REVERSIBLY_TAME,
    /**
     * Games that determine genus when added to any set of Tame games.
     */
    TAMEABLE,
    /**
     * Game that have a tame genus, but are otherwise wild.
     */
    WILD_OF_TAME_GENUS,
    /**
     * Nontame games with all options hereditarily tame and a
     * GPLUS value of 0 or 1.
     */
    HEREDITARILY_RESTIVE,
    /**
     * Nontame games with all options tame and GPLUS value of 0 or
     * 1, which act like restive games via restive-reversible
     * options.
     */
    REVERSIBLY_RESTIVE,
    /**
     * Games of restive genus for which G+G is tame of genus
     * 0<sup>0</sup>.
     */
    WILD_OF_RESTIVE_GENUS,
    /* Nontame games with all options tame and a GMINUS value of 0
     * or 1.
     */
    RESTLESS,
    /**
     * Wild games of restless genus.
     */
    WILD_OF_RESTLESS_GENUS,
    /**
     * Games with GPLUS and GMINUS values different and greater
     * than 1.
     */
    WILD;

    /**
     * Phyla of games that are hereditarily tame.
     */
    public static final EnumSet<Phylum> HEREDITARILY_TAME_PHYLA = EnumSet.of
    (NIMHEAP, HEREDITARILY_TAME);

    /**
     * Phyla of games that are tame.
     */
    public static final EnumSet<Phylum> TAME_PHYLA = EnumSet.of
    (NIMHEAP, HEREDITARILY_TAME, REVERSIBLY_TAME);

    /**
     * Phyla of games that are tameable or tame.
     */
    public static final EnumSet<Phylum> TAMEABLE_PHYLA = EnumSet.of
    (NIMHEAP, HEREDITARILY_TAME, REVERSIBLY_TAME, TAMEABLE);

    /**
     * Phyla of games that are restive
     */
    public static final EnumSet<Phylum> RESTIVE_PHYLA = EnumSet.of
    (HEREDITARILY_RESTIVE, REVERSIBLY_RESTIVE);

    /**
     * Phyla of games that are restive or tame
     */
    public static final EnumSet<Phylum> RESTIVE_OR_TAME_PHYLA = EnumSet.of
    (NIMHEAP, HEREDITARILY_TAME, REVERSIBLY_TAME,
     HEREDITARILY_RESTIVE, REVERSIBLY_RESTIVE);

    /**
     * Phyla of games that are wild.
     */
    public static final EnumSet<Phylum> WILD_PHYLA = EnumSet.of
    (WILD_OF_TAME_GENUS, WILD_OF_RESTIVE_GENUS, WILD_OF_RESTLESS_GENUS, WILD);

    /**
     * Test whether a phylum is hereditarily tame.
     *
     * @return true if the phylum is hereditarily tame.
     */
    public boolean isHereditarilyTame()
    {
        return HEREDITARILY_TAME_PHYLA.contains(this);
    }

    /**
     * Test whether a phylum is tame.
     *
     * @return true if the phylum is tame.
     */
    public boolean isGenerallyTame()
    {
        return TAME_PHYLA.contains(this);
    }

    /**
     * Test whether a phylum is tameable or tame.
     *
     * @return true if the phylum is tameable or tame.
     */
    public boolean isTameable()
    {
        return TAMEABLE_PHYLA.contains(this);
    }

    public boolean isHereditarilyRestive()
    {
        return this == HEREDITARILY_RESTIVE;
    }

    /**
     * Test whether a phylum is restive.
     *
     * @return true if the phylum is restive.
     */
    public boolean isGenerallyRestive()
    {
        return RESTIVE_PHYLA.contains(this);
    }

    /**
     * Test whether a phylum is restive or tame.
     *
     * @return true if the phylum is restive or tame.
     */
    public boolean isRestiveOrTame()
    {
        return RESTIVE_OR_TAME_PHYLA.contains(this);
    }

    /**
     * Test whether a phylum is wild.
     *
     * @return true if the phylum is wild.
     */
    public boolean isWild()
    {
        return WILD_PHYLA.contains(this);
    }
}
