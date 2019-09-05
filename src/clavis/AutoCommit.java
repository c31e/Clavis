package clavis;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoCommit extends Thread {

    int wait;
    boolean proceed;
    AutoCommit(int wait) {
        this.wait = wait;
        proceed = true;

    }

    @Override
    public void run() {
        try {
            Clavis.conn.setAutoCommit(false);
            while (proceed) {
                Thread.sleep(wait);
                Clavis.conn.commit();
            }
        } catch (SQLException ex) {
            System.out.println("error from autoCommit Class [1]");
            Logger.getLogger(AutoCommit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            System.out.println("error from autoCommit Class [2]");
            Logger.getLogger(AutoCommit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void stopCommiting(){
        proceed = false;
        try {
            Thread.sleep(1000);
            Clavis.conn.commit();
            Clavis.conn.setAutoCommit(true);
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoCommit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AutoCommit.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        
    }
}
