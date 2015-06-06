package org.cgsuite.lang

object CanonicalStopperTestCase {

  val instances = Seq(

    CanonicalStopperTestCase(
      "a{a|}", "on", "CanonicalStopper",
      degree = "on",
      isIdempotent = "true",
      isInfinitesimal = "false",
      isNumberish = "false",
      isPlumtree = "true",
      leftOptions = "{on}",
      leftStop = "{on}",
      rightOptions = "{}",
      rightStop = "on",
      variety = "on"
    ),

    CanonicalStopperTestCase(
      "a{|a}", "off", "CanonicalStopper",
      degree = "on",
      isIdempotent = "true",
      isInfinitesimal = "false",
      isNumberish = "false",
      isPlumtree = "true",
      leftOptions = "{}",
      leftStop = "on",
      rightOptions = "{off}",
      rightStop = "on",
      variety = "off"
    ),

    CanonicalStopperTestCase(
      "a{0|a}", "over", "CanonicalStopper",
      degree = "over",
      isIdempotent = "true",
      isInfinitesimal = "true",
      isNumberish = "true",
      isPlumtree = "true",
      leftOptions = "{0}",
      leftStop = "0",
      rightOptions = "{over}",
      rightStop = "0",
      variety = "over"
    )

  )

}

case class CanonicalStopperTestCase(
  x: String,
  xOut: String,
  cls: String,
  degree: String,
  isIdempotent: String,
  isInfinitesimal: String,
  isNumberish: String,
  isPlumtree: String,
  leftOptions: String,
  leftStop: String,
  rightOptions: String,
  rightStop: String,
  variety: String
) {

  def toTests = {
    Seq(
      (x, xOut),
      (s"($x).Class", s"<<game.$cls>>"),
      (s"($x).Degree", degree),
      (s"($x).IsIdempotent", isIdempotent),
      (s"($x).IsInfinitesimal", isInfinitesimal),
      (s"($x).IsInteger", "false"),
      (s"($x).IsLoopfree", "false"),
      (s"($x).IsNimber", "false"),
      (s"($x).IsNumber", "false"),
      (s"($x).IsNumberish", isNumberish),
      (s"($x).IsNumberTiny", "false"),    // TODO?
      (s"($x).IsPlumtree", isPlumtree),
      (s"($x).IsStopper", "true"),
      (s"($x).IsUptimal", "false"),
      (s"($x).IsZero", "false"),
      (s"($x).LeftOptions", leftOptions),
      (s"($x).LeftStop", leftStop),
      (s"($x).Options(Player.Left)", leftOptions),
      (s"($x).Options(Player.Right)", rightOptions),
      (s"($x).RightOptions", rightOptions),
      (s"($x).RightStop", rightStop),
      (s"($x).Stop(Player.Left)", leftStop),
      (s"($x).Stop(Player.Right)", rightStop),
      (s"($x).Variety", variety)
    ) map { case (expr, result) => (expr, expr, result) }
  }

}
