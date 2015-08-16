/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.script.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.ui.script.CgsFileFilter;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;


public abstract class NewFileAction implements ActionListener {
    
    public abstract String getTemplate();
    
    public abstract String getApproveText();

    @Override
    public void actionPerformed(ActionEvent e)
    {
        FileChooserBuilder fcb = new FileChooserBuilder(OpenAction.class);
        fcb.setApproveText(getApproveText());
        fcb.setFileFilter(CgsFileFilter.INSTANCE);
        fcb.setDefaultWorkingDirectory(CgsuitePackage.USER_FOLDER);
        
        JFileChooser jfc = fcb.createFileChooser();
        jfc.setApproveButtonText("Create");

        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = jfc.getSelectedFile();
                FileObject dir = FileUtil.toFileObject(file.getParentFile());
                DataFolder folder = DataFolder.findFolder(dir);
                FileObject foTemplate = FileUtil.getConfigRoot().getFileObject(getTemplate());
                DataObject template = DataObject.find(foTemplate);
                DataObject obj = template.createFromTemplate(folder, file.getName());
                EditorCookie ec = obj.getLookup().lookup(EditorCookie.class);
                
                if (ec != null)
                {
                    ec.open();
                }
            }
            catch (DataObjectNotFoundException exc)
            {
            }
            catch (IOException exc)
            {
                throw new RuntimeException(exc);
            }
        }
    }
    
}
