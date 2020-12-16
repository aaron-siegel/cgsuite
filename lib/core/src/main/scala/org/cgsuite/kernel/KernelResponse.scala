package org.cgsuite.kernel

import org.cgsuite.output.Output

case class KernelResponse(output: Vector[Output], isFinal: Boolean) extends Serializable
