/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang;

import java.util.List;
import org.cgsuite.lang.parser.CgsuiteParser.SyntaxError;
import org.openide.filesystems.FileObject;

/**
 *
 * @author asiegel
 */
public class CgsuiteClassLoadException extends CgsuiteException
{
    private static final long serialVersionUID = 1L;
    
    private FileObject fo;
    private List<SyntaxError> syntaxErrors;

    public CgsuiteClassLoadException(FileObject fo, List<SyntaxError> syntaxErrors)
    {
        super(syntaxErrors.get(0).getMessage());
        
        this.fo = fo;
        this.syntaxErrors = syntaxErrors;
    }
    
    public CgsuiteClassLoadException(FileObject fo, Throwable cause)
    {
        super(cause);
        
        this.fo = fo;
    }
    
    public FileObject getClassFile()
    {
        return fo;
    }
    
    public List<SyntaxError> getSyntaxErrors()
    {
        return syntaxErrors;
    }
}
