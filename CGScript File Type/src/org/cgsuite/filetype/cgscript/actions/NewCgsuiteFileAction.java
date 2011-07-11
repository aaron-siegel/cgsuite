/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.filetype.cgscript.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.io.IOException;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter.Popup;

@ActionID(category = "File",
id = "org.cgsuite.filetype.cgscript.actions.NewCgsuiteFileAction")
@ActionRegistration(iconBase = "org/cgsuite/filetype/cgscript/icon.png",
displayName = "#CTL_NewCgsuiteFileAction")
@ActionReferences({})
@Messages("CTL_NewCgsuiteFileAction=New CGScript File")
public final class NewCgsuiteFileAction extends AbstractAction implements ContextAwareAction, ActionListener, Popup
{
    private Lookup lookup;
    
    public NewCgsuiteFileAction()
    {
        this(null);
    }
    
    public NewCgsuiteFileAction(Lookup lookup)
    {
        this.lookup = lookup;
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem) e.getSource();

        DataObject template = (DataObject) source.getClientProperty("template");
        
        TemplateWizard wizard = new TemplateWizard();
        try
        {
            Set<DataObject> objs = wizard.instantiate(template, preselectedFolder(lookup));
            if (objs != null)
            {
                for (DataObject obj : objs)
                {
                    if (template.getName().equals("Package"))
                    {
                        FileObject file = obj.getPrimaryFile();
                        String name = file.getName();
                        FileObject parent = file.getParent();
                        file.delete();
                        parent.createFolder(name);
                    }
                    else
                    {
                        EditorCookie ec = obj.getLookup().lookup(EditorCookie.class);

                        if (ec != null)
                        {
                            ec.open();
                        }
                    }
                }
            }
        }
        catch (IOException exc)
        {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public JMenuItem getPopupPresenter()
    {
        JMenu submenu = new JMenu("New");

        DataFolder preselectedFolder = preselectedFolder(lookup);

        boolean canWrite;
        if (preselectedFolder == null) {
            canWrite = false;
        } else {
            FileObject pf = preselectedFolder.getPrimaryFile();
            canWrite = pf != null && pf.canWrite();
        }

        DataObject templates[] = new DataObject[]
        {
            findTemplate("Templates/Other/CgscriptTemplate.cgs"),
            findTemplate("Templates/Other/Package")
        };
    
        for (int i = 0; i < templates.length; i++)
        {
            Node n = templates[i].getNodeDelegate();
            Icon icon;
            if ("Package".equals(n.getDisplayName()))
                icon = new ImageIcon((Image) UIManager.get("Nb.Explorer.Folder.icon"));
            else
                icon = new ImageIcon(n.getIcon(BeanInfo.ICON_COLOR_16x16));
            JMenuItem item = new JMenuItem(n.getDisplayName(), icon);
            item.addActionListener(this);
            item.putClientProperty("template", templates[i]);
            item.setEnabled(canWrite);
            submenu.add(item);
        }
        
        return submenu;
    }
    
    @Override
    public Action createContextAwareInstance(Lookup lookup)
    {
        return new NewCgsuiteFileAction(lookup);
    }
    
    private static DataObject findTemplate(String name)
    {
        FileObject tFo = Repository.getDefault().getDefaultFileSystem()
                .findResource(name);
        if (tFo == null) {
            return null;
        }
        try {
            return DataObject.find(tFo);
        } catch (DataObjectNotFoundException e) {
            return null;
        }

    }

    private DataFolder preselectedFolder(Lookup context)
    {
        DataFolder preselectedFolder = null;

        // Try to find selected folder
        preselectedFolder = context.lookup(DataFolder.class);
        if (preselectedFolder == null) {
            // No folder selectd try with DataObject
            DataObject dobj = context.lookup(DataObject.class);
            if (dobj != null) {
                // DataObject found => we'll use the parent folder
                preselectedFolder = dobj.getFolder();
            }
        }

        return preselectedFolder;
    }
    
}
