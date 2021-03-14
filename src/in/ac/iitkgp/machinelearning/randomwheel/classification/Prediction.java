/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.classification;

/**
 *
 * @author Anupam
 */
public class Prediction {
    
    String predictedClass;
    Double predictionConfidence;

    public Prediction(String predictedClass, Double predictionConfidence) {
        this.predictedClass = predictedClass;
        this.predictionConfidence = predictionConfidence;
    }
    
    String goldClass;

    public String getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(String predictedClass) {
        this.predictedClass = predictedClass;
    }

    public Double getPredictionConfidence() {
        return predictionConfidence;
    }

    public void setPredictionConfidence(Double predictionConfidence) {
        this.predictionConfidence = predictionConfidence;
    }

    public String getGoldClass() {
        return goldClass;
    }

    public void setGoldClass(String goldClass) {
        this.goldClass = goldClass;
    }
    
}
