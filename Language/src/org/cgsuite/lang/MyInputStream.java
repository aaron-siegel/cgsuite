/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.io.IOException;
import java.io.InputStream;
import org.antlr.runtime.ANTLRInputStream;

/**
 *
 * @author asiegel
 */
public class MyInputStream extends ANTLRInputStream
{
    private String source;

    public MyInputStream(InputStream in, String source) throws IOException
    {
        super(in);
        this.source = source;
    }

    @Override
    public String getSourceName()
    {
        return source;
    }
}
