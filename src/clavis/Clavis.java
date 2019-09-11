package clavis;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
// System.out.println("Created by Daniel Brewer on 6/02/19");
// find me 2073572995142c9b4def90351a19e64256bc135a

public class Clavis {
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////

    //static String main_log = "D:\\log\\main_log.txt";
    //static String backup_log = "D:\\log\\backup_log.txt";
    static String txtDirectory = "C:\\passwords\\";
    //static String dictionaryEnglishWordsDirectory = "dictionaries\\words_alpha.txt";
    //static String dictionaryEnglishLastNamesDirectory = "dictionaries\\last-names.txt";
    //static String resultsMaskLocation = "D:\\log\\maskResults.csv";
    //static String resultsNumberLocation = "D:\\log\\numberResults.csv";
    //static String resultsWordLocation = "D:\\log\\wordResults.csv";
    static Statement stmt = null;

    static Connection conn = null;

    //static Boolean proceed = true;
    static DecimalFormat largeNumberFormatter = new DecimalFormat("#,###");
    static DecimalFormat smallNumberFormatter = new DecimalFormat("##.##");

    static Scanner in;

    //static ThreadPoolExecutor pool;
    //
    //
    static int updateTime = 500;            // display update time 
    static long totalTime = 0;
    static long increment = 0;

    static String[] tableNames = {"pwd_0", "pwd_1", "pwd_2", "pwd_3", "pwd_4", "pwd_5", "pwd_6", "pwd_7", "pwd_8", "pwd_9", "pwd_a", "pwd_b", "pwd_c", "pwd_d", "pwd_e", "pwd_f"};
    static boolean connected = false;
    //
    static String databaseName;

    //
    static ThreadPoolExecutor bulkExecutor;

    //
    // amount of data given to each thread 15000 best
    static int maxActiveThreads = 30;       // how many active theads it will reach
    //
    static int maxQuerySize = 1000;         // max amount of variables sql will allow 
    static int MaxWaitingThreads = 5;     // how many it will make and add to queue to wait execution

    //SQL server variables
    static Properties properties;
    static String DATABASE = "clavis";
    static String USERNAME = "SA";
    static String PASSWORD = "qpwoei39!@";
    static String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static String IP = "192.168.1.199";
    static String PORT = "1433";

    ////////////////////////// analyze variables ///////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        System.out.println("-------------------- START UP ---------------------");

        try {
            Class.forName(DRIVER).newInstance();
            conn = DriverManager.getConnection("jdbc:sqlserver://" + IP + ":" + PORT, USERNAME, PASSWORD);
            System.out.println("Connected to Server");
            stmt = conn.createStatement();

            if (isDatabasePresent(DATABASE)) {
                System.out.println("using database \'" + DATABASE + "\'");
            } else {
                initialize();
                System.out.println(DATABASE + " database not found creating it...");
                stmt.executeUpdate("CREATE DATABASE " + DATABASE);
            }
            stmt.executeUpdate("use " + DATABASE);

        } catch (SQLServerException e) {
            System.out.println("failed to connect:\n" + IP + ":" + PORT);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println("error from Main Class [1]");
            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
        }

        in = new Scanner(System.in);

