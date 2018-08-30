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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.cgsuite.output.Output;
import org.cgsuite.output.OutputBox;
import org.cgsuite.output.StyledTextOutput;
import org.cgsuite.ui.history.CommandHistoryBuffer;
import org.cgsuite.ui.history.CommandListener;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import scala.Symbol;
import scala.collection.mutable.AnyRefMap;


// TODO Check if there are unsaved files before starting a calculation
// TODO Make error output singly selectable

/**
 *
 * @author asiegel
 */
public class WorksheetPanel extends JPanel
    implements Scrollable, KeyListener, TaskListener, DocumentListener, CommandListener
{
    private final static AnyRefMap<Symbol,Object> WORKSPACE_VAR_MAP = new AnyRefMap<Symbol,Object>();
    
    private boolean deferredAdvance;
    
    private InputPanel activeInputPanel;
    
    private CalculationCapsule currentCapsule;
    private RequestProcessor.Task currentTask;
    private Box.Filler strut;

    private String commandHistoryPrefix;
    private int commandHistoryIndex;
    private boolean seekingCommand;
    
    private CommandHistoryBuffer buffer;
    
    private Queue<List<Output>> outputQueue;
    private OutputBox calculatingOutputBox;

    /** Creates new form WorksheetPanel */
    public WorksheetPanel()
    {
        initComponents();
        outputQueue = new LinkedBlockingQueue<List<Output>>();
        strut = (Box.Filler) Box.createHorizontalStrut(0);
        strut.setAlignmentX(LEFT_ALIGNMENT);
        add(strut);
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

    public void initialize()
    {
        // Forcibly instantiate a CanonicalShortGame so that the interface will seem snappier
        // once the user starts using it
        new CalculationCapsule(WORKSPACE_VAR_MAP, "{1|1/2}").runAndWait();
        processCommand("startup();");
        getBuffer();
        
        getViewport().addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent evt)
            {
                if (deferredAdvance && getViewport().getWidth() > 0)
                {
                    deferredAdvance = false;
                    advanceToNext();
                }
                updateComponentSizes();
            }
        });
        
        // This is a total hack to fight back against the help component stealing focus at startup.
        Thread focusThread = new Thread("CGSuite Focus Thread")
        {
            @Override
            public synchronized void run()
            {
                try
                {
                    wait(1500);
                }
                catch (InterruptedException exc)
                {
                }
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateFocus();
                    }
                });
            }
        };
        
        focusThread.start();
    }
    
    private CommandHistoryBuffer getBuffer()
    {
        if (buffer == null)
        {
            TopComponent tc = WindowManager.getDefault().findTopComponent("CommandHistoryTopComponent");
            if (tc != null)
            {
                buffer = tc.getLookup().lookupResult(CommandHistoryBuffer.class)
                        .allInstances().iterator().next();
                buffer.addCommandListener(this);
            }
        }
        return buffer;
    }
    
    public void updateFocus()
    {
        if (activeInputPanel != null)
        {
            activeInputPanel.getInputPane().requestFocus();
        }
    }

    private JViewport getViewport()
    {
        return (JViewport) getParent();
    }

    private JScrollPane getScrollPane()
    {
        return (JScrollPane) getParent().getParent();
    }

    private void addNewCell()
    {
        activeInputPanel = new InputPanel();
        activeInputPanel.activate();
        activeInputPanel.getInputPane().addKeyListener(this);
        activeInputPanel.getInputPane().getDocument().addDocumentListener(this);
        add(activeInputPanel);
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        if (activeInputPanel == null)
            return;
        
        InputPane source = activeInputPanel.getInputPane();

        switch (evt.getKeyCode())
        {
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
                        commandHistoryIndex = getBuffer().getSize();
                    }
                    seekingCommand = true;
                    source.setText(seekCommand(-1));
                    scrollToBottomLeft();
                    updateFocus();
                }
                break;

            case KeyEvent.VK_DOWN:
                if (evt.getModifiers() == 0 && commandHistoryPrefix != null &&
                    (source.getCaretLine() == source.getLineCount()-1))
                {
                    evt.consume();
                    seekingCommand = true;
                    source.setText(seekCommand(1));
                    scrollToBottomLeft();
                    updateFocus();
                }
                break;

            default:
                break;
        }
    }

    private String seekCommand(int direction)
    {
        for (commandHistoryIndex += direction;
             commandHistoryIndex >= 0 && commandHistoryIndex < getBuffer().getSize();
             commandHistoryIndex--)
        {
            if (getBuffer().getElementAt(commandHistoryIndex).startsWith(commandHistoryPrefix))
            {
                return getBuffer().getElementAt(commandHistoryIndex);
            }
        }
        commandHistoryIndex = getBuffer().getSize();
        return commandHistoryPrefix;
    }
    
    private synchronized void processCommand(InputPane source)
    {
        getBuffer().addCommand(source.getText());
        activeInputPanel.getInputPane().removeKeyListener(this);
        activeInputPanel.getInputPane().getDocument().removeDocumentListener(this);
        activeInputPanel.deactivate();
        activeInputPanel = null;
        commandHistoryPrefix = null;
        processCommand(source.getText());
    }
    
    private synchronized void processCommand(String command)
    {
        assert SwingUtilities.isEventDispatchThread();
        
        CalculationCapsule capsule = new CalculationCapsule(WORKSPACE_VAR_MAP, command);
        RequestProcessor.Task task = capsule.createTask();
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
        
        if (finished)
        {
            /*
            if (capsule.isErrorOutput())
                getToolkit().beep();
            */
            assert capsule.getOutput() != null;
            
            postOutput(capsule.getOutput());
        }
        else
        {
            this.currentCapsule = capsule;
            this.currentTask = task;
        }
        
        drainOutput();

        if (finished)
        {
            advanceToNext();
        }
        else
        {
            this.requestFocusInWindow();
        }
    }

    public synchronized void postOutput(List<Output> output)
    {
        outputQueue.add(output);
        
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                drainOutput();
            }
        });
    }
    
    private synchronized void drainOutput()
    {
        assert SwingUtilities.isEventDispatchThread();
        
        boolean update = false;
        
        while (!outputQueue.isEmpty())
        {
            List<Output> outputs = outputQueue.remove();
            for (Output output : outputs)
            {
                add(makeOutputBox(output), calculatingOutputBox == null ? getComponentCount() : getComponentCount()-1);
                update = true;
            }
        }
        
        if (currentCapsule == null && calculatingOutputBox != null)
        {
            remove(calculatingOutputBox);
            calculatingOutputBox = null;
            update = true;
        }
        else if (currentCapsule != null && calculatingOutputBox == null)
        {
            calculatingOutputBox = makeOutputBox(new StyledTextOutput("Calculating ..."));
            add(calculatingOutputBox);
            update = true;
        }
        
        if (update)
        {
            updateComponentSizes();
            scrollToBottomLeft();
            repaint();
            getScrollPane().validate();
        }
    }
    
    private OutputBox makeOutputBox(Output output)
    {
        OutputBox outputBox = new OutputBox();
        outputBox.setOutput(output);
        outputBox.setWorksheetWidth(getWidth());
        outputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        return outputBox;
    }

    public void clear()
    {
        this.removeAll();
    }

    @Override
    public synchronized void taskFinished(Task task)
    {
        if (currentCapsule == null)
            return;
        
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                List<Output> output = currentCapsule.getOutput();
                assert output != null;
                /*
                if (currentCapsule.isErrorOutput())
                    getToolkit().beep();
                */
                currentCapsule = null;
                currentTask = null;

                postOutput(output);
                drainOutput();
                advanceToNext();
            }
        });
    }

    private void advanceToNext()
    {
        assert SwingUtilities.isEventDispatchThread();
        
        if (getViewport().getWidth() <= 0)
        {
            deferredAdvance = true;
            return;
        }
        
        if (getComponentCount() > 2)
        {
            add(Box.createVerticalStrut(10));
        }
        
        addNewCell();
        updateComponentSizes();
        scrollToBottomLeft();
        updateFocus();
        validate();
    }

    private void scrollToBottomLeft()
    {
        assert SwingUtilities.isEventDispatchThread();
        
        Component component = getComponent(getComponentCount()-1);
        Point topLeft = component.getLocation();
        Point bottomLeft = new Point(topLeft.x, topLeft.y + component.getHeight());
        if (!getViewport().getViewRect().contains(bottomLeft))
        {
            getScrollPane().getHorizontalScrollBar().setValue(0);
            getScrollPane().getVerticalScrollBar().setValue(bottomLeft.y - getViewport().getHeight());
        }
    }

    public void updateComponentSizes()
    {
        assert SwingUtilities.isEventDispatchThread();
        
        if (getComponentCount() == 0)
            return;

        int width = getViewport().getWidth();
        
        if (width <= 0)
            return;
        
        Dimension dim = new Dimension(width, 0);
        strut.changeShape(dim, dim, dim);
        
        Component components[] = getComponents();
        for (int index = 0; index < components.length; index++)
        {
            if (components[index] instanceof InputPanel)
            {
                InputPanel cell = (InputPanel) components[index];
                InputPane pane = cell.getInputPane();
                if (((InputPanel) pane.getParent()).isDeactivated())
                {
                    int worksheetWidth = width - cell.getPrompt().getWidth();
                    pane.setMinimumSize(new Dimension(worksheetWidth, pane.getMinimumSize().height));
                    pane.setMaximumSize(new Dimension(worksheetWidth, pane.getMaximumSize().height));
                    pane.revalidate();
                }
            }
            if (components[index] instanceof OutputBox)
            {
                OutputBox outputBox = (OutputBox) components[index];
                outputBox.setWorksheetWidth(width);
                outputBox.revalidate();
            }
        }
        getScrollPane().validate();
    }
    
    public void killCalculation()
    {
        if (currentTask != null)
        {
            currentTask.cancel();
        }
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

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        if (seekingCommand)
        {
            seekingCommand = false;     // It just got inserted
        }
        else
        {
            commandHistoryPrefix = null;
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        if (!seekingCommand)
        {
            commandHistoryPrefix = null;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
    }

    @Override
    public void commandActivated(String command)
    {
        assert SwingUtilities.isEventDispatchThread();
        
        if (this.getComponentCount() == 0)
            return;
        
        Component lastComponent = this.getComponent(this.getComponentCount()-1);
        if (!(lastComponent instanceof InputPanel))
        {
            getToolkit().beep();
            return;
        }
        
        InputPanel inputPanel = (InputPanel) lastComponent;
        inputPanel.getInputPane().setText(command);
        inputPanel.getInputPane().requestFocus();
    }

    @Override
    public void keyTyped(KeyEvent ke)
    {
    }

    @Override
    public void keyReleased(KeyEvent ke)
    {
    }

}
