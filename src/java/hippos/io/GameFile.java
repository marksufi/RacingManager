package hippos.io;

import hippos.RaceProgramStart;
import hippos.math.betting.GameFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 22.7.2013
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public class GameFile extends File {
    private List V_Horses = new ArrayList();
    private List A_Horses = new ArrayList();
    private List B_Horses = new ArrayList();
    private List C_Horses = new ArrayList();

    public GameFile(String directoryPath, String filename) {
        super(directoryPath, filename);
    }

    public void write(String str) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(getAbsolutePath(), true));

            out.println("V-peli: " + V_Horses);
            out.println("Toto:   " + A_Horses + " / " + B_Horses + " / " + C_Horses);
            out.println(str);

            System.out.println(this.getName());
            System.out.println(str);
            System.out.println("V-peli: " + V_Horses);
            System.out.println("Toto:   " + A_Horses + " / " + B_Horses + " / " + C_Horses);

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAHorse(BigDecimal number) {
        A_Horses.add(number);
    }

    public void addBHorse(BigDecimal number) {
        B_Horses.add(number);
    }

    public void addCHorse(BigDecimal number) {
        C_Horses.add(number);
    }

    public void addVHorse(BigDecimal number) {
        V_Horses.add(number);
    }

    public List getV_Horses() {
        return V_Horses;
    }

    public List getA_Horses() {
        return A_Horses;
    }

    public List getB_Horses() {
        return B_Horses;
    }

    public List getC_Horses() {
        return C_Horses;
    }

    public void write(RaceProgramStart raceProgramStart, GameFactory gameFactory) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(getAbsolutePath(), true));

            out.println(raceProgramStart.toString() + "\n");
            out.println(gameFactory.toString());
            out.println(raceProgramStart.getValueHorseSet());
            out.println("\n");

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getName() + "\n");
        sb.append("V-peli: " + V_Horses + "\n");
        sb.append("Toto:   " + A_Horses + " / " + B_Horses + " / " + C_Horses);
        return sb.toString();
    }
}
