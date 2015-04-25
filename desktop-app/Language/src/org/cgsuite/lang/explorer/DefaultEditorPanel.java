/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.explorer;

import java.awt.Color;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.output.OutputBox;

/**
 *
 * @author asiegel
 */
public class DefaultEditorPanel extends EditorPanel
{
    private CgsuiteObject obj;
    private OutputBox outputBox;

    public DefaultEditorPanel()
    {
        super();
        setBackground(Color.white);
        outputBox = new OutputBox();
        outputBox.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        outputBox.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
        outputBox.setWorksheetWidth(300);
        add(outputBox);
    }

    public DefaultEditorPanel(CgsuiteObject obj)
    {
        this();
        setDisplayedObject(obj);
    }

    public void setDisplayedObject(CgsuiteObject obj)
    {
        this.obj = obj;
        if (obj == null)
        {
            outputBox.setOutput(null);
        }
        else
        {
            outputBox.setOutput(obj.toOutput());
        }
        outputBox.revalidate();
    }

    @Override
    public CgsuiteObject constructObject()
    {
        return obj;
    }
}
