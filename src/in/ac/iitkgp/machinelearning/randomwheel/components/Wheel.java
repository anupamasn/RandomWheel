/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.components;

import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingKnowledge;
import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import in.ac.iitkgp.machinelearning.utils.StatUtil;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Anupam
 */
public class Wheel implements Comparable {
    
    String classLabel;
    Force force;
    Double angularVelocity;

    public Wheel(String classLabel) {
        this.classLabel = classLabel;
    }
    
    public String getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(String classLabel) {
        this.classLabel = classLabel;
    }

    public Force getForce() {
        return force;
    }

    public void setForce(Force force) {
        this.force = force;
    }
    
    public Double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(Double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    public void rotate(ArrayList<Key> keys, TrainingKnowledge trainingKnowledge){
        Force force = new Force();
        force.apply(keys, getClassLabel(), trainingKnowledge);
        
        setForce(force);
        Double p_angularVelocity = force.getMagnitude();   // assuming velocity = force
        setAngularVelocity(p_angularVelocity);
    }
    /*
    private ElementaryForce getElementaryForce(Key key, TrainingKnowledge trainingKnowledge){
        try{
            Integer noOfClassLabelForTheKey = trainingKnowledge.getClassLabelCountForKeys().get(key).get(getClassLabel());
            Integer noOfTotalObservationForTheKey = StatUtil.getSum((trainingKnowledge.getClassLabelCountForKeys().get(key)));
            Integer noOfTotalClassLabelObserved = trainingKnowledge.getClassLabelCounts().get(getClassLabel());
            Integer sizeOfTrainingDataset = trainingKnowledge.getSizeOfTrainingDataset();
            
            //x is the key here whreas y is the class value
            Double support_xy = ((double)noOfClassLabelForTheKey / (double)sizeOfTrainingDataset);
            Double support_x = ((double)noOfTotalObservationForTheKey / (double)sizeOfTrainingDataset);
            Double support_y = ((double)noOfTotalClassLabelObserved/(double)sizeOfTrainingDataset);
            
            Double magnitude = (support_xy/(support_x * support_y));
            
            if(magnitude.isNaN())
                magnitude = 0.0;
            
            ElementaryForce elForce = new ElementaryForce(key, magnitude);
            
            return elForce;
        }catch(Exception ex){
            return new ElementaryForce(key, 0.0);
        }
    }
    */
    @Override
    public String toString() {
        return "Wheel{" + "classLabel=" + classLabel + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.classLabel);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Wheel other = (Wheel) obj;
        if (!Objects.equals(this.classLabel, other.classLabel)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        Wheel other = (Wheel)o;
        return this.classLabel.compareTo(other.classLabel);
    }
    
}
