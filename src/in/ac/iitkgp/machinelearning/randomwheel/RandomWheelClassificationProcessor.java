/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel;

import in.ac.iitkgp.machinelearning.randomwheel.classification.Classifier;
import in.ac.iitkgp.machinelearning.data.DataParameters;
import in.ac.iitkgp.machinelearning.randomwheel.parameters.TuningParameters;
import in.ac.iitkgp.machinelearning.data.Dataset;
import in.ac.iitkgp.machinelearning.data.Observation;
import in.ac.iitkgp.machinelearning.data.Predictor;
import in.ac.iitkgp.machinelearning.data.UnknownObservation;
import in.ac.iitkgp.machinelearning.evaluation.TestData;
import in.ac.iitkgp.machinelearning.evaluation.ClassificationAssessmentArbitrator;
import in.ac.iitkgp.machinelearning.randomwheel.classification.Prediction;
import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingData;
import in.ac.iitkgp.machinelearning.randomwheel.training.TrainingKnowledge;
import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 *
 * @author Anupam
 */
public class RandomWheelClassificationProcessor {
    
    public static void main(String[] args) {
        startCLIExecution();
    }
    
    private static void startCLIExecution(){
        //fixed values
        String csvSplitBy = ",";
        String csvEscapeChar = "\"";
        HashSet<String> missingValueStrings = new HashSet<String>();
        missingValueStrings.add("");
        missingValueStrings.add("?");
        
        String csvFilePath = "";
        HashSet<String> discardedAttributes = new HashSet<String>();
        String classAttName = "";
        
        String[] allColumnHeaders = null;
        
        String test_option = "";
        Double splitPercent = 0.7;
        Integer noOfFold = 10;
        Integer noOfTrial;
        Integer depth;
        Double noiseFraction;
        
        boolean debug = false;
        
        
        // BufferReader for capturing the user input
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
        
        // getting the CSV file from user
        while(true){
            try{
                System.out.print("Please enter path of the CSV file containing the dataset (first header row should include the predictor attribute/class variable name, data should be comma separated and escaped by double quote, and missing value should be blank or ?): ");
                csvFilePath = reader.readLine();
                try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                    String line = br.readLine();
                    line = CommonUtil.removeEscapeCharFromString(line, csvEscapeChar, csvSplitBy);
                    allColumnHeaders = line.split(csvSplitBy);
                } catch (Exception e) {
                    System.out.println("Invalid. Please enter again.");
                    throw new Exception();
                }
                break;
            }catch(Exception ex){}
        }
        
        //getting the discarded attribute names
        System.out.println("");
        while(true){
            try{
                System.out.print("The dataset contains following columns ~ ");
                for(int i = 0; i < allColumnHeaders.length; i++)
                    System.out.print("(" + (i+1) + ")" + allColumnHeaders[i] + " ");
                System.out.print("\nDo you want to discard any of them? If yes enter their indexes separated by comma (eg. 1,2,3), press only enter otherwise: ");
                String p_discardedAttributesStr = reader.readLine();
                try{
                    if(!p_discardedAttributesStr.equals("")){
                        StringTokenizer st = new StringTokenizer(p_discardedAttributesStr,",");
                        while(st.hasMoreTokens()){
                            Integer disAttrIndex = Integer.parseInt(st.nextToken().trim());
                            discardedAttributes.add(allColumnHeaders[disAttrIndex -1]);
                        }
                    }
                }catch(Exception ex){
                    System.out.println("Invalid choice. Please enter your choice again.");
                    throw new Exception();
                }
                break;
            }catch(Exception ex){}
        }
        
