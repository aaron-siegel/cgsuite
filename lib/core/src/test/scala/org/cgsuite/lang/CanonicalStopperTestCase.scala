package org.cgsuite.lang

object CanonicalStopperTestCase {

  val instances = Seq(

    CanonicalStopperTestCase(
      "{pass|}", "on", "CanonicalStopper",
      degree = "on",
      isIdempotent = "true",
      isInfinitesimal = "false",
      isNumberish = "false",
      isPlumtree = "true",
      leftOptions = "{on}",
      leftStop = "on",
      rightOptions = "{}",
      rightStop = "on",
      variety = "on"
    ),

    CanonicalStopperTestCase(
      "{|pass}", "off", "CanonicalStopper",
      degree = "on",
      isIdempotent = "true",
      isInfinitesimal = "false",
      isNumberish = "false",
      isPlumtree = "true",
      leftOptions = "{}",
      leftStop = "off",
      rightOptions = "{off}",
      rightStop = "off",
      variety = "off"
    ),

    CanonicalStopperTestCase(
      "{0|pass}", "over", "CanonicalStopper",
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
    ),

    CanonicalStopperTestCase(
      "{pass|3/2}", "3/2under", "CanonicalStopper",
      degree = "over",
      isIdempotent = "false",
      isInfinitesimal = "false",
      isNumberish = "true",
      isPlumtree = "true",
      leftOptions = "{3/2under}",
      leftStop = "3/2",
      rightOptions = "{3/2}",
      rightStop = "3/2",
      variety = "under"
    ),

    CanonicalStopperTestCase(
      "{-1/4+*|pass}", "-1/4v[on]", "CanonicalStopper",
      degree = "^<on>",
      isIdempotent = "false",
      isInfinitesimal = "false",
      isNumberish = "true",
      isPlumtree = "true",
      leftOptions = "{-1/4*}",
      leftStop = "-1/4",
      rightOptions = "{-1/4v[on]}",
      rightStop = "-1/4",
      variety = "^<on>"
    ),

    CanonicalStopperTestCase(
      "begin uponth := {0||0|0,pass}; {0|uponth||-uponth} end", "{0|^<on>||v<on>}", "CanonicalStopper",
      degree = "^<on>",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isPlumtree = "true",
      leftOptions = "{{0|^<on>}}",
      leftStop = "0",
      rightOptions = "{v<on>}",
      rightStop = "0",
      variety = "{^[on]*,^<on>||v<on>|0}"
    ),
        /*
    CanonicalStopperTestCase(
      "{0|||0||0|{|pass}}", "{0|on.Tiny}", "CanonicalStopper",
      degree = "{0||||0|||on.Miny|0||off}",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isPlumtree = "true",
      leftOptions = "{0}",
      leftStop = "0",
      rightOptions = "{on.Tiny}",
      rightStop = "0",
      variety = "!!Degree must be an idempotent."
    ),
        */
    CanonicalStopperTestCase(
      "a{0||||0|||a|*||*}", "a{0||||0|||a|*||*}", "CanonicalStopper",
      degree = "{0|a{0|0,{0|||a|0||0}}}",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isPlumtree = "false",
      leftOptions = "{0}",
      leftStop = "0",
      rightOptions = "{a{0||||0|a||*|||*}}",
      rightStop = "0",
      variety = "{a{0,{0||0|a|||0}|0}|0}"
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
