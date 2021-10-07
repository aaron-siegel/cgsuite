/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.worksheet;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;
import org.cgsuite.help.HelpIndex;
import org.cgsuite.lang.CgscriptClasspath;
import scala.Symbol;
import scala.collection.mutable.AnyRefMap;

/**
 *
 * @author asiegel
 */
public class WorksheetEnvironment {

    public final static File HOME_FOLDER = FileSystemView.getFileSystemView().getDefaultDirectory();
    public final static File USER_FOLDER = new File(HOME_FOLDER, "CGScript");

    final static AnyRefMap<Symbol,Object> WORKSPACE_VAR_MAP = new AnyRefMap<Symbol,Object>();
    private static boolean isInitialized = false;

    public static void ensureInitialized() {
        if (!isInitialized) {
            try {
                initialize();
            } catch (Throwable t) {
                JOptionPane.showMessageDialog(null, "An error occured while initializing CGSuite.");
                t.printStackTrace();
            }
            isInitialized = true;
        }
    }

    private static void initialize() {

        // Forcibly instantiate a CanonicalShortGame so that the interface will seem snappier
        // once the user starts using it
        new CalculationCapsule(WORKSPACE_VAR_MAP, "{1|1/2}").runAndWait();

        // Force HelpIndex to load, in order to reduce UI sluggishness later
        // (This is just a convenient place for this to go that is guaranteed
        // to load at startup)
        HelpIndex.lookup("C");

        if (!USER_FOLDER.exists()) {
            USER_FOLDER.mkdirs();
        }
        CgscriptClasspath.declareClasspathRoot(USER_FOLDER, true);

    }

}
