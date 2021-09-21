/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang.parser;

import org.antlr.runtime.ANTLRInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author asiegel
 */
public class SourcedAntlrInputStream extends ANTLRInputStream
{
    private String source;

    public SourcedAntlrInputStream(InputStream in, String source) throws IOException
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
