package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.core._
import org.cgsuite.exception.CgsuiteException
import org.cgsuite.output.{Output, OutputTarget}
import org.cgsuite.util.TranspositionTable

import scala.collection.mutable

class StandardObject(val cls: CgscriptClass, objArgs: Array[Any]) extends OutputTarget {

  private[lang] var vars: Array[Any] = _
  init()

  def init() {
    vars = new Array[Any](cls.classInfo.allClassVars.size)
    JSystem.arraycopy(objArgs, 0, vars, 0, objArgs.length)
    val domain = new Domain(null, Some(this))
    cls.ancestors foreach { ancestor =>
      ancestor.initializers foreach { node => node.body evaluate domain }
    }
  }

  val cachedHashCode: Option[Int] = {
    if (cls.isMutable) {
      None
    } else {
      Some(cls.hashCode() ^ java.util.Arrays.hashCode(vars.asInstanceOf[Array[AnyRef]]))
    }
  }

  override def equals(other: Any) = other match {
    case obj: StandardObject =>
      (this eq obj) ||
        cls == obj.cls && java.util.Arrays.equals(vars.asInstanceOf[Array[AnyRef]], obj.vars.asInstanceOf[Array[AnyRef]])
    case _ => false
  }

  override def hashCode(): Int = {
    cachedHashCode match {
      case Some(hc) => hc
      case None => cls.hashCode() ^ java.util.Arrays.hashCode(vars.asInstanceOf[Array[AnyRef]])
    }
  }

  def toOutput: Output = {
    cls.classInfo.toOutputMethod.call(this, Array.empty).asInstanceOf[Output]
  }

  def lookupInstanceMethod(id: Symbol): Option[Any] = {
    cls.lookupMethod(id).map { method =>
      if (method.isStatic) sys.error("foo")
      if (method.autoinvoke)
        method.call(this, Array.empty)
      else
        InstanceMethod(this, method)
    }
  }

}

object GameObject {

  private def callCanonicalForm(g: Game, tt: TranspositionTable) = {
    g match {
      case go: GameObject => go.cls.classInfo.canonicalFormMethod.call(go, Array.empty).asInstanceOf[CanonicalShortGame]
      case _ => g.shortCanonicalForm(tt)
    }
  }

  private def callDepthHint(go: GameObject): Integer = {
    go.cls.classInfo.depthHintMethod.call(go, Array.empty).asInstanceOf[Integer]
  }

  private def callGameValue(go: GameObject): SidedValue = {
    go.cls.classInfo.gameValueMethod.call(go, Array.empty).asInstanceOf[SidedValue]
  }

  private val visited = mutable.HashSet[Game]()

  private var cnt = 0

}

class EnumObject(cls: CgscriptClass, val literal: String) extends StandardObject(cls, Array.empty)

class GameObject(cls: CgscriptClass, objArgs: Array[Any]) extends StandardObject(cls, objArgs) with Game {

  def options(player: Player) = {
    cls.classInfo.optionsMethod.call(this, Array(player)).asInstanceOf[Seq[Game]]
  }

  override def gameValue: SidedValue = {
    try {
      canonicalForm
    } catch {
      case _: NotShortGameException => loopyGameValue
    }
  }

