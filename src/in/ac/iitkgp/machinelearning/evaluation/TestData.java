/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.evaluation;

import in.ac.iitkgp.machinelearning.data.Observation;
import in.ac.iitkgp.machinelearning.data.Predictor;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Anupam
 */
public class TestData {
    
    ArrayList<Predictor> predictors = null;
    ArrayList<Observation> dataset = null;

    public TestData(ArrayList<Observation> testDataset, ArrayList<Predictor> predictors) {
        setDataset(testDataset);
        setPredictors(predictors);
    }

    public ArrayList<Predictor> getPredictors() {
        return predictors;
    }

    public void setPredictors(ArrayList<Predictor> predictors) {
        this.predictors = predictors;
    }

    public ArrayList<Observation> getDataset() {
        return dataset;
    }

    public void setDataset(ArrayList<Observation> dataset) {
        this.dataset = dataset;
    }
    
}
