package clavis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Display extends Thread {

    static long totalCount = 0;
    private static int star = 0;
    private final String mode;
    private final int updateTime;
    private boolean proceed;
    static long lastIncrement = 0;
    static long startTime = 0;
    private static String clear = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
    // private long lastIncrement =0;

    Display(long totalCount, String mode, int updateTime) {
        this.totalCount = totalCount;
        proceed = true;
        this.mode = mode;
        this.updateTime = updateTime;
        this.setPriority(10);

    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        try {

            switch (mode) {
                case "insert":
                    while (proceed) {
                        percentage();
                        lastIncrement = Clavis.increment;
                        Thread.sleep(updateTime);
                    }
                    percentage();
                    Clavis.totalTime = System.currentTimeMillis() - startTime;
                    break;
                case "analyze":
                    while (proceed) {
                     
                        percentage();
                        lastIncrement = Clavis.increment;
                        Thread.sleep(updateTime);
                    }
               
                    percentage();

                    break;
                default:
                    System.out.println("unknown display mode");
                    break;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopDisplay() {
        proceed = false;
    }

    public static void percentage() {
        long currentIncrement = Clavis.increment;
        System.out.print(clear);
        int y = (int) (((double) currentIncrement / (double) totalCount) * 100);
        String percentage = "";
        for (int z = 0; z < y / 5; z++) {
            percentage = percentage + "#";
        }

        double temp = ((currentIncrement - lastIncrement) * (1000.0 / Clavis.updateTime));
        System.out.printf("[%20s] %3s%% %6s/s %10s", percentage, y, (int) temp, timeConvert(System.currentTimeMillis() - startTime));
        if (star == 0) {
            System.out.print(" ");
            star++;
        } else {
            System.out.print("   ");
            star = 0;
        }
    }

    static String timeConvert(long input) {
        double seconds = (double) input / 1000.0;
        if (seconds == Double.MAX_VALUE) {
            return "Exceed max value, AKA this will take more than 69yrs";//68y 241d 14h 26m 8.0s
        }
        NumberFormat formatter3 = new DecimalFormat("#0.000");
        NumberFormat formatter1 = new DecimalFormat("#0.0");
        NumberFormat formatter0 = new DecimalFormat("#0");
        int secInHour = (60 * 60), secInMinute = 60;
        int hours = 0, minutes = 0;

        if (seconds >= secInHour) {
            hours = (int) seconds / secInHour;
            seconds = seconds % secInHour;
        }
        if (seconds >= secInMinute) {
            minutes = (int) seconds / secInMinute;
            seconds = seconds % secInMinute;
        }

        if (hours > 0) {
            return hours + "h " + minutes + "m " + formatter0.format(seconds) + "s";
        } else if (minutes > 0) {
            return minutes + "m " + formatter1.format(seconds) + "s";
        } else if (seconds > 0) {
            return formatter3.format(seconds) + "s";
        } else {
            return "0.0s";
        }

    }

}
