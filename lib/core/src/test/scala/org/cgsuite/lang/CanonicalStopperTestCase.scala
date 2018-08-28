package org.cgsuite.lang

object CanonicalStopperTestCase {

  val instances = Seq(

    CanonicalStopperTestCase(
      "on", "on", "Pseudonumber",
      degree = "on",
      followerCount = "1",
      isIdempotent = "true",
      isInfinitesimal = "false",
      isNumberish = "false",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{on}",
      leftStop = "on",
      outcomeClass = "L",
      rightOptions = "{}",
      rightStop = "on",
      variety = "on"
    ),

    CanonicalStopperTestCase(
      "off", "off", "Pseudonumber",
      degree = "on",
      followerCount = "1",
      isIdempotent = "true",
      isInfinitesimal = "false",
      isNumberish = "false",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{}",
      leftStop = "off",
      outcomeClass = "R",
      rightOptions = "{off}",
      rightStop = "off",
      variety = "off"
    ),

    CanonicalStopperTestCase(
      "over", "over", "Pseudonumber",
      degree = "over",
      followerCount = "2",
      isIdempotent = "true",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{0}",
      leftStop = "over",
      outcomeClass = "L",
      rightOptions = "{over}",
      rightStop = "over",
      variety = "over"
    ),

    CanonicalStopperTestCase(
      "{pass|3/2}", "3/2under", "Pseudonumber",
      degree = "over",
      followerCount = "5",
      isIdempotent = "false",
      isInfinitesimal = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{3/2under}",
      leftStop = "3/2under",
      outcomeClass = "L",
      rightOptions = "{3/2}",
      rightStop = "3/2under",
      variety = "under"
    ),

    CanonicalStopperTestCase(
      "{-1/4+*|pass}", "-1/4v[on]", "CanonicalStopper",
      degree = "^<on>",
      followerCount = "6",
      isIdempotent = "false",
      isInfinitesimal = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{-1/4*}",
      leftStop = "-1/4",
      outcomeClass = "R",
      rightOptions = "{-1/4v[on]}",
      rightStop = "-1/4",
      variety = "^<on>"
    ),

    CanonicalStopperTestCase(
      "begin uponth := {0||0|0,pass}; {0|uponth||-uponth} end", "{0|^<on>||v<on>}", "CanonicalStopper",
      degree = "^<on>",
      followerCount = "7",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{{0|^<on>}}",
      leftStop = "0",
      outcomeClass = "N",
      rightOptions = "{v<on>}",
      rightStop = "0",
      variety = "{^[on]*,^<on>||v<on>|0}"
    ),

    CanonicalStopperTestCase(
      "{0|||0||0|off}", "{0|Tiny(on)}", "CanonicalStopper",
      degree = "{0||||0|||Miny(on)|0||off}",
      followerCount = "5",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{0}",
      leftStop = "0",
      outcomeClass = "L",
      rightOptions = "{Tiny(on)}",
      rightStop = "0",
      variety = "!!Degree must be an idempotent."
    ),

    CanonicalStopperTestCase(
      "{on|0||0}", "Miny(on)", "CanonicalStopper",
      degree = "Tiny(on)",
      followerCount = "4",
      isIdempotent = "true",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isPlumtree = "true",
      leftOptions = "{{on|0}}",
      leftStop = "0",
      outcomeClass = "R",
      rightOptions = "{0}",
      rightStop = "0",
      variety = "Miny(on)"
    ),

    CanonicalStopperTestCase(
      "{0||0|under}", "Tiny(over)", "CanonicalStopper",
      degree = "Tiny(over)",
      followerCount = "4",
      isIdempotent = "true",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isPlumtree = "true",
      leftOptions = "{0}",
      leftStop = "0",
      outcomeClass = "L",
      rightOptions = "{{0|under}}",
      rightStop = "0",
      variety = "Tiny(over)"
    ),

    CanonicalStopperTestCase(
      "+-{0|pass}", "+-over", "CanonicalStopper",
      degree = "over",
      followerCount = "4",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{over}",
      leftStop = "over",
      outcomeClass = "N",
      rightOptions = "{under}",
      rightStop = "under",
      variety = "+-over"
    ),

    CanonicalStopperTestCase(
      "+-{5|pass}", "+-(5over)", "CanonicalStopper",
      degree = "over",
      followerCount = "14",
      isIdempotent = "false",
      isInfinitesimal = "false",
      isNumberish = "false",
      isNumberTiny = "false",
      isPlumtree = "true",
      leftOptions = "{5over}",
      leftStop = "5over",
      outcomeClass = "N",
      rightOptions = "{-5under}",
      rightStop = "-5under",
      variety = "+-{10over|over}"
    ),

    CanonicalStopperTestCase(
      "a{0||||0|||a|*||*}", "a{0||||0|||a|*||*}", "CanonicalStopper",
      degree = "{0|a{0|0,{0|||a|0||0}}}",
      followerCount = "6",
      isIdempotent = "false",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "false",
      isPlumtree = "false",
      leftOptions = "{0}",
      leftStop = "0",
      outcomeClass = "L",
      rightOptions = "{a{0||||0|a||*|||*}}",
      rightStop = "0",
      variety = "{a{0,{0||0|a|||0}|0}|0}"
    ),

    CanonicalStopperTestCase(
      "{0|{0||||-1|||-1+*,a{0||0|a|||-1+*||||-1+*}||-2|b{-2,{-1,-1+*||-1+*|b|||-2}|-2}}}", "{0|{0||||-1|||-1*,a{0||0|a|||-1*||||-1*}||-2|b{-2,{-1,-1*||-1*|b|||-2}|-2}}}", "CanonicalStopper",
      degree = "{0|{0||||-1|||-1*,a{0||0|a|||-1*||||-1*}||-2|b{-2,{-1,-1*||-1*|b|||-2}|-2}}}",
      followerCount = "17",
      isIdempotent = "true",
      isInfinitesimal = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isPlumtree = "false",
      leftOptions = "{0}",
      leftStop = "0",
      outcomeClass = "L",
      rightOptions = "{{0||||-1|||-1*,a{0||0|a|||-1*||||-1*}||-2|b{-2,{-1,-1*||-1*|b|||-2}|-2}}}",
      rightStop = "0",
      variety = "{0|{0||||-1|||-1*,a{0||0|a|||-1*||||-1*}||-2|b{-2,{-1,-1*||-1*|b|||-2}|-2}}}"
    )

  )

}