  final def loopyGameValue: SidedValue = {
    cls.transpositionTable.get(this) match {
      case Some(x: SidedValue) => x
      case Some(x) => sys.error("this should never happen")
      case _ =>
        val nodeMap = mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)]()
        buildNodeMap(nodeMap)
        nodeMap foreach { case (g, (onsideNode, offsideNode)) =>
          if (!cls.transpositionTable.contains(g)) {
            val value = SidedValue(onsideNode, offsideNode)
            cls.transpositionTable.put(g, value)
          }
        }
        cls.transpositionTable(this).asInstanceOf[SidedValue]
    }
  }

  private def buildNodeMap(nodeMap: mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)]): (LoopyGame.Node, LoopyGame.Node) = {
    nodeMap.get(this) match {
      case Some(nodes) => nodes
      case None =>
        val decomp = decomposition
        if (decomp.size == 1 && decomp.head == this) {
          buildNodeMapR(nodeMap)
        } else {
          var result: SidedValue = Values.zero
          decomp foreach {
            case sv: SidedValue => result += sv
            case go: GameObject => result += go.loopyGameValue
          }
          val nodes = (new LoopyGame.Node(result.onside.loopyGame), new LoopyGame.Node(result.offside.loopyGame))
          nodeMap.put(this, nodes)
          nodes
        }
    }
  }

  private def buildNodeMapR(nodeMap: mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)]): (LoopyGame.Node, LoopyGame.Node) = {

    val onsideNode = new LoopyGame.Node()
    val offsideNode = new LoopyGame.Node()
    nodeMap.put(this, (onsideNode, offsideNode))
    val depth = GameObject.callDepthHint(this)
    options(Left) foreach {
      case go: GameObject =>
        if (GameObject.callDepthHint(go) < depth) {
          val value = go.loopyGameValue
          onsideNode.addLeftEdge(value.onside.loopyGame)
          offsideNode.addLeftEdge(value.offside.loopyGame)
        } else {
          val (onsideTarget, offsideTarget) = go.buildNodeMap(nodeMap)
          onsideNode.addLeftEdge(onsideTarget)
          offsideNode.addLeftEdge(offsideTarget)
        }
      case g =>
        val canonicalForm = g.shortCanonicalForm(cls.transpositionTable)   // TODO Handle loopy exits too
        onsideNode.addLeftEdge(new LoopyGame.Node(canonicalForm))
        offsideNode.addLeftEdge(new LoopyGame.Node(canonicalForm))
    }
    options(Right) foreach {
      case go: GameObject =>
        if (GameObject.callDepthHint(go) < depth) {
          val value = go.loopyGameValue
          onsideNode.addRightEdge(value.onside.loopyGame)
          offsideNode.addRightEdge(value.offside.loopyGame)
        } else {
          val (onsideTarget, offsideTarget) = go.buildNodeMap(nodeMap)
          onsideNode.addRightEdge(onsideTarget)
          offsideNode.addRightEdge(offsideTarget)
        }
      case g =>
        val canonicalForm = g.shortCanonicalForm(cls.transpositionTable)   // TODO Handle loopy exits too
        onsideNode.addRightEdge(new LoopyGame.Node(canonicalForm))
        offsideNode.addRightEdge(new LoopyGame.Node(canonicalForm))
    }
    (onsideNode, offsideNode)

  }

  override def canonicalForm: CanonicalShortGame = {
    val decomp = decomposition
    if (decomp.size == 1 && decomp.head == this) {
      canonicalFormR
    } else {
      var result: CanonicalShortGame = Values.zero
      val it = decomp.iterator
      while (it.hasNext) {
        val component = it.next() match {
          case go: GameObject => go.canonicalFormR
          case g: Game => g.shortCanonicalForm(cls.transpositionTable)
        }
        result += component
      }
      result
    }
  }

  private def canonicalFormR: CanonicalShortGame = {
    cls.transpositionTable.get(this) match {
      case Some(x: CanonicalShortGame) => x
      case x =>
        if (x.isDefined || GameObject.visited.contains(this)) {
          throw NotShortGameException(s"That `${cls.qualifiedName}` is not a short game. If that is intentional, try `GameValue` in place of `CanonicalForm`.")
        }
        GameObject.visited += this
        try {
          val lo = options(Left ) map { GameObject.callCanonicalForm(_, cls.transpositionTable) }
          val ro = options(Right) map { GameObject.callCanonicalForm(_, cls.transpositionTable) }
          val canonicalForm = CanonicalShortGame(lo, ro)
          cls.transpositionTable.put(this, canonicalForm)
          canonicalForm
        } finally {
          GameObject.visited -= this
        }
    }
  }

  override def decomposition: Seq[_] = {
    cls.classInfo.decompositionMethod.call(this, Array.empty).asInstanceOf[Seq[_]]
  }

}

case class NotShortGameException(msg: String, cause: Throwable = null) extends CgsuiteException(msg, cause)
