package hippos.util;

import hippos.RaceProgramHorse;
import hippos.SubStart;
import hippos.SubTime;

public interface ChangeMapper {
    double getChange(SubStart subStart, RaceProgramHorse raceProgramHorse);
    void addChange(SubStart subStart, SubStart targetSubStart);
}
