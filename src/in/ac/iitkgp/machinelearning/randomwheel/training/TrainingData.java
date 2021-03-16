/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.training;

import in.ac.iitkgp.machinelearning.data.Observation;
import in.ac.iitkgp.machinelearning.data.Predictor;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author Anupam
 */
public final class TrainingData {
    
    TreeSet<Predictor> predictors = null;
    ArrayList<Observation> dataset = null;
    TreeSet<String> classLabels = null;
    
    public TrainingData(ArrayList<Observation> trainingDataset, TreeSet<Predictor> predictors) {
        setDataset(trainingDataset);
        setPredictors(predictors);
        
        setClassLabels(extractAllClassLabels());
        setUniquePredictorValues();
    }
    
    public TreeSet<Predictor> getPredictors(){
        return this.predictors;
    }
    
    public void setPredictors(TreeSet<Predictor> predictors) {
        this.predictors = predictors;
    }
    
    public ArrayList<Observation> getDataset(){
        return this.dataset;
    }
    
    public void setDataset(ArrayList<Observation> dataset) {
        this.dataset = dataset;
    }
    
    public TreeSet<String> getClassLabels() {
        return classLabels;
    }

    public void setClassLabels(TreeSet<String> classLabels) {
        this.classLabels = classLabels;
    }
    
    private void setUniquePredictorValues(){
        for(Observation obs : getDataset()){
            for (Map.Entry<Predictor, Object> entry : obs.getObservedInput().entrySet()){
                Predictor predictor = entry.getKey();
                Object predictorValue = entry.getValue();
                predictor.addNewPredictorValue(predictorValue);
            }
        }
    }
    
    private TreeSet<String> extractAllClassLabels(){
        TreeSet<String> possibleClassLabels = new TreeSet<String>();
        ArrayList<Observation> trainingDataset = getDataset();
        for(Observation obs : trainingDataset)
            possibleClassLabels.add(obs.getObservedOutput());
        return possibleClassLabels;
    }
    
}
