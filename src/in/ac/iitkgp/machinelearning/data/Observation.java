/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.data;

import java.util.TreeMap;

/**
 *
 * @author Anupam
 */
public class Observation extends UnknownObservation {
    
    String observedOutput;
    
    public Observation(TreeMap<Predictor, Object> observedInput, String observedOutput) {
        super(observedInput);
        this.observedOutput = observedOutput;
    }

    public Observation() {
        super();
    }
    
    public String getObservedOutput() {
        return observedOutput;
    }

    public void setObservedOutput(String observedOutput) {
        this.observedOutput = observedOutput;
    }
    
}
