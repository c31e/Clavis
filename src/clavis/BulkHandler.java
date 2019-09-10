package clavis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BulkHandler implements Runnable {

    private int localInvalidCount = 0;
    private final int maxQuerySize;
    private final String[] arr;
    private Connection conn;
    private Statement stmt;
    private final int last;
    private String table;

    BulkHandler(String[] arr, int last,String table) {
        this.arr = arr;
        this.maxQuerySize = Clavis.maxQuerySize;
        this.last = last;
        this.table = table;
        try {
           // this.conn = Clavis.conn;
           conn = DriverManager.getConnection("jdbc:sqlserver://"+Clavis.IP+":"+Clavis.PORT, Clavis.USERNAME,Clavis.PASSWORD);
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(BulkHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stmp(String text) {
        if (text == null) {
            return;
        }
        try {
            stmt.executeUpdate(text);
        } catch (SQLException ex) {
            Logger.getLogger(BulkHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public  void incrementCount_sync(int x) {
        Clavis.increment += x;
    }

    public  void invalidCount_sync(int x) {
        Clavis.insertInvalidCount += localInvalidCount;
    }

    @Override
    public void run() {
        String command = "insert into " + table + " (pass) Values";
        
        
        
        for(int x =0;x< arr.length;x++){
            if(valid(arr[x]) && x!=arr.length){
                command = command + "('" + arr[x] + "'),";

            }
        }
         command = command + "('" + arr[arr.length] + "');";
         
        stmp(command);
        incrementCount_sync(arr.length);

    }

    public String clean(String x) {
        x = x.replace("\'", "\'\'");
        // x = x.replace('\"', '!');
        return x;
    }
    public boolean valid(String x){
        if(x.length()>20)return false;
        if(x.length()<5)return false;
        if(!x.matches("\\A\\p{ASCII}*\\z")) return false;
        return true;
    }



}