/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.output;

import java.io.PrintWriter;
import java.io.StringWriter;

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
