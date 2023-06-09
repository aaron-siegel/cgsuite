package org.cgsuite.lang

object CompoundGameTestCase {

  val instances = Seq(

    CompoundGameTestCase(
      g = "1/2",
      h = "1/4",
      conjunctiveSum = "1/2",
      conwayProduct = "1/8",
      disjunctiveSum = "3/4",
      ordinalProduct = "11/32",
      ordinalSum = "9/16",
      selectiveSum = "3/4"
    ),

    CompoundGameTestCase(
      g = "*5",
      h = "*6",
      conjunctiveSum = "*5",
      conwayProduct = "*8",
      disjunctiveSum = "*3",
      ordinalProduct = "*30",
      ordinalSum = "*11",
      selectiveSum = "*8"
    ),

    CompoundGameTestCase(
      g = "+-1",
      h = "^^",
      conjunctiveSum = "{0|-1}",
      conwayProduct = "+-^^",
      disjunctiveSum = "{1^^|-1^^}",
      ordinalProduct = "+-1",
      ordinalSum = "{1,+-1|-1,{1,+-1,+-{1,+-1}|-1,+-1}}",
      selectiveSum = "{1^^|-1}"
    ),

    CompoundGameTestCase(
      g = "{1|0}",
      h = "0",
      conjunctiveSum = "0",
      conwayProduct = "0",
      disjunctiveSum = "{1|0}",
      ordinalProduct = "0",
      ordinalSum = "{1|0}",
      selectiveSum = "{1|0}"
    ),

    CompoundGameTestCase(
      g = "'{*|*}'",
      h = "3",
      conjunctiveSum = "2",
      conwayProduct = "0",
      disjunctiveSum = "3",
      ordinalProduct = "0",
      ordinalSum = "^[3]",
      selectiveSum = "3"
    ),

    CompoundGameTestCase(
      g = "*[[[2]],[2],0]",
      h = "*5",
      conjunctiveSum = "*5",
      conwayProduct = "*10",
      disjunctiveSum = "*7",
      ordinalProduct = "*10",
      ordinalSum = "*7",
      selectiveSum = "*7"
    )

  )

}

case class CompoundGameTestCase(
  g: String,
  h: String,
  conjunctiveSum: String,
  conwayProduct: String,
  disjunctiveSum: String,
  ordinalProduct: String,
  ordinalSum: String,
  selectiveSum: String
) {

  def toTests = Seq(
    (s"(($g) ConjunctiveSum ($h)).CanonicalForm", conjunctiveSum),
    (s"(($g) ConwayProduct ($h)).CanonicalForm", conwayProduct),
    (s"(($g) + ($h)).CanonicalForm", disjunctiveSum),
    (s"(($g) OrdinalProduct ($h)).CanonicalForm", ordinalProduct),
    (s"(($g) : ($h)).CanonicalForm", ordinalSum),
    (s"(($g) SelectiveSum ($h)).CanonicalForm", selectiveSum),
  ) map { case (expr, result) => (expr, expr, result) }

}
