/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.util.HashMap;
import java.util.Map;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;

/**
 *
 * @author asiegel
 */
public class Package implements FileChangeListener
{
    private FileObject folder;
    private Map<String,Package> subpackages;
    private Map<String,Capsule> classes;

    public Package(FileObject folder)
    {
        this.folder = folder;
        this.folder.addFileChangeListener(this);
        
        subpackages = new HashMap<String,Package>();
        classes = new HashMap<String,Capsule>();

        for (FileObject fo : folder.getChildren())
        {
            if (fo.isFolder())
            {

            }
        }
    }
    @Override
    public void fileFolderCreated(FileEvent fe)
    {
    }

    @Override
    public void fileDataCreated(FileEvent fe)
    {
    }

    @Override
    public void fileChanged(FileEvent fe)
    {
    }

    @Override
    public void fileDeleted(FileEvent fe)
    {
    }

    @Override
    public void fileRenamed(FileRenameEvent fre)
    {
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fae)
    {
    }
}
