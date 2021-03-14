/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.data;

import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Anupam
 */
public class Dataset {
    
    ArrayList<Predictor> predictors;
    ArrayList<Observation> records;
    TreeSet<String> classLabels;

    public ArrayList<Predictor> getPredictors() {
        return predictors;
    }

    private void setPredictors(ArrayList<Predictor> predictors) {
        this.predictors = predictors;
    }

    public ArrayList<Observation> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Observation> records) {
        this.records = records;
    }

    public TreeSet<String> getClassLabels() {
        return classLabels;
    }

    public void setClassLabels(TreeSet<String> classLabels) {
        this.classLabels = classLabels;
    }

    public Dataset(DataParameters dataParameters) {
        readFromCSV(dataParameters);
    }
    
    public boolean readFromCSV(DataParameters dataParameters){
        // Reading CSV
        //********************************************************************************
        int totalNoOfCols = 0;
        String line = "";
        String[] headers = null;
        ArrayList<String[]> full_dataset = new ArrayList<String[]>();
        Integer classVariableIndex = 0;
        
        ArrayList<Predictor> l_predictorList = new ArrayList<Predictor>();
        ArrayList<Observation> l_records = new ArrayList<Observation>();
        TreeSet<String> l_classLabels = new TreeSet<String>();
        
        File csvFile = new File(dataParameters.getCsvFilePath());
        
        //calculating total number of attributes available in the header row of the dataset 
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            if((line = br.readLine()) != null){
                line = CommonUtil.removeEscapeCharFromString(line, dataParameters.getCsvEscapeChar(), dataParameters.getCsvSplitBy());
                String[] t_headers = line.split(dataParameters.getCsvSplitBy());
                totalNoOfCols = t_headers.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            //getting filtered header after removing the columns to be discarded
            HashSet<Integer> discardedIndexes = new HashSet<Integer>();
            if((line = br.readLine()) != null){
                //System.out.println(line);
                line = CommonUtil.removeEscapeCharFromString(line, dataParameters.getCsvEscapeChar(), dataParameters.getCsvSplitBy());
                String[] t_headers = line.split(dataParameters.getCsvSplitBy());
                for(int i = 0; i < t_headers.length; i++){
                    if(dataParameters.getDiscardedAttributes().contains(t_headers[i]))
                        discardedIndexes.add(i);
                }
                headers = CommonUtil.prepareArray(line, discardedIndexes, dataParameters.getCsvSplitBy(), totalNoOfCols);
            }
            
            //getting the index of class attribute in the filtered header
            classVariableIndex = headers.length - 1;
            for(int i=0; i < headers.length; i++){
                if(headers[i].equals(dataParameters.getClassAttName())){
                    classVariableIndex = i;
                    break;
                }
            }
            
            //builing the predictor list
            for(int i = 0; i < headers.length; i++){
                if(i != classVariableIndex)
                    l_predictorList.add(new Predictor(headers[i], String.class));
            }
            
            while ((line = br.readLine()) != null) {
                line = CommonUtil.removeEscapeCharFromString(line, dataParameters.getCsvEscapeChar(), dataParameters.getCsvSplitBy());
                if(!(line.trim().equals(""))){
                    //getting transaction array including observed class label
                    String[] transaction = CommonUtil.prepareArray(line, discardedIndexes, dataParameters.getCsvSplitBy(), totalNoOfCols);
                    full_dataset.add(transaction);
                    
                    Observation observation = new Observation();
                    for(int i = 0, j = 0; i < transaction.length; i++){
                        if(i != classVariableIndex){
                            Predictor p = l_predictorList.get(j);
                            if(dataParameters.getMissingValueStrings().contains(transaction[i]))
                                observation.addInputAttributeWithValue(p, null);
                            else if(p.getPredictorType().equals(String.class)) 
                                observation.addInputAttributeWithValue(p, transaction[i]);
                            else if(p.getPredictorType().equals(Double.class)) 
                                observation.addInputAttributeWithValue(p, Double.parseDouble(transaction[i]));
                            else if(p.getPredictorType().equals(Integer.class)) 
                                observation.addInputAttributeWithValue(p, Integer.parseInt(transaction[i]));
                            
                            j++;
                        }else if(i == classVariableIndex){
                            observation.setObservedOutput(transaction[i]);
                            l_classLabels.add(transaction[i]);
                        }
                    }
                    l_records.add(observation);
                }
            }
            
            //System.out.println("headers: " + headers);
            //System.out.println("full_dataset: " + full_dataset.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setPredictors(l_predictorList);
        setRecords(l_records);
        setClassLabels(l_classLabels);
        return true;
    }
    
    public Object[] split(Double splitPercent){
        Object[] splitSets = new Object[2];
        ArrayList<Observation> trainSet = new ArrayList<Observation>();
        ArrayList<Observation> testSet = new ArrayList<Observation>();
        for(int i=0; i<getRecords().size(); i++){
            Random rnd = new Random();
            double rnd_number = rnd.nextDouble();
            if(rnd_number <= splitPercent)
                trainSet.add(getRecords().get(i));
            else 
                testSet.add(getRecords().get(i));
        }
        splitSets[0] = trainSet;
        splitSets[1] = testSet;
        
        return splitSets;
    }
    
    public Object[] getFolds(Integer noOfFold){
        Object[] splitSets = new Object[noOfFold];
        for(int i = 0; i < getRecords().size(); i++){
            ArrayList<Observation> p_al = (ArrayList<Observation>)splitSets[i%noOfFold];
            if(p_al == null)
                p_al = new ArrayList<Observation>();
            p_al.add(getRecords().get(i));
            splitSets[i%noOfFold] = p_al;
        }
        return splitSets;
    }
    
}
