package hippos.io;

import hippos.HarnessApp;
import hippos.RaceProgramStart;
import hippos.utils.HipposProperties;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 22.7.2013
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class GameDirectory {

    public static final String directoryPath = HarnessApp.testMode ? HipposProperties.get("TEST_GAME_FILE_PATH") : HipposProperties.get("GAME_FILE_PATH");

    public GameFile createFile(RaceProgramFile file) throws IOException {
        String filename = file.getName().substring(0, file.getName().lastIndexOf("_"));
        return new GameFile(directoryPath, filename + ".log");
    }

    public GameFile createFile(RaceProgramStart raceProgramStart) throws IOException {
        String filename = raceProgramStart.getId().substring(0, raceProgramStart.getId().lastIndexOf("_"));
        return new GameFile(directoryPath, filename + ".log");
    }

}
