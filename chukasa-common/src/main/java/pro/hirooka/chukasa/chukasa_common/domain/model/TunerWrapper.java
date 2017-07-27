package pro.hirooka.chukasa.chukasa_common.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TunerWrapper {
    @JsonProperty("tuner")
    private List<Tuner> tunerList;
}
