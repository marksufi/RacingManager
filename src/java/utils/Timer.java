package utils;



/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 4, 2004
 * AlphaNumber: 11:06:37 PM
 * To change this template use Options | File Templates.
 */
public class Timer {
    private String explanation = "";
    private long startTime = 0L;

    /**
     * Inits Timer without explanation
     */
    public Timer() {}

    /**
     * Inits Timer with explanation
     *
     * @param explanation
     */
    public Timer(String explanation) {
        this.explanation = explanation;
    }

    /**
     * starts the timer
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * printouts the current timer value
     */
    public void print() {
        System.out.println(toString());
    }

    /**
     * @return current timer value as a string
     */
    public String toString() {
        StringBuffer timeStr = new StringBuffer();
        long executionTime = System.currentTimeMillis() - startTime;
        long hours = executionTime / (60L * 60L * 1000L);
        executionTime -= ( hours * 60L * 60L * 1000L);
        long minutes = executionTime / (60L * 1000L);
        executionTime -= ( minutes * 60L * 1000L);
        long seconds = executionTime / 1000L;
        timeStr.append("Timer " + explanation + ": ");
        timeStr.append(executionTime);
        timeStr.append("ms ");
        timeStr.append(hours + "h ");
        timeStr.append(minutes + "m ");
        timeStr.append(seconds + "s ");
        return timeStr.toString();
    }
}
