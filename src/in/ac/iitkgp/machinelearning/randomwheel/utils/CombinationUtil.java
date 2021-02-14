/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.iitkgp.machinelearning.randomwheel.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Anupam
 */
public class CombinationUtil {

    /* arr[]  ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Staring and Ending indexes in arr[]
    index  ---> Current index in data[]
    r ---> Size of a combination to be printed */
    static void combinationFromAnArray(String[] arr, int n, int r, int index,  String[] data, int i, ArrayList<String[]> combs)
    {
        // Current combination is ready to be printed, print it
        if (index == r)
        {
            String[] comb = new String[r];
            for (int j=0; j<r; j++)
                comb[j] = data[j];
            combs.add(comb);
            return;
        }
 
        // When no more elements are there to put in data[]
        if (i >= n)
        return;
 
        // current is included, put next at next location
        data[index] = arr[i];
        combinationFromAnArray(arr, n, r, index+1, data, i+1, combs);
 
        // current is excluded, replace it with next (Note that
        // i+1 is passed, but index is not changed)
        combinationFromAnArray(arr, n, r, index, data, i+1, combs);
    }
 
    // The main function that prints all combinations of size r
    // in arr[] of size n. This function mainly uses combinationUtil()
    public static ArrayList<String[]> getCombinationsFromAnArray(String[] arr, int r)
    {
        // A temporary array to store all combination one by one
        String[] data=new String[r];
        
        int n = arr.length;
        ArrayList<String[]> combs = new ArrayList<String[]>();
        // Print all combination using temprary array 'data[]'
        combinationFromAnArray(arr, n, r, 0, data, 0, combs);
        return combs;
    }
    
    public static <T> Set<List<T>> getCombinationsFromMultipleListsWithOneValEach(List<List<T>> lists) {
        Set<List<T>> combinations = new HashSet<List<T>>();
        Set<List<T>> newCombinations;

        int index = 0;

        // extract each of the integers in the first list
        // and add each to ints as a new list
        for(T i: lists.get(0)) {
            List<T> newList = new ArrayList<T>();
            newList.add(i);
            combinations.add(newList);
        }
        
        index++;
        while(index < lists.size()) {
            List<T> nextList = lists.get(index);
            newCombinations = new HashSet<List<T>>();
            for(List<T> first: combinations) {
                for(T second: nextList) {
                    List<T> newList = new ArrayList<T>();
                    newList.addAll(first);
                    newList.add(second);
                    newCombinations.add(newList);
                }
            }
            combinations = newCombinations;

            index++;
        }

        return combinations;
    }
 
    /*Driver function to check for above function*/
    public static void main (String[] args) {
        /*String[] arr = {"1", "2", "3", "4", "5"};
        int r = 3;
        ArrayList<String[]> combs = getCombinationsFromAnArray(arr, r);
        System.out.println("combs.size: " + combs.size());
        for(int i = 0; i<combs.size(); i++){
            String[] comb = combs.get(i);
            for(int j=0; j<comb.length; j++){
                System.out.print(comb[j] + " ");
            }
            System.out.println("");
        }*/
        
        ArrayList a1 = new ArrayList();
        ArrayList a2 = new ArrayList();
        a2.add("1");
        a2.add("2");
        a2.add("3");
        a2.add("4");
        a2.add("5");
        a1.add("A");
        a1.add("1");
        a1.add("C");
        List<List<String>> Lists = new ArrayList<List<String>>();
        Lists.add(a1);
        Lists.add(a2);
        Set<List<String>> comb = getCombinationsFromMultipleListsWithOneValEach(Lists);
        System.out.println(comb.size());
        Iterator combIt = comb.iterator();
        while(combIt.hasNext()){
            ArrayList lst = (ArrayList)combIt.next();
            System.out.println(lst.size());
        }
    }

}
