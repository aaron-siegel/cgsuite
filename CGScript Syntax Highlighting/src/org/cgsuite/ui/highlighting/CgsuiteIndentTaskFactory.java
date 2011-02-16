/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.highlighting;

import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.IndentTask;

/**
 *
 * @author asiegel
 */
public class CgsuiteIndentTaskFactory implements IndentTask.Factory
{
    @Override
    public IndentTask createTask(Context context)
    {
        return new CgsuiteIndentTask(context);
    }
}
