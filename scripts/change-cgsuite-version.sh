#!/bin/sh

# Usage: change-cgsuite-version.sh <lib-version> <app-version>
# Example: change-cgsuite-version.sh 2.1.0-SNAPSHOT 2.1-beta2

# Requires gsed to be installed (`brew install gnu-sed`)

set -e

if [ -z "$2" ]; then
    echo "Usage: change-cgsuite-version.sh <lib-version> <app-version>"
    exit 1
fi

basedir=$(dirname "$0")/..

gsed -E -i'' "0,/<version>.*<\/version>/s//<version>$1<\/version>/" \
    $basedir/lib/pom.xml \
    $basedir/lib/core/pom.xml \
    $basedir/lib/discord-bot/pom.xml

gsed -E -i'' "s/cgsuite.lib.version=.*/cgsuite.lib.version=$1/" \
    $basedir/desktop-app/nbproject/project.properties

gsed -E -i'' "s/app.version=.*/app.version=$2/" \
    $basedir/desktop-app/nbproject/project.properties

gsed -E -i'' "s/val version = \".*\"/val version = \"$2\"/" \
    $basedir/lib/core/src/main/scala/org/cgsuite/lang/System.scala
