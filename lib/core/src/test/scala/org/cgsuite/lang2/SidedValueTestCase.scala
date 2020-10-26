package org.cgsuite.lang2

object SidedValueTestCase {

  val instances = Seq(

    SidedValueTestCase(
      "dud", "dud", "StopperSidedValue",
      isIdempotent = "true",
      isInfinitesimal = "false",
      isNumberish = "false",
      isStopperSided = "true",
      onside = "on",
      offside = "off",
      outcomeClass = "D"
    ),

    SidedValueTestCase(
      "a{{|a}|}", "1 & 0", "StopperSidedValue",
      isIdempotent = "false",
      isInfinitesimal = "false",
      isNumberish = "false",
      isStopperSided = "true",
      onside = "1",
      offside = "0",
      outcomeClass = "PHat"
    ),

    SidedValueTestCase(
      "{0|{dud|},*}", "^ & v*", "StopperSidedValue",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isStopperSided = "true",
      onside = "^",
      offside = "v*",
      outcomeClass = "NHat"
    ),

    // Bach's Carousel
    SidedValueTestCase(
      "a{0,{1|1,{*,{1+*|1+*,a}|*}}|0}", "a{1|1,{1*|a||*}||0} & a{0,{1|||1*|a||*}|0}", "SidedValue",
      isIdempotent = "false",
      isInfinitesimal = "false",
      isNumberish = "false",
      isStopperSided = "false",
      onside = "!!Not a method or member variable: `Onside` (in object of class `game.SidedValue`)",
      offside = "!!Not a method or member variable: `Offside` (in object of class `game.SidedValue`)",
      outcomeClass = "N"
    )

  )

  case class SidedValueTestCase(
    x: String,
    xOut: String,
    cls: String,
    isIdempotent: String,
    isInfinitesimal: String,
    isNumberish: String,
    isStopperSided: String,
    onside: String,
    offside: String,
    outcomeClass: String
    ) {

    def toTests = {
      Seq(
        (x, xOut),
        (s"($x).Class", s"\u27eagame.$cls\u27eb"),
        (s"($x).IsIdempotent", isIdempotent),
        (s"($x).IsInfinitesimal", isInfinitesimal),
        (s"($x).IsInteger", "false"),
        (s"($x).IsLoopfree", "false"),
        (s"($x).IsNimber", "false"),
        (s"($x).IsNumber", "false"),
        (s"($x).IsNumberish", isNumberish),
        (s"($x).IsNumberTiny", "false"),
        (s"($x).IsOrdinal", "false"),
        (s"($x).IsPlumtree", "false"),
        (s"($x).IsPseudonumber", "false"),
        (s"($x).IsStopper", "false"),
        (s"($x).IsStopperSided", isStopperSided),
        (s"($x).IsUptimal", "false"),
        (s"($x).IsZero", "false"),
        (s"($x).Offside", offside),
        (s"($x).Onside", onside),
        (s"($x).OutcomeClass", outcomeClass)
      ) map { case (expr, result) => (expr, expr, result) }
    }

  }
}
