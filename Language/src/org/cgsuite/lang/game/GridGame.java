/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.game;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteList;
import org.cgsuite.lang.CgsuiteString;
import org.cgsuite.lang.Game;
import org.cgsuite.lang.explorer.EditorPanel;
import org.cgsuite.lang.explorer.GridEditorPanel;
import org.cgsuite.lang.output.GridOutput;

/**
 *
 * @author asiegel
 */
public class GridGame extends Game
{
    public GridGame(CgsuiteClass type)
    {
        super(type);
    }

    @Override
    public GridOutput toOutput()
    {
        return new GridOutput(getGrid(), getIcons(), super.toOutput().toString());
    }

    @Override
    public EditorPanel toEditor()
    {
        return new GridEditorPanel(getCgsuiteClass(), getGrid(), getIcons());
    }

    public Grid getGrid()
    {
        return (Grid) resolve("Grid");
    }
    
    public String getCharMap()
    {
        return ((CgsuiteString) resolve("CharMap")).toJavaString();
    }

    public CgsuiteList getIcons()
    {
        return (CgsuiteList) resolve("Icons");
    }
}
