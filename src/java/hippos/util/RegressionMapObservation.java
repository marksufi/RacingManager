package hippos.util;

import hippos.exception.RegressionModelException;

import java.util.List;

public interface RegressionMapObservation {
    List getRegKey() throws RegressionModelException;
    double [] getRegAddX() throws RegressionModelException;
    double [] getRegGetX() throws RegressionModelException;
    double getRegY() throws RegressionModelException;
}
