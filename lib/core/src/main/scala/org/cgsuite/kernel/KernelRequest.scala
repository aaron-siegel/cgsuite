package org.cgsuite.kernel

trait KernelRequest extends Serializable

case class InputKernelRequest(input: String) extends KernelRequest

case class ExplorerKernelRequest(explorerId: String, nodeOrdinal: Int, action: ExplorerAction.Value) extends KernelRequest

object ExplorerAction extends Enumeration with Serializable {

  val ExpandAllOptions, ExpandSensibleOptions, ExpandSensibleLinesOfPlay = Value

}
