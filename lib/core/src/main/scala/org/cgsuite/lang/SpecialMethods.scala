package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.core.impartial.Spawning
import org.cgsuite.exception.EvalException
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.{Symmetry, Table, UiHarness}

import scala.collection.mutable

object SpecialMethods {

  private val specialMethods0: Map[String, (_, Unit) => Any] = Map(

    "cgsuite.lang.Object.Class" -> { (obj: Any, _: Unit) => CgscriptClass.of(obj).classObject },
    "cgsuite.lang.Object.EnclosingObject" -> { (obj: Any, _: Unit) =>
      obj match {
        case x: StandardObject => x.enclosingObj
        case _ => null
      }
    },
    "cgsuite.lang.Object.JavaClass" -> { (obj: Any, _: Unit) => obj.getClass.getName },
    "cgsuite.lang.Object.ToOutput" -> { (obj: Any, _: Unit) => CgscriptClass instanceToDefaultOutput obj },
    "cgsuite.lang.Collection.Head" -> { (collection: Iterable[_], _: Unit) =>
      if (collection.isEmpty) throw EvalException("That `Collection` is empty.") else collection.head
    },
    "cgsuite.lang.Collection.Mex" -> { (collection: Iterable[_], _: Unit) =>
      val intCollection = collection collect {
        case int: SmallInteger if int.intValue >= 0 => int.intValue
      }
      Integer(ImpartialGame.mex(intCollection))
    },
    "cgsuite.lang.Collection.Tail" -> { (collection: Iterable[_], _: Unit) =>
      if (collection.isEmpty) throw EvalException("That `Collection` is empty.") else collection.tail
    },
    "cgsuite.lang.Collection.ToList" -> { (collection: Iterable[_], _: Unit) => collection.toIndexedSeq },
    "cgsuite.lang.List.Sorted" -> { (list: IndexedSeq[_], _: Unit) => list.sorted(UniversalOrdering) },
    "cgsuite.lang.Map.Entries" -> { (map: scala.collection.Map[_,_], _: Unit) => map.toSet },
    "cgsuite.lang.Map.Keys" -> { (map: scala.collection.Map[_,_], _: Unit) => map.keySet },
    "cgsuite.lang.Map.Values" -> { (map: scala.collection.Map[_,_], _: Unit) => map.values.toSet },
    "cgsuite.lang.MapEntry.Key" -> { (entry: (_,_), _: Unit) => entry._1 },
    "cgsuite.lang.MapEntry.ToOutput" -> { (entry: (_,_), _: Unit) => OutputBuilder.toOutput(entry) },
    "cgsuite.lang.MapEntry.Value" -> { (entry: (_,_), _: Unit) => entry._2 },
    "cgsuite.util.Symmetry.Literal" -> { (symmetry: Symmetry, _: Unit) => symmetry.toString },
    "game.Player.Literal" -> { (player: Player, _: Unit) => player.toString },
    "game.Side.Literal" -> { (side: Side, _: Unit) => side.toString },
    "game.OutcomeClass.Literal" -> { (outcomeClass: LoopyOutcomeClass, _: Unit) => outcomeClass.toString }

  )

