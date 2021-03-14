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
    
    //TreeMap<Predictor, Object> observedInput;
    String observedOutput;
    /*
    public <T extends Object> boolean addInputAttributeWithValue(Predictor<T> predictor, T value) throws Exception{
        if(value != null){
            if(!value.getClass().equals(predictor.getPredictorType())){
                throw new Exception("Invalid predictor value for " + predictor.getPredictorName());
            }
        }
        
        if(getObservedInput() == null)
            setObservedInput(new TreeMap<Predictor, Object>());
        getObservedInput().put(predictor, value);
        
        return true;
    }

    public TreeMap<Predictor, Object> getObservedInput() {
        return observedInput;
    }

    private void setObservedInput(TreeMap<Predictor, Object> observedInput) {
        this.observedInput = observedInput;
    }
    */

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
