/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author Anupam
 */
public class StatUtil {
    
    public boolean debug = false;

    public StatUtil(boolean debug) {
        setDebug(debug);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public Object[] getClassValueCountsOfKeys(ArrayList<String[]> trainSet, String[] headers, ArrayList<String[]> possibleAttributeValues, int classAttIndex, ArrayList<String> jpKeys, HashSet<String> missingValueStrings, int depth){
        Object[] retObj = new Object[2];
        Map<String, Integer[]> keyClassValueCounts = new HashMap<String, Integer[]>();
        Map<String, Integer> classValueCounts = new HashMap<String, Integer>();
        //initialising to all zero element array
        for(int i = 0; i < jpKeys.size(); i++){
            String p_jpKeys = jpKeys.get(i);
            Integer[] p_classValArr = new Integer[possibleAttributeValues.get(classAttIndex).length];
            for(int j = 0; j < p_classValArr.length; j++)
                p_classValArr[j] = 0;
            keyClassValueCounts.put(p_jpKeys, p_classValArr);
        }
        
        for(int i = 0; i < trainSet.size(); i++){
            HashSet<String> applicableJpKeysHS = StringUtil.getApplicableJpKeysFromATransactions(trainSet.get(i), headers, classAttIndex, missingValueStrings, depth);
            //calculating class value counts
            String classValue = trainSet.get(i)[classAttIndex];
            Iterator applicableJpKeysIt = applicableJpKeysHS.iterator();
            while(applicableJpKeysIt.hasNext()){
                String p_jpKey = (String)applicableJpKeysIt.next();
                //System.out.println("\t"+p_jpKey);
                Integer[] p_classValArr = keyClassValueCounts.get(p_jpKey);
                Integer classValIndex = DataUtil.getIndexOf(possibleAttributeValues.get(classAttIndex), classValue);
                p_classValArr[classValIndex] = p_classValArr[classValIndex] + 1;
                keyClassValueCounts.put(p_jpKey, p_classValArr);
            }
            
            //updating class value counts
            Integer p_classValueCount = (classValueCounts.get(classValue) == null ? 0 : classValueCounts.get(classValue));
            classValueCounts.put(classValue, (p_classValueCount + 1));
        }
            
        retObj[0] = keyClassValueCounts;
        retObj[1] = classValueCounts;
        //System.out.print("\n\n"+uniqueValueCounts);
        
        return retObj;
    }
    
    public ArrayList<Object[]> sortMapOnValue(Map<String, Double> map){
        ArrayList<Object[]> sortedList = new ArrayList<Object[]>();
        Set<Map.Entry<String, Double>> set = map.entrySet();
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                Double o1val = (o1.getValue() == null ? -1.0: o1.getValue());
                Double o2val = (o2.getValue() == null ? -1.0: o2.getValue());
                return o2val.compareTo(o1val);
            }
        });
        
        for(int i = 0; i < list.size(); i++){
            Map.Entry<String, Double> entry = list.get(i);
            String p_key = entry.getKey();
            Double p_value = map.get(p_key);
            //System.out.println("["+i+"]"+p_key);
            Object[] p_obj = new Object[2];
            p_obj[0] = p_key;
            p_obj[1] = p_value;
            sortedList.add(p_obj);
        }
        return sortedList;
    }
    
    public Map<String, Double> getAttributeImportanceMap(String[] headers
            , Integer classAttrIndex
            , ArrayList<String[]> possibleAttributeValues
            , ArrayList<String> jpKeys
            , Map<String, Integer[]> keyClassValueCounts
            , Map<String, Integer> classValueCounts){
        Map<String, Double> attrImpMap = new HashMap<String, Double>();
        Integer[] classValueCountsArr = new Integer[classValueCounts.size()];
        Iterator ks = classValueCounts.keySet().iterator();
        int cnt = 0;
        while(ks.hasNext()){
            classValueCountsArr[cnt++] = classValueCounts.get((String)ks.next());
        }
        
        Double classGiniCoeff = getGiniCoeff(classValueCountsArr);
        //System.out.println("classGiniCoeff: " + classGiniCoeff);
        
        //Getting attribute name combinations from joint probability keys
        Map<String, ArrayList<Integer[]>> attrCombClassDist = new HashMap<String, ArrayList<Integer[]>>();
        for(int i = 0; i < jpKeys.size(); i++){
            String attrCombName = StringUtil.getAttrCombinationFromJPKey(jpKeys.get(i));
            Integer[] p_keyClassValueCounts = keyClassValueCounts.get(jpKeys.get(i));
            //attrCombSet.add(attrCombName);
            //System.out.println("jpKeys: " + jpKeys.get(i));
            //System.out.println("attrCombName: " + attrCombName);
            if(attrCombClassDist.containsKey(attrCombName)){
                ArrayList<Integer[]> al = attrCombClassDist.get(attrCombName);
                al.add(p_keyClassValueCounts);
                attrCombClassDist.put(attrCombName, al);
            }else{
                ArrayList<Integer[]> al = new ArrayList<Integer[]>();
                al.add(p_keyClassValueCounts);
                attrCombClassDist.put(attrCombName, al);
            }
        }
        
        //calculating mean decrease gini coeff for all attribute name combinations
        Map<String, Double> attrCombGiniCoeffMap = new HashMap<String, Double>();
        Iterator ksIt = attrCombClassDist.keySet().iterator();
        while(ksIt.hasNext()){
            String p_key = (String)ksIt.next();
            //System.out.println("attrCombName: " + p_key);
            ArrayList<Integer[]> al = attrCombClassDist.get(p_key);
            //for(int j = 0; j < al.size(); j++){
            //    System.out.print("\t");
            //    for(int k = 0; k < al.get(j).length; k++)
            //        System.out.print(al.get(j)[k]+"|");
            //    System.out.print("\n");
            //}
            Double meanDecreaseGiniCoeff = getMeanDecreaseGiniCoeff(al, classGiniCoeff);
            attrCombGiniCoeffMap.put(p_key, meanDecreaseGiniCoeff);
            //System.out.println("\tmeanDecreaseGiniCoeff : " + meanDecreaseGiniCoeff);
        }
        //System.out.println("attrCombGiniCoeffMap: " + attrCombGiniCoeffMap);
        Map<String, Double> combAttrImpMap = new HashMap<String, Double>();
        for(int i = 0; i < jpKeys.size(); i++){
            String attrCombName = StringUtil.getAttrCombinationFromJPKey(jpKeys.get(i));
            Double mdGiniCoeffOfAttrComp = attrCombGiniCoeffMap.get(attrCombName);
            combAttrImpMap.put(jpKeys.get(i), mdGiniCoeffOfAttrComp);
        }
        
        return combAttrImpMap;
    }
    
    public Map<String, Double> getAttributeImportanceMap1(String[] headers
            , Integer classAttrIndex
            , ArrayList<String[]> possibleAttributeValues
            , ArrayList<String> jpKeys
            , Map<String, Integer[]> keyClassValueCounts
            , Map<String, Integer> classValueCounts){
        
        Map<String, Double> attrImpMap = new HashMap<String, Double>();
        Integer[] classValueCountsArr = new Integer[classValueCounts.size()];
        Iterator ks = classValueCounts.keySet().iterator();
        int cnt = 0;
        while(ks.hasNext()){
            classValueCountsArr[cnt++] = classValueCounts.get((String)ks.next());
        }
        
        Double classGiniCoeff = getGiniCoeff(classValueCountsArr);
        //System.out.println("classGiniCoeff: " + classGiniCoeff);
        
        for(int i = 0; i < headers.length; i++){
            if(i != classAttrIndex){
                String[] possibleValues = possibleAttributeValues.get(i);
                Integer totCount = 0;
                Double attrGiniCoeff = 0.0;
                for(int j =0; j < possibleValues.length; j++){
                    String p_jpKey = headers[i] + ":" + possibleValues[j];
                    Double attrValGiniCoeff = getGiniCoeff(keyClassValueCounts.get(p_jpKey));
                    Integer attrValCount = getSum(keyClassValueCounts.get(p_jpKey));
                    totCount += attrValCount;
                    attrGiniCoeff += ((double)attrValCount * (double)attrValGiniCoeff);
                }
                attrGiniCoeff = (attrGiniCoeff / (double)totCount);
                attrGiniCoeff = (classGiniCoeff - attrGiniCoeff);
                attrImpMap.put(headers[i], attrGiniCoeff);
            }
        }
        
        Map<String, Double> combAttrImpMap = new HashMap<String, Double>();
        for(int i = 0; i < jpKeys.size(); i++){
            //System.out.println("["+(i+1)+"]" + jpKeys.get(i));
            String p_jpKey = jpKeys.get(i);
            StringTokenizer st = new StringTokenizer(p_jpKey, ",");
            Double combAttrImp = 0.0;
            Integer noOfTokens = st.countTokens();
            while(st.hasMoreTokens()){
                String n_token = st.nextToken();
                String attrName = n_token.substring(0, n_token.indexOf(":"));
                combAttrImp = combAttrImp + attrImpMap.get(attrName);
            }
            combAttrImp = (combAttrImp/(double)noOfTokens);
            combAttrImpMap.put(p_jpKey, combAttrImp);
            //System.out.println("\tcombAttrImp: " + (combAttrImp/(double)noOfTokens));
        }
        
        return combAttrImpMap;
    }
    
    public ArrayList<Object[]> getApplicableForceKeysInDecOrderOfImportance(HashSet<String> applicableForceKeys, ArrayList<Object[]> sortedFeatureSelectionList){
        
        ArrayList<Object[]> sortedForceKeys = new ArrayList<Object[]>();
        for(int i = 0; i < sortedFeatureSelectionList.size(); i++){
            String p_jpKey = (String)sortedFeatureSelectionList.get(i)[0];
            if(applicableForceKeys.contains(p_jpKey)){
                sortedForceKeys.add(sortedFeatureSelectionList.get(i));
            }
        }
        return sortedForceKeys;
    }
    
    public String[] removeNoisyForceKeys(ArrayList<Object[]> sortedForceKeys, Double noiseFraction){
        Integer noOfFilteredForces = (int)((double)sortedForceKeys.size() * (1 - noiseFraction));
        if(noOfFilteredForces == 0)
            noOfFilteredForces = 1;
        
        String[] choosenForceKeys = new String[noOfFilteredForces];
        Integer choosenForcesCnt = 0;
        for(int i = 0; i < sortedForceKeys.size(); i++){
            if(choosenForcesCnt.equals(noOfFilteredForces))
                break;
            String p_jpKey = (String)sortedForceKeys.get(i)[0];
            //boolean matches = StringUtil.transactionMathechesWithKey(headers, transaction, classAttIndex, p_jpKey);
            //if(matches){
                choosenForceKeys[choosenForcesCnt] = p_jpKey;
                choosenForcesCnt++;
            //}
        }
        return choosenForceKeys;
    }
    
    public Integer[] chooseRandomForces(Integer totalForces, Integer noOfForces){
        Integer[] choosenForces = new Integer[totalForces];
        for(int i = 0; i < choosenForces.length; i++)
            choosenForces[i] = 0;
        int already_choosen = 0;
        while(true){
            Integer minIndex = 0;
            Integer maxIndex = choosenForces.length - 1;
            Integer random_index = minIndex + (int)(Math.random() * ((maxIndex - minIndex) + 1));
            if(choosenForces[random_index] == 0){
                choosenForces[random_index] = 1;
                already_choosen++;
            }
            else
                continue;
            
            if(already_choosen == noOfForces)
                break;
        }
        //System.out.print("\t\t\t");
        //for(int i = 0; i < choosenForces.length; i++)
        //    System.out.print(choosenForces[i]+"|");
        //System.out.println("\n");
        return choosenForces;
    }
    
    public Integer[] chooseBestForces(Integer totalForces, Integer noOfForces){
        Integer[] choosenForces = new Integer[totalForces];
        for(int i = 0; i < choosenForces.length; i++)
            choosenForces[i] = 0;
        for(int i = 0; i < noOfForces; i++)
            choosenForces[i] = 1;
        //System.out.print("\t\t\t");
        //for(int i = 0; i < choosenForces.length; i++)
        //    System.out.print(choosenForces[i]+"|");
        //System.out.println("\n");
        //System.out.println("choosenForces.length: " + choosenForces.length);
        return choosenForces;
    }
    
    public String getVoteWinnerOnCummulativeForceStrength(String[] possibleClassValues, Double[] cummulativeForceStrength){
        Integer max_index = 0;
        Double max_strength = cummulativeForceStrength[0];
        
        if(debug)
            System.out.print("\t");
        for(int i = 0; i < cummulativeForceStrength.length; i++){
            if(cummulativeForceStrength[i] > max_strength){
                max_index = i;
                max_strength = cummulativeForceStrength[i];
            }
            if(debug)
                System.out.print(possibleClassValues[i]+": "+cummulativeForceStrength[i]+"|");
        }
        if(debug)
            System.out.println("\n\tWinner: " + possibleClassValues[max_index]);
        return possibleClassValues[max_index];
    }
    
    public String getVoteWinnerOnNoOfWinningPass(String[] possibleClassValues, ArrayList<Double[]> resultantForcesInPasses){
        Map<String, Integer> voteCounts = new HashMap<String, Integer>();
        
        for(int i = 0; i < resultantForcesInPasses.size(); i++){
            Double[] p_resultantForcesInPass = resultantForcesInPasses.get(i);
            Double max_force = p_resultantForcesInPass[0];
            Integer max_force_key = 0;
            for(int j = 0; j < p_resultantForcesInPass.length; j++){
                if((p_resultantForcesInPass[j]) > max_force){
                    max_force = p_resultantForcesInPass[j];
                    max_force_key = j;
                }
            }
            Integer p_voteCount = (voteCounts.get(possibleClassValues[max_force_key]) == null? 0 : voteCounts.get(possibleClassValues[max_force_key]));
            voteCounts.put(possibleClassValues[max_force_key], (p_voteCount + 1));
        }
        if(debug)
            System.out.println("\tNo of Votes" + voteCounts);
        
        Integer max_count = 0;
        String max_key = "";
        Iterator ksIt = voteCounts.keySet().iterator();
        while(ksIt.hasNext()){
            String p_key = (String)ksIt.next();
            Integer p_count = voteCounts.get(p_key);
            if(p_count > max_count){
                max_key = p_key;
                max_count = p_count;
            }
        }
        if(debug)
            System.out.println("\tWinner: " + max_key);
        return max_key;
    }
    
    public static Double getScaledGiniCoeffOfAKey(Integer[] values){
        Double[] d_values = new Double[values.length];
        for(int i = 0; i < values.length; i++)
            d_values[i] = (double)values[i];
        return getScaledGiniCoeffOfAKey(d_values);
    }
    
    public static Double getScaledGiniCoeffOfAKey(Double[] values){
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
            //System.out.print("\t");
            //for(int i = 0; i < values.length; i++)
            //    System.out.print(values[i] + "|");
            //System.out.println("giniCoeff : " + giniCoeff + ", scaledGiniCoeff : " + scaledGiniCoeff);
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
}
