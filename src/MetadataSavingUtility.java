import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by plexinvise on 4/20/17.
 */
public class MetadataSavingUtility {

    /*
    Saving inputString to file, checking if file is not processed
     */
    public static void saveToFile(String stringToSave, String destFilePath, String inputFileName) throws IOException {
        //Checking if the file is existing file
        if (new File(destFilePath).exists() && new File(destFilePath).isFile()) {
            ArrayList<String> outputArray =
                    (ArrayList<String>) FileUtils.readLines(new File(destFilePath), "UTF-8");
            //Checking if we already stored this file metadata
            if (!isDuplicate(outputArray, inputFileName)) {
                //writing to file with append mode true
                writeToFile(stringToSave, destFilePath);
            } else System.out.println("File "+inputFileName+" already has been processed");
        } else {
            //in case file not exist
            if (createFile(destFilePath)) {
                //ACHTUNG!!! RECURSION DETECTED!!!
                saveToFile(stringToSave, destFilePath, inputFileName);
            }
        }
    }

    /*
    Creating file in specified path, if parent directory not exist - creating it too
    Returning Boolean to make sure that file created and we can use recursive call in saveToFile method
     */
    private static Boolean createFile(String destFilePath) throws IOException {
        if (!new File(destFilePath).getParentFile().exists()) {
            new File(destFilePath).getParentFile().mkdir();
            if (new File(destFilePath).createNewFile()) {
                return true;
            }
        }
        System.out.println("File not created!!!");
        return false;
    }

    /*
    Writing to file
    FileWriter opened with append=true, will add every new string without rewriting other
     */
    private static void writeToFile(String stringToSave, String destFilePath) throws IOException {
        //Just in case file is not writable
        if (new File(destFilePath).canWrite()) {
            FileWriter writer = new FileWriter(destFilePath, true);
            writer.write(stringToSave);
            writer.write(System.lineSeparator());
            writer.close();
        } else {
            System.out.println("Can not access file "+destFilePath+". Make sure file is not locked");
        }
    }

    /*
    Checking if we have this string in the file
     */
    private static Boolean isDuplicate(ArrayList<String> list, String inputFileName) {
        Pattern pattern = Pattern.compile(inputFileName, Pattern.CASE_INSENSITIVE);
        for (String line : list
                ) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

}
