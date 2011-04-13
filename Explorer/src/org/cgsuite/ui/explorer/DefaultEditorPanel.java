/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.explorer;

import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.ui.worksheet.OutputBox;

/**
 *
 * @author asiegel
 */
public class DefaultEditorPanel extends EditorPanel
{
    private CgsuiteObject obj;
    private OutputBox outputBox;

    DefaultEditorPanel()
    {
        super();
        outputBox = new OutputBox();
        outputBox.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        outputBox.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
        outputBox.setWorksheetWidth(300);
        add(outputBox);
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

    CgsuiteObject getDisplayedObject()
    {
        return obj;
    }
}