  private val specialMethods1: Map[String, (_, _) => Any] = Map(

    "cgsuite.lang.Collection.Adjoin" -> { (collection: Iterable[_], obj: Any) => collection ++ Iterable(obj) },
    "cgsuite.lang.Collection.Concat" -> { (collection: Iterable[_], that: Iterable[_]) => collection ++ that },
    "cgsuite.lang.Collection.Exists" -> { (collection: Iterable[_], fn: Function) =>
      collection.exists { x => fn.call(Array(x)).asInstanceOf[Boolean] }
    },
    "cgsuite.lang.Collection.Find" -> { (collection: Iterable[_], fn: Function) =>
      collection.asInstanceOf[Iterable[Any]].find { x => fn.call(Array(x)).asInstanceOf[Boolean] }.orNull
    },
    "cgsuite.lang.Collection.ForAll" -> { (collection: Iterable[_], fn: Function) =>
      collection.forall { x => fn.call(Array(x)).asInstanceOf[Boolean] }
    },
    "cgsuite.lang.Collection.ForEach" -> { (collection: Iterable[_], fn: Function) =>
      collection.foreach { x => fn.call(Array(x)) }; null
    },
    "cgsuite.lang.List.Grouped" -> { (list: IndexedSeq[_], n: Integer) =>
      list.grouped(n.intValue).toVector
    },
    "cgsuite.lang.List.MkOutput" -> { (list: IndexedSeq[_], sep: String) =>
      val output = new StyledTextOutput
      var first = true
      list foreach { x =>
        if (!first)
          output appendMath sep
        first = false
        output append CgscriptClass.instanceToOutput(x)
      }
      output
    },
    "cgsuite.lang.Map.ContainsKey" -> { (map: scala.collection.Map[Any,_], key: Any) =>
      map contains key
    },
    "cgsuite.lang.Set.Intersection" -> { (set: scala.collection.Set[Any], that: scala.collection.Set[Any]) =>
      set intersect that
    },
    "cgsuite.lang.Set.Replaced" -> { (set: scala.collection.Set[Any], replacements: scala.collection.Map[_,_]) =>
      set -- replacements.keys ++ replacements.values
    },
    "cgsuite.lang.Set.Union" -> { (set: scala.collection.Set[Any], that: Iterable[Any]) =>
      set ++ that
    },
    "cgsuite.ui.Explorer" -> { (_: Any, g: Game) => UiHarness.uiHarness.createExplorer(g) },
    "cgsuite.util.MutableList.Add" -> { (list: mutable.ArrayBuffer[Any], x: Any) => list += x; null },
    "cgsuite.util.MutableList.AddAll" -> { (list: mutable.ArrayBuffer[Any], x: Iterable[_]) => list ++= x; null },
    "cgsuite.util.MutableList.Remove" -> { (list: mutable.ArrayBuffer[Any], x: Any) => list -= x; null },
    "cgsuite.util.MutableList.RemoveAll" -> { (list: mutable.ArrayBuffer[Any], x: Iterable[_]) => list --= x; null },
    "cgsuite.util.MutableSet.Add" -> { (set: mutable.Set[Any], x: Any) => set += x; null },
    "cgsuite.util.MutableSet.AddAll" -> { (set: mutable.Set[Any], x: Iterable[_]) => set ++= x; null },
    "cgsuite.util.MutableSet.Remove" -> { (set: mutable.Set[Any], x: Any) => set -= x; null },
    "cgsuite.util.MutableSet.RemoveAll" -> { (set: mutable.Set[Any], x: Iterable[_]) => set --= x; null },
    "cgsuite.util.MutableMap.PutAll" -> { (map: mutable.Map[Any,Any], x: scala.collection.Map[_,_]) => map ++= x; null },
    "cgsuite.util.MutableMap.Remove" -> { (map: mutable.Map[Any,Any], x: Any) => map -= x; null },
    "cgsuite.util.MutableMap.RemoveAll" -> { (map: mutable.Map[Any,Any], x: Iterable[_]) => map --= x; null },
    "cgsuite.util.Table" -> { (_: ClassObject, rows: IndexedSeq[_]) =>
      Table { rows map {
        case list: IndexedSeq[_] => list
        case _ => throw EvalException("The rows of a `Table` must all have type `cgsuite.lang.List`.")
      } } (OutputBuilder.toOutput)
    },
    "game.heap.Spawning" -> { (_: Any, str: String) => Spawning(str) }

  )

  private val specialMethods2: Map[String, (_, _) => Any] = Map(

    "cgsuite.lang.List.Updated" -> { (list: Seq[_], kv: (Integer, Any)) =>
      val i = kv._1.intValue
      if (i >= 1 && i <= list.length)
        list.updated(i - 1, kv._2)
      else
        throw EvalException(s"List index out of bounds: $i")
    },
    "cgsuite.util.MutableMap.Put" -> { (map: mutable.Map[Any,Any], kv: (Any, Any)) => map(kv._1) = kv._2; null }

  )

  val specialMethods =
    specialMethods0.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods1.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods2.asInstanceOf[Map[String, (Any, Any) => Any]]

}
