/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.explorer;

import org.cgsuite.ui.worksheet.ExplorerService;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author asiegel
 */
@ServiceProvider(service=ExplorerService.class)
public class ExplorerServiceImpl implements ExplorerService
{
    @Override
    public Class<?> getExplorerClass()
    {
        return Explorer.class;
    }
    
}
