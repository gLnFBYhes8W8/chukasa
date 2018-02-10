package pro.hirooka.chukasa.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.hirooka.chukasa.api.v1.helper.ChukasaUtility;
import pro.hirooka.chukasa.api.v1.helper.IChukasaBrowserDetector;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.config.hls.HlsConfiguration;
import pro.hirooka.chukasa.domain.model.app.Html5Player;
import pro.hirooka.chukasa.domain.model.common.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;
import pro.hirooka.chukasa.domain.model.recorder.Program;
import pro.hirooka.chukasa.domain.service.common.ICommonUtilityService;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IEpgdumpService;
import pro.hirooka.chukasa.domain.service.epg.ILastEpgdumpExecutedService;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;
import pro.hirooka.chukasa.domain.service.hls.ICoordinatorService;
import pro.hirooka.chukasa.domain.service.recorder.IProgramTableService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.ALTERNATIVE_HLS_PLAYER;

@Slf4j
@RequestMapping("/chukasa")
@Controller
public class ChukasaController {

    @Autowired
    SystemConfiguration systemConfiguration;
    @Autowired
    HlsConfiguration hlsConfiguration;
    @Autowired
    IChukasaModelManagementComponent chukasaModelManagementComponent;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    ISystemService systemService;
    @Autowired
    IChukasaBrowserDetector chukasaBrowserDetector;
    @Autowired
    ICommonUtilityService commonUtilityService;
    @Autowired
    ICoordinatorService taskCoordinatorService;

    @Autowired
    IProgramTableService programTableService;
    @Autowired
    ILastEpgdumpExecutedService lastEpgdumpExecutedService;
    @Autowired
    IEpgdumpService epgdumpService;


    @GetMapping("")
    public String index(Model model){
        String url = "";
        Html5Player html5Player = new Html5Player();
        html5Player.setPlaylistURI(url);
        model.addAttribute("html5Player", html5Player);

        boolean isFFmpeg = systemService.isFFmpeg();
        boolean isPTx = systemService.isTuner();
        boolean isRecpt1 = systemService.isRecxxx();
        boolean isEpgdump = epgdumpService.isEpgdump();
        boolean isMongoDB = systemService.isMongoDB();
        boolean isWebCamera = systemService.isWebCamera();
// PTx
        List<Program> programList = new ArrayList<>();
        boolean isLastEpgdumpExecuted = false;

        List<ChannelConfiguration> channelConfigurationList = commonUtilityService.getChannelConfigurationList();

        if(isMongoDB && isEpgdump){
            programList = programTableService.readByNow(new Date().getTime());
            programList = programList.stream().sorted(Comparator.comparing(Program::getPhysicalLogicalChannel)).collect(Collectors.toList());
//            if(programList != null && lastEpgdumpExecutedService.read(1) != null && programTableService.getNumberOfPhysicalChannels() >= epgdumpChannelMap.size()){
//            if(programList != null && programList.size() > 0 && lastEpgdumpExecutedService.read(1) != null && programTableService.getNumberOfPhysicalLogicalChannels() >= channelConfigurationList.size()){
            if(programList != null && programList.size() > 0 && lastEpgdumpExecutedService.read(1) != null){
                isLastEpgdumpExecuted = true;
            }
        }

        // PTx (switch Program/Channel)
        boolean isPTxByProgram = false;
        if(isFFmpeg && isPTx && isRecpt1 && isLastEpgdumpExecuted){
            isPTxByProgram = true;
        }
        boolean isPTxByChannel = false;
        if(isFFmpeg && isPTx && isRecpt1 && !isLastEpgdumpExecuted){
            programList = new ArrayList<>();
            isPTxByChannel = true;
            for(ChannelConfiguration channelConfiguration : channelConfigurationList){
                try {
                    Program program = new Program();
                    program.setPhysicalLogicalChannel(channelConfiguration.getPhysicalLogicalChannel());
                    program.setRemoteControllerChannel(channelConfiguration.getRemoteControllerChannel());
                    programList.add(program);
                }catch (NumberFormatException e){
                    log.error("invalid value {} {}", e.getMessage(), e);
                }
            }
        }
        assert programList != null;
        programList.sort(Comparator.comparingInt(Program::getRemoteControllerChannel));
        model.addAttribute("programList", programList);

        return "chukasa";
    }

