package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.exception.{CgsuiteException, EvalException}

import scala.collection.mutable

object Resolver {

  private val resolvers = mutable.AnyRefMap[Symbol, Resolver]()

  def forId(id: Symbol): Resolver = resolvers.getOrElseUpdate(id, Resolver(id))

  def clearAll(): Unit = resolvers.clear()

}

case class Resolver(id: Symbol) {

  var resolutions: Array[Resolution] = new Array(0)
  var staticResolutions: Array[Resolution] = new Array(0)

  def findResolution(x: Any): Resolution = {

    // No way to avoid this lookup
    val cls = CgscriptClass.of(x)
    if (cls == CgscriptClass.Class) {
      findStaticResolution(x.asInstanceOf[ClassObject])
    } else {
      findResolutionForClass(cls)
    }

  }

  // TODO This should go away when statics do
  def findStaticResolution(co: ClassObject): Resolution = {
    if (staticResolutions.length <= co.forClass.classOrdinal) {
      staticResolutions = staticResolutions ++ new Array[Resolution](co.forClass.classOrdinal + 1 - staticResolutions.length)
    }
    var res = staticResolutions(co.forClass.classOrdinal)
    if (res == null) {
      res = Resolution(co.forClass, id, static = true)
      staticResolutions(co.forClass.classOrdinal) = res
    }
    res
  }

  def findResolutionForClass(cls: CgscriptClass): Resolution = {
    if (resolutions.length <= cls.classOrdinal) {
      // Grow the array. This can happen at most O(#classes) times.
      resolutions = resolutions ++ new Array[Resolution](cls.classOrdinal + 1 - resolutions.length)
    }
    var res = resolutions(cls.classOrdinal)
    if (res == null) {
      res = Resolution(cls, id)
      resolutions(cls.classOrdinal) = res
    }
    res
  }

  def resolve(x: Any, asFunctionCallAntecedent: Boolean, referenceToken: Token): Any = {
    findResolution(x).evaluateFor(x, asFunctionCallAntecedent, referenceToken)
  }

}

case class Resolution(cls: CgscriptClass, id: Symbol, static: Boolean = false) {

  val classScopeIndex = {
    if (static) {
      cls.classInfo.staticVarOrdinals.getOrElse(id, -1)
    } else {
      cls.classInfo.instanceVarOrdinals.getOrElse(id, -1)
    }
  }
  // TODO Static mutable?
  val isMutableVar = !static && (cls.classInfo.instanceVarLookup get id exists { _.isMutable })
  val nestedClass = {
    // TODO Static nested classes?
    if (static) {
      None
    } else {
      cls.lookupNestedClass(id)
    }
  }
  val methodGroup = {
    if (static) {
      // This is a little more complicated since we also need to look up common
      // static methods, i.e., methods of class Class.
      CgscriptClass.Class.lookupMethodGroup(id) match {
        case Some(x) => Some(x)
        case None => cls.lookupMethodGroup(id)
      }
    } else {
      // In the instance case we can just do a straight lookup.
      cls.lookupMethodGroup(id)
    }
  }

  assert(classScopeIndex == -1 || methodGroup.isEmpty)

  val isResolvable = classScopeIndex >= 0 || methodGroup.isDefined || nestedClass.isDefined

  def evaluateFor(baseObject: Any, asFunctionCallAntecedent: Boolean, referenceToken: Token): Any = {

    if (classScopeIndex >= 0) {

      baseObject match {
        case obj: StandardObject => obj.vars(classScopeIndex)
        case _ => sys error s"This shouldn't happen: ${cls.qualifiedName}.${id.name}"
      }

    } else if (nestedClass.isDefined) {

      InstanceClass(baseObject, nestedClass.get)

    } else {

      if (methodGroup.isDefined) {

        val group = methodGroup.get

        if (group.isPureAutoinvoke || group.autoinvokeMethod.isDefined && !asFunctionCallAntecedent) {

          try {
            group.autoinvokeMethod.get.call(baseObject, Array.empty)
          } catch {
            case exc: CgsuiteException =>
              exc addToken referenceToken
              throw exc
            case _: StackOverflowError =>
              throw EvalException("Possible infinite recursion.")
          }

        } else if (asFunctionCallAntecedent) {

          InstanceMethodGroup(baseObject, group)

        } else {

          sys error "not resolvable"      // TODO Err message like "expecting arguments", I think

        }

      } else {

        sys error "not resolvable"

      }
    }

  }

}
