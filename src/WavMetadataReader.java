import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import org.apache.commons.io.*;


/**
 * Created by plexinvise on 4/12/17.
 */

public class WavMetadataReader {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        File inputFile = new File("..//WavMetadataReader//sounds//MSG365857067000515_192.168.44.118.wav");
        getMetadata(inputFile);
    }

    private static String getMetadata(File inputFile) throws javax.sound.sampled.UnsupportedAudioFileException, java.io.IOException {

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
        System.out.println(encodedMetadata);
        return encodedMetadata;
    }

    //Calculating metadata length here
    private static int byteLengthDifference(InputStream inputStream, AudioInputStream audioInputStream) {
        int byteLenght = 0;
        if (inputStream != null && audioInputStream != null) {
            try {
                byteLenght = inputStream.available() - audioInputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteLenght;
    }

    private static void saveToFile (String stringToSave, String filePath) throws FileNotFoundException {
        //Checking if the file is existing file and we have access to write
        if (new File(filePath).isFile()&&new File(filePath).exists()&&new File(filePath).canRead()) {
            //not complete
        }
    }

}
