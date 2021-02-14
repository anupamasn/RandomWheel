/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.utils;

import in.ac.iitkgp.machinelearning.randomwheel.parameters.DataParameters;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.SortedSet;
import java.util.StringTokenizer;

/**
 *
 * @author Anupam
 */
public class DataUtil {
    
    boolean debug = false;
    DataParameters dp = null;

    public DataParameters getDp() {
        return dp;
    }

    public void setDp(DataParameters dp) {
        this.dp = dp;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public DataUtil(DataParameters dp, boolean debug) {
        setDp(dp);
        setDebug(debug);
    }
    
    public Object[] readFromCSV(){
        // Reading CSV
        //********************************************************************************
        int totalNoOfCols = 0;//36
        String line = "";
        String[] headers = null;
        ArrayList<String[]> full_dataset = new ArrayList<String[]>();
        
        File csvFile = new File(getDp().getCsvFilePath());
        
        //calculating total number of attributes avaiulable in the header row of the dataset 
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            if((line = br.readLine()) != null){
                line = StringUtil.removeEscapeCharFromString(line, getDp().getCsvEscapeChar(), getDp().getCsvSplitBy());
                String[] t_headers = line.split(getDp().getCsvSplitBy());
                totalNoOfCols = t_headers.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            HashSet<Integer> discardedIndexes = new HashSet<Integer>();
            if((line = br.readLine()) != null){
                //System.out.println(line);
                line = StringUtil.removeEscapeCharFromString(line, getDp().getCsvEscapeChar(), getDp().getCsvSplitBy());
                String[] t_headers = line.split(getDp().getCsvSplitBy());
                for(int i = 0; i < t_headers.length; i++){
                    if(getDp().getDiscardedAttributes().contains(t_headers[i]))
                        discardedIndexes.add(i);
                }
                headers = StringUtil.prepareArray(line, discardedIndexes, getDp().getCsvSplitBy(), totalNoOfCols);
            }
            
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                line = StringUtil.removeEscapeCharFromString(line, getDp().getCsvEscapeChar(), getDp().getCsvSplitBy());
                if(!(line.trim().equals(""))){
                    String[] transaction = StringUtil.prepareArray(line, discardedIndexes, getDp().getCsvSplitBy(), totalNoOfCols);
                    //for(int i=0; i<transaction.length; i++)
                    //    System.out.print(transaction[i]);
                    //System.out.print("\n");
                    full_dataset.add(transaction);
                }
            }
            
            //System.out.println("headers: " + headers);
            //System.out.println("full_dataset: " + full_dataset.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Integer classAttIndex = headers.length - 1;
        for(int i=0; i < headers.length; i++){
            if(headers[i].equals(getDp().getClassAttName())){
                classAttIndex = i;
                break;
            }
        }
        
        Object[] objs = new Object[3];
        objs[0] = headers;
        objs[1] = full_dataset;
        objs[2] = classAttIndex;
        
        return objs;
    }
    
    public Object[] split(ArrayList<String[]> dataset){
        Object[] splitSets = new Object[2];
        ArrayList<String[]> trainSet = new ArrayList<String[]>();
        ArrayList<String[]> testSet = new ArrayList<String[]>();
        for(int i=0; i<dataset.size(); i++){
            Random rnd = new Random();
            double rnd_number = rnd.nextDouble();
            //System.out.println(rnd_number);
            if(rnd_number <= getDp().getSplitPercent())
                trainSet.add(dataset.get(i));
            else 
                testSet.add(dataset.get(i));
        }
        splitSets[0] = trainSet;
        splitSets[1] = testSet;
        
        return splitSets;
    }
    
    public Object[] getFolds(ArrayList<String[]> dataset, Integer noOfFold){
        Object[] splitSets = new Object[noOfFold];
        for(int i = 0; i < dataset.size(); i++){
            ArrayList<String[]> p_al = (ArrayList<String[]>)splitSets[i%noOfFold];
            if(p_al == null)
                p_al = new ArrayList<String[]>();
            p_al.add(dataset.get(i));
            splitSets[i%noOfFold] = p_al;
            //System.out.println(i + " added in " + (i%noOfFold));
        }
        return splitSets;
    }
    
    public static Integer getIndexOf(String[] arr, String val){
        Integer attIndex = -1;
        for(int i=0; i<arr.length; i++){
            if(arr[i].equals(val)){
                attIndex = i;
                break;
            }
        }
        return attIndex;
    }
    
    public ArrayList<String[]> getUniqueAttributeAndClassValues(ArrayList<String[]> dataset, String[] headers){
        ArrayList<HashSet> possibleValuesAL = new ArrayList<HashSet>();
        for(int i = 0; i < headers.length; i++)
            possibleValuesAL.add(new HashSet<String>());
        
        for(int i = 0; i < dataset.size(); i++){
            for(int j = 0; j < headers.length; j++){
                String value = dataset.get(i)[j];
                if(!(getDp().getMissingValueStrings().contains(value)))
                    possibleValuesAL.get(j).add(value);
            }
        }
        
        ArrayList<String[]> possibleValues = new ArrayList<String[]>();
        for(int i = 0; i < possibleValuesAL.size(); i++){
            Iterator it = possibleValuesAL.get(i).iterator();
            String[] posValueArr = new String[possibleValuesAL.get(i).size()];
            int j = 0;
            while(it.hasNext()){
                posValueArr[j] = (String)it.next();
                j++;
            }
            possibleValues.add(posValueArr);
            //System.out.println(headers[i] + ": " + posValueArr.length);
        }
        //System.out.println(possibleValues.size());
        return possibleValues;
    }
    
    public ArrayList<String> getJointProbabilityKeys(String[] headers, ArrayList<String[]> possibleAttributeValues, int classAttIndex, int depth)
    {
        if(depth > (headers.length - 1)){
            System.out.println("Invalid depth!!!");
            return null;
        }
        
        ArrayList<String> keys = new ArrayList<String>();
        String[] attHeaders = new String[headers.length - 1];
        //making a attribute header list after discarding class
        int ahIndex = 0;
        for(int i=0; i<headers.length; i++)
        {
            if(i != classAttIndex)
            {
                attHeaders[ahIndex] = headers[i];
                ahIndex++;
            }
        }
        
        for(int level = 1; level <= depth; level++){
            ArrayList<String[]> attrCombs = CombinationUtil.getCombinationsFromAnArray(attHeaders, level);
            //System.out.println("[Level "+level+"]combs.size: " + attrCombs.size());
            for(int i = 0; i < attrCombs.size(); i++){
                String[] attrComb = attrCombs.get(i);
                List<List<String>> p_valComb = new ArrayList<List<String>>();
                for(int j=0; j<attrComb.length; j++){
                    //System.out.print(attrComb[j] + " ");
                    Integer att_index = getIndexOf(headers, attrComb[j]);
                    String[] possibleAttrVal = possibleAttributeValues.get(att_index);
                    ArrayList<String> valList = new ArrayList<String>();
                    for(int k = 0; k < possibleAttrVal.length; k++)
                        valList.add(possibleAttrVal[k]);
                    p_valComb.add(valList);
                }
                //System.out.println("");
                Set<List<String>> valComb = CombinationUtil.getCombinationsFromMultipleListsWithOneValEach(p_valComb);
                Iterator valCombIt = valComb.iterator();
                while(valCombIt.hasNext()){
                    Object[] p_val = ((ArrayList)valCombIt.next()).toArray();
                    String p_key = "";
                    for(int k = 0; k < p_val.length; k++){
                        p_key += (attrComb[k]+":"+p_val[k]+",");
                    }
                    //System.out.println("\t"+p_key.substring(0, p_key.lastIndexOf(",")));
                    keys.add(p_key.substring(0, p_key.lastIndexOf(",")));
                }
            }
        }
        return keys;
    }
}
