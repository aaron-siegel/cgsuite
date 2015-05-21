package org.cgsuite.lang

object SpecialMethods {

  private val specialMethods0: Map[String, (_, Unit) => Any] = Map(

    "cgsuite.lang.Object.Class" -> { (obj: Any, _: Unit) => CgscriptClass.of(obj).classObject },
    "cgsuite.lang.Object.JavaClass" -> { (obj: Any, _: Unit) => obj.getClass.getName },
    "cgsuite.lang.List.Sorted" -> { (list: Seq[_], _: Unit) => list.sorted(UniversalOrdering) },
    "cgsuite.lang.Map.Entries" -> { (map: Map[_,_], _: Unit) => map.toSeq },
    "cgsuite.lang.MapEntry.Key" -> { (entry: (_,_), _: Unit) => entry._1 },
    "cgsuite.lang.MapEntry.Value" -> { (entry: (_,_), _: Unit) => entry._2 }

  )

  private val specialMethods1: Map[String, (_, _) => Any] = Map()

  val specialMethods =
    specialMethods0.asInstanceOf[Map[String, (Any, Any) => Any]] ++
    specialMethods1.asInstanceOf[Map[String, (Any, Any) => Any]]

}
