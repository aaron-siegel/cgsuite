/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.history;

import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.lookup.Lookups;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.cgsuite.ui.history//CommandHistory//EN",
autostore = false)
@TopComponent.Description(preferredID = "CommandHistoryTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = true)
@ActionID(category = "Window", id = "org.cgsuite.ui.history.CommandHistoryTopComponent")
@ActionReference(path = "Menu/Window", position = 1725)
@TopComponent.OpenActionRegistration(displayName = "#CTL_CommandHistoryAction",
preferredID = "CommandHistoryTopComponent")
public final class CommandHistoryTopComponent extends TopComponent implements ListDataListener {
    
    private CommandHistoryBufferImpl buffer;

    public CommandHistoryTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(CommandHistoryTopComponent.class, "CTL_CommandHistoryTopComponent"));
        setToolTipText(NbBundle.getMessage(CommandHistoryTopComponent.class, "HINT_CommandHistoryTopComponent"));

        buffer = new CommandHistoryBufferImpl();
        jList1.setModel(buffer);
        this.associateLookup(Lookups.singleton(buffer));

        buffer.load();
        buffer.addCommand("// " + new Date(System.currentTimeMillis()).toString());
        buffer.addListDataListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jList1.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jList1FocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
        {
            String cmd = jList1.getSelectedValue();
            if (cmd != null)
            {
                buffer.fireCommandActivated(cmd);
            }
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jList1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jList1FocusLost
        jList1.setSelectedIndices(new int[0]);
    }//GEN-LAST:event_jList1FocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened()
    {
        scrollToBottom();
    }

    @Override
    public void componentClosed()
    {
    }
        
    public CommandHistoryBuffer getBuffer()
    {
        return buffer;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void intervalAdded(ListDataEvent e)
    {
        scrollToBottom();
    }

    @Override
    public void intervalRemoved(ListDataEvent e)
    {
        scrollToBottom();
    }

    @Override
    public void contentsChanged(ListDataEvent e)
    {
        scrollToBottom();
    }
    
    private void scrollToBottom()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                jList1.scrollRectToVisible(jList1.getCellBounds(buffer.getSize()-1, buffer.getSize()));
            }
        });
    }
}
