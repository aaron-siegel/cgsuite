/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.help;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.cgsuite.help//CgsuiteHelp//EN",
autostore = false)
@TopComponent.Description(preferredID = "CgsuiteHelpTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "help", openAtStartup = false)
@ActionID(category = "Window", id = "org.cgsuite.help.CgsuiteHelpTopComponent")
@ActionReference(path = "Menu/Window", position = 1750)
@TopComponent.OpenActionRegistration(displayName = "#CTL_CgsuiteHelpAction",
preferredID = "CgsuiteHelpTopComponent")
public final class CgsuiteHelpTopComponent extends TopComponent {

    public final static String ROOT_URL = HelpIndex.class.getResource("docs").toExternalForm() + "/";

    public final static String CONTENTS_PAGE = "contents.html";
    public final static String PACKAGES_PAGE = "reference/overview.html";
    public final static String INDEX_PAGE = "reference/cgscript-index.html";
    public final static String GETTING_STARTED_PAGE = "tutorials/getting-started/getting-started.html";

    private JFXPanel fxPanel;
    private WebView webView;
    private final WebEngineCtl webEngineCtl = new WebEngineCtl();

    private final Action COPY_ACTION = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Platform.runLater(() -> {
                String selection = (String) webView.getEngine().executeScript("window.getSelection().toString()");
                if (selection != null && !selection.isEmpty()) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(selection), null);
                }
            });
        }
    };

    public CgsuiteHelpTopComponent() {

        initComponents();
        // We need to set this explicitly, since it's the default & otherwise
        // NetBeans won't set it at all:
        buttonBar.setBackground(new java.awt.Color(238, 238, 238));
        setName(NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CTL_CgsuiteHelpTopComponent"));
        setToolTipText(NbBundle.getMessage(CgsuiteHelpTopComponent.class, "HINT_CgsuiteHelpTopComponent"));

        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            webView = new WebView();
            webView.contextMenuEnabledProperty().setValue(false);
            webView.getEngine().getLoadWorker().stateProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue == State.SUCCEEDED) {
                    // NetBeans doesn't recognize the netscape.javascript package at compile time, and I
                    // haven't been able to figure out why. This simply funnels the needed functionality
                    // through a helper class in cgsuite-core. Pretty lame, but it works.
                    Object window = webView.getEngine().executeScript("window");
                    HelpUtil.setJSMember(window, "cgsuite", webEngineCtl);
                    //JSObject window = (JSObject) webView.getEngine().executeScript("window");
                    //window.setMember("cgsuite", webEngineCtl);
                }
            });
            navigateTo(CONTENTS_PAGE);
            fxPanel.setScene(new Scene(webView));
        });
        fxPanel.getActionMap().put(DefaultEditorKit.copyAction, COPY_ACTION);

    }

    public void navigateTo(String path) {

        SwingUtilities.invokeLater(() -> fxPanel.requestFocus());
        Platform.runLater(() -> {
            webView.getEngine().load(ROOT_URL + path);
        });

    }

    public static void openAndNavigateTo(String path) {

        CgsuiteHelpTopComponent helpComponent = (CgsuiteHelpTopComponent) WindowManager.getDefault().findTopComponent("CgsuiteHelpTopComponent");
        helpComponent.open();
        helpComponent.navigateTo(path);

    }

    public void navigateBack() {
        SwingUtilities.invokeLater(() -> fxPanel.requestFocus());
        Platform.runLater(() -> webView.getEngine().executeScript("history.back()"));
    }

    public void navigateForward() {
        SwingUtilities.invokeLater(() -> fxPanel.requestFocus());
        Platform.runLater(() -> webView.getEngine().executeScript("history.forward()"));
    }

    public class WebEngineCtl {

        public void openExternal(String path) {
            try {
                Desktop.getDesktop().browse(new URL(path).toURI());
            } catch (URISyntaxException | IOException exc) {
            }

        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        buttonBar = new javax.swing.JToolBar();
        backButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        contentsButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        packagesButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        indexButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        buttonBar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(211, 211, 211)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        buttonBar.setFloatable(false);
        buttonBar.setForeground(new java.awt.Color(238, 238, 238));
        buttonBar.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(backButton, org.openide.util.NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CgsuiteHelpTopComponent.backButton.text")); // NOI18N
        backButton.setToolTipText(org.openide.util.NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CgsuiteHelpTopComponent.backButton.toolTipText")); // NOI18N
        backButton.setFocusable(false);
        backButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        backButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        buttonBar.add(backButton);

        org.openide.awt.Mnemonics.setLocalizedText(forwardButton, org.openide.util.NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CgsuiteHelpTopComponent.forwardButton.text")); // NOI18N
        forwardButton.setFocusable(false);
        forwardButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        forwardButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });
        buttonBar.add(forwardButton);
        buttonBar.add(jSeparator3);

        org.openide.awt.Mnemonics.setLocalizedText(contentsButton, org.openide.util.NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CgsuiteHelpTopComponent.contentsButton.text")); // NOI18N
        contentsButton.setToolTipText(org.openide.util.NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CgsuiteHelpTopComponent.contentsButton.toolTipText")); // NOI18N
        contentsButton.setFocusable(false);
        contentsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        contentsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        contentsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentsButtonActionPerformed(evt);
            }
        });
        buttonBar.add(contentsButton);
        buttonBar.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(packagesButton, org.openide.util.NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CgsuiteHelpTopComponent.packagesButton.text")); // NOI18N
        packagesButton.setFocusable(false);
        packagesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        packagesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        packagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packagesButtonActionPerformed(evt);
            }
        });
        buttonBar.add(packagesButton);
        buttonBar.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(indexButton, org.openide.util.NbBundle.getMessage(CgsuiteHelpTopComponent.class, "CgsuiteHelpTopComponent.indexButton.text")); // NOI18N
        indexButton.setFocusable(false);
        indexButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        indexButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        indexButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexButtonActionPerformed(evt);
            }
        });
        buttonBar.add(indexButton);

        add(buttonBar, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void contentsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentsButtonActionPerformed
        navigateTo(CONTENTS_PAGE);
    }//GEN-LAST:event_contentsButtonActionPerformed

    private void packagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packagesButtonActionPerformed
        navigateTo(PACKAGES_PAGE);
    }//GEN-LAST:event_packagesButtonActionPerformed

    private void indexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexButtonActionPerformed
        navigateTo(INDEX_PAGE);
    }//GEN-LAST:event_indexButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        navigateBack();
    }//GEN-LAST:event_backButtonActionPerformed

    private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardButtonActionPerformed
        navigateForward();
    }//GEN-LAST:event_forwardButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JToolBar buttonBar;
    private javax.swing.JButton contentsButton;
    private javax.swing.JButton forwardButton;
    private javax.swing.JButton indexButton;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JButton packagesButton;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