        //getting the class attribute name
        System.out.println("");
        while(true){
            try{
                System.out.print("The dataset contains following columns ~ ");
                for(int i = 0; i < allColumnHeaders.length; i++)
                    System.out.print("(" + (i+1) + ")" + allColumnHeaders[i] + " ");
                System.out.print("\nWhich one should be considered as the class variable? Enter between 1 to "+allColumnHeaders.length+": ");
                String p_classIndex = reader.readLine();
                try{
                    Integer classIndex = Integer.parseInt(p_classIndex);
                    classAttName = allColumnHeaders[classIndex - 1];
                    if(discardedAttributes.contains(classAttName))
                        throw new Exception();
                }catch(Exception ex){
                    System.out.println("Invalid choice. Please enter your choice again.");
                    throw new Exception();
                }
                break;
            }catch(Exception ex){}
        }
        
        //Generating dataset for execution
        DataParameters dataParameters = new DataParameters(csvFilePath, csvSplitBy, csvEscapeChar, classAttName, missingValueStrings, discardedAttributes);
        Dataset processedDataset = new Dataset(dataParameters);
        
        // getting the test option from user
        System.out.println("");
        while(true){
            try{
                System.out.print("Please choose whether you want to test the prediction effectiveness by (1) spliting the dataset, or (2) k-fold cross validation: ");
                String p_test_option = reader.readLine();
                if(!(p_test_option.equals("1") || p_test_option.equals("2"))){
                    System.out.println("Invalid choice. Please enter your choice again.");
                    throw new Exception();
                }else
                    test_option = p_test_option;
                break;
            }catch(Exception ex){}
        }
        
        // getting the test parameter from user
        System.out.println("");
        while(true){
            try{
                if(test_option.equals("1")){
                    System.out.print("Please enter the split percentage of training dataset (0 - 1): ");
                    String p_test_param = reader.readLine();
                    try{
                        splitPercent = Double.parseDouble(p_test_param);
                        if(!(splitPercent > 0.0 && splitPercent < 1.0)){
                            throw new Exception();
                        }
                    }catch(Exception ex){
                        System.out.println("Invalid. Please enter again.");
                        throw new Exception();
                    }
                    
                }
                else if(test_option.equals("2")){
                    System.out.print("Please enter the number of fold (3 - 100): ");
                    String p_test_param = reader.readLine();
                    try{
                        noOfFold = Integer.parseInt(p_test_param);
                        if(!(noOfFold >= 3 && noOfFold <= 100)){
                            throw new Exception();
                        }
                    }catch(Exception ex){
                        System.out.println("Invalid. Please enter again.");
                        throw new Exception();
                    }
                }
                break;
            }catch(Exception ex){}
        }
        
        // getting the depth parameter from user
        System.out.println("");
        while(true){
            try{
                System.out.print("Please select the depth of force to be applied (between 1 to "+(processedDataset.getPredictors().size())+"): ");
                String p_depth = reader.readLine();
                try{
                    depth = Integer.parseInt(p_depth);
                    if(depth < 1 || depth > (processedDataset.getPredictors().size()))
                        throw new Exception();
                }catch(Exception ex){
                    System.out.println("Invalid. Please enter again.");
                    throw new Exception();
                }
                break;
            }catch(Exception ex){}
        }
        
        // getting the noise fraction parameter from user
        System.out.println("");
        while(true){
            try{
                System.out.print("Please select the noise fraction to remove irrelevant forces (between 0 to 1): ");
                String p_noiseFraction = reader.readLine();
                try{
                    noiseFraction = Double.parseDouble(p_noiseFraction);
                    if(noiseFraction < 0.0 || noiseFraction > 1.0)
                        throw new Exception();
                }catch(Exception ex){
                    System.out.println("Invalid. Please enter again.");
                    throw new Exception();
                }
                break;
            }catch(Exception ex){}
        }
        
        // getting number of trials from user
        System.out.println("");
        while(true){
            try{
                System.out.print("Please select the number of trials (1 or more): ");
                String p_noOfTrial = reader.readLine();
                try{
                    noOfTrial = Integer.parseInt(p_noOfTrial);
                    if(noOfTrial < 1)
                        throw new Exception();
                }catch(Exception ex){
                    System.out.println("Invalid. Please enter again.");
                    throw new Exception();
                }
                break;
            }catch(Exception ex){}
        }
        
