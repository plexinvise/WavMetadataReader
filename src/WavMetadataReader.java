import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.*;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;


/**
 * Created by plexinvise on 4/12/17.
 */

public class WavMetadataReader {

    static Logger logger = Logger.getLogger(WavMetadataReader.class);
    MetadataSavingUtility metadataSavingUtility = new MetadataSavingUtility();


    public WavMetadataReader() {
        PropertyConfigurator.configure(IVariables.LOG4J_PROPS);
    }

    public void scanFiles() throws IOException, UnsupportedAudioFileException {

        File outputFile = new File(IVariables.OUTPUT_FILE_PATH);
        if (!outputFile.exists()) {
            metadataSavingUtility.createFile(outputFile);
        }
        List outputArray = new ArrayList(FileUtils.readLines(outputFile, "UTF-8"));

        //Getting files list in order to process them in the loop below
        File[] allFiles = new File(IVariables.WAV_INPUT_FOLDER).listFiles();

        // getting all files in the folder and processing if they are newer then output
        for (File file : allFiles) {

            //Checking if file is wav file and if we already processed this file
            if (!file.toPath().getFileName().toString().toLowerCase().endsWith(".wav")) {
                logger.info("File " + file.getName() + " is not WAV file");
                continue;
            }
            if (metadataSavingUtility.isAlreadyProcessed(outputArray, file.getName())) {
                logger.info("File " + file.getName() + " has already been processed");
                continue;
            }

            //Obtaining metadata and saving it to the file
            metadataSavingUtility.saveToFile(getMetadata(file), outputFile, file.getName());

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
