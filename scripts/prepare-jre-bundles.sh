#!/bin/sh

# Usage: prepare-jre-bundles.sh <jre-version>

# This script assumes there is a directory

# $basedir/desktop-app/local/jre-bundles

# with subfolders

# macos/jdk-$version
# windows/jdk-$version
# linux/jdk-$version

set -e

modules=java.base,java.datatransfer,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,\
java.prefs,java.scripting,java.sql,java.xml,jdk.jsobject,jdk.management,jdk.unsupported,jdk.unsupported.desktop,jdk.zipfs

if [ -z "$1" ]; then
  echo "Usage: prepare-jre-bundles.sh <jre-version>"
  exit 1
fi

basedir=$(readlink -f $(dirname "$0")/..)

cd "$basedir/desktop-app/local/jre-bundles"

# Use jlink to prepare custom JREs

for os in {macos,windows,linux}; do

    echo "Preparing custom JRE for $os ..."

    # Clean any existing JRE image
    "rm" -Rf "$os/prepared-jre-$1"

    macos/jdk-$1/bin/jlink --compress 2 \
        --add-modules "$modules" \
        --module-path "$os/jdk-$1/jmods" \
        --output "$os/prepared-jre-$1"

    # Bundle the copyright notices & etc. This ensures they'll be handled
    # correctly by the uninstaller (I'm not sure why it matters)
    (cd "$os/prepared-jre-$1"; zip -9 -r -q legal.zip legal; "rm" -Rf legal)

done

# NOTE: Due to a bug in NBI, for newer JREs to work, we need to create a
# "phantom" unpack200.exe in the Windows JRE binaries dir.

echo "Creating phantom unpack200.exe ..."

touch windows/prepared-jre-$1/bin/unpack200.exe

# Copy the piggyback JFX bundles.

echo "Copying cgsuite-jfx-bundle ..."

cp $basedir/lib/jfx-bundle/target/cgsuite-jfx-bundle-windows-$1-jar-with-dependencies.jar windows/prepared-jre-$1/cgsuite-jfx-bundle.jar
cp $basedir/lib/jfx-bundle/target/cgsuite-jfx-bundle-linux-$1-jar-with-dependencies.jar linux/prepared-jre-$1/cgsuite-jfx-bundle.jar

echo "Building JRE archive for windows ..."

rm windows/windows-jre-$1.zip
(cd windows/prepared-jre-$1; zip -9 -r -y -q ../windows-jre-$1.zip .)
(cd windows; cat unz600xn.exe windows-jre-$1.zip > windows-jre-$1.exe)

echo "Building JRE archive for linux ..."

rm linux/linux-jre-$1.zip
(cd linux/prepared-jre-$1; zip -9 -r -y -q ../linux-jre-$1.zip .)
(cd linux; cat unzipsfx linux-jre-$1.zip > linux-jre-$1.sh)

echo "Done!"
