/*
 * OpCache.java
 *
 * Created on September 30, 2006, 8:56 AM
 * $Id: OpCache.java,v 1.4 2007/02/16 20:10:14 asiegel Exp $
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
 * A lightweight cache, especially useful for recursively-defined
 * operations. 
 *
 * An operations cache is a mapping from [byte, int, int] to int.
 * Typically the user will define a set of nonzero byte codes
 * representing operations, and will use ints to represent
 * objects.  The OpCache is used for cacheing results of
 * operations that result in new objects.
 *
 * The byte argument is a user-defined OPERATION code.  The byte value
 * 0 is reserved for no operation.  The user may use any other byte
 * values for different operations.  More than one operation can share
 * the same <code>OpCache</code>.
 *
 * For a general binary operation, use the <code>store</code> and
 * <code>lookup</code> methods.  If the binary operation is
 * commutative, use the <code>storeCommutative</code> and
 * <code>lookupCommutative</code> methods.  If the operation is unary,
 * use the <code>storeUnary</code> and <code>lookupUnary</code>
 * methods.  Do not use different kinds of methods for the same
 * operation.
 *
 * If a hash collision occurs, the old value is simply overwritten.
 * In that case, it might have to be recalculated the next time
 * through.  We hope not to have a lot of collisions between
 * frequently-computed values.
 *
 * The result value of -1 is reserved for an operation that has not
 * been cached.  (This could be made a member datum that can be
 * changed, if desired.)
 *
 * The <code>store...</code> methods return the integer result being
 * stored, enabling a coding paradigm of the form:
 * <pre> int ans=myOpCache.lookup(OP,arg1,arg2);
 * if (ans != -1) return ans;
 * // ... calculate result ...
 * return myCache.store(OP,arg1,arg2, result);
 * </pre>
 *
 * @author  Aaron Siegel
 * @author  Dan Hoey
 * @version $Revision: 1.4 $ $Date: 2007/02/16 20:10:14 $
 * @since   0.7
 */
final class OpCache
{
    ////////////////////////////////////////////////////////////////////////
    // Operations table.
    
    private final static int
        DEFAULT_OP_TABLE_SIZE = 1 << 18;
    
    private final static byte
        OPERATION_NONE = 0;

    private int
        opTableSize = DEFAULT_OP_TABLE_SIZE,
        opTableMask = opTableSize - 1;
    private byte[] opTableOp;
    private int[] opTableG, opTableH, opTableResult;
    
    private int storeGs=0, lookupGs=0, clobberGs=0;
    private int storeCs=0, lookupCs=0, clobberCs=0;
    private int storeUs=0, lookupUs=0, clobberUs=0;
    
    ////////////////////////////////////////////////////////////////////////
    // Constructors.

    /**
     * Construct an optable of the default size.
     */
    public OpCache()
    {
        opTableSize = DEFAULT_OP_TABLE_SIZE;
        opCacheInit();
    }
    
    /**
     * Constructs an OpCache with the given size, which must be a power of two.
     * @param size  Initial size of OpCache.
     * @throws IllegalArgumentException if <code>size</code> is not a power of two.
     */
    public OpCache(int size)
    {
        if ((size & (size-1)) != 0) {
            throw new IllegalArgumentException
                ("OpCache size must be a power of two, not " + size);
        }
        opTableSize = size;
        opCacheInit();
    }

    /**
     * Initializes this opTable
     */
    private void opCacheInit()
    {
        opTableMask = opTableSize - 1;

        opTableOp = null;
        opTableG = opTableH = opTableResult = null;
        //!! Maybe?        System.gc();
        opTableOp = new byte[opTableSize];
        Arrays.fill(opTableOp, OPERATION_NONE);

        opTableG = new int[opTableSize];
        opTableH = new int[opTableSize];
        opTableResult = new int[opTableSize];

    }

    /**
     * Stores a cached value for a general binary operation.
     *
     * @param operation The operation that is being defined.
     * @param gId       The first argument to the <code>operation</code>
     * @param hId       The second argument to the <code>operation</code>
     * @param result    The value of the <code>operation</code>
     *                  applied to <code>gId</code> and
     *                  <code>hId</code>.
     * @return   <code>result</code>
     */
    public int store(byte operation, int gId, int hId, int result)
    {
        int hc = (operation ^ gId ^ (hId * 69)) & opTableMask;
        storeGs+=1;
        if (opTableOp[hc] != OPERATION_NONE) clobberGs+=1;
        opTableOp[hc] = operation;
        opTableG[hc] = gId;
        opTableH[hc] = hId;
        opTableResult[hc] = result;
        return result;
    }

    /**
     * Retrieves a cached value for a general binary operation.
     *
     * @param operation The operation that is being retrieved.
     * @param gId       The first argument to the <code>operation</code>
     * @param hId       The second argument to the <code>operation</code>
     *
     * @return   <ul><li>The <code>result</code> that was used in a previous
     *           call to <code>store(operation,gId,hId,result)</code>,
     *           or</li><li>-1 if no such call has been made or the result has
     *           been clobbered by a hash collision.</li></ul>
     */
    public int lookup(byte operation, int gId, int hId)
    {
        int hc = (operation ^ gId ^ (hId * 69)) & opTableMask;
        lookupGs+=1;
        if (opTableOp[hc] == operation &&
            opTableG[hc] == gId && opTableH[hc] == hId)
        {
            return opTableResult[hc];
        }
        return -1;
    }
    
