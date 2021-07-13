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
    //private Map valueHorses = new HashMap();

    public void add(Totopeli totopeli) {
        gameList.add(totopeli);
    }

    /*
    public void submit(RaceProgramFile raceProgramFile) {
        submit(raceProgramFile.getRaceProgramStart());
    }*/

    public void submit(RaceProgramStart raceProgramStart) {
        /*
        List sortedValueHorses = raceProgramStart.getSortedHorseList();

        valueHorses.put(raceProgramStart.getId(), sortedValueHorses);

        Iterator itr = gameList.iterator();
        while(itr.hasNext()) {
            ((Totopeli) itr.next()).submit(raceProgramStart);
        }
        */
        for(Totopeli totopeli : gameList) {
            totopeli.submit(raceProgramStart);
        }


        //submit(raceProgramStart.getValueHorseSet());
        //submit(raceProgramStart);
    }


    /*
    public void submit(TreeSet startHorseSet) {

        Iterator itr = gameList.iterator();
        while(itr.hasNext()) {
            ((Totopeli) itr.next()).submit(startHorseSet);
        }
    }*/

    public void check(RaceResultStart raceResultStart) {

        for(Totopeli totopeli : gameList) {
            totopeli.check(raceResultStart);
        }

        //valueHorses.remove(raceResultStart.getId());
    }

    public void update(Connection conn, RaceProgramFile raceProgramFile) {
        Iterator itr = gameList.iterator();
        while(itr.hasNext()) {
            ((Totopeli) itr.next()).update(conn, raceProgramFile);
        }
    }

    /*
    public void check(TreeSet resultHorseSet) {
        Iterator itr = gameList.iterator();
        while(itr.hasNext()) {
            ((Totopeli) itr.next()).check(resultHorseSet);
        }
    }*/

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator itr = gameList.iterator();
        while(itr.hasNext()) {
            sb.append(itr.next().toString() + "\n");
        }
        return sb.toString();
    }

}
