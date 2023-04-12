#!/bin/sh

# Usage: macbundle.sh <dmg-target-file> <bundle-dir> <jre-version>

if [ -e $2/Applications ];
then
    echo Applications alias already exists.
else
    echo Making Applications alias.
    ./make-alias.sh /Applications $2
fi

# We need to copy the JRE here, not in Ant, in order to preserve executable file modes
echo Copying JRE.
mkdir ../dist/macbundle/CGSuite.app/Contents/Resources/CGSuite/jre
cp -r ../local/jre-bundles/macos/prepared-jre-$3/* ../dist/macbundle/CGSuite.app/Contents/Resources/CGSuite/jre

./create-dmg.sh \
  --volname CGSuite \
  --background dmg-background.png \
  --window-size 415 295 \
  --icon-size 48 \
  --icon CGSuite.app 110 150 \
  --icon Applications 300 150 \
  $1 \
  $2
