/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel;

import in.ac.iitkgp.machinelearning.randomwheel.parameters.DataParameters;
import in.ac.iitkgp.machinelearning.randomwheel.parameters.TuningParameters;
import in.ac.iitkgp.machinelearning.randomwheel.utils.DataUtil;
import in.ac.iitkgp.machinelearning.randomwheel.utils.EvaluationUtil;
import in.ac.iitkgp.machinelearning.randomwheel.utils.StatUtil;
import in.ac.iitkgp.machinelearning.randomwheel.utils.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Anupam
 */
public class RandomWheel {

    /**
     * @param args the command line arguments
     */
    private static boolean debug = false;
    
    DataParameters dp = null;
    TuningParameters tp = null;
    
    //internal parameters
    ArrayList<String> jpKeys = null;
    ArrayList<String[]> possibleAttributeValues = null;
    String[] headers = null;
    int classAttIndex = 0;
    Object[] trainingObj = null;

    public int getClassAttIndex() {
        return classAttIndex;
    }

    public void setClassAttIndex(int classAttIndex) {
        this.classAttIndex = classAttIndex;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public ArrayList<String> getJpKeys() {
        return jpKeys;
    }

    public void setJpKeys(ArrayList<String> jpKeys) {
        this.jpKeys = jpKeys;
    }

    public ArrayList<String[]> getPossibleAttributeValues() {
        return possibleAttributeValues;
    }

    public void setPossibleAttributeValues(ArrayList<String[]> possibleAttributeValues) {
        this.possibleAttributeValues = possibleAttributeValues;
    }

    public DataParameters getDp() {
        return dp;
    }

    public void setDp(DataParameters dp) {
        this.dp = dp;
    }

    public TuningParameters getTp() {
        return tp;
    }

    public void setTp(TuningParameters tp) {
        this.tp = tp;
    }

    public void setTrainingObj(Object[] trainingObj) {
        this.trainingObj = trainingObj;
    }

    public Object[] getTrainingObj() {
        return trainingObj;
    }
    
    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        RandomWheel.debug = debug;
    }
    
    public RandomWheel(DataParameters dp, TuningParameters tp, boolean debug) {
        setDp(dp);
        setTp(tp);
        setDebug(debug);
    }
    
    public void initiateLearingOnKFoldCV(){
        DataUtil du = new DataUtil(getDp(), debug);
        
        ArrayList<String[]> full_dataset = readCSVandValidateInputs(du);
        
        setPossibleAttributeValues(du.getUniqueAttributeAndClassValues(full_dataset, getHeaders()));
        setJpKeys(du.getJointProbabilityKeys(getHeaders(), getPossibleAttributeValues(), getClassAttIndex(), getTp().getDepth()));
        System.out.println("No of Keys: " + getJpKeys().size());
        System.out.println("==================");
        
        ArrayList<Object[][]> allPred = new ArrayList<Object[][]>();
        // Generating folds and for each folds
        //*******************************************************************************
        Object[] trtsObj = du.getFolds(full_dataset, getDp().getNoOfFold());
        for(int i = 0; i < getDp().getNoOfFold(); i++){
            System.out.println("Starting Fold " + (i+1));
            ArrayList<String[]> testSet = (ArrayList<String[]>)trtsObj[i];
            ArrayList<String[]> trainSet = new ArrayList<String[]>();
            for(int j = 0; j < getDp().getNoOfFold(); j++){
                if(j != i){
                    trainSet.addAll((ArrayList<String[]>)trtsObj[j]);
                }
            }
            
            //Training
            train(trainSet);
            //System.out.println("Training completed...");
            
            //Predict
            Object[][] prediction = predictAllInTestSet(testSet);
            
            allPred.add(prediction);
            
        }
        //System.out.println("==================");
        
        Integer cnt = 0;
        Object[][] allPredArr = new Object[full_dataset.size()][3];
        for(int i = 0; i < allPred.size(); i++){
            Object[][] prediction = allPred.get(i);
            for(int j = 0; j < prediction.length; j++){
                //if(debug)
                //    System.out.println("[Fold " + i + "-Test "+j+"] " + prediction[j][0] + "-" + prediction[j][1]);
                allPredArr[cnt][0] = prediction[j][0];
                allPredArr[cnt][1] = prediction[j][1];
                allPredArr[cnt][2] = prediction[j][2];
                cnt++;
            }
        }
        
        //evaluate
        evaluate(allPredArr);
    }
    
