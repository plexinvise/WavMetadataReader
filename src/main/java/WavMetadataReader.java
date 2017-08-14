/**
 * WavMetadataReared class contain main logic which is getting metadata
 * from .wav file and extracting it to the string. Contain next methods:
 * - scanFiles() - Scanning all wav files in the specified folder
 * and extracting metadata from them.
 * - getMetadata(File inputFile) - taking .wav file as inputFile
 * and returning full metadata piece from it as a String.
 * - byteLengthDifference(InputStream inputStream, AudioInputStream audioInputStream) -
 * calculating byteLength of metadata in .wav file using features of
 * input streams.
 * - resultSorter(File inputFile, String stringMetadata) - taking stringMetadata and
 * getting pieces of info we need with regexp. Adding inputFile name at the end of returned
 * string so return of this method is already formatted string which we can save.
 * - getMetadataPart(String stringMetadata, String regExPattern) - service method
 * which getting string part from stringMetadata using regExPattern.
 *
 * Created by plexinvise on 4/12/17.
 */

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.io.*;
import org.jetbrains.annotations.NotNull;

public class WavMetadataReader {

    private static Logger logger = Logger.getLogger(WavMetadataReader.class);
    private Properties constants = new Properties();

    public WavMetadataReader() throws IOException {
        /*
         * To run from IDE need to replace propIn initialization with
         * InputStream propIn = new FileInputStream("./constants.properties");
         */
        InputStream propIn = new FileInputStream(
                new File(getClass().getProtectionDomain().getCodeSource()
                        .getLocation().getPath()).getParentFile().getPath()
                        + "/constants.properties");
        constants.load(propIn);
        if (!constants.contains("log4jProps")) {
            PropertyConfigurator.configure(getClass().getResourceAsStream("log4j.properties"));
        } else {
            PropertyConfigurator.configure(constants.getProperty("log4jProps"));
        }
    }

    public void scanFiles() throws IOException, UnsupportedAudioFileException {

        File outputFile = new File(constants.getProperty("outputFilePath"));
        if (!outputFile.exists()) {
            MetadataSavingUtility.createFile(outputFile);
        }
        List outputArray = new ArrayList(FileUtils.readLines(outputFile, "UTF-8"));

        //Getting files list in order to process them in the loop below
        File[] allFiles = new File(constants.getProperty("wavInputFolder")).listFiles();

        // getting all files in the folder and processing if they are newer then output
        for (File file : allFiles) {

            //Checking if file is wav file and if we already processed this file
            if (!file.toPath().getFileName().toString().toLowerCase().endsWith(".wav")) {
                logger.info("File " + file.getName() + " is not WAV file");
                continue;
            }
            if (MetadataSavingUtility.isAlreadyProcessed(outputArray, file.getName())) {
                logger.info("File " + file.getName() + " has already been processed");
                continue;
            }

            //Obtaining metadata and saving it to the file
            MetadataSavingUtility.saveToFile(getMetadata(file), outputFile, file.getName());

        }
    }

    @NotNull
    private String getMetadata(File inputFile) throws java.io.IOException, UnsupportedAudioFileException {

        //Creating streams to find how much bytes metadata takes
        InputStream inputStream = new FileInputStream(inputFile);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);

        //creating byte[] with size = metadata length found with byteLengthDifference method
        byte[] metadataBytes;
        try {
            metadataBytes = new byte[byteLengthDifference(inputStream, audioInputStream)];
        } catch (IllegalArgumentException error) {
            audioInputStream.close();
            inputStream.close();
            //This mechanism is to prevent writing null values into file if no metadata found
            logger.error("No metadata found, execution will be aborted for " + inputFile.getName(), error);
            throw error;
        }

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
    private int byteLengthDifference(InputStream inputStream, AudioInputStream audioInputStream) {
        int byteLength = 0;
        if (inputStream != null && audioInputStream != null) {
            try {
                byteLength = inputStream.available() - audioInputStream.available();
            } catch (IOException e) {
                logger.error(e);
            }
        }
        if (byteLength != 0) {
            return byteLength;
        } else {
            throw new IllegalArgumentException();
        }

    }

    /*Building string for final result of this class
    Pattern: Date Time Phone Operator Filename
     */
    @NotNull
    private String resultSorter(File inputFile, String stringMetadata) {
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
    private String getMetadataPart(String stringMetadata, String regExPattern) {
        String metadataPart = "";
        Pattern pattern = Pattern.compile(regExPattern);
        Matcher matcher = pattern.matcher(stringMetadata);
        if (matcher.find()) {
            metadataPart = matcher.group(0);
        }
        return metadataPart;
    }
}
