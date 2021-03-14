/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.data;

import java.util.HashSet;

/**
 *
 * @author Anupam
 */
public final class DataParameters {
    //data related parameters
    String csvFilePath = "";
    String csvSplitBy = "";
    String csvEscapeChar = "";
    String classAttName = "";
    HashSet<String> missingValueStrings = null;
    HashSet<String> discardedAttributes = null;
    
    public DataParameters(String csvFilePath, String csvSplitBy, String csvEscapeChar, String classAttName
            , HashSet<String> missingValueStrings, HashSet<String> discardedAttributes) {
        setCsvFilePath(csvFilePath);
        setCsvSplitBy(csvSplitBy);
        setCsvEscapeChar(csvEscapeChar);
        setClassAttName(classAttName);
        setMissingValueStrings(missingValueStrings);
        setDiscardedAttributes(discardedAttributes);
    }
    

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public String getCsvSplitBy() {
        return csvSplitBy;
    }

    public void setCsvSplitBy(String csvSplitBy) {
        this.csvSplitBy = csvSplitBy;
    }

    public String getCsvEscapeChar() {
        return csvEscapeChar;
    }

    public void setCsvEscapeChar(String csvEscapeChar) {
        this.csvEscapeChar = csvEscapeChar;
    }

    public String getClassAttName() {
        return classAttName;
    }

    public void setClassAttName(String classAttName) {
        this.classAttName = classAttName;
    }

    public HashSet<String> getMissingValueStrings() {
        return missingValueStrings;
    }

    public void setMissingValueStrings(HashSet<String> missingValueStrings) {
        this.missingValueStrings = missingValueStrings;
    }

    public HashSet<String> getDiscardedAttributes() {
        return discardedAttributes;
    }

    public void setDiscardedAttributes(HashSet<String> discardedAttributes) {
        this.discardedAttributes = discardedAttributes;
    }
    
}