    public void initiateLearingOnSplitPercent(){
        DataUtil du = new DataUtil(getDp(), debug);
        
        ArrayList<String[]> full_dataset = readCSVandValidateInputs(du);

        // Spliting CSV
        //*******************************************************************************
        Object[] splitSets = du.split(full_dataset);
        ArrayList<String[]> trainSet = (ArrayList<String[]>)splitSets[0];
        ArrayList<String[]> testSet = (ArrayList<String[]>)splitSets[1];
        
        System.out.println("trainSet: " + trainSet.size());
        System.out.println("testSet: " + testSet.size());
        
        //generating possible attribute values and keys
        setPossibleAttributeValues(du.getUniqueAttributeAndClassValues(trainSet, getHeaders()));
        setJpKeys(du.getJointProbabilityKeys(getHeaders(), getPossibleAttributeValues(), getClassAttIndex(), getTp().getDepth()));
        
        //Training
        train(trainSet);
        System.out.println("Training completed. No of Keys: " + getJpKeys().size());
        
        //Predict
        Object[][] prediction = predictAllInTestSet(testSet);
        
        //evaluate
        evaluate(prediction);
    }
    
    private ArrayList<String[]> readCSVandValidateInputs(DataUtil du){
        //Reading CSV
        Object[] dataObj = du.readFromCSV();
        //setting headers
        setHeaders((String[])dataObj[0]);
        ArrayList<String[]> full_dataset = (ArrayList<String[]>)dataObj[1];
        //setting class attribute index
        setClassAttIndex((Integer)dataObj[2]);
        
        if(getTp().getDepth() == 0){
            Integer p_depth = (int)Math.ceil(Math.sqrt((double)(headers.length - 1)));
            getTp().setDepth(p_depth);
        }
        
        System.out.println("No of trials: " + (getTp().getNoOfPass()));
        System.out.println("Depth: " + (getTp().getDepth()));
        System.out.println("Noise fraction: " + getTp().getNoiseFraction());
        String discardedAttrStr = "";
        Iterator it = getDp().getDiscardedAttributes().iterator();
        while(it.hasNext()){
            if(discardedAttrStr.equals(""))
                discardedAttrStr += ((String)it.next());
            else
                discardedAttrStr += (", " + (String)it.next());
        }
        System.out.println("Discarded attributes: " + (discardedAttrStr.equals("")?"<none>":discardedAttrStr));
        System.out.println("Debug: " + (debug?"yes":"no"));
        System.out.println("==================");
        
        System.out.print("No of attributes: " + (headers.length - 1) + " {");
        for(int i = 0; i < headers.length; i++)
            System.out.print(headers[i]+(i<(headers.length - 1)?",":""));
        System.out.println("}");
        System.out.println("Class attribute name: " + getDp().getClassAttName());
        System.out.println("Total no of records: " + (full_dataset.size()));
        
        if(getTp().getDepth() > (headers.length - 1)){
            System.out.println("Depth should not be greater than " + (headers.length - 1));
            System.exit(0);
        }
        
        return full_dataset;
    }
    
