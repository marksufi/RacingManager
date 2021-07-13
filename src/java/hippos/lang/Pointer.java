package hippos.lang;

/**
 * Created by marktolo on 28.7.2014.
 */
public class Pointer extends Thread {
    int secs = 0;

    public Pointer(String s) {
        super(s);
    }

    public void run() {
        try {
            System.out.print(this.getName());
            while(true){
                System.out.print(".");
                sleep(1000);
                secs++;
            }
        } catch (InterruptedException e) {
            System.out.println(" " + secs + " seconds");
        }
    }
}
