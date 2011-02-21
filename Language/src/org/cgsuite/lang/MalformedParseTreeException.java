/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import org.cgsuite.CgsuiteException;

/**
 *
 * @author asiegel
 */
public class MalformedParseTreeException extends CgsuiteException
{
    private CgsuiteTree tree;

    public MalformedParseTreeException(CgsuiteTree tree)
    {
        super(tree.getToken() + ": " + tree.toStringTree());
        this.tree = tree;
    }
}
