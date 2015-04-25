/*
 * Plot.java
 *
 * Created on April 5, 2004, 12:35 PM
 * $Id: Plot.java,v 1.12 2007/02/20 20:17:50 asiegel Exp $
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

package org.cgsuite.lang.output;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.game.RationalNumber;
import org.cgsuite.lang.game.Thermograph;
import org.cgsuite.lang.game.Trajectory;

/**
 * An {@link org.cgsuite.plugin.Output} type for graphical display of
 * {@link org.cgsuite.Thermograph}s.
 *
 * @author  Aaron Siegel
 * @version $Revision: 1.12 $ $Date: 2007/02/20 20:17:50 $
 */
public class PlotOutput extends CgsuiteObject implements Output
{
    public final static CgsuiteClass TYPE = CgsuitePackage.forceLookupClass("Plot");
    
    private final static Color[] STANDARD_COLORS = new Color[] {
        Color.black,
        Color.blue,
        Color.red,
        Color.green.darker()
    };
    
    private final static RationalNumber
        HALF = new RationalNumber(1, 2), THIRD = new RationalNumber(1, 3), FOUR_THIRDS = new RationalNumber(4, 3);
    
    private final static int PRIMARY_TICK_EXTENT = 12;
    private final static int[] STANDARD_TICK_EXTENTS = new int[] { 8, 5, 3, 2, 1 };
    
    static Color getColorForIndex(int index)
    {
        return STANDARD_COLORS[index % STANDARD_COLORS.length];
    }
    
    static int getDisplacementForIndex(int index)
    {
        int magnitude = 2 * ((index+1) / 2);
        if (index % 2 == 0)
        {
            return magnitude;
        }
        else
        {
            return -magnitude;
        }
    }
    
    private List<TrajectoryInfo> trajectories;
    private RationalNumber waterLevel;
    private PlotInfo plotInfo;
    
    // We cache a wide a variety of data for faster repainting.
    
    // true if the cached data is accurate; false if it needs to be recalculated.
    private boolean valid = false;
    
    // All the means, temperatures, and critical points and values of all
    // trajectories in this plot.
    private List<RationalNumber> allMeans, allCriticalValues, allTemperatures, allCriticalTemps;
    
    // RationalNumbers corresponding to the boundaries of the plot region.
    private RationalNumber viewLeft, viewRight, viewTop, viewBottom;
    private RationalNumber scale;
    private int tCoordOfVAxis, vCoordOfTAxis;
    private RationalNumber vTickBase, tTickBase, secondaryVTickBase, secondaryTTickBase, startingVTick, startingTTick;
    private Font font;
    private int minHorizontalLabelSpacing, minVerticalLabelSpacing;
    private int trailerHeight;
    private Dimension actualImageSize;
    private List<TickInfo> vTicks, tTicks;
    
    public PlotOutput()
    {
        this(new PlotInfo());
    }
    
    public PlotOutput(PlotInfo plotInfo)
    {
        super(TYPE);
        trajectories = new ArrayList<TrajectoryInfo>();
        allMeans = new ArrayList<RationalNumber>();
        allCriticalTemps = new ArrayList<RationalNumber>();
        allTemperatures = new ArrayList<RationalNumber>();
        allCriticalValues = new ArrayList<RationalNumber>();
        tTicks = new ArrayList<TickInfo>();
        vTicks = new ArrayList<TickInfo>();
        this.plotInfo = plotInfo;
    }
    
    // TODO: Handle infinite trajectories?
    public void addTrajectory(Trajectory trajectory, Color color, int hDisplacement)
    {
        if (trajectory.getMastValue().isInfinite())
        {
            return;
        }
        trajectories.add(new TrajectoryInfo(trajectory, color, hDisplacement));
        valid = false;
    }
    
    public void addThermograph(Thermograph thermograph, Color color, int hDisplacement)
    {
        addTrajectory(thermograph.getLeftWall(), color, hDisplacement);
        addTrajectory(thermograph.getRightWall(), color, hDisplacement);
    }
    
