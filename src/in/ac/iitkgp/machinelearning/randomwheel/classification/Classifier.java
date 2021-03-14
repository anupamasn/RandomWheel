/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.classification;

import in.ac.iitkgp.machinelearning.data.DataParameters;
import in.ac.iitkgp.machinelearning.data.Observation;
import in.ac.iitkgp.machinelearning.data.Predictor;
import in.ac.iitkgp.machinelearning.data.UnknownObservation;
import in.ac.iitkgp.machinelearning.randomwheel.components.Factor;
import in.ac.iitkgp.machinelearning.randomwheel.components.Force;
import in.ac.iitkgp.machinelearning.randomwheel.components.Key;
import in.ac.iitkgp.machinelearning.randomwheel.components.Trial;
import in.ac.iitkgp.machinelearning.randomwheel.components.Wheel;
import in.ac.iitkgp.machinelearning.randomwheel.parameters.TuningParameters;
import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingData;
import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingKnowledge;
import in.ac.iitkgp.machinelearning.utils.StatUtil;
import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.util.Pair;

/**
 *
 * @author Anupam
 */
public class Classifier {

    /**
     * @param args the command line arguments
     */
    DataParameters dp = null;
    TuningParameters tp = null;
    
    public DataParameters getDp() {
        return dp;
    }

    private void setDp(DataParameters dp) {
        this.dp = dp;
    }

    public TuningParameters getTp() {
        return tp;
    }

    private void setTp(TuningParameters tp) {
        this.tp = tp;
    }
    
    public Classifier(DataParameters dp, TuningParameters tp) {
        setDp(dp);
        setTp(tp);
    }
    
    public TrainingKnowledge train(TrainingData trainingData, TreeSet<String> classLabels){
        TrainingKnowledge trainingKnowledge = new TrainingKnowledge();
        
        //retriving the applicable factors
        ArrayList<Factor> applicableFactors = CommonUtil.getAllApplicableFactors(trainingData.getPredictors(), getTp().getDepth());
        
        //retriving the applicable keys
        ArrayList<Key> applicableKeys = CommonUtil.getAllApplicableKeys(applicableFactors, trainingData.getAllPredictorValues());
        
        //measuring the class label counts for onward calculation of probabilities 
        ArrayList<Observation> trainSet = trainingData.getDataset();
        TreeMap<String, Integer> classLabelCounts = StatUtil.getClassLabelCounts(trainSet, classLabels);
        TreeMap<Key, TreeMap<String, Integer>> classLabelCountForKeys = StatUtil.getClassLabelCountForKeys(
                applicableKeys, classLabels
                , trainingData.getDataset(), trainingData.getPredictors()
                , getTp().getDepth());
        trainingKnowledge.setClassLabelCounts(classLabelCounts);
        trainingKnowledge.setClassLabelCountForKeys(classLabelCountForKeys);
        trainingKnowledge.setSizeOfTrainingDataset(trainSet.size());
        
        //measuing and updating the factor importance, sorting them and updating the knowledge
        TreeMap<Factor, Double> factorImportanceMap = StatUtil.measureFactorImportance(
                applicableFactors, trainingData.getAllPredictorValues(), 
                classLabelCountForKeys, classLabelCounts);
        for(Map.Entry<Factor, Double> entry : factorImportanceMap.entrySet()){
            entry.getKey().setImportance(entry.getValue());
        }
        ArrayList<Factor> applicableFactorsSortedByImportance = CommonUtil.sortFactorsOnImportance(factorImportanceMap);
        trainingKnowledge.setApplicableFactorsSortedByImportance(applicableFactorsSortedByImportance);
        
        //measuring the weightages for all keys and then updating all key objects
        StatUtil.measureKeyWeightage(applicableKeys, classLabelCountForKeys);
        trainingKnowledge.setApplicableKeys(applicableKeys);
        
        return trainingKnowledge;
    }
    
    public Prediction classify(UnknownObservation uobs, ArrayList<Predictor> predictors, TreeSet<String> classLabels, TrainingKnowledge trainingKnowledge){
        ArrayList<TreeMap<String, Wheel>> wheelsInTrials = new ArrayList<TreeMap<String, Wheel>>();
        
        //=============== Executing the trials in separate threads and accumulating the wheelsInTrials ==============================
        ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<TreeMap<String, Wheel>>> trialResults = new ArrayList<Future<TreeMap<String, Wheel>>>();
        Callable<TreeMap<String, Wheel>> trial = new Trial(classLabels, trainingKnowledge, uobs, getTp().getNoiseFraction());
        try{
            //for each trial
            for(int i = 0; i < getTp().getNoOfTrial(); i++){
                Future<TreeMap<String, Wheel>> future = executor.submit(trial);
                trialResults.add(future);
            }
            //for each trial result
            for(Future<TreeMap<String, Wheel>> p_result : trialResults){
                try {
                    wheelsInTrials.add(p_result.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        executor.shutdown();
        //============================================================================================================================
        
        
        ArrayList<Pair<String, Double>> classLabelsInDescendingOderOfCummVelocity = CommonUtil.decideWinnerAndRunnerUpWheel(wheelsInTrials, classLabels);
        
        String predictedClassLabel = classLabelsInDescendingOderOfCummVelocity.get(0).getKey();
        //String predicted_value = su.getVoteWinnerOnNoOfWinningPass(getPossibleAttributeValues().get(getClassAttIndex()), resultantForceStrengthsInPasses);
        
        Double winner_cumm_velocity = classLabelsInDescendingOderOfCummVelocity.get(0).getValue();
        Double runner_cumm_velocity = classLabelsInDescendingOderOfCummVelocity.get(1).getValue();
        
        Double[] narr = new Double[2];
        narr[0] = winner_cumm_velocity;
        narr[1] = runner_cumm_velocity;
        Double inequality = StatUtil.getScaledGiniCoeff(narr);
        
        if(inequality.isNaN())
            inequality = 0.0;
        
        Prediction prediction = new Prediction(predictedClassLabel, inequality);
        return prediction;
    }
    
}
