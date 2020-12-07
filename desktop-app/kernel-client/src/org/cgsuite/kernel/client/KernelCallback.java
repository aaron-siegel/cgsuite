/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.kernel.client;

import org.cgsuite.kernel.KernelResponse;

/**
 *
 * @author asiegel
 */
public interface KernelCallback {
    
    public void receive(KernelResponse response);
    
}
