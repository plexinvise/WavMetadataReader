import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by plexinvise on 4/20/17.
 */
public class MetadataSavingUtility {

    private static File inputFile;
    static Logger logger = Logger.getLogger(MetadataSavingUtility.class);


    /*
    Saving inputString to file, checking if file is not processed
     */
    public static void saveToFile(String stringToSave, String destFilePath, String inputFileName) throws IOException {
        PropertyConfigurator.configure(Variables.LOG4J_PROPS);
        inputFile = new File(destFilePath);
        //Checking if the file is existing file
        if (inputFile.exists() && inputFile.isFile()) {
            List outputArray = new ArrayList(FileUtils.readLines(inputFile, "UTF-8"));
            //Checking if we already stored this file metadata
            if (!isAlreadyProcessed(outputArray, inputFileName)) {
                //writing to file with append mode true
                writeToFile(stringToSave, destFilePath);
                logger.info("File " + inputFileName + " processed");
            } else {
                logger.info("File " + inputFileName + " already has been processed");
            }
        } else {
            //in case file not exist
            if (createFile()) {
                //Regression call
                saveToFile(stringToSave, destFilePath, inputFileName);

            }
        }
    }

    /*
    Creating file in specified path, if parent directory not exist - creating it too
    Returning boolean to make sure that file created and we can use recursive call in saveToFile method
     */
    private static boolean createFile() throws IOException {
        if (!inputFile.getParentFile().exists()) {
            inputFile.getParentFile().mkdir();
        }
        if (inputFile.createNewFile()) {
            return true;
        }
        logger.error("Unable to create/access output file");
        return false;
    }

    /*
    Writing to file
    FileWriter opened with append=true, will add every new string without rewriting other
     */
    private static void writeToFile(String stringToSave, String destFilePath) throws IOException {
        //Just in case file is not writable
        if (inputFile.canWrite()) {
            FileWriter writer = new FileWriter(destFilePath, true);
            writer.write(stringToSave + "\n");
            writer.close();
        } else {
            logger.warn("Can not access file " + destFilePath + ". Make sure file is not locked");
        }
    }

    /*
    Checking if we have this string in the file
     */
    private static boolean isAlreadyProcessed(List<String> list, String inputFileName) {
        Pattern pattern = Pattern.compile(inputFileName, Pattern.CASE_INSENSITIVE);
        for (String line : list) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

}
