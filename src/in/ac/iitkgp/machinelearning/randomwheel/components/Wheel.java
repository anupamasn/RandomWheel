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
        Force resultantForce = new Force();
        resultantForce.apply(keys, getClassLabel(), trainingKnowledge);
        setForce(resultantForce);
        
        Double p_angularVelocity = resultantForce.getMagnitude();   // assuming velocity = force
        setAngularVelocity(p_angularVelocity);
    }
    
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