case class CanonicalStopperTestCase(
  x: String,
  xOut: String,
  cls: String,
  degree: String,
  followerCount: String,
  isIdempotent: String,
  isInfinitesimal: String,
  isNumberish: String,
  isNumberTiny: String,
  isPlumtree: String,
  leftOptions: String,
  leftStop: String,
  outcomeClass: String,
  rightOptions: String,
  rightStop: String,
  variety: String
) {

  def toTests = {
    Seq(
      (x, xOut),
      (s"($x).Class", s"<<game.$cls>>"),
      (s"($x).Degree", degree),
      (s"($x).FollowerCount", followerCount),
      (s"($x).IsIdempotent", isIdempotent),
      (s"($x).IsInfinitesimal", isInfinitesimal),
      (s"($x).IsInteger", "false"),
      (s"($x).IsLoopfree", "false"),
      (s"($x).IsNimber", "false"),
      (s"($x).IsNumber", "false"),
      (s"($x).IsNumberish", isNumberish),
      (s"($x).IsNumberTiny", isNumberTiny),
      (s"($x).IsOrdinal", "false"),
      (s"($x).IsPlumtree", isPlumtree),
      (s"($x).IsPseudonumber", (cls == "Pseudonumber").toString),
      (s"($x).IsStopper", "true"),
      (s"($x).IsStopperSided", "true"),
      (s"($x).IsUptimal", "false"),
      (s"($x).IsZero", "false"),
      (s"($x).LeftOptions", leftOptions),
      (s"($x).LeftStop", leftStop),
      (s"($x).Offside", xOut),
      (s"($x).Onside", xOut),
      (s"($x).Options(Left)", leftOptions),
      (s"($x).Options(Right)", rightOptions),
      (s"($x).OutcomeClass", outcomeClass),
      (s"($x).RightOptions", rightOptions),
      (s"($x).RightStop", rightStop),
      (s"($x).Stop(Left)", leftStop),
      (s"($x).Stop(Right)", rightStop),
      (s"($x).Variety", variety)
    ) map { case (expr, result) => (expr, expr, result) }
  }

}
