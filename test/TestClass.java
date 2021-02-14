/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Anupam
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;  

public class TestClass {
    public static void main(String[] args) {
        /*ArrayList a1 = new ArrayList();
        ArrayList a2 = new ArrayList();
        a2.add("1");
        a2.add("2");
        a2.add("3");
        a2.add("4");
        a2.add("5");
        a1.add("A");
        a1.add("B");
        a1.add("C");
        List<List<String>> Lists = new ArrayList<List<String>>();
        Lists.add(a1);
        Lists.add(a2);
        System.out.println("Lists.size: " + Lists.size());
        List<String> Result = new ArrayList<String>();
        String current = "";
        GeneratePermutations(Lists, Result, 0, current);
        System.out.println("");*/
        Integer randomPassword = (10000000 + (int)(Math.random() * ((99999999 - 10000000) + 1)));
        System.out.println("randomPassword: " + randomPassword.toString());
    }
    
    static void GeneratePermutations(List<List<String>> Lists, List<String> result, int depth, String current)
    {
        if(depth == Lists.size())
        {
           result.add(current);
           return;
         }

        for(int i = 0; i < Lists.get(depth).size(); ++i)
        {
            GeneratePermutations(Lists, result, depth + 1, current + Lists.get(depth).get(i));
        }
    }
}
