/*
 * LoopyGame.java
 *
 * Created on February 21, 2003, 6:32 PM
 * $Id: LoopyGame.java,v 1.29 2007/02/16 20:10:13 asiegel Exp $
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

import static org.cgsuite.lang.game.CanonicalShortGame.STAR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteCollection;
import org.cgsuite.lang.CgsuiteInteger;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.CgsuiteSet;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.InputException;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;


/**
 * An arbitrary loopy game, represented by an explicit game digraph.
 * <p>
 * Combinatorial Game Suite provides substantial support for loopy games with
 * finite game graphs.  Such games are represented internally by a
 * {@link org.cgsuite.util.Digraph}, a directed graph with separate left and right
 * edges.
 * Most of the theory concerning loopy games can be found in Chapter 11 of
 * Winning Ways, and many of the algorithms employed here are adapted from
 * that presentation.
 * <p>
 * There are two ways to construct a <code>LoopyGame</code>.  The first is to
 * pass an explicit <code>Digraph</code> and starting vertex to the
 * {@link #LoopyGame(org.cgsuite.util.Digraph, int) LoopyGame(Digraph, int)}
 * constructor.  Since this is
 * inconvenient in practice, construction can also be handled by the
 * auxiliary class {@link LoopyGame.Node}.  To construct a
 * <code>LoopyGame</code> using this method, first
 * construct a <code>Node</code> for each node of the graph, then link the
 * nodes together, and finally, pass the root node of the completed graph
 * structure to the {@link #LoopyGame(LoopyGame.Node) LoopyGame(Node)}
 * constructor.  The
 * constructor then assembles the result into a <code>Digraph</code>.  For
 * convenience, it's also possible to "hang" other <code>LoopyGame</code>s or
 * {@link CanonicalGame}s from individual nodes.  See the
 * <code>LoopyGame.Node</code> documentation for further details.
 * <p>
 * Although Combinatorial Game Suite is equipped to handle arbitrary loopy
 * games, it is most effective at analyzing <i>stoppers</i>, games for which
 * there is no infinite alternating sequence of moves from any subposition.
 * Combinatorial Game Suite uses separate, faster algorithms for analyzing
 * stoppers.  There are also efficient algorithms for handling
 * <i>stopper-sided</i> games; that is, games whose onsides and offsides are
 * stoppers.  See the {@link StopperSidedGame} class for details.
 * <p>
 * Note that <code>LoopyGame</code> can also be used to represent the explicit
 * game tree of an ender that is not in canonical form, in the rare
 * circumstances when it is necessary to do so.
 * <p>
 * As of version 0.6, Combinatorial Game Suite can handle extended
 * thermography for loopy games in a number of different environments.  See
 * the {@link #thermograph(Environment) thermograph} method for details.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.29 $ $Date: 2007/02/16 20:10:13 $
 * @see     LoopyGame.Node
 * @see     CanonicalStopperGame
 * @see     StopperSidedGame
 * @see     org.cgsuite.util.Digraph
 */
public class LoopyGame extends Game
{
    private static byte[] markers;
    private static byte[] reachMarkers;
    
    static int
        STOPPER = 0,
        ONSIDE  = 0x1,
        OFFSIDE = 0x2,
        GENERAL = 0x3;
    
    private static boolean DEBUG = false;
    
    Bigraph graph;
    int startVertex;
    
    ////////////////////////////////////////////////////////////////////////
    // Construction and initialization.
    
    /**
     * Constructs a <code>LoopyGame</code> whose game graph is provided by an
     * explicit {@link org.cgsuite.util.Digraph}.
     *
     * @param   graph The game graph of this game.
     * @param   startVertex The vertex number of the starting vertex of this
     *          game.
     * @throws  NullPointerException If <code>graph</code> is
     *          <code>null</code>.
     * @throws  IllegalArgumentException If <code>startVertex</code> is not
     *          between <code>0</code> and
     *          <code>graph.getNumVertices()-1</code>.
     */
    public LoopyGame(Bigraph graph, int startVertex)
    {
        this();
        if (startVertex < 0 || startVertex >= graph.getNumVertices())
        {
            throw new IllegalArgumentException("startVertex is out of range.");
        }
        this.graph = graph;
        this.startVertex = startVertex;
    }
    
    /**
     * Constructs a <code>LoopyGame</code> whose game graph is described by
     * the specified {@link Node}.
     *
     * @param   node The root node of this game's graph.
     */
    public LoopyGame(Node node)
    {
        this();
        if (node.graphInfo == null || !node.graphInfo.valid)
        {
            initialize(node, false);
        }
        
        graph = node.graphInfo.graph;
        startVertex = node.startVertex;
    }

    LoopyGame(CgsuiteClass type)
    {
        super(type);
    }
    
    private LoopyGame()
    {
        this(CgsuiteClass.OBJECT_TYPE);
    }
    
    private static void expandMarkers(int size)
    {
        if (markers == null || markers.length < size)
        {
            // To avoid excessive reallocation, we grow the markers array
            // using the following scheme.  If 8M markers or fewer are
            // needed, use the smallest power of 2 that suffices (min: 64K).
            // Otherwise, use the smallest number of the form (m + n*m/8),
            // where m is a power of 2 and 0 <= n < 7.
            
            // Find the largest power of 2 that's < size.
            int pow2 = 16, len;
            while (pow2 < 30 && (1 << (pow2+1)) < size)
            {
                pow2++;
            }
            
            if (pow2 < 23)
            {
                // Use the next larger power of 2.
                len = 1 << (pow2+1);
            }
            else
            {
                len = 1 << pow2;
                while (len < size)
                {
                    len += (1 << (pow2-3));
                }
            }
//            Context.getActiveContext().getLogger().finer
//                ("Growing static markers array for loopy game calculations (new size: " + (len >> 10) + " KB).");
            markers = null;     // In case memory is low
            markers = new byte[len];
        }
    }
    
    private static void expandReachMarkers(int size)
    {
        if (reachMarkers == null || reachMarkers.length < size)
        {
            reachMarkers = null;
            int len = (1 << 10);
            while (len < size)
            {
                len <<= 1;
            }
            reachMarkers = new byte[len];
        }
    }
    
    static Bigraph initialize(Node startNode)
    {
        return initialize(startNode, false);
    }
    
    // missingMovesAreSenteThreats option is currently not publicly accessible.
    private static Bigraph initialize(Node startNode, boolean missingMovesAreSenteThreats)
    {
        List<Object> nodes = new ArrayList<Object>();
        Map<Object,Integer> nodeMap = new HashMap<Object,Integer>();
        int numVertices = serializeNodes(nodes, nodeMap, startNode, 0);
        if (missingMovesAreSenteThreats)
        {
            numVertices = serializeNodes(nodes, nodeMap, CanonicalStopperGame.ON, numVertices);
            numVertices = serializeNodes(nodes, nodeMap, CanonicalStopperGame.OFF, numVertices);
        }
        Pregraph pg = new Pregraph(numVertices);
        Node.GraphInfo graphInfo = new Node.GraphInfo();
        graphInfo.canonical = new boolean[numVertices];
        
        for (Object o : nodes)
        {
            int vertex = nodeMap.get(o);

            if (o instanceof Node && ((Node) o).isTerminal())
            {
                Node node = (Node) o;
                node.graphInfo = graphInfo;
                node.startVertex = vertex;
            }
            else
            {
                int k;
                CgsuiteCollection leftEdges, rightEdges;
                if (o instanceof Node)
                {
                    Node node = (Node) o;
                    node.graphInfo = graphInfo;
                    node.startVertex = vertex;
                    CgsuiteCollection leftEdgeObjs = new CgsuiteSet(),
                                      rightEdgeObjs = new CgsuiteSet();
                    for (Node target : node.getLeftEdges())
                    {
                        if (target.isTerminal())
                        {
                            leftEdgeObjs.add(target.getValue());
                        }
                        else
                        {
                            leftEdgeObjs.add(target);
                        }
                    }
                    for (Node target : node.getRightEdges())
                    {
                        if (target.isTerminal())
                        {
                            rightEdgeObjs.add(target.getValue());
                        }
                        else
                        {
                            rightEdgeObjs.add(target);
                        }
                    }
                    if (missingMovesAreSenteThreats && leftEdgeObjs.isEmpty())
                    {
                        leftEdges = CgsuiteSet.singleton(CanonicalStopperGame.ON);
                    }
                    else
                    {
                        leftEdges = leftEdgeObjs;
                    }
                    if (missingMovesAreSenteThreats && rightEdgeObjs.isEmpty())
                    {
                        rightEdges = CgsuiteSet.singleton(CanonicalStopperGame.OFF);
                    }
                    else
                    {
                        rightEdges = rightEdgeObjs;
                    }
                }
                else
                {
                    leftEdges = ((Game) o).getLeftOptions();
                    rightEdges = ((Game) o).getRightOptions();
                    if (o instanceof CanonicalShortGame || o instanceof CanonicalStopperGame)
                    {
                        graphInfo.canonical[vertex] = true;
                    }
                }
                pg.leftEdges[vertex] = new int[leftEdges.size()];
                pg.rightEdges[vertex] = new int[rightEdges.size()];
                k = 0;
                for (Object target : leftEdges)
                {
                    assert nodeMap.containsKey(target) : target;
                    pg.leftEdges[vertex][k++] = nodeMap.get(target);
                }
                k = 0;
                for (Object target : rightEdges)
                {
                    assert nodeMap.containsKey(target) : target;
                    pg.rightEdges[vertex][k++] = nodeMap.get(target);
                }
            }
        }
        graphInfo.graph = new Bigraph(pg.leftEdges, pg.rightEdges);
        return graphInfo.graph;
    }

