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
import javax.swing.DefaultListModel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
    private FileObject historyFile;
    
    public CommandHistoryBufferImpl()
    {
        listeners = new ArrayList<CommandListener>();
    }
    
    void load()
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
                    addElement(command.replace('\1', '\n'));
                }
            }
        }
        catch (IOException exc)
        {
        }
        
        trim();
    }
    
    void save()
    {
        try
        {
            PrintStream stream = new PrintStream(historyFile.getOutputStream());
            for (Enumeration<?> e = elements(); e.hasMoreElements();)
            {
                String command = (String) e.nextElement();
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
            trim();
            save();
        }
    }
    
    private void trim()
    {
        if (size() > CAPACITY)
        {
            this.removeRange(0, size()-CAPACITY);
        }
    }
}
