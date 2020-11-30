package org.cgsuite.lang

class DeclTest extends CgscriptSpec {

  CgscriptSystem.evaluate("0")

  "CGScript Declarations" should "validate class definitions correctly" in {

    val classdefPackage = testPackage declareSubpackage "classdef"
    classdefPackage declareSubpackage "invalidconstants"
    decl("test.classdef.WrongName", "class RightName end")
    decl("test.classdef.WrongNameEnum", "enum RightNameEnum EnumValue; end")
    decl("test.classdef.BaseClass", "class BaseClass def Method := 3; end")
    decl("test.classdef.SingletonClass", "singleton class SingletonClass end")
    decl("test.classdef.MutableClass", "mutable class MutableClass end")
    decl("test.classdef.MissingOverride", "class MissingOverride extends BaseClass def Method := 4; end")
    decl("test.classdef.ExtraneousOverride", "class ExtraneousOverride extends BaseClass override def NewMethod := 4; end")
    decl("test.classdef.ExternalMethodWithBody", "system class ExternalMethodWithBody external def ExternalMethod := 3; end")
    decl("test.classdef.NonsystemClassWithExternalMethod", "class NonsystemClassWithExternalMethod external def ExternalMethod := 3; end")
    decl("test.classdef.DuplicateMethodMethod", "class DuplicateMethodMethod def Method := 3; def Method := 4; end")
    decl("test.classdef.DuplicateMethodNested", "class DuplicateMethodNested def Method := 3; class Method() end end")
    decl("test.classdef.DuplicateMethodVar", "class DuplicateMethodVar def x := 3; var x := 4; end")
    decl("test.classdef.DuplicateNestedVar", "class DuplicateNestedVar class x end var x := 3; end")
    decl("test.classdef.DuplicateVarVar", "class DuplicateVarVar var x := 3; var x := 4; end")
    decl("test.classdef.SingletonWithConstructor", "singleton class SingletonWithConstructor(nope as Integer) end")
    decl("test.classdef.SubclassOfSingleton", "class SubclassOfSingleton extends SingletonClass end")
    decl("test.classdef.invalidconstants.constants", "class constants end")
    decl("test.classdef.ImmutableSubclassOfMutable", "class ImmutableSubclassOfMutable extends MutableClass end")
    decl("test.classdef.ImmutableNestedClassOfMutable", "mutable class ImmutableNestedClassOfMutable class Nested end end")
    decl("test.classdef.MutableVarOfImmutable", "class MutableVarOfImmutable mutable var x := 4; end")
    decl("test.classdef.ImmutableVarWithNoInitializer", "class ImmutableVarWithNoInitializer var x; end")
    decl("test.classdef.UnknownClassInParameterDeclaration",
      "class UnknownClassInParameterDeclaration def Method(parameter as UnknownClass) := 3; end")

    executeTests(Table(
      header,
      ("Wrong name", "test.classdef.WrongName",
        "!!Class name does not match filename: `RightName` (was expecting `WrongName`)"),
      ("Wrong name (enum)", "test.classdef.WrongNameEnum",
        "!!Class name does not match filename: `RightNameEnum` (was expecting `WrongNameEnum`)"),
      ("Missing override", "test.classdef.MissingOverride.X",
        "!!Method `test.classdef.MissingOverride.Method` must be declared with `override`, since it overrides `test.classdef.BaseClass.Method`"),
      ("Extraneous override", "test.classdef.ExtraneousOverride.X",
        "!!Method `test.classdef.ExtraneousOverride.NewMethod` overrides nothing"),
      ("External method with body", "test.classdef.ExternalMethodWithBody.X",
        "!!Method is declared `external` but has a method body"),
      ("External method of nonsystem class", "test.classdef.NonsystemClassWithExternalMethod.X",
        "!!Method is declared `external`, but class `test.classdef.NonsystemClassWithExternalMethod` is not declared `system`"),
      ("Duplicate method + method", "test.classdef.DuplicateMethodMethod.X",
        "!!Method `test.classdef.DuplicateMethodMethod.Method` is declared twice with identical signature"),
      ("Duplicate method + nested", "test.classdef.DuplicateMethodNested.X",
        "!!Duplicate symbol `Method` in class `test.classdef.DuplicateMethodNested`"),
      ("Duplicate method + var", "test.classdef.DuplicateMethodVar.X",
        "!!Duplicate symbol `x` in class `test.classdef.DuplicateMethodVar`"),
      ("Duplicate nested + var", "test.classdef.DuplicateNestedVar.X",
        "!!Duplicate symbol `x` in class `test.classdef.DuplicateNestedVar`"),
      ("Duplicate var + var", "test.classdef.DuplicateVarVar.X",
        "!!Duplicate symbol `x` in class `test.classdef.DuplicateVarVar`"),
      ("Singleton with constructor", "test.classdef.SingletonWithConstructor.X",
        "!!Class `test.classdef.SingletonWithConstructor` cannot have a constructor if declared `singleton`"),
      ("Subclass of singleton", "test.classdef.SubclassOfSingleton.X",
        "!!Class `test.classdef.SubclassOfSingleton` cannot extend singleton class `test.classdef.SingletonClass`"),
      ("constants is not singleton", "test.classdef.invalidconstants.constants.X",
        "!!Constants class `test.classdef.invalidconstants.constants` must be declared `singleton`"),
      /*
      ("Immutable subclass of mutable class", "test.classdef.ImmutableSubclassOfMutable.X",
        "!!Subclass `test.classdef.ImmutableSubclassOfMutable` of mutable class `test.classdef.MutableClass` is not declared `mutable`"),
      ("Immutable nested class of mutable class", "test.classdef.ImmutableNestedClassOfMutable.X",
        "!!Nested class `Nested` of mutable class `test.classdef.ImmutableNestedClassOfMutable` is not declared `mutable`"),
      ("Mutable var of immutable class", "test.classdef.MutableVarOfImmutable.X",
        "!!Class `test.classdef.MutableVarOfImmutable` is immutable, but variable `x` is declared `mutable`"),
       */
      ("Immutable var with no initializer", "test.classdef.ImmutableVarWithNoInitializer.X",
        "!!Immutable variable `x` must be assigned a value"),
      ("Unknown class in parameter declaration", "test.classdef.UnknownClassInParameterDeclaration",
        "!!Unrecognized type: `UnknownClass`")
    ))

  }

