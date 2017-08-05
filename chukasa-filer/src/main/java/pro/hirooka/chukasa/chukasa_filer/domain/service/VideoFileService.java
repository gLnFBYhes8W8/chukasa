package pro.hirooka.chukasa.chukasa_filer.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.chukasa_common.domain.configuration.CommonConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.configuration.SystemConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.model.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pro.hirooka.chukasa.chukasa_common.domain.constants.ChukasaConstants.FILE_SEPARATOR;

@Slf4j
@Service
public class VideoFileService implements IVideoFileService {

    @Autowired
    CommonConfiguration commonConfiguration;
    @Autowired
    SystemConfiguration systemConfiguration;

    @Override
    public List<VideoFile> getAll() {
        List<VideoFile> videoFileList = new ArrayList<>();
        List<String> extensionList = Arrays.asList(commonConfiguration.getVideoFileExtension());
        File videoFileDirectory = new File(systemConfiguration.getFilePath());
        File[] fileArray = videoFileDirectory.listFiles();
        if(fileArray != null) {
            for (File file : fileArray) {
                for(String extension : extensionList){
                    if(file.getName().endsWith("." + extension)){
                        VideoFile videoFile = new VideoFile();
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
            String name = videoFile.getName();
            File directory = new File(systemConfiguration.getFilePath() + FILE_SEPARATOR + name);
            if(directory.exists()){
                File[] files = directory.listFiles();

            }
        }
        return videoFileList;
    }
}
