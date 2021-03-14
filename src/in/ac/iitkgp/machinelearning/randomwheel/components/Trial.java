/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.components;

import in.ac.iitkgp.machinelearning.data.UnknownObservation;
import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingKnowledge;
import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import in.ac.iitkgp.machinelearning.utils.StatUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;

/**
 *
 * @author Anupam
 */
public class Trial implements Callable<TreeMap<String, Wheel>> {
    TreeSet<String> classLabels;
    TrainingKnowledge trainingKnowledge;
    UnknownObservation obs;
    Double noiseFraction;

    public Trial(TreeSet<String> classLabels, TrainingKnowledge trainingKnowledge, UnknownObservation obs, Double noiseFraction) {
        this.classLabels = classLabels;
        this.trainingKnowledge = trainingKnowledge;
        this.obs = obs;
        this.noiseFraction = noiseFraction;
    }
    

    @Override
    public TreeMap<String, Wheel> call() { 
        TreeMap<String, Wheel> output = perform();
        return output;
    }
    
    private TreeMap<String, Wheel> perform(){
        //creating the wheels
        TreeMap<String, Wheel> wheels = new TreeMap<String, Wheel>();
        for(String p_classLabel : classLabels){
            Wheel p_wheel = new Wheel(p_classLabel);
            wheels.put(p_classLabel, p_wheel);
        }
        
        //getting number of factors to be considered in this trial
        Integer noOfBestFactors = CommonUtil.getRandomInteger(1, (int)(trainingKnowledge.getApplicableFactorsSortedByImportance().size() * (1 - noiseFraction)));
        ArrayList<Factor> bestFactors = CommonUtil.getBestFactors(trainingKnowledge.getApplicableFactorsSortedByImportance(), noOfBestFactors);
        
        //getting a key for each individual factor
        ArrayList<Key> keys = CommonUtil.getKeyFromAllFactors(bestFactors, obs, trainingKnowledge.getApplicableKeys());
        
        //rotate all wheels
        for(Map.Entry<String, Wheel> entry : wheels.entrySet()){
            Wheel p_wheel = entry.getValue();
            p_wheel.rotate(keys, trainingKnowledge);
        }
        return wheels;
    }
    
}
