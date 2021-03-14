/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.utils;

import in.ac.iitkgp.machinelearning.data.Observation;
import in.ac.iitkgp.machinelearning.data.Predictor;
import in.ac.iitkgp.machinelearning.data.UnknownObservation;
import in.ac.iitkgp.machinelearning.randomwheel.components.Factor;
import in.ac.iitkgp.machinelearning.randomwheel.components.Key;
import in.ac.iitkgp.machinelearning.randomwheel.components.KeyItem;
import in.ac.iitkgp.machinelearning.randomwheel.components.Wheel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 *
 * @author Anupam
 */
public class CommonUtil {
    
    public static String removeEscapeCharFromString(String line, String excapeChar, String splitChar){
        line = line.replaceAll((excapeChar+","), splitChar);
        line = line.replaceAll((","+excapeChar), splitChar);
        line = line.startsWith(excapeChar)?line.substring(1):line;
        line = line.endsWith(excapeChar)?line.substring(0,line.length() - 1):line;
        return line;
    }
    
    public static String[] prepareArray(String line, HashSet<Integer> discardedIndexes, String csvSplitBy, int totalNoOfCols){
        String[] tokens = line.split(csvSplitBy);
        String[] arr = new String[totalNoOfCols - discardedIndexes.size()];
        int j=0;
        for(int i = 0; i < tokens.length; i++){
            if(!(discardedIndexes.contains(i))){
                arr[j] = tokens[i];
                j++;
            }
        }
        return arr;
    }
    
    public static ArrayList<Factor> getAllApplicableFactors(ArrayList<Predictor> predictors, int depth){
        if(depth > (predictors.size())){
            System.out.println("Invalid depth!!!");
            return null;
        }
        
        ArrayList<Factor> factors = new ArrayList<Factor>();
        String[] attHeaders = new String[predictors.size()];
        //making a attribute header list after discarding class
        int ahIndex = 0;
        for(int i=0; i<predictors.size(); i++)
        {
            attHeaders[ahIndex] = predictors.get(i).getPredictorName();
            ahIndex++;
        }
        
        for(int level = 1; level <= depth; level++){
            ArrayList<String[]> attrCombs = CombinationUtil.getCombinationsFromAnArray(attHeaders, level);
            for(int i = 0; i < attrCombs.size(); i++){
                String[] p_attrComb = attrCombs.get(i);
                Factor p_factor = new Factor();
                for(int j = 0; j < p_attrComb.length; j++){
                    final String t_predictorName = p_attrComb[j];
                    List<Predictor> predList = predictors
                                                    .stream()
                                                    .filter(p -> p.getPredictorName().equals((t_predictorName)))
                                                    .collect(Collectors.toList());
                    Predictor pred = predList.get(0);
                    p_factor.addPredictor(pred);
                }
                factors.add(p_factor);
            }
        }
        return factors;
    }
    
    public static ArrayList<Key> getAllApplicableKeys(ArrayList<Factor> factors, TreeMap<String, TreeSet<Object>> allPredictorValues)
    {
        ArrayList allKeyList = new ArrayList<Key>();
        
        for(int i = 0; i < factors.size(); i++){
            Factor p_factor = factors.get(i);
            ArrayList<Key> applicableKeysForAFactor = CommonUtil.getApplicableKeysFromAFactor(p_factor, allPredictorValues);
            allKeyList.addAll(applicableKeysForAFactor);
        }
        
        return allKeyList;
    }
    
