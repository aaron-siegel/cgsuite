/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.loaders.DataObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.cgsuite.ui.browser//Browser//EN",
autostore = false)
public final class BrowserTopComponent extends TopComponent implements ExplorerManager.Provider, PropertyChangeListener
{
    private static BrowserTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "BrowserTopComponent";
    
    private File USER_FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "CGSuite");
    private File CORE_JAR = InstalledFileLocator.getDefault().locate("modules/ext/cgsuite-core.jar", "org.cgsuite", false);
    private File DEV_LIB_FOLDER = FileUtil.normalizeFile(new File("../lib/core/src/main/resources/org/cgsuite/lang/resources"));
    private File DEV_TEST_FOLDER = FileUtil.normalizeFile(new File("../lib/core/src/test/resources/org/cgsuite"));
    private FileObject root;
    private DataObject rootDataObject;
    private ExplorerManager em;
    private Lookup lookup;

    public BrowserTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(BrowserTopComponent.class, "CTL_BrowserTopComponent"));
        setToolTipText(NbBundle.getMessage(BrowserTopComponent.class, "HINT_BrowserTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        FileObject libFileObject = null;
        try
        {
            // TODO Ensure this works.
            if (!USER_FOLDER.exists())
            {
                File defaultUserFolder = InstalledFileLocator.getDefault().locate("etc/default-userdir", "org.cgsuite", false);
                copyFolder(defaultUserFolder, USER_FOLDER);
            }
            LocalFileSystem fs = new LocalFileSystem();
            fs.setRootDirectory(USER_FOLDER);
            fs.setReadOnly(false);
            this.root = fs.getRoot();
            this.rootDataObject = DataObject.find(root);
            libFileObject = new JarFileSystem(CORE_JAR).getRoot().getFileObject("org/cgsuite/lang/resources");
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }

        ActionMap map = getActionMap();
        
        this.em = new ExplorerManager();
        this.lookup = ExplorerUtils.createLookup(this.em, map);
        this.associateLookup(this.lookup);

        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(em));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(em));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(em));
        map.put("delete", ExplorerUtils.actionDelete(em, true));
        
        this.em.setRootContext(rootDataObject.getNodeDelegate());
        
        setRootFolder(new RootFolder(FileUtil.toFileObject(USER_FOLDER), "User Folder"));
        
        jComboBox1.addItem(new RootFolder(FileUtil.toFileObject(USER_FOLDER), "User Folder"));
        jComboBox1.addItem(new RootFolder(libFileObject, "System Folder"));
        
        if (System.getProperty("org.cgsuite.devbuild") != null)
        {
            // Add some convenience folders for developers
            File defaultUserdir = new File(System.getProperty("org.cgsuite.devbuild"), "release/etc/default-userdir");
            jComboBox1.addItem(new RootFolder(FileUtil.toFileObject(DEV_LIB_FOLDER), "[dev] Core Library Source Folder"));
            jComboBox1.addItem(new RootFolder(FileUtil.toFileObject(DEV_TEST_FOLDER), "[dev] Core Library Test Folder"));
            jComboBox1.addItem(new RootFolder(FileUtil.toFileObject(defaultUserdir), "[dev] Default User Folder"));
            jComboBox1.addItem(new RootFolder(FileUtil.getConfigRoot(), "[dev] System Filesystem"));
        }
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
 
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane1 = new BeanTreeView();

        setLayout(new java.awt.BorderLayout());

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        add(jComboBox1, java.awt.BorderLayout.PAGE_START);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        RootFolder rf = (RootFolder) evt.getItem();
        setRootFolder(rf);
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized BrowserTopComponent getDefault()
    {
        if (instance == null) {
            instance = new BrowserTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the BrowserTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized BrowserTopComponent findInstance()
    {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(BrowserTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof BrowserTopComponent) {
            return (BrowserTopComponent) win;
        }
        Logger.getLogger(BrowserTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened()
    {
    }

    @Override
    public void componentClosed()
    {
    }

    void writeProperties(java.util.Properties p)
    {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    Object readProperties(java.util.Properties p)
    {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p)
    {
        String version = p.getProperty("version");
    }

    @Override
    protected String preferredID()
    {
        return PREFERRED_ID;
    }

    @Override
    public ExplorerManager getExplorerManager()
    {
        return em;
    }
   
    private void setRootFolder(RootFolder rf) {
        try {
            this.root = rf.folder;
            this.rootDataObject = DataObject.find(this.root);
            this.rootDataObject.addPropertyChangeListener(this);
            this.em.setRootContext(rootDataObject.getNodeDelegate());
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
    
    private static class RootFolder
    {
        private FileObject folder;
        private String displayName;
        
        RootFolder(FileObject folder, String displayName)
        {
            this.folder = folder;
            this.displayName = displayName;
        }
        
        @Override
        public String toString()
        {
            return displayName;
        }
    }
}
