#!/bin/sh

if [ -e $2/Applications ];
then
    echo Applications alias already exists.
else
    echo Making Applications alias.
    ./make-alias.sh /Applications $2
fi

echo Copying JRE.
mkdir ../dist/macbundle/CGSuite.app/Contents/Resources/CGSuite/jre
cp -r ../local/jre-bundles/macos/jre/* ../dist/macbundle/CGSuite.app/Contents/Resources/CGSuite/jre

./create-dmg.sh \
  --volname CGSuite \
  --background dmg-background.png \
  --window-size 415 295 \
  --icon-size 48 \
  --icon CGSuite.app 110 150 \
  --icon Applications 300 150 \
  $1 \
  $2
