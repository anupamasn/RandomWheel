/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.data;

import java.util.Objects;
import java.util.TreeSet;

/**
 *
 * @author Anupam
 */
public class Predictor<T> implements Comparable {
    
    String predictorName;
    Class<T> predictorType;
    TreeSet<T> allPredictorValues;
    
    public Predictor(String predictorName, Class<T> predictorType) {
        this.predictorName = predictorName;
        this.predictorType = predictorType;
    }
    
    public String getPredictorName() {
        return predictorName;
    }

    public void setPredictorName(String predictorName) {
        this.predictorName = predictorName;
    }

    public TreeSet<T> getAllPredictorValues() {
        return allPredictorValues;
    }

    public void setAllPredictorValues(TreeSet<T> allPredictorValues) {
        this.allPredictorValues = allPredictorValues;
    }
    
    public Class<T> getPredictorType() {
        return predictorType;
    }

    public void setPredictorType(Class<T> predictorType) {
        this.predictorType = predictorType;
    }
    
    public void addNewPredictorValue(T obj){
        if(getAllPredictorValues() == null)
            setAllPredictorValues(new TreeSet<T>());
        if(obj != null)
            getAllPredictorValues().add(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.predictorName);
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
        final Predictor<?> other = (Predictor<?>) obj;
        if (!Objects.equals(this.predictorName, other.predictorName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Predictor{" + "predictorName=" + predictorName + '}';
    }
    
    @Override
    public int compareTo(Object o) {
        Predictor other = (Predictor) o;
        return this.predictorName.compareTo(other.predictorName);
    }
    
    
    
}
