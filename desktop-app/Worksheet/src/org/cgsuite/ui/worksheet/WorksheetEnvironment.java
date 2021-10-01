/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.worksheet;

import java.io.File;
import javax.swing.filechooser.FileSystemView;
import org.cgsuite.help.HelpIndex;
import org.cgsuite.lang.CgscriptClasspath;
import scala.Symbol;
import scala.collection.mutable.AnyRefMap;

/**
 *
 * @author asiegel
 */
class WorksheetEnvironment {

    final static AnyRefMap<Symbol,Object> WORKSPACE_VAR_MAP = new AnyRefMap<Symbol,Object>();

    static void initialize() {

        // Forcibly instantiate a CanonicalShortGame so that the interface will seem snappier
        // once the user starts using it
        new CalculationCapsule(WORKSPACE_VAR_MAP, "{1|1/2}").runAndWait();

        File homeFolder = FileSystemView.getFileSystemView().getDefaultDirectory();
        File userFolder = new File(homeFolder, "CGSuite");

        if (!userFolder.exists()) {
            userFolder.mkdir();
        }
        CgscriptClasspath.declareClasspathRoot(userFolder, true);

        // Force HelpIndex to load, in order to reduce UI sluggishness later
        // (This is just a convenient place for this to go that is guaranteed
        // to load at startup)
        HelpIndex.lookup("C");

    }

}
