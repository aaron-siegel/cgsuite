package org.cgsuite.lang

import org.cgsuite.core.{Side, Player}
import org.cgsuite.util.Symmetry

object SpecialMethods {

  private val specialMethods0: Map[String, (_, Unit) => Any] = Map(

    "cgsuite.lang.Object.Class" -> { (obj: Any, _: Unit) => CgscriptClass.of(obj).classObject },
    "cgsuite.lang.Object.JavaClass" -> { (obj: Any, _: Unit) => obj.getClass.getName },
    "cgsuite.lang.List.Sorted" -> { (list: Seq[_], _: Unit) => list.sorted(UniversalOrdering) },
    "cgsuite.lang.Map.Entries" -> { (map: Map[_,_], _: Unit) => map.toSeq },
    "cgsuite.lang.MapEntry.Key" -> { (entry: (_,_), _: Unit) => entry._1 },
    "cgsuite.lang.MapEntry.Value" -> { (entry: (_,_), _: Unit) => entry._2 },
    "cgsuite.util.Symmetry.Literal" -> { (symmetry: Symmetry, _: Unit) => symmetry.toString },
    "game.Player.Literal" -> { (player: Player, _: Unit) => player.toString },
    "game.Side.Literal" -> { (side: Side, _: Unit) => side.toString }

  )

  private val specialMethods1: Map[String, (_, _) => Any] = Map()

  val specialMethods =
    specialMethods0.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods1.asInstanceOf[Map[String, (Any, Any) => Any]]

}
