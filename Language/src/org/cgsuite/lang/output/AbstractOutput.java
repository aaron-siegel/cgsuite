/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.output;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.cgsuite.lang.output.Output.Mode;

/**
 *
 * @author asiegel
 */
public abstract class AbstractOutput implements Output
{
    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        write(new PrintWriter(sw), Mode.PLAIN_TEXT);
        return sw.toString();
    }
}
