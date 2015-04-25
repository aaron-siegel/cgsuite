/*
 * Digraph.java
 *
 * Created on April 11, 2003, 12:19 AM
 * $Id: Digraph.java,v 1.6 2005/11/10 00:14:40 asiegel Exp $
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

import java.util.Arrays;

/**
 * A directed graph with separate left and right edge sets.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.6 $ $Date: 2005/11/10 00:14:40 $
 * @since   0.6
 */
public class Bigraph
{
    private static final int
        FLAG_CYCLE_FREE = 0x0001,
        FLAG_NOT_CYCLE_FREE = 0x0002,
        FLAG_RES_CYCLE_FREE = 0x0003,
        FLAG_LEFT_ACF = 0x0004,
        FLAG_NOT_LEFT_ACF = 0x0008,
        FLAG_RES_LEFT_ACF = 0x000c,
        FLAG_RIGHT_ACF = 0x0010,
        FLAG_NOT_RIGHT_ACF = 0x0020,
        FLAG_RES_RIGHT_ACF = 0x0030;
    
    private static final int
        MARKER_1 = 0x0100,
        MARKER_2 = 0x0200,
        MARKERS = 0x0300,
        MARKER_REACHABLE = 0x0400;

    private int numVertices;
    private int leftEdges[][];
    private int rightEdges[][];
    private int flags[];
    private int leftPreds[][];
    private int rightPreds[][];
    private transient int reachableFrom;
    
    /**
     * Constructs a new <code>Digraph</code> with the specified structure.
     * The resulting graph will have <code>leftEdges.length</code> vertices,
     * numbered <code>0</code> through <code>leftEdges.length-1</code>.  The
     * array <code>leftEdges[i]</code> should enumerate the target vertices of
     * all left edges originating at vertex <code>i</code>.  That is, if
     * <code>leftEdges[i][j] == k</code>, for any <code>j</code>, then there is
     * assumed to be a directed left edge from vertex <code>i</code> to vertex
     * <code>k</code>.
     *
     * @param   leftEdges An enumeration of all the left edges in this graph.
     * @param   rightEdges An enumeration of all the right edges in this graph.
     * @throws  IllegalArgumentException If
     *          <code>leftEdges.length != rightEdges.length</code>.
     * @throws  IllegalArgumentException If some <code>leftEdges[i][j]</code>
     *          or <code>rightEdges[i][j]</code> is not between
     *          <code>0</code> and <code>leftEdges.length-1</code>.
     */
    public Bigraph(int[][] leftEdges, int[][] rightEdges)
    {
        numVertices = leftEdges.length;
        if (rightEdges.length != numVertices)
        {
            throw new IllegalArgumentException("leftEdges.length != rightEdges.length");
        }
        this.leftEdges = new int[numVertices][];
        this.rightEdges = new int[numVertices][];
        for (int i = 0; i < numVertices; i++)
        {
            int numLeftEdges = 0, numRightEdges = 0;
            for (int j = 0; j < leftEdges[i].length; j++)
            {
                int value = leftEdges[i][j];
                if (value >= 0 && value < numVertices)
                {
                    numLeftEdges++;
                }
                else if (value != -1)
                {
                    throw new IllegalArgumentException("leftEdges[" + i + "][" + j + "] is not a valid vertex number.");
                }
            }
            this.leftEdges[i] = new int[numLeftEdges];
            for (int j = 0, k = 0; j < leftEdges[i].length; j++)
            {
                if (leftEdges[i][j] != -1)
                {
                    this.leftEdges[i][k] = leftEdges[i][j];
                    k++;
                }
            }
            for (int j = 0; j < rightEdges[i].length; j++)
            {
                int value = rightEdges[i][j];
                if (value >= 0 && value < numVertices)
                {
                    numRightEdges++;
                }
                else if (value != -1)
                {
                    throw new IllegalArgumentException("rightEdges[" + i + "][" + j + "] is not a valid vertex number.");
                }
            }
            this.rightEdges[i] = new int[numRightEdges];
            for (int j = 0, k = 0; j < rightEdges[i].length; j++)
            {
                if (rightEdges[i][j] != -1)
                {
                    this.rightEdges[i][k] = rightEdges[i][j];
                    k++;
                }
            }
        }
        flags = new int[numVertices];
        markVertices();
        reachableFrom = -1;
    }
    
    /**
     * Constructs a new <code>Digraph</code> by packing the specified structure
     * according to <code>masterVertex</code>.  See {@link #pack(int) pack} for
     * details on what this means.  It is permissible for entries in the edge
     * arrays to have value <code>-1</code>; this indicates a vacated index
     * (one that no longer refers to an edge).
     *
     * @param   leftEdges An enumeration of all the left edges in this graph.
     * @param   rightEdges An enumeration of all the right edges in this graph.
     * @throws  IllegalArgumentException If
     *          <code>leftEdges.length != rightEdges.length</code>.
     * @throws  IllegalArgumentException If some <code>leftEdges[i][j]</code>
     *          or <code>rightEdges[i][j]</code> is not between
     *          <code>-1</code> and <code>leftEdges.length-1</code>.
     * @see     #pack(int) pack
     */
    public Bigraph(int[][] leftEdges, int[][] rightEdges, int masterVertex)
    {
        if (leftEdges.length != rightEdges.length)
        {
            throw new IllegalArgumentException("leftEdges.length != rightEdges.length");
        }
        int[][][] edges = pack(leftEdges, rightEdges, masterVertex);
        this.leftEdges = edges[0];
        this.rightEdges = edges[1];
        numVertices = this.leftEdges.length;
        flags = new int[numVertices];
        markVertices();
        reachableFrom = -1;
    }
    
