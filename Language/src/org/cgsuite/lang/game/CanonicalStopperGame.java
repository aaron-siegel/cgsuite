/*
 * CanonicalStopperGame.java
 *
 * Created on April 24, 2003, 6:59 PM
 * $Id: CanonicalStopperGame.java,v 1.9 2006/05/19 18:02:57 asiegel Exp $
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

package org.cgsuite.lang.game;

import java.math.BigInteger;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.CgsuiteSet;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.InputException;


/**
 * A stopper in canonical form.  A loopy game is a <i>stopper</i> provided
 * there is no infinite alternating sequence of moves proceeding from any
 * subposition.
 * <p>
 * Just as with loopfree games, every stopper has a canonical form obtained
 * by eliminating all dominated options and bypassing all reversible ones.
 * Much of the theory regarding stoppers can be found in Chapter 11 of
 * Winning Ways.
 * <p>
 * There are two ways to construct a <code>CanonicalStopperGame</code>: Pass it
 * a {@link CanonicalGame}, or first construct a {@link LoopyGame} and then
 * call {@link LoopyGame#canonicalizeStopper() LoopyGame.canonicalizeStopper}.
 *
 * @author Aaron Siegel
 * @version $Revision: 1.9 $ $Date: 2006/05/19 18:02:57 $
 */
public final class CanonicalStopperGame extends LoopyGame
{
    public static final CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("CanonicalStopperGame");
    
    /**
     * A static reference to the game <code>ON = {ON|}</code>.
     */
    public static final CanonicalStopperGame ON;
    /**
     * A static reference to the game <code>OFF = {|OFF}</code>.
     */
    public static final CanonicalStopperGame OFF;
    /**
     * A static reference to the game <code>OVER = {0|OVER}</code>.
     */
    public static final CanonicalStopperGame OVER;
    /**
     * A static reference to the game <code>UNDER = {UNDER|0}</code>.
     */
    public static final CanonicalStopperGame UNDER;
    
    static
    {
        ON = new CanonicalStopperGame();
        ON.graph = new Bigraph(
            new int[][] { new int[] { 0 } },
            new int[][] { new int[0] }
            );
        ON.startVertex = 0;
        OFF = ON.negate();
        OVER = new CanonicalStopperGame();
        OVER.graph = new Bigraph(
            new int[][] { new int[] { 1 }, new int[0] },
            new int[][] { new int[] { 0 }, new int[0] }
            );
        OVER.startVertex = 0;
        UNDER = OVER.negate();
    }
    
    CanonicalStopperGame()
    {
        super(TYPE);
    }
    
    public CanonicalStopperGame(CgsuiteInteger n)
    {
        this(new CanonicalShortGame(n));
    }
    
    /**
     * Constructs a new <code>CanonicalStopperGame</code> that is equivalent
     * to the specified <code>CanonicalGame</code>.
     */
    public CanonicalStopperGame(CanonicalShortGame g)
    {
        this();
        graph = initialize(new LoopyGame.Node(g));
        startVertex = 0;
    }

    @Override
    public CgsuiteSet getLeftOptions()
    {
        CgsuiteSet leftOptions = new CgsuiteSet();
        for (int i = 0; i < graph.getNumLeftEdges(startVertex); i++)
        {
            CanonicalStopperGame lo = new CanonicalStopperGame();
            lo.graph = graph;
            lo.startVertex = graph.getLeftEdgeTarget(startVertex, i);
            leftOptions.add(lo);
        }
        return leftOptions;
    }
    
    @Override
    public CgsuiteSet getRightOptions()
    {
        CgsuiteSet rightOptions = new CgsuiteSet();
        for (int i = 0; i < graph.getNumRightEdges(startVertex); i++)
        {
            CanonicalStopperGame ro = new CanonicalStopperGame();
            ro.graph = graph;
            ro.startVertex = graph.getRightEdgeTarget(startVertex, i);
            rightOptions.add(ro);
        }
        return rightOptions;
    }
    
    public LoopyGame add(CanonicalStopperGame h)
    {
        return super.add(h);
    }
    
    public LoopyGame subtract(CanonicalStopperGame h)
    {
        return super.subtract(h);
    }
    
    public boolean leq(CanonicalStopperGame h)
    {
        return super.leq(h);
    }
    
    public Game ordinalSum(CanonicalStopperGame h)
    {
        return super.ordinalSum(h).simplify();
    }
    
    public Game asOnside(CanonicalStopperGame offside)
    {
        if (this.equals(offside))
            return this;
        else if (offside.leq(this))
            return new StopperSidedGame(this, offside);
        else
            throw new InputException("offside must be <= onside.");
    }
    
    @Override
    public CanonicalStopperGame negate()
    {
        CanonicalStopperGame inverse = new CanonicalStopperGame();
        inverse.graph = graph.getInverse();
        inverse.startVertex = startVertex;
        return inverse;
    }

    @Override
    public Game simplify()
    {
        if (graph.isCycleFree(startVertex))
        {
            return canonicalize(startVertex).simplify();
        }
        else
        {
            return this;
        }
    }
    