    private static int serializeNodes(List<Object> nodes, Map<Object,Integer> nodeMap, Object o, int nextVertexOrdinal)
    {
        if (nodeMap.containsKey(o))
        {
            return nextVertexOrdinal;
        }
        
        nodes.add(o);
        
        if (o instanceof Node)
        {
            Node node = (Node) o;
            if (node.isTerminal())
            {
                nextVertexOrdinal = serializeNodes(nodes, nodeMap, node.getValue(), nextVertexOrdinal);
                nodeMap.put(node, nodeMap.get(node.getValue()));
                return nextVertexOrdinal;
            }
            else
            {
                nodeMap.put(node, nextVertexOrdinal);
                nextVertexOrdinal++;
                for (Node target : node.getLeftEdges())
                {
                    nextVertexOrdinal = serializeNodes(nodes, nodeMap, target, nextVertexOrdinal);
                }
                for (Node target : node.getRightEdges())
                {
                    nextVertexOrdinal = serializeNodes(nodes, nodeMap, target, nextVertexOrdinal);
                }
            }
        }
        else if (o instanceof CanonicalShortGame || o instanceof CgsuiteInteger || o instanceof LoopyGame)
        {
            Game g = (Game) o;
            nodeMap.put(g, nextVertexOrdinal);
            nextVertexOrdinal++;
            for (CgsuiteObject opt : g.getLeftOptions())
            {
                nextVertexOrdinal = serializeNodes(nodes, nodeMap, (Game) opt, nextVertexOrdinal);
            }
            for (CgsuiteObject opt : g.getRightOptions())
            {
                nextVertexOrdinal = serializeNodes(nodes, nodeMap, (Game) opt, nextVertexOrdinal);
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
        
        return nextVertexOrdinal;
    }
    
    ////////////////////////////////////////////////////////////////////////
    // Interface implementations and overrides (Object, Cacheable, Game).

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof LoopyGame &&
            graph.isIsomorphicTo(startVertex, ((LoopyGame) obj).graph, ((LoopyGame) obj).startVertex);
    }
    
    @Override
    public int hashCode()
    {
        return graph.hashCodeIsomorphismInvariant(startVertex);
    }
    
    @Override
    public StyledTextOutput toOutput()
    {
        StyledTextOutput output = new StyledTextOutput();
        toOutput(output, true);
        return output;
    }
    
    private enum UponType
    {
        UPON,
        UPONTH,
        UPOVER,
        UPOVERTH;
    }
    
    private class UponForm
    {

        public UponForm(UponType type, RationalNumber number)
        {
            this.type = type;
            this.number = number;
        }
        
        UponType type;
        RationalNumber number;
    }
    
    private CanonicalShortGame isNumberTiny()
    {
        if (!isStopper())
            return null;
        
        if (getLeftOptions().size() != 1 || getRightOptions().size() != 1)
            return null;
        
        LoopyGame gl = (LoopyGame) getLeftOptions().anyElement();
        if (!gl.isLoopfree())
            return null;
        
        CanonicalShortGame number = gl.loopfreeRepresentation();
        if (!number.isNumber())
            return null;
        
        LoopyGame gr = (LoopyGame) getRightOptions().iterator().next();
        
        if (gr.getLeftOptions().size() != 1 || gr.getRightOptions().size() != 1)
            return null;
        
        LoopyGame grl = (LoopyGame) gr.getLeftOptions().anyElement();
        
        if (!grl.isLoopfree())
            return null;
        
        if (!grl.loopfreeRepresentation().equals(number))
            return null;
        
        LoopyGame grr = (LoopyGame) gr.getRightOptions().anyElement();
        
        if (!grr.subtract(number).leq(CanonicalStopperGame.UNDER))
            return null;
        
        return number;
    }
    
    private UponForm uponForm(boolean checkUponth)
    {
        if (!isStopper())
        {
            return null;
        }
        
        if (!(this instanceof CanonicalStopperGame))
        {
            return ((LoopyGame) canonicalizeStopper()).uponForm(checkUponth);
        }
        
        if (getLeftOptions().size() != 1 || getRightOptions().size() != 1)
        {
            return null;
        }
        
        LoopyGame gl = (LoopyGame) getLeftOptions().iterator().next();
        LoopyGame gr = (LoopyGame) getRightOptions().iterator().next();
        
        CanonicalShortGame glc = null, grc = null;
        
        if (gl.isLoopfree())
            glc = gl.loopfreeRepresentation();
        if (gr.isLoopfree())
            grc = gr.loopfreeRepresentation();
        
        if (equals(gl) && grc != null && grc.add(STAR).isNumber())
        {
            return new UponForm(UponType.UPON, grc.getNumberPart());
        }
        
        if (checkUponth)
        {
            if (glc != null && glc.isNumber())
            {
                RationalNumber number = glc.getNumberPart();
                UponForm uf = gr.negate().add(STAR).uponForm(false);
                if (uf != null && uf.type == UponType.UPON && uf.number.negate().equals(number))
                {
                    return new UponForm(UponType.UPONTH, number);
                }
            }
        }
        
        return null;
    }

    int toOutput(StyledTextOutput output, boolean forceBrackets)
    {
        return toOutput(output, forceBrackets, new HashMap<LoopyGame,String>(), new int[1]);
    }

    private static int maxSlashes = 4;

    int toOutput(StyledTextOutput output, boolean forceBrackets, Map<LoopyGame,String> nodeStack, int[] numNamedNodes)
    {
        LoopyGame g = this;
        if (nodeStack.containsKey(g))
        {
            String name = nodeStack.get(g);
            if (name == null)
            {
                name = nthName(numNamedNodes[0]);
                nodeStack.put(g, name);
                numNamedNodes[0]++;
            }
            output.appendMath(name);
            return 0;
        }
        else if (g.isLoopfree())
        {
            return g.loopfreeRepresentation().toOutput(output, forceBrackets, false);
        }
        else if (g.isOn())
        {
            output.appendMath("on");
            return 0;
        }
        else if (g.isOff())
        {
            output.appendMath("off");
            return 0;
        }
        else if (g.getLeftOptions().size() == 1 && g.getRightOptions().size() == 1)
        {
            LoopyGame gl = (LoopyGame) g.getLeftOptions().iterator().next(),
                      gr = (LoopyGame) g.getRightOptions().iterator().next();

            CanonicalShortGame glc = null, grc = null;

            if (gl.isLoopfree())
            {
                glc = gl.loopfreeRepresentation();
            }
            if (gr.isLoopfree())
            {
                grc = gr.loopfreeRepresentation();
            }

            if (glc != null && glc.isNumber() && gr.equals(g))
            {
                if (!glc.isZero())
                {
                    glc.toOutput(output, false, false);
                }
                output.appendMath("over");
                return 0;
            }
            else if (gl.equals(g) && grc != null && grc.isNumber())
            {
                if (!grc.isZero())
                {
                    grc.toOutput(output, false, false);
                }
                output.appendMath("under");
                return 0;
            }
        }
        
        CanonicalShortGame tinyN = isNumberTiny();
        CanonicalShortGame minyN = negate().isNumberTiny();
        
        if ((tinyN != null || minyN != null) && isPlumtree())
        {
            boolean tiny = (tinyN != null);
            
            String str;
            CanonicalShortGame translate;
            LoopyGame subscript;
            
            if (tiny)
            {
                str = "Tiny";
                translate = tinyN;
                subscript = ((LoopyGame) ((LoopyGame) getRightOptions().anyElement()).getRightOptions().anyElement()).subtract(translate).canonicalizeStopper().negate();
            }
            else
            {
                str = "Miny";
                translate = minyN.negate();
                subscript = ((LoopyGame) ((LoopyGame) getLeftOptions().anyElement()).getLeftOptions().anyElement()).subtract(translate).canonicalizeStopper();
            }
            
            // First get a sequence for the subscript.  If that sequence contains any
            // subscripts or superscripts, then we suppress.
            StyledTextOutput sub = subscript.toOutput();
            
            EnumSet<StyledTextOutput.Style> styles = sub.allStyles();
            styles.retainAll(StyledTextOutput.Style.TRUE_LOCATIONS);
            if (styles.isEmpty())
            {
                if (!translate.equals(CanonicalShortGame.ZERO))
                {
                    output.appendOutput(translate.getNumberPart().toOutput());
                    output.appendText(Output.Mode.PLAIN_TEXT, "+");
                }
                output.appendSymbol(
                    EnumSet.noneOf(StyledTextOutput.Style.class),
                    EnumSet.complementOf(EnumSet.of(Output.Mode.PLAIN_TEXT)),
                    tiny ? StyledTextOutput.Symbol.TINY : StyledTextOutput.Symbol.MINY
                    );
                output.appendOutput(EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT), sub);
                output.appendText(Output.Mode.PLAIN_TEXT, "." + str);
                return 0;
            }
        }
            
        UponForm uf = null;
        boolean ufStar = false;
        int ufSign = 1;

        uf = uponForm(true);
        if (uf == null)
        {
            ufSign = -1;
            uf = negate().uponForm(true);
        }
        if (uf == null)
        {
            ufStar = true;
            uf = negate().add(STAR).uponForm(true);
        }
        if (uf == null)
        {
            ufSign = 1;
            uf = add(STAR).uponForm(true);
        }

        if (uf != null)
        {
            if (ufSign < 0)
                uf.number = uf.number.negate();

            if (!uf.number.equals(RationalNumber.ZERO))
                output.appendOutput(uf.number.toOutput());

            if (ufSign > 0)
                output.appendSymbol(StyledTextOutput.Symbol.UP);
            else
                output.appendSymbol(StyledTextOutput.Symbol.DOWN);

            EnumSet<StyledTextOutput.Style> exponentStyle =
                EnumSet.of(StyledTextOutput.Style.FACE_MATH,
                    ufSign > 0 ? StyledTextOutput.Style.LOCATION_SUPERSCRIPT : StyledTextOutput.Style.LOCATION_SUBSCRIPT);

            switch (uf.type)
            {
                case UPON:
                    output.appendText(exponentStyle, "[on]");
                    break;

                case UPONTH:
                    output.appendText(exponentStyle, "on");
                    break;

                case UPOVER:
                    output.appendText(exponentStyle, "[over]");
                    break;

                case UPOVERTH:
                    output.appendText(exponentStyle, "over");
                    break;

                default:
                    assert false;
            }

            if (ufStar)
                output.appendSymbol(StyledTextOutput.Symbol.STAR);

            return 0;
        }

        nodeStack.put(g, null);
        int numSlashes = 1;
        StyledTextOutput leftOutput = new StyledTextOutput(), rightOutput = new StyledTextOutput();
        List<Game>
            leftOptions  = sortLoopyOptions(g.getLeftOptions()),
            rightOptions = sortLoopyOptions(g.getRightOptions());
        for (Iterator<Game> i = leftOptions.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (o.equals(g))
            {
                leftOutput.appendMath("pass");
            }
            else if (o instanceof CanonicalShortGame)
            {
                numSlashes = Math.max
                    (numSlashes, ((CanonicalShortGame) o).toOutput(leftOutput, leftOptions.size() > 1, false) + 1);
            }
            else
            {
                numSlashes = Math.max(numSlashes, ((LoopyGame) o).toOutput(
                    leftOutput,
                    leftOptions.size() > 1,
                    nodeStack,
                    numNamedNodes
                    ) + 1);
            }
            if (i.hasNext())
            {
                leftOutput.appendMath(",");
            }
        }
        for (Iterator i = rightOptions.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (o.equals(g))
            {
                rightOutput.appendMath("pass");
            }
            else if (o instanceof CanonicalShortGame)
            {
                numSlashes = Math.max
                    (numSlashes, ((CanonicalShortGame) o).toOutput(rightOutput, rightOptions.size() > 1, false) + 1);
            }
            else
            {
                numSlashes = Math.max(numSlashes, ((LoopyGame) o).toOutput(
                    rightOutput,
                    rightOptions.size() > 1,
                    nodeStack,
                    numNamedNodes
                    ) + 1);
            }
            if (i.hasNext())
            {
                rightOutput.appendMath(",");
            }
        }
        String name = nodeStack.remove(g);

        if (name != null)
        {
            output.appendMath(name);
            forceBrackets = true;
        }
        if (leftOptions.isEmpty() || rightOptions.isEmpty())
        {
            // Force brackets for clarity.
            forceBrackets = true;
        }
        if (forceBrackets || numSlashes == maxSlashes)
        {
            output.appendMath("{");
        }
        output.appendOutput(leftOutput);
        output.appendMath(CanonicalShortGame.getSlashString(numSlashes));
        output.appendOutput(rightOutput);
        if (forceBrackets || numSlashes == maxSlashes)
        {
            output.appendMath("}");
            return 0;
        }
        else
        {
            return numSlashes;
        }
    }

    private static String nthName(int n)
    {
        if (n < 26)
        {
            return String.valueOf((char) (97+n));
        }
        else
        {
            return "N" + String.valueOf(n-25);
        }
    }

    private List<Game> sortLoopyOptions(CgsuiteSet options)
    {
        List<Game> sortedOptions = new ArrayList<Game>();
        for (CgsuiteObject obj : options)
        {
            LoopyGame g = (LoopyGame) obj;
            if (g.isLoopfree())
            {
                sortedOptions.add(g.loopfreeRepresentation());
            }
            else
            {
                sortedOptions.add(g);
            }
        }
        Collections.sort(sortedOptions, UNIVERSAL_COMPARATOR);
        return sortedOptions;
    }

    @Override
    public CgsuiteSet getLeftOptions()
    {
        CgsuiteSet leftOptions = new CgsuiteSet();
        for (int i = 0; i < graph.getNumLeftEdges(startVertex); i++)
        {
            LoopyGame lo = new LoopyGame();
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
            LoopyGame ro = new LoopyGame();
            ro.graph = graph;
            ro.startVertex = graph.getRightEdgeTarget(startVertex, i);
            rightOptions.add(ro);
        }
        return rightOptions;
    }
    
    @Override
    public LoopyGame negate()
    {
        LoopyGame inverse = new LoopyGame();
        inverse.graph = graph.getInverse();
        inverse.startVertex = startVertex;
        return inverse;
    }
    
    public LoopyGame solve()
    {
        Bigraph onside  = simplifyGraph(graph, new boolean[graph.getNumVertices()], ONSIDE, startVertex),
                offside = simplifyGraph(graph, new boolean[graph.getNumVertices()], OFFSIDE, startVertex);
        
        if (onside.isAlternatingCycleFree(0) && offside.isAlternatingCycleFree(0) &&
            onside.isIsomorphicTo(0, offside, 0))
        {
            CanonicalStopperGame solution = new CanonicalStopperGame();
            solution.graph = onside;
            solution.startVertex = 0;
            return solution;
        }
        else
        {
            return this;
        }
    }
    
    public LoopyGame solve(boolean onside)
    {
        Bigraph side = simplifyGraph(graph, new boolean[graph.getNumVertices()], onside ? ONSIDE : OFFSIDE, startVertex);
        
        LoopyGame solution = (side.isAlternatingCycleFree(0) ? new CanonicalStopperGame() : new LoopyGame());
        solution.graph = side;
        solution.startVertex = 0;
        return solution;
    }
    
