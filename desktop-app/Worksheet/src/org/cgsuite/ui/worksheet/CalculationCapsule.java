/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.ui.worksheet;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cgsuite.lang.CgscriptClasspath;
import org.cgsuite.lang.EvalUtil;
import org.cgsuite.output.Output;
import org.openide.util.RequestProcessor;
import scala.Symbol;
import scala.collection.JavaConverters;
import scala.collection.mutable.AnyRefMap;

/**
 *
 * @author asiegel
 */
public class CalculationCapsule implements Runnable
{
    private final static Logger log = Logger.getLogger(CalculationCapsule.class.getName());

    public final static RequestProcessor REQUEST_PROCESSOR = new RequestProcessor
        (WorksheetPanel.class.getName(), 1, true);

    private AnyRefMap<Symbol,Object> varMap;
    private String text;
    private List<Output> output;

    public CalculationCapsule(AnyRefMap<Symbol,Object> varMap, String text)
    {
        this.varMap = varMap;
        this.text = text;
    }
    
    public RequestProcessor.Task createTask()
    {
        return REQUEST_PROCESSOR.create(this);
    }
    
    public void runAndWait()
    {
        RequestProcessor.Task task = createTask();
        REQUEST_PROCESSOR.submit(task);
        task.waitFinished();
    }

    @Override
    public void run()
    {
        CgscriptClasspath.reloadModifiedFiles();
        try
        {
            output = JavaConverters.seqAsJavaList(EvalUtil.evaluate(text, varMap));
        }
        catch (Throwable exc)
        {
            log.log(Level.WARNING, "Exception thrown extracting error output!", exc);
            output = Collections.singletonList(EvalUtil.errorOutput("An unexpected error occurred."));
        }
    }
    
    public List<Output> getOutput()
    {
        return output;
    }

}
