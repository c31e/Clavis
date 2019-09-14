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

    static String txtDirectory = "C:\\passwords\\";
    static Statement stmt = null;
    static Connection conn = null;

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

            if (getDatabaseList().contains(DATABASE)) {
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

    public static ArrayList getDatabaseList() {
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

        return databases;
    }

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

    public static int threadInfo(String x) {
        if (x.equals("analyze active")) {
            return analyzeExecutor.getActiveCount();
        } else if (x.equals("analyze waiting")) {
            return analyzeExecutor.getQueue().size();
        } else if (x.equals("insert active")) {
            return bulkExecutor.getActiveCount();
        } else if (x.equals("insert waiting")) {
            return bulkExecutor.getQueue().size();
        }
        return -1;
    }
////////////////////////////////////////////////////////////////////////////////
//============================================================================//
////////////////////////////////////////////////////////////////////////////////

    public static int vertifySelection(String input, int min, int max) {

        if (input == null) {
            System.out.println("Must enter input");
            return -1;
        }
        if (input.length() == 0) {
            System.out.println("Must have length");
            return -1;
        }
        if(!input.chars().allMatch(Character::isDigit)){
            System.out.println("Must be digit");
            return -1;
        }
        int inputInt = Integer.parseInt(input);
        if(inputInt>max){
            System.out.println("digit to large range["+min+","+max+"]");
            return -1;
        }
        if(inputInt<min){
            System.out.println("digit to small range["+min+","+max+"]");
            return -1;
        }
        return inputInt;
    }
    static int analyzeMaxWaitingThreads = 2;
    static ThreadPoolExecutor analyzeExecutor;
    static int analyzeMaxActiveThreads = 4;
    static int analyzeDataSentLimit = 10000;
    static Results resultClass;

    public static void analyzeTable() {
        
resultClass = null;
        ArrayList<String> tables = getTableList();

        for (int x = 0; x < tables.size(); x++) {
            System.out.println((x + 1) + ". " + tables.get(x));
        }
        System.out.print("Chose table to analyze:");

        String input = in.nextLine();
        int selection = vertifySelection(input, 1, tables.size());
        System.out.println(selection);
        if(selection==-1)return;
        
        
        
        
        resultClass = new Results();
        analyzeExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(analyzeMaxActiveThreads);

        Display display = new Display(tableSize( tables.get(selection-1)), "analyze", updateTime);
        Thread displayThread = new Thread(display);

        displayThread.start();

        String[] arr = new String[analyzeDataSentLimit];
        try {

            ResultSet rs = stmt.executeQuery("select * from " + tables.get(selection-1));
            int loopCount = 0;

            while (rs.next()) {
                arr[loopCount++] = rs.getString(1);
                if (loopCount == analyzeDataSentLimit) {

                    analyzeExecutor.submit(new BulkAnalyzer(arr, loopCount));
                    loopCount = 0;
                }
                while (threadInfo("analyze waiting") >= analyzeMaxWaitingThreads) {

                    Thread.sleep(500);
                }
            }

            analyzeExecutor.submit(new BulkAnalyzer(arr, loopCount));
            // display.increaseProgress();

            while (threadInfo("analyze active") != 0) {
                Thread.sleep(100);
            }

            //  display.increaseProgress();
            display.stopDisplay();

            displayThread.join();

        } catch (SQLException ex) {
            System.out.println("error from Main Class [2]");
            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Clavis.class.getName()).log(Level.SEVERE, null, ex);
        }
        resultClass.printResults();
    }

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
//        System.out.println(increment);
//        System.out.println(totalTime);
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