    /**
     * Stores a cached value for a commutative binary operation.
     *
     * @param operation The operation that is being defined.  This
     *                  value of <code>operation</code> may not be
     *                  used in the general or unary forms of these
     *                  methods.
     * @param gId       One argument to the <code>operation</code>
     * @param hId       The other argument to the <code>operation</code>
     * @param result    The value of the <code>operation</code>
     *                  applied to <code>gId</code> and
     *                  <code>hId</code>.
     * @return   <code>result</code>
     */
    public int storeCommutative(byte operation, int gId, int hId, int result)
    {
        int hc = ((operation*66587) ^
                  (((((gId>>18)+1)*37129) + (gId*8257) + (gId>>1))*
                   ((((hId>>18)+1)*37129) + (hId*8257) + (hId>>1)))
                  ) & opTableMask;
        storeCs+=1;
        if (opTableOp[hc] != OPERATION_NONE) {
        /*
             System.out.println("Clobber " +hc+","+ opTableOp[hc] +","+
                                opTableG[hc] +","+ opTableH[hc]);
        */
            clobberCs+=1;
        }
        opTableOp[hc] = operation;
        opTableG[hc] = gId;
        opTableH[hc] = hId;
        opTableResult[hc] = result;
        return result;
    }

    /**
     * Retrieves a cached value for a commutative binary operation.
     *
     * @param operation The operation that is being retrieved.
     * @param gId       One argument to the <code>operation</code>
     * @param hId       The other argument to the <code>operation</code>
     *
     * @return   <ul><li>The <code>result</code> that was used in a previous
     *           call to <code>storeCommutative(operation,gId,hId,result)</code>
     *           or <code>storeCommmutative(operation,hId,gId,result)</code>,
     *           or</li><li>-1 if no such call has been made or the result has
     *           been clobbered by a hash collision.</li></ul>
     */
    public int lookupCommutative(byte operation, int gId, int hId)
    {
        int hc = ((operation*66587) ^
                  (((((gId>>18)+1)*37129) + (gId*8257) + (gId>>1))*
                   ((((hId>>18)+1)*37129) + (hId*8257) + (hId>>1)))
                  ) & opTableMask;
        lookupCs+=1;
        if (opTableOp[hc] == operation &&
            (opTableG[hc] == gId && opTableH[hc] == hId ||
             opTableG[hc] == hId && opTableH[hc] == gId))
        {
            return opTableResult[hc];
        }
        return -1;
    }

    /**
     * Stores a cached value for a unary operation.
     *
     * @param operation The operation that is being defined.  This
     *                  value of <code>operation</code> may not be
     *                  used in the general or commutative forms of these
     *                  methods.
     * @param gId       The argument to the <code>operation</code>
     * @param result    The value of the <code>operation</code>
     *                  applied to <code>gId</code>.
     * @return   <code>result</code>
     */
    public int storeUnary(byte operation, int gId, int result)
    {
        int hc = (operation ^ (gId * 155)) & opTableMask;
        storeUs+=1;
        if (opTableOp[hc] != OPERATION_NONE) clobberUs+=1;
        opTableOp[hc] = operation;
        opTableG[hc] = gId;
        opTableResult[hc] = result;
        return result;
    }

    /**
     * Retrieves a cached value for a unary operation.
     *
     * @param operation The operation that is being retrieved.
     * @param gId       The argument to the <code>operation</code>
     *
     * @return   <ul><li>The <code>result</code> that was used in a previous
     *           call to <code>storeUnary(operation,gId,result)</code>,
     *           or</li><li>-1 if no such call has been made or the result has
     *           been clobbered by a hash collision.</li></ul>
     */
    public int lookupUnary(byte operation, int gId)
    {
        int hc = (operation ^ (gId * 155)) & opTableMask;
        lookupUs+=1;
        if (opTableOp[hc] == operation && opTableG[hc] == gId)
        {
            return opTableResult[hc];
        }
        else
        {
            return -1;
        }
    }

    /**
     * Retrieves statistics for this <code>OpCache</object>
     *
     * @return A string listing the number of stores, clobbers, and
     *         lookups for each kind of operation (general,
     *         commutative and unary).
     */
    public String getStats()
    {
        int used=0;
        for(int i : opTableOp)
        {
            if (i != OPERATION_NONE) used += 1;
        }
        return
            "Used " + (used*100 + 50)/opTableSize + "%=" +
            used + "/" + opTableSize +
            " Stores g" + storeGs + " c" + storeCs + " u" + storeUs +
            " Clobbers g" + clobberGs + " c" + clobberCs +
            " u" + clobberUs +
            " Lookups g" + lookupGs + " c" + lookupCs +
            " u" + lookupUs + "\n";
    }
}
