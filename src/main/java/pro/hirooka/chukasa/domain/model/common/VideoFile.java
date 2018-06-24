package pro.hirooka.chukasa.domain.model.common;

import java.util.List;

public class VideoFile {
    private String name;
    private String extension;
    private boolean isM4v;
    private boolean isWatch;
    private boolean isHls;
    private List<Integer> hlsBitrateList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isM4v() {
        return isM4v;
    }

    public void setM4v(boolean m4v) {
        isM4v = m4v;
    }

    public boolean isWatch() {
        return isWatch;
    }

    public void setWatch(boolean watch) {
        isWatch = watch;
    }

    public boolean isHls() {
        return isHls;
    }

    public void setHls(boolean hls) {
        isHls = hls;
    }

    public List<Integer> getHlsBitrateList() {
        return hlsBitrateList;
    }

    public void setHlsBitrateList(List<Integer> hlsBitrateList) {
        this.hlsBitrateList = hlsBitrateList;
    }
}
