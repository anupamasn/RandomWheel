/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel;

import in.ac.iitkgp.machinelearning.randomwheel.parameters.DataParameters;
import in.ac.iitkgp.machinelearning.randomwheel.parameters.TuningParameters;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 *
 * @author Anupam
 */
public class Learn {
    public static void main(String[] args) {
        String p_test_option = "";
        String p_test_param = "";
        String p_classAttName = "";
        String p_csvFilePath = "";
        String p_noOfPass = "";
        String p_depth = "";
        String p_noiseFraction = "";
        String p_discardedAttributesStr = "";
        String p_debug = "";
        
        if(args.length < 4){
            System.out.println("Are you using basic mode? Invalid parameter set. Mandator parameter in basic mode (in order): test option, test parameter, class, csv file path");
            return;
        }else if(args.length == 4){
            p_test_option = args[0];
            p_test_param = args[1];
            p_classAttName = args[2];
            p_csvFilePath = args[3];
        }else if(args.length > 4){
            System.out.println("args.length: " + args.length);
            if(args.length == 9){
                p_test_option = args[0];
                p_test_param = args[1];
                p_classAttName = args[2];
                p_csvFilePath = args[3];
                p_noOfPass = args[4];
                p_depth = args[5];
                p_noiseFraction = args[6];
                p_discardedAttributesStr = args[7];
                p_debug = args[8];
            }else{
                System.out.println("Invalid parameter set in advanced mode. For advanced mode, all nine parameters are required");
                return;
            }
        }
        
        String test_option = "";
        if(p_test_option.equals("")){
            System.out.println("Please provide the test option.");
            return;
        }else{
            if(!(p_test_option.equals("1") || p_test_option.equals("2"))){
                System.out.println("Invalid test option: 1 for split 2 for cross validation.");
                return;
            }
            else
                test_option = p_test_option;
        }
        
        Double splitPercent = 0.7;
        Integer noOfFold = 10;
        if(p_test_param.equals("")){
            System.out.println("Please provide the test option parameter");
            return;
        }else{
            if(test_option.equals("1")){
                try{
                    splitPercent = Double.parseDouble(p_test_param);
                }catch(Exception e){
                    System.out.println("Invalid split percentage");
                    return;
                }
                if(!(splitPercent > 0.0 && splitPercent < 1.0)){
                    System.out.println("Split percentage should be > 0.0 and < 1.0");
                    return;
                }
            }else if(test_option.equals("2")){
                try{
                    noOfFold = Integer.parseInt(p_test_param);
                }catch(Exception e){
                    System.out.println("Invalid number of fold");
                    return;
                }
                if(!(noOfFold >= 3 && noOfFold <= 100)){
                    System.out.println("Number of fold should be between 3 and 100");
                    return;
                }
            }
        }
        
        String classAttName;
        if(p_classAttName.equals("")){
            System.out.println("Please provide the class attribute name");
            return;
        }else{
            classAttName = p_classAttName;
        }
        
        String csvFilePath;
        if(!p_csvFilePath.equals("")){
            csvFilePath = p_csvFilePath;
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\data.csv"; //1,3,5,lift
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\data-all.csv"; //1,3,5,lift
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\data-all-revised.csv"; //1,3,5,lift
            //forward confidence
            //lift
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\UCI\\car.csv"; //2,6,false,lift
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\vote.csv"; //1,8,false,lift
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\monks.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\mushroom.csv"; //1,4,false,lift

            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\balance-scale.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\zoo.csv"; //1,4,false,lift
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\connect-4.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\nursery.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\UCI\\chess.csv";
            csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\credit_fruad.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\soybean.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\BNG-CMC.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\UCI\\tic-tac-toe.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\UCI\\breast-cancer.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\contact-lenses.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\primary-tumor.csv";
            //csvFilePath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Datasets\\lymphography.csv";

            //csvFilePath = "/home/erp/data-all-revised.csv"; //1,3,5,lift
            //csvFilePath = "/home/erp/vote.csv";
        }else{
            System.out.println("Please provide the input file path");
            return;
        }
        
        Integer noOfPass;
        if(p_noOfPass.equals("")){
            noOfPass = 100;
        }else{
            try{
                noOfPass = Integer.parseInt(p_noOfPass);
            }catch(Exception e){
                System.out.println("Invalid number of trial");
                return;
            }
        }
        
        Integer depth;
        if(p_depth.equals("")){
            depth = 0;
        }else{
            try{
                depth = Integer.parseInt(p_depth);
            }catch(Exception e){
                System.out.println("Invalid depth");
                return;
            }
        }
        
        Double noiseFraction;
        if(p_noiseFraction.equals("")){
            noiseFraction = 0.5;
        }else{
            try{
                noiseFraction = Double.parseDouble(p_noiseFraction);
            }catch(Exception e){
                System.out.println("Invalid noise fraction");
                return;
            }
        }
        
        HashSet<String> discardedAttributes = new HashSet<String>();
        if(!p_discardedAttributesStr.equals("")){
            String discardedAttributesStr = p_discardedAttributesStr;
            if(!discardedAttributesStr.equals("")){
                StringTokenizer st = new StringTokenizer(discardedAttributesStr,",");
                while(st.hasMoreTokens()){
                    discardedAttributes.add(st.nextToken().trim());
                }
            }
        }
        
        boolean debug = false;
        if(p_debug.equals("")){
            debug = false;
        }else{
            if(!(p_debug.equalsIgnoreCase("y") || p_debug.equalsIgnoreCase("n"))){
                System.out.println("Invalid debug option");
                return;
            }else{
                if(p_debug.equalsIgnoreCase("y"))
                    debug = true;
                else if(p_debug.equalsIgnoreCase("n"))
                    debug = false;
            }  
        }
        
        /*Integer depth = 2;
        Double noiseFraction = 0.8;
        //String classAttName = "PassFail";
        //String classAttName = "Impr1";
        String classAttName = "Impr2";
        //String classAttName = "class";
        HashSet<String> discardedAttributes = new HashSet<String>();
        discardedAttributes.add("SlNo");
        discardedAttributes.add("Rollno");
        discardedAttributes.add("Time");
        discardedAttributes.add("Subno");
        discardedAttributes.add("PassFail");
        discardedAttributes.add("Impr1");
        //discardedAttributes.add("Impr2");
        discardedAttributes.add("ImprFactor");
        //discardedAttributes.add("no_of_earlier_failure");
        //discardedAttributes.add("no_of_earlier_negative");
        //discardedAttributes.add("no_of_earlier_positive");
        boolean debug = false;
        */
        
        String csvSplitBy = ",";
        String csvEscapeChar = "\"";
        HashSet<String> missingValueStrings = new HashSet<String>();
        missingValueStrings.add("");
        missingValueStrings.add("?");
        
        showInputParameters(test_option, splitPercent, noOfFold, classAttName, csvFilePath, noOfPass, depth, noiseFraction, discardedAttributes, debug);
        
        TuningParameters tp = new TuningParameters(noOfPass, depth, noiseFraction);
        
        if(test_option.equals("1")){
            //For split percentage
            DataParameters dp = new DataParameters(csvFilePath, csvSplitBy, csvEscapeChar, classAttName, missingValueStrings, discardedAttributes, splitPercent);
            RandomWheel rndWh = new RandomWheel(dp, tp, debug);
            rndWh.initiateLearingOnSplitPercent();
        }else if(test_option.equals("2")){
            //For k-Fold cross validation
            DataParameters dp = new DataParameters(csvFilePath, csvSplitBy, csvEscapeChar, classAttName, missingValueStrings, discardedAttributes, noOfFold);
            RandomWheel rndWh = new RandomWheel(dp, tp, debug);
            rndWh.initiateLearingOnKFoldCV();
        }
    }
    
    private static void showInputParameters(String test_option
            , Double splitPercent
            , Integer noOfFold
            , String classAttName
            , String csvFilePath
            , Integer noOfPass
            , Integer depth
            , Double noiseFraction
            , HashSet<String> discardedAttributes
            , boolean debug){
        System.out.println("Input parameters\n==================");
        String test_option_str = "";
        String test_param_str = "";
        if(test_option.equals("1")){
            test_option_str = "Split";
            test_param_str = ((splitPercent * 100) + "% for training dataset");
        }else if(test_option.equals("2")){
            test_option_str = "Cross validation";
            test_param_str = (noOfFold + " folds");
        }
        System.out.println("Test option: " + test_option_str);
        System.out.println("Test parameter: " + test_param_str);
        System.out.println("Class: " + classAttName);
        System.out.println("Data file: " + csvFilePath);
        
        System.out.println("==================");
    }
}