package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.core.impartial.Spawning
import org.cgsuite.exception.{EvalException, InvalidArgumentException}
import org.cgsuite.lang.CgscriptClass.SafeCast
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.{Symmetry, Table}

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
    "cgsuite.lang.Collection.Flattened" -> { (collection: Iterable[_], _: Unit) =>
      collection flatMap {
        case it: Iterable[_] => it
        case obj => Some(obj)
      }
    },
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
    "cgsuite.util.MutableList.Sort" -> { (list: mutable.ArrayBuffer[Any], _: Unit) => list.sortInPlace()(UniversalOrdering) },
    "cgsuite.util.MutableMap.Entries" -> { (map: scala.collection.Map[_,_], _: Unit) => map.toSet },
    "cgsuite.util.Symmetry.Literal" -> { (symmetry: Symmetry, _: Unit) => symmetry.toString },
    "game.Player.Literal" -> { (player: Player, _: Unit) => player.toString },
    "game.Side.Literal" -> { (side: Side, _: Unit) => side.toString },
    "game.OutcomeClass.Literal" -> { (outcomeClass: LoopyOutcomeClass, _: Unit) => outcomeClass.toString }

  )

  private val specialMethods1: Map[String, (_, _) => Any] = Map(

    "cgsuite.lang.Collection.Adjoin" -> { (collection: Iterable[_], obj: Any) => collection ++ Iterable(obj) },
    "cgsuite.lang.Collection.Concat" -> { (collection: Iterable[_], that: Iterable[_]) => collection ++ that },
    "cgsuite.lang.Collection.Exists" -> { (collection: Iterable[_], fn: Function) =>
      validateArity(fn, 1)
      collection.exists { x => fn.call(Array(x)).castAs[java.lang.Boolean] }
    },
    "cgsuite.lang.Collection.Filter" -> { (collection: Iterable[_], fn: Function) =>
      validateArity(fn, 1)
      collection.filter { x => fn.call(Array(x)).castAs[java.lang.Boolean] }
    },
    "cgsuite.lang.Collection.Find" -> { (collection: Iterable[_], fn: Function) =>
      validateArity(fn, 1)
      collection.find { x => fn.call(Array(x)).castAs[java.lang.Boolean] }.orNull
    },
    "cgsuite.lang.Collection.ForAll" -> { (collection: Iterable[_], fn: Function) =>
      validateArity(fn, 1)
      collection.forall { x => fn.call(Array(x)).castAs[java.lang.Boolean] }
    },
    "cgsuite.lang.Collection.ForEach" -> { (collection: Iterable[_], fn: Function) =>
      validateArity(fn, 1)
      collection.foreach { x => fn.call(Array(x)) }
      null
    },
    "cgsuite.lang.List.IndexOf" -> { (list: IndexedSeq[_], obj: Any) =>
      Integer(list.indexOf(obj) + 1)
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
    "cgsuite.lang.List.SortedWith" -> { (list: IndexedSeq[_], fn: Function) =>
      validateArity(fn, 2)
      list.sorted((x: Any, y: Any) => fn.call(Array(x, y)).castAs[Integer].intValue)
    },
    "cgsuite.lang.List.Take" -> { (collection: Iterable[_], n: Integer) =>
      collection.take(n.intValue)
    },
    "cgsuite.lang.Map.ContainsKey" -> { (map: scala.collection.Map[Any,_], key: Any) =>
      map contains key
    },
    "cgsuite.lang.Set.Intersection" -> { (set: scala.collection.Set[Any], that: scala.collection.Set[Any]) =>
      set intersect that
    },
    "cgsuite.lang.Set.Replaced" -> { (set: scala.collection.immutable.Set[Any], replacements: scala.collection.Map[_,_]) =>
      set -- replacements.keys ++ replacements.values
    },
    "cgsuite.lang.Set.Union" -> { (set: scala.collection.Set[Any], that: Iterable[Any]) =>
      set ++ that
    },
    "cgsuite.lang.String.op []" -> { (str: String, index: Integer) => str.charAt(index.intValue).toString },
    "cgsuite.util.MutableList.Add" -> { (list: mutable.ArrayBuffer[Any], x: Any) => list += x; null },
    "cgsuite.util.MutableList.AddAll" -> { (list: mutable.ArrayBuffer[Any], x: Iterable[_]) => list ++= x; null },
    "cgsuite.util.MutableList.Remove" -> { (list: mutable.ArrayBuffer[Any], x: Any) => list -= x; null },
    "cgsuite.util.MutableList.RemoveAll" -> { (list: mutable.ArrayBuffer[Any], x: Iterable[_]) => list --= x; null },
    "cgsuite.util.MutableSet.Add" -> { (set: mutable.Set[Any], x: Any) => set += x; null },
    "cgsuite.util.MutableSet.AddAll" -> { (set: mutable.Set[Any], x: Iterable[_]) => set ++= x; null },
    "cgsuite.util.MutableSet.Remove" -> { (set: mutable.Set[Any], x: Any) => set -= x; null },
    "cgsuite.util.MutableSet.RemoveAll" -> { (set: mutable.Set[Any], x: Iterable[_]) => set --= x; null },
    "cgsuite.util.MutableMap.ContainsKey" -> { (map: scala.collection.Map[Any,_], key: Any) =>
      map contains key
    },
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

    "cgsuite.lang.List.Sublist" -> { (list: IndexedSeq[_], range: (Integer, Integer)) =>
      list.slice(range._1.intValue - 1, range._2.intValue)
    },
    "cgsuite.lang.List.Updated" -> { (list: IndexedSeq[_], kv: (Integer, Any)) =>
      val i = kv._1.intValue
      if (i >= 1 && i <= list.length)
        list.updated(i - 1, kv._2)
      else
        throw EvalException(s"List index out of bounds: $i")
    },
    "cgsuite.lang.String.Replace" -> { (str: String, args: (String, String)) => str.replace(args._1, args._2) },
    "cgsuite.lang.String.ReplaceRegex" -> { (str: String, args: (String, String)) => str.replaceAll(args._1, args._2) },
    "cgsuite.lang.String.Substring" -> { (str: String, range: (Integer, Integer)) =>
      str.substring(range._1.intValue - 1, range._2.intValue)
    },
    "cgsuite.lang.String.Updated" -> { (str: String, kv: (Integer, String)) =>
      str.updated(kv._1.intValue - 1, kv._2.head)
    },
    "cgsuite.util.MutableList.Update" -> { (list: mutable.ArrayBuffer[Any], args: (Integer, Any)) =>
      list.update(args._1.intValue - 1, args._2); null
    },
    "cgsuite.util.MutableMap.Put" -> { (map: mutable.Map[Any,Any], kv: (Any, Any)) => map(kv._1) = kv._2; null }

  )

  val specialMethods =
    specialMethods0.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods1.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods2.asInstanceOf[Map[String, (Any, Any) => Any]]

  def validateArity(fn: Function, arity: Int): Unit = {
    if (fn.parameters.length != arity) {
      throw InvalidArgumentException(s"Function has invalid number of parameters (expecting $arity, has ${fn.parameters.length}).")
    }
  }

}
