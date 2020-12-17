/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.kernel.client.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.cgsuite.kernel.client.KernelClient;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "System",
id = "org.cgsuite.kernel.client.actions.ResetKernelAction")
@ActionRegistration(displayName = "#CTL_ResetKernelAction")
@ActionReferences({
    @ActionReference(path = "Menu/System", position = 200)
})
@NbBundle.Messages("CTL_ResetKernelAction=Reset Kernel")
public class ResetKernelAction implements ActionListener
{
    
    private final static Logger log = Logger.getLogger(ResetKernelAction.class.getName());

    private Icon icon;
    
    public ResetKernelAction() {
        try {
            icon = new ImageIcon(ImageIO.read(ResetKernelAction.class.getResourceAsStream("/org/cgsuite/kernel/client/thermograph-32x32.png")));
        } catch (Exception exc) {
            exc.printStackTrace();
            log.warning("Unable to load icon: " + exc.getMessage());
            icon = null;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        int yesOrNo = JOptionPane.showConfirmDialog(
            null,
            "Resetting the kernel will cancel any currently running\n" +
            "calculations and clear any stored variables. Are you sure?",
            "Reset Kernel",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            icon
        );
        
        if (yesOrNo == JOptionPane.YES_OPTION) {
            resetKernel();
        }
        
    }
    
    void resetKernel() {
        
        final JOptionPane optionPane = new JOptionPane(
            "Resetting Kernel. Please wait ...",
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            icon,
            new Object[0],
            null
        );

        final JDialog dialog = optionPane.createDialog(null, "Reset Kernel");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        new Thread(() -> {
            KernelClient.client.resetKernel();
            KernelClient.client.postRequest("{3/2|-1/2};", response -> {
                dialog.dispose();
                resetFinished();
            });
        }).start();

        dialog.setVisible(true);
        
    }
    
    void resetFinished() {
        
        JOptionPane.showMessageDialog(
            null,
            "The kernel has been reset.",
            "Kernel Reset",
            JOptionPane.INFORMATION_MESSAGE,
            icon
        );
        
    }
    
}