    public CanonicalShortGame loopfreeRepresentation()
    {
        if (graph.isCycleFree(startVertex))
        {
            return canonicalize(startVertex);
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    CanonicalShortGame canonicalize(int vertex)
    {
        return canonicalize(vertex, new CanonicalShortGame[graph.getNumVertices()]);
    }
    
    private CanonicalShortGame canonicalize(int vertex, CanonicalShortGame[] cachedCanonicalForms)
    {
        if (cachedCanonicalForms[vertex] == null)
        {
            CanonicalShortGame[] leftOptions = new CanonicalShortGame[graph.getNumLeftEdges(vertex)],
                            rightOptions = new CanonicalShortGame[graph.getNumRightEdges(vertex)];
            for (int i = 0; i < leftOptions.length; i++)
            {
                leftOptions[i] = canonicalize(graph.getLeftEdgeTarget(vertex, i), cachedCanonicalForms);
            }
            for (int i = 0; i < rightOptions.length; i++)
            {
                rightOptions[i] = canonicalize(graph.getRightEdgeTarget(vertex, i), cachedCanonicalForms);
            }
            cachedCanonicalForms[vertex] = CanonicalShortGame.construct(leftOptions, rightOptions);
        }
        return cachedCanonicalForms[vertex];
    }
    
    @Override
    public Game simplify()
    {
        if (graph.isCycleFree(startVertex))
        {
            return canonicalize(startVertex).simplify();
        }
        else if (isStopper())
        {
            return canonicalizeStopperInternal();
        }
        else
        {
            try {
                CanonicalStopperGame onside = onside();
                CanonicalStopperGame offside = offside();
                if (onside.equals(offside))
                    return onside;
                else
                    return new StopperSidedGame(onside(), offside());
            } catch (NotStopperException exc) {
                throw new InputException("That game is not stopper-sided.");
            }
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    // Basic operations.
    
    /**
     * Calculates the sum of this game and <code>h</code>.  The game graph
     * of the sum is equal to the direct sum of the components' game graphs.
     * Note that the sum of two stoppers need not be a stopper.
     *
     * @param   h The game to add to this game.
     * @return  The sum of this game and <code>h</code>.
     * @see     org.cgsuite.util.Digraph#directSum(org.cgsuite.util.Digraph) Digraph.directSum
     */
    public LoopyGame add(LoopyGame h)
    {
        LoopyGame sum = new LoopyGame();
        sum.graph = graph.directSum(h.graph);
        sum.startVertex = startVertex * h.graph.getNumVertices() + h.startVertex;
        return sum;
    }
    
    public LoopyGame add(CanonicalShortGame h)
    {
        return add(new CanonicalStopperGame(h));
    }
    
    /**
     * Calculates the difference of this game and <code>h</code>.
     * <p>
     * <code>subtract(h)</code> is equivalent to
     * <code>add(h.getInverse())</code>.
     *
     * @param   h The game to subtract from this game.
     * @return  The difference of this game and <code>h</code>.
     */
    public LoopyGame subtract(LoopyGame h)
    {
        return add(h.negate());
    }
    
    public LoopyGame subtract(CanonicalShortGame h)
    {
        return subtract(new CanonicalStopperGame(h));
    }
    
    public LoopyGame ordinalSum(LoopyGame h)
    {
        LoopyGame ordinalSum = new LoopyGame();
        ordinalSum.graph = graph.ordinalSum(h.graph, startVertex);
        ordinalSum.startVertex = graph.getNumVertices() + h.startVertex;
        return ordinalSum;
    }
    
    /**
     * Compares this game with another <code>LoopyGame</code>.
     *
     * @param   h The game to compare this game with.
     * @return  <code>true</code> if this game is less than or equal to
     *          <code>h</code>.
     */
    public boolean leq(LoopyGame h)
    {
        expandMarkers(graph.getNumVertices() * h.graph.getNumVertices());
        
        if (isStopper() && h.isStopper())
        {
            // If both are stoppers we need only check that left can survive with
            // right to move in H-G.
            return leq(new Pregraph(graph), startVertex, new Pregraph(h.graph), h.startVertex, STOPPER);
        }
        else
        {
            return leq(new Pregraph(graph), startVertex, new Pregraph(h.graph), h.startVertex, ONSIDE) &&
                   leq(new Pregraph(graph), startVertex, new Pregraph(h.graph), h.startVertex, OFFSIDE);
        }
    }
    
    /**
     * Compares a side of this game with the same side of another
     * <code>LoopyGame</code>.  This method is particularly useful for
     * comparing games that are not stopper-sided.
     *
     * @param   h The game to compare this game with.
     * @param   onside <code>true</code> for the onside,
     *          <code>false</code> for the offside.
     * @return  <code>true</code> if this game is less than or equal to
     *          <code>h</code> when played in the specified side.
     */
    public boolean leq(LoopyGame h, boolean onside)
    {
        if (isStopper() && h.isStopper())
        {
            // If both are stoppers we need only check that left can survive with
            // right to move in H-G.
            return leq(new Pregraph(graph), startVertex, new Pregraph(h.graph), h.startVertex, STOPPER);
        }
        else
        {
            return leq(new Pregraph(graph), startVertex, new Pregraph(h.graph), h.startVertex, onside ? ONSIDE : OFFSIDE);
        }
    }
    
    /**
     * Compares this game with a stopper-sided game.
     *
     * @return  <code>true</code> if this game is less than or equal to
     *          <code>h</code>.
     */
    /*
    public boolean leq(StopperSidedGame h)
    {
        expandMarkers(graph.getNumVertices() * h.getOffside().graph.getNumVertices());
        
        if (isStopper())
        {
            return leq(new Pregraph(graph), startVertex, new Pregraph(h.getOffside().graph), h.getOffside().startVertex, STOPPER);
        }
        else
        {
            return leq(new Pregraph(graph), startVertex, new Pregraph(h.getOnside().graph), h.getOnside().startVertex, ONSIDE) &&
                   leq(new Pregraph(graph), startVertex, new Pregraph(h.getOffside().graph), h.getOffside().startVertex, OFFSIDE);
        }
    }
     */
    
    /**
     * Compares this game with a stopper-sided game.
     *
     * @return  <code>true</code> if this game is greater than or equal to
     *          <code>h</code>.
     */
    /*
    public boolean geq(StopperSidedGame h)
    {
        if (isStopper())
        {
            return leq(new Pregraph(h.getOnside().graph), h.getOnside().startVertex, new Pregraph(graph), startVertex, STOPPER);
        }
        else
        {
            return leq(new Pregraph(h.getOnside().graph), h.getOnside().startVertex, new Pregraph(graph), startVertex, ONSIDE) &&
                   leq(new Pregraph(h.getOffside().graph), h.getOffside().startVertex, new Pregraph(graph), startVertex, OFFSIDE);
        }
    }
     */
    
    public Bigraph getGraph()
    {
        return graph;
    }
    
    public int getStartVertex()
    {
        return startVertex;
    }
    
    public LoopyGame deriveGame(int newStartVertex)
    {
        return new LoopyGame(graph, newStartVertex);
    }
    
    public boolean isLoopfree()
    {
        return graph.isCycleFree(startVertex);
    }
    
    /**
     * Returns <code>true</code> if this game is a stopper.  A game is a
     * stopper if there is no infinite alternating sequence of moves
     * proceeding from any subposition.
     *
     * @return  <code>true</code> if this game is a stopper.
     */
    public boolean isStopper()
    {
        return graph.isAlternatingCycleFree(startVertex);
    }

    /**
     * Returns <code>true</code> if this game is a plumtree.  A game is a
     * plumtree if its graph contains no cycles of length strictly greater
     * than one; in other words, if its graph is a tree decorated with
     * occasional pass moves.
     *
     * @return  <code>true</code> if this game is a plumtree.
     */
    public boolean isPlumtree()
    {
        return graph.isLongCycleFree(startVertex);
    }
    
    /**
     * Returns <code>true</code> if this game is equal to
     * {@link CanonicalStopperGame#ON ON}.
     *
     * @return  <code>true</code> if this game is equal to <code>ON</code>.
     */
    public boolean isOn()
    {
        return isOn(startVertex);
    }
    
    boolean isOn(int vertex)
    {
        return graph.getNumLeftEdges(vertex) == 1 &&
            graph.getNumRightEdges(vertex) == 0 &&
            graph.getLeftEdgeTarget(vertex, 0) == vertex;
    }
    
    /**
     * Returns <code>true</code> if this game is equal to
     * {@link CanonicalStopperGame#OFF OFF}.
     *
     * @return  <code>true</code> if this game is equal to <code>OFF</code>.
     */
    public boolean isOff()
    {
        return isOff(startVertex);
    }
    
    boolean isOff(int vertex)
    {
        return graph.getNumLeftEdges(vertex) == 0 &&
            graph.getNumRightEdges(vertex) == 1 &&
            graph.getRightEdgeTarget(vertex, 0) == vertex;
    }
    
    public int graphComplexity()
    {
        return graph.getNumVertices();
    }
    
    ////////////////////////////////////////////////////////////////////////
    // Comparison algorithms.

    private final static byte
        LEFT_TO_MOVE = 0x55,           //  01010101
        RIGHT_TO_MOVE = (byte) 0xaa,   //  10101010
        VISITED = 0x03,                //  00000011
        WINNER_KNOWN = 0x0c,           //  00001100
        MOVER_WINS = 0x03,             //  00000011
        VISITED_MASK = 0x0f,           //  00001111
        VISITED_SEC = 0x30,            //  00110000
        GOOD = (byte) 0xc0,            //  11000000
        MOVE_MASK = (byte) 0xff        //  11111111
        ;
    
    private final static int REACHABLE = 0x1, LEFT_AR = 0x4, RIGHT_AR = 0x8, AR_MASK = 0xc;
    
    /*
     * Algorithm for comparing stoppers:
     *
     * We wish to determine whether G <= H for stoppers G,H.  Thus we need to
     * show that Left can play so as never to run out of moves in H-G.  This is
     * true iff Right cannot force play to a state where Left has no moves.
     * The idea is to effect an alternating traversal of the game graph of
     * (-G) + H and determine those vertices from which one of the players can
     * force the other to run out of moves.  If, once the traversal is
     * finished, Right cannot force Left to run out of moves, then we've
     * established G <= H.
     *
     * I'll use the word "state" to refer to a (vertex,mover) pair (V,X).  The
     * outedges of (V,L) are the Left followers of V; the outedges of (V,R) are
     * the Right followers of V.  We start by traversing the state (S,R), where
     * S is the start vertex of (-G) + H.
     *
     * When we traverse a state (V,X):
     * - If (V,X) is marked visited, ignore it and return immediately.
     * - Mark (V,X) as visited.
     * - If some outedge of (V,X) is marked as a win for X, mark (V,X) as a
     *   win for X.
     * - Otherwise, set a counter on (V,X) which is equal to the number of
     *   outedges that are not marked as wins for the opponent.
     * - If this counter is zero, mark (V,X) as a win for the opponent.
     * - If we just marked (V,X) as a win for either player, examine, as a
     *   separate action, all predecessors (W,Y) of (V,X) such that
     *   1) (W,Y) is marked visited, and
     *   2) The winner of (W,Y) has not been determined.
     *   If we marked (V,X) as a win for X, decrement the counter on (W,Y)
     *   and mark (W,Y) as a win for X if the counter reaches 0.  Otherwise,
     *   immediately mark (W,Y) as a win for Y.
     *   Any time this causes the winner of (W,Y) to be determined, repeat
     *   this action with (W,Y) in place of (V,X).
     * - Finally, traverse to all outedges of (V,X).  After examining each
     *   outedge, check whether the winner of (V,X) has been determined, and
     *   if so, return immediately.
     */
    
    private static boolean leq(Pregraph pg, int gVertex, Pregraph ph, int hVertex, int side)
    {
        return leq(pg, pg.reverse(), gVertex, ph, ph.reverse(), hVertex, side);
    }
    
    private static boolean leq
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex, int side)
    {
        expandMarkers(pg.getNumVertices() * ph.getNumVertices());
        
        markStrongWins(pg, pgReverse, gVertex, ph, phReverse, hVertex, RIGHT_TO_MOVE, true);
        
        if (side != STOPPER)
        {
            boolean changed;
            do
            {
                markGoodStates(pg, pgReverse, gVertex, ph, phReverse, hVertex, RIGHT_TO_MOVE, side == ONSIDE, true);
                changed = markBadStatesLost(pg, pgReverse, gVertex, ph, phReverse, hVertex, true);
            } while (changed);
        }
        
        boolean leq = markerIndicatesLeq(pg, gVertex, ph, hVertex);
        if (side == STOPPER)
        {
            clearMarkers(pg, gVertex, ph, hVertex);
        }
        else
        {
            Arrays.fill(markers, 0, pg.getNumVertices() * ph.getNumVertices(), (byte) 0);
        }
        return leq;
    }
    
    private static void markAllStrongWins
        (Pregraph pg, Pregraph pgReverse, Pregraph ph, Pregraph phReverse)
    {
        for (int gVertex = 0; gVertex < pg.getNumVertices(); gVertex++)
        {
            for (int hVertex = 0; hVertex < ph.getNumVertices(); hVertex++)
            {
                markStrongWins(pg, pgReverse, gVertex, ph, phReverse, hVertex, LEFT_TO_MOVE, false);
                markStrongWins(pg, pgReverse, gVertex, ph, phReverse, hVertex, RIGHT_TO_MOVE, false);
            }
        }
    }
    
    private static void markStrongWins
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex, int mover, boolean recurse)
    {
        int markerPos = gVertex * ph.getNumVertices() + hVertex;
        if ((markers[markerPos] & mover & VISITED_MASK) != 0)
        {
            // Already visited this vertex with this player to move.
            return;
        }
        
        markers[markerPos] |= (mover & VISITED);
        boolean foundWinningMove = false, foundExit = false;
        int exits = 0;
        
        int[] gEdges = (mover == LEFT_TO_MOVE ? pg.rightEdges[gVertex] : pg.leftEdges[gVertex]);
        int[] hEdges = (mover == LEFT_TO_MOVE ? ph.leftEdges[hVertex] : ph.rightEdges[hVertex]);
        
        // The following loops fill two functions:
        //  (i) Determine if we've already detected a winning move from here.
        // (ii) Set the counter.  If we haven't yet determined the winner, then
        //      the counter indicates the number of options that have not yet
        //      been proven to be wins for the opponent.
        
        for (int i = 0; i < gEdges.length; i++)
        {
            if (gEdges[i] != -1)
            {
                // The target will be an opposing state.  We want the target
                // marker to indicate a win for the opponent, since *WE* will
                // be the opponent.
                int targetMarker = (markers[gEdges[i] * ph.getNumVertices() + hVertex] & (mover ^ MOVE_MASK));
                if ((targetMarker & WINNER_KNOWN) == 0)
                {
                    foundExit = true;
                }
                else if ((targetMarker & MOVER_WINS) == 0)
                {
                    foundWinningMove = true;
                    break;
                }
            }
        }
        
        if (!foundWinningMove) for (int i = 0; i < hEdges.length; i++)
        {
            if (hEdges[i] != -1)
            {
                int targetMarker = (markers[gVertex * ph.getNumVertices() + hEdges[i]] & (mover ^ MOVE_MASK));
                if ((targetMarker & WINNER_KNOWN) == 0)
                {
                    foundExit = true;
                }
                else if ((targetMarker & MOVER_WINS) == 0)
                {
                    foundWinningMove = true;
                    break;
                }
            }
        }
        
        if (foundWinningMove)
        {
            // Set this vertex as a win for mover.
            markers[markerPos] &= ~(mover & VISITED);
            markers[markerPos] |= (mover & (WINNER_KNOWN | MOVER_WINS));
            alterPredecessorsOfWonState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, true);
            if (recurse)
            {
                return;
            }
        }
        else if (!foundExit)
        {
            markers[markerPos] &= ~(mover & VISITED);
            markers[markerPos] |= (mover & WINNER_KNOWN);
            alterPredecessorsOfWonState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, false);
            if (recurse)
            {
                return;
            }
        }
        
        if (recurse)
        {
            for (int i = 0; i < gEdges.length; i++)
            {
                if (gEdges[i] != -1)
                {
                    markStrongWins(pg, pgReverse, gEdges[i], ph, phReverse, hVertex, mover ^ MOVE_MASK, true);
                    if ((markers[markerPos] & mover & WINNER_KNOWN) != 0)
                    {
                        // Winner is determined, so we can stop.
                        return;
                    }
                }
            }
            for (int i = 0; i < hEdges.length; i++)
            {
                if (hEdges[i] != -1)
                {
                    markStrongWins(pg, pgReverse, gVertex, ph, phReverse, hEdges[i], mover ^ MOVE_MASK, true);
                    if ((markers[markerPos] & mover & WINNER_KNOWN) != 0)
                    {
                        return;
                    }
                }
            }
        }
        return;
    }
    
