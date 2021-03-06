package pro.hirooka.chukasa.domain.service.filer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.CommonConfiguration;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.model.common.M4vFile;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.model.common.type.M4vType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;

@Service
public class VideoFileService implements IVideoFileService {

    private static final Logger log = LoggerFactory.getLogger(VideoFileService.class);

    private final CommonConfiguration commonConfiguration;
    private final SystemConfiguration systemConfiguration;

    @Autowired
    public VideoFileService(
            CommonConfiguration commonConfiguration,
            SystemConfiguration systemConfiguration
    ) {
        this.commonConfiguration = requireNonNull(commonConfiguration);
        this.systemConfiguration = requireNonNull(systemConfiguration);
    }

    @Override
    public List<VideoFile> getAll() {
        final List<VideoFile> videoFileList = new ArrayList<>();
        final List<String> extensionList = Arrays.asList(commonConfiguration.getVideoFileExtension());
        final File videoFileDirectory = new File(systemConfiguration.getFilePath());
        final File[] fileArray = videoFileDirectory.listFiles();
        if(fileArray != null) {
            for (File file : fileArray) {
                for(String extension : extensionList){
                    if(file.getName().endsWith("." + extension)){
                        final VideoFile videoFile = new VideoFile();
                        videoFile.setName(file.getName().split("." + extension)[0]);
                        videoFile.setExtension(extension);
                        videoFile.setHls(false);
                        videoFile.setHlsBitrateList(new ArrayList<>());
                        videoFile.setM4v(false);
                        videoFile.setWatch(false);
                        videoFileList.add(videoFile);
                    }
                }
            }
        }else{
            log.warn("'{}' does not exist.", videoFileDirectory);
        }
        for(VideoFile videoFile : videoFileList){
            final String name = videoFile.getName();
            final File directory = new File(systemConfiguration.getFilePath() + FILE_SEPARATOR + name);
            if(directory.exists()){
                File[] files = directory.listFiles();

            }
        }
        return videoFileList;
    }

    @Override
    public List<M4vFile> getAllM4v(M4vType m4vType) {
        final List<M4vFile> m4vFileList = new ArrayList<>();
        final File videoFileDirectory = new File(systemConfiguration.getFilePath());
        final File[] fileArray = videoFileDirectory.listFiles();
        if(fileArray != null) {
            for (File file : fileArray) {
                if(m4vType == M4vType.WATCH) {
                    if (file.getName().endsWith(".watch.m4v")) {
                        final M4vFile m4vFile = new M4vFile();
                        m4vFile.setName(file.getName().split(".watch.m4v")[0]);
                        m4vFile.setType(m4vType);
                        m4vFileList.add(m4vFile);
                    }
                }else if(m4vType == M4vType.PHONE){
                    if (file.getName().endsWith(".m4v") && !file.getName().endsWith(".watch.m4v")) {
                        final M4vFile m4vFile = new M4vFile();
                        m4vFile.setName(file.getName().split(".m4v")[0]);
                        m4vFile.setType(m4vType);
                        m4vFileList.add(m4vFile);
                    }
                }else if(m4vType == M4vType.PAD){
                    if (file.getName().endsWith(".m4v") && !file.getName().endsWith(".watch.m4v")) {
                        final M4vFile m4vFile = new M4vFile();
                        m4vFile.setName(file.getName().split(".m4v")[0]);
                        m4vFile.setType(m4vType);
                        m4vFileList.add(m4vFile);
                    }
                }
            }
        }else{
            log.warn("'{}' does not exist.", videoFileDirectory);
        }
        return m4vFileList;
    }

    @Override
    public List<VideoFile> getVideoFileList() {
        final List<VideoFile> videoFileList = new ArrayList<>();
        final File fileDirectory = new File(systemConfiguration.getFilePath());
        final File[] fileArray = fileDirectory.listFiles();
        if(fileArray != null) {
            final String[] videoFileExtensionArray = commonConfiguration.getVideoFileExtension();
            final List<String> videoFileExtensionList = Arrays.asList(videoFileExtensionArray);
            for (File file : fileArray) {
                for(String videoFileExtension : videoFileExtensionList){
                    if(file.getName().endsWith("." + videoFileExtension)){
                        final VideoFile videoFileModel = new VideoFile();
                        videoFileModel.setName(file.getName());
                        videoFileList.add(videoFileModel);
                    }
                }
            }
        }else{
            log.warn("'{}' does not exist.", fileDirectory);
        }
        videoFileList.sort(Comparator.comparing(VideoFile::getName));
        return videoFileList;
    }
}