    @PostMapping("")
    String play(Model model, @RequestBody @Validated ChukasaSettings chukasaSettings, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "index";
        }

        taskCoordinatorService.cancel();
        taskCoordinatorService.stop();
        taskCoordinatorService.remove();

        String userAgent = httpServletRequest.getHeader("user-agent");
        log.info("userAgent: {}", userAgent);

        FfmpegVcodecType ffmpegVcodecType = systemService.getFfmpegVcodecType(userAgent);
        if(ffmpegVcodecType == FfmpegVcodecType.UNKNOWN){
            return "index";
        }

        ChukasaUtility.initializeRunner(chukasaModelManagementComponent, systemConfiguration);
        if(chukasaModelManagementComponent.get().size() > 0){
            return "index";
        }

        log.info("ChukasaSettings -> {}", chukasaSettings.toString());

        ChukasaModel chukasaModel = new ChukasaModel();
        chukasaModel.setSystemConfiguration(systemConfiguration);
        chukasaModel.setHlsConfiguration(hlsConfiguration);
        chukasaModel.setChukasaSettings(chukasaSettings);

        chukasaModel.setUuid(UUID.randomUUID());
        chukasaModel.setAdaptiveBitrateStreaming(0);
        chukasaModel.setFfmpegVcodecType(ffmpegVcodecType);
        // TODO: -> system service
        if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC || ffmpegVcodecType == FfmpegVcodecType.HEVC_QSV){
            chukasaModel.setStreamFileExtension(".m4s");
        }

        chukasaModel = ChukasaUtility.operateEncodingSettings(chukasaModel);
        if(chukasaModel == null){
            return "index";
        }

        String servletRealPath = httpServletRequest.getSession().getServletContext().getRealPath("");
        String streamRootPath = commonUtilityService.getStreamRootPath(servletRealPath);
        chukasaModel.setStreamRootPath(streamRootPath);
        chukasaModel = ChukasaUtility.createChukasaDerectory(chukasaModel);
        chukasaModel = ChukasaUtility.calculateTimerTaskParameter(chukasaModel);

        String playlistURI = ChukasaUtility.buildM3u8URI(chukasaModel);
        if(playlistURI.equals("/")){
            return "index";
        }

        chukasaModelManagementComponent.create(0, chukasaModel);

        //chukasaTaskService.execute(0);
        taskCoordinatorService.execute();

        Html5Player html5PlayerModel = new Html5Player();
        html5PlayerModel.setPlaylistURI(playlistURI);
        model.addAttribute("html5Player", html5PlayerModel);



        if(chukasaBrowserDetector.isNativeSupported(userAgent)){
            return "embedded-native-player";
        }else if(chukasaBrowserDetector.isAlternativeSupported(userAgent)){
            return "embedded-" + ALTERNATIVE_HLS_PLAYER + "-player";
        }else{
            return "index";
        }
    }



    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    String stop(){
        taskCoordinatorService.cancel();
        taskCoordinatorService.stop();
        return "redirect:/chukasa/remove";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    String remove(Model model){
        taskCoordinatorService.remove();

        Html5Player html5PlayerModel = new Html5Player();
        html5PlayerModel.setPlaylistURI("");
        model.addAttribute("html5Player", html5PlayerModel);

        String userAgent = httpServletRequest.getHeader("user-agent");
        if(chukasaBrowserDetector.isNativeSupported(userAgent)){
            return "embedded-native-player";
        }else if(chukasaBrowserDetector.isAlternativeSupported(userAgent)){
            return "embedded-" + ALTERNATIVE_HLS_PLAYER + "-player";
        }else{
            return "index";
        }
    }
}