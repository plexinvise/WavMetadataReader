/**
 * Created by plexinvise on 6/11/17.
 */
public interface IVariables {

    /**
     * Need to discuss why interface is better then just a class,
     * read that it is a bad practice to store global variables in interface.
     */

    String WAV_INPUT_FOLDER = "..//WavMetadataReader//wavInputFiles//";

    String OUTPUT_FILE_PATH = "..//WavMetadataReader//output//metaDataCollector.txt";

    String LOG4J_PROPS = "..//WavMetadataReader//log4j.properties";

}
