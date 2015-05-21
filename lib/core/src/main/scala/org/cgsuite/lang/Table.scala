/*
 * Table.java
 *
 * Created on March 7, 2003, 8:21 PM
 * $Id: Table.java,v 1.19 2008/01/11 02:53:05 haoyuep Exp $
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
package org.cgsuite.lang

import org.cgsuite.core.Integer
import org.cgsuite.lang.Table.Format
import org.cgsuite.output.{IntensityPlotOutput, Output, OutputTarget, TableOutput}

/**
 * A table whose entries may be any objects.  This is primarily useful for
 * plug-ins that want to display information in tabular form.  When output is
 * requested for a <code>Table</code>, it is displayed in a nicely arranged
 * grid format.
 *
 * @author  Aaron Siegel
 * @author  Dan Hoey
 * @version $Revision: 1.19 $ $Date: 2008/01/11 02:53:05 $
 */
object Table {

  /**
   * A format specifier for a <code>Table</code>.
   *
   * @author  Aaron Siegel
   * @version $Revision: 1.19 $ $Date: 2008/01/11 02:53:05 $
   * @since   0.7
   */
  object Format extends Enumeration {
    type Format = Value
    val HorizontalGridLines, VerticalGridLines = Value
  }

}

case class Table (
  rows: Seq[Seq[_]],
  format: Set[Format.Value] = Set(Format.HorizontalGridLines, Format.VerticalGridLines)
  )(outputBuilder: Any => Output) extends Iterable[Seq[_]] with OutputTarget {

  def iterator: Iterator[Seq[_]] = rows.iterator

  def toOutput: TableOutput = {
    TableOutput(rows map { _ map { outputBuilder } }, format, Int.MaxValue)
  }

  def intensityPlot: IntensityPlotOutput = {
    val ints = rows map { _ map { entry =>
      entry.asInstanceOf[Integer].intValue
    } }
    IntensityPlotOutput(ints)
  }

}
