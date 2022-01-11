package hippos.util;

import hippos.RaceProgramHorse;
import hippos.RaceResultHorse;

public class HObservable implements Comparable {
    private Comparable element;
    private Object content;
    private RaceProgramHorse raceProgramHorse;

    public HObservable(Comparable element, Object content, RaceProgramHorse raceProgramHorse) {

        this.element = element;
        this.content = content;
        this.raceProgramHorse = raceProgramHorse;
    }

    public RaceProgramHorse getRaceProgramHorse() {
        return raceProgramHorse;
    }

    @Override
    public int compareTo(Object o) {
        HObservable ho = (HObservable) o;

        return element.compareTo(ho.element);
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(element);
        sb.append("[");
        sb.append(content);
        sb.append("]");
        sb.append(("/"));
        sb.append(raceProgramHorse.getRaceHorseName());

        if(raceProgramHorse.getRaceResultHorse() != null) {
            sb.append("=>");
            sb.append(raceProgramHorse.getRaceResultHorse().getRaceRanking());
        }

        return sb.toString();
    }
}
