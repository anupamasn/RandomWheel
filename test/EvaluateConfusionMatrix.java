/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import in.ac.iitkgp.machinelearning.evaluation.ConfusionMatrix;
import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author Anupam
 */
public class EvaluateConfusionMatrix {
    /*
    public static void main(String[] args) {
        String csvSplitBy = ",";
        String csvEscapeChar = "\"";
        String csvPath = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Analysis\\confusion_matrices\\soybean-RW4.csv";
        
        readConfusionMatrixFromCSV(csvPath, csvSplitBy, csvEscapeChar);
    }
    */
    public static ConfusionMatrix readConfusionMatrixFromCSV(String csvFile, String csvSplitBy, String csvEscapeChar){
        String line = "";
        String[] predicted_labels = null;
        String[] actual_labels = null;
        Integer[][] counts = null;
        //generating the predicted_labels array from the second line
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            if((line = br.readLine()) != null){
                //skip first line
            }
            if((line = br.readLine()) != null){
                line = CommonUtil.removeEscapeCharFromString(line, csvEscapeChar, csvSplitBy);
                String[] t_headers = line.split(csvSplitBy);
                predicted_labels = new String[t_headers.length - 1];
                actual_labels = new String[t_headers.length - 1];
                for(int i = 0; i < predicted_labels.length; i++)
                    predicted_labels[i] = t_headers[i+1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /*for(int i = 0; i < predicted_labels.length; i++)
            System.out.println(predicted_labels[i]);
        */
        counts = new Integer[predicted_labels.length][predicted_labels.length];
        Integer actual_labels_cnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            if((line = br.readLine()) != null){//skip first line
            }
            
            if((line = br.readLine()) != null){//skip second line
            }
            
            while ((line = br.readLine()) != null) {
                Integer predicted_labels_cnt = 0;
                line = CommonUtil.removeEscapeCharFromString(line, csvEscapeChar, csvSplitBy);
                //System.out.println("line: " + line);
                String[] t_line_arr = line.split(csvSplitBy);
                Integer t_line_arr_cnt = 0;
                actual_labels[actual_labels_cnt] = t_line_arr[t_line_arr_cnt++];
                for(; t_line_arr_cnt < t_line_arr.length; t_line_arr_cnt++){
                    //System.out.println("actual_labels_cnt: " + actual_labels_cnt + ", predicted_labels_cnt: " + predicted_labels_cnt + ", t_line_arr[t_line_arr_cnt]:" +t_line_arr[t_line_arr_cnt]);
                    counts[actual_labels_cnt][predicted_labels_cnt++] = Integer.parseInt(t_line_arr[t_line_arr_cnt]);
                }
                actual_labels_cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        ConfusionMatrix cm = new ConfusionMatrix();
        for(int i = 0; i < actual_labels.length; i++){
            for(int j = 0; j < predicted_labels.length; j++){
                cm.increaseValue(actual_labels[i], predicted_labels[j], counts[i][j]);
            }
        }
        System.out.println(cm);
        System.out.println(cm.printLabelPrecRecFm());
        System.out.println("Wt. Precision: " + cm.getWeightedPrecision());
        System.out.println("Wt. Recall: " + cm.getWeightedRecall());
        System.out.println("Wt. F-measure: " + cm.getWeightedFMeasure());
        System.out.println("Kappa: " + cm.getCohensKappa());
        return null;
    }
    
}
