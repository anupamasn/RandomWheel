/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.utils;

import in.ac.iitkgp.machinelearning.data.Observation;
import in.ac.iitkgp.machinelearning.data.Predictor;
import in.ac.iitkgp.machinelearning.randomwheel.components.Factor;
import in.ac.iitkgp.machinelearning.randomwheel.components.Key;
import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Anupam
 */
public class StatUtil {
    
    public static TreeMap<Key, TreeMap<String, Integer>> getClassLabelCountForKeys(
            ArrayList<Key> keyList
            , TreeSet<String> classLabels
            , ArrayList<Observation> trainSet
            , ArrayList<Predictor> predictors
            , int depth){
        TreeMap<Key, TreeMap<String, Integer>> classLabelCountsForKeys = new TreeMap<Key, TreeMap<String, Integer>>();
        //initialising to all zero element array
        for(Key p_jpKeys : keyList){
            TreeMap<String, Integer> z_map = new TreeMap<String, Integer>();
            for(String classLabel : classLabels)
                z_map.put(classLabel, 0);
            classLabelCountsForKeys.put(p_jpKeys, z_map);
        }
        
        for(int i = 0; i < trainSet.size(); i++){
            //obtaining applicable keylist for an observed input
            ArrayList<Key> applicableKeyList = CommonUtil.getApplicableKeysFromAnObservation(predictors, trainSet.get(i).getObservedInput(), depth);
            //incrementing the class value counts
            String classLabel = trainSet.get(i).getObservedOutput();
            for(Key p_key : applicableKeyList){
                Integer p_count = classLabelCountsForKeys.get(p_key).get(classLabel);
                p_count = p_count + 1;
                classLabelCountsForKeys.get(p_key).put(classLabel, p_count);
            }
        }
        
        return classLabelCountsForKeys;
    }
    
    public static TreeMap<String, Integer> getClassLabelCounts(ArrayList<Observation> trainSet, TreeSet<String> classLabels){
        TreeMap<String, Integer> classLabelCounts = new TreeMap<String, Integer>();
        for(int i = 0; i < trainSet.size(); i++){
            String classLabel = trainSet.get(i).getObservedOutput();
            Integer p_classLabelCount = (classLabelCounts.get(classLabel) == null ? 0 : classLabelCounts.get(classLabel));
            classLabelCounts.put(classLabel, (p_classLabelCount + 1));
        }
        return classLabelCounts;
    }
    
    public static void measureKeyWeightage(ArrayList<Key> keys, TreeMap<Key, TreeMap<String, Integer>> classLabelCountsForKeys){
        for(Key key : keys){
            TreeMap<String, Integer> countMap = classLabelCountsForKeys.get(key);
            Integer[] p_vals = new Integer[countMap.size()];
            int j = 0;
            for (Map.Entry<String, Integer> entry : countMap.entrySet()){
                p_vals[j++] = entry.getValue();
            }
            
            Double scaledGiniCoeff = StatUtil.getScaledGiniCoeff(p_vals);
            key.setWeightage(scaledGiniCoeff);
        }
    }
    
