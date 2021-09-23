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
import org.cgsuite.output.Output;
import org.openide.util.RequestProcessor;
import org.slf4j.LoggerFactory;
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
        if (":debug".equals(text))
        {
            ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME))
                .setLevel(ch.qos.logback.classic.Level.DEBUG);
            output = Collections.<Output>emptyList();
        }
        else if (":info".equals(text))
        {
            ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME))
                .setLevel(ch.qos.logback.classic.Level.INFO);
            output = Collections.<Output>emptyList();
        }
        else
        {
            CgscriptClasspath.reloadModifiedFiles();
            try
            {
                long startTime = System.nanoTime();
                output = JavaConverters.seqAsJavaList(org.cgsuite.lang.System.evaluate(text, varMap));
                long duration = System.nanoTime() - startTime;
                log.info(String.format("Calculation finished in %d.%03d seconds.", duration / 1000000000L, (duration % 1000000000L) / 1000000L));
            }
            catch (Throwable exc)
            {
                log.log(Level.WARNING, "Exception thrown extracting error output!", exc);
                output = Collections.singletonList(org.cgsuite.lang.System.errorOutput("An unexpected error occurred."));
            }
        }
    }
    
    public List<Output> getOutput()
    {
        return output;
    }

}
