package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pro.hirooka.chukasa.api.v1.helper.ChukasaUtility;
import pro.hirooka.chukasa.api.v1.helper.IChukasaBrowserDetector;
import pro.hirooka.chukasa.domain.config.common.CommonConfiguration;
import pro.hirooka.chukasa.domain.config.common.HyarukaConfiguration;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.config.common.type.StreamingType;
import pro.hirooka.chukasa.domain.config.hls.HlsConfiguration;
import pro.hirooka.chukasa.domain.model.app.Html5Player;
import pro.hirooka.chukasa.domain.model.epg.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;
import pro.hirooka.chukasa.domain.service.common.IHyarukaClientService;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IEpgService;
import pro.hirooka.chukasa.domain.service.epg.IProgramService;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;
import pro.hirooka.chukasa.domain.service.hls.ICoordinatorService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.ALTERNATIVE_HLS_PLAYER;

@RequestMapping("/chukasa")
@Controller
public class ChukasaController {

    private static final Logger log = LoggerFactory.getLogger(ChukasaController.class);

    @Autowired
    CommonConfiguration commonConfiguration;
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
    ICoordinatorService taskCoordinatorService;
    @Autowired
    HyarukaConfiguration hyarukaConfiguration;
    @Autowired
    IHyarukaClientService hyarukaClientService;
    @Autowired
    IEpgService epgService;
    @Autowired
    IProgramService programService;

    // TODO: refactor

    @GetMapping("")
    public String index(Model model){
        String url = "";
        Html5Player html5Player = new Html5Player();
        html5Player.setPlaylistURI(url);
        model.addAttribute("html5Player", html5Player);

        boolean isFFmpeg = systemService.isFFmpeg();
        boolean isMongoDB = systemService.isMongoDB();
        boolean isWebCamera = systemService.isWebCamera();
// PTx
        List<Program> programList = new ArrayList<>();
        boolean isLastEpgdumpExecuted = false;

        List<ChannelConfiguration> channelConfigurationList = epgService.getChannelConfigurationList();

        if(isMongoDB){
            programList = programService.readByNow(new Date().getTime());
            programList = programList.stream().sorted(Comparator.comparing(Program::getChannelRecording)).collect(Collectors.toList());
//            if(programList != null && lastEpgdumpExecutedService.read(1) != null && programTableService.getNumberOfPhysicalChannels() >= epgdumpChannelMap.size()){
//            if(programList != null && programList.size() > 0 && lastEpgdumpExecutedService.read(1) != null && programTableService.getNumberOfPhysicalLogicalChannels() >= channelConfigurationList.size()){
            if(programList != null && programList.size() > 0 && epgService.readLatestEpgAcquisition(1) != null){
                isLastEpgdumpExecuted = true;
            }
        }

        // PTx (switch Program/Channel)
        boolean isPTxByProgram = false;
        if(isFFmpeg && isLastEpgdumpExecuted){
            isPTxByProgram = true;
        }
        boolean isPTxByChannel = false;
        if(isFFmpeg && !isLastEpgdumpExecuted){
            programList = new ArrayList<>();
            isPTxByChannel = true;
            for(ChannelConfiguration channelConfiguration : channelConfigurationList){
                try {
                    Program program = new Program();
                    program.setChannelRecording(channelConfiguration.getChannelRecording());
                    program.setChannelRemoteControl(channelConfiguration.getChannelRemoteControl());
                    programList.add(program);
                }catch (NumberFormatException e){
                    log.error("invalid value {} {}", e.getMessage(), e);
                }
            }
        }
        assert programList != null;
        programList.sort(Comparator.comparingInt(Program::getChannelRemoteControl));
        model.addAttribute("programList", programList);

        // FILE
        List<VideoFile> videoFileModelList = new ArrayList<>();
        File fileDirectory = new File(systemConfiguration.getFilePath());
        File[] fileArray = fileDirectory.listFiles();
        if(fileArray != null) {
            String[] videoFileExtensionArray = commonConfiguration.getVideoFileExtension();
            List<String> videoFileExtensionList = Arrays.asList(videoFileExtensionArray);
            for (File file : fileArray) {
                for(String videoFileExtension : videoFileExtensionList){
                    if(file.getName().endsWith("." + videoFileExtension)){
                        VideoFile videoFileModel = new VideoFile();
                        videoFileModel.setName(file.getName());
                        videoFileModelList.add(videoFileModel);
                    }
                }
            }
        }else{
            log.warn("'{}' does not exist.", fileDirectory);
        }
        videoFileModelList.sort(Comparator.comparing(VideoFile::getName));

        model.addAttribute("isPTxByChannel", isPTxByChannel);
        model.addAttribute("isPTxByProgram", isPTxByProgram);
        model.addAttribute("isWebCamera", isWebCamera);
        model.addAttribute("videoFileModelList", videoFileModelList);

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

        String unixDomainSocketPath = "";
        if(hyarukaConfiguration.isEnabled() && hyarukaConfiguration.isUnixDomainSocketEnabled()){
            unixDomainSocketPath = hyarukaClientService.getUnixDomainSocketPath(epgService.getTunerType(chukasaSettings.getChannelRecording()), chukasaSettings.getChannelRecording());
        }

        ChukasaModel chukasaModel = new ChukasaModel();
        chukasaModel.setSystemConfiguration(systemConfiguration);
        chukasaModel.setHlsConfiguration(hlsConfiguration);
        chukasaModel.setUnixDomainSocketPath(unixDomainSocketPath);
        chukasaSettings.setTunerType(epgService.getTunerType(chukasaSettings.getChannelRecording()));
        log.info("ChukasaSettings -> {}", chukasaSettings.toString());
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
        String streamRootPath = systemService.getStreamRootPath(servletRealPath);
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

        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
            if(hyarukaConfiguration.isEnabled() && chukasaModel.getChukasaSettings().getStreamingType() == StreamingType.TUNER) {
                final String HYARUKA_USERNAME = hyarukaConfiguration.getUsername();
                final String HYARUKA_PASSWORD = hyarukaConfiguration.getPassword();
                final String HYARUKA_SCHEME = hyarukaConfiguration.getScheme().name();
                final String HYARUKA_HOST = hyarukaConfiguration.getHost();
                final int HYARUKA_PORT = hyarukaConfiguration.getPort();
                final String HYARUKA_API_VERSION = hyarukaConfiguration.getApiVersion();
                final String HYARUKA_URI = HYARUKA_SCHEME.toLowerCase() + "://"
                        //+ HYARUKA_USERNAME + ":" + HYARUKA_PASSWORD + "@"
                        + HYARUKA_HOST + ":" + HYARUKA_PORT
                        + "/api/" + HYARUKA_API_VERSION + "/streams/"
                        + chukasaModel.getChukasaSettings().getTunerType().name() + "/" + chukasaModel.getChukasaSettings().getChannelRecording();
                log.info("{}", HYARUKA_URI);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getInterceptors().add(
                        new BasicAuthorizationInterceptor(HYARUKA_USERNAME, HYARUKA_PASSWORD));
                //restTemplate.delete(HYARUKA_URI);
                restTemplate.exchange(HYARUKA_URI, HttpMethod.DELETE, null, String.class);
            }
        });

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
