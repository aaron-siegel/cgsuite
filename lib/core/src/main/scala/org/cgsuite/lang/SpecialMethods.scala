package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.util.Symmetry

object SpecialMethods {

  private val specialMethods0: Map[String, (_, Unit) => Any] = Map(

    "cgsuite.lang.Object.Class" -> { (obj: Any, _: Unit) => CgscriptClass.of(obj).classObject },
    "cgsuite.lang.Object.JavaClass" -> { (obj: Any, _: Unit) => obj.getClass.getName },
    "cgsuite.lang.Collection.Head" -> { (collection: Iterable[_], _: Unit) =>
      if (collection.isEmpty) throw InputException("That Collection is empty.") else collection.head
    },
    "cgsuite.lang.Collection.Tail" -> { (collection: Iterable[_], _: Unit) =>
      if (collection.isEmpty) throw InputException("That Collection is empty.") else collection.tail
    },
    "cgsuite.lang.List.Sorted" -> { (list: Seq[_], _: Unit) => list.sorted(UniversalOrdering) },
    "cgsuite.lang.Map.Entries" -> { (map: Map[_,_], _: Unit) => map.toSeq },
    "cgsuite.lang.MapEntry.Key" -> { (entry: (_,_), _: Unit) => entry._1 },
    "cgsuite.lang.MapEntry.Value" -> { (entry: (_,_), _: Unit) => entry._2 },
    "cgsuite.util.Symmetry.Literal" -> { (symmetry: Symmetry, _: Unit) => symmetry.toString },
    "game.Player.Literal" -> { (player: Player, _: Unit) => player.toString },
    "game.Side.Literal" -> { (side: Side, _: Unit) => side.toString }

  )

  private val specialMethods1: Map[String, (_, _) => Any] = Map(

    "cgsuite.lang.Collection.Exists" -> { (collection: Iterable[_], proc: Procedure) =>
      collection.exists { x => proc.call(Array(x)).asInstanceOf[Boolean] }
    },
    "cgsuite.lang.Collection.ForAll" -> { (collection: Iterable[_], proc: Procedure) =>
      collection.forall { x => proc.call(Array(x)).asInstanceOf[Boolean] }
    },
    "cgsuite.lang.Set.Replaced" -> { (set: Set[Any], replacements: Map[_,_]) =>
      set -- replacements.keys ++ replacements.values
    }

  )

  private val specialMethods2: Map[String, (_, _) => Any] = Map(

    "cgsuite.lang.List.Updated" -> { (list: Seq[_], kv: (Integer, Any)) => list.updated(kv._1.intValue-1, kv._2) }

  )

  val specialMethods =
    specialMethods0.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods1.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods2.asInstanceOf[Map[String, (Any, Any) => Any]]

}
