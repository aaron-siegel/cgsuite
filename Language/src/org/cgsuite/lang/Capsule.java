/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.lang;

import java.io.IOException;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.cgsuite.CgsuiteException;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;

/**
 *
 * @author asiegel
 */
public class Capsule implements FileChangeListener
{
    private FileObject fo;
    private boolean loaded;
    private CgsuiteTree parseTree;

    public Capsule(FileObject fo)
    {
        this.fo = fo;
        this.loaded = false;
        this.fo.addFileChangeListener(this);
    }

    public CgsuiteTree loadParseTree() throws RecognitionException, IOException
    {
        if (!loaded)
        {
            ANTLRInputStream in = new MyInputStream(fo.getInputStream(), fo.getNameExt());
            CgsuiteLexer lexer = new CgsuiteLexer(in);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CgsuiteParser parser = new CgsuiteParser(tokens);
            parser.setTreeAdaptor(new CgsuiteTreeAdaptor());
            CgsuiteParser.compilationUnit_return r = parser.compilationUnit();
            if (parser.getNumberOfSyntaxErrors() == 0)
            {
                parseTree = (CgsuiteTree) r.getTree();
                loaded = true;
            }
            else
            {
                throw new CgsuiteException("Syntax error(s) in " + fo.getNameExt() + ":\n" + parser.getErrorMessageString());
            }
        }

        return parseTree;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    @Override
    public void fileFolderCreated(FileEvent fe)
    {
    }

    @Override
    public void fileDataCreated(FileEvent fe)
    {
    }

    @Override
    public void fileChanged(FileEvent fe)
    {
        loaded = false;
    }

    @Override
    public void fileDeleted(FileEvent fe)
    {
        loaded = false;
    }

    @Override
    public void fileRenamed(FileRenameEvent fre)
    {
        loaded = false;
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fae)
    {
    }
}
