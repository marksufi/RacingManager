package hippos.web;

import hippos.HarnessApp;
import hippos.io.RaceFile;
import hippos.io.RaceResultDirectory;
import hippos.io.Validator;
import hippos.utils.FileUtils;
import hippos.utils.HipposProperties;
import utils.Log;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Sep 1, 2004
 * AlphaNumber: 8:39:43 PM
 * To change this template use Options | File Templates.
 */
public class ResultListener implements Listener {
    public static final String resultUrl = HipposProperties.get("RESULT_URL");
    NokiaSocket connection;
    Validator validator;
    List resultfileCandidates;
    RaceResultDirectory raceResultDir;

    /*
    public ResultListener(List resultfileCandidates)  throws UnknownHostException, IOException {
        this.resultfileCandidates = resultfileCandidates;
    }*/

    public ResultListener() throws FileNotFoundException {
        raceResultDir =  new RaceResultDirectory();
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public List find() {
        return find(null);
    }

    public List find(Validator validator) {
        System.out.println("SEARCHING FOR NEW RESULT URLS");
        List newUrls = new Vector();
        try {
            Iterator resultfileCandidatesItr = resultfileCandidates.iterator();
            while(resultfileCandidatesItr.hasNext()) {
                URL url = new URL(resultUrl + resultfileCandidatesItr.next());
                if(validator == null || validator.validate(url) == true) {
                    newUrls.add(url);
                }
            }
        } catch (Exception e) {
            Log.write(e);
        }
        return newUrls;
    }

    /**
     * https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1435957200000&sp=CEL
     * https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1419199200000&sp=CES
     * https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1424210400000&sp=CEH
     *
     * https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CEL&sp=CF1423692000000&sp=CC0
     * https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CEL&sp=CF1435957200000&sp=CC0
     *
     * @param filename
     */
    public void updateResultFile(String filename) throws UnknownHostException {
        try {
            String filesplit [] = filename.split("_");
            StringBuffer sb = new StringBuffer();
            sb.append(HarnessApp.HipposUrl + "/heppa/app?page=racing%2FRaceResults&service=external");
            sb.append("&sp=");
            sb.append(filesplit[1].substring(0, filesplit[1].indexOf("."))); // &sp=CEL
            sb.append("&sp=");
            sb.append(filesplit[0]); //&sp=CF1423692000000
            sb.append("&sp=CC0");

            URL raceURL = new URL(sb.toString());
            RaceFile raceFile = raceResultDir.createFile(filename);
            FileUtils.write(raceURL, raceFile);
            System.out.println("EXISTING RESULT FILE: " + raceFile + " UPDATED");
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
