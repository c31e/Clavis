/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clavis;

public class Results {
    static long totalLengthSum=0;
    static long totalAnalyzedCount = 0;
    Results(){
        
        
        
    }
    
    
    public static void printResults(){
        System.out.println("\nTotal Analyzed count:"+totalAnalyzedCount);
        System.out.println("Total length count:"+totalLengthSum);
        System.out.println("length/count:"+(totalLengthSum/totalAnalyzedCount));
    }
    
    
    
    
    
    
    
}
