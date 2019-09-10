  package clavis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Display extends Thread {

    private static int star = 0;
    private long totalCount = 0;
    private static long millis_startTime;
    private final String mode;
    private int progress = 0;
    private final int updateTime;
    private boolean proceed;
    private final String clear = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";

    Display(long totalCount, String mode, int updateTime) {
        proceed = true;
        this.totalCount = totalCount;
        this.mode = mode;
        this.updateTime = updateTime;
        this.setPriority(10);
    }

    @Override
    public void run() {
        try {
            millis_startTime = System.currentTimeMillis();
            switch (mode) {
                case "find":
                    while (proceed && Clavis.connected) {
                        System.out.printf("%sSearching...%39s", clear, timeConvert(System.currentTimeMillis() - millis_startTime));
                        updateHelper();
                        Thread.sleep(updateTime);
                    }
                    break;
                case "clean":
                    while (proceed && Clavis.connected) {
                        System.out.printf("%sCleaning [%3.0f%%]%36s", clear, ((double) progress / totalCount) * 100, timeConvert(System.currentTimeMillis() - millis_startTime));
                        updateHelper();
                        Thread.sleep(updateTime);
                    }
                    break;
                case "analyze":
                    while (proceed && Clavis.connected) {
                        if (progress == 17) {
                            System.out.printf("%sAnalyze Writing to file... %23s%s", clear,
                                    timeConvert(System.currentTimeMillis() - millis_startTime), ((ThreadPoolExecutor) Clavis.analyzeExecutor).getActiveCount());
                        } else {
                            System.out.printf("%sAnalyze [%3.0f%%]%33s %s/%s ", clear, (progress / (double) totalCount) * 100,
                                    timeConvert(System.currentTimeMillis() - millis_startTime), ((ThreadPoolExecutor) Clavis.analyzeExecutor).getActiveCount(), Clavis.analyzeExecutor.getQueue().size());
                        }
                        updateHelper();
                        Thread.sleep(updateTime);
                    }

                    
                    break;
                case "insert":
                    long lastIncrement = 0;
                    while (proceed && Clavis.connected) {
                        percentage(Clavis.increment, lastIncrement, totalCount, millis_startTime, System.currentTimeMillis());
                        lastIncrement = Clavis.increment;
                        Thread.sleep(updateTime);
                    }
                    Clavis.totalTime = System.currentTimeMillis() - millis_startTime;
                    percentage(totalCount, totalCount, totalCount, millis_startTime, System.currentTimeMillis());
                    break;
                default:
                    System.out.println("unknown display mode");
                    break;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Display.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void increaseProgress() {
        progress++;
    }

    public void stopDisplay() {
        proceed = false;
    }

    public static void percentage(long x, long lastIncrement, long total, long start, long end) {

        System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");

        int y = (int) (((double) x / (double) total) * 100);

        String percentage = "";
        for (int z = 0; z < y / 5; z++) {
            percentage = percentage + "#";
        }
        for (int z = 0; z < 20 - (y / 5); z++) {
            percentage = percentage + " ";
        }

        //  System.out.print(Clavis.debug);
        System.out.print("[" + percentage + "] ");
        System.out.printf("%3s", y);
        System.out.print("% ");
        double temp = ((x - lastIncrement) * (1000.0 / Clavis.updateTime));

        System.out.printf("%6s", (int) temp);
        // avg = avg + temp;
        // avgTimes++;
        System.out.print("/s ");
        System.out.printf("%10s", timeConvert(end - start));
        //System.out.print(" "+Clavis.test);
        try {
            System.out.printf(" %3s", ((ThreadPoolExecutor) Clavis.bulkExecutor).getActiveCount());

        } catch (NullPointerException ex) {

        }

        updateHelper();

    }

    static void updateHelper() {
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
