package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.hirooka.chukasa.api.v1.helper.ChukasaUtility;
import pro.hirooka.chukasa.domain.activity.*;
import pro.hirooka.chukasa.domain.model.app.Html5Player;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;
import pro.hirooka.chukasa.domain.service.hls.util.BrowserDetector;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.ALTERNATIVE_HLS_PLAYER;

@RequestMapping("/chukasa")
@Controller
public class ChukasaController {

    private static final Logger log = LoggerFactory.getLogger(ChukasaController.class);

    private final HttpServletRequest httpServletRequest;
    private final IHlsActivity hlsActivity;
    private final IFileActivity fileActivity;
    private final ISystemActivity systemActivity;
    private final IProgramActivity programActivity;

    @Autowired
    public ChukasaController(
            HttpServletRequest httpServletRequest,
            IHlsActivity hlsActivity,
            IFileActivity fileActivity,
            ISystemActivity systemActivity,
            IProgramActivity programActivity) {
        this.httpServletRequest = requireNonNull(httpServletRequest);
        this.hlsActivity = requireNonNull(hlsActivity);
        this.fileActivity = requireNonNull(fileActivity);
        this.systemActivity = requireNonNull(systemActivity);
        this.programActivity = requireNonNull(programActivity);
    }


    @GetMapping("")
    public String index(Model model){

        final boolean hasWebcam = systemActivity.hasWebcam();
        model.addAttribute("isWebCamera", hasWebcam);

        final String url = "";
        final Html5Player html5Player = new Html5Player();
        html5Player.setPlaylistURI(url);
        model.addAttribute("html5Player", html5Player);

        final List<Program> programList = programActivity.getProgramListNow();
        boolean isPTxByChannel = false;
        boolean isPTxByProgram = true;
        for(Program program : programList){
            if(program.getTitle() == null || program.getTitle().equals("")){
                isPTxByChannel = true;
                isPTxByProgram = false;
            }
        }
        model.addAttribute("programList", programList);
        model.addAttribute("isPTxByChannel", isPTxByChannel);
        model.addAttribute("isPTxByProgram", isPTxByProgram);

        final List<VideoFile> videoFileList = fileActivity.getVideoFileList();
        model.addAttribute("videoFileModelList", videoFileList);

        return "chukasa";
    }

    @PostMapping("")
    String play(Model model, @RequestBody @Validated ChukasaSettings chukasaSettings, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "index";
        }

        String userAgent = httpServletRequest.getHeader("user-agent");
        log.info("userAgent: {}", userAgent);

        String servletRealPath = httpServletRequest.getSession().getServletContext().getRealPath("");
        log.info("servletRealPath = {}", servletRealPath);

        final ChukasaModel chukasaModel = hlsActivity.startPlayback(chukasaSettings, userAgent, servletRealPath);
        final String playlistURI = ChukasaUtility.buildM3u8URI(chukasaModel);
        if(playlistURI.equals("/")){
            return "index";
        }

        final Html5Player html5PlayerModel = new Html5Player();
        html5PlayerModel.setPlaylistURI(playlistURI);
        model.addAttribute("html5Player", html5PlayerModel);

        if(BrowserDetector.isNativeSupported(userAgent)){
            return "embedded-native-player";
        }else if(BrowserDetector.isAlternativeSupported(userAgent)){
            return "embedded-" + ALTERNATIVE_HLS_PLAYER + "-player";
        }else{
            return "index";
        }
    }



    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    String stop(){
        hlsActivity.stopPlayback();
        return "redirect:/chukasa/remove";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    String remove(Model model){
        hlsActivity.removeStream();

        final Html5Player html5PlayerModel = new Html5Player();
        html5PlayerModel.setPlaylistURI("");
        model.addAttribute("html5Player", html5PlayerModel);

        final String userAgent = httpServletRequest.getHeader("user-agent");
        if(BrowserDetector.isNativeSupported(userAgent)){
            return "embedded-native-player";
        }else if(BrowserDetector.isAlternativeSupported(userAgent)){
            return "embedded-" + ALTERNATIVE_HLS_PLAYER + "-player";
        }else{
            return "index";
        }
    }
}
