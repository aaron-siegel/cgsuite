#!/bin/sh

java -Dorg.cgsuite.devbuild=.. -cp target/classes:target/cgsuite-core-2.0.0-jar-with-dependencies.jar org.cgsuite.lang.Repl
