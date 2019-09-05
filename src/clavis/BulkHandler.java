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
    private final String[] arrLocal;
    private Connection conn;
    private Statement stmt;
    private final int last;

    BulkHandler(String[] arr, int last) {
        arrLocal = arr;
        this.maxQuerySize = Clavis.maxQuerySize;
        this.last = last;
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
        try {
            String[] pwd_0 = new String[maxQuerySize], pwd_1 = new String[maxQuerySize],
                    pwd_2 = new String[maxQuerySize], pwd_3 = new String[maxQuerySize],
                    pwd_4 = new String[maxQuerySize], pwd_5 = new String[maxQuerySize],
                    pwd_6 = new String[maxQuerySize], pwd_7 = new String[maxQuerySize],
                    pwd_8 = new String[maxQuerySize], pwd_9 = new String[maxQuerySize],
                    pwd_a = new String[maxQuerySize], pwd_b = new String[maxQuerySize],
                    pwd_c = new String[maxQuerySize], pwd_d = new String[maxQuerySize],
                    pwd_e = new String[maxQuerySize], pwd_f = new String[maxQuerySize];
            int pwd_0_count = 0, pwd_1_count = 0, pwd_2_count = 0, pwd_3_count = 0,
                    pwd_4_count = 0, pwd_5_count = 0, pwd_6_count = 0, pwd_7_count = 0,
                    pwd_8_count = 0, pwd_9_count = 0, pwd_a_count = 0, pwd_b_count = 0,
                    pwd_c_count = 0, pwd_d_count = 0, pwd_e_count = 0, pwd_f_count = 0;
            for (int y = 0; y < last; y++) {
                String x = arrLocal[y];
                if ((x.length() > 20) || (x.length() < 5) || !x.matches("\\A\\p{ASCII}*\\z")) {
                    localInvalidCount++;
                } else {
                    //TODO: make sure clean dosent change hash value!!
                    String temp = SHA1FirstByte(x);
                    switch (temp) {
                        case "0":
                            pwd_0[pwd_0_count++] = clean(x);
                            if (pwd_0_count == maxQuerySize) {
                                insert(pwd_0, "pwd_0", pwd_0_count);
                                pwd_0_count = 0;
                            }
                            break;
                        case "1":
                            pwd_1[pwd_1_count++] = clean(x);
                            if (pwd_1_count == maxQuerySize) {
                                insert(pwd_1, "pwd_1", pwd_1_count);
                                pwd_1_count = 0;
                            }
                            break;
                        case "2":
                            pwd_2[pwd_2_count++] = clean(x);
                            if (pwd_2_count == maxQuerySize) {
                                insert(pwd_2, "pwd_2", pwd_2_count);
                                pwd_2_count = 0;
                            }
                            break;
                        case "3":
                            pwd_3[pwd_3_count++] = clean(x);
                            if (pwd_3_count == maxQuerySize) {
                                insert(pwd_3, "pwd_3", pwd_3_count);
                                pwd_3_count = 0;
                            }
                            break;
                        case "4":
                            pwd_4[pwd_4_count++] = clean(x);
                            if (pwd_4_count == maxQuerySize) {
                                insert(pwd_4, "pwd_4", pwd_4_count);
                                pwd_4_count = 0;
                            }
                            break;
                        case "5":
                            pwd_5[pwd_5_count++] = clean(x);
                            if (pwd_5_count == maxQuerySize) {
                                insert(pwd_5, "pwd_5", pwd_5_count);
                                pwd_5_count = 0;
                            }
                            break;
                        case "6":
                            pwd_6[pwd_6_count++] = clean(x);
                            if (pwd_6_count == maxQuerySize) {
                                insert(pwd_6, "pwd_6", pwd_6_count);
                                pwd_6_count = 0;
                            }
                            break;
                        case "7":
                            pwd_7[pwd_7_count++] = clean(x);
                            if (pwd_7_count == maxQuerySize) {
                                insert(pwd_7, "pwd_7", pwd_7_count);
                                pwd_7_count = 0;
                            }
                            break;
                        case "8":
                            pwd_8[pwd_8_count++] = clean(x);
                            if (pwd_8_count == maxQuerySize) {
                                insert(pwd_8, "pwd_8", pwd_8_count);
                                pwd_8_count = 0;
                            }
                            break;
                        case "9":
                            pwd_9[pwd_9_count++] = clean(x);
                            if (pwd_9_count == maxQuerySize) {
                                insert(pwd_9, "pwd_9", pwd_9_count);
                                pwd_9_count = 0;
                            }
                            break;
                        case "a":
                            pwd_a[pwd_a_count++] = clean(x);
                            if (pwd_a_count == maxQuerySize) {
                                insert(pwd_a, "pwd_a", pwd_a_count);
                                pwd_a_count = 0;
                            }
                            break;
                        case "b":
                            pwd_b[pwd_b_count++] = clean(x);
                            if (pwd_b_count == maxQuerySize) {
                                insert(pwd_b, "pwd_b", pwd_b_count);
                                pwd_b_count = 0;
                            }
                            break;
                        case "c":
                            pwd_c[pwd_c_count++] = clean(x);
                            if (pwd_c_count == maxQuerySize) {
                                insert(pwd_c, "pwd_c", pwd_c_count);
                                pwd_c_count = 0;
                            }
                            break;
                        case "d":
                            pwd_d[pwd_d_count++] = clean(x);
                            if (pwd_d_count == maxQuerySize) {
                                insert(pwd_d, "pwd_d", pwd_d_count);
                                pwd_d_count = 0;
                            }
                            break;
                        case "e":
                            pwd_e[pwd_e_count++] = clean(x);
                            if (pwd_e_count == maxQuerySize) {
                                insert(pwd_e, "pwd_e", pwd_e_count);
                                pwd_e_count = 0;
                            }
                            break;
                        case "f":
                            pwd_f[pwd_f_count++] = clean(x);
                            if (pwd_f_count == maxQuerySize) {
                                insert(pwd_f, "pwd_f", pwd_f_count);
                                pwd_f_count = 0;
                            }
                            break;
                        default:
                            System.out.println("Error in BulkHandler [1]");
                            break;
                    }

                }
            }

            insert(pwd_0, "pwd_0", pwd_0_count);
            insert(pwd_1, "pwd_1", pwd_1_count);
            insert(pwd_2, "pwd_2", pwd_2_count);
            insert(pwd_3, "pwd_3", pwd_3_count);
            insert(pwd_4, "pwd_4", pwd_4_count);
            insert(pwd_5, "pwd_5", pwd_5_count);
            insert(pwd_6, "pwd_6", pwd_6_count);
            insert(pwd_7, "pwd_7", pwd_7_count);
            insert(pwd_8, "pwd_8", pwd_8_count);
            insert(pwd_9, "pwd_9", pwd_9_count);
            insert(pwd_a, "pwd_a", pwd_a_count);
            insert(pwd_b, "pwd_b", pwd_b_count);
            insert(pwd_c, "pwd_c", pwd_c_count);
            insert(pwd_d, "pwd_d", pwd_d_count);
            insert(pwd_e, "pwd_e", pwd_e_count);
            insert(pwd_f, "pwd_f", pwd_f_count);

            stmt.closeOnCompletion();
            conn.close();
        } catch (Exception ex) {
            System.out.println("\n=====");
            System.out.println(ex);
            System.out.println("=====");
//            for (String x : arr) {
//                if (x == null) {
//                    System.out.print("null  ");
//                } else {
//                    System.out.print(x + "  ");
//                }
//
//            }
            System.out.println();
            System.out.println("=====");
            ex.printStackTrace();
            System.out.println("=====");
            Logger.getLogger(BulkHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("=====");
        }

        invalidCount_sync(localInvalidCount);
    }

    public String clean(String x) {
        x = x.replace("\'", "\'\'");
        // x = x.replace('\"', '!');
        return x;
    }

    public void insert(String[] arr, String table, int count) {
        if (count == 0) {
            return;
        }
        try {
            incrementCount_sync(count);

            if (count == 1) {
                String command = "insert into " + table + " (pass) Values" + "('" + arr[0] + "');";
                stmp(command);
            } else {
                String command = "insert into " + table + " (pass) Values";
                for (int y = 0; y < count - 1; y++) {
                    command = command + "('" + arr[y] + "'),";
                }
                command = command + "('" + arr[count - 1] + "');";
                stmp(command);
            }
        } catch (Exception e) {
            System.out.println("Error in insert" + e);
            System.out.println(arr + " " + count);
            e.printStackTrace();
        }

    }

    public static String SHA1FirstByte(String message) {
        try {
            return String.format("%02x", MessageDigest.getInstance("SHA-1")
                    .digest(message.getBytes())[0]).substring(0, 1);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BulkHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}