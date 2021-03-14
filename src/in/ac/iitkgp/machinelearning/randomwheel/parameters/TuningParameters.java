/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.parameters;

/**
 *
 * @author Anupam
 */
public final class TuningParameters {
    
    Integer noOfTrial = 0;
    Integer depth = 1;
    Double noiseFraction = null;
    
    public TuningParameters(Integer noOfTrial, Integer depth, Double noiseFraction) {
        setNoOfTrial(noOfTrial);
        setDepth(depth);
        setNoiseFraction(noiseFraction);
    }

    public Double getNoiseFraction() {
        return noiseFraction;
    }

    public void setNoiseFraction(Double noiseFraction) {
        this.noiseFraction = noiseFraction;
    }
    
    public Integer getDepth() {
        return depth;
    }

    public Integer getNoOfTrial() {
        return noOfTrial;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public void setNoOfTrial(Integer noOfTrial) {
        this.noOfTrial = noOfTrial;
    }

    
}
