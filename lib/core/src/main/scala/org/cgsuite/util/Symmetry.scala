package org.cgsuite.util

object Symmetry {

  case object Identity              extends SymmetryImpl(false, false, false)
  case object Inversion             extends SymmetryImpl(true,  true,  false)
  case object HorizontalFlip        extends SymmetryImpl(true,  false, false)
  case object VerticalFlip          extends SymmetryImpl(false, true,  false)
  case object Transpose             extends SymmetryImpl(false, false, true )
  case object AntiTranspose         extends SymmetryImpl(true,  true,  true )
  case object ClockwiseRotation     extends SymmetryImpl(false, true,  true )
  case object AnticlockwiseRotation extends SymmetryImpl(true,  false, true )

  val Flip = Seq(Inversion, HorizontalFlip, VerticalFlip)

}

sealed trait Symmetry {
  def isHorizontalFlip: Boolean
  def isVerticalFlip: Boolean
  def isTranspose: Boolean
}

sealed abstract class
SymmetryImpl(val isHorizontalFlip: Boolean, val isVerticalFlip: Boolean, val isTranspose: Boolean)
  extends Symmetry
