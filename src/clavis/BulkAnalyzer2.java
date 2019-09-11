package clavis;

import com.sun.tools.javac.util.StringUtils;

class MaskObj {

    String maskValue;
    int maskCount;

    MaskObj(String maskValue) {
        this.maskValue = maskValue;
        this.maskCount = 1;
    }

}

public class BulkAnalyzer2 implements Runnable {

    private final String[] arr;
    private int count;
    long localTotalCount;
    long localTotalLength;

    BulkAnalyzer2(String[] arr, int count) {
        this.arr = arr;
        this.count = count;
        localTotalCount = 0;
        localTotalLength = 0;
    }

//    public synchronized void addMask_sync(String mask) {
//        Clavis.maskData.add(new MaskObj(mask));
//    }
//
//
//    private synchronized MaskObj maskDataGet_sync(int x) {
//        return Clavis.maskData.get(x);
//    }
//
//    private synchronized int maskDataSize_sync() {
//        return Clavis.maskData.size();
//    }
    @Override
    public void run() {
        try {
            for (int x = 0; x < count; x++) {

                String temp = generateMask(arr[x]);
                String temp2 = arr[x];

                localTotalCount++;
                localTotalLength += arr[x].length();

                findMatches(arr[x]);
                findNumbers(temp,temp2);
                 Clavis.insertMask_sync(generateMask(arr[x]));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Clavis.totalLength_sync(localTotalLength);
        Clavis.totalCount_sync(localTotalCount);
    }

    public void findNumbers(String temp, String temp2) {
        if (isNumeric(temp)) {
            Clavis.insertNumber_sync(temp2, 1);
        } else {
            if (temp.substring(0, 1).equals("d")) {
                for (int y = 0; y < temp.length(); y++) {
                    if (!temp.substring(y, y + 1).equals("d")) {
                        //String temp2 = ;
                        Clavis.insertNumber_sync(temp2.substring(0, y), 2);
                        break;
                    }
                }
            }
            if (temp.substring(temp.length() - 1, temp.length()).equals("d")) {
                for (int y = temp.length(); y > 0; y--) {
                    if (!temp.substring(y - 1, y).equals("d")) {
                        // String temp2 = 
                        try {
                            Clavis.insertNumber_sync(temp2.substring(y, temp.length()), 3);
                        } catch (StringIndexOutOfBoundsException ex) {
                            // System.out.println("Mask:" + temp + " arr[x]:" + temp2 + "x:" + x);
                        }
                        break;
                    }
                }
            }

        }

    }

    public void findMatches(String input) {
        input = input.toLowerCase();
        String input2 = input;

        for (int x = 0; x < Clavis.dicWords.length; x++) {
            if (input.contains(Clavis.dicWords[x])) {
                Clavis.dicWordsCount[x]++;
                input = input.replace(Clavis.dicWords[x], " ");
            }
        }
        //
        for (int x = 0; x < Clavis.dicLastNames.length; x++) {
            if (input2.contains(Clavis.dicLastNames[x])) {
                Clavis.dicLastNamesCount[x]++;
                input2 = input.replace(Clavis.dicLastNames[x], " ");
            }
        }
        if(!generateMask(input).matches("[lu]+")){
            //all upper and lower letters belong to words
               Clavis.countWithallWords++;
        }
//        if(!generateMask(input2).matches("[lu]+")){
//            //all upper and lower letters belong to words
//            Clavis.countWithallWords++;
//        }
        
        

    }

    public static boolean isNumeric(String mask) {
        for (int x = 0; x < mask.length(); x++) {
            if (!mask.substring(x, x + 1).equals("d")) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0) {
            return false; // Empty string is contained
        }
        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp) {
                continue;
            }

            if (src.regionMatches(true, i, what, 0, length)) {
                return true;
            }
        }

        return false;
    }

    public static String generateMask(String input) {
        String returnVar = "";
        // ~!@#$%^&*()_+`-={}|:"<>?,./;'[]\
        for (int x = 0; x < input.length(); x++) {//
            char temp = input.charAt(x);
            if (Character.isDigit(temp)) {
                returnVar = returnVar + "d";
            } else if (Character.isLowerCase(temp)) {
                returnVar += "l";
            } else if (Character.isUpperCase(temp)) {
                returnVar += "u";
            } else {
                returnVar += "s";
            }
        }
        return returnVar;
    }
}
