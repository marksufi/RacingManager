package hippos.web;

import hippos.io.Validator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 3, 2005
 * AlphaNumber: 6:58:29 PM
 * To change this template use Options | File Templates.
 */
public interface Listener {
    public List find();
    public List find(Validator validator);
}
