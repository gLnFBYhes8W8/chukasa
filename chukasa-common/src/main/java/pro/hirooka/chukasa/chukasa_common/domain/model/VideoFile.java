package pro.hirooka.chukasa.chukasa_common.domain.model;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.List;

@Data
public class VideoFile {
    private String name;
    private String extension;
    private boolean isM4v;
    private boolean isWatch;
    private boolean isHls;
    private List<Integer> hlsBitrateList;
}
