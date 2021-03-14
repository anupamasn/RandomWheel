/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.components;

import in.ac.iitkgp.machinelearning.data.Predictor;
import java.util.Objects;
import java.util.TreeSet;

/**
 *
 * @author Anupam
 */
public class Factor implements Comparable {
    
    TreeSet<Predictor> predictors;
    Double importance;
    
    public Factor(TreeSet<Predictor> predictors) {
        this.predictors = predictors;
    }

    public Factor() {
    }

    public void addPredictor(Predictor predictor){
        if(getPredictors() == null)
            setPredictors(new TreeSet<Predictor>());
        getPredictors().add(predictor);
    }
    
    public TreeSet<Predictor> getPredictors() {
        return predictors;
    }

    public void setPredictors(TreeSet<Predictor> predictors) {
        this.predictors = predictors;
    }

    public Double getImportance() {
        return importance;
    }

    public void setImportance(Double importance) {
        this.importance = importance;
    }
    
    @Override
    public String toString() {
        String retStr = "";
        for(Predictor p : predictors){
            retStr += ((!retStr.equals("")?",":"") + p.getPredictorName());
        }
        return retStr;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.toString());
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
        final Factor other = (Factor) obj;
        if (!Objects.equals(this.predictors, other.predictors)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        Factor other = (Factor) o;
        return this.toString().compareTo(other.toString());
    }
    
    
    
}
