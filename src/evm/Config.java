package evm;

import evm.YAMLpublic.PublicBallot;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * A class that stores the data read in the config files
 */
public class Config {

    /* The list of ballots for program to have the user fill out */
    private List<PublicBallot> ballots;

    /* Any generic extra data we need for the execution of the program */
    private Map<String, Object> extraData;

    /**
     * Parses a YAML file at filePath and converts it to a Config object.
     * @param filePath the file path to parse
     * @return the Config generated
     * @throws FileNotFoundException thrown by FileInputStream
     */
    public static Config readConfig(String filePath) throws FileNotFoundException {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream input = new FileInputStream(new File(filePath));
        return yaml.load(input);
    }

    /**
     * Creates an empty config with no information
     */
    public Config(){}

    /**
     * Creates a config with ballots and extraData
      * @param ballots the ballots for the config
     * @param extraData the extraData for the config
     */
    public Config(List<PublicBallot> ballots, Map<String, Object> extraData) {
        this.ballots = ballots;
        this.extraData = extraData;
    }

    /**
     * Getter for the ballots
     * @return the ballots
     */
    public List<PublicBallot> getBallots() {
        return ballots;
    }

    /**
     * Getter for the extraData
     * @return the extraData
     */
    public Map<String, Object> getExtraData() {
        return extraData;
    }

    /**
     * Setter for the ballots
     * @param ballots the new ballots
     */
    public void setBallots(List<PublicBallot> ballots) {
        this.ballots = ballots;
    }

    /**
     * Setter for the extraData
     * @param extraData the new extraData
     */
    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
}
