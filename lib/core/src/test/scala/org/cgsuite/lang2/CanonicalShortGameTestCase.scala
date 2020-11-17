package org.cgsuite.lang2

object CanonicalShortGameTestCase {

  val instances = Seq(

    // Numbers

    CanonicalShortGameTestCase(
      "0", "0", "Zero",
      atomicWeight = "0",
      birthday = "0",
      companion = "*",
      followerCount = "1",
      freeze = "0",
      incentives = "{}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "true",
      isEvenTempered = "true",
      isInfinitesimal = "true",
      isInteger = "true",
      isNimber = "true",
      isNumber = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "true",
      isUptimal = "true",
      isZero = "true",
      leftIncentives = "{}",
      leftOptions = "{}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "P",
      reducedCanonicalForm = "0",
      rightIncentives = "{}",
      rightOptions = "{}",
      rightStop = "0",
      stopCount = "1",
      temperature = "-1",
      thermograph = "Thermograph(0,[],[0],0,[],[0])",
      uptimalExpansion = "0.0"
    ),

    CanonicalShortGameTestCase(
      "5", "5", "Integer",
      atomicWeight = "!!That game is not atomic: 5",
      birthday = "5",
      companion = "5",
      followerCount = "6",
      freeze = "5",
      incentives = "{-1}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "true",
      isInfinitesimal = "false",
      isInteger = "true",
      isNimber = "false",
      isNumber = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "true",
      isOddTempered = "false",
      isOrdinal = "true",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{-1}",
      leftOptions = "{4}",
      leftStop = "5",
      mean = "5",
      outcomeClass = "L",
      reducedCanonicalForm = "5",
      rightIncentives = "{}",
      rightOptions = "{}",
      rightStop = "5",
      stopCount = "1",
      temperature = "-1",
      thermograph = "Thermograph(5,[],[0],5,[],[0])",
      uptimalExpansion = "5.0"
    ),

    CanonicalShortGameTestCase(
      "-1823437481924", "-1823437481924", "Integer",
      atomicWeight = "!!That game is not atomic: -1823437481924",
      birthday = "1823437481924",
      companion = "-1823437481924",
      followerCount = "1823437481925",
      freeze = "-1823437481924",
      incentives = "{-1}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "true",
      isEvenTempered = "true",
      isInfinitesimal = "false",
      isInteger = "true",
      isNimber = "false",
      isNumber = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{}",
      leftOptions = "{}",
      leftStop = "-1823437481924",
      mean = "-1823437481924",
      outcomeClass = "R",
      reducedCanonicalForm = "-1823437481924",
      rightIncentives = "{-1}",
      rightOptions = "{-1823437481923}",
      rightStop = "-1823437481924",
      stopCount = "1",
      temperature = "-1",
      thermograph = "!!Integer out of bounds in game specifier (must satisfy -2147483648 <= n <= 2147483647)",
      uptimalExpansion = "-1823437481924.0"
      ),

    CanonicalShortGameTestCase(
      "-1/2", "-1/2", "DyadicRational",
      atomicWeight = "!!That game is not atomic: -1/2",
      birthday = "2",
      companion = "-1/2",
      followerCount = "3",
      freeze = "-1/2",
      incentives = "{-1/2}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "true",
      isInfinitesimal = "false",
      isInteger = "false",
      isNimber = "false",
      isNumber = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{-1/2}",
      leftOptions = "{-1}",
      leftStop = "-1/2",
      mean = "-1/2",
      outcomeClass = "R",
      reducedCanonicalForm = "-1/2",
      rightIncentives = "{-1/2}",
      rightOptions = "{0}",
      rightStop = "-1/2",
      stopCount = "1",
      temperature = "-1/2",
      thermograph = "Thermograph(-1/2,[-1/2],[0,-1],-1/2,[-1/2],[0,1])",
      uptimalExpansion = "-1/2.0"
    ),

    CanonicalShortGameTestCase(
      "25/16", "25/16", "DyadicRational",
      atomicWeight = "!!That game is not atomic: 25/16",
      birthday = "6",
      companion = "25/16",
      followerCount = "7",
      freeze = "25/16",
      incentives = "{-1/16}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "true",
      isInfinitesimal = "false",
      isInteger = "false",
      isNimber = "false",
      isNumber = "true",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{-1/16}",
      leftOptions = "{3/2}",
      leftStop = "25/16",
      mean = "25/16",
      outcomeClass = "L",
      reducedCanonicalForm = "25/16",
      rightIncentives = "{-1/16}",
      rightOptions = "{13/8}",
      rightStop = "25/16",
      stopCount = "1",
      temperature = "-1/16",
      thermograph = "Thermograph(25/16,[-1/16,-1/2],[0,-1,0],25/16,[-1/16,-1/8,-1/2],[0,1,0,1])",
      uptimalExpansion = "25/16.0"
    ),

    // Nimbers

    CanonicalShortGameTestCase(
      "*8", "*8", "Nimber",
      atomicWeight = "0",
      birthday = "8",
      companion = "*8",
      followerCount = "9",
      freeze = "*8",
      incentives = "{*8,*9,*10,*11,*12,*13,*14,*15}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "true",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{*8,*9,*10,*11,*12,*13,*14,*15}",
      leftOptions = "{0,*,*2,*3,*4,*5,*6,*7}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "N",
      reducedCanonicalForm = "0",
      rightIncentives = "{*8,*9,*10,*11,*12,*13,*14,*15}",
      rightOptions = "{0,*,*2,*3,*4,*5,*6,*7}",
      rightStop = "0",
      stopCount = "4374",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[0],[0,1])",
      uptimalExpansion = "*8.0"
    ),

