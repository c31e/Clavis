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

public class BulkAnalyzer implements Runnable {

    private final String[] arr;
    private int count;
    private long localTotalCount=0;
    private long localTotalLength =0;

    BulkAnalyzer(String[] arr, int count) {
        this.arr = arr;
        this.count = count;
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

                //String temp = generateMask(arr[x]);
                //String temp2 = arr[x];

                localTotalCount++;
                localTotalLength += arr[x].length();

                //findMatches(arr[x]);
                //findNumbers(temp,temp2);
                // Clavis.insertMask_sync(generateMask(arr[x]));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
