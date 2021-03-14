/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.components;

import in.ac.iitkgp.machinelearning.data.Predictor;
import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author Anupam
 */
public class KeyItem implements Comparable {
    Predictor predictor;
    Object value;

    public KeyItem(Predictor predictor, Object value) {
        this.predictor = predictor;
        this.value = value;
    }
    
    public Predictor getPredictor() {
        return predictor;
    }

    public void setPredictor(Predictor predictor) {
        this.predictor = predictor;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.predictor);
        hash = 53 * hash + Objects.hashCode(this.value);
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
        final KeyItem other = (KeyItem) obj;
        if (!Objects.equals(this.predictor, other.predictor)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(Object o) {
        KeyItem other = (KeyItem) o;
        //return this.predictor.compareTo(other.predictor);
        int i = this.getPredictor().compareTo(other.getPredictor());
        if (i != 0) return i;
        
        return this.getValue().toString().compareTo(other.getValue().toString());
    }
}
