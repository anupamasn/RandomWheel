/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel;

import in.ac.iitkgp.machinelearning.randomwheel.utils.StatUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

/**
 *
 * @author Anupam
 */
public class MeasureResultantForcesInSinglePass implements Callable<Double[]> {
    String[] filteredForceKeys;
    boolean debug;
    ArrayList<String[]> possibleAttributeValues;
    int classAttIndex;
    Object[] trainingObj;
    
    public MeasureResultantForcesInSinglePass (String[] p_filteredForceKeys
            , ArrayList<String[]> p_possibleAttributeValues
            , int p_classAttIndex
            , Object[] p_trainingObj
            , boolean p_debug) { 
        this.filteredForceKeys = p_filteredForceKeys;
        this.possibleAttributeValues = p_possibleAttributeValues;
        this.classAttIndex = p_classAttIndex;
        this.trainingObj = p_trainingObj;
        this.debug = p_debug;
    }

    public Double[] call() { 
        StatUtil su = new StatUtil(debug);
        Integer noOfFilteredForces = filteredForceKeys.length;
        Integer noOfAppliedForce = (1 + (int)(Math.random() * ((noOfFilteredForces - 1) + 1)));
        //if(debug)
        //    System.out.println("\tPass" + "["+noOfAppliedForce+"/" + filteredForceKeys.length + " forces] ");

        Integer[] choosenForceIndexes = su.chooseBestForces(noOfFilteredForces, noOfAppliedForce);
        String[] choosenForceKeys = new String[noOfAppliedForce];
        int cnt = 0;
        for(int j=0; j < choosenForceIndexes.length; j++){
            if(choosenForceIndexes[j] == 1){
                choosenForceKeys[cnt] = filteredForceKeys[j];
                cnt++;
            }
        }

        Double[] resultantForceStrengths = getResultantForces(choosenForceKeys, possibleAttributeValues, classAttIndex, trainingObj);
        
        return resultantForceStrengths;
    }
    
    private Double[] getResultantForces(String[] choosenForceKeys, ArrayList<String[]> possibleAttributeValues, int classAttIndex, Object[] trainingObj){
        Double[] resultantForces = new Double[possibleAttributeValues.get(classAttIndex).length];
        //For each possible class values
        for(int i = 0; i < possibleAttributeValues.get(classAttIndex).length; i++){
            resultantForces[i] = 0.0;
            String classValue = possibleAttributeValues.get(classAttIndex)[i];
            //For each predictor attributes
            //if(debug)
            //    System.out.println("\t\tWheel for class > "+classValue);
            for(int j = 0; j < choosenForceKeys.length; j++){
                try{
                    String p_jpKey = choosenForceKeys[j];
                    Double p_force = calculateForce(p_jpKey, possibleAttributeValues, classAttIndex, trainingObj, i);
                    resultantForces[i] += p_force;
                    //if(debug)
                    //    System.out.println("\t\t\t["+p_jpKey+"|"+(((Map<String, Double>)trainingObj[2]).get(p_jpKey))+"]"+p_force);
                    
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            
            //if(debug)
            //    System.out.println("\t\t"+possibleAttributeValues.get(classAttIndex)[i]+":"+resultantForces[i]);
        }
        
        return resultantForces;
    }
    
    private Double calculateForce(String forceJPKey, ArrayList<String[]> possibleAttributeValues, int classAttIndex, Object[] trainingObj, Integer classIndex){
        try{
            Integer force_class_value_count = ((Map<String, Integer[]>)trainingObj[0]).get(forceJPKey)[classIndex];
            Integer force_class_values_sum = StatUtil.getSum(((Map<String, Integer[]>)trainingObj[0]).get(forceJPKey));
            Double scaledGiniCoeffofForce = ((Map<String, Double>)trainingObj[1]).get(forceJPKey);
            Double attrImpOfForce = ((Map<String, Double>)trainingObj[2]).get(forceJPKey);
            Integer class_value_counts = ((Map<String, Integer>)trainingObj[3]).get(possibleAttributeValues.get(classAttIndex)[classIndex]);
            Integer no_of_training_records = (Integer)trainingObj[4];
            Integer no_of_attr_comb = (new StringTokenizer(forceJPKey, ",")).countTokens();
            
            //System.out.println("\t\t\t"+force_class_value_count+"|"+force_class_values_sum+"|"+scaledGiniCoeffofForce+"|"+class_value_counts+"|"+no_of_training_records);
            
            //x is the forceJPKey here whreas y is the class value
            Double sup_xy = ((double)force_class_value_count / (double)no_of_training_records);
            Double sup_x = ((double)force_class_values_sum / (double)no_of_training_records);
            Double sup_y = ((double)class_value_counts/(double)no_of_training_records);
            //System.out.println("\t\t\tsup_xy : " + sup_xy + ", sup_x : " + sup_x + ", sup_y : " + sup_y + ", attValWeightageComp : " + attValWeightageComp);
            //calculating force
            //Double a = (sup_xy/(sup_x));
            //Double a = (sup_xy/sup_y) - ;
            //Double a = (sup_xy/sup_y) * (sup_xy/(sup_x));
            Double a = (sup_xy/(sup_x * sup_y));
            //Double a = ratio;
            //Double a = (sup_xy - (sup_x * sup_y));
            
            Double b = scaledGiniCoeffofForce;
            //force = force * (attrImpOfForce);
            //System.out.println("\t\t\tforce: " + force);
            //if(scaledGiniCoeffofForce == 1.0)
            //    force = 99999.0;
            //else if(scaledGiniCoeffofForce == 0.0)
            //    force = -99999.0;
            
            Double force = a * b;
            
            if(force.isNaN())
                force = 0.0;
            //System.out.println("\t\ta: "+a+", b: " + b + ", c: " + c + " = " + force);
            return force;
        }catch(Exception ex){
            return 0.0;
        }
    }
}
