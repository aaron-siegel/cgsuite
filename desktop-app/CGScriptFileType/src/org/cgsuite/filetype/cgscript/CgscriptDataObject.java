/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.filetype.cgscript;

import java.io.IOException;
import java.util.prefs.Preferences;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.SimpleValueNames;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

@Messages({
    "LBL_Cgscript_LOADER=Files of Cgscript"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Cgscript_LOADER",
        mimeType = "text/x-cgscript",
        extension = {"cgs", "CGS"}
)
@DataObject.Registration(
        mimeType = "text/x-cgscript",
        iconBase = "org/cgsuite/filetype/cgscript/thermograph-16x16.png",
        displayName = "#LBL_Cgscript_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/text/x-cgscript/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    ),
    @ActionReference(
            path = "Loaders/folder/any/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.NewTemplateAction"),
            position = 200
    )
})
public class CgscriptDataObject extends MultiDataObject {

    public CgscriptDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {

        super(pf, loader);
        registerEditor("text/x-cgscript", false);

        // This is as fine a place as any to apply some first-time preferences changes.
        Preferences cgsuitePreferences = NbPreferences.forModule(CgscriptDataObject.class);
        Boolean hasRunV2 = cgsuitePreferences.getBoolean("initialized-v2-settings", false);
        if (!hasRunV2) {
            Preferences editorPreferences = MimeLookup.getLookup(MimePath.EMPTY).lookup(Preferences.class);
            editorPreferences.putInt(SimpleValueNames.INDENT_SHIFT_WIDTH, 2);
            editorPreferences.putInt(SimpleValueNames.TAB_SIZE, 2);
            Preferences cgscriptEditorPreferences = MimeLookup.getLookup("text/x-cgscript").lookup(Preferences.class);
            cgscriptEditorPreferences.putBoolean(SimpleValueNames.ON_SAVE_USE_GLOBAL_SETTINGS, false);
            cgscriptEditorPreferences.put(SimpleValueNames.ON_SAVE_REMOVE_TRAILING_WHITESPACE, "always");
            cgscriptEditorPreferences.put(SimpleValueNames.ON_SAVE_REFORMAT, "never");
            cgsuitePreferences.putBoolean("initialized-v2-settings", true);
        }

    }

    @Override
    protected int associateLookup() {
        return 1;
    }

}
