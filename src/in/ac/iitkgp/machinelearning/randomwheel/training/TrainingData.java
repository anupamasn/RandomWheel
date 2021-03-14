/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.training;

import in.ac.iitkgp.machinelearning.randomwheel.components.Key;
import in.ac.iitkgp.machinelearning.randomwheel.components.Factor;
import in.ac.iitkgp.machinelearning.data.Observation;
import in.ac.iitkgp.machinelearning.data.Predictor;
import in.ac.iitkgp.machinelearning.randomwheel.components.KeyItem;
import in.ac.iitkgp.machinelearning.utils.CombinationUtil;
import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author Anupam
 */
public final class TrainingData {
    
    ArrayList<Predictor> predictors = null;
    ArrayList<Observation> dataset = null;
    TreeMap<String, TreeSet<Object>> allPredictorValues = null;
    TreeSet<String> classLabels = null;
    
    public TrainingData(ArrayList<Observation> trainingDataset, ArrayList<Predictor> predictors, int depth) {
        setDataset(trainingDataset);
        setPredictors(predictors);
        
        setAllPredictorValues(getUniquePredictorValues());
        setClassLabels(getAllClassLabels());
    }

    public ArrayList<Predictor> getPredictors(){
        return this.predictors;
    }
    
    public void setPredictors(ArrayList<Predictor> predictors) {
        this.predictors = predictors;
    }
    
    public ArrayList<Observation> getDataset(){
        return this.dataset;
    }
    
    public void setDataset(ArrayList<Observation> dataset) {
        this.dataset = dataset;
    }

    public TreeMap<String, TreeSet<Object>> getAllPredictorValues() {
        return allPredictorValues;
    }

    public void setAllPredictorValues(TreeMap<String, TreeSet<Object>> allPredictorValues) {
        this.allPredictorValues = allPredictorValues;
    }

    public TreeSet<String> getClassLabels() {
        return classLabels;
    }

    public void setClassLabels(TreeSet<String> classLabels) {
        this.classLabels = classLabels;
    }
    
    private TreeMap<String, TreeSet<Object>> getUniquePredictorValues(){
        TreeMap<String, TreeSet<Object>> possibleValues = new TreeMap<String, TreeSet<Object>>();
        ArrayList<Observation> trainingDataset = getDataset();
        for(int i = 0; i < trainingDataset.size(); i++){
            Observation obs = trainingDataset.get(i);
            TreeMap<Predictor, Object> observedInput = obs.getObservedInput();
            
            for (Map.Entry<Predictor, Object> entry : observedInput.entrySet()){
                String predictorName = entry.getKey().getPredictorName();
                Object predictorValue = entry.getValue();
                if(possibleValues.get(predictorName) == null)
                    possibleValues.put(predictorName, new TreeSet<Object>());
                if(predictorValue != null)
                    possibleValues.get(predictorName).add(predictorValue);
            }
        }
        
        return possibleValues;
    }
    
    private TreeSet<String> getAllClassLabels(){
        TreeSet<String> possibleClassLabels = new TreeSet<String>();
        ArrayList<Observation> trainingDataset = getDataset();
        for(Observation obs : trainingDataset)
            possibleClassLabels.add(obs.getObservedOutput());
        return possibleClassLabels;
    }
    
    
}
