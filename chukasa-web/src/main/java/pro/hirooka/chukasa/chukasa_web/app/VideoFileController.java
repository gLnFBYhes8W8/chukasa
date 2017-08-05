package pro.hirooka.chukasa.chukasa_web.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pro.hirooka.chukasa.chukasa_common.domain.model.VideoFile;
import pro.hirooka.chukasa.chukasa_filer.domain.service.IVideoFileService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("files")
public class VideoFileController {

    @Autowired
    IVideoFileService videoFileService;

    @RequestMapping(method = RequestMethod.GET)
    String getAll(Model model){
        List<VideoFile> videoFileList = videoFileService.getAll();
        model.addAttribute("videoFileList", videoFileList);
        return "files/list";
    }

    @RequestMapping(value = "transcode", method = RequestMethod.POST)
    String create(@Validated VideoFile videoFile, Model model){
        int i = 0;
        return "files/list";
    }
}
