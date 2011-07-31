/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author asiegel
 */
public class FunctionalTestReader
{
    private BufferedReader reader;
    
    public FunctionalTestReader(InputStream in)
    {
        reader = new BufferedReader(new InputStreamReader(in));
    }
    
    public String[] nextTestInstance() throws IOException
    {
        List<String> instance = new ArrayList<String>();
        String line;
        String nextStr = "";
        while ((line = reader.readLine()) != null)
        {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//"))
                continue;
            
            String[] split = line.split("\\\\");
            for (int i = 0; i < split.length; i++)
            {
                nextStr += split[i];
                if (i < split.length-1 || line.charAt(line.length()-1) == '\\')
                {
                    instance.add(nextStr.trim());
                    nextStr = "";
                }
                else
                {
                    nextStr += '\n';
                }
            }
            
            if (instance.size() >= 3)
                break;
        }
        
        if (instance.size() == 0)
            return null;
        else
            return instance.toArray(new String[3]);
    }
}
