package org.cgsuite.ui.tips;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.tips.TipLoader;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * Action which shows "Tip Of The Day".
 */
@SuppressWarnings("deprecation")
public class TipTCAction extends AbstractAction
{
    //Here we set the folder in the user directory
    //that we will use to store our tips.properties file.
    private static FileObject prefsFolder = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Preferences");
    
    //Encoding:
    private static final String ENCODING = "UTF-8"; // NOI18N
    
    public TipTCAction() {
        super(NbBundle.getMessage(TipTCAction.class, "CTL_TipTCAction"));
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        showDialog(true);
    }
    
    public static void showDialog(final boolean force)
    {
        try
        {
            final JXTipOfTheDay jXTipOfTheDay1 = new JXTipOfTheDay(loadModel());
            jXTipOfTheDay1.setCurrentTip(getStartingTipLocation());
            jXTipOfTheDay1.showDialog(null, new JXTipOfTheDay.ShowOnStartupChoice()
            {
                @Override
                public boolean isShowingOnStartup()
                {
                    //Always show when menu item used:
                    return isStartupChoiceOption();
                }
                
                @Override
                public void setShowingOnStartup(boolean showOnStartup)
                {
                    //Store whether to show at start up next time:
                    setStartupChoiceOption(showOnStartup);
                    //Store next tip location, sending current and total tips:
                    setNextStartingTipLocation(jXTipOfTheDay1.getCurrentTip(), jXTipOfTheDay1.getModel().getTipCount());
                }
            }, force);
        }
        catch (Exception ex)
        {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private static TipOfTheDayModel loadModel() throws Exception {
        //Load the tips into the tip loader:
        InputStream propertiesIn = TipTCAction.class.getResourceAsStream("resources/tips.properties");
        Properties properties = new Properties();
        properties.load(propertiesIn);
        return TipLoader.load(properties);
        
    }
    
    //Return whether the tip dialog should be shown at start up:
    private static boolean isStartupChoiceOption() {
        Preferences pref = NbPreferences.forModule(TipTCAction.class);
        boolean s = pref.getBoolean("StartUpPref", true);
        return s;
    }
    
    //Store whether the tip dialog should be shown at start up:
    private static void setStartupChoiceOption(boolean val) {
        NbPreferences.forModule(TipTCAction.class).putBoolean("StartUpPref", val);
        System.out.println("Show Tips on Startup: " + val);
    }
    
    //Get the tip to be shown,
    //via the NbPreferences API:
    private static int getStartingTipLocation() {
        Preferences pref = NbPreferences.forModule(TipTCAction.class);
        //Return the first tip if pref is null:
        if (pref == null) {
            return 0;
        //Otherwise, return the tip found via NbPreferences API,
        //with '0' as the default:    
        } else {
            int s = pref.getInt("StartTipPref", 0);
            return s;
        }
    }
    
    //Set the tip that will be shown next time,
    //we receive the current tip and the total tips:
    private static void setNextStartingTipLocation(int loc,int tot) {
        
        int nextTip = 0;
        //Back to zero if the maximum is reached:
        if (loc + 1 == tot) {
            nextTip = 0;
        //Otherwise find the next tip and store it:
        } else {
            nextTip = loc + 1;
        }
        
        //Store the tip, via the NbPreferences API,
        //so that it will be stored in the NetBeans user directory:
        NbPreferences.forModule(TipTCAction.class).putInt("StartTipPref", nextTip);
        
        System.out.println("Total tips: " + tot);
        System.out.println("Current tip location: " + loc);
        System.out.println("Future tip location: " + nextTip);
    }
    
    //All the remaining methods that follow deal with the situation
    //where no tips.properties file is found, so
    //it must be created from our template in the 'resources' folder/
    
    //Get the tip.properties resource from its folder:
    private String readResource(String name) throws IOException {
        return readTextResource(getClass().getResourceAsStream("resources/" + name), ENCODING); // NOI18N
    }
    
    //Read the content line by line:
    private static String readTextResource(InputStream is, String encoding) throws IOException {
        StringBuilder sb = new StringBuilder();
        String lineSep = System.getProperty("line.separator"); //NOI18N
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(lineSep);
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }
    
    //Create the file:
    private static void createFile(FileObject targetFolder, String name, String content) throws IOException {
        FileObject file = FileUtil.createData(targetFolder, name);
        writeFile(file, content, ENCODING);
    }
    
    //Write the content of the file:
    private static void writeFile(FileObject file, String content, String encoding) throws IOException {
        FileLock lock = file.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(file.getOutputStream(lock), encoding));
            try {
                bw.write(content);
            } finally {
                bw.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
}