    public static TreeMap<Factor, Double> measureFactorImportance(
              ArrayList<Factor> allApplicableFactors
            , TreeMap<String, TreeSet<Object>> allPredictorValues
            , TreeMap<Key, TreeMap<String, Integer>> classLabelCountForAllKeys
            , TreeMap<String, Integer> classLabelCounts){
        
        TreeMap<Factor, Double> factorImportanceMap = new TreeMap<Factor, Double>();
        
        Integer[] classLabelCountsArr = new Integer[classLabelCounts.size()];
        int cnt = 0;
        for (Map.Entry<String, Integer> entry : classLabelCounts.entrySet())  
            classLabelCountsArr[cnt++] = classLabelCounts.get(entry.getKey());
        Double classGiniCoeff = getGiniCoeff(classLabelCountsArr);
        //System.out.println("classGiniCoeff: " + classGiniCoeff);
        
        for(Factor factor : allApplicableFactors){
            ArrayList<Key> allApplicableKeysOfTheFactor = CommonUtil.getApplicableKeysFromAFactor(factor, allPredictorValues);
            ArrayList<Integer[]> classLabelCountArrOfTheFactor = new ArrayList<Integer[]>();
            for(Key p_key : allApplicableKeysOfTheFactor){
                TreeMap<String, Integer> p_distmap = classLabelCountForAllKeys.get(p_key);
                Integer[] t_arr = new Integer[p_distmap.size()];
                int index = 0;
                for(Map.Entry<String, Integer> entry : p_distmap.entrySet()){
                    t_arr[index++] = entry.getValue();
                }
                classLabelCountArrOfTheFactor.add(t_arr);
            }
            Double meanDecreaseGiniCoeff = getMeanDecreaseGiniCoeff(classLabelCountArrOfTheFactor, classGiniCoeff);
            factorImportanceMap.put(factor, meanDecreaseGiniCoeff);
        }
        return factorImportanceMap;
    }
    
    public static Double getScaledGiniCoeff(Integer[] values){
        Double[] d_values = new Double[values.length];
        for(int i = 0; i < values.length; i++)
            d_values[i] = (double)values[i];
        return getScaledGiniCoeff(d_values);
    }
    
    public static Double getScaledGiniCoeff(Double[] values){
        Double scaledGiniCoeff = 0.0;
        Integer n = values.length;
        Double sum = 0.0;
        for(int i = 0; i < values.length; i++)
            sum += values[i];
        if(sum == 0){
            return 0.0;
        }
        else{
            Double giniCoeff = getGiniCoeff(values);
            scaledGiniCoeff = Math.abs((1.0-(1.0/(double)n)) - giniCoeff) * (((double)n)/((double)(n - 1)));
            return scaledGiniCoeff;
        }
    }
    
    public static Double getGiniCoeff(Integer[] values){
        Double[] d_values = new Double[values.length];
        for(int i = 0; i < values.length; i++)
            d_values[i] = (double)values[i];
        return getGiniCoeff(d_values);
    }
    
    public static Double getGiniCoeff(Double[] values){
        Double totalCount = 0.0;
        Integer n = values.length;
        for(int i = 0; i < n; i++)
            totalCount += values[i];
        
        Double giniCoeff = 0.0;
        for(int i = 0; i < n; i++){
            Double p_count = values[i];
            if(!(p_count == 0))
                giniCoeff += Math.pow(((double)p_count/(double)totalCount), 2);
        }
        giniCoeff = (1 - giniCoeff);
        return giniCoeff;
    }
    
    public static Double getMeanDecreaseGiniCoeff(ArrayList<Integer[]> attrClassDistList, Double classGiniCoeff){
        Double meanDecreaseGiniCoeff = 0.0;
        Integer totCount = 0;
        for(int i = 0; i < attrClassDistList.size(); i++){
            Integer[] p_dist = attrClassDistList.get(i);
            Integer sum = getSum(p_dist);
            Double giniCoeffComp = getGiniCoeff(p_dist);
            totCount += sum;
            meanDecreaseGiniCoeff += (sum * giniCoeffComp);
        }
        meanDecreaseGiniCoeff = (classGiniCoeff - (meanDecreaseGiniCoeff / (double) totCount));
        return meanDecreaseGiniCoeff;
    }
    
    public static Integer getSum(Integer[] arr){
        Integer sum = 0;
        for(int i = 0; i < arr.length; i++)
            sum += arr[i];
        return sum;
    }
    
    public static Integer getSum(TreeMap<String, Integer> classLabelCounts){
        Integer sum = 0;
        for(Map.Entry<String, Integer> entry : classLabelCounts.entrySet()){
            sum += entry.getValue();
        }
        return sum;
    }
}
