package pro.hirooka.chukasa.domain.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TunerWrapper {
    @JsonProperty("tuner")
    private List<Tuner> tunerList;

    public List<Tuner> getTunerList() {
        return tunerList;
    }

    public void setTunerList(List<Tuner> tunerList) {
        this.tunerList = tunerList;
    }
}
