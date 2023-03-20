/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.worksheet;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.awt.MenuBar;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;

/**
 *
 * @author asiegel
 */
public class PopupMenuHelper {

    public static JPopupMenu INPUT_PANE_POPUP_MENU;

    static {
        FileObject menu = FileUtil.getConfigFile("Worksheet/Popups");
        MenuBar bar = new MenuBar(DataFolder.findFolder(menu));
        bar.getMenuCount();
        INPUT_PANE_POPUP_MENU = menuToPopup(bar.getMenu(0));
    }

    static JPopupMenu menuToPopup(JMenu menu) {
        JPopupMenu popup = new JPopupMenu();
        for (Object item : menu.getMenuComponents()) {
            popup.add((JMenuItem) item);
        }
        return popup;
    }

}
