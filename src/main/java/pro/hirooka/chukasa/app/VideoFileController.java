package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.service.filer.IVideoFileService;

import java.util.List;

@Controller
@RequestMapping("files")
public class VideoFileController {

    private static final Logger log = LoggerFactory.getLogger(VideoFileController.class);

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
