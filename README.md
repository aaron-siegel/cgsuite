# cgsuite

CGSuite is a computer algebra system for research in combinatorial game theory. It implements the Conway
algebra of partizan combinatorial games and several related systems. CGSuite has a built-in library of
well-known rulesets such as Clobber, Toads and Frogs, Kayles, and Wythoff Nim, and it includes a custom-designed
scripting language, CGScript, that can be used to create and explore new ones.

A working knowledge of combinatorial game theory is assumed. An introductory CGT text, such as _Winning Ways_ by
Berlekamp, Conway, and Guy or _Lessons in Play_ by Albert, Nowakowski, and Wolfe, will provide the necessary
background material.

### As a desktop application

CGSuite is usually used as a desktop application, which includes a Worksheet view for calculations in the
algebra(s) of combinatorial games, a CGScript editor, and various other tools. Download links for Mac OS,
Windows, and Linux can be found at:
* http://www.cgsuite.org/ - latest stable release
* https://github.com/aaron-siegel/cgsuite/releases - latest beta version

The desktop application includes an embedded Help feature with a user guide and tutorials.

### As a library

Starting with version 2.0-beta1, CGSuite is also provided as a Scala library, useful for standalone applications or
higher-performance calculations than are achievable with CGScript. An artifact will be published with the
upcoming 2.0 release; in the meantime, consider the CGSuite library to be very much a work in progress, but feel
free to clone the repo and try it out if you're feeling brave.
