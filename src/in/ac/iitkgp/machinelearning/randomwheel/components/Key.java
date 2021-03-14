/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.components;

import in.ac.iitkgp.machinelearning.data.Predictor;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.util.Pair;

/**
 *
 * @author Anupam
 */
public class Key implements Comparable {
       
    TreeSet<KeyItem>  key;
    Double weightage;

    public Key(TreeSet<KeyItem> key) {
        this.key = key;
    }

    public TreeSet<KeyItem> getKey() {
        return key;
    }

    public void setKey(TreeSet<KeyItem> key) {
        this.key = key;
    }

    public Double getWeightage() {
        return weightage;
    }

    public void setWeightage(Double weightage) {
        this.weightage = weightage;
    }
    
    public void addKeyItem(KeyItem keyItem) {
        if(getKey() == null)
            setKey(new TreeSet<KeyItem>());
        getKey().add(keyItem);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.toString());
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
        final Key other = (Key) obj;
        if (!Objects.equals(this.key.toString(), other.key.toString())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retStr = "";
        for(KeyItem keyItem : key){
            retStr += ((!retStr.equals("")?",":"") + keyItem.getPredictor().getPredictorName() + ":" + keyItem.getValue().toString());
        }
        return retStr;
    }

    @Override
    public int compareTo(Object o) {
        Key other = (Key) o;
        return this.toString().compareTo(other.toString());
    }
    
}
