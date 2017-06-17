/**
 * MetadataSavingUtility is a utility class with purpose to provide
 * support methods for proper program execution. Contains next methods:
 * - saveToFile(String stringToSave, File outputFile, String inputFileName) -
 * saving stringToSave + inputFileName into outputFile. Additional logic
 * provided to make sure that outputFile exist.
 * - createFile(File outputFile) - creating outputFile if it is not exist
 * - writeToFile(String stringToSave, File outputFile) - writing
 * stringToSave into the outputFile.
 * - isAlreadyProcessed(List<String> list, String inputFileName) -
 * serve to find inputFileName in the list of already stored metadata.
 *
 * Created by plexinvise on 4/20/17.
 */

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

public class MetadataSavingUtility {

    private static Logger logger = Logger.getLogger(MetadataSavingUtility.class);

    /*
    Saving inputString to file, checking if file is not processed
     */
    public static void saveToFile(String stringToSave, File outputFile, String inputFileName) throws IOException {
        PropertyConfigurator.configure(Constants.LOG4J_PROPS);

        //Checking if the file is existing file
        if (outputFile.exists() && outputFile.isFile()) {

            //writing to file with append mode true
            writeToFile(stringToSave, outputFile);
            logger.info("File " + inputFileName + " processed");
        }
    }

    /*
    Creating file in specified path, if parent directory not exist - creating it too
    Returning boolean to make sure that file created and we can use recursive call in saveToFile method
     */
    public static boolean createFile(File outputFile) throws IOException {
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdir();
        }
        if (outputFile.createNewFile()) {
            logger.info("File " + outputFile.getName() + " was not exist and has been created");
            return true;
        }
        logger.error("Unable to create/access output file");
        return false;
    }

    /*
    Writing to file
    FileWriter opened with append=true, will add every new string without rewriting other
     */
    private static void writeToFile(String stringToSave, File outputFile) throws IOException {
        //Just in case file is not writable
        if (outputFile.canWrite()) {
            FileWriter writer = new FileWriter(outputFile, true);
            writer.write(stringToSave + "\n");
            writer.close();
        } else {
            logger.warn("Can not access file " + outputFile + ". Make sure file is not locked");
        }
    }

    //Checking if we have this string in the file
    public static boolean isAlreadyProcessed(List<String> list, String inputFileName) {
        Pattern pattern = Pattern.compile(inputFileName, Pattern.CASE_INSENSITIVE);

        for (String line : list) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }
}
