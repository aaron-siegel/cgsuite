/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.ui.worksheet;

import org.cgsuite.core.Game;
import org.cgsuite.util.Explorer;

/**
 *
 * @author asiegel
 */
public interface ExplorerService
{
    Explorer newExplorer(Game g);
}
