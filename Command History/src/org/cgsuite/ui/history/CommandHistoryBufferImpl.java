/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.history;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author asiegel
 */
@ServiceProvider(service=CommandHistoryBuffer.class)
public class CommandHistoryBufferImpl extends DefaultListModel implements CommandHistoryBuffer
{
    private final static int CAPACITY = 10000;
    private List<CommandListener> listeners;
    
    public CommandHistoryBufferImpl()
    {
        listeners = new ArrayList<CommandListener>();
    }
    
    @Override
    public void addCommandListener(CommandListener l)
    {
        listeners.add(l);
    }
    
    @Override
    public void removeCommandListener(CommandListener l)
    {
        listeners.remove(l);
    }
    
    @Override
    public String get(int index)
    {
        return (String) getElementAt(index);
    }
    
    void fireCommandActivated(String str)
    {
        System.out.println(listeners);
        for (CommandListener l : listeners)
        {
            l.commandActivated(str);
        }
    }

    @Override
    public void addCommand(String command)
    {
        if (isEmpty() || !lastElement().equals(command))
        {
            addElement(command);
        }
        trim();
    }
    
    String serializeToString()
    {
        StringBuilder str = new StringBuilder();
        for (Enumeration<?> e = elements(); e.hasMoreElements();)
        {
            String cmd = (String) e.nextElement();
            str.append(cmd);
            if (e.hasMoreElements())
                str.append('`');
        }
        return str.toString();
    }
    
    void deserializeFromString(String str)
    {
        for (String cmd : str.split("`"))
        {
            addElement(cmd);
        }
        trim();
    }
    
    private void trim()
    {
        if (size() > CAPACITY)
        {
            this.removeRange(0, size()-CAPACITY);
        }
    }
}
