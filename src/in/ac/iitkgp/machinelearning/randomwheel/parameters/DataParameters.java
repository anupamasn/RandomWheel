/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.parameters;

import java.util.HashSet;

/**
 *
 * @author Anupam
 */
public final class DataParameters {
    //data related pameters
    String csvFilePath = "";
    String csvSplitBy = "";
    String csvEscapeChar = "";
    String classAttName = "";
    HashSet<String> missingValueStrings = null;
    HashSet<String> discardedAttributes = null;
    
    Double splitPercent = null;
    Integer noOfFold = 0;
    
    public DataParameters(String csvFilePath, String csvSplitBy, String csvEscapeChar, String classAttName
            , HashSet<String> missingValueStrings, HashSet<String> discardedAttributes
            , Double splitPercent) {
        setCsvFilePath(csvFilePath);
        setCsvSplitBy(csvSplitBy);
        setCsvEscapeChar(csvEscapeChar);
        setClassAttName(classAttName);
        setMissingValueStrings(missingValueStrings);
        setDiscardedAttributes(discardedAttributes);
        setSplitPercent(splitPercent);
    }
    
    public DataParameters(String csvFilePath, String csvSplitBy, String csvEscapeChar, String classAttName
            , HashSet<String> missingValueStrings, HashSet<String> discardedAttributes
            , Integer noOfFold) {
        setCsvFilePath(csvFilePath);
        setCsvSplitBy(csvSplitBy);
        setCsvEscapeChar(csvEscapeChar);
        setClassAttName(classAttName);
        setMissingValueStrings(missingValueStrings);
        setDiscardedAttributes(discardedAttributes);
        setNoOfFold(noOfFold);
    }

    public Integer getNoOfFold() {
        return noOfFold;
    }

    public void setNoOfFold(Integer noOfFold) {
        this.noOfFold = noOfFold;
    }

    public Double getSplitPercent() {
        return splitPercent;
    }

    public void setSplitPercent(Double splitPercent) {
        this.splitPercent = splitPercent;
    }
    
    public String getClassAttName() {
        return classAttName;
    }

    public String getCsvEscapeChar() {
        return csvEscapeChar;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public String getCsvSplitBy() {
        return csvSplitBy;
    }

    public HashSet<String> getDiscardedAttributes() {
        return discardedAttributes;
    }

    public HashSet<String> getMissingValueStrings() {
        return missingValueStrings;
    }

    public void setClassAttName(String classAttName) {
        this.classAttName = classAttName;
    }

    public void setCsvEscapeChar(String csvEscapeChar) {
        this.csvEscapeChar = csvEscapeChar;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public void setCsvSplitBy(String csvSplitBy) {
        this.csvSplitBy = csvSplitBy;
    }

    public void setDiscardedAttributes(HashSet<String> discardedAttributes) {
        this.discardedAttributes = discardedAttributes;
    }

    public void setMissingValueStrings(HashSet<String> missingValueStrings) {
        this.missingValueStrings = missingValueStrings;
    }

}
