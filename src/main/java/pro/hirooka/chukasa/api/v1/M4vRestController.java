package pro.hirooka.chukasa.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.model.common.M4vFile;
import pro.hirooka.chukasa.domain.model.common.type.M4vType;
import pro.hirooka.chukasa.domain.service.filer.IVideoFileService;

import java.util.List;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;

@Slf4j
@RestController
@RequestMapping("api/v1/m4v")
public class M4vRestController {

    @Autowired
    SystemConfiguration systemConfiguration;
    @Autowired
    IVideoFileService videoFileService;

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = "video/mp4")
    Resource downloadFile(@RequestBody @Validated M4vFile m4vFile){

        String filePath = "";

        switch (m4vFile.getType()){
            case PHONE:
                filePath = systemConfiguration.getFilePath() + FILE_SEPARATOR + m4vFile.getName() + ".m4v";
                break;
            case PAD:
                filePath = systemConfiguration.getFilePath() + FILE_SEPARATOR + m4vFile.getName() + ".m4v";
                break;
            case WATCH:
                filePath = systemConfiguration.getFilePath() + FILE_SEPARATOR + m4vFile.getName() + ".watch.m4v";
                break;
            default:
                break;
        }

        return new FileSystemResource(filePath);
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    Resource downloadFile(@PathVariable String name){
        String filePath = systemConfiguration.getFilePath() + FILE_SEPARATOR + name + ".watch.m4v";
        return new FileSystemResource(filePath);
    }

    @RequestMapping(value = "/watch", method = RequestMethod.GET)
    List<M4vFile> readAllWatchM4v(){
        List<M4vFile> m4vFileList = videoFileService.getAllM4v(M4vType.WATCH);
        return m4vFileList;
    }

    @RequestMapping(value = "/phone", method = RequestMethod.GET)
    List<M4vFile> readAllPhoneM4v(){
        List<M4vFile> m4vFileList = videoFileService.getAllM4v(M4vType.PHONE);
        return m4vFileList;
    }

    @RequestMapping(value = "/watch/{name}", method = RequestMethod.GET)
    Resource downloadWatchM4v(@PathVariable String name){
        String filePath = systemConfiguration.getFilePath() + FILE_SEPARATOR + name + ".watch.m4v";
        return new FileSystemResource(filePath);
    }
}
