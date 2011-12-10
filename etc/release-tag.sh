svn copy https://cgsuite.svn.sourceforge.net/svnroot/cgsuite/cgsuite/trunk \
    https://cgsuite.svn.sourceforge.net/svnroot/cgsuite/cgsuite/tags/release/$1 -m "CGSuite $1 release tag."
    
svn copy https://cgsuite.svn.sourceforge.net/svnroot/cgsuite/cgsuite/tags/release/$1 \
    https://cgsuite.svn.sourceforge.net/svnroot/cgsuite/cgsuite/branches/maint/$1 -m "CGSuite $1 maintenance branch."