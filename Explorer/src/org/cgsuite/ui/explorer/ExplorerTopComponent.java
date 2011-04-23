/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Box;
import org.cgsuite.lang.CgsuitePackage;
import org.cgsuite.lang.Domain;
import org.cgsuite.lang.explorer.EditorPanel;
import org.cgsuite.lang.explorer.Explorer;
import org.cgsuite.lang.explorer.ExplorerNode;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
import org.cgsuite.ui.worksheet.CalculationCapsule;
import org.cgsuite.ui.worksheet.EmbeddedTextArea;
import org.cgsuite.ui.worksheet.WorksheetPanel;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.cgsuite.ui.explorer//Explorer//EN",
autostore = false)
public final class ExplorerTopComponent extends TopComponent implements ExplorerTreeListener, KeyListener, TaskListener
{

    private static ExplorerTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "ExplorerTopComponent";

    private ExplorerTreeComponent tree;
    private Explorer explorer;
    private String evaluationText;

    private Domain explorerDomain;
    private CalculationCapsule currentCapsule;

    public ExplorerTopComponent()
    {
//        gamesToNodes = new HashMap<Game,ExplorerNode>();
        initComponents();
        Box box = WorksheetPanel.createInputBox();
        box.getComponent(1).addKeyListener(this);
        analysisPanel.add(box, java.awt.BorderLayout.SOUTH);
        editorScrollPane.getViewport().setBackground(Color.white);
        treeScrollPane.getViewport().setBackground(Color.white);
        setName(NbBundle.getMessage(ExplorerTopComponent.class, "CTL_ExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(ExplorerTopComponent.class, "HINT_ExplorerTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

        explorerDomain = new Domain(CgsuitePackage.ROOT_IMPORT);
    }

    public void setExplorer(Explorer explorer)
    {
        this.explorer = explorer;
        tree = new ExplorerTreeComponent(explorer);
        tree.addExplorerTreeListener(this);
        treeScrollPane.setViewportView(tree);
        updateEditor();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        expandSensibleOptionsMenuItem = new javax.swing.JMenuItem();
        primarySplitPane = new javax.swing.JSplitPane();
        detailSplitPane = new javax.swing.JSplitPane();
        editorScrollPane = new javax.swing.JScrollPane();
        analysisPanel = new javax.swing.JPanel();
        analysisScrollPane = new javax.swing.JScrollPane();
        analysisOutputBox = new org.cgsuite.lang.output.OutputBox();
        treeScrollPane = new javax.swing.JScrollPane();
        infoPanel = new javax.swing.JPanel();
        typeLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(expandSensibleOptionsMenuItem, org.openide.util.NbBundle.getMessage(ExplorerTopComponent.class, "ExplorerTopComponent.expandSensibleOptionsMenuItem.text")); // NOI18N
        expandSensibleOptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandSensibleOptionsMenuItemActionPerformed(evt);
            }
        });
        jPopupMenu1.add(expandSensibleOptionsMenuItem);

        setBackground(java.awt.Color.white);
        setLayout(new java.awt.BorderLayout());

        primarySplitPane.setBackground(new java.awt.Color(255, 255, 255));
        primarySplitPane.setDividerLocation(480);

        detailSplitPane.setDividerLocation(480);
        detailSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        editorScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        detailSplitPane.setLeftComponent(editorScrollPane);

        analysisPanel.setBackground(new java.awt.Color(255, 255, 255));
        analysisPanel.setLayout(new java.awt.BorderLayout());

        analysisScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        analysisScrollPane.setViewportView(analysisOutputBox);

        analysisPanel.add(analysisScrollPane, java.awt.BorderLayout.CENTER);

        detailSplitPane.setRightComponent(analysisPanel);

        primarySplitPane.setLeftComponent(detailSplitPane);

        treeScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        primarySplitPane.setRightComponent(treeScrollPane);

        add(primarySplitPane, java.awt.BorderLayout.CENTER);

