package clavis;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoCommit extends Thread {

    private int wait;
    private boolean proceed;

    AutoCommit(int wait) {


        this.wait = wait;
        proceed = true;
        try {
            Clavis.conn.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(AutoCommit.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
    
        try {
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

    public void stopCommiting() throws SQLException {
        proceed = false;
       Clavis.conn.commit();
       Clavis.conn.setAutoCommit(true);

    }
}