    private void train(ArrayList<String[]> trainSet){
        
        StatUtil su = new StatUtil(debug);
        Object[] retObj = su.getClassValueCountsOfKeys(trainSet, getHeaders(), getPossibleAttributeValues(), getClassAttIndex(), getJpKeys(), getDp().getMissingValueStrings(), getTp().getDepth());
        Map<String, Integer[]> keyClassValueCounts = (Map<String, Integer[]>)retObj[0];
        Map<String, Integer> classValueCounts = (Map<String, Integer>)retObj[1];
        
        Integer[] classVals = new Integer[classValueCounts.size()];
        Iterator ksIt = classValueCounts.keySet().iterator();
        int cnt = 0;
        while(ksIt.hasNext()){
            String key = (String)ksIt.next();
            classVals[cnt++] = classValueCounts.get(key);
        }
        Double classGiniCoeff = StatUtil.getScaledGiniCoeffOfAKey(classVals);
        
        Map<String, Double> scaledGiniCoeffMap = new HashMap<String, Double>();
        Iterator<String> classValKeysIt = keyClassValueCounts.keySet().iterator();
        int keyCnt = 0;
        while(classValKeysIt.hasNext()){
            String key = (String)classValKeysIt.next();
            Integer[] p_vals = keyClassValueCounts.get(key);
            if(debug){
                System.out.print("["+(++keyCnt)+"]"+key+": ");
                for(int i = 0; i < p_vals.length; i++)
                    System.out.print(getPossibleAttributeValues().get(getClassAttIndex())[i]+"="+p_vals[i] + "|");
            }
            Double scaledGiniCoeff = StatUtil.getScaledGiniCoeffOfAKey(p_vals);
            //scaledGiniCoeff = Math.abs(scaledGiniCoeff - classGiniCoeff);
            scaledGiniCoeffMap.put(key, scaledGiniCoeff);
            if(debug)
                System.out.print(scaledGiniCoeff + "\n");
        }
        if(debug)
            System.out.println("class label counts : " + classValueCounts);
        
        Map<String, Double> attributeImportance = su.getAttributeImportanceMap(getHeaders(), getClassAttIndex(), getPossibleAttributeValues(), getJpKeys(), keyClassValueCounts, classValueCounts);
        
        //System.out.println("attributeImportance: " + attributeImportance);
        Object[] l_trainingObj = new Object[5];
        l_trainingObj[0] = keyClassValueCounts;
        l_trainingObj[1] = scaledGiniCoeffMap;
        l_trainingObj[2] = attributeImportance;
        l_trainingObj[3] = classValueCounts;
        l_trainingObj[4] = trainSet.size();
        
        setTrainingObj(l_trainingObj);
    }
    
    private Object[][] predictAllInTestSet(ArrayList<String[]> testSet){
        Object[][] prediction = new Object[testSet.size()][3];
        StatUtil su = new StatUtil(debug);
        ArrayList<Object[]> sortedAttributeImportanceList = su.sortMapOnValue((Map<String, Double>)getTrainingObj()[2]);
        
        //For each record in test set
        for(int i = 0; i < testSet.size(); i++){
            String[] transaction = testSet.get(i);
            String actual_value = transaction[getClassAttIndex()];
            
            Object[] predArr = predictATransaction(transaction, sortedAttributeImportanceList);
            
            String predicted_value = (String)predArr[0];
            Double prediction_confidence = (Double)predArr[1];
            prediction[i][0] = actual_value;
            prediction[i][1] = predicted_value;
            prediction[i][2] = prediction_confidence;
            
            if(debug){
                System.out.print("["+i+"],");
                for(int n = 0; n < transaction.length; n++){
                    System.out.print(transaction[n]+",");
                }
                System.out.print(prediction[i][1]+(!predicted_value.equals(actual_value)?",<wrong>":",<correct>")+","+prediction_confidence+"\n");
            }
            
        }
        return prediction;
    }
    