  it should "correctly inherit ancestor types" in {

    testPackage declareSubpackage "ancestor"
    CgscriptClass declareSystemClass ("test.ancestor.Ancestor1", explicitDefinition = Some("class Ancestor1 of `T end"))
    CgscriptClass declareSystemClass ("test.ancestor.Ancestor2", explicitDefinition = Some("class Ancestor2 of `T end"))
    decl("test.ancestor.ConcreteSubclass", "class ConcreteSubclass extends Ancestor1 of Integer end")
    decl("test.ancestor.SubclassWithTypeVariable1", "class SubclassWithTypeVariable1 of `T extends Ancestor1 of `T end")
    decl("test.ancestor.SubclassWithTypeVariable2", "class SubclassWithTypeVariable2 of `T extends Ancestor1 of Collection of `T end")
    decl("test.ancestor.SubclassWithTypeVariable3", "class SubclassWithTypeVariable3 of `U extends Ancestor1 of Collection of `U end")
    decl("test.ancestor.ConcreteSubsubclass", "class ConcreteSubsubclass extends SubclassWithTypeVariable3 of Integer end")
    decl("test.ancestor.MultipleInheritance1", "class MultipleInheritance1 extends SubclassWithTypeVariable3 of Integer, SubclassWithTypeVariable1 of Collection of Integer end")
    decl("test.ancestor.IllegalMultipleInheritance", "class IllegalMultipleInheritance extends SubclassWithTypeVariable3 of Integer, SubclassWithTypeVariable1 of Integer end")

    val objectType = CgscriptClass.Object.mostGenericType
    val ancestor1 = CgscriptPackage.lookupClassByName("test.ancestor.Ancestor1").get
    val ancestor2 = CgscriptPackage.lookupClassByName("test.ancestor.Ancestor2").get
    val subclassWithTypeVariable1 = CgscriptPackage.lookupClassByName("test.ancestor.SubclassWithTypeVariable1").get
    val subclassWithTypeVariable3 = CgscriptPackage.lookupClassByName("test.ancestor.SubclassWithTypeVariable3").get

    def assertAncestors(className: String, expectedProperAncestors: CgscriptType*): Unit = {
      CgscriptPackage.lookupClassByName(className).get.classInfo.properAncestorTypes shouldBe (objectType +: expectedProperAncestors.toVector)
    }

    assertAncestors(
      "test.ancestor.ConcreteSubclass",
      ancestor1.substituteForTypeParameters(CgscriptClass.Integer.mostGenericType)
    )
    assertAncestors(
      "test.ancestor.SubclassWithTypeVariable1",
      ancestor1.mostGenericType
    )
    assertAncestors(
      "test.ancestor.SubclassWithTypeVariable2",
      ancestor1.substituteForTypeParameters(CgscriptClass.Collection.mostGenericType)
    )
    assertAncestors(
      "test.ancestor.SubclassWithTypeVariable3",
      ancestor1.substituteForTypeParameters(CgscriptClass.Collection.substituteForTypeParameters(TypeVariable(Symbol("`U"))))
    )
    assertAncestors(
      "test.ancestor.ConcreteSubsubclass",
      ancestor1.substituteForTypeParameters(CgscriptClass.Collection.substituteForTypeParameters(CgscriptClass.Integer.mostGenericType)),
      subclassWithTypeVariable3.substituteForTypeParameters(CgscriptClass.Integer.mostGenericType)
    )
    assertAncestors(
      "test.ancestor.MultipleInheritance1",
      ancestor1.substituteForTypeParameters(CgscriptClass.Collection.substituteForTypeParameters(CgscriptClass.Integer.mostGenericType)),
      subclassWithTypeVariable1.substituteForTypeParameters(CgscriptClass.Collection.substituteForTypeParameters(CgscriptClass.Integer.mostGenericType)),
      subclassWithTypeVariable3.substituteForTypeParameters(CgscriptClass.Integer.mostGenericType)
    )

    executeTests(Table(
      header,
      ("Illegal multiple inheritance", "test.ancestor.IllegalMultipleInheritance",
        "!!Class `test.ancestor.IllegalMultipleInheritance` extends multiple conflicting types: `test.ancestor.Ancestor1 of game.Integer`, `test.ancestor.Ancestor1 of cgsuite.lang.Collection of game.Integer`")
    ))

  }

