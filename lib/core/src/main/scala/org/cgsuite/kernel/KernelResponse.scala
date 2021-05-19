package org.cgsuite.kernel

import org.cgsuite.output.Output
import org.cgsuite.util.Explorer

trait KernelResponse extends Serializable {
  def isFinal: Boolean
}

case class WorksheetKernelResponse(output: Vector[Output], exc: Throwable, isFinal: Boolean) extends KernelResponse

case class NewExplorerKernelResponse(explorerId: String, isFinal: Boolean) extends KernelResponse

case class ExplorerUpdatedKernelResponse(explorerId: String, explorerUpdate: ExplorerUpdate, isFinal: Boolean) extends KernelResponse

trait ExplorerUpdate extends Serializable

case class RootNodeCreatedExplorerUpdate(info: NodeInfo) extends ExplorerUpdate

case class NodeExpandedExplorerUpdate(nodeOrdinal: Int, newLeftOptions: Iterable[NodeInfo], newRightOptions: Iterable[NodeInfo]) extends ExplorerUpdate

object NodeInfo {

  def apply(node: Explorer#Node): NodeInfo = {
    NodeInfo(node.ordinal, node.g.toOutput)
  }

}

case class NodeInfo(nodeOrdinal: Int, output: Output) extends Serializable
