package org.cgsuite.kernel

import org.cgsuite.output.Output

trait KernelResponse extends Serializable

case class WorksheetKernelResponse(output: Vector[Output], exc: Throwable, isFinal: Boolean) extends KernelResponse

case class NewExplorerKernelResponse(explorerId: String, initialSelection: Option[Output]) extends KernelResponse