    private static void alterPredecessorsOfWonState
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex,
         int mover, boolean winForMover)
    {
        int[] gPred = (mover == LEFT_TO_MOVE ? pgReverse.leftEdges[gVertex] : pgReverse.rightEdges[gVertex]);
        int[] hPred = (mover == LEFT_TO_MOVE ? phReverse.rightEdges[hVertex] : phReverse.leftEdges[hVertex]);
        
        for (int i = 0; i < gPred.length; i++)
        {
            if (gPred[i] != -1)
            {
                alterForWonFollower(pg, pgReverse, gPred[i], ph, phReverse, hVertex, mover ^ MOVE_MASK, winForMover);
            }
        }
        for (int i = 0; i < hPred.length; i++)
        {
            if (hPred[i] != -1)
            {
                alterForWonFollower(pg, pgReverse, gVertex, ph, phReverse, hPred[i], mover ^ MOVE_MASK, winForMover);
            }
        }
    }
    
    private static void alterForWonFollower
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex,
         int mover, boolean followerWonForMover)
    {
        int markerPos = gVertex * ph.getNumVertices() + hVertex;
        if ((markers[markerPos] & mover & VISITED_MASK) != 0 && (markers[markerPos] & mover & WINNER_KNOWN) == 0)
        {
            // We've visited this state before, but its winner is still
            // unknown and we just obtained new information about it.
            if (followerWonForMover)
            {
                boolean foundExit = false;
                int[] gEdges = (mover == LEFT_TO_MOVE ? pg.rightEdges[gVertex] : pg.leftEdges[gVertex]);
                int[] hEdges = (mover == LEFT_TO_MOVE ? ph.leftEdges[hVertex] : ph.rightEdges[hVertex]);
                for (int i = 0; i < gEdges.length; i++)
                {
                    if (gEdges[i] != -1 &&
                        (markers[gEdges[i] * ph.getNumVertices() + hVertex] & (mover ^ MOVE_MASK) & WINNER_KNOWN) == 0)
                    {
                        foundExit = true;
                        break;
                    }
                }
                if (!foundExit) for (int i = 0; i < hEdges.length; i++)
                {
                    if (hEdges[i] != -1 &&
                        (markers[gVertex * ph.getNumVertices() + hEdges[i]] & (mover ^ MOVE_MASK) & WINNER_KNOWN) == 0)
                    {
                        foundExit = true;
                        break;
                    }
                }
                if (!foundExit)
                {
                    // All exits have been eliminated; it's a loss for the player
                    // to move.
                    markers[markerPos] &= ~(mover & VISITED);
                    markers[markerPos] |= (mover & WINNER_KNOWN);
                    alterPredecessorsOfWonState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, false);
                }
            }
            else
            {
                markers[markerPos] &= ~(mover & VISITED);
                markers[markerPos] |= (mover & (WINNER_KNOWN | MOVER_WINS));
                alterPredecessorsOfWonState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, true);
            }
        }
    }
    
    private static void clearMarkers(Pregraph pg, int gVertex, Pregraph ph, int hVertex)
    {
        if (gVertex == -1 || hVertex == -1)
        {
            return;
        }
        
        int markerPos = gVertex * ph.getNumVertices() + hVertex;
        boolean left = ((markers[markerPos] & LEFT_TO_MOVE) != 0);
        boolean right = ((markers[markerPos] & RIGHT_TO_MOVE) != 0);
        markers[markerPos] &= ~MOVE_MASK;
        
        if (left)
        {
            for (int i = 0; i < ph.leftEdges[hVertex].length; i++)
            {
                clearMarkers(pg, gVertex, ph, ph.leftEdges[hVertex][i]);
            }
            for (int i = 0; i < pg.rightEdges[gVertex].length; i++)
            {
                clearMarkers(pg, pg.rightEdges[gVertex][i], ph, hVertex);
            }
        }
        if (right)
        {
            for (int i = 0; i < ph.rightEdges[hVertex].length; i++)
            {
                clearMarkers(pg, gVertex, ph, ph.rightEdges[hVertex][i]);
            }
            for (int i = 0; i < pg.leftEdges[gVertex].length; i++)
            {
                clearMarkers(pg, pg.leftEdges[gVertex][i], ph, hVertex);
            }
        }
    }
    
    private static boolean markerIndicatesLeq(Pregraph pg, int gVertex, Pregraph ph, int hVertex)
    {
        return (markers[gVertex * ph.getNumVertices() + hVertex] & RIGHT_TO_MOVE & (WINNER_KNOWN | MOVER_WINS)) !=
            (RIGHT_TO_MOVE & (WINNER_KNOWN | MOVER_WINS));
    }
    
    private static int maxPassesObserved = 3;
    
    private static void markStatesWonForRight
        (Pregraph pg, Pregraph pgReverse, Pregraph ph, Pregraph phReverse, int side)
    {
        expandMarkers(pg.getNumVertices() * ph.getNumVertices());
        
        // First determine all states from which some player has a forced win.
        markAllStrongWins(pg, pgReverse, ph, phReverse);
        
        if (side != STOPPER)
        {
            boolean changed;
            int passes = 0;
            do
            {
                markAllGoodStates(pg, pgReverse, ph, phReverse, side == ONSIDE);
                changed = markAllBadStatesLost(pg, pgReverse, ph, phReverse);
                passes++;
            } while (changed);
            if (passes > maxPassesObserved)
            {
                maxPassesObserved = passes;
//                Context.getActiveContext().getLogger().finer
//                    ("Needed " + passes + " passes for the general state-marking algorithm.");
            }
        }
    }
    
    private static void markAllGoodStates(Pregraph pg, Pregraph pgReverse, Pregraph ph, Pregraph phReverse, boolean onside)
    {
        for (int gVertex = 0; gVertex < pg.getNumVertices(); gVertex++)
        {
            for (int hVertex = 0; hVertex < ph.getNumVertices(); hVertex++)
            {
                markGoodStates(pg, pgReverse, gVertex, ph, phReverse, hVertex, LEFT_TO_MOVE, onside, false);
                markGoodStates(pg, pgReverse, gVertex, ph, phReverse, hVertex, RIGHT_TO_MOVE, onside, false);
            }
        }
    }
    
    private static void markGoodStates
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex,
         int mover, boolean onside, boolean recursive)
    {
        int markerPos = gVertex * ph.getNumVertices() + hVertex;
        if ((markers[markerPos] & mover & (VISITED_SEC | WINNER_KNOWN)) != 0)
        {
            // Already visited this state on this iteration, *or* the winner
            // was determined on a previous iteration.
            return;
        }
        markers[markerPos] |= (mover & VISITED_SEC);
        
        int[] gEdges = (mover == LEFT_TO_MOVE ? pg.rightEdges[gVertex] : pg.leftEdges[gVertex]);
        int[] hEdges = (mover == LEFT_TO_MOVE ? ph.leftEdges[hVertex] : ph.rightEdges[hVertex]);

        if (mover == LEFT_TO_MOVE)
        {
            boolean foundGoodMove = false;
            
            for (int i = 0; i < gEdges.length; i++)
            {
                if (gEdges[i] != -1)
                {
                    int targetPos = gEdges[i] * ph.getNumVertices() + hVertex;
                    if ((markers[targetPos] & RIGHT_TO_MOVE & WINNER_KNOWN) == 0 &&
                        (!onside || (markers[targetPos] & RIGHT_TO_MOVE & GOOD) != 0))
                    {
                        // The target is not known to be a win for Right, and either:
                        //  (i) We're playing in the offside (so Left is eager to play in G); or
                        // (ii) We've already established that this follower is good.
                        // (Note that if the winner is known, it *must* be a win for Right,
                        // since otherwise we'd have marked this vertex as a win for Left
                        // on a previous iteration.)
                        foundGoodMove = true;
                        break;
                    }
                }
            }
            if (!foundGoodMove) for (int i = 0; i < hEdges.length; i++)
            {
                if (hEdges[i] != -1)
                {
                    int targetPos = gVertex * ph.getNumVertices() + hEdges[i];
                    if ((markers[targetPos] & RIGHT_TO_MOVE & WINNER_KNOWN) == 0 &&
                        (onside || (markers[targetPos] & RIGHT_TO_MOVE & GOOD) != 0))
                    {
                        foundGoodMove = true;
                        break;
                    }
                }
            }
            if (foundGoodMove)
            {
                markers[markerPos] |= (LEFT_TO_MOVE & GOOD);
                alterPredecessorsOfGoodState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, onside);
            }
        }
        else
        {
            if (!rightHasPutativeExit(pg, gVertex, ph, hVertex, onside))
            {
                // This vertex is good.
                markers[markerPos] |= (RIGHT_TO_MOVE & GOOD);
                alterPredecessorsOfGoodState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, onside);
            }
        }
        
        if (recursive)
        {
            for (int i = 0; i < gEdges.length; i++)
            {
                if (gEdges[i] != -1)
                {
                    markGoodStates(pg, pgReverse, gEdges[i], ph, phReverse, hVertex, mover ^ MOVE_MASK, onside, true);
                }
            }
            for (int i = 0; i < hEdges.length; i++)
            {
                if (hEdges[i] != -1)
                {
                    markGoodStates(pg, pgReverse, gVertex, ph, phReverse, hEdges[i], mover ^ MOVE_MASK, onside, true);
                }
            }
        }
    }
    
    private static boolean rightHasPutativeExit
        (Pregraph pg, int gVertex, Pregraph ph, int hVertex, boolean onside)
    {
        if (onside)
        {
            // We're playing the onside, so search G for putative Right exits.
            int[] gEdges = pg.leftEdges[gVertex];
            for (int i = 0; i < gEdges.length; i++)
            {
                if (gEdges[i] != -1)
                {
                    int targetPos = gEdges[i] * ph.getNumVertices() + hVertex;
                    if ((markers[targetPos] & LEFT_TO_MOVE & (WINNER_KNOWN | GOOD)) == 0)
                    {
                        // The following conditions hold:
                        //  (i) This follower is not an immediate win for Left;
                        // (ii) This follower has not been proven good.
                        // So we've found a (putative) exit for Right.
                        // Note: We don't have to worry about *winning* moves for Right,
                        // since if there is one, this state would already be marked as a
                        // Right win.
                        return true;
                    }
                }
            }
        }
        else
        {
            int[] hEdges = ph.rightEdges[hVertex];
            for (int i = 0; i < hEdges.length; i++)
            {
                if (hEdges[i] != -1)
                {
                    int targetPos = gVertex * ph.getNumVertices() + hEdges[i];
                    if ((markers[targetPos] & LEFT_TO_MOVE & (WINNER_KNOWN | GOOD)) == 0)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static void alterPredecessorsOfGoodState
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex,
         int mover, boolean onside)
    {
        // Called when the specified state becomes GOOD for the first time.
        if (onside)
        {
            int[] gPred = (mover == LEFT_TO_MOVE ? pgReverse.leftEdges[gVertex] : pgReverse.rightEdges[gVertex]);
            for (int i = 0; i < gPred.length; i++)
            {
                if (gPred[i] != -1)
                {
                    alterForGoodFollower(pg, pgReverse, gPred[i], ph, phReverse, hVertex, mover ^ MOVE_MASK, onside);
                }
            }
        }
        else
        {
            int[] hPred = (mover == LEFT_TO_MOVE ? phReverse.rightEdges[hVertex] : phReverse.leftEdges[hVertex]);
            for (int i = 0; i < hPred.length; i++)
            {
                if (hPred[i] != -1)
                {
                    alterForGoodFollower(pg, pgReverse, gVertex, ph, phReverse, hPred[i], mover ^ MOVE_MASK, onside);
                }
            }
        }
    }
    
    private static void alterForGoodFollower
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex,
         int mover, boolean onside)
    {
        int markerPos = gVertex * ph.getNumVertices() + hVertex;
        
        if ((markers[markerPos] & mover & VISITED_SEC) == 0 ||
            (markers[markerPos] & mover & WINNER_KNOWN) != 0)
        {
            return;
        }
        
        if (mover == LEFT_TO_MOVE &&
            (markers[markerPos] & LEFT_TO_MOVE & GOOD) == 0)
        {
            // We just found out that this state is good.
            markers[markerPos] |= (LEFT_TO_MOVE & GOOD);
            alterPredecessorsOfGoodState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, onside);
        }
        else if (mover == RIGHT_TO_MOVE &&
            !rightHasPutativeExit(pg, gVertex, ph, hVertex, onside))
        {
            // We just found out that this state is good.
            markers[markerPos] |= (RIGHT_TO_MOVE & GOOD);
            alterPredecessorsOfGoodState(pg, pgReverse, gVertex, ph, phReverse, hVertex, mover, onside);
        }
    }
    
    private static boolean markAllBadStatesLost
        (Pregraph pg, Pregraph pgReverse, Pregraph ph, Pregraph phReverse)
    {
        boolean changed = false;
        for (int gVertex = 0; gVertex < pg.getNumVertices(); gVertex++)
        {
            for (int hVertex = 0; hVertex < ph.getNumVertices(); hVertex++)
            {
                if (markBadStatesLost(pg, pgReverse, gVertex, ph, phReverse, hVertex, false))
                {
                    changed = true;
                }
            }
        }
        return changed;
    }
    
    private static boolean markBadStatesLost
        (Pregraph pg, Pregraph pgReverse, int gVertex, Pregraph ph, Pregraph phReverse, int hVertex, boolean recursive)
    {
        if (gVertex == -1 || hVertex == -1)
        {
            return false;
        }
        
        int markerPos = gVertex * ph.getNumVertices() + hVertex;
        
        boolean left = ((markers[markerPos] & LEFT_TO_MOVE & VISITED_SEC) != 0);
        boolean right = ((markers[markerPos] & RIGHT_TO_MOVE & VISITED_SEC) != 0);
        markers[markerPos] &= ~(VISITED_SEC);
        
        boolean changed = false;
        
        if (left)
        {
            // We visited this vertex as left.
            if ((markers[markerPos] & LEFT_TO_MOVE & GOOD) == 0 &&
                (markers[markerPos] & LEFT_TO_MOVE & WINNER_KNOWN) == 0)
            {
                // This state is bad, and the winner is not determined,
                // so mark it as losing for Left.
                markers[markerPos] &= ~(LEFT_TO_MOVE & VISITED);
                markers[markerPos] |= (LEFT_TO_MOVE & WINNER_KNOWN);
                alterPredecessorsOfWonState(pg, pgReverse, gVertex, ph, phReverse, hVertex, LEFT_TO_MOVE, false);
                changed = true;
            }
            markers[markerPos] &= ~(GOOD & LEFT_TO_MOVE);
        }
        if (right)
        {
            if ((markers[markerPos] & RIGHT_TO_MOVE & GOOD) == 0 &&
                (markers[markerPos] & RIGHT_TO_MOVE & WINNER_KNOWN) == 0)
            {
                // This state is bad, and the winner is not determined,
                // so mark it as losing for Left.
                markers[markerPos] &= ~(RIGHT_TO_MOVE & VISITED);
                markers[markerPos] |= (RIGHT_TO_MOVE & (WINNER_KNOWN | MOVER_WINS));
                alterPredecessorsOfWonState(pg, pgReverse, gVertex, ph, phReverse, hVertex, RIGHT_TO_MOVE, true);
                changed = true;
            }
            markers[markerPos] &= ~(GOOD & RIGHT_TO_MOVE);
        }
        if (recursive)
        {
            if (left)
            {
                for (int i = 0; i < pg.rightEdges[gVertex].length; i++)
                {
                    if (pg.rightEdges[gVertex][i] != -1 &&
                        markBadStatesLost(pg, pgReverse, pg.rightEdges[gVertex][i], ph, phReverse, hVertex, true))
                    {
                        changed = true;
                    }
                }
                for (int i = 0; i < ph.leftEdges[hVertex].length; i++)
                {
                    if (ph.leftEdges[hVertex][i] != -1 &&
                        markBadStatesLost(pg, pgReverse, gVertex, ph, phReverse, ph.leftEdges[hVertex][i], true))
                    {
                        changed = true;
                    }
                }
            }
            if (right)
            {
                for (int i = 0; i < pg.leftEdges[gVertex].length; i++)
                {
                    if (pg.leftEdges[gVertex][i] != -1 &&
                        markBadStatesLost(pg, pgReverse, pg.leftEdges[gVertex][i], ph, phReverse, hVertex, true))
                    {
                        changed = true;
                    }
                }
                for (int i = 0; i < ph.rightEdges[hVertex].length; i++)
                {
                    if (ph.rightEdges[hVertex][i] != -1 &&
                        markBadStatesLost(pg, pgReverse, gVertex, ph, phReverse, ph.rightEdges[hVertex][i], true))
                    {
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
    
    ////////////////////////////////////////////////////////////////////////
    // Simplification.

    /**
     * Calculates the canonical form of this game.  For details, see the
     * {@link CanonicalStopperGame} class.
     *
     * @return  The canonical form of this game.
     * @throws  NotStopperException This game is not a stopper.
     */
    public CanonicalStopperGame canonicalizeStopper() throws NotStopperException
    {
        if (graph.isCycleFree(startVertex))
        {
            return new CanonicalStopperGame(canonicalize(startVertex));
        }
        else if (isStopper())
        {
            return canonicalizeStopperInternal();
        }
        else
        {
            throw new NotStopperException();
        }
    }
    
    private CanonicalStopperGame canonicalizeStopperInternal()
    {
        CanonicalStopperGame g = new CanonicalStopperGame();
        g.graph = simplifyGraph(graph, null, STOPPER, startVertex);
        g.startVertex = 0;
        return g;
    }
    
    public static LoopyGame constructSimplifiedGame(LoopyGame.Node node, boolean onside)
    {
        if (node.graphInfo == null || !node.graphInfo.valid)
        {
            initialize(node);
        }
        int side = (onside ? ONSIDE : OFFSIDE);
        if (node.graphInfo.simplifiedGraphs[side] == null)
        {
            //Context.getActiveContext().getLogger().finer("Building simplified graph.");
            long tm = System.currentTimeMillis();
            node.graphInfo.simplifiedGraphs[side] = simplifyGraph
                (node.graphInfo.graph, node.graphInfo.canonical, side, -1);
            tm = System.currentTimeMillis() - tm;
            if (tm >= 1000L)
            {
//                Context.getActiveContext().getLogger().finer
//                    ("Built simplified graph in " + tm + " ms.");
            }
        }
        
        if (node.graphInfo.simplifiedGraphs[side].isAlternatingCycleFree(node.startVertex))
        {
            CanonicalStopperGame g = new CanonicalStopperGame();
            Pregraph pg = new Pregraph(node.graphInfo.simplifiedGraphs[side].pack(node.startVertex));
            //Context.getActiveContext().getLogger().finer("Fusing an ACF graph.");
            fuse(pg, pg.reverse(), STOPPER, 0);
            //Context.getActiveContext().getLogger().finer("Finished fusing ACF graph.");
            g.graph = new Bigraph(pg.leftEdges, pg.rightEdges, 0);
            g.startVertex = 0;
            return g;
        }
        
        //Context.getActiveContext().getLogger().finer
        //    ("Simplified graph contains alternating cycles.  Attempting local simplifications.");
        
        // There are some vertices in the graph not equivalent to stoppers.  But there is still hope.
        
        Bigraph simplerGraph = simplifyGraph
            (node.graphInfo.simplifiedGraphs[side], node.graphInfo.canonical, side, node.startVertex);
        //Context.getActiveContext().getLogger().finer(
        //    simplerGraph.isAlternatingCycleFree(0) ?
        //    "Local simplifications produced an alternating cycle-free graph." :
        //    "There are still alternating cycles.  The graph is a carousel!"
        //    );
        LoopyGame g = (simplerGraph.isAlternatingCycleFree(0) ? new CanonicalStopperGame() : new LoopyGame());
        g.graph = simplerGraph;
        g.startVertex = 0;
        return g;
    }
    
    private static Bigraph simplifyGraph(Bigraph graph, boolean[] canonical, int side, int specificVertex)
    {
        //if (side != STOPPER) Context.getActiveContext().getLogger().finer("Entered simplifyGraph: " + side + "," + specificVertex);

        long tm, basicTm = 0L, unravelTm = 0L, secondBasicTm = 0L, secondElimTm = 0L;
        
        boolean acf =
            (specificVertex == -1 && graph.isAlternatingCycleFree() ||
             specificVertex != -1 && graph.isAlternatingCycleFree(specificVertex));
        
        Pregraph newGraph;
        
        if (specificVertex == -1)
        {
            newGraph = new Pregraph(graph);
        }
        else
        {
            newGraph = new Pregraph(graph.pack(specificVertex));
            specificVertex = 0;
        }
        
        expandMarkers(newGraph.getNumVertices() * newGraph.getNumVertices());
        updateReachable(newGraph, specificVertex);
        
        if (side != STOPPER)
        {
            // Perform obvious reductions.
            for (int vertex = 0; vertex < newGraph.leftEdges.length; vertex++)
            {
                if (specificVertex == -1 || (reachMarkers[vertex] & REACHABLE) != 0)
                {
                    eliminateObviouslyIrrelevantEdges(newGraph, vertex, side);
                }
            }
        }
        
        Pregraph reverse = newGraph.reverse();
        
        if (acf)
        {
            side = STOPPER;
        }
        //if (side != STOPPER && specificVertex == -1) Context.getActiveContext().getLogger().finer
        //    ("Applying basic simplifcations (" + (getUsedMemory() >> 10) + "K) (" + ((newGraph.byteSize() + reverse.byteSize()) >> 10) + "K).");
        tm = System.currentTimeMillis();
        applyBasicSimplifications(newGraph, reverse, canonical, side, specificVertex);
        basicTm = System.currentTimeMillis() - tm;
        //if (side != STOPPER && specificVertex == -1) Context.getActiveContext().getLogger().finer
        //    ("Finished basic simplifications (" + (getUsedMemory() >> 10) + "K) (" + ((newGraph.byteSize() + reverse.byteSize()) >> 10) + "K).");
        // If there are cyclic edges, try to unravel them.
        
        if (side != STOPPER)
        {
            Pregraph
                testGraph = (Pregraph) newGraph.clone(),
                tgReverse = (Pregraph) reverse.clone();
                
            tm = System.currentTimeMillis();
            
            for (int vertex = 0; vertex < newGraph.leftEdges.length; vertex++)
            {
                if (specificVertex == -1 || (reachMarkers[vertex] & REACHABLE) != 0)
                {
                    //Context.getActiveContext().getLogger().finer("Eliminating irrelevant edges for vertex " + vertex + ".");
                    eliminateIrrelevantCyclicEdges
                        (newGraph, reverse, testGraph, tgReverse, vertex, side, specificVertex, false);
                    //Context.getActiveContext().getLogger().finer("Finished eliminating irrelevant edges for vertex " + vertex + ".");
                    updateReachable(newGraph, specificVertex);
                }
            }
            
            testGraph = tgReverse = null;       // Free up a little memory.
            
            //if (specificVertex == -1)
            //    Context.getActiveContext().getLogger().finer("Starting unraveling process (" + (getUsedMemory() >> 10) + "K).");
            boolean unraveled = unravelFully(newGraph, reverse, side == ONSIDE, specificVertex);
            //if (specificVertex == -1)
            //    Context.getActiveContext().getLogger().finer("Finished unraveling process (" + (getUsedMemory() >> 10) + "K).");
            
            unravelTm = System.currentTimeMillis() - tm;
            
            // Apply another round of basic simplifications to clear out any newly-created
            // reversible moves.  This is crucial, since we intend to apply fusion directly
            // to the resulting graph.  If unraveling worked, we can use the faster STOPPER
            // simplifications.
            //if (specificVertex == -1)
            //    Context.getActiveContext().getLogger().finer("Applying second round of basic simplifcations.");
            tm = System.currentTimeMillis();
            applyBasicSimplifications(newGraph, reverse, canonical, unraveled ? STOPPER : side, specificVertex);
            secondBasicTm = System.currentTimeMillis() - tm;
            //if (specificVertex == -1)
            //    Context.getActiveContext().getLogger().finer("Finished applying second round of basic simplifications.");
        }
        
        if (side != STOPPER)
        {
            // Unraveling failed - the graph is a genuine non-stopper.
            // As a last step, eliminate any remaining irrelevant edges.
            // (Maybe we should simplify further by iteratively eliminating
            // irrelevant edges and bypassing reversible moves until there is
            // no further change?  It's really sad that there's no simplicity
            // theorem here . . . it's not clear when to stop.)
            
            Pregraph
                testGraph = (Pregraph) newGraph.clone(),
                tgReverse = (Pregraph) reverse.clone();
            
            tm = System.currentTimeMillis();
            for (int vertex = 0; vertex < newGraph.leftEdges.length; vertex++)
            {
                if (specificVertex == -1 || (reachMarkers[vertex] & REACHABLE) != 0)
                {
                    //Context.getActiveContext().getLogger().finer("Eliminating irrelevant edges (2nd) for vertex " + vertex + ".");
                    eliminateIrrelevantCyclicEdges
                        (newGraph, reverse, testGraph, tgReverse, vertex, side, specificVertex, true);
                    //Context.getActiveContext().getLogger().finer("Finished eliminating irrelevant edges (2nd) for vertex " + vertex + ".");
                    updateReachable(newGraph, specificVertex);
                }
            }
            secondElimTm = System.currentTimeMillis() - tm;
        }
        
        if (specificVertex == -1)
        {
            if (basicTm + unravelTm + secondBasicTm + secondElimTm >= 1000L)
            {
//                Context.getActiveContext().getLogger().finer
//                    ("Basic / Unraveling / 2nd Basic / 2nd Elim Times: " +
//                     basicTm + " / " + unravelTm + " / " + secondBasicTm + " / " + secondElimTm + " ms.");
            }
            return new Bigraph(newGraph.leftEdges, newGraph.rightEdges);
        }
        else
        {
            // Apply fusion.
            //if (side != STOPPER) Context.getActiveContext().getLogger().finer("Applying fusion.");
            fuse(newGraph, reverse, side, specificVertex);
            //if (side != STOPPER) Context.getActiveContext().getLogger().finer("Finished applying fusion.");
            return new Bigraph(newGraph.leftEdges, newGraph.rightEdges, specificVertex);
        }
    }
    
    private static void applyBasicSimplifications
        (Pregraph pg, Pregraph reverse, boolean[] canonical, int side, int specificVertex)
    {
        markStatesWonForRight(pg, reverse, pg, reverse, side);
        for (int vertex = 0; vertex < pg.leftEdges.length; vertex++)
        {
            if (specificVertex == -1 || (reachMarkers[vertex] & REACHABLE) != 0)
            {
                bypassReversibleOptionsL(pg, reverse, vertex, side);
                bypassReversibleOptionsR(pg, reverse, vertex, side);
                eliminateDominatedOptions(pg, reverse, vertex, side);
                updateReachable(pg, specificVertex);
            }
        }
        Arrays.fill(markers, 0, pg.getNumVertices() * pg.getNumVertices(), (byte) 0);
    }
    
    static Bigraph canonicalizeStopperGraph(Bigraph graph, int startVertex)
    {
        Pregraph newGraph = new Pregraph(graph.pack(startVertex));
        // Note: In F&G, reversing the graph accounts for 15% of total
        // computation time.  TODO: Find ways to reduce this impact.
        Pregraph reverse = newGraph.reverse();
        expandMarkers(newGraph.getNumVertices() * newGraph.getNumVertices());
        
        updateReachable(newGraph, 0);
        for (int vertex = 0; vertex < newGraph.getNumVertices(); vertex++)
        {
            if ((reachMarkers[vertex] & REACHABLE) != 0)
            {
                bypassReversibleOptionsL(newGraph, reverse, vertex, STOPPER);
                bypassReversibleOptionsR(newGraph, reverse, vertex, STOPPER);
                eliminateDominatedOptions(newGraph, reverse, vertex, STOPPER);
                updateReachable(newGraph, 0);
            }
        }
        
        fuse(newGraph, reverse, STOPPER, 0);
        
        return new Bigraph(newGraph.leftEdges, newGraph.rightEdges, 0);
    }
    
    private static void updateReachable(Pregraph pg, int startVertex)
    {
        if (startVertex == -1)
        {
            return;
        }
        expandReachMarkers(pg.leftEdges.length);
        for (int i = 0; i < pg.getNumVertices(); i++)
        {
            reachMarkers[i] &= ~REACHABLE;
        }
        markReachable(pg, startVertex);
    }
    
    private static void markReachable(Pregraph pg, int vertex)
    {
        if ((reachMarkers[vertex] & REACHABLE) != 0)
        {
            return;
        }
        reachMarkers[vertex] |= REACHABLE;
        for (int i = 0; i < pg.leftEdges[vertex].length; i++)
        {
            if (pg.leftEdges[vertex][i] != -1)
            {
                markReachable(pg, pg.leftEdges[vertex][i]);
            }
        }
        for (int i = 0; i < pg.rightEdges[vertex].length; i++)
        {
            if (pg.rightEdges[vertex][i] != -1)
            {
                markReachable(pg, pg.rightEdges[vertex][i]);
            }
        }
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
    
    private static int clearFromArray(int[] array, int value)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value)
            {
                array[i] = -1;
                return i;
            }
        }
        return -1;
    }
    
    private static int[] expandArray(int[] array, int value)
    {
        int[] newArray = new int[array.length+1];
        for (int i = 0; i < array.length; i++)
        {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        return newArray;
    }
    
    private static int[] packArray(int[] array)
    {
        int count = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] != -1)
            {
                count++;
            }
        }
        if (count == array.length)
        {
            return array;
        }
        int[] packedArray = new int[count];
        for (int i = 0, nextIndex = 0; i < array.length; i++)
        {
            if (array[i] != -1)
            {
                packedArray[nextIndex] = array[i];
                nextIndex++;
            }
        }
        return packedArray;
    }
    
    private static void eliminateObviouslyIrrelevantEdges(Pregraph pg, int vertex, int side)
    {
        // First perform some obvious fast simplifications:
        // (i)   Eliminate Left (Right) edges dominated by on (off)
        // (ii)  Eliminate Left (Right) edges to off (on)
        // (iii) Break any alternating 2-cycles.
        for (int i = 0; i < pg.leftEdges[vertex].length; i++)
        {
            int target = pg.leftEdges[vertex][i];
            if (target != -1)
            {
                if (pg.isOn(target) || side == ONSIDE && pg.isDud(target))
                {
                    // Left has a move to on; all others are irrelevant.
                    isolateInArray(pg.leftEdges[vertex], i);
                    break;
                }
                else if (pg.isOff(target) || side == OFFSIDE && pg.isDud(target))
                {
                    // Left can *never* gain by moving to off.
                    pg.leftEdges[vertex][i] = -1;
                }
                else if (side == OFFSIDE && arrayContains(pg.rightEdges[target], vertex))
                {
                    // Right can immediately counteract Left's move, so it's irrelevant in the offside.
                    pg.leftEdges[vertex][i] = -1;
                }
            }
        }
        for (int i = 0; i < pg.rightEdges[vertex].length; i++)
        {
            int target = pg.rightEdges[vertex][i];
            if (target != -1)
            {
                if (pg.isOff(target) || side == OFFSIDE && pg.isDud(target))
                {
                    isolateInArray(pg.rightEdges[vertex], i);
                    break;
                }
                else if (pg.isOn(target) || side == ONSIDE && pg.isDud(target))
                {
                    pg.rightEdges[vertex][i] = -1;
                }
                else if (side == ONSIDE && arrayContains(pg.leftEdges[target], vertex))
                {
                    pg.rightEdges[vertex][i] = -1;
                }
            }
        }
    }
        
    private static boolean bypassReversibleOptionsL(Pregraph pg, Pregraph reverse, int vertex, int side)
    {
        boolean bypassed = false;
        int[] leftEdges = pg.leftEdges[vertex];
        
        // Reverse options for left.
        for (int i = 0; i < leftEdges.length; i++)
        {
            if (leftEdges[i] != -1)
            for (int j = 0; j < pg.rightEdges[leftEdges[i]].length; j++)
            {
                int gLR = pg.rightEdges[leftEdges[i]][j];
                if (gLR != -1 &&
                    (side == STOPPER || !pg.isAlternatingReachable(gLR, LEFT_TO_MOVE, leftEdges[i], RIGHT_TO_MOVE)) &&
                    markerIndicatesLeq(pg, gLR, pg, vertex))
                {
                    // i is reversible!
                    bypassed = true;
                    int[] extraLeftEdges = pg.leftEdges[gLR];
                    int[] newLeftEdges = new int[leftEdges.length - 1 + extraLeftEdges.length];
                    for (int k = 0; k < i; k++)
                    {
                        newLeftEdges[k] = leftEdges[k];
                    }
                    // Update the reverse graph to indicate that we've removed this edge.
                    clearFromArray(reverse.leftEdges[leftEdges[i]], vertex);
                    for (int k = i+1; k < leftEdges.length; k++)
                    {
                        newLeftEdges[k-1] = leftEdges[k];
                    }
                    for (int k = 0; k < extraLeftEdges.length; k++)
                    {
                        if (extraLeftEdges[k] == -1 ||
                            arrayContains(leftEdges, extraLeftEdges[k]))
                        {
                            newLeftEdges[k+leftEdges.length-1] = -1;
                        }
                        else
                        {
                            newLeftEdges[k+leftEdges.length-1] = extraLeftEdges[k];
                            // Note: Calls to expandArray when bypassing reversible moves
                            // account for 10% of total computation time.
                            // TODO: Find ways to reduce this impact
                            reverse.leftEdges[extraLeftEdges[k]] =
                                expandArray(reverse.leftEdges[extraLeftEdges[k]], vertex);
                        }
                    }
                    i--;
                    pg.leftEdges[vertex] = leftEdges = newLeftEdges;
                    break;
                }
            }
        }
        return bypassed;
    }
    
    private static boolean bypassReversibleOptionsR(Pregraph pg, Pregraph reverse, int vertex, int side)
    {
        boolean bypassed = false;
        int[] rightEdges = pg.rightEdges[vertex];
        
        // Reverse options for right.
        for (int i = 0; i < rightEdges.length; i++)
        {
            if (rightEdges[i] != -1)
            for (int j = 0; j < pg.leftEdges[rightEdges[i]].length; j++)
            {
                // Let gRL = pg.leftEdges[rightEdges[i]][j].  Then j reverses i
                // provided gRL-g >= 0, i.e. provided left survives gRL-g with right
                // to move.
                int gRL = pg.leftEdges[rightEdges[i]][j];
                if (gRL != -1 &&
                    (side == STOPPER || !pg.isAlternatingReachable(gRL, RIGHT_TO_MOVE, rightEdges[i], LEFT_TO_MOVE)) &&
                    markerIndicatesLeq(pg, vertex, pg, gRL))
                {
                    // i is reversible!
                    bypassed = true;
                    int[] extraRightEdges = pg.rightEdges[gRL];
                    int[] newRightEdges = new int[rightEdges.length - 1 + extraRightEdges.length];
                    for (int k = 0; k < i; k++)
                    {
                        newRightEdges[k] = rightEdges[k];
                    }
                    clearFromArray(reverse.rightEdges[rightEdges[i]], vertex);
                    for (int k = i+1; k < rightEdges.length; k++)
                    {
                        newRightEdges[k-1] = rightEdges[k];
                    }
                    for (int k = 0; k < extraRightEdges.length; k++)
                    {
                        if (extraRightEdges[k] == -1 ||
                            arrayContains(rightEdges, extraRightEdges[k]))
                        {
                            newRightEdges[k+rightEdges.length-1] = -1;
                        }
                        else
                        {
                            newRightEdges[k+rightEdges.length-1] = extraRightEdges[k];
                            reverse.rightEdges[extraRightEdges[k]] =
                                expandArray(reverse.rightEdges[extraRightEdges[k]], vertex);
                        }
                    }
                    i--;
                    pg.rightEdges[vertex] = rightEdges = newRightEdges;
                    break;
                }
            }
        }
        
        return bypassed;
    }
    
    private static void eliminateDominatedOptions(Pregraph pg, Pregraph reverse, int vertex, int side)
    {
        // Eliminate dominated options for left.
        for (int i = 0; i < pg.leftEdges[vertex].length; i++)
        {
            if (pg.leftEdges[vertex][i] != -1)
            for (int j = i+1; j < pg.leftEdges[vertex].length; j++)
            {
                // eliminate j provided left survives gi-gj with right to move
                if (pg.leftEdges[vertex][i] == pg.leftEdges[vertex][j])
                {
                    clearFromArray(reverse.leftEdges[pg.leftEdges[vertex][j]], vertex);
                    pg.leftEdges[vertex][j] = -1;
                }
                else if (pg.leftEdges[vertex][j] != -1 &&
                    ((side & OFFSIDE) == 0 || !pg.isAlternatingReachable
                         (pg.leftEdges[vertex][i], RIGHT_TO_MOVE, vertex, LEFT_TO_MOVE)) &&
                    markerIndicatesLeq(pg, pg.leftEdges[vertex][j], pg, pg.leftEdges[vertex][i]))
                {
                    clearFromArray(reverse.leftEdges[pg.leftEdges[vertex][j]], vertex);
                    pg.leftEdges[vertex][j] = -1;
                }
                else if (pg.leftEdges[vertex][j] != -1 &&
                    ((side & OFFSIDE) == 0 || !pg.isAlternatingReachable
                        (pg.leftEdges[vertex][j], RIGHT_TO_MOVE, vertex, LEFT_TO_MOVE)) &&
                    markerIndicatesLeq(pg, pg.leftEdges[vertex][i], pg, pg.leftEdges[vertex][j]))
                {
                    clearFromArray(reverse.leftEdges[pg.leftEdges[vertex][i]], vertex);
                    pg.leftEdges[vertex][i] = -1;
                    break;
                }
            }
        }
        
        pg.leftEdges[vertex] = packArray(pg.leftEdges[vertex]);
        
        // Eliminate dominated options for right.
        for (int i = 0; i < pg.rightEdges[vertex].length; i++)
        {
            if (pg.rightEdges[vertex][i] != -1)
            for (int j = i+1; j < pg.rightEdges[vertex].length; j++)
            {
                if (pg.rightEdges[vertex][i] == pg.rightEdges[vertex][j])
                {
                    clearFromArray(reverse.rightEdges[pg.rightEdges[vertex][j]], vertex);
                    pg.rightEdges[vertex][j] = -1;
                }
                // eliminate j provided left survives gj-gi with right to move.
                else if (pg.rightEdges[vertex][j] != -1 &&
                    ((side & ONSIDE) == 0 || !pg.isAlternatingReachable
                        (pg.rightEdges[vertex][i], LEFT_TO_MOVE, vertex, RIGHT_TO_MOVE)) &&
                    markerIndicatesLeq(pg, pg.rightEdges[vertex][i], pg, pg.rightEdges[vertex][j]))
                {
                    clearFromArray(reverse.rightEdges[pg.rightEdges[vertex][j]], vertex);
                    pg.rightEdges[vertex][j] = -1;
                }
                else if (pg.rightEdges[vertex][j] != -1 &&
                    ((side & ONSIDE) == 0 || !pg.isAlternatingReachable
                        (pg.rightEdges[vertex][j], LEFT_TO_MOVE, vertex, RIGHT_TO_MOVE)) &&
                    markerIndicatesLeq(pg, pg.rightEdges[vertex][j], pg, pg.rightEdges[vertex][i]))
                {
                    clearFromArray(reverse.rightEdges[pg.rightEdges[vertex][i]], vertex);
                    pg.rightEdges[vertex][i] = -1;
                    break;
                }
            }
        }
        
        pg.rightEdges[vertex] = packArray(pg.rightEdges[vertex]);
    }
    
    private static void eliminateIrrelevantCyclicEdges
        (Pregraph pg, Pregraph reverse, Pregraph testGraph, Pregraph tgReverse,
         int vertex, int side, int perspectiveVertex, boolean all)
    {
        int testVertex = (perspectiveVertex == -1 ? vertex : perspectiveVertex);
        boolean checkAllLeftEdges = false, checkAllRightEdges = false;
        if (all)
        {
            for (int i = 0; i < pg.leftEdges[vertex].length; i++)
            {
                if (pg.isSelfAlternatingFollower(pg.leftEdges[vertex][i], RIGHT_TO_MOVE))
                {
                    checkAllLeftEdges = true;
                    break;
                }
            }
            for (int i = 0; i < pg.rightEdges[vertex].length; i++)
            {
                if (pg.isSelfAlternatingFollower(pg.rightEdges[vertex][i], LEFT_TO_MOVE))
                {
                    checkAllRightEdges = true;
                    break;
                }
            }
        }
        for (int i = 0; i < pg.leftEdges[vertex].length; i++)
        {
            int target = pg.leftEdges[vertex][i];
            if (target != -1 && (checkAllLeftEdges || pg.isAlternatingReachable(target, RIGHT_TO_MOVE, vertex, LEFT_TO_MOVE)))
            {
                // Try voiding this option.
                int indexInReverse = clearFromArray(tgReverse.leftEdges[target], vertex);
                testGraph.leftEdges[vertex][i] = -1;
                
                // Check that G' >= G.
                if (leq(pg, reverse, testVertex, testGraph, tgReverse, testVertex, side))
                {
                    clearFromArray(reverse.leftEdges[target], vertex);
                    pg.leftEdges[vertex][i] = -1;
                }
                else
                {
                    testGraph.leftEdges[vertex][i] = target;
                    tgReverse.leftEdges[target][indexInReverse] = vertex;
                }
            }
        }
        for (int i = 0; i < pg.rightEdges[vertex].length; i++)
        {
            int target = pg.rightEdges[vertex][i];
            if (target != -1 && (checkAllRightEdges || pg.isAlternatingReachable(target, LEFT_TO_MOVE, vertex, RIGHT_TO_MOVE)))
            {
                // Try voiding this option.
                int indexInReverse = clearFromArray(tgReverse.rightEdges[target], vertex);
                testGraph.rightEdges[vertex][i] = -1;
                
                // Check that G' <= G.
                if (leq(testGraph, tgReverse, testVertex, pg, reverse, testVertex, side))
                {
                    clearFromArray(reverse.rightEdges[target], vertex);
                    pg.rightEdges[vertex][i] = -1;
                }
                else
                {
                    testGraph.rightEdges[vertex][i] = target;
                    tgReverse.rightEdges[target][indexInReverse] = vertex;
                }
            }
        }
    }
    
    private static boolean unravelFully(Pregraph graph, Pregraph reverse, boolean onside, int specificVertex)
    {
        Pregraph newGraph = graph, ngReverse = reverse;
        boolean unravelingFailed = false, unraveled = false;
        unravelingLoop: while (true)
        {
            for (int vertex = 0; vertex < newGraph.leftEdges.length; vertex++)
            {
                if (specificVertex == -1 || (reachMarkers[vertex] & REACHABLE) != 0)
                {
                    int[] edges = (onside ? newGraph.rightEdges[vertex] : newGraph.leftEdges[vertex]);
                    int mover = (onside ? RIGHT_TO_MOVE : LEFT_TO_MOVE);
                    for (int i = 0; i < edges.length; i++)
                    {
                        if (edges[i] != -1 && newGraph.isAlternatingReachable(edges[i], mover ^ MOVE_MASK, vertex, mover))
                        {
                            for (int n = 1; n < 4; n++)
                            {
                                Pregraph unraveledGraph = newGraph.unravelBy(vertex, i, mover, n);
                                int testVertex = (specificVertex == -1 ? vertex : specificVertex);
                                if ( onside && leq(unraveledGraph, unraveledGraph.reverse(), testVertex,
                                                   newGraph, ngReverse, testVertex, ONSIDE) ||
                                    !onside && leq(newGraph, ngReverse, testVertex,
                                                   unraveledGraph, unraveledGraph.reverse(), testVertex, OFFSIDE))
                                {
                                    if (!unraveled)
                                    {
                                        unraveled = true;
//                                        Context.getActiveContext().getLogger().finer
//                                            ("Unraveling in the " + (specificVertex == -1 ? "general" : "specific") + " case.");
                                    }
                                    newGraph = unraveledGraph;
                                    ngReverse = newGraph.reverse();
                                    updateReachable(newGraph, specificVertex);
//                                    Context.getActiveContext().getLogger().finer("Needed unraveling " + n + ".");
                                    continue unravelingLoop;
                                }
                            }
                            unravelingFailed = true;
                            break unravelingLoop;
                        }
                    }
                }
            }
            break;
        }
        
        if (unraveled)
        {
            if (unravelingFailed)
            {
//                Context.getActiveContext().getLogger().finer("A later unraveling failed.");
            }
            else
            {
                // Verify equivalence.
                if (specificVertex == -1)
                {
                    verifyEquivalentGraphs(graph, newGraph, onside ? ONSIDE : OFFSIDE);
                }
                else if (!leq(graph, reverse, specificVertex, newGraph, ngReverse, specificVertex, onside ? ONSIDE : OFFSIDE) ||
                         !leq(newGraph, ngReverse, specificVertex, graph, reverse, specificVertex, onside ? ONSIDE : OFFSIDE))
                {
                    throw new RuntimeException("Unraveling algorithm failed!");
                }
                graph.leftEdges = newGraph.leftEdges;
                graph.rightEdges = newGraph.rightEdges;
                reverse.leftEdges = ngReverse.leftEdges;
                reverse.rightEdges = ngReverse.rightEdges;
            }
        }
        
        return !unravelingFailed;
    }
    
    private static void fuse(Pregraph pg, Pregraph reverse, int side, int startVertex)
    {
        int[] fuseMap = new int[pg.leftEdges.length];
        for (int i = 0; i < fuseMap.length; i++)
        {
            fuseMap[i] = i;
        }
        
        Bigraph graphForACTesting = null;
        
        if (side != STOPPER)
        {
            // Right now, to be totally safe, we only fuse vertices
            // below which there are no alternating cycles.  This will
            // have to do until I understand fusion better.
            
            graphForACTesting = new Bigraph(pg.leftEdges, pg.rightEdges);
        }
        
        markAllStrongWins(pg, reverse, pg, reverse);
        
        updateReachable(pg, startVertex);
        
        for (int i = 0; i < pg.leftEdges.length; i++)
        {
            if ((reachMarkers[i] & REACHABLE) != 0)
            for (int j = 0; j < i; j++)
            {
                if ((reachMarkers[j] & REACHABLE) != 0 &&
                    (side == STOPPER || graphForACTesting.isAlternatingCycleFree(i)
                                     && graphForACTesting.isAlternatingCycleFree(j)) &&
                    markerIndicatesLeq(pg, i, pg, j) &&
                    markerIndicatesLeq(pg, j, pg, i))
                {
                    // Mark vertex i for fusion to vertex j.
                    fuseMap[i] = j;
                    updateReachable(pg, startVertex);
                    break;
                }
            }
        }
        
        Arrays.fill(markers, 0, pg.getNumVertices() * pg.getNumVertices(), (byte) 0);
        
        applyFusion(pg, fuseMap);
    }
    
    private static void applyFusion(Pregraph pg, int[] fuseMap)
    {
        for (int vertex = 0; vertex < pg.leftEdges.length; vertex++)
        {
            if ((reachMarkers[vertex] & REACHABLE) != 0)
            {
                for (int i = 0; i < pg.leftEdges[vertex].length; i++)
                {
                    if (pg.leftEdges[vertex][i] != -1)
                    {
                        pg.leftEdges[vertex][i] = fuseMap[pg.leftEdges[vertex][i]];
                    }
                }
                for (int i = 0; i < pg.rightEdges[vertex].length; i++)
                {
                    if (pg.rightEdges[vertex][i] != -1)
                    {
                        pg.rightEdges[vertex][i] = fuseMap[pg.rightEdges[vertex][i]];
                    }
                }
            }
        }
        
    }
    
    private static void isolateInArray(int[] array, int index)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (i != index)
            {
                array[i] = -1;
            }
        }
    }
    
    private static void verifyEquivalentGraphs(Pregraph pg, Pregraph ph, int side)
    {
        long tm = System.currentTimeMillis();
        
        Pregraph pgReverse = pg.reverse(), phReverse = ph.reverse();
        
        markStatesWonForRight(pg, pgReverse, ph, phReverse, side);
        for (int vertex = 0; vertex < pg.getNumVertices(); vertex++)
        {
            if (!markerIndicatesLeq(pg, vertex, ph, vertex))
            {
                throw new RuntimeException("Algorithm failed: Graphs are not equivalent.");
            }
        }
        Arrays.fill(markers, 0, pg.getNumVertices() * ph.getNumVertices(), (byte) 0);
        
        markStatesWonForRight(ph, phReverse, pg, pgReverse, side);
        for (int vertex = 0; vertex < pg.getNumVertices(); vertex++)
        {
            if (!markerIndicatesLeq(ph, vertex, pg, vertex))
            {
                throw new RuntimeException("Algorithm failed: Graphs are not equivalent.");
            }
        }
        Arrays.fill(markers, 0, pg.getNumVertices() * ph.getNumVertices(), (byte) 0);

        tm = System.currentTimeMillis() - tm;
        if (tm >= 100)
        {
//            Context.getActiveContext().getLogger().finer("Verified answer in " + tm + " ms.");
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    // Sidling.

    /**
     * Calculates the unique <code>StopperSidedGame</code> that is equivalent
     * to this game.  Not all loopy games have onsides and offsides which are
     * equivalent to stoppers.  If this game does not, then this method will
     * throw a <code>NotStopperException</code>.
     * <p>
     * If this game <i>is</i> equivalent to some stopper-sided game, then
     * this method is guaranteed to find it.  (We use a different algorithm
     * than the sidling algorithm described in Winning Ways, which fails to
     * converge in a number of important cases.)
     *
     * @return  The equivalent <code>StopperSidedGame</code>.
     * @throws  NotStopperException This game is not stopper-sided.
     * @see     #onside() onside
     * @see     #offside() offside
     */
    /*
    public StopperSidedGame sidle() throws NotStopperException
    {
        return new StopperSidedGame(onside(), offside());
    }
     */
    
    /**
     * Calculates the onside of this game.  Not all loopy games have onsides
     * which are equivalent to stoppers.  If this game does not, then this
     * method will throw a <code>NotStopperException</code>.
     * <p>
     * If the onside of this game <i>is</i> equivalent to a stopper, then this
     * method is guaranteed to find it.
     *
     * @return  The onside of this game, or <code>null</code> if it is not a
     *          stopper.
     * @throws  NotStopperException The onside of this game is not equivalent
     *          to a stopper.
     * @see     #sidle() sidle
     * @see     #offside() offside
     */
    public CanonicalStopperGame onside() throws NotStopperException
    {
        CanonicalStopperGame onside = new CanonicalStopperGame();
        onside.graph = simplifyGraph(graph, null, ONSIDE, startVertex);
        onside.startVertex = 0;
        if (onside.graph.isAlternatingCycleFree(0))
        {
            return onside;
        }
        else
        {
            throw new NotStopperException();
        }
    }
    
    /**
     * Calculates the offside of this game.  Not all loopy games have offsides
     * which are equivalent to stoppers.  If this game does not, then this
     * method will throw a <code>NotStopperException</code>.
     * <p>
     * If the offside of this game <i>is</i> equivalent to a stopper, then this
     * method is guaranteed to find it.
     *
     * @return  The offside of this game, or <code>null</code> if it is not a
     *          stopper.
     * @throws  NotStopperException The offside of this game is not equivalent
     *          to a stopper.
     * @see     #sidle() sidle
     * @see     #onside() onside
     */
    public CanonicalStopperGame offside() throws NotStopperException
    {
        CanonicalStopperGame offside = new CanonicalStopperGame();
        offside.graph = simplifyGraph(graph, null, OFFSIDE, startVertex);
        offside.startVertex = 0;
        if (offside.graph.isAlternatingCycleFree(0))
        {
            return offside;
        }
        else
        {
            throw new NotStopperException();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    // Nested classes.

    /**
     * A single node of a game graph.  This class is used to specify a game
     * graph to a {@link LoopyGame} constructor.  Objects of type
     * <code>Node</code> are mutable, so you can first construct a
     * <code>Node</code> for each node in your game graph, and then specify
     * the left and right edges that connect them.
     * <p>
     * A <i>terminal</i> node is a node with no edges.  It is possible to
     * specify values for terminal nodes other than zero.  The value of a
     * terminal node can be any {@link CanonicalShortGame} or
     * <code>LoopyGame</code>.
     *
     * @author  Aaron Siegel
     * @version $Revision: 1.29 $ $Date: 2007/02/16 20:10:13 $
     * @see     LoopyGame
     */
    public static class Node extends CgsuiteObject implements java.io.Serializable
    {
        static class GraphInfo
        {
            boolean valid;
            Bigraph graph;
            boolean[] canonical;
            Bigraph[] simplifiedGraphs = new Bigraph[4];
            GraphInfo() { valid = true; }
        }
        
        private List<Node> leftEdges, rightEdges;
        private transient Game value;
        private String label;

        transient GraphInfo graphInfo;
        transient int startVertex;
        
        /**
         * Constructs a new <code>Node</code> whose left and right edge sets
         * are initially empty and whose value is initially
         * {@link CanonicalShortGame#ZERO ZERO}.
         */
        public Node()
        {
            super(CgsuitePackage.forceLookupClass("Object"));
            leftEdges = new ArrayList<Node>();
            rightEdges = new ArrayList<Node>();
            value = CanonicalShortGame.ZERO;
        }
        
        /**
         * Constructs a new <code>Node</code> whose left and right edge sets
         * are initially empty and whose value is initially <code>g</code>.
         * 
         * @param  g The initial value for this node.
         */
        public Node(LoopyGame g)
        {
            this();
            setValue(g);
        }
        
        /**
         * Constructs a new <code>Node</code> whose left and right edge sets
         * are initially empty and whose value is initially <code>g</code>.
         * 
         * @param  g The initial value for this node.
         */
        public Node(CanonicalShortGame g)
        {
            this();
            setValue(g);
        }
        
        public Node(String label)
        {
            this();
            this.label = label;
        }
        
        /**
         * Constructs a new <code>Node</code> with the specified initial left
         * and right edge sets.  It is permissible to add additional left and
         * right edges later on.
         */
        public Node(Collection<? extends Node> leftEdges, Collection<? extends Node> rightEdges)
        {
            this();
            addAllLeftEdges(leftEdges);
            addAllRightEdges(rightEdges);
        }
        
        public Node(Collection<? extends Node> leftEdges, Collection<? extends Node> rightEdges, String label)
        {
            this(leftEdges, rightEdges);
            this.label = label;
        }

        public boolean equals(Object o)
        {
            return this == o;
        }

        /**
         * Gets the left edges of this node.  The <code>List</code> that is
         * returned may not be modified; use
         * {@link #addLeftEdge(LoopyGame.Node) addLeftEdge} instead.
         *
         * @return  The left edges of this node.
         * @see     #addLeftEdge(LoopyGame.Node) addLeftEdge
         * @see     #getRightEdges
         */
        public List<Node> getLeftEdges()
        {
            return Collections.unmodifiableList(leftEdges);
        }
        
        /**
         * Gets the right edges of this node.  The <code>List</code> that is
         * returned may not be modified; use
         * {@link #addRightEdge(LoopyGame.Node) addRightEdge} instead.
         *
         * @return  The right edges of this node.
         * @see     #addRightEdge(LoopyGame.Node) addRightEdge
         * @see     #getLeftEdges
         */
        public List<Node> getRightEdges()
        {
            return Collections.unmodifiableList(rightEdges);
        }
        
        /**
         * Gets the value associated to this node.  The value is relevant
         * only if this is a terminal node.
         *
         * @return  The value of this node.
         */
        public Game getValue()
        {
            return value;
        }
        
        /**
         * Adds a left edge from this node to <code>dest</code>.
         *
         * @param   dest The destination of the edge to add.
         */
        public void addLeftEdge(Node dest)
        {
            leftEdges.add(dest);
            invalidate();
        }
        
        /**
         * Adds a left edge from this node to a new terminal node whose value
         * is <code>g</code>.
         *
         * @param   g The value of the new terminal node to add.
         */
        public void addLeftEdge(LoopyGame g)
        {
            addLeftEdge(new Node(g));
        }
        
        /**
         * Adds a left edge from this node to a new terminal node whose value
         * is <code>g</code>.
         *
         * @param   g The value of the new terminal node to add.
         */
        public void addLeftEdge(CanonicalShortGame g)
        {
            addLeftEdge(new Node(g));
        }
        
        /**
         * Adds a left edge to this node for each destination in the specified
         * collection.
         *
         * @param   dests The collection of destination nodes.
         */
        public void addAllLeftEdges(Collection<? extends Node> dests)
        {
            leftEdges.addAll(dests);
            invalidate();
        }
        
        /**
         * Removes the left edge from this node to <code>dest</code>, if one
         * exists.
         *
         * @param   dest The destination of the edge to remove.
         * @return  <code>true</code> if the edge was removed;
         *          <code>false</code> if no such edge exists.
         */
        public boolean removeLeftEdge(Node dest)
        {
            boolean removed = leftEdges.remove(dest);
            if (removed)
            {
                invalidate();
            }
            return removed;
        }
        
        /**
         * Removes all left edges from this node to elements of
         * <code>dests</code>.  It is permissible for <code>dests</code> to
         * contain nodes for which no left edge exists.
         *
         * @param   dests The destinations of the edges to remove.
         * @return  <code>true</code> if at least one edge was removed.
         */
        public boolean removeAllLeftEdges(Collection<? extends Node> dests)
        {
            boolean removed = leftEdges.removeAll(dests);
            if (removed)
            {
                invalidate();
            }
            return removed;
        }
        
        /**
         * Adds a right edge from this node to <code>dest</code>.
         *
         * @param   dest The destination of the edge to add.
         */
        public void addRightEdge(Node dest)
        {
            rightEdges.add(dest);
            invalidate();
        }
        
        /**
         * Adds a right edge from this node to a new terminal node whose value
         * is <code>g</code>.
         *
         * @param   g The value of the new terminal node to add.
         */
        public void addRightEdge(LoopyGame g)
        {
            addRightEdge(new Node(g));
        }
        
        /**
         * Adds a right edge from this node to a new terminal node whose value
         * is <code>g</code>.
         *
         * @param   g The value of the new terminal node to add.
         */
        public void addRightEdge(CanonicalShortGame g)
        {
            addRightEdge(new Node(g));
        }
        
        /**
         * Adds a right edge to this node for each destination in the specified
         * collection.
         *
         * @param   dests The collection of destination nodes.
         */
        public void addAllRightEdges(Collection<? extends Node> dests)
        {
            rightEdges.addAll(dests);
            invalidate();
        }
        
        /**
         * Removes the right edge from this node to <code>dest</code>, if one
         * exists.
         *
         * @param   dest The destination of the edge to remove.
         * @return  <code>true</code> if the edge was removed;
         *          <code>false</code> if no such edge exists.
         */
        public boolean removeRightEdge(Node dest)
        {
            boolean removed = rightEdges.remove(dest);
            if (removed)
            {
                invalidate();
            }
            return removed;
        }
        
        /**
         * Removes all right edges from this node to elements of
         * <code>dests</code>.  It is permissible for <code>dests</code> to
         * contain nodes for which no right edge exists.
         *
         * @param   dests The destinations of the edges to remove.
         * @return  <code>true</code> if at least one edge was removed.
         */
        public boolean removeAllRightEdges(Collection<? extends Node> dests)
        {
            boolean removed = rightEdges.removeAll(dests);
            if (removed)
            {
                invalidate();
            }
            return removed;
        }
        
        /**
         * Returns <code>true</code> if this node is terminal.
         *
         * @return  <code>true</code> if this node is terminal.
         */
        public boolean isTerminal()
        {
            return leftEdges.isEmpty() && rightEdges.isEmpty();
        }
        
        /**
         * Sets the value of this node to the specified game.  Note that the
         * value of a node is only relevant if the node is terminal.
         *
         * @param   g The new value for this node.
         * @throws  NullPointerException <code>g</code> is <code>null</code>.
         */
        public void setValue(LoopyGame g)
        {
            if (g == null)
            {
                throw new NullPointerException();
            }
            value = g;
            invalidate();
        }

        /**
         * Sets the value of this node to the specified game.  Note that the
         * value of a node is only relevant if the node is terminal.
         *
         * @param   g The new value for this node.
         * @throws  NullPointerException <code>g</code> is <code>null</code>.
         */
        public void setValue(CanonicalShortGame g)
        {
            if (g == null)
            {
                throw new NullPointerException();
            }
            value = g;
            invalidate();
        }
        
        private void invalidate()
        {
            if (graphInfo != null)
            {
                graphInfo.valid = false;
            }
        }
    }
    
    private static class Pregraph implements Cloneable
    {
        int[][] leftEdges, rightEdges;
        
        Pregraph(int numVertices)
        {
            leftEdges = new int[numVertices][];
            rightEdges = new int[numVertices][];
        }
        
        Pregraph(Bigraph g)
        {
            leftEdges = g.cloneAllLeftEdges();
            rightEdges = g.cloneAllRightEdges();
        }
        
        Pregraph(int[][][] edges)
        {
            leftEdges = edges[0];
            rightEdges = edges[1];
        }
        
        public String toString()
        {
            String s = "";
            for (int vertex = 0; vertex < leftEdges.length; vertex++)
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

        public Object clone()
        {
            Pregraph clone = new Pregraph(leftEdges.length);
            for (int vertex = 0; vertex < leftEdges.length; vertex++)
            {
                clone.leftEdges[vertex] = new int[leftEdges[vertex].length];
                System.arraycopy(leftEdges[vertex], 0, clone.leftEdges[vertex], 0, leftEdges[vertex].length);
                clone.rightEdges[vertex] = new int[rightEdges[vertex].length];
                System.arraycopy(rightEdges[vertex], 0, clone.rightEdges[vertex], 0, rightEdges[vertex].length);
            }
            return clone;
        }
        
        int getNumVertices()
        {
            return leftEdges.length;
        }

        boolean isOn(int vertex)
        {
            return leftEdges[vertex].length == 1 &&
                rightEdges[vertex].length == 0 &&
                leftEdges[vertex][0] == vertex;
        }
        
        boolean isOff(int vertex)
        {
            return rightEdges[vertex].length == 1 &&
                leftEdges[vertex].length == 0 &&
                rightEdges[vertex][0] == vertex;
        }
        
        boolean isDud(int vertex)
        {
            return leftEdges[vertex].length == 1 &&
                rightEdges[vertex].length == 1 &&
                leftEdges[vertex][0] == vertex &&
                rightEdges[vertex][0] == vertex;
        }
        
        private static int[] leftCounters, rightCounters;
        
        Pregraph reverse()
        {
            if (leftCounters == null || leftCounters.length < leftEdges.length)
            {
                leftCounters = new int[leftEdges.length * 2];
                rightCounters = new int[rightEdges.length * 2];
            }
            else
            {
                Arrays.fill(leftCounters, 0, leftEdges.length, 0);
                Arrays.fill(rightCounters, 0, rightEdges.length, 0);
            }
            Pregraph reverse = new Pregraph(leftEdges.length);
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
                reverse.leftEdges[vertex] = new int[leftCounters[vertex]];
                reverse.rightEdges[vertex] = new int[rightCounters[vertex]];
                leftCounters[vertex] = rightCounters[vertex] = 0;
            }
            for (int vertex = 0; vertex < leftEdges.length; vertex++)
            {
                for (int i = 0; i < leftEdges[vertex].length; i++)
                {
                    if (leftEdges[vertex][i] != -1)
                    {
                        int target = leftEdges[vertex][i];
                        reverse.leftEdges[target][leftCounters[target]] = vertex;
                        leftCounters[target]++;
                    }
                }
                for (int i = 0; i < rightEdges[vertex].length; i++)
                {
                    if (rightEdges[vertex][i] != -1)
                    {
                        int target = rightEdges[vertex][i];
                        reverse.rightEdges[target][rightCounters[target]] = vertex;
                        rightCounters[target]++;
                    }
                }
            }
            return reverse;
        }
        
        Pregraph unravelBy(int fromVertex, int edge, int mover, int n)
        {
            expandMarkers(2 * leftEdges.length);
            int[][] moverEdges = (mover == LEFT_TO_MOVE ? leftEdges : rightEdges);
            int[][] otherEdges = (mover == LEFT_TO_MOVE ? rightEdges : leftEdges);
            
            int[] acVertexRenumbering = new int[leftEdges.length];
            Arrays.fill(acVertexRenumbering, -1);
            int acVertexCount = findACVertices(acVertexRenumbering, fromVertex, moverEdges[fromVertex][edge], mover);
            if (acVertexRenumbering[moverEdges[fromVertex][edge]] == -1)
            {
                throw new IllegalArgumentException("Tried to unravel by a non-AC edge.");
            }
            
            int newNumVertices = leftEdges.length + 2 * n * acVertexCount;
            Pregraph unraveledGraph = new Pregraph(newNumVertices);
            int[][] newMoverEdges = (mover == LEFT_TO_MOVE ? unraveledGraph.leftEdges : unraveledGraph.rightEdges);
            for (int vertex = 0; vertex < leftEdges.length; vertex++)
            {
                unraveledGraph.leftEdges[vertex] = new int[leftEdges[vertex].length];
                unraveledGraph.rightEdges[vertex] = new int[rightEdges[vertex].length];
                System.arraycopy(leftEdges[vertex], 0, unraveledGraph.leftEdges[vertex], 0, leftEdges[vertex].length);
                System.arraycopy(rightEdges[vertex], 0, unraveledGraph.rightEdges[vertex], 0, rightEdges[vertex].length);
                if (acVertexRenumbering[vertex] != -1)
                {
                    for (int k = 0; k < n; k++)
                    {
                        int lkaIndex = leftEdges.length + 2 * k * acVertexCount + acVertexRenumbering[vertex];
                        int rkaIndex = leftEdges.length + (2 * k + 1) * acVertexCount + acVertexRenumbering[vertex];
                        unraveledGraph.leftEdges[lkaIndex] = new int[leftEdges[vertex].length];
                        unraveledGraph.rightEdges[lkaIndex] = new int[rightEdges[vertex].length];
                        unraveledGraph.leftEdges[rkaIndex] = new int[leftEdges[vertex].length];
                        unraveledGraph.rightEdges[rkaIndex] = new int[rightEdges[vertex].length];
                        for (int i = 0; i < leftEdges[vertex].length; i++)
                        {
                            int target = leftEdges[vertex][i];
                            unraveledGraph.leftEdges[rkaIndex][i] = target;
                            if (target == -1 || acVertexRenumbering[target] == -1)
                            {
                                unraveledGraph.leftEdges[lkaIndex][i] = target;
                            }
                            else
                            {
                                unraveledGraph.leftEdges[lkaIndex][i] =
                                    leftEdges.length + (2 * k + 1) * acVertexCount + acVertexRenumbering[target];
                            }
                        }
                        for (int i = 0; i < rightEdges[vertex].length; i++)
                        {
                            int target = rightEdges[vertex][i];
                            unraveledGraph.rightEdges[lkaIndex][i] = target;
                            if (target == -1 || acVertexRenumbering[target] == -1)
                            {
                                unraveledGraph.rightEdges[rkaIndex][i] = target;
                            }
                            else
                            {
                                unraveledGraph.rightEdges[rkaIndex][i] =
                                    leftEdges.length + 2 * k * acVertexCount + acVertexRenumbering[target];
                            }
                        }
                    }
                }
            }
            // Redirect 0 => 1
            newMoverEdges[fromVertex][edge] = (n == 0 ? -1 :
                 leftEdges.length + (mover == LEFT_TO_MOVE ? acVertexCount : 0) + acVertexRenumbering[moverEdges[fromVertex][edge]]);
            // Redirect k => k + 1
            for (int k = 0; k < n; k++)
            {
                newMoverEdges[leftEdges.length + (2 * k + (mover == LEFT_TO_MOVE ? 0 : 1)) * acVertexCount +
                        acVertexRenumbering[fromVertex]][edge] = (k == n-1 ? -1 :
                    leftEdges.length + (2 * (k+1) + (mover == LEFT_TO_MOVE ? 1 : 0)) * acVertexCount +
                        acVertexRenumbering[moverEdges[fromVertex][edge]]);
            }
            return unraveledGraph;
        }
        
        private int findACVertices(int[] acVertexRenumbering, int fromVertex, int target, int mover)
        {
            // TODO: IMPROVE.
            int count = 0;
            for (int vertex = 0; vertex < leftEdges.length; vertex++)
            {
                if (isAlternatingReachable(vertex, LEFT_TO_MOVE, fromVertex, mover) &&
                    isAlternatingReachable(target, mover ^ MOVE_MASK, vertex, LEFT_TO_MOVE) ||
                    isAlternatingReachable(vertex, RIGHT_TO_MOVE, fromVertex, mover) &&
                    isAlternatingReachable(target, mover ^ MOVE_MASK, vertex, RIGHT_TO_MOVE))
                {
                    acVertexRenumbering[vertex] = count;
                    count++;
                }
            }
            return count;
        }
        
        boolean isSelfAlternatingFollower(int vertex, int mover)
        {
            int[] edges = (mover == LEFT_TO_MOVE ? leftEdges[vertex] : rightEdges[vertex]);
            for (int i = 0; i < edges.length; i++)
            {
                if (edges[i] != -1 && isAlternatingReachable(edges[i], mover ^ MOVE_MASK, vertex, mover))
                {
                    return true;
                }
            }
            return false;
        }
        
        boolean isAlternatingReachable(int startVertex, int startMover, int targetVertex, int targetMover)
        {
            expandReachMarkers(leftEdges.length);
            for (int i = 0; i < leftEdges.length; i++)
            {
                reachMarkers[i] &= ~(LEFT_AR | RIGHT_AR);
            }
            boolean reachable = isAlternatingReachableR
                (startVertex, startMover == LEFT_TO_MOVE ? LEFT_AR : RIGHT_AR,
                 targetVertex, targetMover == LEFT_TO_MOVE ? LEFT_AR : RIGHT_AR);
            return reachable;
        }
        
        private boolean isAlternatingReachableR(int vertex, int mover, int targetVertex, int targetMover)
        {
            if (vertex == targetVertex && mover == targetMover)
            {
                return true;
            }
            if ((reachMarkers[vertex] & mover) != 0)
            {
                return false;
            }
            reachMarkers[vertex] |= mover;
            int[] edges = (mover == LEFT_AR ? leftEdges[vertex] : rightEdges[vertex]);
            for (int i = 0; i < edges.length; i++)
            {
                if (edges[i] != -1 &&
                    isAlternatingReachableR(edges[i], mover ^ AR_MASK, targetVertex, targetMover))
                {
                    if (DEBUG) System.out.println(vertex);
                    return true;
                }
            }
            return false;
        }
        
        int byteSize()
        {
            int byteSize = 0;
            for (int i = 0; i < leftEdges.length; i++)
            {
                byteSize += leftEdges[i].length;
                byteSize += rightEdges[i].length;
            }
            return byteSize;
        }
    }
}