    private Bigraph(int numVertices)
    {
        this.numVertices = numVertices;
        leftEdges = new int[numVertices][];
        rightEdges = new int[numVertices][];
        flags = new int[numVertices];
        reachableFrom = -1;
    }
    
    private Bigraph()
    {
        reachableFrom = -1;
    }
    
    public int[][][] pack(int masterVertex)
    {
        return pack(leftEdges, rightEdges, masterVertex);
    }
    
    public Bigraph packGraph(int masterVertex)
    {
        int[][][] edges = pack(masterVertex);
        Bigraph newGraph = new Bigraph();
        newGraph.numVertices = edges[0].length;
        newGraph.leftEdges = edges[0];
        newGraph.rightEdges = edges[1];
        newGraph.flags = new int[newGraph.numVertices];
        newGraph.markVertices();
        return newGraph;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Bigraph))
        {
            return false;
        }
        Bigraph other = (Bigraph) obj;
        if (numVertices != other.numVertices)
        {
            return false;
        }
        for (int i = 0; i < numVertices; i++)
        {
            if (leftEdges[i].length != other.leftEdges[i].length ||
                rightEdges[i].length != other.rightEdges[i].length)
            {
                return false;
            }
            for (int j = 0; j < leftEdges[i].length; j++)
            {
                if (leftEdges[i][j] != other.leftEdges[i][j])
                {
                    return false;
                }
            }
            for (int j = 0; j < rightEdges[i].length; j++)
            {
                if (rightEdges[i][j] != other.rightEdges[i][j])
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int hashCode()
    {
        int code = numVertices;
        for (int i = 0; i < numVertices; i++)
        {
            for (int j = 0; j < leftEdges[i].length; j++)
            {
                code = 31 * code + leftEdges[i][j];
            }
            code *= 7;
            for (int j = 0; j < rightEdges[i].length; j++)
            {
                code = 31 * code + rightEdges[i][j];
            }
        }
        return code;
    }

    @Override
    public String toString()
    {
        String s = "";
        for (int vertex = 0; vertex < numVertices; vertex++)
        {
            s += String.valueOf(vertex) + ": ";
            for (int i = 0; i < leftEdges[vertex].length; i++)

            {
                s += String.valueOf(leftEdges[vertex][i]) + " ";
            }
            s += "| ";
            for (int i = 0; i < rightEdges[vertex].length; i++)
            {
                s += String.valueOf(rightEdges[vertex][i]) + " ";
            }
            s += "\n";
        }
        return s;
    }

    /**
     * Gets the number of vertices in this graph.
     *
     * @return  The number of vertices in this graph.
     */
    public int getNumVertices()
    {
        return numVertices;
    }
    
    /**
     * Gets the number of left edges directed from the specified vertex.
     *
     * @param   vertex The vertex to examine.
     * @return  The number of left edges directed from <code>vertex</code>.
     * @throws  ArrayIndexOutOfBoundsException If the specified vertex
     *          is not between <code>0</code> and
     *          <code>getNumVertices()-1</code>.
     */
    public int getNumLeftEdges(int vertex)
    {
        return leftEdges[vertex].length;
    }
    
    /**
     * Gets the number of right edges directed from the specified vertex.
     *
     * @param   vertex The vertex to examine.
     * @return  The number of right edges directed from <code>vertex</code>.
     * @throws  ArrayIndexOutOfBoundsException If the specified vertex
     *          is not between <code>0</code> and
     *          <code>getNumVertices()-1</code>.
     */
    public int getNumRightEdges(int vertex)
    {
        return rightEdges[vertex].length;
    }
    
    /**
     * Gets the target vertex of the <code>i<sup>th</sup></code> left edge
     * directed from the specified vertex.
     *
     * @param   vertex The vertex to examine.
     * @param   i The index of the edge to examine.
     * @return  The target vertex.
     * @throws  ArrayIndexOutOfBoundsException If the specified vertex
     *          is not between <code>0</code> and
     *          <code>getNumVertices()-1</code>, or the specified index is not
     *          between <code>0</code> and
     *          <code>getNumLeftEdges(vertex)-1</code>.
     */
    public int getLeftEdgeTarget(int vertex, int i)
    {
        return leftEdges[vertex][i];
    }
    
    /**
     * Gets the target vertex of the <code>i<sup>th</sup></code> right edge
     * directed from the specified vertex.
     *
     * @param   vertex The vertex to examine.
     * @param   i The index of the edge to examine.
     * @return  The target vertex.
     * @throws  ArrayIndexOutOfBoundsException If the specified vertex
     *          is not between <code>0</code> and
     *          <code>getNumVertices()-1</code>, or the specified index is not
     *          between <code>0</code> and
     *          <code>getNumRightEdges(vertex)-1</code>.
     */
    public int getRightEdgeTarget(int vertex, int i)
    {
        return rightEdges[vertex][i];
    }
    
    /**
     * Returns <code>true</code> if there is a left edge directed from the
     * specified vertex to the specified target.
     *
     * @param   vertex The starting vertex.
     * @param   target The target vertex.
     * @return  <code>true</code> if there is a left edge from
     *          <code>vertex</code> to <code>target</code>.
     */
    public boolean containsLeftEdge(int vertex, int target)
    {
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if (leftEdges[vertex][i] == target)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns <code>true</code> if there is a right edge directed from the
     * specified vertex to the specified target.
     *
     * @param   vertex The starting vertex.
     * @param   target The target vertex.
     * @return  <code>true</code> if there is a right edge from
     *          <code>vertex</code> to <code>target</code>.
     */
    public boolean containsRightEdge(int vertex, int target)
    {
        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if (rightEdges[vertex][i] == target)
            {
                return true;
            }
        }
        return false;
    }
    
    public int[][] cloneAllLeftEdges()
    {
        int[][] allLeftEdges = new int[numVertices][];
        for (int i = 0; i < numVertices; i++)
        {
            allLeftEdges[i] = leftEdges[i].clone();
        }
        return allLeftEdges;
    }
    
    public int[][] cloneAllRightEdges()
    {
        int[][] allRightEdges = new int[numVertices][];
        for (int i = 0; i < numVertices; i++)
        {
            allRightEdges[i] = rightEdges[i].clone();
        }
        return allRightEdges;
    }

    /**
     * Gets the inverse of this graph (obtained by swapping the left and right
     * edge sets).
     *
     * @return The inverse of this graph.
     */
    public Bigraph getInverse()
    {
        Bigraph inverse = new Bigraph();
        // The following is safe since the structure is immutable.
        inverse.numVertices = numVertices;
        inverse.leftEdges = rightEdges;
        inverse.rightEdges = leftEdges;
        inverse.flags = new int[numVertices];
        inverse.markVertices();
        return inverse;
    }
    
    /**
     * Calculates the direct sum of this graph and <code>h</code>.  If this
     * graph has <code>M</code> vertices and <code>h</code> has <code>N</code>
     * vertices, then the direct sum has <code>M * N</code> vertices.  The
     * direct sum has a left edge from <code>(i * N) + j</code> to
     * <code>(i' * N) + j</code> precisely when this graph has a left edge
     * from <code>i</code> to <code>i'</code>, and to <code>(i * N) + j'</code>
     * precisely when <code>h</code> has a left edge from <code>j</code> to
     * <code>j'</code>.
     * <p>
     * If this graph and <code>h</code> represent the game graphs of two
     * combinatorial games, then their direct sum is the game graph of the
     * sum of the two games.
     *
     * @param   h The <code>Digraph</code> to add to this one.
     * @return  The direct sum of this graph and <code>h</code>.
     */
    public Bigraph directSum(Bigraph h)
    {
        int numH = h.numVertices;
        Bigraph sumGraph = new Bigraph(numVertices * numH);

        for (int i = 0; i < numVertices; i++)
        {
            for (int j = 0; j < numH; j++)
            {
                int thisIndex = i * numH + j;
                sumGraph.leftEdges[thisIndex] =
                    new int[leftEdges[i].length + h.leftEdges[j].length];
                sumGraph.rightEdges[thisIndex] =
                    new int[rightEdges[i].length + h.rightEdges[j].length];
                for (int k = 0; k < leftEdges[i].length; k++)
                {
                    sumGraph.leftEdges[thisIndex][k] =
                        (leftEdges[i][k] == -1 ? -1 : leftEdges[i][k] * numH + j);
                }
                for (int k = 0; k < h.leftEdges[j].length; k++)
                {
                    sumGraph.leftEdges[thisIndex][leftEdges[i].length+k] =
                        (h.leftEdges[j][k] == -1 ? -1 : i * numH + h.leftEdges[j][k]);
                }
                for (int k = 0; k < rightEdges[i].length; k++)
                {
                    sumGraph.rightEdges[thisIndex][k] =
                        (rightEdges[i][k] == -1 ? -1 : rightEdges[i][k] * numH + j);
                }
                for (int k = 0; k < h.rightEdges[j].length; k++)
                {
                    sumGraph.rightEdges[thisIndex][rightEdges[i].length+k] =
                        (h.rightEdges[j][k] == -1 ? -1 : i * numH + h.rightEdges[j][k]);
                }
                Arrays.sort(sumGraph.leftEdges[thisIndex]);
                Arrays.sort(sumGraph.rightEdges[thisIndex]);
            }
        }
        sumGraph.markVertices();
        return sumGraph;
    }
    
    public Bigraph ordinalSum(Bigraph h, int junctionVertex)
    {
        int numH = h.numVertices;
        Bigraph sumGraph = new Bigraph(numVertices + numH);
        
        for (int i = 0; i < numVertices; i++)
        {
            sumGraph.leftEdges[i] = Arrays.copyOf(leftEdges[i], leftEdges[i].length);
            sumGraph.rightEdges[i] = Arrays.copyOf(rightEdges[i], rightEdges[i].length);
        }
        
        int jlenL = leftEdges[junctionVertex].length;
        int jlenR = rightEdges[junctionVertex].length;
        
        for (int i = 0; i < numH; i++)
        {
            sumGraph.leftEdges[numVertices+i] = new int[jlenL + h.leftEdges[i].length];
            for (int k = 0; k < jlenL; k++)
            {
                sumGraph.leftEdges[numVertices+i][k] = leftEdges[junctionVertex][k];
            }
            for (int k = 0; k < h.leftEdges[i].length; k++)
            {
                sumGraph.leftEdges[numVertices+i][jlenL+k] = h.leftEdges[i][k] + numVertices;
            }
            
            sumGraph.rightEdges[numVertices+i] = new int[jlenR + h.rightEdges[i].length];
            for (int k = 0; k < jlenR; k++)
            {
                sumGraph.rightEdges[numVertices+i][k] = rightEdges[junctionVertex][k];
            }
            for (int k = 0; k < h.rightEdges[i].length; k++)
            {
                sumGraph.rightEdges[numVertices+i][jlenR+k] = h.rightEdges[i][k] + numVertices;
            }
        }
        
        return sumGraph;
    }
    
    private final static int
        MINISIG_BASE = -962764907,
        LEFT_ONE_CYCLE = -742481099,
        RIGHT_ONE_CYCLE = -121939996,
        LOPT_MULTIPLIER = -1692903693,
        ROPT_MULTIPLIER = 1419677861,
        EOPT_MULTIPLIER = 1161334719,
        ONE_CYCLE_FULLSIG = 1567540980;
    
    /**
     * Calculates a hash code for this graph that is invariant under
     * isomorphism.
     * <p>
     * If <code>startVertex == -1</code>, then the hash code is computed for
     * the full graph.  If <code>startVertex != -1</code>, then the hash code
     * is computed for the structure <code>(G,V)</code>, where <code>V</code>
     * is <code>startVertex</code> and <code>G</code> is the subgraph induced
     * by the set of vertices reachable from <code>V</code>.
     *
     * @param   startVertex The vertex used to identify the subgraph to
     *          analyze, or <code>-1</code> to analyze the full graph.
     * @return  A hash code for the specified subgraph that is invariant under
     *          isomorphism.
     */
    public int hashCodeIsomorphismInvariant(int startVertex)
    {
        // We compute the hash code as follows.  Each vertex has an associated
        // "signature" that depends only on isomorphism-invariant properties
        // of that vertex.  The hash code is computed from the signatures of
        // all vertices in the graph.  If a startVertex is specified, we
        // identify it.
        
        int hc = 0;
        for (int vertex = 0; vertex < leftEdges.length; vertex++)
        {
            if (vertex == startVertex)
            {
                hc ^= (vertexSignature(vertex) << 1);
            }
            else if (startVertex == -1 || isReachable(startVertex, vertex))
            {
                hc ^= vertexSignature(vertex);
            }
        }
        return hc;
    }
    
    private int vertexSignature(int vertex)
    {
        int lsig = 0, rsig = 0;
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if (leftEdges[vertex][i] == vertex)
            {
                lsig += ONE_CYCLE_FULLSIG;
            }
            else
            {
                lsig += vertexMinisig(leftEdges[vertex][i]);
            }
        }
        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if (rightEdges[vertex][i] == vertex)
            {
                rsig += ONE_CYCLE_FULLSIG;
            }
            else
            {
                rsig += vertexMinisig(rightEdges[vertex][i]);
            }
        }
        return (lsig << 2) + (rsig << 1) + vertexMinisig(vertex);
    }
    
    private int vertexMinisig(int vertex)
    {
        int sig = MINISIG_BASE;
        
        sig ^= leftEdges[vertex].length * (leftEdges[vertex].length+1) * LOPT_MULTIPLIER;
        sig ^= rightEdges[vertex].length * (rightEdges[vertex].length+1) * ROPT_MULTIPLIER;
        
        int eoptions = 0;
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if (leftEdges[vertex][i] == vertex)
            {
                sig ^= LEFT_ONE_CYCLE;
            }
            else if (arrayContains(rightEdges[vertex], leftEdges[vertex][i]))
            {
                eoptions++;
            }
        }
        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if (rightEdges[vertex][i] == vertex)
            {
                sig ^= RIGHT_ONE_CYCLE;
            }
        }
        
        sig ^= eoptions * (eoptions+1) * EOPT_MULTIPLIER;
        
        return sig;
    }
    
    private static class TerminalSet
    {
        short[] leftElements;
        short[] rightElements;
        int leftSize, rightSize;
        
        TerminalSet(int capacity)
        {
            leftElements = new short[capacity];
            rightElements = new short[capacity];
            leftSize = rightSize = 0;
        }
        
        boolean containsLeft(int index)
        {
            return leftElements[index] > 0;
        }
        
        boolean containsRight(int index)
        {
            return rightElements[index] > 0;
        }
        
        void putLeft(int index, short depth)
        {
            if (leftElements[index] == 0)
            {
                leftElements[index] = depth;
                leftSize++;
            }
        }
        
        void putRight(int index, short depth)
        {
            if (rightElements[index] == 0)
            {
                rightElements[index] = depth;
                rightSize++;
            }
        }
        
        void putAll(int[] indices, short depth, boolean left, int[] flagsForReachability)
        {
            for (int i = 0; i < indices.length; i++)
            {
                if (flagsForReachability == null ||
                    (flagsForReachability[indices[i]] & MARKER_REACHABLE) != 0)
                {
                    if (left)
                    {
                        putLeft(indices[i], depth);
                    }
                    else
                    {
                        putRight(indices[i], depth);
                    }
                }
            }
        }
        
        void removeLeft(int index, short depth)
        {
            if (leftElements[index] == depth)
            {
                leftElements[index] = 0;
                leftSize--;
            }
        }
        
        void removeRight(int index, short depth)
        {
            if (rightElements[index] == depth)
            {
                rightElements[index] = 0;
                rightSize--;
            }
        }
        
        void removeAll(int[] indices, short depth, boolean left)
        {
            for (int i = 0; i < indices.length; i++)
            {
                if (left)
                {
                    removeLeft(indices[i], depth);
                }
                else
                {
                    removeRight(indices[i], depth);
                }
            }
        }
        
        void adjustSizeExcluding(int index)
        {
            if (leftElements[index] > 0)
            {
                leftSize--;
            }
            if (rightElements[index] > 0)
            {
                rightSize--;
            }
        }
        
        void adjustSizeIncluding(int index)
        {
            if (leftElements[index] > 0)
            {
                leftSize++;
            }
            if (rightElements[index] > 0)
            {
                rightSize++;
            }
        }
    }
    
    public boolean isIsomorphicTo(int vertex, Bigraph h, int hVertex)
    {
        // Check for equal #s of reachables!!!
        if (   countReachableVertices(vertex) != h.countReachableVertices(hVertex)
            || hashCodeIsomorphismInvariant(vertex) != h.hashCodeIsomorphismInvariant(hVertex))
        {
            return false;
        }
        if (vertex == hVertex && equals(h))
        {
            return true;
        }
        buildPredecessors();
        h.buildPredecessors();
        int[] mapping = new int[leftEdges.length],
              revMapping = new int[h.leftEdges.length];
        Arrays.fill(mapping, -1);
        Arrays.fill(revMapping, -1);
        boolean iso = findIsomorphism(
            vertex,
            h,
            hVertex,
            mapping,
            revMapping,
            new TerminalSet(leftEdges.length),
            new TerminalSet(h.leftEdges.length),
            new TerminalSet(leftEdges.length),
            new TerminalSet(h.leftEdges.length),
            (short) 1
            );
        /*
        if (!iso)
        {
            System.out.println("Hash collision: " + hashCodeIsomorphismInvariant(vertex));
        }
        */
        return iso;
    }
    
    private boolean findIsomorphism(
        int vertexAt,
        Bigraph h,
        int hVertexAt,
        int[] mapping,
        int[] revMapping,
        TerminalSet inG,
        TerminalSet inH,
        TerminalSet outG,
        TerminalSet outH,
        short depth
        )
    {
        // Test for feasibility.
        
        // Simple pre-test: Check that there are equal #s of left & right edges.
        // Can't do the same for preds since they might not be reachable from
        // the indicated vertices.  (Possible TODO: A count of reachable preds?)
        if (leftEdges[vertexAt].length != h.leftEdges[hVertexAt].length ||
            rightEdges[vertexAt].length != h.rightEdges[hVertexAt].length)
        {
            return false;
        }
        
        // Set up the mappings and in/out sets.
        mapping[vertexAt] = hVertexAt;
        revMapping[hVertexAt] = vertexAt;
        inG.putAll(leftPreds[vertexAt], depth, true, flags);
        inG.putAll(rightPreds[vertexAt], depth, false, flags);
        outG.putAll(leftEdges[vertexAt], depth, true, null);
        outG.putAll(rightEdges[vertexAt], depth, false, null);
        inH.putAll(h.leftPreds[hVertexAt], depth, true, h.flags);
        inH.putAll(h.rightPreds[hVertexAt], depth, false, h.flags);
        outH.putAll(h.leftEdges[hVertexAt], depth, true, null);
        outH.putAll(h.rightEdges[hVertexAt], depth, false, null);
        inG.adjustSizeExcluding(vertexAt);
        outG.adjustSizeExcluding(vertexAt);
        inH.adjustSizeExcluding(hVertexAt);
        outH.adjustSizeExcluding(hVertexAt);
        
        // VF feasibility tests.
        if (// R_termin and R_termout:
            inG.leftSize == inH.leftSize &&
            inG.rightSize == inH.rightSize &&
            outG.leftSize == outH.leftSize &&
            outG.rightSize == outH.rightSize &&
            // R_pred and R_succ:
            arraysAgreeUnderMapping(leftPreds[vertexAt], h.leftPreds[hVertexAt], mapping, revMapping) &&
            arraysAgreeUnderMapping(rightPreds[vertexAt], h.rightPreds[hVertexAt], mapping, revMapping) &&
            arraysAgreeUnderMapping(leftEdges[vertexAt], h.leftEdges[hVertexAt], mapping, revMapping) &&
            arraysAgreeUnderMapping(rightEdges[vertexAt], h.rightEdges[hVertexAt], mapping, revMapping))
        {
            int nextVertex = leftEdges.length, vertexFrom = -1;
            boolean isLeftFollower = false;
            for (int vertex = 0; vertex < mapping.length; vertex++)
            {
                if (mapping[vertex] != -1)
                {
                    for (int i = 0; i < leftEdges[vertex].length; i++)
                    {
                        if (mapping[leftEdges[vertex][i]] == -1 &&
                            leftEdges[vertex][i] < nextVertex)
                        {
                            nextVertex = leftEdges[vertex][i];
                            isLeftFollower = true;
                            vertexFrom = vertex;
                        }
                    }
                    for (int i = 0; i < rightEdges[vertex].length; i++)
                    {
                        if (mapping[rightEdges[vertex][i]] == -1 &&
                            rightEdges[vertex][i] < nextVertex)
                        {
                            nextVertex = rightEdges[vertex][i];
                            isLeftFollower = false;
                            vertexFrom = vertex;
                        }
                    }
                }
            }
            if (nextVertex == leftEdges.length)
            {
                // Done!
                return true;
            }
            
            // Try all possible choices for mapping[nextVertex].
            int[] targets = (isLeftFollower ? h.leftEdges[mapping[vertexFrom]] : h.rightEdges[mapping[vertexFrom]]);
            
            for (int i = 0; i < targets.length; i++)
            {
                if (findIsomorphism(nextVertex, h, targets[i], mapping, revMapping, inG, inH, outG, outH, (short) (depth+1)))
                {
                    return true;
                }
            }
        }
        
        // Restore the mappings and in/out sets.
        inG.adjustSizeIncluding(vertexAt);
        outG.adjustSizeIncluding(vertexAt);
        inH.adjustSizeIncluding(hVertexAt);
        outH.adjustSizeIncluding(hVertexAt);
        inG.removeAll(leftPreds[vertexAt], depth, true);
        inG.removeAll(rightPreds[vertexAt], depth, false);
        outG.removeAll(leftEdges[vertexAt], depth, true);
        outG.removeAll(rightEdges[vertexAt], depth, false);
        inH.removeAll(h.leftPreds[hVertexAt], depth, true);
        inH.removeAll(h.rightPreds[hVertexAt], depth, false);
        outH.removeAll(h.leftEdges[hVertexAt], depth, true);
        outH.removeAll(h.rightEdges[hVertexAt], depth, false);
        mapping[vertexAt] = -1;
        revMapping[hVertexAt] = -1;
        return false;
    }
    
    private static boolean arraysAgreeUnderMapping(int[] array1, int[] array2, int[] mapping, int[] revMapping)
    {
        for (int i = 0; i < array1.length; i++)
        {
            if (mapping[array1[i]] != -1 &&
                !arrayContains(array2, mapping[array1[i]]))
            {
                return false;
            }
        }
        for (int i = 0; i < array2.length; i++)
        {
            if (revMapping[array2[i]] != -1 &&
                !arrayContains(array1, revMapping[array2[i]]))
            {
                return false;
            }
        }
        return true;
    }
    
    private static boolean arrayContains(int[] array, int value)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public Bigraph starOperator(Bigraph h)
    {
        Bigraph starGraph = new Bigraph(numVertices + h.numVertices);
        // Copy all the moves from H
        for (int vertex = 0; vertex < h.numVertices; vertex++)
        {
            starGraph.leftEdges[numVertices+vertex] = new int[h.leftEdges[vertex].length];
            for (int i = 0; i < h.leftEdges[vertex].length; i++)
            {
                starGraph.leftEdges[numVertices+vertex][i] =
                    h.leftEdges[vertex][i] + numVertices;
            }
            starGraph.rightEdges[numVertices+vertex] = new int[h.rightEdges[vertex].length];
            for (int i = 0; i < h.rightEdges[vertex].length; i++)
            {
                starGraph.rightEdges[numVertices+vertex][i] =
                    h.rightEdges[vertex][i] + numVertices;
            }
        }
        // Now copy redirected versions of the moves from G.
        for (int vertex = 0; vertex < numVertices; vertex++)
        {
            starGraph.leftEdges[vertex] = new int[leftEdges[vertex].length];
            for (int i = 0; i < leftEdges[vertex].length; i++)
            {
                starGraph.leftEdges[vertex][i] = leftEdges[vertex][i] + numVertices;
            }
            starGraph.rightEdges[vertex] = new int[rightEdges[vertex].length];
            for (int i = 0; i < rightEdges[vertex].length; i++)
            {
                starGraph.rightEdges[vertex][i] = rightEdges[vertex][i] + numVertices;
            }
            Arrays.sort(starGraph.leftEdges[vertex]);
            Arrays.sort(starGraph.rightEdges[vertex]);
        }
        starGraph.markVertices();
        return starGraph;
    }
    
    public boolean isCycleFree()
    {
        for (int vertex = 0; vertex < numVertices; vertex++)
        {
            if ((flags[vertex] & FLAG_CYCLE_FREE) == 0)
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean isAlternatingCycleFree()
    {
        for (int vertex = 0; vertex < numVertices; vertex++)
        {
            if ((flags[vertex] & (FLAG_NOT_LEFT_ACF | FLAG_NOT_RIGHT_ACF)) != 0)
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns <code>true</code> if this graph contains no cycles reachable
     * from the specified vertex.
     */
    public boolean isCycleFree(int vertex)
    {
        return (flags[vertex] & FLAG_CYCLE_FREE) != 0;
    }
    
    /**
     * Returns <code>true</code> if this graph contains no alternating cycles
     * reachable from the specified vertex.  Note that the alternation need not
     * originate at the specified vertex: If there is an alternating cycle that
     * is reachable from <code>vertex</code> by a non-alternating path, then
     * this method will still return <code>false</code>.
     */
    public boolean isAlternatingCycleFree(int vertex)
    {
        boolean acf = isACFInternal(vertex);
        clearMarkers();
        return acf;
    }
    
    /**
     * Returns <code>true</code> if this graph contains no cycles reachable
     * from the specified vertex <i>of length strictly greater than one</i>.
     */
    public boolean isLongCycleFree(int vertex)
    {
        boolean lcf = isLCFInternal(vertex);
        clearMarkers();
        return lcf;
    }
    
    private boolean isACFInternal(int vertex)
    {
        if ((flags[vertex] & MARKER_1) != 0)
        {
            return true;
        }
        if ((flags[vertex] & (FLAG_NOT_LEFT_ACF | FLAG_NOT_RIGHT_ACF)) != 0)
        {
            return false;
        }
        
        flags[vertex] |= MARKER_1;
        
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if (!isACFInternal(leftEdges[vertex][i]))
            {
                return false;
            }
        }
        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if (!isACFInternal(rightEdges[vertex][i]))
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean isLCFInternal(int vertex)
    {
        if ((flags[vertex] & MARKER_2) != 0)
        {
            return true;
        }
        else if ((flags[vertex] & MARKER_1) != 0)
        {
            return false;
        }
        
        flags[vertex] |= MARKER_1;
        
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if (leftEdges[vertex][i] != vertex &&
                !isLCFInternal(leftEdges[vertex][i]))
            {
                return false;
            }
        }
        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if (rightEdges[vertex][i] != vertex &&
                !isLCFInternal(rightEdges[vertex][i]))
            {
                return false;
            }
        }
        
        flags[vertex] &= ~MARKER_1;
        flags[vertex] |= MARKER_2;
        
        return true;
    }

    private void clearMarkers()
    {
        for (int i = 0; i < numVertices; i++)
        {
            flags[i] &= ~MARKERS;
        }
    }

    private void markVertices()
    {
        for (int vertex = 0; vertex < numVertices; vertex++)
        {
            markCycleFree(vertex);
            markLeftAlternatingCycleFree(vertex);
            markRightAlternatingCycleFree(vertex);
        }
    }
    
    private void markCycleFree(int vertex)
    {
        if ((flags[vertex] & FLAG_RES_CYCLE_FREE) != 0)
        {
            return;
        }

        flags[vertex] |= MARKER_1;
        boolean cycleFree = true;
        
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if ((flags[leftEdges[vertex][i]] & MARKER_1) != 0)
            {
                // A vertex can only have a marker if it's an ancestor
                // of this vertex.  So we've found a loop.
                cycleFree = false;
            }
            else
            {
                markCycleFree(leftEdges[vertex][i]);
                if ((flags[leftEdges[vertex][i]] & FLAG_NOT_CYCLE_FREE) != 0)
                {
                    cycleFree = false;
                }
            }
        }
        
        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if ((flags[rightEdges[vertex][i]] & MARKER_1) != 0)
            {
                cycleFree = false;
            }
            else
            {
                markCycleFree(rightEdges[vertex][i]);
                if ((flags[rightEdges[vertex][i]] & FLAG_NOT_CYCLE_FREE) != 0)
                {
                    cycleFree = false;
                }
            }
        }
        
        flags[vertex] |= (cycleFree ? FLAG_CYCLE_FREE : FLAG_NOT_CYCLE_FREE);
        flags[vertex] &= ~MARKER_1;
    }

    private void markLeftAlternatingCycleFree(int vertex)
    {
        if ((flags[vertex] & FLAG_RES_LEFT_ACF) != 0)
        {
            return;
        }
        
        flags[vertex] |= MARKER_1;
        boolean leftAlternatingCycleFree = true;
        
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if ((flags[leftEdges[vertex][i]] & MARKER_2) != 0)
            {
                // The only way a vertex k could have a MARKER_2 is if
                // there were some alternating sequence of edges from k,
                // starting with a right edge and leading to this vertex.
                // So there is an alternating loop from this vertex,
                // starting with left to move.
                leftAlternatingCycleFree = false;
            }
            else
            {
                markRightAlternatingCycleFree(leftEdges[vertex][i]);
                if ((flags[leftEdges[vertex][i]] & FLAG_NOT_RIGHT_ACF) != 0)
                {
                    leftAlternatingCycleFree = false;
                }
            }
        }

        flags[vertex] |= (leftAlternatingCycleFree ? FLAG_LEFT_ACF : FLAG_NOT_LEFT_ACF);
        flags[vertex] &= ~MARKER_1;
    }

    private void markRightAlternatingCycleFree(int vertex)
    {
        if ((flags[vertex] & FLAG_RES_RIGHT_ACF) != 0)
        {
            return;
        }
        
        flags[vertex] |= MARKER_2;
        boolean rightAlternatingCycleFree = true;

        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if ((flags[rightEdges[vertex][i]] & MARKER_1) != 0)
            {
                rightAlternatingCycleFree = false;
            }
            else
            {
                markLeftAlternatingCycleFree(rightEdges[vertex][i]);
                if ((flags[rightEdges[vertex][i]] & FLAG_NOT_LEFT_ACF) != 0)
                {
                    rightAlternatingCycleFree = false;
                }
            }
        }

        flags[vertex] |= (rightAlternatingCycleFree ? FLAG_RIGHT_ACF : FLAG_NOT_RIGHT_ACF);
        flags[vertex] &= ~MARKER_2;
    }

    public boolean isReachable(int startVertex, int targetVertex)
    {
        if (reachableFrom != startVertex)
        {
            if (reachableFrom != -1)
            {
                for (int i = 0; i < numVertices; i++)
                {
                    flags[i] &= ~MARKER_REACHABLE;
                }
            }
            markReachableVertices(startVertex);
            reachableFrom = startVertex;
        }
        return ((flags[targetVertex] & MARKER_REACHABLE) != 0);
    }
    
    private int countReachableVertices(int startVertex)
    {
        if (reachableFrom != -1)
        {
            for (int i = 0; i < numVertices; i++)
            {
                flags[i] &= ~MARKER_REACHABLE;
            }
        }
        reachableFrom = startVertex;
        return markReachableVertices(startVertex);
    }
    
    private int markReachableVertices(int vertex)
    {
        if ((flags[vertex] & MARKER_REACHABLE) != 0)
        {
            return 0;
        }
        int count = 1;
        flags[vertex] |= MARKER_REACHABLE;
        for (int i = 0; i < leftEdges[vertex].length; i++)
        {
            if (leftEdges[vertex][i] != -1)
            {
                count += markReachableVertices(leftEdges[vertex][i]);
            }
        }
        for (int i = 0; i < rightEdges[vertex].length; i++)
        {
            if (rightEdges[vertex][i] != -1)
            {
                count += markReachableVertices(rightEdges[vertex][i]);
            }
        }
        return count;
    }
    
    private static int[][][] pack(int[][] oldLeftEdges, int[][] oldRightEdges, int masterVertex)
    {
        int packMap[] = new int[oldLeftEdges.length];
        Arrays.fill(packMap, -1);
        int newNumVertices = initializePackMap(oldLeftEdges, oldRightEdges, packMap, masterVertex, 0);
        int[][] leftEdges = new int[newNumVertices][],
                rightEdges = new int[newNumVertices][];
        for (int vertex = 0; vertex < packMap.length; vertex++)
        {
            if (packMap[vertex] != -1)
            {
                int numLeftEdges = 0, numRightEdges = 0;
                Arrays.sort(oldLeftEdges[vertex]);
                for (int i = 0; i < oldLeftEdges[vertex].length; i++)
                {
                    if (oldLeftEdges[vertex][i] != -1 &&
                        (i == 0 || oldLeftEdges[vertex][i] != oldLeftEdges[vertex][i-1]))
                    {
                        numLeftEdges++;
                    }
                }
                Arrays.sort(oldRightEdges[vertex]);
                for (int i = 0; i < oldRightEdges[vertex].length; i++)
                {
                    if (oldRightEdges[vertex][i] != -1 &&
                        (i == 0 || oldRightEdges[vertex][i] != oldRightEdges[vertex][i-1]))
                    {
                        numRightEdges++;
                    }
                }
                leftEdges[packMap[vertex]] = new int[numLeftEdges];
                rightEdges[packMap[vertex]] = new int[numRightEdges];
                int nextEdgeOrdinal = 0;
                for (int i = 0; i < oldLeftEdges[vertex].length; i++)
                {
                    if (oldLeftEdges[vertex][i] != -1 &&
                        (i == 0 || oldLeftEdges[vertex][i] != oldLeftEdges[vertex][i-1]))
                    {
                        leftEdges[packMap[vertex]][nextEdgeOrdinal] = packMap[oldLeftEdges[vertex][i]];
                        nextEdgeOrdinal++;
                    }
                }
                nextEdgeOrdinal = 0;
                for (int i = 0; i < oldRightEdges[vertex].length; i++)
                {
                    if (oldRightEdges[vertex][i] != -1 &&
                        (i == 0 || oldRightEdges[vertex][i] != oldRightEdges[vertex][i-1]))
                    {
                        rightEdges[packMap[vertex]][nextEdgeOrdinal] = packMap[oldRightEdges[vertex][i]];
                        nextEdgeOrdinal++;
                    }
                }
            }
        }
        return new int[][][] { leftEdges, rightEdges };
    }
    
    private static int initializePackMap
        (int[][] oldLeftEdges, int[][] oldRightEdges, int[] packMap, int vertex, int nextPackedOrdinal)
    {
        if (packMap[vertex] != -1)
        {
            // We've already assigned a target to this vertex.
            return nextPackedOrdinal;
        }
        
        packMap[vertex] = nextPackedOrdinal;
        nextPackedOrdinal++;
        
        for (int i = 0; i < oldLeftEdges[vertex].length; i++)
        {
            if (oldLeftEdges[vertex][i] != -1)
            {
                nextPackedOrdinal = initializePackMap
                    (oldLeftEdges, oldRightEdges, packMap, oldLeftEdges[vertex][i], nextPackedOrdinal);
            }
        }
        for (int i = 0; i < oldRightEdges[vertex].length; i++)
        {
            if (oldRightEdges[vertex][i] != -1)
            {
                nextPackedOrdinal = initializePackMap
                    (oldLeftEdges, oldRightEdges, packMap, oldRightEdges[vertex][i], nextPackedOrdinal);
            }
        }
        
        return nextPackedOrdinal;
    }
    
    private void buildPredecessors()
    {
        if (leftPreds != null)
        {
            return;
        }
        
        leftPreds = new int[leftEdges.length][];
        rightPreds = new int[rightEdges.length][];
        
        int[] leftCounters = new int[leftEdges.length],
              rightCounters = new int[rightEdges.length];
        
        for (int vertex = 0; vertex < leftEdges.length; vertex++)
        {
            for (int i = 0; i < leftEdges[vertex].length; i++)
            {
                if (leftEdges[vertex][i] != -1)
                {
                    leftCounters[leftEdges[vertex][i]]++;
                }
            }
            for (int i = 0; i < rightEdges[vertex].length; i++)
            {
                if (rightEdges[vertex][i] != -1)
                {
                    rightCounters[rightEdges[vertex][i]]++;
                }
            }
        }
        for (int vertex = 0; vertex < leftEdges.length; vertex++)
        {
            leftPreds[vertex] = new int[leftCounters[vertex]];
            rightPreds[vertex] = new int[rightCounters[vertex]];
            leftCounters[vertex] = rightCounters[vertex] = 0;
        }
        for (int vertex = 0; vertex < leftEdges.length; vertex++)
        {
            for (int i = 0; i < leftEdges[vertex].length; i++)
            {
                if (leftEdges[vertex][i] != -1)
                {
                    int target = leftEdges[vertex][i];
                    leftPreds[target][leftCounters[target]] = vertex;
                    leftCounters[target]++;
                }
            }
            for (int i = 0; i < rightEdges[vertex].length; i++)
            {
                if (rightEdges[vertex][i] != -1)
                {
                    int target = rightEdges[vertex][i];
                    rightPreds[target][rightCounters[target]] = vertex;
                    rightCounters[target]++;
                }
            }
        }
    }

    public static void main(String[] args)
    {
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < 12; i++)
        {
            System.out.println(r.nextInt());
        }
        System.exit(0);
        Bigraph g = new Bigraph(4);
        Bigraph h = new Bigraph(3);
        
        g.leftEdges = new int[][] { { 2, 1 }, { }, { 1 }, { } };
        g.rightEdges = new int[][] { { 3 }, { }, { 1 }, { 3 } };
        h.leftEdges = new int[][] { { 1 }, { }, { } };
        h.rightEdges = new int[][] { { 2 }, { }, { 2 } };
        
        System.out.println(h.isIsomorphicTo(1, g, 1));
    }
}
