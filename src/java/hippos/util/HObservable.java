package hippos.util;

import hippos.RaceResultHorse;

public class HObservable implements Comparable {
    private Comparable element;
    private RaceResultHorse raceResultHorse;

    public HObservable () { }

    public HObservable(Comparable element, RaceResultHorse raceResultHorse) {

        this.element = element;
        this.raceResultHorse = raceResultHorse;
    }

    @Override
    public int compareTo(Object o) {
        HObservable ho = (HObservable) o;

        return element.compareTo(ho.element);
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(element);
        sb.append(("/"));
        sb.append(raceResultHorse.getRaceHorseName());

        return sb.toString();
    }
}
