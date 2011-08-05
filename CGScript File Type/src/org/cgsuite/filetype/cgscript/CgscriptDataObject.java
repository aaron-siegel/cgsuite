/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.filetype.cgscript;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.SaveAsCapable;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;

public class CgscriptDataObject extends MultiDataObject
{
    private DataEditorSupport des;
    
    public CgscriptDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException
    {
        super(pf, loader);
        
        CookieSet cookies = getCookieSet();
        
        // Create DataEditorSupport for this object and add it as a cookie
        des = (DataEditorSupport) DataEditorSupport.create(this, getPrimaryEntry(), cookies);
        cookies.add((Cookie) des);
        
        // Add the SaveAs capability
        getCookieSet().assign(SaveAsCapable.class, new SaveAsCapable()
        {
            @Override
            public void saveAs(FileObject folder, String fileName) throws IOException
            {
                des.saveAs(folder, fileName);
            }
        });
    }

    @Override
    protected Node createNodeDelegate()
    {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup()
    {
        return getCookieSet().getLookup();
    }

}
