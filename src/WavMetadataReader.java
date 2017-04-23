import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.*;


/**
 * Created by plexinvise on 4/12/17.
 */

public class WavMetadataReader {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        File inputFile = new File("..//WavMetadataReader//sounds//MSG365857069500516_192.168.44.118.wav");
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
            System.out.println("No metadata found");
            return 0;
        }

    }

    /*Building string for final result of this class
    Pattern: Date Time Phone Operator Filename
     */
    private static String resultSorter (File inputFile, String stringMetadata) {
        String formatedResult = getDate(stringMetadata) + " " + getTime(stringMetadata) + " "
                + getPhone(stringMetadata) + " " + getExtension(stringMetadata)
                + " " + inputFile.getName();
        return formatedResult;
    }

    /*Next 4 methods to get info we interested in from the string
    Rules not covering most of situations (Phones, Extensions), need something more flexible
     */
    private static String getPhone (String stringMetadata) {
        String phone = null;
        //10 symbols after + will not cover all variants
        Pattern pattern = Pattern.compile("\\+\\d{10}");
        Matcher matcher = pattern.matcher(stringMetadata);
        if (matcher.find()) {
            phone = matcher.group(0);
        }
        return phone;
    }

    private static String getExtension (String stringMetadata) {
        String extension = null;
        Pattern pattern = Pattern.compile("Extn\\d{3}");
        Matcher matcher = pattern.matcher(stringMetadata);
        if (matcher.find()) {
            extension = matcher.group(0);
        }
        return extension;
    }

    private static String getDate (String stringMetadata) {
        String date = null;
        Pattern pattern = Pattern.compile("\\d{4}/\\d{2}/\\d{2}");
        Matcher matcher = pattern.matcher(stringMetadata);
        if (matcher.find()) {
            date = matcher.group(0);
        }
        return date;
    }

    private static String getTime (String stringMetadata) {
        String time = null;
        Pattern pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(stringMetadata);
        if (matcher.find()) {
            time = matcher.group(0);
        }
        return time;
    }

}
