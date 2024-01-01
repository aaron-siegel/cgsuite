package org.cgsuite.lang

import org.cgsuite.core.impartial.{HeapRuleset, Periodicity, Spawning, TakeAndBreak}
import org.cgsuite.core.misere.{Genus, MisereCanonicalGame}
import org.cgsuite.core._
import org.cgsuite.output.{EmptyOutput, GridOutput, Output, StripOutput, StyledTextOutput}
import org.cgsuite.util._

import scala.collection.immutable.NumericRange
import scala.collection.mutable

private[lang] object SystemClassRegistry {

  val baseSystemClasses: Seq[(String, Class[_])] = Seq(

    "cgsuite.lang.Object" -> classOf[AnyRef],
    "cgsuite.lang.Enum" -> classOf[EnumObject]

  )

  val typedSystemClasses: Seq[(String, Class[_])] = Seq(

    "cgsuite.lang.Class" -> classOf[ClassObject],
    "cgsuite.lang.Script" -> classOf[Script],

    "cgsuite.util.MutableList" -> classOf[mutable.ArrayBuffer[_]],
    "cgsuite.util.MutableSet" -> classOf[mutable.HashSet[_]],
    "cgsuite.util.MutableMap" -> classOf[mutable.HashMap[_,_]],

    "cgsuite.lang.Boolean" -> classOf[java.lang.Boolean],
    "cgsuite.lang.String" -> classOf[String],
    "cgsuite.lang.Coordinates" -> classOf[Coordinates],
    "cgsuite.lang.Range" -> classOf[NumericRange[_]],
    "cgsuite.lang.List" -> classOf[IndexedSeq[_]],
    "cgsuite.lang.Set" -> classOf[scala.collection.Set[_]],
    "cgsuite.lang.Map" -> classOf[scala.collection.Map[_,_]],
    "cgsuite.lang.MapEntry" -> classOf[(_,_)],
    "cgsuite.lang.Function" -> classOf[Function],
    "cgsuite.lang.System" -> classOf[System],
    "cgsuite.lang.Collection" -> classOf[Iterable[_]],
    "cgsuite.lang.InstanceClass" -> classOf[InstanceClass],

    "cgsuite.util.Strip" -> classOf[Strip],
    "cgsuite.util.Grid" -> classOf[Grid],
    "cgsuite.util.Symmetry" -> classOf[Symmetry],
    "cgsuite.util.Graph" -> classOf[Graph[_]],
    "cgsuite.util.EdgeColoredGraph" -> classOf[EdgeColoredGraph[_]],
    "cgsuite.util.Table" -> classOf[Table],
    "cgsuite.util.Thermograph" -> classOf[Thermograph],
    "cgsuite.util.Trajectory" -> classOf[Trajectory],
    "cgsuite.util.UptimalExpansion" -> classOf[UptimalExpansion],

    "cgsuite.ui.Explorer" -> classOf[Explorer],

    // The order is extremely important in the following hierarchies (most specific first)

    "cgsuite.util.output.EmptyOutput" -> classOf[EmptyOutput],
    "cgsuite.util.output.GridOutput" -> classOf[GridOutput],
    "cgsuite.util.output.StripOutput" -> classOf[StripOutput],
    "cgsuite.util.output.TextOutput" -> classOf[StyledTextOutput],
    "cgsuite.lang.Output" -> classOf[Output],

    "game.Zero" -> classOf[Zero],
    "game.Integer" -> classOf[Integer],
    "game.GeneralizedOrdinal" -> classOf[GeneralizedOrdinal],
    "game.DyadicRational" -> classOf[DyadicRationalNumber],
    "game.Rational" -> classOf[RationalNumber],
    "game.SurrealNumber" -> classOf[SurrealNumber],
    "game.Nimber" -> classOf[Nimber],
    "game.TransfiniteNimber" -> classOf[TransfiniteNimber],
    "game.Uptimal" -> classOf[Uptimal],
    "game.CanonicalShortGame" -> classOf[CanonicalShortGame],
    "game.Pseudonumber" -> classOf[Pseudonumber],
    "game.CanonicalStopper" -> classOf[CanonicalStopper],
    "game.StopperSidedValue" -> classOf[StopperSidedValue],
    "game.SidedValue" -> classOf[SidedValue],
    "game.NormalValue" -> classOf[NormalValue],

    "game.misere.Genus" -> classOf[Genus],
    "game.misere.MisereCanonicalGame" -> classOf[MisereCanonicalGame],

    "game.CompoundImpartialGame" -> classOf[CompoundImpartialGame],
    "game.CompoundGame" -> classOf[CompoundGame],
    "game.ExplicitGame" -> classOf[ExplicitGame],
    "game.NegativeGame" -> classOf[NegativeGame],

    "game.ImpartialGame" -> classOf[ImpartialGame],
    "game.Game" -> classOf[Game],

    "game.CompoundType" -> classOf[CompoundType],
    "game.Player" -> classOf[Player],
    "game.Side" -> classOf[Side],
    "game.OutcomeClass" -> classOf[LoopyOutcomeClass],

    "game.heap.TakeAndBreak" -> classOf[TakeAndBreak],
    "game.heap.Spawning" -> classOf[Spawning],
    "game.heap.HeapRuleset" -> classOf[HeapRuleset],
    "game.heap.Periodicity" -> classOf[Periodicity]

  )

  val allSystemClasses = baseSystemClasses ++ typedSystemClasses

}
