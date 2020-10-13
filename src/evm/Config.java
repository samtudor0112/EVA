package evm;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A class that stores the data read in the config files
 */
public class Config {
    private List<Ballot> ballots;
    private Map<String, Object> extraData;

    public Config(String filepath) throws IOException {

    }

    public Config(){}

    public List<Ballot> getBallots() {
        return ballots;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setBallots(List<Ballot> ballots) {
        this.ballots = ballots;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
}
