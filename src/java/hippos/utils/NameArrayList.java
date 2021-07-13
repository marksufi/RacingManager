package hippos.utils;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Markus on 11.12.2015.
 */
public class NameArrayList extends ArrayList {
    private String name;

    public NameArrayList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
