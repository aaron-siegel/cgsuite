/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.history;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author asiegel
 */
@ServiceProvider(service=CommandHistoryBuffer.class)
public class CommandHistoryBufferImpl extends AbstractListModel implements CommandHistoryBuffer
{
    private final static Logger LOG = Logger.getLogger(CommandHistoryBufferImpl.class.getName());
    
    private final static int CAPACITY = 10000;
    private ArrayList<String> history;
    private List<CommandListener> listeners;
    private FileObject historyFile;
    
    public CommandHistoryBufferImpl()
    {
        history = new ArrayList<String>();
        listeners = new ArrayList<CommandListener>();
    }
    
    synchronized void load()
    {
        try
        {
            historyFile = FileUtil.getConfigFile("CommandHistory.txt");
            if (historyFile == null)
            {
                historyFile = FileUtil.getConfigRoot().createData("CommandHistory.txt");
            }
            else
            {
                for (String command : historyFile.asLines())
                {
                    history.add(command.replace('\1', '\n'));
                }
            }
        }
        catch (IOException exc)
        {
        }
        
        trim();
    }
    
    synchronized void save()
    {
        try
        {
            PrintStream stream = new PrintStream(historyFile.getOutputStream());
            for (String command : history)
            {
                String escaped = command.replaceAll("\\\r(\\\n)?|\\\n", "\1");
                stream.println(escaped);
            }
            stream.close();
        }
        catch (IOException exc)
        {
        }
    }
    
    @Override
    public synchronized void addCommandListener(CommandListener l)
    {
        listeners.add(l);
    }
    
    @Override
    public synchronized void removeCommandListener(CommandListener l)
    {
        listeners.remove(l);
    }
    
    @Override
    public synchronized int getSize()
    {
        return history.size();
    }

    @Override
    public synchronized String getElementAt(int index)
    {
        if (index < 0 || index >= history.size())
        {
            LOG.log(Level.SEVERE, "Index out of bounds: " + index + " (size is " + history.size() + ")");
            return "";
        }
        
        return history.get(index);
    }
    
    void fireCommandActivated(String str)
    {
        for (CommandListener l : listeners)
        {
            l.commandActivated(str);
        }
    }

    @Override
    public synchronized void addCommand(String command)
    {
        if (history.isEmpty() || !history.get(history.size()-1).equals(command))
        {
            history.add(command);
            trim();
            save();
        }
        this.fireIntervalAdded(this, history.size()-1, history.size()-1);
    }
    
    private synchronized void trim()
    {
        int excess = getSize() - CAPACITY;
        if (excess > 0)
        {
            history.subList(0, excess).clear();
            this.fireIntervalRemoved(this, 0, excess-1);
        }
    }
}
