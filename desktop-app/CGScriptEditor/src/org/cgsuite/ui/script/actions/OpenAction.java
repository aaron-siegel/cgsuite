/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.script.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.cgsuite.ui.script.CgsFileFilter;
import org.cgsuite.ui.worksheet.WorksheetEnvironment;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

// TODO Opening the same file twice shouldn't create multiple windows

public final class OpenAction implements ActionListener
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        FileChooserBuilder fcb = new FileChooserBuilder(OpenAction.class);
        fcb.setApproveText("Open");
        fcb.setFileFilter(CgsFileFilter.INSTANCE);
        fcb.setDefaultWorkingDirectory(WorksheetEnvironment.USER_FOLDER);

        JFileChooser jfc = fcb.createFileChooser();

        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = jfc.getSelectedFile();
                FileObject foSelectedFile = FileUtil.toFileObject(file);

                DataObject obj = DataObject.find(foSelectedFile);
                EditorCookie ec = obj.getLookup().lookup(EditorCookie.class);

                if (ec != null)
                {
                    ec.open();
                }
            }
            catch (DataObjectNotFoundException exc)
            {
            }
        }
    }

}