    public static ArrayList<Key> getApplicableKeysFromAnObservation(ArrayList<Predictor> predictors, TreeMap<Predictor, Object> observedInput, int depth){
        //getting array with elements as header:value
        ArrayList<String> transactionAsAL = new ArrayList<String>();
        for(int j = 0; j < predictors.size(); j++){
            if(observedInput.get(predictors.get(j)) != null){
                transactionAsAL.add((predictors.get(j).getPredictorName() + ":" + (String)observedInput.get(predictors.get(j))));
            }
        }
        String[] transactionAsArray = new String[transactionAsAL.size()];
        for(int j = 0; j < transactionAsAL.size(); j++)
            transactionAsArray[j] = transactionAsAL.get(j);

        //getting all applicable jpKeys
        ArrayList<String[]> allAttrAndValCombs = new ArrayList<String[]>();
        for(int level = 1; level <= depth; level++)
            allAttrAndValCombs.addAll(CombinationUtil.getCombinationsFromAnArray(transactionAsArray, level));

        ArrayList<Key> keyList = new ArrayList<Key>();
        for(int j = 0; j < allAttrAndValCombs.size(); j++){
            //String p_jpKey = "";
            TreeSet<KeyItem> key = new TreeSet<KeyItem>();
            String[] p_arr = allAttrAndValCombs.get(j);
            for(int k = 0; k < p_arr.length; k++){
                //p_jpKey += ((k==0?"":",") + p_arr[k]);
                //applicableJpKeysHS.add(p_jpKey);
                StringTokenizer st = new StringTokenizer(p_arr[k], ":");
                String predictorName = st.nextToken();
                Predictor p = CommonUtil.getPredictorByName(predictors, predictorName);
                Object value = st.nextToken();
                KeyItem keyItem = new KeyItem(p, value);
                key.add(keyItem);
            }
            Key keyObj = new Key(key);
            keyList.add(keyObj);
        }
        return keyList;
    }
    
    public static ArrayList<Key> getApplicableKeysFromAFactor(Factor factor, TreeMap<String, TreeSet<Object>> allPredictorValues)
    {
        ArrayList<Key> keyList = new ArrayList<Key>();
        
        ArrayList<Predictor> predictorList = new ArrayList<Predictor>();
        String[] attrComb = new String[factor.getPredictors().size()];
        int index = 0;
        for(Predictor p : factor.getPredictors()){
            attrComb[index++] = p.getPredictorName();
            predictorList.add(p);
        }
        
        List<List<String>> p_valComb = new ArrayList<List<String>>();
        //for each attribute in a combination
        for(int j=0; j < attrComb.length; j++){
            TreeSet<Object> possibleValues = allPredictorValues.get(attrComb[j]);
            ArrayList<String> valList = new ArrayList<String>();
            //for each values of the attribute
            for(Object obj : possibleValues)
                valList.add((String)obj);
            p_valComb.add(valList);
        }
        Set<List<String>> valComb = CombinationUtil.getCombinationsFromMultipleListsWithOneValEach(p_valComb);
        Iterator valCombIt = valComb.iterator();
        while(valCombIt.hasNext()){
            Object[] p_val = ((ArrayList)valCombIt.next()).toArray();
            TreeSet<KeyItem> key = new TreeSet<KeyItem>();
            for(int k = 0; k < p_val.length; k++){
                KeyItem keyItem = new KeyItem(CommonUtil.getPredictorByName(predictorList, attrComb[k]), p_val[k]);
                key.add(keyItem);
            }
            Key keyObj = new Key(key);
            keyList.add(keyObj);
        }
        return keyList;
    }
    
    public static ArrayList<Key> getKeyFromAllFactors(ArrayList<Factor> factors, UnknownObservation obs, ArrayList<Key> allKeys){
        ArrayList<Key> keys = new ArrayList<Key>();
        for(Factor p_factor : factors){
            Key key = getKeyFromFactor(p_factor, obs, allKeys);
            if(key != null)
                keys.add(key);
        }
        return keys;
    }
    
