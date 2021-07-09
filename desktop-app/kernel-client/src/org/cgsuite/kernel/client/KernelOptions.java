/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.kernel.client;

import org.openide.util.NbPreferences;

/**
 *
 * @author asiegel
 */
public class KernelOptions {
    
    public final static int DEFAULT_HEAP_SIZE_MB = 1024;
    
    public static int getHeapSizeMb() {
        return Integer.valueOf(
            NbPreferences.forModule(KernelOptionsPanel.class).get("heapSizeMb", String.valueOf(DEFAULT_HEAP_SIZE_MB))
        );
    }

    public static void setHeapSizeMb(int mb) {
        NbPreferences.forModule(KernelOptionsPanel.class).put("heapSizeMb", String.valueOf(mb));
    }

}
