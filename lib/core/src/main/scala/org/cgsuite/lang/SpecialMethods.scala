package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.util.Symmetry

import scala.collection.mutable

object SpecialMethods {

  private val specialMethods0: Map[String, (_, Unit) => Any] = Map(

    "cgsuite.lang.Object.Class" -> { (obj: Any, _: Unit) => CgscriptClass.of(obj).classObject },
    "cgsuite.lang.Object.JavaClass" -> { (obj: Any, _: Unit) => obj.getClass.getName },
    "cgsuite.lang.Collection.Head" -> { (collection: Iterable[_], _: Unit) =>
      if (collection.isEmpty) throw InputException("That `Collection` is empty.") else collection.head
    },
    "cgsuite.lang.Collection.Mex" -> { (collection: Iterable[_], _: Unit) =>
      val intCollection = collection collect {
        case int: SmallInteger if int.intValue >= 0 => int.intValue
      }
      Integer(ImpartialGame.mex(intCollection))
    },
    "cgsuite.lang.Collection.Tail" -> { (collection: Iterable[_], _: Unit) =>
      if (collection.isEmpty) throw InputException("That `Collection` is empty.") else collection.tail
    },
    "cgsuite.lang.List.Sorted" -> { (list: Seq[_], _: Unit) => list.sorted(UniversalOrdering) },
    "cgsuite.lang.Map.Entries" -> { (map: scala.collection.Map[_,_], _: Unit) => map.toSeq },
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
    "cgsuite.lang.List.Grouped" -> { (list: Seq[_], n: Integer) =>
      list.grouped(n.intValue).toIterable
    },
    "cgsuite.lang.Set.Replaced" -> { (set: scala.collection.Set[Any], replacements: scala.collection.Map[_,_]) =>
      set -- replacements.keys ++ replacements.values
    },
    "cgsuite.util.MutableList.Add" -> { (list: mutable.ArrayBuffer[Any], x: Any) => list += x; Nil },
    "cgsuite.util.MutableList.AddAll" -> { (list: mutable.ArrayBuffer[Any], x: Iterable[_]) => list ++= x; Nil },
    "cgsuite.util.MutableList.Remove" -> { (list: mutable.ArrayBuffer[Any], x: Any) => list -= x; Nil },
    "cgsuite.util.MutableList.RemoveAll" -> { (list: mutable.ArrayBuffer[Any], x: Iterable[_]) => list --= x; Nil },
    "cgsuite.util.MutableSet.Add" -> { (set: mutable.Set[Any], x: Any) => set += x; Nil },
    "cgsuite.util.MutableSet.AddAll" -> { (set: mutable.Set[Any], x: Iterable[_]) => set ++= x; Nil },
    "cgsuite.util.MutableSet.Remove" -> { (set: mutable.Set[Any], x: Any) => set -= x; Nil },
    "cgsuite.util.MutableSet.RemoveAll" -> { (set: mutable.Set[Any], x: Iterable[_]) => set --= x; Nil },
    "cgsuite.util.MutableMap.PutAll" -> { (map: mutable.Map[Any,Any], x: scala.collection.Map[_,_]) => map ++= x; Nil },
    "cgsuite.util.MutableMap.Remove" -> { (map: mutable.Map[Any,Any], x: Any) => map -= x; Nil },
    "cgsuite.util.MutableMap.RemoveAll" -> { (map: mutable.Map[Any,Any], x: Iterable[_]) => map --= x; Nil }

  )

  private val specialMethods2: Map[String, (_, _) => Any] = Map(

    "cgsuite.lang.List.Updated" -> { (list: Seq[_], kv: (Integer, Any)) => list.updated(kv._1.intValue-1, kv._2) },
    "cgsuite.util.MutableMap.Put" -> { (map: mutable.Map[Any,Any], kv: (Any, Any)) => map(kv._1) = kv._2; Nil }

  )

  val specialMethods =
    specialMethods0.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods1.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods2.asInstanceOf[Map[String, (Any, Any) => Any]]

}
