/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import in.ac.iitkgp.machinelearning.utils.CommonUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Anupam
 */
public class EvaluateConfidenceMatrix {
    /*
    public static void main(String[] args) {
        String csvSplitBy = ",";
        String csvEscapeChar = "\"";
        String csvFile_confusion = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Analysis\\confusion_matrices\\soybean-RW4.csv";
        String csvPath_confidence = "D:\\Personal\\My Files\\Academics\\PhD\\Work\\07. Performance Prediction\\BendingMoment\\Analysis\\confusion_matrices\\soybean-RW4-confidence.csv";
        
        evaluateConfidenceMatrix(csvFile_confusion, csvPath_confidence, csvSplitBy, csvEscapeChar);
    }
    */
    public static void evaluateConfidenceMatrix(String csvFile_confusion, String csvFile_confidence, String csvSplitBy, String csvEscapeChar){
        String line = "";
        String[] predicted_labels = null;
        String[] actual_labels = null;
        Integer[][] counts = null;
        Integer[][] confidences = null;
        //generating the predicted_labels array from the second line
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile_confusion))) {
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
        //reading confusion matrix to build counts[][]
        counts = new Integer[predicted_labels.length][predicted_labels.length];
        Integer actual_labels_cnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile_confusion))) {
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
        
        //reading confusion matrix to build counts[][]
        confidences = new Integer[predicted_labels.length][predicted_labels.length];
        actual_labels_cnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile_confidence))) {
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
                    confidences[actual_labels_cnt][predicted_labels_cnt++] = Integer.parseInt(t_line_arr[t_line_arr_cnt]);
                }
                actual_labels_cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Integer cum_correct_confidence = 0;
        Integer cum_wrong_confidence = 0;
        for(int i = 0; i < actual_labels.length; i++){
            for(int j = 0; j < predicted_labels.length; j++){
                if(i==j)
                    cum_correct_confidence += (confidences[i][j] * counts[i][j]);
                else
                    cum_wrong_confidence += (confidences[i][j] * counts[i][j]);
            }
        }
        
        Integer tot_correct_count = 0;
        Integer tot_wrong_count = 0;
        for(int i = 0; i < actual_labels.length; i++){
            for(int j = 0; j < predicted_labels.length; j++){
                if(i==j)
                    tot_correct_count += counts[i][j];
                else
                    tot_wrong_count += counts[i][j];
            }
        }
        
        
        //System.out.println("tot_correct_count: " + tot_correct_count);
        //System.out.println("tot_wrong_count: " + tot_wrong_count);
        
        //System.out.println("cum_correct_confidence: " + cum_correct_confidence);
        //System.out.println("cum_wrong_confidence: " + cum_wrong_confidence);
        
        System.out.println("Avg Correct Confidence: " + ((double)cum_correct_confidence/(double)tot_correct_count));
        System.out.println("Avg Wrong Confidence: " + ((double)cum_wrong_confidence/(double)tot_wrong_count));
        System.out.println("Diff: " + (((double)cum_correct_confidence/(double)tot_correct_count) - ((double)cum_wrong_confidence/(double)tot_wrong_count)));
        
    }
}
