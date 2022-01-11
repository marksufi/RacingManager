package hippos.lang.toto;

import hippos.RaceProgramStart;
import hippos.RaceResultStart;
import hippos.io.RaceProgramFile;

import java.sql.Connection;
import java.util.*;

/**
 * Created by marktolo on 18.11.2014.
 */
public class TotoEngine {
    private List <Totopeli> gameList = new ArrayList<>();

    public void add(Totopeli totopeli) {
        gameList.add(totopeli);
    }

    public void submit(RaceProgramStart raceProgramStart) {
        for(Totopeli totopeli : gameList) {
            totopeli.submit(raceProgramStart);
        }

    }

    public void check(RaceResultStart raceResultStart) {

        for(Totopeli totopeli : gameList) {
            totopeli.check(raceResultStart);
        }

    }

    public void update(Connection conn, RaceProgramFile raceProgramFile) {
        Iterator itr = gameList.iterator();
        while(itr.hasNext()) {
            ((Totopeli) itr.next()).update(conn, raceProgramFile);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator itr = gameList.iterator();
        while(itr.hasNext()) {
            sb.append(itr.next().toString() + "\n");
        }
        return sb.toString();
    }

}
