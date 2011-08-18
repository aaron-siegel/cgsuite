/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.worksheet;

import org.cgsuite.lang.output.Output;
import org.cgsuite.lang.output.OutputTarget;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author asiegel
 */
@ServiceProvider(service=OutputTarget.class)
public class WorksheetOutputTarget implements OutputTarget
{
    @Override
    public void postOutput(final Output output)
    {
        WorksheetTopComponent.getDefault().postOutput(output);
    }
}
