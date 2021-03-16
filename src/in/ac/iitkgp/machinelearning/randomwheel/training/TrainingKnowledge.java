/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.training;

import in.ac.iitkgp.machinelearning.randomwheel.components.Factor;
import in.ac.iitkgp.machinelearning.randomwheel.components.Key;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author Anupam
 */
public class TrainingKnowledge {
    
    //count of each class label
    TreeMap<String, Integer> classLabelCounts;
    //distribution of counts for each class label given a certain key 
    TreeMap<Key, TreeMap<String, Integer>> classLabelCountForKeys;
    //size of training dataset
    Integer sizeOfTrainingDataset;
    
    //List of factors in descending order of importance
    ArrayList<Factor> applicableFactorsSortedByImportance;
    //list of all keys
    ArrayList<Key> applicableKeys = null;
    
    public TreeMap<String, Integer> getClassLabelCounts() {
        return classLabelCounts;
    }

    public void setClassLabelCounts(TreeMap<String, Integer> classLabelCounts) {
        this.classLabelCounts = classLabelCounts;
    }

    public TreeMap<Key, TreeMap<String, Integer>> getClassLabelCountForKeys() {
        return classLabelCountForKeys;
    }

    public void setClassLabelCountForKeys(TreeMap<Key, TreeMap<String, Integer>> classLabelCountForKeys) {
        this.classLabelCountForKeys = classLabelCountForKeys;
    }

    public Integer getSizeOfTrainingDataset() {
        return sizeOfTrainingDataset;
    }

    public void setSizeOfTrainingDataset(Integer sizeOfTrainingDataset) {
        this.sizeOfTrainingDataset = sizeOfTrainingDataset;
    }

    public ArrayList<Factor> getApplicableFactorsSortedByImportance() {
        return applicableFactorsSortedByImportance;
    }

    public void setApplicableFactorsSortedByImportance(ArrayList<Factor> applicableFactorsSortedByImportance) {
        this.applicableFactorsSortedByImportance = applicableFactorsSortedByImportance;
    }

    public ArrayList<Key> getApplicableKeys() {
        return applicableKeys;
    }

    public void setApplicableKeys(ArrayList<Key> applicableKeys) {
        this.applicableKeys = applicableKeys;
    }
    
}
