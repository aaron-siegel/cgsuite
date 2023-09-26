#!/bin/bash

set -e
cd lib/core
mvn package -f pom.xml -DskipTests
java -cp target/cgsuite-core-2.1.0-jar-with-dependencies.jar org.cgsuite.tools.BuildExternalHelp
cp -r target/site/docs ../../site
