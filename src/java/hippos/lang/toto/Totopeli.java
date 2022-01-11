package hippos.lang.toto;

import hippos.RaceProgramStart;
import hippos.RaceResultStart;
import hippos.io.RaceProgramFile;

import java.sql.Connection;
import java.util.TreeSet;

/**
 * Created by marktolo on 18.11.2014.
 */
public interface Totopeli {
    /**
     * Luo pelit ohjelman laskemasta paremmuusjärjestyksestä
     *
     * @param raceProgramStart lähtötiedosto joka sisältää hevoset paremmuusjärjestyksessä
     */
    public void submit(RaceProgramStart raceProgramStart);

    /**
     * Tarkistaa pelit lähdön tuloksia vastaan
     *
     * @param raceResultStart tulostiedosto joka sisältää lähtötulokset
     */
    public void check(RaceResultStart raceResultStart);

    /**
     * Päivittää tietokantaan lähdön pelien voitot ja tappiot
     *
     * @param conn
     * @param raceProgramFile
     */
    void update(Connection conn, RaceProgramFile raceProgramFile);

}