        ///////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        // url = "jdbc:sqlserver://192.168.1.199:1433;databaseName=" + databaseName;
        ///////////////////////////////////////////////////////////////////////////////////
        while (true) {
            //System.out.println("---------------------------------------------------");
            //System.out.println("-------------------------|-------------------------");
            System.out.println("---------------------- MENU -----------------------");
            //System.out.println("1. ------------- 2. ------------- 3. -------------");
            System.out.println("1)Add passwords    2)Statistics  3)Delete Table");
            System.out.println("4)Analyze table    5) view table results");
            System.out.print("Select (1-4 or exit): ");

            String input = in.nextLine();
            if (input.toLowerCase().equals("exit")) {
                break;
            } else if (!input.matches("^[0-9]+$")) {
                System.out.println("Wrong input please enter (1-7) or exit");
            } else if (input.equals("1")) {
                selectFiles();
            } else if (input.equals("2")) {
                showStatistics();
            } else if (input.equals("3")) {
                dropTable();
            } else if (input.equals("4")) {
                analyzeTable();
            } else if (input.equals("5")) {

            } else if (input.equals("6")) {

            } else if (input.equals("7")) {
                System.out.println("Created by Daniel Brewer on 6/02/19");
            } else if (input.equals("8")) {
                //analyzeDatabase();
            }
        }
        System.out.println("-------------------- GOODBYTE ---------------------");
    }

    public static boolean isDatabasePresent(String table) {
        ArrayList<String> databases = new ArrayList();
        try {
            ResultSet rs = conn.getMetaData().getCatalogs();
            while (rs.next()) {
                String temp = rs.getString("TABLE_CAT");
                //System.out.println(temp);
                databases.add(temp);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
        }

        return databases.contains(table);
    }

//    public static boolean isTablePresent(String table) {
//        ArrayList<String> databases = new ArrayList();
//        try {
//            DatabaseMetaData md = conn.getMetaData();
//            ResultSet rs = md.getTables(null, null, "%", null);
//            while (rs.next()) {
//
//                databases.add(rs.getString(3));
//
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return databases.contains(table);
//    }
    public static ArrayList getTableList() {
        ArrayList<String> tables = new ArrayList();
        try {

            ResultSet rs = conn.getMetaData().getTables(null, "dbo", "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tables;
    }

////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
    public static void initialize() {

    }
    
      public static void analyzeTable(){
        
          
          
          
    }

//    public static int threadInfo(String x) {
//        if (x.equals("analyze active")) {
//            return analyzeExecutor.getActiveCount();
//        } else if (x.equals("analyze waiting")) {
//            return analyzeExecutor.getQueue().size();
//        } else if (x.equals("insert active")) {
//            return bulkExecutor.getActiveCount();
//        } else if (x.equals("insert waiting")) {
//            return bulkExecutor.getQueue().size();
//        }
//        return -1;
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//static List<MaskObj> maskData = new ArrayList<MaskObj>();
//    static ThreadPoolExecutor analyzeExecutor;
//    static int analyzeDataSentLimit = 10000;
//    static int analyzeMaxActiveThreads = 4;
//    static int analyzeMaxWaitingThreads = 2;
//    //
//    static int analyzedTotalCount;
//    static long analyzedTotalLength;
//    //
//    //static MaskObj[] maskData;
//    static int maskDataCount;
//    //
//    static String[] maskData_allNumbers;
//    static String[] maskData_startNumbers;
//    static String[] maskData_endNumbers;
//    static int[] maskData_allNumbersIncrement;
//    static int[] maskData_startNumbersIncrement;
//    static int[] maskData_endNumbersIncrement;
//    static int maskData_allNumbersCount;
//    static int maskData_startNumbersCount;
//    static int maskData_endNumbersCount;
//    //
//
//    static int maskObjArraySize = 500000000;
//    static int maskNumberArraySize = 10000000;
//    static String[] dicWords;
//    static String[] dicLastNames;
//    static int[] dicWordsCount;
//    static int[] dicLastNamesCount;
////
//    static int countWithallWords;

//    public static void analyzeDatabase() {
//        System.out.println("--------------------- ANALYZE ---------------------");
//        //System.out.println(fileSize(dictionaryEnglishWordsDirectory));
//        //System.out.println(fileSize(dictionaryEnglishLastNamesDirectory));
//        dicWords = new String[369650];
//        dicWordsCount = new int[369650];
//
//        dicLastNames = new String[88698];
//        dicLastNamesCount = new int[88698];
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(txtDirectory + dictionaryEnglishWordsDirectory));
//            String line = in.readLine();
//            int x = 0;
//            while (line != null) {
//                if (line.length() > 2) {
//                    dicWords[x] = line.toLowerCase();
//                    x++;
//                }
//
//                line = in.readLine();
//            }
//            //
//            in = new BufferedReader(new FileReader(txtDirectory + dictionaryEnglishLastNamesDirectory));
//            line = in.readLine();
//            x = 0;
//            while (line != null) {
//                if (line.length() > 2) {
//                    dicLastNames[x] = line.toLowerCase();
//                    x++;
//                }
//
//                line = in.readLine();
//            }
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
//        }
////        for(int x =0;x<1000000;x++){
////            if(dicWords[x]==null){
////                System.out.println(x);
////                break;
////            }
////        }
////        for(int x =0;x<1000000;x++){
////            if(dicLastNames[x]==null){
////                System.out.println(x);
////                break;
////            }
////        }
//
//        Arrays.sort(dicWords, new StringLengthComparator());
//        Arrays.sort(dicLastNames, new StringLengthComparator());
////        for (int x = 0; x < dicWords.length; x++) {
////
////            if (dicWords[x].contains("testing")) {
////                System.out.println("testing " + x);
////            }
////            if (dicWords[x].contains("test")) {
////                System.out.println("test " + x + "original:" + dicWords[x]);
////            }
////
////        }
//
//        // dicWords
//        //dicLastNames
//        countWithallWords = 0;
//      //  maskData = new MaskObj[maskObjArraySize];//limit 2 billion//500000000
//        maskData_allNumbers = new String[maskNumberArraySize];
//        maskData_startNumbers = new String[maskNumberArraySize];
//        maskData_endNumbers = new String[maskNumberArraySize];
//        maskData_allNumbersIncrement = new int[maskNumberArraySize];
//        maskData_startNumbersIncrement = new int[maskNumberArraySize];
//        maskData_endNumbersIncrement = new int[maskNumberArraySize];
//        //
//
//        maskDataCount = 0;
//        analyzedTotalCount = 0;
//        analyzedTotalLength = 0;
//
//        maskData_allNumbersCount = 0;
//        maskData_startNumbersCount = 0;
//        maskData_endNumbersCount = 0;
//
//        analyzeExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(analyzeMaxActiveThreads);
//        Display display = new Display((long) 16, "analyze", updateTime);
//        Thread displayThread = new Thread(display);
//
//        displayThread.start();
//
//        try {
//            for (int x = 0; x < tableNames.length; x++) {
//                ResultSet rs = stmt.executeQuery("select * from " + tableNames[x]);
//                String[] arr = new String[analyzeDataSentLimit];
//                int loopCount = 0;
//                while (rs.next()) {
//                    arr[loopCount++] = rs.getString(1);
//                    if (loopCount == analyzeDataSentLimit) {
//                        analyzeExecutor.submit(new BulkAnalyzer2(arr, loopCount));
//                        loopCount = 0;
//                    }
//                    while (threadInfo("analyze waiting") >= analyzeMaxWaitingThreads) {
//                        Thread.sleep(500);
//                    }
//                }
//                analyzeExecutor.submit(new BulkAnalyzer2(arr, loopCount));
//                display.increaseProgress();
//            }
//            while (threadInfo("analyze active") != 0) {
//                Thread.sleep(100);
//            }
//            display.increaseProgress();
//         //   writeMaskObjArrayToCSV(maskData);
//            writeNumberArrayToCSV(maskData_allNumbers, maskData_allNumbersIncrement,
//                    maskData_startNumbers, maskData_startNumbersIncrement, maskData_endNumbers,
//                    maskData_endNumbersIncrement);
//
//            writeWordArrayToCSV(dicWords, dicWordsCount, dicLastNames, dicLastNamesCount);
//
//            display.stopDisplay();
//            displayThread.join();
//        } catch (SQLException ex) {
//            System.out.println("error from Main Class [2]");
//            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        System.out.println("\nCount: " + myFormatter.format(analyzedTotalCount));
//        System.out.println("Length: " + myFormatter2.format(analyzedTotalLength / (double) analyzedTotalCount));
//        System.out.println("All words passwords: " + myFormatter.format(countWithallWords) + " "
//                + myFormatter2.format(((countWithallWords / (double) analyzedTotalCount)) * 100) + "%");
//        System.out.println("Mask Array         :" + myFormatter2.format((maskDataCount / (double) maskObjArraySize) * 100.0) + "% full");
//        System.out.println("All numbers array  :" + myFormatter2.format((maskData_allNumbersCount / (double) maskNumberArraySize) * 100.0) + "% full");
//        System.out.println("Start numbers array:" + myFormatter2.format((maskData_startNumbersCount / (double) maskNumberArraySize) * 100.0) + "% full");
//        System.out.println("End numbers array  :" + myFormatter2.format((maskData_endNumbersCount / (double) maskNumberArraySize) * 100.0) + "% full");
//        System.out.println("Mask Data -> D:\\log\\results.csv");
//
//      //  maskData = null;
//        maskData_allNumbers = null;
//        maskData_startNumbers = null;
//        maskData_endNumbers = null;
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    static class StringLengthComparator implements Comparator<String> {
//
//        @Override
//        public int compare(String s1, String s2) {
//            return s2.length() - s1.length(); // compare length of Strings
//        }
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    public static void cleanDatabase() {
//        System.out.println("-------------------- CLEANING ---------------------");
//        Display display = new Display((long) 16, "clean", updateTime);
//        Thread displayThread = new Thread(display);
//        displayThread.start();
//        int total = 0;
//        for (String tableName : tableNames) {
//            total = total + cleanTable(tableName);
//            display.increaseProgress();
//        }
//        display.stopDisplay();
//        try {
//            displayThread.join();
//        } catch (InterruptedException ex) {
//            System.out.println("error from Main Class [3]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//        System.out.println("\n" + largeNumberFormatter.format(total) + " rows affected");
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    public static void writeMaskObjArrayToCSV(MaskObj[] arr) {
//        BufferedWriter outputWriter = null;
//        try {
//            outputWriter = new BufferedWriter(new FileWriter(resultsMaskLocation, false));
//
//            for (int x = 0; x < maskDataCount; x++) {
//                outputWriter.write(arr[x].maskValue + "," + arr[x].maskCount);
//                outputWriter.newLine();
//            }
//            outputWriter.flush();
//            outputWriter.close();
//        } catch (IOException ex) {
//            System.out.println("error from Main Class [4]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    public static void writeNumberArrayToCSV(String[] arr1, int[] arr1Count,
//            String[] arr2, int[] arr2Count, String[] arr3, int[] arr3Count) {
//
//        BufferedWriter outputWriter = null;
//        try {
//            outputWriter = new BufferedWriter(new FileWriter(resultsNumberLocation, false));
//            outputWriter.write("All numbers,count,,start numbers,count,,end numbers,count");
//            outputWriter.newLine();
//            for (int x = 0; x < maskNumberArraySize; x++) {
//                if (arr1Count[x] != 0) {
//                    outputWriter.write(arr1[x] + "," + arr1Count[x] + ",,");
//                } else {
//                    outputWriter.write(",,,");
//                }
//                if (arr2Count[x] != 0) {
//                    outputWriter.write(arr2[x] + "," + arr2Count[x] + ",,");
//                } else {
//                    outputWriter.write(",,,");
//                }
//                if (arr3Count[x] != 0) {
//                    outputWriter.write(arr3[x] + "," + arr3Count[x] + ",,");
//                } else {
//                    outputWriter.write(",,,");
//                }
//
//                outputWriter.newLine();
//                if (arr1Count[x] == 0 && arr2Count[x] == 0 && arr3Count[x] == 0) {
//                    break;
//                }
//            }
//
//            outputWriter.flush();
//            outputWriter.close();
//        } catch (IOException ex) {
//            System.out.println("error from Main Class [4]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
    ////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    public static void writeWordArrayToCSV(String[] arr1, int[] arr1Count,
//            String[] arr2, int[] arr2Count) {
//
//        BufferedWriter outputWriter = null;
//        try {
//            outputWriter = new BufferedWriter(new FileWriter(resultsWordLocation, false));
//            outputWriter.write("English Dictionary Words,count,,English Last Names,count");
//            outputWriter.newLine();
//            for (int x = 0; x < 400000; x++) {
//                if (x >= arr1Count.length) {
//                    outputWriter.write(",,,");
//                } else {
//                    outputWriter.write(arr1[x] + "," + arr1Count[x] + ",,");
//                }
//                //
//                if (x >= arr2Count.length) {
//                    outputWriter.write(",,,");
//                } else {
//                    outputWriter.write(arr2[x] + "," + arr2Count[x] + ",,");
//                }
//
//                outputWriter.newLine();
////                if (arr1Count[x] == 0 && arr2Count[x] == 0) {
////                    break;
////                }
//            }
//
//            outputWriter.flush();
//            outputWriter.close();
//        } catch (IOException ex) {
//            System.out.println("error from Main Class [4]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    public static void addLogtoFile(String x) {
//        String filename;
//        if (databaseName.equals("clavis_main")) {
//            filename = main_log;
//        } else {
//            filename = backup_log;
//        }
//        BufferedWriter outputWriter = null;
//        try {
//            outputWriter = new BufferedWriter(new FileWriter(filename, true));
//            outputWriter.write(x + "");
//            outputWriter.newLine();
//            outputWriter.flush();
//            outputWriter.close();
//        } catch (IOException ex) {
//            System.out.println("error from Main Class [4]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    public static boolean readFromLog(String x) {
//        String filename;
//        if (databaseName.equals("clavis_main")) {
//            filename = main_log;
//        } else {
//            filename = backup_log;
//        }
//        BufferedReader br;
//        try {
//            br = new BufferedReader(new FileReader(filename));
//            String st;
//            while ((st = br.readLine()) != null) {
//                if (x.equals(st)) {
//                    return true;
//                }
//            }
//        } catch (FileNotFoundException ex) {
//            System.out.println("error from Main Class [5]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            System.out.println("error from Main Class [6]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//        return false;
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    public static int cleanTable(String tableName) {
//        int result = 0;
//        String sql = "WITH cteDuplicate AS \n"
//                + "(\n"
//                + "    SELECT \n"
//                + "       pass\n"
//                + "       ,ROW_NUMBER() OVER (PARTITION BY pass ORDER BY pass) RowNumber\n"
//                + "    FROM  " + tableName + "\n"
//                + ")\n"
//                + "DELETE FROM cteDuplicate\n"
//                + "WHERE RowNumber > 1";
//        try {
//            result = stmt.executeUpdate(sql);
//        } catch (SQLException ex) {
//            System.out.println("error from Main Class [7]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//        return result;
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
    public static void showStatistics() {
        System.out.println("------------------ STATISTICS ---------------------");

        long total = 0;

        try {
            ResultSet rs = conn.getMetaData().getTables(null, "dbo", "%", new String[]{"TABLE"});

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                total = total + tableSize(tableName);
                System.out.println(tableName + " " + largeNumberFormatter.format(tableSize(tableName)));
            }

        } catch (SQLException e) {
            System.err.println("Something went wrong!");
            e.printStackTrace();
            return;
        }

        System.out.println(
                "\nTotal: " + largeNumberFormatter.format(total));
    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
    public static final String ANSI_RED = "\u001B[31m";

    public static final String RESET = "\u001B[0m";
    // public static final String BLUE_BACKGROUND = "\u001B[41m";
    //  public static final String BLACK = "\u001B[30m";

    public static void selectFiles() {
        System.out.println("---------------------- FILES ----------------------");
        File[] files = new File(txtDirectory).listFiles((dir, name) -> name.endsWith(".txt"));
        for (int x = 0; x < files.length; x++) {
            if (files[x].isFile()) {// might be unesesarry

                System.out.printf("%-25s", ((x + 1) + ". " + files[x].getName()));

                if (x % 2 == 1) {
                    System.out.println();
                }
            }
        }
        System.out.print("\nSelect file number: ");
        String input = in.next();
        input = input + in.nextLine();

        if (input.equals("all")) {

            System.out.println("------------------ BULK INSERT ALL ----------------");
            for (int x = 0; x < files.length; x++) {

                bulkInsert(files[x].getName());

//                    if (connected) {
//                        addLogtoFile(files[x].getName());
//                    }
            }
        } else if (input.matches("![0-9, /,]+")) {
            System.out.println("Wrong input please enter number");
            // TODO: work you dammit
        } else {
            System.out.println("-------------------- BULK INSERT ------------------");
            String[] values = input.split(",");
            for (int x = 0; x < values.length; x++) {
                int inputInt = Integer.parseInt(values[x]);
                if (inputInt > 0 && inputInt <= files.length) {

                    bulkInsert(files[inputInt - 1].getName());

                } else {
                    System.out.println("input out of range");
                }
            }

        }

    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////

//    public static void hashText() {
//        System.out.println("-------------------- HASH TEXT --------------------");
//        System.out.print("Please enter Password to Hash: ");
//        String input = in.next();
//        input = input + in.nextLine();
//        System.out.print(SHA1(input) + "\n");
//
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
    public static void dropTable() {
        try {
            System.out.println("----------------- ERASE ALL DATA ------------------");

            ArrayList<String> tables = getTableList();

            for (int x = 0; x < tables.size(); x++) {
                System.out.println((x + 1) + ". " + tables.get(x));

            }
            System.out.print("Chose table to delete:");
            String input = in.nextLine();
            int input2 = Integer.parseInt(input);
            //TODO: make sure input is valid

            //if(input>tables.size() && input > 0)
            stmt.executeUpdate("drop table if exists " + tables.get(input2 - 1));

        } catch (SQLException ex) {
            System.out.println("error from Main Class [9]");
        }
    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////

//    public static void findHash() {
//        long foundID = -1;
//        String foundVar = "";
//        String foundHash = "";
//        System.out.println("------------------ FINDING HASH -------------------");
//        System.out.println("Please enter SHA1 Hash to find: ");
//        String input = in.next();
//        input = input + in.nextLine();
//        String tableName = "";
//        String temp = input.substring(0, 1);
//
//        // TODO: maybe make better
//        switch (temp) {
//            case "0":
//                tableName = "pwd_0";
//                break;
//            case "1":
//                tableName = "pwd_1";
//                break;
//            case "2":
//                tableName = "pwd_2";
//                break;
//            case "3":
//                tableName = "pwd_3";
//                break;
//            case "4":
//                tableName = "pwd_4";
//                break;
//            case "5":
//                tableName = "pwd_5";
//                break;
//            case "6":
//                tableName = "pwd_6";
//                break;
//            case "7":
//                tableName = "pwd_7";
//                break;
//            case "8":
//                tableName = "pwd_8";
//                break;
//            case "9":
//                tableName = "pwd_9";
//                break;
//            case "a":
//                tableName = "pwd_a";
//                break;
//            case "b":
//                tableName = "pwd_b";
//                break;
//            case "c":
//                tableName = "pwd_c";
//                break;
//            case "d":
//                tableName = "pwd_d";
//                break;
//            case "e":
//                tableName = "pwd_e";
//                break;
//            case "f":
//                tableName = "pwd_f";
//                break;
//            default:
//                System.out.println("Error text not hexDecimal");
//                return;
//        }
//        System.out.println("Search space: " + largeNumberFormatter.format(tableSize(tableName)) + " from table " + tableName);
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////
//        Display display = new Display((long) 0, "find", updateTime);
//        Thread displayThread = new Thread(display);
//        displayThread.start();
//        ResultSet rs;
//        try {
//            rs = stmt.executeQuery("SELECT  pass FROM " + tableName + " ;");
//            int x = 0;
//            while (rs.next()) {
//                x++;
//                if (SHA1(rs.getString(1)).equals(input)) {
//                    foundID = x;
//                    foundVar = rs.getString(1);
//                    foundHash = SHA1(rs.getString(1));
//                    break;
//                }
//            }
//        } catch (SQLException ex) {
//            System.out.println("error from Main Class [10]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//        display.stopDisplay();
//        try {
//            displayThread.join();
//
//        } catch (InterruptedException ex) {
//            System.out.println("error from Main Class [11]");
//            Logger
//                    .getLogger(Clavis.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//        if (foundID != -1) {
//
//            System.out.println("\n                  ! HASH Matched !");
//            System.out.println("Password: " + foundVar + "\nIndex: " + largeNumberFormatter.format(foundID));
//            System.out.println("Hash: " + foundHash);
//
//        } else {
//
//            System.out.print("\nSHA1 hash not found :(");
//            System.out.println();
//        }
//
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
    static int export_amount = 1000;
    static int invalid_count = 0;
    static int AutoCommitWaitTime = 20000;
    //static int executeBatchEvery = 2000;

    static void bulkInsert(String file) {
        invalid_count = 0;
        increment = 0;
        long lineCount = fileLineCount(file);

        System.out.printf("%-25s %25s%n", "File: " + file, "Word count: " + largeNumberFormatter.format(lineCount));
        if (!getTableList().contains(file.substring(0, file.length() - 4))) {
            String sql = "CREATE TABLE " + file.substring(0, file.length() - 4) + " (pass VARCHAR(30));";
            executeUpdate(sql);
        }
        //
        Display display = new Display(lineCount, "insert", updateTime);
        Thread displayThread = new Thread(display);
        displayThread.start();
        AutoCommit commit = new AutoCommit(AutoCommitWaitTime);
        Thread commitThread = new Thread(commit);
        commitThread.start();
        //
        int count = 0;
        String[] exportArray = new String[export_amount];
        try {
            BufferedReader in = new BufferedReader(new FileReader(txtDirectory + file));
            String line = in.readLine();
            //
            while (line != null) {
                if (valid(line)) {
                    exportArray[count++] = clean(line);
                } else {
                    invalid_count++;
                }
                if (count == export_amount) {
                    buildBulkInsertCommand(file, count, exportArray, commit);
                    increment = increment + count;
                    exportArray = null;
                    count = 0;
                    exportArray = new String[export_amount];
                }
                line = in.readLine();
            }
            
            
            buildBulkInsertCommand(file, count, exportArray, commit);
            commit.stopCommiting();
            //TODO: make sure it will always do last commit
            commit.join();
            display.stopDisplay();
            displayThread.join();

        } catch (IOException ex) {
            Logger.getLogger(Clavis.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Clavis.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.printf("\n%-25s%26s\n", "Avg speed: " + largeNumberFormatter.format(((double) increment / (double) totalTime) * 1000.00) + "/s",
                "Invalid:" + largeNumberFormatter.format(invalid_count));
    }

    public static void buildBulkInsertCommand(String file, int count, String[] exportArray, AutoCommit commit) {
        String command = "insert into " + file.substring(0, file.length() - 4) + " (pass) Values";
        for (int x = 0; x < count - 1; x++) {
            command = command + "('" + exportArray[x] + "'),";
        }
        command = command + "('" + exportArray[count - 1] + "');";
        executeUpdate(command);

    }

    public static void executeUpdate(String command) {
        try {
            stmt.executeUpdate(command);
        } catch (SQLException ex) {
            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static boolean valid(String x) {
        if (x.length() > 30) {
            return false;
        }
        if (x.length() < 5) {
            return false;
        }
        if (!x.matches("\\A\\p{ASCII}*\\z")) {
            return false;
        }
        return true;
    }

    public static String clean(String x) {
        x = x.replace("\'", "\'\'");
        // x = x.replace('\"', '!');
        return x;
    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////

    static int tableSize(String table) {
        int size = 0;
        try {
            ResultSet rs = stmt.executeQuery("SELECT SUM (row_count)\n"
                    + "FROM sys.dm_db_partition_stats\n"
                    + "WHERE object_id=OBJECT_ID('" + table + "')   \n"
                    + "AND (index_id=0 or index_id=1)");
            rs.next();
            String columnValue = rs.getString(1);
            size = Integer.parseInt(columnValue);
            rs.close();
        } catch (NumberFormatException x) {
            System.out.println("[1]Error Table probably dosent exist, dummy");
        } catch (SQLServerException x) {
            System.out.println("error from Main Class [15]");
        } catch (SQLException e) {
            System.out.println("error from Main Class [16]");
        }
        return size;
    }

////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
    static long fileLineCount(String file) {
        Path path = Paths.get(txtDirectory + file);
        try {
            return Files.lines(path, Charset.forName("ISO-8859-1")).count();
        } catch (IOException e1) {
            System.out.println("error from Main Class [18]");
            e1.printStackTrace();
        }
        return -1;
    }

}
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////

//    static String SHA1(String message) {
//        MessageDigest digest;
//        try {
//            digest = MessageDigest.getInstance("SHA-1");
//            digest.reset();
//            digest.update(message.getBytes("utf8"));
//            String sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
//            return sha1;
//        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
//            System.out.println("error from Main Class [19]");
//            e.printStackTrace();
//        }
//        return null;
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    static synchronized void disconnect(SQLServerException x, String y) {
//        if (connected == true) {
//            System.out.println("\nServer error:" + x);
//            System.out.println(y);
//            connected = false;
//        }
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    static synchronized void insertNumber_sync(String input, int loc) {
//        try {
//
//            if (input == null) {
//                System.out.println("input==null");
//            }
//
//            switch (loc) {
//                case 1:
//                    for (int x = 0; x < Clavis.maskData_allNumbersCount; x++) {
//                        if (Clavis.maskData_allNumbers[x].equals(input)) {
//                            Clavis.maskData_allNumbersIncrement[x]++;
//                            return;
//                        }
//                    }
//                    //if not found in array insert
//                    Clavis.maskData_allNumbers[Clavis.maskData_allNumbersCount] = input;
//                    Clavis.maskData_allNumbersIncrement[Clavis.maskData_allNumbersCount] = 1;
//                    Clavis.maskData_allNumbersCount++;
//                    break;
//                case 2:
//                    for (int x = 0; x < Clavis.maskData_startNumbersCount; x++) {
//                        if (Clavis.maskData_startNumbers[x].equals(input)) {
//                            Clavis.maskData_startNumbersIncrement[x]++;
//                            return;
//                        }
//                    }
//                    //if not found in array insert
//                    Clavis.maskData_startNumbers[Clavis.maskData_startNumbersCount] = input;
//                    Clavis.maskData_startNumbersIncrement[Clavis.maskData_startNumbersCount] = 1;
//                    Clavis.maskData_startNumbersCount++;
//                    break;
//                case 3:
//                    for (int x = 0; x < Clavis.maskData_endNumbersCount; x++) {
//                        if (Clavis.maskData_endNumbers[x].equals(input)) {
//                            Clavis.maskData_endNumbersIncrement[x]++;
//                            return;
//                        }
//                    }
//                    //if not found in array insert
//                    Clavis.maskData_endNumbers[Clavis.maskData_endNumbersCount] = input;
//                    Clavis.maskData_endNumbersIncrement[Clavis.maskData_endNumbersCount] = 1;
//                    Clavis.maskData_endNumbersCount++;
//                    break;
//                default:
//                    System.out.println("error in analyzer");
//                    break;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    static synchronized void insertMask_sync(String mask) {
//        //boolean found = false;
//        for (int x = 0; x < Clavis.maskDataCount; x++) {
//            if (mask.equals(Clavis.maskData[x].maskValue)) {
//                Clavis.maskData[x].maskCount++;
//                return;
//            }
//        }
//        // if (!found) {
//        Clavis.maskData[Clavis.maskDataCount++] = new MaskObj(mask);
//        // }
//    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////
//    static synchronized void totalCount_sync(long x) {
//        Clavis.analyzedTotalCount += x;
//    }
//////////////////////////////////////////////////////////////////////////////////
////============================================================================//
//////////////////////////////////////////////////////////////////////////////////
//
//    static synchronized void totalLength_sync(long x) {
//        Clavis.analyzedTotalLength += x;
//    }
//}