    CanonicalShortGameTestCase(
      "9/8+*", "9/8*", "Uptimal",
      atomicWeight = "!!That game is not atomic: 9/8*",
      birthday = "6",
      companion = "9/8*",
      followerCount = "7",
      freeze = "9/8*",
      incentives = "{*}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "false",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "true",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{*}",
      leftOptions = "{9/8}",
      leftStop = "9/8",
      mean = "9/8",
      outcomeClass = "L",
      reducedCanonicalForm = "9/8",
      rightIncentives = "{*}",
      rightOptions = "{9/8}",
      rightStop = "9/8",
      stopCount = "2",
      temperature = "0",
      thermograph = "Thermograph(9/8,[0,-1/8,-1/4],[0,-1,0,-1],9/8,[0,-1/8],[0,1,0])",
      uptimalExpansion = "9/8*.0"
    ),

    // Primary uptimals

    CanonicalShortGameTestCase(
      "^", "^", "Uptimal",
      atomicWeight = "1",
      birthday = "2",
      companion = "^*",
      followerCount = "3",
      freeze = "^",
      incentives = "{^*}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{v}",
      leftOptions = "{0}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{^*}",
      rightOptions = "{*}",
      rightStop = "0",
      stopCount = "3",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "0.1"
    ),

    CanonicalShortGameTestCase(
      "v*", "v*", "Uptimal",
      atomicWeight = "-1",
      birthday = "2",
      companion = "v",
      followerCount = "3",
      freeze = "v*",
      incentives = "{^*}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{^*}",
      leftOptions = "{0}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "N",
      reducedCanonicalForm = "0",
      rightIncentives = "{v,v*}",
      rightOptions = "{0,*}",
      rightStop = "0",
      stopCount = "4",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[0],[0,1])",
      uptimalExpansion = "*.1-"
    ),


    CanonicalShortGameTestCase(
      "6+^*", "6^*", "Uptimal",
      atomicWeight = "!!That game is not atomic: 6^*",
      birthday = "8",
      companion = "6^*",
      followerCount = "9",
      freeze = "6^*",
      incentives = "{^*}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "false",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{v,v*}",
      leftOptions = "{6,6*}",
      leftStop = "6",
      mean = "6",
      outcomeClass = "L",
      reducedCanonicalForm = "6",
      rightIncentives = "{^*}",
      rightOptions = "{6}",
      rightStop = "6",
      stopCount = "4",
      temperature = "0",
      thermograph = "Thermograph(6,[0],[0,-1],6,[0],[0,1])",
      uptimalExpansion = "6*.1"
    ),

    CanonicalShortGameTestCase(
      "^*6", "^*6", "Uptimal",
      atomicWeight = "1",
      birthday = "8",
      companion = "^*6",
      followerCount = "9",
      freeze = "^*6",
      incentives = "{^*}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{v*6}",
      leftOptions = "{0}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{^*}",
      rightOptions = "{*7}",
      rightStop = "0",
      stopCount = "1459",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "*6.1"
    ),

    CanonicalShortGameTestCase(
      "73/16+v9*8", "73/16v9*8", "Uptimal",
      atomicWeight = "!!That game is not atomic: 73/16v9*8",
      birthday = "27",
      companion = "73/16v9*8",
      followerCount = "28",
      freeze = "73/16v9*8",
      incentives = "{^*}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "false",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{^*}",
      leftOptions = "{73/16v8*9}",
      leftStop = "73/16",
      mean = "73/16",
      outcomeClass = "L",
      reducedCanonicalForm = "73/16",
      rightIncentives = "{v9*8}",
      rightOptions = "{73/16}",
      rightStop = "73/16",
      stopCount = "13131",
      temperature = "0",
      thermograph = "Thermograph(73/16,[-1/16,-1/2],[0,-1,0],73/16,[0,-1/16,-1/2],[0,1,0,1])",
      uptimalExpansion = "73/16*8.9-"
    ),

    // More complex uptimals

    CanonicalShortGameTestCase(
      "{0|*||*|||*||||*}", "^[4]", "Uptimal",
      atomicWeight = "1",
      birthday = "5",
      companion = "^[4]*",
      followerCount = "6",
      freeze = "^[4]",
      incentives = "{^[4]*}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{v<4>}",
      leftOptions = "{^[3]}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{^[4]*}",
      rightOptions = "{*}",
      rightStop = "0",
      stopCount = "9",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "0.1111"
    ),

    CanonicalShortGameTestCase(
      "{0||0|v*}", "{0|^<2>}", "Uptimal",
      atomicWeight = "1",
      birthday = "4",
      companion = "{0|^<2>*}",
      followerCount = "5",
      freeze = "{0|^<2>}",
      incentives = "{^[2]*}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{{v<2>|0}}",
      leftOptions = "{0}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{^[2]*}",
      rightOptions = "{^<2>}",
      rightStop = "0",
      stopCount = "6",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "*.12"
    ),

    CanonicalShortGameTestCase(
      "{0||0,v*|vv|||{0||0,v*|vv|||vv,{0,v*|vv}},{0||0,v*|vv}|{0||0,v*|vv|||vv,{0,v*|vv}||||vv}}",
      "{0||0,v*|vv|||{0||0,v*|vv|||vv,{0,v*|vv}},{0||0,v*|vv}|{0||0,v*|vv|||vv,{0,v*|vv}||||vv}}",
      "Uptimal",
      atomicWeight = "0",
      birthday = "9",
      companion = "{0,{0,*||*,v|vv*}||{0,*||*,v|vv*|||vv*,{*,v|vv*}},{0,*||*,v|vv*}|{0,*||*,v|vv*|||vv*,{*,v|vv*}||||vv*}}",
      followerCount = "10",
      freeze = "{0||0,v*|vv|||{0||0,v*|vv|||vv,{0,v*|vv}},{0||0,v*|vv}|{0||0,v*|vv|||vv,{0,v*|vv}||||vv}}",
      incentives = "{^[4]*}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{{0|^[3]*||0,^[3]*|||0}}",
      leftOptions = "{{0||0,v*|vv}}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{^[4]*}",
      rightOptions = "{{{0||0,v*|vv|||vv,{0,v*|vv}},{0||0,v*|vv}|{0||0,v*|vv|||vv,{0,v*|vv}||||vv}}}",
      rightStop = "0",
      stopCount = "79",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "0.0202"
    ),

    CanonicalShortGameTestCase(
      "{0,*,*2,*3,{0,*,*2,*3,*4||*4,v*5|vv*4}||{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}},{0,*,*2,*3,*4||*4,v*5|vv*4}|{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}||||vv*4}}",
      "{0,*,*2,*3,{0,*,*2,*3,*4||*4,v*5|vv*4}||{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}},{0,*,*2,*3,*4||*4,v*5|vv*4}|{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}||||vv*4}}",
      "Uptimal",
      atomicWeight = "0",
      birthday = "12",
      companion = "{0,*,*2,*3,{0,*,*2,*3,*4||*4,v*5|vv*4}||{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}},{0,*,*2,*3,*4||*4,v*5|vv*4}|{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}||||vv*4}}",
      followerCount = "13",
      freeze = "{0,*,*2,*3,{0,*,*2,*3,*4||*4,v*5|vv*4}||{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}},{0,*,*2,*3,*4||*4,v*5|vv*4}|{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}||||vv*4}}",
      incentives = "{^[4]*}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "true",
      isZero = "false",
      leftIncentives = "{{{^^*4||||{^^*4|*4,^*5},^^*4|||^^*4|*4,^*5||0,*,*2,*3,*4}|{^^*4|*4,^*5||0,*,*2,*3,*4}," +
                       "{{^^*4|*4,^*5},^^*4|||^^*4|*4,^*5||0,*,*2,*3,*4}||0,*,*2,*3,{^^*4|*4,^*5||0,*,*2,*3,*4}}," +
                       "{{^^*5||||{^^*5|*5,^*4},^^*5|||^^*5|*5,^*4||0,*,*2,*3,*4,*5}|{^^*5|*5,^*4||0,*,*2,*3,*4,*5}," +
                       "{{^^*5|*5,^*4},^^*5|||^^*5|*5,^*4||0,*,*2,*3,*4,*5}||0,*,*2,*3,*4,{^^*5|*5,^*4||0,*,*2,*3,*4,*5}}," +
                       "{{^^*6||||{^^*6|*6,^*7},^^*6|||^^*6|*6,^*7||0,*,*2,*3,*4,*5,*6}|{^^*6|*6,^*7||0,*,*2,*3,*4,*5,*6}," + "" +
                       "{{^^*6|*6,^*7},^^*6|||^^*6|*6,^*7||0,*,*2,*3,*4,*5,*6}||0,*,*2,*3,*4,*5,{^^*6|*6,^*7||0,*,*2,*3,*4,*5,*6}}," +
                       "{{^^*7||||{^^*7|*7,^*6},^^*7|||^^*7|*7,^*6||0,*,*2,*3,*4,*5,*6,*7}|{^^*7|*7,^*6||0,*,*2,*3,*4,*5,*6,*7}," +
                       "{{^^*7|*7,^*6},^^*7|||^^*7|*7,^*6||0,*,*2,*3,*4,*5,*6,*7}||0,*,*2,*3,*4,*5,*6,{^^*7|*7,^*6||0,*,*2,*3,*4,*5,*6,*7}},{0|^[3]*||0,^[3]*|||0}}",
      leftOptions = "{0,*,*2,*3,{0,*,*2,*3,*4||*4,v*5|vv*4}}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "N",
      reducedCanonicalForm = "0",
      rightIncentives = "{^[4]*}",
      rightOptions = "{{{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}},{0,*,*2,*3,*4||*4,v*5|vv*4}|{0,*,*2,*3,*4||*4,v*5|vv*4|||vv*4,{*4,v*5|vv*4}||||vv*4}}}",
      rightStop = "0",
      stopCount = "1509",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[0],[0,1])",
      uptimalExpansion = "*4.0202"
    ),

    // Other infinitesimals, tinies

    CanonicalShortGameTestCase(
      "{0||0|vvv}", "{0||0|v3}", "CanonicalShortGame",
      atomicWeight = "0",
      birthday = "6",
      companion = "{0,*||*|v3*}",
      followerCount = "7",
      freeze = "{0||0|v3}",
      incentives = "{{^3,{^3|0}|0}}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{{^3|0||0}}",
      leftOptions = "{0}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{{^3,{^3|0}|0}}",
      rightOptions = "{{0|v3}}",
      rightStop = "0",
      stopCount = "7",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    CanonicalShortGameTestCase(
      "{^^^|vv}", "{^3|vv}", "CanonicalShortGame",
      atomicWeight = "{1|0}",
      birthday = "5",
      companion = "{^3*|vv*}",
      followerCount = "8",
      freeze = "{^3|vv}",
      incentives = "{{^5|0}}",
      isAllSmall = "true",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{{^5|0}}",
      leftOptions = "{^3}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "N",
      reducedCanonicalForm = "0",
      rightIncentives = "{{^5|0}}",
      rightOptions = "{vv}",
      rightStop = "0",
      stopCount = "10",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[0],[0,1])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    CanonicalShortGameTestCase(
      "{0||0|-3/2}", "Tiny(3/2)", "CanonicalShortGame",
      atomicWeight = "0",
      birthday = "5",
      companion = "{0,*||*|-3/2}",
      followerCount = "6",
      freeze = "Tiny(3/2)",
      incentives = "{{3/2,{3/2|0}|0}}",
      isAllSmall = "false",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{Miny(3/2)}",
      leftOptions = "{0}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{{3/2,{3/2|0}|0}}",
      rightOptions = "{{0|-3/2}}",
      rightStop = "0",
      stopCount = "3",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    CanonicalShortGameTestCase(
      "{0|||0||-1|-3}", "Tiny({3|1})", "CanonicalShortGame",
      atomicWeight = "0",
      birthday = "6",
      companion = "{0,*|||*||-1|-3}",
      followerCount = "7",
      freeze = "Tiny({3|1})",
      incentives = "{{3Tiny({3|1})|1Tiny({3|1}),{2,{3|1}|0}||0}}",
      isAllSmall = "false",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{Miny({3|1})}",
      leftOptions = "{0}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{{3Tiny({3|1})|1Tiny({3|1}),{2,{3|1}|0}||0}}",
      rightOptions = "{{0||-1|-3}}",
      rightStop = "0",
      stopCount = "4",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    CanonicalShortGameTestCase(
      "{5||3|1|||0||||0}", "Miny({5||3|1})", "CanonicalShortGame",
      atomicWeight = "0",
      birthday = "8",
      companion = "{5||3|1|||*||||0,*}",
      followerCount = "10",
      freeze = "Miny({5||3|1})",
      incentives = "{{5Tiny({5||3|1})|{3,{3|1}|||3|1||0|-2,{-2|-4}},{{4|2},{5||3|1}|0}||0}}",
      isAllSmall = "false",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "true",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{{5Tiny({5||3|1})|{3,{3|1}|||3|1||0|-2,{-2|-4}},{{4|2},{5||3|1}|0}||0}}",
      leftOptions = "{{5||3|1|||0}}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "R",
      reducedCanonicalForm = "0",
      rightIncentives = "{Miny({5||3|1})}",
      rightOptions = "{0}",
      rightStop = "0",
      stopCount = "5",
      temperature = "0",
      thermograph = "Thermograph(0,[],[0],0,[0],[0,1])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    CanonicalShortGameTestCase(
      "{1|^^||^}", "{1|^^||^}", "CanonicalShortGame",
      atomicWeight = "2",
      birthday = "5",
      companion = "{1|^^*||^*}",
      followerCount = "8",
      freeze = "{1|^^||^}",
      incentives = "{{1v|^||0},{{1v|^},{1v||1vv|0}|0,{^|0,{0|-1^^}}}}",
      isAllSmall = "false",
      isAtomic = "true",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{{{1v|^},{1v||1vv|0}|0,{^|0,{0|-1^^}}}}",
      leftOptions = "{{1|^^}}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{{1v|^||0}}",
      rightOptions = "{^}",
      rightStop = "0",
      stopCount = "9",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    CanonicalShortGameTestCase(
      "{1|^^^||^}", "{1|^3||^}", "CanonicalShortGame",
      atomicWeight = "!!That game is not atomic.",
      birthday = "6",
      companion = "{1|^3*||^*}",
      followerCount = "8",
      freeze = "{1|^3||^}",
      incentives = "{{1v|^^||0},{{1v|^^},{1v||1v3|0}|0,{^^|0,{0|-1^3}}}}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "true",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "true",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{{{1v|^^},{1v||1v3|0}|0,{^^|0,{0|-1^3}}}}",
      leftOptions = "{{1|^3}}",
      leftStop = "0",
      mean = "0",
      outcomeClass = "L",
      reducedCanonicalForm = "0",
      rightIncentives = "{{1v|^^||0}}",
      rightOptions = "{^}",
      rightStop = "0",
      stopCount = "9",
      temperature = "0",
      thermograph = "Thermograph(0,[0],[0,-1],0,[],[0])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    // Hot games

    CanonicalShortGameTestCase(
      "{3||2+*|1+*}", "{3||2*|1*}", "CanonicalShortGame",
      atomicWeight = "!!That game is not atomic.",
      birthday = "5",
      companion = "{3||2*|1*}",
      followerCount = "8",
      freeze = "9/4*",
      incentives = "{{2*|1*||0}}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "false",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "false",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "true",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{{2*|1*||0}}",
      leftOptions = "{3}",
      leftStop = "3",
      mean = "9/4",
      outcomeClass = "L",
      reducedCanonicalForm = "{3||2|1}",
      rightIncentives = "{{2*|1*||0}}",
      rightOptions = "{{2*|1*}}",
      rightStop = "2",
      stopCount = "5",
      temperature = "3/4",
      thermograph = "Thermograph(9/4,[3/4],[0,-1],9/4,[3/4,1/2,0],[0,1,0,1])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    ),

    CanonicalShortGameTestCase(
      "{3/2+*|1/2||{0|-3},{-1+*,{-1/2|-1+*}|-5/2}}", "{3/2*|1/2||{0|-3},{-1*,{-1/2|-1*}|-5/2}}", "CanonicalShortGame",
      atomicWeight = "!!That game is not atomic.",
      birthday = "6",
      companion = "{3/2*|1/2||{*|-3},{-1*,{-1/2|-1*}|-5/2}}",
      followerCount = "17",
      freeze = "-5/16*",
      incentives = "{{{4*|3||{5/2|3/2*},{5/2|2*||3/2*|1}},{9/2*|7/2||3/2*|1/2}|0},{{9/2*|7/2||{3|0},{2*,{5/2|2*}|1/2}},{9/2*|7/2||3/2*|1/2}|0,{3/2*|1/2||{0|-3},{-1*,{-1/2|-1*}|-5/2}}}}",
      isAllSmall = "false",
      isAtomic = "false",
      isEven = "false",
      isEvenTempered = "false",
      isInfinitesimal = "false",
      isInteger = "false",
      isNimber = "false",
      isNumber = "false",
      isNumberish = "false",
      isNumberTiny = "false",
      isOdd = "false",
      isOddTempered = "false",
      isOrdinal = "false",
      isUptimal = "false",
      isZero = "false",
      leftIncentives = "{{{4*|3||{5/2|3/2*},{5/2|2*||3/2*|1}},{9/2*|7/2||3/2*|1/2}|0}}",
      leftOptions = "{{3/2*|1/2}}",
      leftStop = "1/2",
      mean = "-5/16",
      outcomeClass = "N",
      reducedCanonicalForm = "{3/2|1/2||{0|-3},{-1/2|-1||-5/2}}",
      rightIncentives = "{{4*|3||{5/2|3/2*},{5/2|2*||3/2*|1}|||0,{5/2|1*,{1*|1/2}||-1/2|-2*,{-2*|-5/2}}},{{9/2*|7/2||{3|0},{2*,{5/2|2*}|1/2}},{9/2*|7/2||3/2*|1/2}|0,{3/2*|1/2||{0|-3},{-1*,{-1/2|-1*}|-5/2}}}}",
      rightOptions = "{{0|-3},{-1*,{-1/2|-1*}|-5/2}}",
      rightStop = "-1",
      stopCount = "11",
      temperature = "21/16",
      thermograph = "Thermograph(-5/16,[21/16,1/2,-1/2],[0,-1,0,-1],-5/16,[21/16,7/8,1/4,0],[0,1,0,1,0])",
      uptimalExpansion = "!!That `game.CanonicalShortGame` is not of type `game.Uptimal`."
    )

  )

}

case class CanonicalShortGameTestCase(
  x: String,
  xOut: String,
  cls: String,
  atomicWeight: String,
  birthday: String,
  companion: String,
  followerCount: String,
  freeze: String,
  incentives: String,
  isAllSmall: String,
  isAtomic: String,
  isEven: String,
  isEvenTempered: String,
  isInfinitesimal: String,
  isInteger: String,
  isNimber: String,
  isNumber: String,
  isNumberish: String,
  isNumberTiny: String,
  isOdd: String,
  isOddTempered: String,
  isOrdinal: String,
  isUptimal: String,
  isZero: String,
  leftIncentives: String,
  leftOptions: String,
  leftStop: String,
  mean: String,
  outcomeClass: String,
  reducedCanonicalForm: String,
  rightIncentives: String,
  rightOptions: String,
  rightStop: String,
  stopCount: String,
  temperature: String,
  thermograph: String,
  uptimalExpansion: String
  ) {

  def toTests = Seq(
    (x, xOut),
    //(s"($x).Class", s"\u27eagame.$cls\u27eb"),
    (s"($x).AtomicWeight", atomicWeight),
    (s"($x).Birthday", birthday),
    (s"($x).Companion", companion),
    (s"($x).Degree", "0"),
    (s"($x).FollowerCount", followerCount),
    (s"($x).Freeze", freeze),
    (s"($x).Incentives", incentives),
    (s"($x).IsAllSmall", isAllSmall),
    (s"($x).IsAtomic", isAtomic),
    (s"($x).IsEven", isEven),
    (s"($x).IsEvenTempered", isEvenTempered),
    (s"($x).IsIdempotent", isZero),
    (s"($x).IsInfinitesimal", isInfinitesimal),
    (s"($x).IsInteger", isInteger),
    (s"($x).IsLoopfree", "true"),
    (s"($x).IsNimber", isNimber),
    (s"($x).IsNumber", isNumber),
    (s"($x).IsNumberish", isNumberish),
    (s"($x).IsNumberTiny", isNumberTiny),
    (s"($x).IsOdd", isOdd),
    (s"($x).IsOddTempered", isOddTempered),
    (s"($x).IsOrdinal", isOrdinal),
    (s"($x).IsPlumtree", "true"),
    (s"($x).IsPseudonumber", isNumber),   // IsPseudonumber is equivalent to IsNumber for short games
    (s"($x).IsStopper", "true"),
    (s"($x).IsStopperSided", "true"),
    (s"($x).IsUptimal", isUptimal),
    (s"($x).IsZero", isZero),
    (s"($x).LeftIncentives", leftIncentives),
    (s"($x).LeftOptions", leftOptions),
    (s"($x).LeftStop", leftStop),
    (s"($x).Mean", mean),
    (s"($x).Offside", xOut),
    (s"($x).Onside", xOut),
    (s"($x).OutcomeClass", outcomeClass),
    (s"($x).OptionsFor(Left)", leftOptions),
    (s"($x).OptionsFor(Right)", rightOptions),
    (s"($x).ReducedCanonicalForm", reducedCanonicalForm),
    (s"($x).RightIncentives", rightIncentives),
    (s"($x).RightOptions", rightOptions),
    (s"($x).RightStop", rightStop),
    (s"($x).Stop(Left)", leftStop),
    (s"($x).Stop(Right)", rightStop),
    (s"($x).StopCount", stopCount),
    (s"($x).Temperature", temperature),
    (s"($x).Thermograph", thermograph),
    (s"($x).UptimalExpansion", uptimalExpansion),
    (s"($x).Variety", "0")
  ) map { case (expr, result) => (expr, expr, result) }

}