        // getting the debug parameter from user
        System.out.println("");
        while(true){
            try{
                System.out.print("Do you want the all predictions to be printed? (enter 1 if yes 0 otherwise): ");
                String p_debug = reader.readLine();
                try{
                    Integer debugint = Integer.parseInt(p_debug);
                    if(!(debugint == 0 || debugint == 1))
                        throw new Exception();
                    else if(debugint == 0)
                        debug = false;
                    else if(debugint == 1)
                        debug = true;
                }catch(Exception ex){
                    System.out.println("Invalid. Please enter again.");
                    throw new Exception();
                }
                break;
            }catch(Exception ex){}
        }
        
        showInputParameters(allColumnHeaders, dataParameters, processedDataset.getRecords().size(), test_option, splitPercent, noOfFold, depth, noiseFraction, noOfTrial, debug);
        
        TuningParameters tuningParameters = new TuningParameters(noOfTrial, depth, noiseFraction);
        
        if(test_option.equals("1")){
            //For split percentage
            learnAndEvauateBySplittingTheDataset(processedDataset, splitPercent, dataParameters, tuningParameters, debug);
        }else if(test_option.equals("2")){
            //For k-Fold cross validation
            learnAndEvauateWithKFoldCV(processedDataset, noOfFold, dataParameters, tuningParameters, debug);
        }
    }
    
    private static void learnAndEvauateWithKFoldCV(Dataset processedDataset, Integer noOfFold, DataParameters dataParameters, TuningParameters tuningParameters, boolean debug){
        List<Prediction> allFoldPredictions = new ArrayList<Prediction>();
        // Generating folds and for each folds
        //*******************************************************************************
        Object[] trtsObj = processedDataset.getFolds(noOfFold);
        for(int i = 0; i < noOfFold; i++){
            System.out.println("Starting Fold " + (i+1));
            ArrayList<Observation> testSet = (ArrayList<Observation>)trtsObj[i];
            ArrayList<Observation> trainSet = new ArrayList<Observation>();
            for(int j = 0; j < noOfFold; j++){
                if(j != i){
                    trainSet.addAll((ArrayList<Observation>)trtsObj[j]);
                }
            }
            
            TrainingData trainingData = new TrainingData(trainSet, processedDataset.getPredictors());
            TestData testData = new TestData(testSet, processedDataset.getPredictors());

            //Training
            Classifier rndWh = new Classifier(dataParameters, tuningParameters);
            TrainingKnowledge trainingKnowledge = rndWh.train(trainingData, processedDataset.getClassLabels());
            
            //Predict
            List<Prediction> predictions = classifyAllInTestSet(testData, trainingKnowledge, trainingData.getClassLabels(), dataParameters, tuningParameters, debug);
            
            allFoldPredictions.addAll(predictions);
            
        }
        
        //evaluate
        ClassificationAssessmentArbitrator.evaluate(allFoldPredictions);
    }
    
    private static void learnAndEvauateBySplittingTheDataset(Dataset processedDataset, Double splitPercent, DataParameters dataParameters, TuningParameters tuningParameters, boolean debug){
        // Spliting CSV
        Object[] splitSets = processedDataset.split(splitPercent);
        
        TrainingData trainingData = new TrainingData((ArrayList<Observation>)splitSets[0], processedDataset.getPredictors());
        TestData testData = new TestData((ArrayList<Observation>)splitSets[1], processedDataset.getPredictors());
        
        //Training
        Classifier rndWh = new Classifier(dataParameters, tuningParameters);
        TrainingKnowledge trainingKnowledge = rndWh.train(trainingData, processedDataset.getClassLabels());
        System.out.println("Training completed. No of Factors: " + trainingKnowledge.getApplicableFactorsSortedByImportance().size());
        
        //Predict
        List<Prediction> predictions = classifyAllInTestSet(testData, trainingKnowledge, trainingData.getClassLabels(), dataParameters, tuningParameters, debug);
        
        //evaluate
        ClassificationAssessmentArbitrator.evaluate(predictions);
    }
    
    private static List<Prediction> classifyAllInTestSet(TestData testData, TrainingKnowledge trainingKnowledge, TreeSet<String> classLabels, DataParameters dataParameters, TuningParameters tuningParameters, boolean debug){
        ArrayList<Observation> testSet = testData.getDataset();
        List<Prediction> predictions = new ArrayList<Prediction>();
        
        if(debug){
            System.out.println("");
            System.out.print("#,");
            for(Predictor predictor : testData.getPredictors())
                System.out.print(predictor.getPredictorName()+",");
            System.out.print("prediction,status,confidence");
            System.out.println("");
        }
        
        //For each record in test set
        int i = 1; 
        for(Observation obs : testSet){
            String observedOutput = obs.getObservedOutput();
            UnknownObservation uobs = new UnknownObservation(obs.getObservedInput());
            Classifier rndWh = new Classifier(dataParameters, tuningParameters);
            Prediction pred = rndWh.classify(uobs, testData.getPredictors(), classLabels, trainingKnowledge);
            pred.setGoldClass(observedOutput);
            
            if(debug){
                System.out.print((i++) + ",");
                for(Map.Entry<Predictor, Object> entry: uobs.getObservedInput().entrySet()){
                    System.out.print(entry.getValue() + ",");
                }
                System.out.print(pred.getPredictedClass()+(!pred.getPredictedClass().equals(pred.getGoldClass())?",<wrong>":",<correct>")+","+pred.getPredictionConfidence()+"\n");
            }
            
            predictions.add(pred);
            
        }
        return predictions;
    }
    
    private static void showInputParameters(
            String[] allColumnHeaders
            , DataParameters dp
            , Integer dataSize 
            , String test_option
            , Double splitPercent
            , Integer noOfFold
            , Integer depth
            , Double noiseFraction
            , Integer noOfTrial
            , boolean debug){
        System.out.println("\nInput parameters\n==================");
        
        System.out.println("Data file: " + dp.getCsvFilePath());
        String discardedAttrStr = "";
        Iterator it = dp.getDiscardedAttributes().iterator();
        while(it.hasNext()){
            if(discardedAttrStr.equals(""))
                discardedAttrStr += ((String)it.next());
            else
                discardedAttrStr += (", " + (String)it.next());
        }
        System.out.println("Discarded attributes: " + (discardedAttrStr.equals("")?"<none>":discardedAttrStr));
        System.out.print("No of attributes: " + (allColumnHeaders.length - dp.getDiscardedAttributes().size() - 1) + " {");
        for(int i = 0; i < allColumnHeaders.length; i++)
            if(!dp.getDiscardedAttributes().contains(allColumnHeaders[i]) && !allColumnHeaders[i].equals(dp.getClassAttName()))
                System.out.print(allColumnHeaders[i]+(i<(allColumnHeaders.length - 1)?",":""));
        System.out.println("}");
        System.out.println("Class variable name: " + dp.getClassAttName());
        System.out.println("Total no of records: " + dataSize);
        System.out.println("==================");
        
        String test_option_str = "";
        String test_param_str = "";
        if(test_option.equals("1")){
            test_option_str = "Split";
            test_param_str = ((100 - (splitPercent * 100)) + "% observations in the dataset");
        }else if(test_option.equals("2")){
            test_option_str = "Cross validation";
            test_param_str = (noOfFold + " folds");
        }
        System.out.println("Test option: " + test_option_str);
        System.out.println("Test using: " + test_param_str);
        System.out.println("Depth: " + depth);
        System.out.println("Noise fraction: " + noiseFraction);
        System.out.println("No of trials: " + noOfTrial);
        System.out.println("==================");
        
        System.out.println("Print Details: " + (debug?"yes":"no"));
        System.out.println("==================");
    }
}
