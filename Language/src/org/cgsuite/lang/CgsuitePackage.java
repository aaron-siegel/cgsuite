/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.LocalFileSystem;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author asiegel
 */
public class CgsuitePackage implements FileChangeListener
{
    private final static Logger log = Logger.getLogger(CgsuitePackage.class.getName());

    public final static File LIB_FOLDER;
    public final static File USER_FOLDER;
    public final static CgsuitePackage ROOT_PACKAGE = new CgsuitePackage("");
    public final static List<CgsuitePackage> ROOT_IMPORT = Collections.singletonList(ROOT_PACKAGE);
    
    private final static Map<String,CgsuitePackage> PACKAGE_LOOKUP = new HashMap<String,CgsuitePackage>();

    static
    {
        PACKAGE_LOOKUP.put("", ROOT_PACKAGE);
        try
        {
            boolean isDevBuild = false;
            
            String devbuildPath = System.getProperty("org.cgsuite.devbuild");
            
            if (devbuildPath == null)
            {
                // Release build.
                LIB_FOLDER = InstalledFileLocator.getDefault().locate("lib", "org.cgsuite", false);
            }
            else
            {
                // Dev build.  Point lib folder to dev tree for easy editing.
                LIB_FOLDER = new File(new File(devbuildPath, "release"), "lib");
            }
            USER_FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "CGSuite");
            
            if (!USER_FOLDER.exists())
                USER_FOLDER.mkdir();
            
            ROOT_PACKAGE.addRootFolder(LIB_FOLDER);
            ROOT_PACKAGE.addRootFolder(USER_FOLDER);
        }
        catch (IOException exc)
        {
            throw new RuntimeException(exc);
        }
    }

    private String packageName;
    private List<FileObject> folders;
    private Map<String,CgsuitePackage> subpackages;
    private Map<String,CgsuiteClass> classes;

    public CgsuitePackage(String packageName)
    {
        this.packageName = packageName;
        this.folders = new ArrayList<FileObject>();
        this.subpackages = new HashMap<String,CgsuitePackage>();
        this.classes = new HashMap<String,CgsuiteClass>();
    }

    public void addRootFolder(File file) throws IOException
    {
        try
        {
            LocalFileSystem fs = new LocalFileSystem();
            fs.setRootDirectory(file);
            addFolder(fs.getRoot());
        }
        catch (PropertyVetoException exc)
        {
            throw new RuntimeException(exc);
        }
    }

    public void addFolder(FileObject folder)
    {
        if (this.folders.contains(folder))
            return;

        this.folders.add(folder);
        folder.addFileChangeListener(this);

        for (FileObject fo : folder.getChildren())
        {
            if (fo.isFolder())
            {
                addPackage(fo);
            }
            else if ("text/x-cgscript".equals(fo.getMIMEType()))
            {
                addClass(fo);
            }
        }
    }
    
    private static void addPackage(FileObject node)
    {
        if (node.getName().startsWith("."))
            return;
        
        String packageName = node.getPath().replace('/', '.');
        
        CgsuitePackage pkg = PACKAGE_LOOKUP.get(packageName);
        if (pkg == null)
        {
            pkg = new CgsuitePackage(packageName);
            PACKAGE_LOOKUP.put(packageName, pkg);
        }
        
        pkg.addFolder(node);
    }

    private void addClass(FileObject node)
    {
        if (classes.containsKey(node.getName()))
        {
            classes.get(node.getName()).setFileObject(node, this);
        }
        else if ("Object".equals(node.getName()))
        {
            CgsuiteClass.OBJECT.setFileObject(node, this);
            classes.put("Object", CgsuiteClass.OBJECT);
        }
        else if ("Class".equals(node.getName()))
        {
            CgsuiteClass.CLASS.setFileObject(node, this);
            classes.put("Class", CgsuiteClass.CLASS);
        }
        else
        {
            classes.put(node.getName(), new CgsuiteClass(node, this));
        }
    }

    public static CgsuiteClass forceLookupClass(String name)
    {
        return forceLookupClass(name, ROOT_IMPORT);
    }

    public static CgsuiteClass forceLookupClass(String name, List<CgsuitePackage> packages)
    {
        CgsuiteClass type = lookupClass(name, packages);
        if (type == null)
        {
            throw new CgsuiteException("Class not found: " + name);
        }
        return type;
    }

    public static CgsuiteClass lookupClass(String name, List<CgsuitePackage> packages) throws CgsuiteException
    {
        CgsuiteClass type = null;

        for (CgsuitePackage pkg : packages)
        {
            CgsuiteClass t = pkg.lookupClassInPackage(name);
            if (t != null)
            {
                if (type != null)
                    throw new CgsuiteException("Ambiguous class name: " + name);
                type = t;
            }
        }

        return type;
    }

    public static CgsuitePackage getRootPackage()
    {
        return ROOT_PACKAGE;
    }

    public static CgsuitePackage lookupPackage(String packageName)
    {
        return PACKAGE_LOOKUP.get(packageName);
    }

    public static void refreshAll()
    {
        for (FileObject fo : ROOT_PACKAGE.folders)
        {
            refresh(fo);
        }
    }
    
    private static void refresh(FileObject fo)
    {
        fo.refresh();
        for (FileObject subFo : fo.getChildren())
        {
            refresh(subFo);
        }
    }

    public CgsuiteClass forceLookupClassInPackage(String name)
    {
        CgsuiteClass type = lookupClassInPackage(name);
        if (type == null)
        {
            throw new CgsuiteException("Class not found: " + name);
        }
        return type;
    }

    public CgsuiteClass lookupClassInPackage(String name)
    {
        return classes.get(name);
    }

    public String getName()
    {
        return packageName;
    }

    @Override
    public String toString()
    {
        return "Package[" + packageName + "]";
    }

    @Override
    public void fileFolderCreated(FileEvent fe)
    {
        addPackage(fe.getFile());
    }

    @Override
    public void fileDataCreated(FileEvent fe)
    {
        if ("text/x-cgscript".equals(fe.getFile().getMIMEType()))
        {
            addClass(fe.getFile());
        }
    }

    @Override
    public void fileChanged(FileEvent fe)
    {
    }

    @Override
    public void fileDeleted(FileEvent fe)
    {
        // XXX Implement
    }

    @Override
    public void fileRenamed(FileRenameEvent fre)
    {
        // XXX Implement
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fae)
    {
    }
}
