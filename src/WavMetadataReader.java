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

    static Logger logger = Logger.getLogger(WavMetadataReader.class.getName());

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        PropertyConfigurator.configure("..//WavMetadataReader//log4j.properties");
        File inputFile = new File("..//WavMetadataReader//wavInputFiles//MSG365857069500516_192.168.44.118.wav");
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
        int byteLenght = 0;
        if (inputStream != null && audioInputStream != null) {
            try {
                byteLenght = inputStream.available() - audioInputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (byteLenght!=0) {
            return byteLenght;
        } {
            logger.warn("No metadata found");
            return 0;
        }

    }

    /*Building string for final result of this class
    Pattern: Date Time Phone Operator Filename
     */
    private static String resultSorter (File inputFile, String stringMetadata) {
        String formatedResult = getMetadataPart(stringMetadata, "\\d{4}/\\d{2}/\\d{2}")
                + " " + getMetadataPart(stringMetadata, "\\d{2}:\\d{2}:\\d{2}")
                + " " + getMetadataPart(stringMetadata, "\\+\\d{10}")
                + " " + getMetadataPart(stringMetadata, "Extn\\d{3}")
                + " " + inputFile.getName();
        return formatedResult;
    }

    /*
    get string from found metadata
     */
    private static String getMetadataPart (String stringMetadata, String regExPattern) {
        String metadataPart = null;
        //10 symbols after + will not cover all variants
        Pattern pattern = Pattern.compile(regExPattern);
        Matcher matcher = pattern.matcher(stringMetadata);
        if (matcher.find()) {
            metadataPart = matcher.group(0);
        }
        return metadataPart;
    }
}
