/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * java
 *
 * Created on Jan 30, 2011, 10:55:50 AM
 */

package org.cgsuite.ui.worksheet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.StyledTextOutput;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author asiegel
 */
public class WorksheetPanel extends javax.swing.JPanel implements Scrollable, TaskListener
{
    private final static RequestProcessor REQUEST_PROCESSOR = new RequestProcessor(WorksheetPanel.class);

    private CalculationCapsule currentCapsule;
    private EmbeddedTextArea currentSource;

    private List<String> commandHistory = new ArrayList<String>();
    private String commandHistoryPrefix;
    private int commandHistoryIndex;

    /** Creates new form WorksheetPanel */
    public WorksheetPanel()
    {
        initComponents();
        addNewCell();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    private JViewport getViewport()
    {
        return (JViewport) getParent();
    }

    private JScrollPane getScrollPane()
    {
        return (JScrollPane) getParent().getParent();
    }

    private Box addNewCell()
    {
        JLabel label = new JLabel("> ");
        label.setFont(new Font("Monospaced", Font.PLAIN, 12));
        label.setAlignmentY(Component.TOP_ALIGNMENT);
        EmbeddedTextArea textArea = new EmbeddedTextArea();
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) { cellKeyPressed(evt); }
        });
        textArea.setAlignmentY(Component.TOP_ALIGNMENT);
        Box box = Box.createHorizontalBox();
        box.add(label);
        box.add(textArea);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(box);
        return box;
    }

    private void cellKeyPressed(KeyEvent evt)
    {
        EmbeddedTextArea source = (EmbeddedTextArea) evt.getSource();
        Component[] components = getComponents();
        int index;
        for (index = 0; index < components.length; index++)
        {
            if (components[index] instanceof Box &&
                ((Box) components[index]).getComponent(1) == source)
            {
                break;
            }
        }

        switch (evt.getKeyCode())
        {
            /*
            case KeyEvent.VK_TAB:
                if (evt.getModifiers() == 0)
                {
                    evt.consume();
                    for (index++; index < components.length; index++)
                    {
                        if (components[index] instanceof Box)
                        {
                            ((Box) components[index]).getComponent(1).requestFocusInWindow();
                            break;
                        }
                    }
                }
                else if (evt.getModifiers() == KeyEvent.SHIFT_MASK)
                {
                    evt.consume();
                    for (index--; index >= 0; index--)
                    {
                        if (components[index] instanceof Box)
                        {
                            ((Box) components[index]).getComponent(1).requestFocusInWindow();
                            break;
                        }
                    }
                }
                break;
            */
            case KeyEvent.VK_ENTER:
                if (evt.getModifiers() == 0)
                {
                    evt.consume();
                    if (!source.getText().equals(""))
                    {
                        processCommand(source);
                    }
                }
                else if (evt.getModifiers() == KeyEvent.SHIFT_MASK)
                {
                    evt.consume();
                    source.insert("\n", source.getCaretPosition());
                }
                break;

            case KeyEvent.VK_UP:
                if (evt.getModifiers() == 0 && source.getCaretLine() == 0)
                {
                    evt.consume();
                    if (commandHistoryPrefix == null)
                    {
                        commandHistoryPrefix = source.getText();
                        commandHistoryIndex = commandHistory.size();
                    }
                    source.setText(seekCommand(-1));
                }
                break;

            case KeyEvent.VK_DOWN:
                if (evt.getModifiers() == 0 && commandHistoryPrefix != null &&
                    (source.getCaretLine() == source.getLineCount()-1))
                {
                    evt.consume();
                    source.setText(seekCommand(1));
                }
                break;

            default:
                break;
        }
    }

    private String seekCommand(int direction)
    {
        for (commandHistoryIndex += direction;
             commandHistoryIndex >= 0 && commandHistoryIndex < commandHistory.size();
             commandHistoryIndex--)
        {
            if (commandHistory.get(commandHistoryIndex).startsWith(commandHistoryPrefix))
            {
                return commandHistory.get(commandHistoryIndex);
            }
        }
        commandHistoryIndex = commandHistory.size();
        return commandHistoryPrefix;
    }
    
    private synchronized void processCommand(EmbeddedTextArea source)
    {
        source.setEditable(false);
        commandHistory.add(source.getText());
        commandHistoryPrefix = null;
        CalculationCapsule capsule = new CalculationCapsule(source.getText());
        RequestProcessor.Task task = REQUEST_PROCESSOR.create(capsule);
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
            if (capsule.isErrorOutput())
                getToolkit().beep();
        }
        else
        {
            output = new Output[] { new StyledTextOutput("Calculating ...") };
            this.currentCapsule = capsule;
            this.currentSource = source;
        }

        OutputBox outputBox = postOutput(output);

        if (finished)
        {
            advanceToNext();
        }
        else
        {
            this.requestFocusInWindow();
            Point topLeft = outputBox.getLocation();
            Point bottomLeft = new Point(topLeft.x, topLeft.y + outputBox.getHeight());
            if (!getViewport().getViewRect().contains(bottomLeft))
            {
                getScrollPane().getHorizontalScrollBar().setValue(0);
                getScrollPane().getVerticalScrollBar().setValue(bottomLeft.y - getViewport().getHeight());
            }
        }
    }

    private OutputBox postOutput(Output ... output)
    {
        OutputBox outputBox = null;
        for (int i = 0; i < output.length; i++)
        {
            outputBox = new OutputBox();
            outputBox.setOutput(output[i]);
            outputBox.setWorksheetWidth(getWidth());
            outputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(outputBox);
            repaint();
            getScrollPane().validate();
        }
        return outputBox;
    }

    @Override
    public synchronized void taskFinished(Task task)
    {
        if (currentSource == null)
            return;

        Output[] output = currentCapsule.getOutput();
        if (currentCapsule.isErrorOutput())
            getToolkit().beep();

        remove(getComponents().length-1);

        postOutput(output);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                advanceToNext();
            }
        });

        currentSource = null;
        currentCapsule = null;
    }

    private void advanceToNext()
    {
        add(Box.createVerticalStrut(10));
        Box cell = addNewCell();
        updateComponentSizes();
        validate();
        Point topLeft = cell.getLocation();
        Point bottomLeft = new Point(topLeft.x, topLeft.y + cell.getHeight());
        if (!getViewport().getViewRect().contains(bottomLeft))
        {
            getScrollPane().getHorizontalScrollBar().setValue(0);
            getScrollPane().getVerticalScrollBar().setValue(bottomLeft.y - getViewport().getHeight());
        }
        cell.getComponent(1).requestFocusInWindow();
        validate();
    }

    public void updateComponentSizes()
    {
        if (getComponentCount() == 0)
        {
            return;
        }
        int width = getViewport().getExtentSize().width;
        Component components[] = getComponents();
        for (int index = 0; index < components.length; index++)
        {
            if (components[index] instanceof Box)
            {
                Box box = (Box) components[index];
                EmbeddedTextArea eta = (EmbeddedTextArea) box.getComponent(1);
                int etaW = width - box.getComponent(0).getWidth();
                eta.setMinimumSize(new Dimension(etaW, eta.getMinimumSize().height));
                eta.setMaximumSize(new Dimension(etaW, eta.getMaximumSize().height));
                eta.setSize(etaW, eta.getHeight());
                eta.invalidate();
            }
            if (components[index] instanceof OutputBox)
            {
                OutputBox outputBox = (OutputBox) components[index];
                outputBox.setWorksheetWidth(width);
                outputBox.invalidate();
            }
        }
        getScrollPane().validate();
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return 40;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return 200;
    }

    @Override
    public boolean getScrollableTracksViewportWidth()
    {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