    public static Key getKeyFromFactor(Factor factor, UnknownObservation obs, ArrayList<Key> allKeys)
    {
        TreeSet<KeyItem> keyItemSet = new TreeSet<KeyItem>();
        for(Predictor predictor : factor.getPredictors()){
            Object predictorValue = obs.getObservedInput().get(predictor);
            if(predictorValue == null)
                return null;
            KeyItem keyItem = new KeyItem(predictor, predictorValue);
            keyItemSet.add(keyItem);
        }
        Key searchKey = new Key(keyItemSet);
        
        String searchKeyStr = searchKey.toString();
        for(Key p_key : allKeys){
            if(p_key.toString().equals(searchKeyStr))
                return p_key;
        }
        return null;
    }
    
    public static ArrayList<Factor> sortFactorsOnImportance(Map<Factor, Double> map){
        ArrayList<Factor> sortedList = new ArrayList<Factor>();
        Set<Map.Entry<Factor, Double>> set = map.entrySet();
        List<Map.Entry<Factor, Double>> list = new ArrayList<Map.Entry<Factor, Double>>(set);
        Collections.sort(list, new Comparator<Map.Entry<Factor, Double>>() {
            public int compare(Map.Entry<Factor, Double> o1, Map.Entry<Factor, Double> o2) {
                Double o1val = (o1.getValue() == null ? -1.0: o1.getValue());
                Double o2val = (o2.getValue() == null ? -1.0: o2.getValue());
                return o2val.compareTo(o1val);
            }
        });
        
        for(int i = 0; i < list.size(); i++){
            Map.Entry<Factor, Double> entry = list.get(i);
            Factor p_factor = entry.getKey();
            sortedList.add(p_factor);
        }
        return sortedList;
    }
    
    public static ArrayList<Pair<String, Double>> decideWinnerAndRunnerUpWheel(ArrayList<TreeMap<String, Wheel>> wheelsInTrials, TreeSet<String> classLabels){
        //measuring the cumulative angular velocity
        TreeMap<String, Double> cummulativeAngularVelocity = new TreeMap<String, Double>();
        for(String classLabel : classLabels)
            cummulativeAngularVelocity.put(classLabel, 0.0);
        
        for(TreeMap<String, Wheel> p_trialResult : wheelsInTrials){
            for(String classLabel : classLabels){
                Double p_cummAngVel = cummulativeAngularVelocity.get(classLabel);
                p_cummAngVel += p_trialResult.get(classLabel).getAngularVelocity();
                cummulativeAngularVelocity.put(classLabel, p_cummAngVel);
            }
        }
        
        //sorting the class labels in descending order of their cumulative angular velocity
        Set<Map.Entry<String, Double>> set = cummulativeAngularVelocity.entrySet();
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                Double o1val = (o1.getValue() == null ? -1.0: o1.getValue());
                Double o2val = (o2.getValue() == null ? -1.0: o2.getValue());
                return o2val.compareTo(o1val);
            }
        });
        
        //preparing the return list 
        ArrayList<Pair<String, Double>> classLabelsInDescendingOderOfCummVelocity = new ArrayList<Pair<String, Double>>();
        for(Map.Entry<String, Double> entry : list)
            classLabelsInDescendingOderOfCummVelocity.add(new Pair<String, Double>(entry.getKey(), entry.getValue()));
        
        return classLabelsInDescendingOderOfCummVelocity;
    }
    
    public static ArrayList<Factor> getBestFactors(ArrayList<Factor> factorListSortedByImportance, Integer no_of_factors){
        ArrayList<Factor> bestFactorList = new ArrayList<Factor>();
        for(int i = 0; i < no_of_factors; i++)
            bestFactorList.add(factorListSortedByImportance.get(i));
        return bestFactorList;
    }
    
    public static int getRandomInteger(int min, int max){
        int random = (min + (int)(Math.random() * ((max - min) + 1)));
        return random;
    }
    
    private static Predictor getPredictorByName(ArrayList<Predictor> predictors, String p_predictorName){
        for(int i = 0; i < predictors.size(); i++){
            if(predictors.get(i).getPredictorName().equals(p_predictorName))
                return predictors.get(i);
        }
        return null;
    }
    
}
