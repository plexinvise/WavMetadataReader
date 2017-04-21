import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by plexinvise on 4/20/17.
 */
public class MetadataSavingUtility {

    private static void saveToFile (String stringToSave, String filePath) throws FileNotFoundException {
        //Checking if the file is existing file and we have access to write
        if (new File(filePath).isFile()&&new File(filePath).exists()&&new File(filePath).canRead()) {
            //not complete
        }
    }

}