  /*
  it should "handle methods, constructors, and initializers correctly" in {

    testPackage declareSubpackage "init"
    decl("test.init.ExpandableParameter", "singleton class ExpandableParameter def expandableParameter(args...) := args; end")
    decl("test.init.TypedExpandableParameter", "singleton class TypedExpandableParameter def expandableParameter(bool as Boolean, args as Integer...) := [bool, args]; end")
    decl("test.init.IllegalExpandableParameter", "singleton class IllegalExpandableParameter def expandableParameter(args..., after) := args; end")
    decl("test.init.NonExpandableParameter", "singleton class NonExpandableParameter def nonExpandableParameter(bool as Boolean, int as Integer) := [bool, int]; end")
    decl("test.init.InitializerLocalScope", "singleton class InitializerLocalScope var x := for n from 1 to 3 yield n end; end")
    decl("test.init.NoConstructor", "class NoConstructor end")
    decl("test.init.NestedNoConstructor", "singleton class NestedNoConstructor class Nested end end")
    decl("test.init.InfiniteRecursion", "singleton class InfiniteRecursion def Infinite() := Infinite(); def InfiniteAuto := InfiniteAuto; end")

    executeTests(Table(
      header,
      ("Var args - 0 arguments", "test.init.ExpandableParameter.expandableParameter()", "[]"),
      ("Var args - 1 argument", "test.init.ExpandableParameter.expandableParameter(3)", "[3]"),
      ("Var args - multiple arguments", "test.init.ExpandableParameter.expandableParameter(1, 7, 10)", "[1,7,10]"),
      ("Typed var args - 0 arguments", "test.init.TypedExpandableParameter.expandableParameter(true)", "[true,[]]"),
      ("Typed var args - 1 argument", "test.init.TypedExpandableParameter.expandableParameter(true, 3)", "[true,[3]]"),
      ("Typed var args - multiple arguments", "test.init.TypedExpandableParameter.expandableParameter(true, 1, 7, 10)", "[true,[1,7,10]]"),
      ("Typed var args - type mismatch (1)", "test.init.TypedExpandableParameter.expandableParameter(1, 7, 10)", "!!Argument `bool` (in call to `test.init.TypedExpandableParameter.expandableParameter`) has type `game.Integer`, which does not match expected type `cgsuite.lang.Boolean`"),
      //("Typed var args - type mismatch (2)", "test.init.TypedExpandableParameter.expandableParameter(true, 1, false, 10)", "!!askdjfasfe"),
      ("Illegal var args", "test.init.IllegalExpandableParameter.expandableParameter(1, 2, 3)", "!!Invalid expansion for parameter `args`: must be in last position"),
      ("Nested initializer", "test.init.InitializerLocalScope.x", "[1,2,3]"),
      ("Attempt to instantiate with no constructor", "test.init.NoConstructor()",
        "!!The class `test.init.NoConstructor` has no constructor and cannot be directly instantiated."),
      ("Instantiate InstanceClass", "test.init.NestedNoConstructor.Nested", "InstanceClass.instance"),
      ("Attempt to instantiate nested class with no constructor", "test.init.NestedNoConstructor.Nested()",
        "!!The class `test.init.NestedNoConstructor.Nested` has no constructor and cannot be directly instantiated."),
      ("Infinite recursion (method call)", "test.init.InfiniteRecursion.Infinite()", "!!Possible infinite recursion."),
      ("Infinite recursion (autoinvoke)", "test.init.InfiniteRecursion.InfiniteAuto", "!!Possible infinite recursion.")
    ))

  }

  it should "handle mutables correctly" in {

    testPackage declareSubpackage "mutables"
    decl("test.mutables.MutableClass",
      """mutable class MutableClass()
        |  var immutableVar := 5;
        |  mutable var mutableVar;
        |  def SetMutable(newValue) begin mutableVar := newValue end
        |  def SetImmutable(newValue) begin immutableVar := newValue end
        |end""".stripMargin)
    decl("test.mutables.ImmutableClass1", "class ImmutableClass1() var immutableVar := MutableClass(); end")
    decl("test.mutables.SingletonImmutableClass", "singleton class SingletonImmutableClass var immutableVar := MutableClass(); end")
    decl("test.mutables.ImmutableClass2", "class ImmutableClass2(cparam) end")

    executeTests(Table(
      header,
      ("Reassign to mutable", "x := test.mutables.MutableClass(); x.SetMutable(168); x.mutableVar", "168"),
      ("Cannot reassign to immutable", "y := test.mutables.MutableClass(); y.SetImmutable(168)",
        "!!Cannot reassign to immutable var: `immutableVar`"),
      ("Cannot assign mutable object to var of immutable class", "test.mutables.ImmutableClass1()",
        "!!Cannot assign mutable object to var `immutableVar` of immutable class `test.mutables.ImmutableClass1`"),
      ("Cannot assign mutable object to var of singleton immutable class", "test.mutables.SingletonImmutableClass.X",
        "!!Cannot assign mutable object to var `immutableVar` of immutable class `test.mutables.SingletonImmutableClass`"),
      ("Cannot pass mutable object to constructor of immutable class",
        "x := test.mutables.MutableClass(); test.mutables.ImmutableClass2(x)",
        "!!Cannot assign mutable object to var `cparam` of immutable class")
    ))

  }

  it should "handle malformed Game classes correctly" in {

    testPackage declareSubpackage "game"
    decl("test.game.GameWithHackedOptions",
      """class GameWithHackedOptions(args) extends Game
        |  override def OptionsFor(player as Player) := args;
        |end
      """.stripMargin)
    decl("test.game.ImpartialGameWithHackedOptions",
      """class ImpartialGameWithHackedOptions(args) extends ImpartialGame
        |  override def Options := args;
        |end
      """.stripMargin)

    executeTests(Table(
      header,
      ("Game with String option", "test.game.GameWithHackedOptions([19,^,\"foo\"]).CanonicalForm",
        "!!The `Options` returned by class `test.game.GameWithHackedOptions` include an object of type `cgsuite.lang.String`, which is not a `Game`."),
      ("Game with Nothing option", "test.game.GameWithHackedOptions([19,^,Nothing]).CanonicalForm",
        "!!The `Options` returned by class `test.game.GameWithHackedOptions` include an object of type `cgsuite.lang.Nothing`, which is not a `Game`."),
      ("Game with invalid options", "test.game.GameWithHackedOptions(\"foo\").CanonicalForm",
        "!!The `Options` method in class `test.game.GameWithHackedOptions` returned an invalid value of type `cgsuite.lang.String` (expected a `Collection`)."),
      ("Game with null options", "test.game.GameWithHackedOptions(Nothing).CanonicalForm",
        "!!The `Options` method in class `test.game.GameWithHackedOptions` returned an invalid value of type `cgsuite.lang.Nothing` (expected a `Collection`)."),
      ("Impartial game with String option", "test.game.ImpartialGameWithHackedOptions([0,*2,\"foo\"]).CanonicalForm",
        "!!The `Options` returned by class `test.game.ImpartialGameWithHackedOptions` include an object of type `cgsuite.lang.String`, which is not a `Game`."),
      ("Impartial game with Nothing option", "test.game.ImpartialGameWithHackedOptions([0,*2,Nothing]).CanonicalForm",
        "!!The `Options` returned by class `test.game.ImpartialGameWithHackedOptions` include an object of type `cgsuite.lang.Nothing`, which is not a `Game`."),
      ("Impartial game with invalid options", "test.game.ImpartialGameWithHackedOptions(\"foo\").CanonicalForm",
        "!!The `Options` method in class `test.game.ImpartialGameWithHackedOptions` returned an invalid value of type `cgsuite.lang.String` (expected a `Collection`)."),
      ("Impartial game with null options", "test.game.ImpartialGameWithHackedOptions(Nothing).CanonicalForm",
        "!!The `Options` method in class `test.game.ImpartialGameWithHackedOptions` returned an invalid value of type `cgsuite.lang.Nothing` (expected a `Collection`)."),
      ("Impartial game with partizan option", "test.game.ImpartialGameWithHackedOptions([0,*2,9]).NimValue",
        "!!Class `test.game.ImpartialGameWithHackedOptions` is an `ImpartialGame`, but its `Options` include a partizan `Game`.")
    ))

  }
*/
}
