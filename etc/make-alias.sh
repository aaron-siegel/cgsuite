#!/bin/sh

# make_alias
# This script takes two command-line arguments:
# 1) The name (relative or full path) of a source file or folder (directory)
# 2) The name (relative or full path) of a destination folder (directory)
# The script makes a Finder-style alias to the source file or folder
# and puts it in the specified destination folder.
#
# Cameron Hayne (macdev@hayne.net), December 2003
# Modified as suggested by Paul Russell (prussell@sonic.net), May 2006
#          to allow either file or folder as source
# Mofified to work with ".app" files, January 2007

scriptname=`basename $0`
if [ $# -lt 2 ]; then
    echo "Usage: $scriptname srcPath destPath"
    exit
fi

srcPath=$1
destPath=$2

if [ ! -e "$srcPath" ]; then
    echo "$scriptname: $srcPath: No such file or directory"
    exit
fi

# remove possible trailing slash from $srcPath
srcPath=${srcPath%/}

# set $srcType to "file" or "folder" as appropriate
if [ -d "$srcPath" ]; then
    if [ "${srcPath##*.}" == "app" ]; then
        srcType="file"
    else
        srcType="folder"
    fi
else
    srcType="file"
fi

# check if the $destPath directory exists
if [ ! -d "$destPath" ]; then
    echo "$scriptname: $destPath: No such directory"
    exit
fi

# check if we have permission to create a new file in the $destPath directory
if [ ! -w "$destPath" ]; then
    echo "$scriptname: No write permission in the directory $destPath"
    exit
fi

case $srcPath in
/*) fullSrcPath=$srcPath ;;
~*) fullSrcPath=$srcPath ;;
*)  fullSrcPath=`pwd`/$srcPath ;;
esac

case $destPath in
/*) fullDestPath=$destPath ;;
~*) fullDestPath=$destPath ;;
*)  fullDestPath=`pwd`/$destPath ;;
esac

/usr/bin/osascript > /dev/null <<EOT
tell application "Finder"
    set macSrcPath to POSIX file "$fullSrcPath" as text
    set macDestPath to POSIX file "$fullDestPath" as text
    make new alias file to $srcType macSrcPath at folder macDestPath
end tell
EOT

