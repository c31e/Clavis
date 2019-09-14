/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clavis;


 class charObj {

   private char charValue;
   private int charCount;

     charObj() {
        //this.charValue = maskValue;
        //this.charCount = 1;
    }
    synchronized void addObj(char charValue){
        this.charValue = charValue;
        charCount = 1;
        
        
    }
    synchronized void incrementCount(){
        charCount++;
    }
    synchronized char getCharValue(){
        return charValue;
    }
    synchronized int getCharCount(){
        return charCount;
    }
    
    
    
    
    
    

}




public class Results {
     static charObj[] charArray = new charObj[100];
    static long totalLengthSum=0;
    static long totalAnalyzedCount = 0;
    
    
    Results(){
        
        
        
    }
    
    
    public static void printResults(){
        System.out.println("\nTotal Analyzed count:"+totalAnalyzedCount);
        System.out.println("Total length count:"+totalLengthSum);
        System.out.println("length/count:"+((float)totalLengthSum/(float)totalAnalyzedCount));
        long total = 0;
        for(int x =0;x<charArray.length;x++){
            if(charArray[x]==null)break;
            System.out.println((x+1)+"). "+charArray[x].getCharValue()+"  "+charArray[x].getCharCount());
            total += charArray[x].getCharCount();
        }
         System.out.println("total:"+total);
    }
    
    
    
    
    
    
    
}
