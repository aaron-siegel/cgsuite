package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class CgsuiteUtilTest extends CgscriptSpec {

  "cgsuite.util.Strip" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("Empty", "Strip.Empty(8).Length", "8"),
      ("Parse", """strip := Strip.Parse(".abbd.", ".abcd")""", "Strip([0, 1, 2, 2, 4, 0])"),
      ("ToString", """strip.ToString(".abcd")""", "\".abbd.\""),
      ("op []", "strip[3]", "2"),
      ("FindAll", "strip.FindAll(2)", "[3,4]"),
      ("Length", "strip.Length", "6"),
      ("Updated", """strip.Updated({ 3 => 4, 4 => 1 }).ToString(".abcd")""", "\".adad.\""),
      ("UpdatedRange", """strip.UpdatedRange(1, 4, 0).ToString(".abcd")""", "\"....d.\""),
      ("Substrip", """strip.Substrip(2, 5).ToString(".abcd")""", "\"abbd\""),
      ("Singleton Substrip", """strip.Substrip(2, 2).ToString(".abcd")""", "\"a\""),
      ("Empty Substrip 1", """strip.Substrip(3, 2).ToString(".abcd")""", "\"\""),
      ("Empty Substrip 2", """strip.Substrip(19, 2).ToString(".abcd")""", "\"\"")
    ))
  }

}
