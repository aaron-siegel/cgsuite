/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.lang.output;

import org.cgsuite.lang.CgsuiteClass;
import org.cgsuite.lang.CgsuiteObject;
import org.cgsuite.lang.CgsuiteString;
import org.openide.util.Lookup;

/**
 *
 * @author asiegel
 */
public class Utilities extends CgsuiteObject
{
    public static void print(CgsuiteObject obj)
    {
        OutputTarget ot = Lookup.getDefault().lookup(OutputTarget.class);
        ot.postOutput(obj.toOutput());
    }
    
    public static void print(String str)
    {
        print(new CgsuiteString(str));
    }
    
    public static long memory()
    {
        return Runtime.getRuntime().maxMemory();
    }
    
    public static String version()
    {
        String productVersion = System.getProperty("netbeans.productversion");
        assert productVersion.startsWith("CGSuite ");
        return productVersion.substring("CGSuite ".length());
    }
    
    private Utilities()
    {
        super(CgsuiteClass.OBJECT_TYPE);
    }
}
