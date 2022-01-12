package hippos.util;

import hippos.RaceProgramHorse;
import hippos.RaceResultHorse;

import java.util.Comparator;

public class HObservable implements Comparable {
    private Comparable element;
    private Object content;
    private RaceProgramHorse raceProgramHorse;
    private Comparator comparator = new AssendingComparator(); // Default

    public HObservable(Comparable element, Object content, RaceProgramHorse raceProgramHorse) {

        this.element = element;
        this.content = content;
        this.raceProgramHorse = raceProgramHorse;
    }

    public HObservable(Comparable element, Object content, RaceProgramHorse raceProgramHorse, Comparator comparator) {

        this.element = element;
        this.content = content;
        this.raceProgramHorse = raceProgramHorse;
        this.comparator = comparator;
    }

    public RaceProgramHorse getRaceProgramHorse() {
        return raceProgramHorse;
    }

    @Override
    public int compareTo(Object o) {
        HObservable ho = (HObservable) o;

        if(hashCode() == o.hashCode())
            return 0;

        return comparator.compare(element, ho.element);

        //return element.compareTo(ho.element);
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(element);

        if(content != null) {
            sb.append("[");
            sb.append(content);
            sb.append("]");
        }

        sb.append(("/"));
        sb.append(raceProgramHorse.getRaceHorseName());

        if(raceProgramHorse.getRaceResultHorse() != null) {
            sb.append("=>");
            sb.append(raceProgramHorse.getRaceResultHorse().getRaceRanking());
        }

        return sb.toString();
    }
}
