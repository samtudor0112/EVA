package evm;

import evm.YAMLpublic.PublicBallot;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A class that stores the data read in the config files
 */
public class Config {
    private List<PublicBallot> ballots;
    private Map<String, Object> extraData;

    public Config(String filepath) throws IOException {
        // TODO
    }

    public Config(){}

    public List<PublicBallot> getBallots() {
        return ballots;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setBallots(List<PublicBallot> ballots) {
        this.ballots = ballots;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
}
