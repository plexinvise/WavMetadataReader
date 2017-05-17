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
    // use getClass() method as parameter for logger. It would help to get proper logging in case of class extension in future
    //Changed, need more details about logger     
    static Logger logger = Logger.getLogger(MetadataSavingUtility.class);


    /*
    Saving inputString to file, checking if file is not processed
     */
    public static void saveToFile(String stringToSave, String destFilePath, String inputFileName) throws IOException {
        PropertyConfigurator.configure("..//WavMetadataReader//log4j.properties");
        inputFile = new File(destFilePath);
        //Checking if the file is existing file
        if (inputFile.exists() && inputFile.isFile()) {
            // As understood it is for making changes easily?
            // TBD
            List outputArray = new ArrayList(FileUtils.readLines(inputFile, "UTF-8"));
            //Checking if we already stored this file metadata
            if (!isDuplicate((ArrayList<String>) outputArray, inputFileName)) {
                //writing to file with append mode true
                writeToFile(stringToSave, destFilePath);
                logger.info("File "+inputFileName+" processed");
                // it's better to use bracets for all constructions like if/else/for/etc, cause it 
                // simplifies reading
            } else logger.info("File "+inputFileName+" already has been processed");
        } else {
            //in case file not exist
            if (createFile()) {
                //ACHTUNG!!! RECURSION DETECTED!!!
                saveToFile(stringToSave, destFilePath, inputFileName);

            }
        }
    }

    /*
    Creating file in specified path, if parent directory not exist - creating it too
    Returning Boolean to make sure that file created and we can use recursive call in saveToFile method
     */
    // It would be better to use primitive 'boolean' as return type in the following method. 
    // Object 'Boolean' datatype means that result could be null, which is not true
    //That was typo that I did not even mentioned, fixed
    private static boolean createFile() throws IOException {
            if (!inputFile.getParentFile().exists()) {
                inputFile.getParentFile().mkdir();
            }
            if (inputFile.createNewFile()) {
                    return true;
                // use CTRL+ALT+L in Idea for text formatting
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
            // better to use 'write' method once - when you call it two times instead of one, it executes twice as long as one. 
            // that would be cause of bad performance in future. 
            // not fixed
            writer.write(stringToSave);
            writer.write(System.lineSeparator());
            writer.close();
        } else {
            logger.warn("Can not access file "+destFilePath+". Make sure file is not locked");
        }
    }

    /*
    Checking if we have this string in the file
     */
    // It would be better to use primitive 'boolean' as return type in the following method. 
    // Object 'Boolean' datatype means that result could be null, which is not true
    
    // 1. Changing type of first parameter let you get rid of casts in method calling.
    // usage of concrete implementation of java.util.List, such as ArrayList,
    // is nessecary only of you planning to use ArrayList-specific methods. 
    // In other cases it's better to use List
    
    // 2. Rename method. Only you understands what 'isDuplicate' check preformed there
    private static Boolean isDuplicate(ArrayList<String> list, String inputFileName) {
        Pattern pattern = Pattern.compile(inputFileName, Pattern.CASE_INSENSITIVE);        
        for (String line : list) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

}