    public CgsuiteObject leftStop()
    {
        return stopConvert(leftStop(startVertex));
    }
    
    private RationalNumber leftStop(int vertex)
    {
        if (graph.isCycleFree(vertex))
        {
            return canonicalize(vertex).leftStop();
        }
        else if (isOn(vertex))
        {
            return RationalNumber.POSITIVE_INFINITY;
        }
        else if (isOff(vertex))
        {
            return RationalNumber.NEGATIVE_INFINITY;
        }
        else
        {
            RationalNumber stop = RationalNumber.NEGATIVE_INFINITY;
            for (int i = 0; i < graph.getNumLeftEdges(vertex); i++)
            {
                stop = stop.max(rightStop(graph.getLeftEdgeTarget(vertex, i)));
            }
            return stop;
        }
    }
    
    public CgsuiteObject rightStop()
    {
        return stopConvert(rightStop(startVertex));
    }
    
    private RationalNumber rightStop(int vertex)
    {
        if (graph.isCycleFree(vertex))
        {
            return canonicalize(vertex).rightStop();
        }
        else if (isOn(vertex))
        {
            return RationalNumber.POSITIVE_INFINITY;
        }
        else if (isOff(vertex))
        {
            return RationalNumber.NEGATIVE_INFINITY;
        }
        else
        {
            RationalNumber stop = RationalNumber.POSITIVE_INFINITY;
            for (int i = 0; i < graph.getNumRightEdges(vertex); i++)
            {
                stop = stop.min(leftStop(graph.getRightEdgeTarget(vertex, i)));
            }
            return stop;
        }
    }
    
    private CgsuiteObject stopConvert(RationalNumber x)
    {
        if (x.equals(RationalNumber.POSITIVE_INFINITY))
            return ON;
        else if (x.equals(RationalNumber.NEGATIVE_INFINITY))
            return OFF;
        else
            return x;
    }
    
    public CanonicalStopperGame solve()
    {
        return this;
    }
    
    public BigInteger stopCount()
    {
        return stopCount(new BigInteger[graph.getNumVertices()], startVertex);
    }
    
    private BigInteger stopCount(BigInteger[] counts, int vertex)
    {
        if (counts[vertex] != null)
        {
            return counts[vertex];
        }
        if (graph.isCycleFree(vertex))
        {
            counts[vertex] = canonicalize(vertex).stopCount();
            return counts[vertex];
        }
        if (isOn(vertex) || isOff(vertex))
        {
            counts[vertex] = BigInteger.ONE;
            return counts[vertex];
        }
        
        BigInteger count = BigInteger.ZERO;
        counts[vertex] = BigInteger.ZERO;
        
        for (int i = 0; i < graph.getNumLeftEdges(vertex); i++)
        {
            count = count.add(stopCount(counts, graph.getLeftEdgeTarget(vertex, i)));
        }
        for (int i = 0; i < graph.getNumRightEdges(vertex); i++)
        {
            count = count.add(stopCount(counts, graph.getRightEdgeTarget(vertex, i)));
        }
        
        counts[vertex] = count;
        return count;
    }
    
    /**
     * Calculates the upsum of this game and <code>h</code>.
     * The upsum of <code>G</code> and <code>H</code> is equal to the
     * onside of <code>G + H</code>.
     * <p>
     * This method is equivalent to <code>plus(h).getOnside()</code>.
     *
     * @param   h The game to add to this game.
     * @return  The upsum of this game and <code>h</code>.
     * @throws  NotStopperException The sum of this game and <code>h</code>
     *          does not have an onside which is a stopper.
     * @see     #downsum(CanonicalStopperGame) downsum
     */
    public CanonicalStopperGame upsum(CanonicalStopperGame h) throws NotStopperException
    {
        return add(h).onside();
    }
    
    /**
     * Calculates the downsum of this game and <code>h</code>.
     * The downsum of <code>G</code> and <code>H</code> is equal to the
     * offside of <code>G + H</code>.
     * <p>
     * This method is equivalent to <code>plus(h).getOffside()</code>.
     *
     * @param   h The game to add to this game.
     * @return  The downsum of this game and <code>h</code>.
     * @throws  NotStopperException The sum of this game and <code>h</code>
     *          does not have an offside which is a stopper.
     * @see     #upsum(CanonicalStopperGame) upsum
     */
    public CanonicalStopperGame downsum(CanonicalStopperGame h) throws NotStopperException
    {
        return add(h).offside();
    }
    
    /**
     * Calculates the degree of loopiness of this game.
     * The degree of <code>G</code> is equal to the upsum of <code>G</code>
     * and <code>-G</code>.
     *
     * @return  The degree of this game.
     * @throws  NotStopperException The degree of this game is not a stopper.
     * @see     #upsum(CanonicalStopperGame) upsum
     */
    public CanonicalStopperGame degree() throws NotStopperException
    {
        if (graph.isCycleFree(startVertex))
        {
            return new CanonicalStopperGame(CanonicalShortGame.ZERO);
        }
        else
        {
            return upsum(negate());
        }
    }
}
