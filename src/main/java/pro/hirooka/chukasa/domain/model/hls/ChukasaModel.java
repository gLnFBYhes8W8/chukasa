package pro.hirooka.chukasa.domain.model.hls;

import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.config.hls.HlsConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChukasaModel {

    // TODO: せいり

    private int adaptiveBitrateStreaming;

    private SystemConfiguration systemConfiguration;
    private HlsConfiguration hlsConfiguration;

    // Configuration
    private String streamRootPath;
    private String streamPath;
    private String tempEncPath;
    private int videoBitrate;

    private long timerSegmenterDelay;
    private long timerSegmenterPeriod;
    private long timerPlaylisterDelay;
    private long timerPlaylisterPeriod;

    private ChukasaSettings chukasaSettings;

    private String streamFileExtension;

    // Segmenter
    private long readBytes;
    private int seqTs;
    private int seqTsEnc;
    private int seqTsOkkake;
    private int seqTsLast;
    private boolean flagSegFullDuration;
    private boolean flagLastTs;
    private BigDecimal initPcrSecond;
    private BigDecimal lastPcrSecond;
    private BigDecimal diffPcrSecond;
    private BigDecimal lastPcrSec;
    private double duration;
    private List<Double> extinfList;
    private BigDecimal nextInit;
    private int segmentedSequenceByFFmpeg;

    // Encrypter
    private ArrayList<String> keyArrayList;
    private ArrayList<String> ivArrayList;

    // Playlister
    private int seqPl;
    private String namePl;
    private boolean flagLastPl;
    private int sequenceInitialPlaylist;

    // Flag for Timer
    private boolean flagTimerSegmenter;
    private boolean flagTimerFFmpegHLSSegmenter;
    private boolean flagTimerPlaylister;

    // Remover
    private boolean flagRemoveFile;

    private boolean isTrascoding;
    private long ffmpegPID;

    private UUID uuid;

    private FfmpegVcodecType ffmpegVcodecType;

    private int sequenceMediaSegment;
    private int sequenceLastMediaSegment;
    private int sequencePlaylist;

    private String tunerDeviceName;

    private String unixDomainSocketPath;

    public ChukasaModel(){

        adaptiveBitrateStreaming = 0;

        this.systemConfiguration = null;
        this.hlsConfiguration = null;

        // Configuration
        this.streamRootPath = "";
        this.streamPath = "";
        this.tempEncPath = "";
        this.videoBitrate = 0;

        this.timerSegmenterDelay = 0;
        this.timerSegmenterPeriod = 0;
        this.timerPlaylisterDelay = 0;
        this.timerPlaylisterPeriod = 0;

        this.chukasaSettings = null;

        this.streamFileExtension = ".ts";

        // Segmenter
        this.readBytes = 0;
        this.seqTs = -1;
        this.seqTsEnc = 0;
        this.seqTsOkkake = 0;
        this.seqTsLast = 0;
        this.flagSegFullDuration = false;
        this.flagLastTs = false;
        this.initPcrSecond = new BigDecimal("0.0");
        this.lastPcrSecond = new BigDecimal("0.0");
        this.diffPcrSecond = new BigDecimal("0.0");
        this.lastPcrSec = new BigDecimal("0.0");
        this.duration = 0;
        this.extinfList = new ArrayList<>();
        this.nextInit = new BigDecimal("0.0");
        this.segmentedSequenceByFFmpeg = -1;

        // Encrypter
        keyArrayList = new ArrayList<>();
        ivArrayList = new ArrayList<>();

        // Playlister
        this.seqPl = -1;
        this.namePl = "playlist.m3u8";
        this.flagLastPl = false;
        this.sequenceInitialPlaylist = -1;

        // Flag for Timer
        this.flagTimerSegmenter = false;
        this.flagTimerFFmpegHLSSegmenter = false;
        this.flagTimerPlaylister = false;

        // Remover
        this.flagRemoveFile = false;

        this.isTrascoding = false;
        this.ffmpegPID = -1;

        this.uuid = null;

        this.ffmpegVcodecType = FfmpegVcodecType.UNKNOWN;

        this.sequenceMediaSegment = -1;
        this.sequenceLastMediaSegment = -1;
        this.sequencePlaylist = -1;

        this.tunerDeviceName = "";

        this.unixDomainSocketPath = "";
    }

    public int getAdaptiveBitrateStreaming() {
        return adaptiveBitrateStreaming;
    }

    public void setAdaptiveBitrateStreaming(int adaptiveBitrateStreaming) {
        this.adaptiveBitrateStreaming = adaptiveBitrateStreaming;
    }

    public SystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }

    public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public HlsConfiguration getHlsConfiguration() {
        return hlsConfiguration;
    }

    public void setHlsConfiguration(HlsConfiguration hlsConfiguration) {
        this.hlsConfiguration = hlsConfiguration;
    }

    public String getStreamRootPath() {
        return streamRootPath;
    }

    public void setStreamRootPath(String streamRootPath) {
        this.streamRootPath = streamRootPath;
    }

    public String getStreamPath() {
        return streamPath;
    }

    public void setStreamPath(String streamPath) {
        this.streamPath = streamPath;
    }

    public String getTempEncPath() {
        return tempEncPath;
    }

    public void setTempEncPath(String tempEncPath) {
        this.tempEncPath = tempEncPath;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public long getTimerSegmenterDelay() {
        return timerSegmenterDelay;
    }

    public void setTimerSegmenterDelay(long timerSegmenterDelay) {
        this.timerSegmenterDelay = timerSegmenterDelay;
    }

    public long getTimerSegmenterPeriod() {
        return timerSegmenterPeriod;
    }

    public void setTimerSegmenterPeriod(long timerSegmenterPeriod) {
        this.timerSegmenterPeriod = timerSegmenterPeriod;
    }

    public long getTimerPlaylisterDelay() {
        return timerPlaylisterDelay;
    }

    public void setTimerPlaylisterDelay(long timerPlaylisterDelay) {
        this.timerPlaylisterDelay = timerPlaylisterDelay;
    }

    public long getTimerPlaylisterPeriod() {
        return timerPlaylisterPeriod;
    }

    public void setTimerPlaylisterPeriod(long timerPlaylisterPeriod) {
        this.timerPlaylisterPeriod = timerPlaylisterPeriod;
    }

    public ChukasaSettings getChukasaSettings() {
        return chukasaSettings;
    }

    public void setChukasaSettings(ChukasaSettings chukasaSettings) {
        this.chukasaSettings = chukasaSettings;
    }

    public String getStreamFileExtension() {
        return streamFileExtension;
    }

    public void setStreamFileExtension(String streamFileExtension) {
        this.streamFileExtension = streamFileExtension;
    }

    public long getReadBytes() {
        return readBytes;
    }

    public void setReadBytes(long readBytes) {
        this.readBytes = readBytes;
    }

    public int getSeqTs() {
        return seqTs;
    }

    public void setSeqTs(int seqTs) {
        this.seqTs = seqTs;
    }

    public int getSeqTsEnc() {
        return seqTsEnc;
    }

    public void setSeqTsEnc(int seqTsEnc) {
        this.seqTsEnc = seqTsEnc;
    }

    public int getSeqTsOkkake() {
        return seqTsOkkake;
    }

    public void setSeqTsOkkake(int seqTsOkkake) {
        this.seqTsOkkake = seqTsOkkake;
    }

    public int getSeqTsLast() {
        return seqTsLast;
    }

    public void setSeqTsLast(int seqTsLast) {
        this.seqTsLast = seqTsLast;
    }

    public boolean isFlagSegFullDuration() {
        return flagSegFullDuration;
    }

    public void setFlagSegFullDuration(boolean flagSegFullDuration) {
        this.flagSegFullDuration = flagSegFullDuration;
    }

    public boolean isFlagLastTs() {
        return flagLastTs;
    }

    public void setFlagLastTs(boolean flagLastTs) {
        this.flagLastTs = flagLastTs;
    }

    public BigDecimal getInitPcrSecond() {
        return initPcrSecond;
    }

    public void setInitPcrSecond(BigDecimal initPcrSecond) {
        this.initPcrSecond = initPcrSecond;
    }

    public BigDecimal getLastPcrSecond() {
        return lastPcrSecond;
    }

    public void setLastPcrSecond(BigDecimal lastPcrSecond) {
        this.lastPcrSecond = lastPcrSecond;
    }

    public BigDecimal getDiffPcrSecond() {
        return diffPcrSecond;
    }

    public void setDiffPcrSecond(BigDecimal diffPcrSecond) {
        this.diffPcrSecond = diffPcrSecond;
    }

    public BigDecimal getLastPcrSec() {
        return lastPcrSec;
    }

    public void setLastPcrSec(BigDecimal lastPcrSec) {
        this.lastPcrSec = lastPcrSec;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<Double> getExtinfList() {
        return extinfList;
    }

    public void setExtinfList(List<Double> extinfList) {
        this.extinfList = extinfList;
    }

    public BigDecimal getNextInit() {
        return nextInit;
    }

    public void setNextInit(BigDecimal nextInit) {
        this.nextInit = nextInit;
    }

    public int getSegmentedSequenceByFFmpeg() {
        return segmentedSequenceByFFmpeg;
    }

    public void setSegmentedSequenceByFFmpeg(int segmentedSequenceByFFmpeg) {
        this.segmentedSequenceByFFmpeg = segmentedSequenceByFFmpeg;
    }

    public ArrayList<String> getKeyArrayList() {
        return keyArrayList;
    }

    public void setKeyArrayList(ArrayList<String> keyArrayList) {
        this.keyArrayList = keyArrayList;
    }

    public ArrayList<String> getIvArrayList() {
        return ivArrayList;
    }

    public void setIvArrayList(ArrayList<String> ivArrayList) {
        this.ivArrayList = ivArrayList;
    }

    public int getSeqPl() {
        return seqPl;
    }

    public void setSeqPl(int seqPl) {
        this.seqPl = seqPl;
    }

    public String getNamePl() {
        return namePl;
    }

    public void setNamePl(String namePl) {
        this.namePl = namePl;
    }

    public boolean isFlagLastPl() {
        return flagLastPl;
    }

    public void setFlagLastPl(boolean flagLastPl) {
        this.flagLastPl = flagLastPl;
    }

    public int getSequenceInitialPlaylist() {
        return sequenceInitialPlaylist;
    }

    public void setSequenceInitialPlaylist(int sequenceInitialPlaylist) {
        this.sequenceInitialPlaylist = sequenceInitialPlaylist;
    }

    public boolean isFlagTimerSegmenter() {
        return flagTimerSegmenter;
    }

    public void setFlagTimerSegmenter(boolean flagTimerSegmenter) {
        this.flagTimerSegmenter = flagTimerSegmenter;
    }

    public boolean isFlagTimerFFmpegHLSSegmenter() {
        return flagTimerFFmpegHLSSegmenter;
    }

    public void setFlagTimerFFmpegHLSSegmenter(boolean flagTimerFFmpegHLSSegmenter) {
        this.flagTimerFFmpegHLSSegmenter = flagTimerFFmpegHLSSegmenter;
    }

    public boolean isFlagTimerPlaylister() {
        return flagTimerPlaylister;
    }

    public void setFlagTimerPlaylister(boolean flagTimerPlaylister) {
        this.flagTimerPlaylister = flagTimerPlaylister;
    }

    public boolean isFlagRemoveFile() {
        return flagRemoveFile;
    }

    public void setFlagRemoveFile(boolean flagRemoveFile) {
        this.flagRemoveFile = flagRemoveFile;
    }

    public boolean isTrascoding() {
        return isTrascoding;
    }

    public void setTrascoding(boolean trascoding) {
        isTrascoding = trascoding;
    }

    public long getFfmpegPID() {
        return ffmpegPID;
    }

    public void setFfmpegPID(long ffmpegPID) {
        this.ffmpegPID = ffmpegPID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public FfmpegVcodecType getFfmpegVcodecType() {
        return ffmpegVcodecType;
    }

    public void setFfmpegVcodecType(FfmpegVcodecType ffmpegVcodecType) {
        this.ffmpegVcodecType = ffmpegVcodecType;
    }

    public int getSequenceMediaSegment() {
        return sequenceMediaSegment;
    }

    public void setSequenceMediaSegment(int sequenceMediaSegment) {
        this.sequenceMediaSegment = sequenceMediaSegment;
    }

    public int getSequenceLastMediaSegment() {
        return sequenceLastMediaSegment;
    }

    public void setSequenceLastMediaSegment(int sequenceLastMediaSegment) {
        this.sequenceLastMediaSegment = sequenceLastMediaSegment;
    }

    public int getSequencePlaylist() {
        return sequencePlaylist;
    }

    public void setSequencePlaylist(int sequencePlaylist) {
        this.sequencePlaylist = sequencePlaylist;
    }

    public String getTunerDeviceName() {
        return tunerDeviceName;
    }

    public void setTunerDeviceName(String tunerDeviceName) {
        this.tunerDeviceName = tunerDeviceName;
    }

    public String getUnixDomainSocketPath() {
        return unixDomainSocketPath;
    }

    public void setUnixDomainSocketPath(String unixDomainSocketPath) {
        this.unixDomainSocketPath = unixDomainSocketPath;
    }
}

