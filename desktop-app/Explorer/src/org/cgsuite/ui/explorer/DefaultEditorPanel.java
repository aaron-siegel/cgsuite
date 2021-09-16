/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.explorer;

import java.awt.Color;
import org.cgsuite.output.Output;
import org.cgsuite.output.OutputBox;
import org.cgsuite.output.OutputTarget;

/**
 *
 * @author asiegel
 */
public class DefaultEditorPanel extends EditorPanel
{
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

    public DefaultEditorPanel(Output output)
    {
        this();
        setDisplayedOutput(output);
    }

    public final void setDisplayedOutput(Output output)
    {
        outputBox.setOutput(output);
        outputBox.revalidate();
    }

    @Override
    public OutputTarget constructObject()
    {
        return null;
    }
}
