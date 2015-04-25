/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    public final static File TEST_FOLDER;
    public final static CgsuitePackage ROOT_PACKAGE = new CgsuitePackage("");
    public final static CgsuitePackage LANG_PACKAGE = new CgsuitePackage("cgsuite.lang");
    public final static CgsuitePackage UI_PACKAGE   = new CgsuitePackage("cgsuite.ui");
    public final static CgsuitePackage UTIL_PACKAGE = new CgsuitePackage("cgsuite.util");
    public final static CgsuitePackage GAME_PACKAGE = new CgsuitePackage("game");
    public final static List<CgsuitePackage> DEFAULT_PACKAGE_IMPORTS = Arrays.asList
        (new CgsuitePackage[] { ROOT_PACKAGE, LANG_PACKAGE, UI_PACKAGE, UTIL_PACKAGE, GAME_PACKAGE });
    public final static Map<String,CgsuiteClass> DEFAULT_CLASS_IMPORTS = Collections.emptyMap();
    
    private final static Map<String,CgsuitePackage> PACKAGE_LOOKUP = new HashMap<String,CgsuitePackage>();

    static
    {
        log.info("Initializing CGSuite package hierarchy.");
        
        for (CgsuitePackage pkg : DEFAULT_PACKAGE_IMPORTS)
        {
            PACKAGE_LOOKUP.put(pkg.getName(), pkg);
        }
        
        try
        {
            String devbuildPath = System.getProperty("org.cgsuite.devbuild");
            
            if (devbuildPath == null)
            {
                // Release build.
                LIB_FOLDER = InstalledFileLocator.getDefault().locate("lib", "org.cgsuite", false);
            }
            else
            {
                // Dev build.  Point lib folder to dev tree for easy editing.
                log.info("Dev build: " + devbuildPath);
                LIB_FOLDER = new File(new File(devbuildPath, "release"), "lib");
            }
            USER_FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "CGSuite");
            
            if (!USER_FOLDER.exists())
            {
                File defaultUserFolder = InstalledFileLocator.getDefault().locate("etc/default-userdir", "org.cgsuite", false);
                CgsuitePackage.copyFolder(defaultUserFolder, USER_FOLDER);
            }
            
            ROOT_PACKAGE.addRootFolder(LIB_FOLDER);
            ROOT_PACKAGE.addRootFolder(USER_FOLDER);
            
            if (devbuildPath == null)
            {
                TEST_FOLDER = null;
            }
            else
            {
                TEST_FOLDER = new File(new File(devbuildPath, "etc"), "test-lib");
                ROOT_PACKAGE.addRootFolder(TEST_FOLDER);
            }
        }
        catch (IOException exc)
        {
            throw new RuntimeException(exc);
        }
    }

    private String packageName;
    private List<FileObject> folders;
    private Map<String,CgsuiteClass> classes;

    public CgsuitePackage(String packageName)
    {
        this.packageName = packageName;
        this.folders = new ArrayList<FileObject>();
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
            else if ("text/x-cgscript".equals(fo.getMIMEType()) || "cgs".equals(fo.getExt()))
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
            CgsuiteClass.OBJECT_TYPE.setFileObject(node, this);
            classes.put("Object", CgsuiteClass.OBJECT_TYPE);
        }
        else if ("Class".equals(node.getName()))
        {
            CgsuiteClass.TYPE.setFileObject(node, this);
            classes.put("Class", CgsuiteClass.TYPE);
        }
        else if ("Nil".equals(node.getName()))
        {
            CgsuiteClass.NIL_TYPE.setFileObject(node, this);
            classes.put("Nil", CgsuiteClass.NIL_TYPE);
        }
        else
        {
            classes.put(node.getName(), new CgsuiteClass(node, this));
        }
    }
    
    public static CgsuitePackage forceLookupPackage(String name)
    {
        CgsuitePackage pkg = lookupPackage(name);
        if (pkg == null)
        {
            throw new InputException("Package not found: " + name);
        }
        return pkg;
    }
    
    public static CgsuiteClass forceLookupClass(String name)
    {
        return forceLookupClass(name, DEFAULT_PACKAGE_IMPORTS, DEFAULT_CLASS_IMPORTS);
    }
    
    public static CgsuiteClass forceLookupClass(String name, CgsuitePackage pkg)
    {
        return forceLookupClass(name, Collections.singletonList(pkg), DEFAULT_CLASS_IMPORTS);
    }

    public static CgsuiteClass forceLookupClass(String name, List<CgsuitePackage> packages, Map<String,CgsuiteClass> classImports)
    {
        CgsuiteClass type = lookupClass(name, packages, classImports);
        if (type == null)
        {
            throw new InputException("Class not found: " + name);
        }
        return type;
    }

    public static CgsuiteClass lookupClass(String name, List<CgsuitePackage> packages, Map<String,CgsuiteClass> classImports) throws CgsuiteException
    {
        CgsuiteClass type = null;

        for (CgsuitePackage pkg : packages)
        {
            CgsuiteClass t = pkg.lookupClassInPackage(name);
            if (t != null)
            {
                if (type != null)
                    throw new InputException("Ambiguous class name: " + name);
                type = t;
            }
        }
        
        CgsuiteClass t = classImports.get(name);
        
        if (t != null)
        {
            if (type != null)
                throw new InputException("Ambiguous class name: " + name);
            
            type = t;
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
        if (!fo.isFolder() || fo.getName().startsWith("."))
            return;
        
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

    public static void copyFolder(File src, File dest) throws IOException
    {
    	if (src.isDirectory())
        {
            dest.mkdir();
 
    		//list all the directory contents
    		String files[] = src.list();
 
    		for (String file : files)
            {
    		   //construct the src and dest file structure
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   //recursive copy
    		   copyFolder(srcFile, destFile);
    		}
    	}
        else
        {
    		InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest); 

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0)
            {
               out.write(buffer, 0, length);
            }

            in.close();
            out.close();
    	}
    }
}
