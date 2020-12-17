/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.kernel.client;

import java.util.Collections;
import java.util.List;
import org.cgsuite.output.Output;
import org.cgsuite.output.StyledTextOutput;

/**
 *
 * @author asiegel
 */
public class KernelDispatch {
    
    private List<Output> output;
    private boolean isFinal;
    
    public KernelDispatch(List<Output> output, boolean isFinal) {
        this.output = output;
        this.isFinal = isFinal;
    }
    
    public KernelDispatch(String message, boolean isFinal) {
        this.output = Collections.singletonList(new StyledTextOutput(message));
        this.isFinal = isFinal;
    }
    
    public List<Output> getOutput() {
        return output;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
}
