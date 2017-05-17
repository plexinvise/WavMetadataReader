import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import org.apache.log4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.*;
import org.apache.log4j.PropertyConfigurator;


/**
 * Created by plexinvise on 4/12/17.
 */

public class WavMetadataReader {

    static Logger logger = Logger.getLogger(WavMetadataReader.class);

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        PropertyConfigurator.configure("..//WavMetadataReader//log4j.properties");
        File inputFile = new File("..//WavMetadataReader//wavInputFiles//MSG365857067000515_192.168.44.118.wav");
        String outputFile = "..//WavMetadataReader//output//metaDataCollector.txt";
        MetadataSavingUtility.saveToFile(getMetadata(inputFile), outputFile, inputFile.getName());
    }

    private static String getMetadata(File inputFile) throws java.io.IOException, UnsupportedAudioFileException {

        //Creating streams to find how much bytes metadata takes
        InputStream inputStream = new FileInputStream(inputFile);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);

        //creating byte[] with size = metadata length found with byteLengthDifference method
        byte[] metadataBytes = new byte[byteLengthDifference(inputStream, audioInputStream)];

        //Closing, for the glory of memory
        audioInputStream.close();

        //Reading metadata from InputStream to byte[]
        IOUtils.read(inputStream, metadataBytes);

        //Closing, again, for the glory of memory
        inputStream.close();

        //Converting and writing to String
        String encodedMetadata = IOUtils.toString(metadataBytes, "UTF-8");

        //returning formatted string gathered with resultSorter method
        return resultSorter(inputFile, encodedMetadata);
    }

    /*Calculating metadata length here
    (AudioInputStream cutting metadata and InputStream not - difference is the metadata length)
     */
    private static int byteLengthDifference(InputStream inputStream, AudioInputStream audioInputStream) {
        int byteLength = 0; //corrected typo
        if (inputStream != null && audioInputStream != null) {
            try {
                byteLength = inputStream.available() - audioInputStream.available();
            } catch (IOException e) {
                logger.error("Caught exception: ", e);
            }
        }
        if (byteLength!=0) {
            return byteLength;
        } else { //Added else, my bad
            logger.warn("No metadata found");
            return 0;
        }

    }

    /*Building string for final result of this class
    Pattern: Date Time Phone Operator Filename
     */
    // Replaced with a string builder, need to clarify why not stringBuffer
    private static String resultSorter (File inputFile, String stringMetadata) {
        StringBuilder formatedResult = new StringBuilder();
        formatedResult.append(getMetadataPart(stringMetadata, "\\d{4}/\\d{2}/\\d{2}")).append(" ")
                .append(getMetadataPart(stringMetadata, "\\d{2}:\\d{2}:\\d{2}")).append(" ")
                .append(getMetadataPart(stringMetadata, "\\+\\d{10}")).append(" ")
                .append(getMetadataPart(stringMetadata, "Extn\\d{3}")).append(" ")
                .append(inputFile.getName());
        return formatedResult.toString();
    }

    /*
    getting string from found metadata
     */
    private static String getMetadataPart (String stringMetadata, String regExPattern) {
        String metadataPart = null;
        Pattern pattern = Pattern.compile(regExPattern);
        Matcher matcher = pattern.matcher(stringMetadata);
        if (matcher.find()) {
            metadataPart = matcher.group(0);
        }
        return metadataPart;
    }
}
