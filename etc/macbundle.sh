#!/bin/sh

./create-dmg.sh \
  --volname CGSuite \
  --background dmg-background.png \
  --window-size 415 295 \
  --icon-size 48 \
  --icon CGSuite.app 110 150 \
  --icon Applications 300 150 \
  $1 \
  $2
