/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 *
 * @author Anupam
 */
public class StringUtil {
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
    
    public static String buildStringFromRecordArr(String[] headers, String[] record, int classAttIndex){
        String retStr = "";
        for(int i = 0; i < record.length; i++){
            if(i != classAttIndex){
                retStr += ((retStr.equals("")?"":",") + headers[i] + ":" + record[i]);
            }
        }
        retStr += ((retStr.equals("")?"":"&") + headers[classAttIndex] + ":" + record[classAttIndex]);
        return retStr;
    }
    
    public static String getAttrCombinationFromJPKey(String p_jpKey){
        String attrCombName = "";
        StringTokenizer st = new StringTokenizer(p_jpKey, ",");
        while(st.hasMoreTokens()){
            String p_token = (String)st.nextToken();
            attrCombName += ((attrCombName.equals("")?"":",") + (p_token.substring(0, p_token.indexOf(":"))));
        }
        return attrCombName;
    }
    
    public static HashSet<String> getApplicableJpKeysFromATransactions(String transaction[], String[] headers, int classAttIndex, HashSet<String> missingValueStrings, int depth){
        //getting array with elements as header:value
        ArrayList<String> transactionAsAL = new ArrayList<String>();
        for(int j = 0; j < transaction.length; j++){
            if(j != classAttIndex && !missingValueStrings.contains(transaction[j].toString())){
                transactionAsAL.add((headers[j] + ":" + transaction[j]));
            }
        }
        String[] transactionAsArray = new String[transactionAsAL.size()];
        for(int j = 0; j < transactionAsAL.size(); j++)
            transactionAsArray[j] = transactionAsAL.get(j);

        //getting all applicable jpKeys
        HashSet<String> applicableJpKeysHS = new HashSet<String>();
        ArrayList<String[]> allAttrAndValCombs = new ArrayList<String[]>();
        for(int level = 1; level <= depth; level++)
            allAttrAndValCombs.addAll(CombinationUtil.getCombinationsFromAnArray(transactionAsArray, level));

        for(int j = 0; j < allAttrAndValCombs.size(); j++){
            String p_jpKey = "";
            String[] p_arr = allAttrAndValCombs.get(j);
            for(int k = 0; k < p_arr.length; k++){
                p_jpKey += ((k==0?"":",") + p_arr[k]);
                applicableJpKeysHS.add(p_jpKey);
            }
        }
        return applicableJpKeysHS;
    }
}