    private Object[] predictATransaction(String[] transaction, ArrayList<Object[]> sortedAttributeImportanceList){
        ArrayList<Double[]> resultantForceStrengthsInPasses = new ArrayList<Double[]>();
        StatUtil su = new StatUtil(debug);
        if(debug){
            System.out.println(StringUtil.buildStringFromRecordArr(getHeaders(), transaction, getClassAttIndex()));
        }
        
        Double noiseFraction = getTp().getNoiseFraction();
        HashSet<String> applJpKeys = StringUtil.getApplicableJpKeysFromATransactions(transaction, getHeaders(), getClassAttIndex(), getDp().getMissingValueStrings(), getTp().getDepth());
        ArrayList<Object[]> applForceKeys = su.getApplicableForceKeysInDecOrderOfImportance(applJpKeys, sortedAttributeImportanceList);
        //removing noisy keys
        if(debug){
            System.out.println("\tapplForceKeys: " + applForceKeys.size() + ", noiseFraction: " + noiseFraction);
        }
        String[] filteredForceKeys = su.removeNoisyForceKeys(applForceKeys, noiseFraction);
        
        //=============== Thread related changes =================
        ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        List<Future<Double[]>> list = new ArrayList<Future<Double[]>>();
        Callable<Double[]> callable = new MeasureResultantForcesInSinglePass(
                          filteredForceKeys
                        , possibleAttributeValues
                        , classAttIndex
                        , trainingObj
                        , isDebug());
        try{
            for(int i = 0; i < getTp().getNoOfPass(); i++){
                Future<Double[]> future = executor.submit(callable);
                list.add(future);
            }
            for(Future<Double[]> fut : list){
                try {
                    resultantForceStrengthsInPasses.add(fut.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        executor.shutdown();
        //========================================================
        
        
        Double[] cummulativeForceStrength = new Double[getPossibleAttributeValues().get(getClassAttIndex()).length];
        for(int i = 0; i < cummulativeForceStrength.length; i++)
            cummulativeForceStrength[i] = 0.0;
        
        for(int i = 0; i < resultantForceStrengthsInPasses.size(); i++){
            Double[] p_resultantForcesInPass = resultantForceStrengthsInPasses.get(i);
            for(int j = 0; j < p_resultantForcesInPass.length; j++){
                cummulativeForceStrength[j] += p_resultantForcesInPass[j];
            }
        }
        
        String predicted_value = su.getVoteWinnerOnCummulativeForceStrength(getPossibleAttributeValues().get(getClassAttIndex()), cummulativeForceStrength);
        //String predicted_value = su.getVoteWinnerOnNoOfWinningPass(getPossibleAttributeValues().get(getClassAttIndex()), resultantForceStrengthsInPasses);
        
        //Double resultantForceInequality = StatUtil.getScaledGiniCoeffOfAKey(cummulativeForceStrength);
        Arrays.sort(cummulativeForceStrength);
        Double winner_strength = (double)(cummulativeForceStrength[cummulativeForceStrength.length - 1]);
        Double runner_strength = (double)(cummulativeForceStrength[cummulativeForceStrength.length - 2]);
        //Double resultantForceInequality = (winner_strength - runner_strength)/(winner_strength);
        
        Double[] narr = new Double[2];
        narr[0] = winner_strength;
        narr[1] = runner_strength;
        Double resultantForceInequality = StatUtil.getScaledGiniCoeffOfAKey(narr);
        
        if(resultantForceInequality.isNaN())
            resultantForceInequality = 0.0;
        Object[] retObj = new Object[2];
        
        retObj[0] = predicted_value;
        retObj[1] = resultantForceInequality;
        
        return retObj;
    }
    
    
    private void evaluate(Object[][] predictionArray){
        if(debug)
            System.out.println("Starting evaluation");
        
        EvaluationUtil eu = new EvaluationUtil();
        Integer[][] confusionMatrix = eu.buildConfusionMatrix(predictionArray, getPossibleAttributeValues().get(getClassAttIndex()));
        double overallAccuracy = eu.getOverallAccuracy(confusionMatrix);
        Map<String, Double> classAccuracy = eu.getClassAccuracies(confusionMatrix, getPossibleAttributeValues().get(getClassAttIndex()));
    }
    
}
