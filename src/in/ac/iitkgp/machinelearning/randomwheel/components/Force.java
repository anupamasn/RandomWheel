/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.components;

import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingKnowledge;
import java.util.ArrayList;

/**
 *
 * @author Anupam
 */
public class Force {
    
    Double magnitude;
    ArrayList<ElementaryForce> elementaryForces;

    public Force() {
        setElementaryForces(new ArrayList<ElementaryForce>());
        setMagnitude(0.0);
    }

    public Double getMagnitude() {
        return magnitude;
    }

    private void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public ArrayList<ElementaryForce> getElementaryForces() {
        return elementaryForces;
    }

    private void setElementaryForces(ArrayList<ElementaryForce> elementaryForces) {
        this.elementaryForces = elementaryForces;
    }
    
    public void apply(ArrayList<Key> keys, String classLabel, TrainingKnowledge trainingKnowledge){
        
        for(Key key : keys){
            try{
                ElementaryForce elForce = new ElementaryForce(key);
                elForce.apply(classLabel, trainingKnowledge);
                elementaryForces.add(elForce);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
        //aggregating elementary forces to measure the magnitude of resultant force
        Double magnitude_of_resultant_force = 0.0;
        //for each elementary force
        for(ElementaryForce p_elementaryForce : elementaryForces){
            try{
                magnitude_of_resultant_force += (p_elementaryForce.getKey().getWeightage() * p_elementaryForce.getMagnituge());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
        setMagnitude(magnitude_of_resultant_force);
    }
    
}