        infoPanel.setBackground(new java.awt.Color(255, 255, 255));
        infoPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        infoPanel.setLayout(new javax.swing.BoxLayout(infoPanel, javax.swing.BoxLayout.LINE_AXIS));

        typeLabel.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(typeLabel, org.openide.util.NbBundle.getMessage(ExplorerTopComponent.class, "ExplorerTopComponent.typeLabel.text")); // NOI18N
        typeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 4));
        infoPanel.add(typeLabel);

        add(infoPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void expandSensibleOptionsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_expandSensibleOptionsMenuItemActionPerformed
    {//GEN-HEADEREND:event_expandSensibleOptionsMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expandSensibleOptionsMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.cgsuite.lang.output.OutputBox analysisOutputBox;
    private javax.swing.JPanel analysisPanel;
    private javax.swing.JScrollPane analysisScrollPane;
    private javax.swing.JSplitPane detailSplitPane;
    private javax.swing.JScrollPane editorScrollPane;
    private javax.swing.JMenuItem expandSensibleOptionsMenuItem;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSplitPane primarySplitPane;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized ExplorerTopComponent getDefault()
    {
        if (instance == null)
        {
            instance = new ExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ExplorerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ExplorerTopComponent findInstance()
    {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null)
        {
            Logger.getLogger(ExplorerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ExplorerTopComponent)
        {
            return (ExplorerTopComponent) win;
        }
        Logger.getLogger(ExplorerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType()
    {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened()
    {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed()
    {
        // TODO add custom code on component closing
    }

    @Override
    public void selectionPathChanged(List<ExplorerNode> newPath)
    {
        updateEditor();
    }

    private void updateEditor()
    {
        ExplorerNode node = tree.getSelectedNode();
        if (node == null)
        {
            this.typeLabel.setText("No position is selected.");
        }
        else
        {
            EditorPanel editorPanel = node.getG().toEditor();
            this.editorScrollPane.setViewportView(editorPanel);
            this.typeLabel.setText("Exploring " + node.getG().getCgsuiteClass().getQualifiedName() + ".");
        }
    }

    void writeProperties(java.util.Properties p)
    {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p)
    {
        if (instance == null)
        {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p)
    {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID()
    {
        return PREFERRED_ID;
    }

    private synchronized void reeval()
    {
        if (evaluationText == null || tree.getSelectedNode() == null)
        {
            analysisOutputBox.setOutput(new StyledTextOutput("Type a command into the box below to generate analysis."));
            return;
        }

        explorerDomain.put("g", tree.getSelectedNode().getG());

        CalculationCapsule capsule = new CalculationCapsule(evaluationText, explorerDomain);
        RequestProcessor.Task task = CalculationCapsule.REQUEST_PROCESSOR.create(capsule);
        task.addTaskListener(this);
        task.schedule(0);

        boolean finished = false;

        try
        {
            finished = task.waitFinished(50);
        }
        catch (InterruptedException exc)
        {
        }

        Output[] output;

        if (finished)
        {
            output = capsule.getOutput();
        }
        else
        {
            output = new Output[] { new StyledTextOutput("Calculating ...") };
            this.currentCapsule = capsule;
        }

        analysisOutputBox.setOutput(output[0]);
        analysisOutputBox.repaint();
        analysisScrollPane.validate();
    }

    @Override
    public synchronized void taskFinished(Task task)
    {
        if (currentCapsule == null)
            return;
        
        Output[] output = currentCapsule.getOutput();
        analysisOutputBox.setOutput(output[0]);
        currentCapsule = null;
        analysisOutputBox.repaint();
        analysisScrollPane.validate();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        EmbeddedTextArea source = (EmbeddedTextArea) evt.getSource();

        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_ENTER:
                if (evt.getModifiers() == 0)
                {
                    evt.consume();
                    if (!source.getText().equals(""))
                    {
                        evaluationText = source.getText();
                        reeval();
                    }
                }
                else if (evt.getModifiers() == KeyEvent.SHIFT_MASK)
                {
                    evt.consume();
                    source.insert("\n", source.getCaretPosition());
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }
}
