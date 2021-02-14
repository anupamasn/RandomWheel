/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Anupam
 */
public class EvaluationUtil {
    public Integer[][] buildConfusionMatrix(Object[][] prediction, String[] possibleClassValues){
        Integer[][] confusionMatrix = new Integer[possibleClassValues.length][possibleClassValues.length];
        Double[][] predConfMatrix = new Double[possibleClassValues.length][possibleClassValues.length];
        Map<String, Integer> indexMap = new HashMap<String, Integer>();
        for(int i=0; i<possibleClassValues.length; i++){
            indexMap.put(possibleClassValues[i], i);
            for(int j=0; j<possibleClassValues.length; j++){
                confusionMatrix[i][j] = 0;
                predConfMatrix[i][j] = 0.0;
            }
        }
        //System.out.println("indexMap: " + indexMap);
        for(int i=0; i<prediction.length; i++){
            Integer actualValIndex = indexMap.get((String)prediction[i][0]);
            Integer predictedValIndex = indexMap.get((String)prediction[i][1]);
            Double predictionConfidence = (Double)prediction[i][2];
            //try{
            //System.out.println((String)prediction[i][0] + ", " + (String)prediction[i][1] + ", " + predictionConfidence);
            confusionMatrix[actualValIndex][predictedValIndex] = confusionMatrix[actualValIndex][predictedValIndex] + 1;
            predConfMatrix[actualValIndex][predictedValIndex] = predConfMatrix[actualValIndex][predictedValIndex] + predictionConfidence;
            //}catch(Exception ex){}
        }
        
        //calculating average confidence
        for(int i=0; i<possibleClassValues.length; i++){
            for(int j=0; j<possibleClassValues.length; j++){
                predConfMatrix[i][j] = (predConfMatrix[i][j]/(double)confusionMatrix[i][j]);
            }
        }
        
        System.out.print("\n\nConfusion Matrix\n----------------------\n");
        for(int i=0; i<possibleClassValues.length; i++)
            System.out.print((i+1)+ "> " + possibleClassValues[i]+", ");
        System.out.println("\n");
        for(int i=0; i<possibleClassValues.length; i++)
            System.out.print("\t"+(i+1));
        System.out.print("\t <-- Predicted As \n");
        for(int i=0; i<possibleClassValues.length; i++){
            System.out.print((i+1) + ">\t");
            for(int j=0; j<possibleClassValues.length; j++){
                System.out.print(confusionMatrix[i][j] + "("+Math.round(predConfMatrix[i][j] * 100)+"%)" + "\t");
            }
            System.out.print("\n");
        }
        
        return confusionMatrix;
    }
    
    public double getOverallAccuracy(Integer[][] confusionMatrix){
        Integer correctCount = 0;
        Integer totalCount = 0;
        for(int i=0; i<confusionMatrix.length; i++){
            for(int j=0; j<confusionMatrix.length; j++){
                if(i==j)
                    correctCount += confusionMatrix[i][j];
                totalCount += confusionMatrix[i][j];
            }
        }
        double overallAccuracy = ((double)correctCount/(double)totalCount);
        System.out.println("\nOverall accuracy: " + overallAccuracy);
        return overallAccuracy;
    }
    
    public Map<String, Double> getClassAccuracies(Integer[][] confusionMatrix, String[] possibleClassValues){
        Map<String, Double> classAccuracies = new HashMap<String, Double>();
        for(int i=0; i<confusionMatrix.length; i++){
            Integer correctCount = 0;
            Integer totalCountInClass = 0;
            for(int j=0; j<confusionMatrix.length; j++){
                if(i==j)
                    correctCount += confusionMatrix[i][j];
                totalCountInClass += confusionMatrix[i][j];
            }
            double classAccuracy = ((double)correctCount/(double)totalCountInClass);
            classAccuracies.put(possibleClassValues[i], classAccuracy);
        }
        System.out.println("\nClass accuracies: " + classAccuracies);
        return classAccuracies;
    }
}
