/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.game;

import java.util.EnumSet;

/**
 *
 * @author asiegel
 */
    /**
     * Specifies a compass direction for grid operations.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.10 $ $Date: 2007/02/16 20:10:14 $
     * @since   0.7
     */
    public enum Direction
    {
        NORTH       (-1, 0),
        NORTHEAST   (-1,-1),
        EAST        ( 0,-1),
        SOUTHEAST   ( 1,-1),
        SOUTH       ( 1, 0),
        SOUTHWEST   ( 1, 1),
        WEST        ( 0, 1),
        NORTHWEST   (-1, 1);

        /**
         * The set of all orthogonal directions (north, east, south, west).
         */
        public static final EnumSet<Direction> ORTHOGONALS = EnumSet.of(NORTH, EAST, SOUTH, WEST);
        /**
         * The set of all diagonal directions (northeast, southeast, southwest, northwest).
         */
        public static final EnumSet<Direction> DIAGONALS = EnumSet.of(NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST);
        /**
         * The set of all compass directions.
         */
        public static final EnumSet<Direction> ALL = EnumSet.allOf(Direction.class);

        final int rowShift;
        final int columnShift;

        private Direction(int rowShift, int columnShift)
        {
            this.rowShift = rowShift;
            this.columnShift = columnShift;
        }

        /**
         * Returns <code>true</code> if this is an orthogonal direction (north, east, south, west).
         *
         * @return  <code>true</code> if this is an orthogonal direction.
         */
        public boolean isOrthogonal()
        {
            return ORTHOGONALS.contains(this);
        }

        /**
         * Returns <code>true</code> if this is a diagonal direction (northeast, southeast, southwest, northwest).
         *
         * @return  <code>true</code> if this is a diagonal direction.
         */
        public boolean isDiagonal()
        {
            return DIAGONALS.contains(this);
        }

        /**
         * Calculates the number of rows traversed by moving <code>distance</code> spaces in this direction.
         * The shift is computed using Manhattan distance, not Euclidean distance.  For example,
         * <code>NORTHEAST.rowShift(3)</code> and <code>NORTHEAST.columnShift(3)</code> return
         * <code>-3</code> and <code>3</code>, respectively.
         *
         * @param   distance The distance of movement.
         * @return  The number of rows traversed.
         * @see     #columnShift(int) columnShift
         */
        public int rowShift(int distance)
        {
            return rowShift * distance;
        }

        /**
         * Calculates the number of columns traversed by moving <code>distance</code> spaces in this direction.
         * The shift is computed using Manhattan distance, not Euclidean distance.  For example,
         * <code>NORTHEAST.rowShift(3)</code> and <code>NORTHEAST.columnShift(3)</code> return
         * <code>-3</code> and <code>3</code>, respectively.
         *
         * @param   distance The distance of movement.
         * @return  The number of columns traversed.
         * @see     #rowShift(int) rowShift
         */
        public int columnShift(int distance)
        {
            return columnShift * distance;
        }
    }
