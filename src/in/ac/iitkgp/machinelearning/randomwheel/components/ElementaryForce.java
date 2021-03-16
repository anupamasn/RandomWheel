/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.components;

import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingKnowledge;
import in.ac.iitkgp.machinelearning.utils.StatUtil;
import java.util.Objects;

/**
 *
 * @author Anupam
 */
public class ElementaryForce {
    
    Key key;
    Double magnituge;

    public ElementaryForce(Key key) {
        this.key = key;
    }
    
    public void measure(String classLabel, TrainingKnowledge trainingKnowledge){
        try{
            Integer noOfClassLabelForTheKey = trainingKnowledge.getClassLabelCountForKeys().get(key).get(classLabel);
            Integer noOfTotalObservationForTheKey = StatUtil.getSum((trainingKnowledge.getClassLabelCountForKeys().get(key)));
            Integer noOfTotalClassLabelObserved = trainingKnowledge.getClassLabelCounts().get(classLabel);
            Integer sizeOfTrainingDataset = trainingKnowledge.getSizeOfTrainingDataset();
            
            //x is the key here whreas y is the class value
            Double support_xy = ((double)noOfClassLabelForTheKey / (double)sizeOfTrainingDataset);
            Double support_x = ((double)noOfTotalObservationForTheKey / (double)sizeOfTrainingDataset);
            Double support_y = ((double)noOfTotalClassLabelObserved/(double)sizeOfTrainingDataset);
            
            Double p_magnitude = (support_xy/(support_x * support_y));
            
            if(p_magnitude.isNaN())
                p_magnitude = 0.0;
            
            setMagnituge(p_magnitude);
        }catch(Exception ex){
            setMagnituge(0.0);
        }
    }
    
    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Double getMagnituge() {
        return magnituge;
    }

    public void setMagnituge(Double magnituge) {
        this.magnituge = magnituge;
    }

    @Override
    public String toString() {
        return "ElementaryForce{" + "key=" + key + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.key);
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
        final ElementaryForce other = (ElementaryForce) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }
    
}
