/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.evaluation;

import in.ac.iitkgp.machinelearning.evaluation.ConfusionMatrix;
import in.ac.iitkgp.machinelearning.randomwheel.classification.Prediction;
import java.util.List;

/**
 *
 * @author Anupam
 */
public class ClassificationAssessmentArbitrator {
    
    public static void evaluate(List<Prediction> predictions){
        System.out.println("");
        System.out.println("==============================================");
        System.out.println("Efficiency Evaluation");
        System.out.println("==============================================");
        ConfusionMatrix cm = new ConfusionMatrix();
        for(int i = 0; i < predictions.size(); i++){
            cm.increaseValue(predictions.get(i).getGoldClass(), predictions.get(i).getPredictedClass(), 1);
        }
        System.out.println(cm);
        System.out.println(cm.printLabelPrecRecFm());
        System.out.println("Wt. Precision: " + cm.getWeightedPrecision());
        System.out.println("Wt. Recall: " + cm.getWeightedRecall());
        System.out.println("Wt. F-measure: " + cm.getWeightedFMeasure());
        System.out.println("Kappa: " + cm.getCohensKappa());
        
        System.out.println("");
        
        Double cum_correct_confidence = 0.0;
        Double cum_wrong_confidence = 0.0;
        Integer tot_correct_count = 0;
        Integer tot_wrong_count = 0;
        for(int i = 0; i < predictions.size(); i++){
            String goldClass = predictions.get(i).getGoldClass();
            String predictedClass = predictions.get(i).getPredictedClass();
            if(predictedClass.equals(goldClass)){
                cum_correct_confidence += predictions.get(i).getPredictionConfidence();
                tot_correct_count++;
            }else{
                cum_wrong_confidence += predictions.get(i).getPredictionConfidence();
                tot_wrong_count++;
            }
        }
        
        System.out.println("Avg Correct Confidence: " + ((double)cum_correct_confidence/(double)tot_correct_count));
        System.out.println("Avg Wrong Confidence: " + ((double)cum_wrong_confidence/(double)tot_wrong_count));
        System.out.println("Diff: " + (((double)cum_correct_confidence/(double)tot_correct_count) - ((double)cum_wrong_confidence/(double)tot_wrong_count)));
    }
    
}