    public void setWaterLevel(RationalNumber waterLevel)
    {
        if (waterLevel.isInfinite() || waterLevel.compareTo(RationalNumber.NEGATIVE_ONE) < 0)
        {
            throw new IllegalArgumentException("waterLevel must be >= -1 and < Infinity.");
        }
        
        // TODO: Negative water levels?
        if (waterLevel.compareTo(RationalNumber.ZERO) > 0)
        {
            this.waterLevel = waterLevel;
        }
        else
        {
            waterLevel = null;
        }
    }
    
    private void recalc()
    {
        if (valid)
        {
            return;
        }
        
        allMeans.clear();
        allCriticalTemps.clear();
        allTemperatures.clear();
        allCriticalValues.clear();
        tTicks.clear();
        vTicks.clear();
        
        font = new Font("SansSerif", Font.PLAIN, plotInfo.fontSize);
        
        for (Iterator i = trajectories.iterator(); i.hasNext();)
        {
            Trajectory t = ((TrajectoryInfo) i.next()).trajectory;
            allMeans.add(t.getMastValue());
            for (int j = 0;
                 j < t.getNumCriticalPoints() &&
                 (waterLevel == null || t.getCriticalPoint(j).compareTo(waterLevel) > 0 ||
                  t.getCriticalPoint(j).compareTo(waterLevel) == 0 && plotInfo.drawTailsUnderWaterLevel) &&
                 (plotInfo.subzero || t.getCriticalPoint(j).compareTo(RationalNumber.ZERO) > 0);
                 j++)
            {
                allCriticalValues.add(t.valueAt(t.getCriticalPoint(j)));
                if (j == 0)
                {
                    allTemperatures.add(t.getCriticalPoint(j));
                }
                else
                {
                    allCriticalTemps.add(t.getCriticalPoint(j));
                }
            }
            if (waterLevel == null || RationalNumber.ZERO.compareTo(waterLevel) > 0)
            {
                allCriticalValues.add(t.valueAt(RationalNumber.ZERO));
            }
            else
            {
                // Add the extrapolated value.
                allCriticalValues.add
                    (t.valueAt(waterLevel).subtract(t.slopeBelow(waterLevel).multiply(waterLevel)));
            }
        }
        Collections.sort(allMeans);
        Collections.sort(allCriticalValues);
        Collections.sort(allTemperatures);
        Collections.sort(allCriticalTemps);
        
        if (plotInfo.viewLeft == null)
        {
            viewBottom = plotInfo.subzero ? RationalNumber.NEGATIVE_ONE : RationalNumber.ZERO;
            if (trajectories.size() == 0)
            {
                viewTop = RationalNumber.ONE;
                viewLeft = RationalNumber.ONE;
                viewRight = RationalNumber.ONE.negate();
            }
            else
            {
                // Auto-determine RationalNumber bounds for the trajectories.
                // Currently we only consider trajectories with vertical masts.
                viewTop = (waterLevel == null ? RationalNumber.ZERO : waterLevel.multiply(FOUR_THIRDS));
                viewLeft = RationalNumber.NEGATIVE_INFINITY;
                viewRight = RationalNumber.POSITIVE_INFINITY;
                for (TrajectoryInfo ti : trajectories)
                {
                    Trajectory t = ti.trajectory;
                    if (t.getNumCriticalPoints() > 0)
                    {
                        viewTop = viewTop.max(t.getCriticalPoint(0).multiply(FOUR_THIRDS));
                    }
                    for (int j = 0; j <= t.getNumCriticalPoints(); j++)
                    {
                        RationalNumber temp;
                        if (j == t.getNumCriticalPoints())
                        {
                            temp = (waterLevel != null ? waterLevel :
                                    (plotInfo.subzero ? RationalNumber.NEGATIVE_ONE : RationalNumber.ZERO));
                        }
                        else if (waterLevel != null && t.getCriticalPoint(j).compareTo(waterLevel) < 0)
                        {
                            temp = waterLevel;
                        }
                        else if (!plotInfo.subzero && t.getCriticalPoint(j).compareTo(RationalNumber.ZERO) < 0)
                        {
                            temp = RationalNumber.ZERO;
                        }
                        else
                        {
                            temp = t.getCriticalPoint(j);
                        }
                        RationalNumber value = t.valueAt(temp);
                        viewLeft = viewLeft.max(value);
                        viewRight = viewRight.min(value);
                    }
                    if (waterLevel != null && plotInfo.drawTailsUnderWaterLevel)
                    {
                        // We need to adjust viewLeft and viewRight to accomodate the tails.
                        RationalNumber value = t.valueAt(waterLevel).subtract
                            (t.slopeBelow(waterLevel).multiply(waterLevel));
                        viewLeft = viewLeft.max(value);
                        viewRight = viewRight.min(value);
                    }
                }
            }
        }
        else
        {
            viewLeft = plotInfo.viewLeft;
            viewRight = plotInfo.viewRight;
            viewTop = plotInfo.viewTop;
            viewBottom = plotInfo.viewBottom;
        }
        
        if (plotInfo.subzero)
        {
            viewTop = viewTop.add(THIRD);
        }
        if (viewTop.equals(RationalNumber.ZERO))
        {
            viewTop = RationalNumber.ONE;
        }
        
        // Find the scale (the distance in the co-ordinate space that
        // corresponds to a single pixel).
        RationalNumber width = viewLeft.subtract(viewRight);
        RationalNumber height = viewTop.subtract(viewBottom);
        RationalNumber horizontalScale = width.divide(new RationalNumber(plotInfo.imageSize.width, 1)),
                 verticalScale = height.divide(new RationalNumber(plotInfo.imageSize.height, 1));
        scale = horizontalScale.max(verticalScale);
        
        // Adjust the axes to preserve a 1-1 aspect ratio.
        if (horizontalScale.compareTo(scale) < 0)
        {
            // Re-center the view horizontally.
            RationalNumber halfExtraSpace = verticalScale.multiply
                (new RationalNumber(plotInfo.imageSize.width, 1)).subtract(width).divide(new RationalNumber(2, 1));
            viewLeft = viewLeft.add(halfExtraSpace);
            viewRight = viewRight.subtract(halfExtraSpace);
        }
        if (verticalScale.compareTo(scale) < 0)
        {
            // Extend the view vertically.
            RationalNumber extraSpace = horizontalScale.multiply
                (new RationalNumber(plotInfo.imageSize.height, 1)).subtract(height);
            viewTop = viewTop.add(extraSpace);
        }
        
        tCoordOfVAxis = tCoord(RationalNumber.ZERO);
        if (viewLeft.compareTo(RationalNumber.ZERO) < 0)
        {
            vCoordOfTAxis = plotInfo.marginWidth;
        }
        else if (viewRight.compareTo(RationalNumber.ZERO) > 0)
        {
            vCoordOfTAxis = plotInfo.marginWidth + plotInfo.imageSize.width;
        }
        else
        {
            vCoordOfTAxis = vCoord(RationalNumber.ZERO);
        }
        
        // Calculate the tick bases and starting base ticks.
        // If at least one integer fits, then
        // the tick base is an integer; otherwise it's 1/2^n where n is
        // smallest such that some m/2^n fits.
        // The starting tick is the largest tick at the base scale that
        // fits in the view.
        // The secondary tick base is 1/2^n where n is second-smallest
        // such that some m/2^n fits (in lowest terms).  For example,
        // if the window is [1025/1024,1023/1024], then 1 is the tick
        // base, and 1/1024 is the secondary tick base.
        
        // First the v tick base.
        if (viewLeft.floor().compareTo(viewRight) >= 0)
        {
            // It's an integer.  We make sure the ticks are spaced at
            // least 16 pixels apart (at most width/16 ticks).
            vTickBase = viewLeft.subtract(viewRight).divide
                (new RationalNumber(plotInfo.imageSize.width, 16)).ceiling();
        }
        else
        {
            // A non-integer.
            for (vTickBase = HALF;
                 viewLeft.divide(vTickBase).floor().compareTo(viewRight.divide(vTickBase)) < 0;
                 vTickBase = vTickBase.multiply(HALF));
        }
        startingVTick = viewLeft.divide(vTickBase).floor().multiply(vTickBase);
        for (secondaryVTickBase = vTickBase.multiply(HALF);
             viewLeft.subtract(startingVTick).compareTo(secondaryVTickBase) < 0 &&
             startingVTick.subtract(viewRight).compareTo(secondaryVTickBase) < 0;
             secondaryVTickBase = secondaryVTickBase.multiply(HALF));
        
        // Now the t tick base.
        if (viewTop.floor().compareTo(viewBottom) >= 0)
        {
            tTickBase = viewTop.subtract(viewBottom).divide
                (new RationalNumber(plotInfo.imageSize.height, 16)).ceiling();
        }
        else
        {
            for (tTickBase = HALF;
                 viewTop.divide(tTickBase).floor().compareTo(viewBottom.divide(tTickBase)) < 0;
                 tTickBase = tTickBase.multiply(HALF));
        }
        startingTTick = viewTop.divide(tTickBase).floor().multiply(tTickBase);
        for (secondaryTTickBase = tTickBase.multiply(HALF);
             viewTop.subtract(startingTTick).compareTo(secondaryTTickBase) < 0 &&
             startingTTick.subtract(viewBottom).compareTo(secondaryTTickBase) < 0;
             secondaryTTickBase = secondaryTTickBase.multiply(HALF));
        
        // Find the label height and spacing.
        FontRenderContext frc =
            ((Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            .getGraphics()).getFontRenderContext();
        LineMetrics
            numberMetrics = font.getLineMetrics("0123456789/", 0, 11, frc);
        int tickAndLabelHeight = PRIMARY_TICK_EXTENT + 4
            + ((int) (numberMetrics.getAscent() + numberMetrics.getDescent()));
        if (plotInfo.subzero)
        {
            // Adjust the tickAndLabelHeight since part (or all) of the labels
            // will lie between 0 and -1.
            tickAndLabelHeight = Math.max(
                0,
                tickAndLabelHeight - scale.reciprocal().intValue()
                );
        }
        if (!plotInfo.drawTicks)
        {
            tickAndLabelHeight = 0;
        }
        minVerticalLabelSpacing = (int) numberMetrics.getLeading();
        minHorizontalLabelSpacing = (int) new TextLayout(" ", font, frc).getAdvance();

        // Find the actual image size.
        trailerHeight = (plotInfo.subzero ? 1 : plotInfo.tailLength);
        actualImageSize = new Dimension(
            plotInfo.imageSize.width + 2 * plotInfo.marginWidth,
            plotInfo.imageSize.height + plotInfo.marginWidth + Math.max(trailerHeight, tickAndLabelHeight)
            );
        
        // Add the means and temperatures before the other critical values and
        // temperatures, so that they are given priority when labelling.
        addPrimaryTicks(vTicks, allMeans);
        addPrimaryTicks(vTicks, allCriticalValues);
        recalcStandardTicks(vTicks, true);
        addPrimaryTicks(tTicks, allTemperatures);
        addPrimaryTicks(tTicks, allCriticalTemps);
        recalcStandardTicks(tTicks, false);
        
        valid = true;
    }
    
    private void addPrimaryTicks(List<TickInfo> ticks, List<RationalNumber> locations)
    {
        for (RationalNumber r : locations)
        {
            ticks.add(new TickInfo(r, PRIMARY_TICK_EXTENT, true, false));
        }
    }
    
    private void recalcStandardTicks(List<TickInfo> ticks, boolean vTicks)
    {
        RationalNumber tickBase = vTicks ? vTickBase : tTickBase;
        RationalNumber secondaryTickBase = vTicks ? secondaryVTickBase : secondaryTTickBase;
        RationalNumber startingTick = vTicks ? startingVTick : startingTTick;
        RationalNumber viewMax = vTicks ? viewLeft : viewTop;
        RationalNumber viewMin = vTicks ? viewRight : viewBottom;
        
        int numTicksPainted = 0;
        
        // Paint the basic tick(s).
        if (tickBase.isInteger())
        {
            for (RationalNumber r = startingTick; r.compareTo(viewMin) >= 0; r = r.subtract(tickBase))
            {
                ticks.add(new TickInfo(r, STANDARD_TICK_EXTENTS[0], true, plotInfo.drawGrid));
                numTicksPainted++;
            }
        }
        else
        {
            ticks.add(new TickInfo(startingTick, STANDARD_TICK_EXTENTS[0], true, plotInfo.drawGrid));
            numTicksPainted++;
        }
        
        // Paint the sub-ticks.  We paint as few labels as possible so that
        // (i) At least two labels are painted;
        // (ii) All ticks at the same resolution are treated equally.
        // Further, we decrease the extent anytime labels were actually
        // painted at that resolution.
        int tickExtentIndex = 0;
        for (RationalNumber tickResolution = secondaryTickBase;
             tickResolution.divide(scale).intValue() >= 8 && tickExtentIndex < STANDARD_TICK_EXTENTS.length;
             tickResolution = tickResolution.multiply(HALF))
        {
            // The following makes resolutionMax the largest integer n with
            // n * tickResolution inside the view, and resolutionMin the
            // smallest.
            RationalNumber resolutionMax = viewMax.divide(tickResolution).floor(),
                     resolutionMin = viewMin.divide(tickResolution).ceiling();
            // Now we want to make sure resolutionMin and resolutionMax are odd.
            if (!resolutionMax.getNumerator().testBit(0))
            {
                resolutionMax = resolutionMax.subtract(RationalNumber.ONE);
            }
            if (!resolutionMin.getNumerator().testBit(0))
            {
                resolutionMin = resolutionMin.add(RationalNumber.ONE);
            }
            boolean paintLabels = true;
            int numTicksPaintedAtThisResolution =
                1 + (resolutionMax.subtract(resolutionMin).intValue() / 2);
            if (numTicksPainted + numTicksPaintedAtThisResolution >= 4)
            {
                tickExtentIndex++;
                paintLabels = false;
            }
            if (tickExtentIndex >= STANDARD_TICK_EXTENTS.length)
            {
                break;
            }
            // If the fourth tick will be painted at this resolution, then
            // lower the tick extent and stop painting labels.  This seems a
            // bit contrived, but it seems to produce nice-looking output in
            // all cases.
            for (RationalNumber r = resolutionMax.multiply(tickResolution);
                 r.compareTo(viewMin) >= 0;
                 r = r.subtract(tickResolution).subtract(tickResolution))
            {
                ticks.add(new TickInfo(r, STANDARD_TICK_EXTENTS[tickExtentIndex], paintLabels, plotInfo.drawGrid));
                numTicksPainted++;
            }
        }
    }
    
    @Override
    public void write(PrintWriter out, Output.Mode mode)
    {
        if (mode == Output.Mode.PLAIN_TEXT)
        {
            out.print("<Plot>");
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    public String toLatexSource()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Dimension getSize(int preferredWidth)
    {
        recalc();
        return actualImageSize;
    }
    
    @Override
    public void paint(Graphics2D graphics, int preferredWidth)
    {
        recalc();
        graphics.setBackground(Color.white);
        graphics.clearRect(0, 0, actualImageSize.width, actualImageSize.height);
        
        // Paint the axes and tick marks.
        graphics.setColor(Color.lightGray);
        if (plotInfo.drawVAxis)
        {
            graphics.drawLine(0, tCoordOfVAxis, actualImageSize.width, tCoordOfVAxis);
            if (plotInfo.drawTicks)
            {
                for (TickInfo tick : vTicks)
                {
                    paintVTick(graphics, tick);
                }
            }
        }
        if (plotInfo.drawTAxis)
        {
            graphics.drawLine(vCoordOfTAxis, 0, vCoordOfTAxis, actualImageSize.height);
            if (plotInfo.drawTicks)
            {
                for (TickInfo tick : tTicks)
                {
                    paintTTick(graphics, tick);
                }
            }
        }

        if (waterLevel != null)
        {
            // Paint the water line.
            graphics.setStroke(new BasicStroke(2.0f));
            graphics.drawLine(0, tCoord(waterLevel), actualImageSize.width, tCoord(waterLevel));
            graphics.setStroke(new BasicStroke());
        }
        
        // Paint the tick labels.
        graphics.setColor(Color.black);
        if (plotInfo.drawVAxis && plotInfo.drawTicks && plotInfo.drawTickLabels)
        {
            List<int[]> occupiedSpace = new ArrayList<int[]>();
            for (TickInfo tick : vTicks)
            {
                paintVTickLabel(graphics, tick, occupiedSpace);
            }
        }
        if (plotInfo.drawTAxis && plotInfo.drawTicks && plotInfo.drawTickLabels)
        {
            List<int[]> occupiedSpace = new ArrayList<int[]>();
            for (TickInfo tick : tTicks)
            {
                paintTTickLabel(graphics, tick, occupiedSpace);
            }
        }
        
        // Paint the trajectories.
        for (TrajectoryInfo t : trajectories)
        {
            paintTrajectory(graphics, t);
        }
    }
    
    private void paintTrajectory(Graphics2D graphics, TrajectoryInfo t)
    {
        graphics.setColor(t.color);
        
        int i, numCPs = t.trajectory.getNumCriticalPoints();
        for (i = -1; i < numCPs-1; i++)
        {
            RationalNumber cp1 = (i == -1 ? viewTop : t.trajectory.getCriticalPoint(i)),
                     cp2 = t.trajectory.getCriticalPoint(i+1);
            if (!plotInfo.subzero && t.trajectory.getCriticalPoint(i+1).compareTo(RationalNumber.ZERO) < 0 ||
                waterLevel != null && t.trajectory.getCriticalPoint(i+1).compareTo(waterLevel) < 0)
            {
                break;
            }
            graphics.drawLine(
                vCoord(t.trajectory.valueAt(cp1)) + t.hDisplacement,
                tCoord(cp1),
                vCoord(t.trajectory.valueAt(cp2)) + t.hDisplacement,
                tCoord(cp2)
                );
        }
        
        // Paint the last segment with tail.
        int tailTCoord, tailVCoord;
        if (plotInfo.subzero)
        {
            tailVCoord = vCoord(t.trajectory.valueAt(RationalNumber.NEGATIVE_ONE));
            tailTCoord = tCoord(RationalNumber.NEGATIVE_ONE);
        }
        else
        {
            tailVCoord = vCoord(t.trajectory.valueAt(RationalNumber.ZERO).subtract
                (scale.multiply(new RationalNumber(plotInfo.tailLength, 1)).multiply(t.trajectory.slopeBelow(RationalNumber.ZERO))));
            tailTCoord = tCoord(RationalNumber.ZERO) + plotInfo.tailLength;
        }
        
        if (waterLevel == null)
        {
            graphics.drawLine(
                vCoord(t.trajectory.valueAt(i == -1 ? viewTop : t.trajectory.getCriticalPoint(i))) + t.hDisplacement,
                tCoord(i == -1 ? viewTop : t.trajectory.getCriticalPoint(i)),
                tailVCoord + t.hDisplacement,
                tailTCoord
                );
        }
        else
        {
            // Set the clip to match the waterLevel.
            // We do it this way, rather than alter the line co-ordinates, so that
            // the lines are drawn consistently if the water level changes.
            Shape oldClip = graphics.getClip();
            graphics.setClip(oldClip.getBounds().intersection
                             (new Rectangle(0, 0, actualImageSize.width, tCoord(waterLevel))));
            RationalNumber extrapolatedValueAtZero =
                t.trajectory.valueAt(waterLevel).subtract(t.trajectory.slopeBelow(waterLevel).multiply(waterLevel));
            int
                v1 = vCoord(t.trajectory.valueAt(i == -1 ? viewTop : t.trajectory.getCriticalPoint(i))) + t.hDisplacement,
                t1 = tCoord(i == -1 ? viewTop : t.trajectory.getCriticalPoint(i)),
                v2 = vCoord(extrapolatedValueAtZero.subtract(scale.multiply(new RationalNumber(plotInfo.tailLength, 1))
                            .multiply(t.trajectory.slopeBelow(waterLevel)))) + t.hDisplacement,
                t2 = tCoord(RationalNumber.ZERO) + plotInfo.tailLength;
            graphics.drawLine(v1, t1, v2, t2);
            if (plotInfo.drawTailsUnderWaterLevel)
            {
                graphics.setStroke(new BasicStroke(
                    1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f,
                    new float[] { 2.0f, 3.0f },
                    0.0f
                    ));
                graphics.setClip(oldClip.getBounds().intersection
                                 (new Rectangle(0, tCoord(waterLevel), actualImageSize.width, actualImageSize.height)));
                graphics.drawLine(v1, t1, v2, t2);
                graphics.setStroke(new BasicStroke());
            }
            graphics.setClip(oldClip);
        }
        
        // Paint the mast hooks.
        int hookVCoord = vCoord(t.trajectory.valueAt(viewTop));
        graphics.drawLine(
            hookVCoord + t.hDisplacement,
            plotInfo.marginWidth,
            hookVCoord - plotInfo.hookLength + t.hDisplacement,
            plotInfo.marginWidth + plotInfo.hookLength
            );
        graphics.drawLine(
            hookVCoord + t.hDisplacement,
            plotInfo.marginWidth,
            hookVCoord + plotInfo.hookLength + t.hDisplacement,
            plotInfo.marginWidth + plotInfo.hookLength
            );
    }
    
    private void paintVTick(Graphics2D graphics, TickInfo tick)
    {
        int vCoord = vCoord(tick.location);
        // TODO: Why did I need to comment this out?  (In general the Plot code
        // needs to be thoroughly checked for compatibility with tilted masts.)
        /*
        if (vCoord < plotInfo.marginWidth || vCoord > plotInfo.marginWidth + plotInfo.imageSize.width)
        {
            throw new RuntimeException
                ("Tick algorithm failed: vCoord = " + vCoord +
                 ", margin = " + plotInfo.marginWidth + ", width = " + plotInfo.imageSize.width);
        }
        */
        graphics.drawLine(
            vCoord,
            tick.gridLine ? 0 : tCoordOfVAxis - tick.extent,
            vCoord,
            tick.gridLine ? actualImageSize.height : tCoordOfVAxis + tick.extent
            );
    }
    
    private void paintVTickLabel(Graphics2D graphics, TickInfo tick, List<int[]> occupiedSpace)
    {
        if (!tick.paintLabel)
        {
            return;
        }
        
        int vCoord = vCoord(tick.location);
        
        // Determine where the label should be drawn.
        TextLayout label = new TextLayout
            (tick.location.toString(), font, graphics.getFontRenderContext());
        int labelWidth = (int) label.getAdvance();
        int labelVCoord = vCoord - labelWidth / 2;
        if (labelVCoord < 0)
        {
            labelVCoord = 0;
        }
        else if (labelVCoord + labelWidth > actualImageSize.width)
        {
            labelVCoord = actualImageSize.width - labelWidth;
        }
        
        // Now check if it's possible to draw the label without overclustering.
        for (int[] interval : occupiedSpace)
        {
            if (labelVCoord < interval[1] + minHorizontalLabelSpacing &&
                labelVCoord + labelWidth > interval[0] - minHorizontalLabelSpacing)
            {
                // Nope.
                return;
            }
        }

        graphics.setColor(Color.black);
        label.draw(graphics, labelVCoord, tCoordOfVAxis + PRIMARY_TICK_EXTENT + 4 + ((int) label.getAscent()));
        occupiedSpace.add(new int[] { labelVCoord, labelVCoord + labelWidth });
    }
    
    private void paintTTick(Graphics2D graphics, TickInfo tick)
    {
        int tCoord = tCoord(tick.location);
        if (tCoord < 0 || tCoord > plotInfo.marginWidth + plotInfo.imageSize.height)
        {
            throw new RuntimeException("Tick algorithm failed: tCoord = " + tCoord);
        }
        graphics.setColor(Color.lightGray);
        graphics.drawLine(
            tick.gridLine ? 0 : vCoordOfTAxis - tick.extent,
            tCoord,
            tick.gridLine ? actualImageSize.width : vCoordOfTAxis + tick.extent,
            tCoord
            );
    }
    
    private void paintTTickLabel(Graphics2D graphics, TickInfo tick, List<int[]> occupiedSpace)
    {
        if (!tick.paintLabel ||
            plotInfo.drawVAxis && tick.location.equals(RationalNumber.ZERO))
        {
            // If the v axis is painted, there's no point in labelling 0
            // (and it looks rather ridiculous).
            return;
        }
        
        int tCoord = tCoord(tick.location);
            
        // Determine where the label should be drawn.
        TextLayout label = new TextLayout
            (tick.location.toString(), font, graphics.getFontRenderContext());
        int labelHeight = (int) (label.getAscent() + label.getDescent());
        int labelTCoord = tCoord - labelHeight / 2;
        if (labelTCoord < 0)
        {
            labelTCoord = 0;
        }
        else if (labelTCoord + labelHeight > actualImageSize.height)
        {
            labelTCoord = actualImageSize.height - labelHeight;
        }
        
        // Now check if it's possible to draw the label without overclustering.
        for (int[] interval : occupiedSpace)
        {
            if (labelTCoord < interval[1] + minVerticalLabelSpacing &&
                labelTCoord + labelHeight > interval[0] - minVerticalLabelSpacing)
            {
                // Nope.
                return;
            }
        }
        
        graphics.setColor(Color.black);
        if (vCoordOfTAxis < (actualImageSize.width / 2))
        {
            // Paint the label to the right of the t axis.
            label.draw(
                graphics,
                vCoordOfTAxis + PRIMARY_TICK_EXTENT + 4,
                labelTCoord + ((int) label.getAscent())
                );
        }
        else
        {
            // Paint the label to the left of the t axis.
            label.draw(
                graphics,
                vCoordOfTAxis - PRIMARY_TICK_EXTENT - 4 - ((int) label.getAdvance()),
                labelTCoord + ((int) label.getAscent())
                );
        }
        occupiedSpace.add(new int[] { labelTCoord, labelTCoord + labelHeight });
    }

    public int vCoord(RationalNumber value)
    {
        return plotInfo.marginWidth + viewLeft.subtract(value).divide(scale).intValue();
    }
    
    public int tCoord(RationalNumber temp)
    {
        try{
        return plotInfo.marginWidth + viewTop.subtract(temp).divide(scale).intValue();
        }catch (ArithmeticException exc)
        { System.out.println(scale); System.out.println(viewTop); System.out.println(temp); System.out.println(trajectories); throw exc; }
    }
    
    @Override
    public Output toOutput()
    {
        return this;
    }
    
    public static class PlotInfo
    {
        public Dimension imageSize;
        public RationalNumber viewLeft, viewRight, viewTop, viewBottom;
        public int marginWidth, hookLength, tailLength;
        public boolean drawTAxis, drawVAxis, drawGrid, drawTicks, drawTickLabels, subzero, labelCPs;
        public RationalNumber[] tickMarksT, tickMarksV;
        public int fontSize;
        public boolean drawTailsUnderWaterLevel;

        public PlotInfo()
        {
            imageSize = new Dimension(256, 256);
            viewLeft = viewRight = viewTop = viewBottom = null;
            marginWidth = hookLength = 8;
            tailLength = 16;
            drawTAxis = drawVAxis = drawTicks = drawTickLabels = true;
            drawGrid = subzero = labelCPs = false;
            tickMarksT = tickMarksV = null;
            fontSize = 12;
            drawTailsUnderWaterLevel = false;
        }
    }
    
    private static class TrajectoryInfo
    {
        Trajectory trajectory;
        Color color;
        int hDisplacement;
        
        TrajectoryInfo(Trajectory trajectory, Color color, int hDisplacement)
        {
            this.trajectory = trajectory;
            this.color = color;
            this.hDisplacement = hDisplacement;
        }
        
        public String toString()
        {
            return trajectory.toString();
        }
    }
    
    private static class TickInfo
    {
        RationalNumber location;
        int extent;
        boolean paintLabel;
        boolean gridLine;
        
        TickInfo(RationalNumber location, int extent, boolean paintLabel, boolean gridLine)
        {
            this.location = location;
            this.extent = extent;
            this.paintLabel = paintLabel;
            this.gridLine = gridLine;
        }
    }
}

