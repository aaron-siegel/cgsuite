/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.script;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class CgsFileFilter extends FileFilter
{
    public final static CgsFileFilter INSTANCE = new CgsFileFilter();
    
    private CgsFileFilter()
    {
    }
    
    @Override
    public boolean accept(File pathname)
    {
        if (pathname.isDirectory())
        {
            return true;
        }

        String[] path  = pathname.getPath().split("\\.");
        if (path[path.length - 1].equalsIgnoreCase("cgs"))
        {
            return true;
        }

        return false;
    }

    @Override
    public String getDescription()
    {
        return "CGScript Files";
    }
}
