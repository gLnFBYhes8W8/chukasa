package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.hirooka.chukasa.api.v1.helper.IChukasaBrowserDetector;
import pro.hirooka.chukasa.domain.config.common.CommonConfiguration;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.model.aaa.ChukasaUserDetails;
import pro.hirooka.chukasa.domain.model.epg.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.recorder.RecordingProgramModel;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IEpgService;
import pro.hirooka.chukasa.domain.service.epg.IProgramService;
import pro.hirooka.chukasa.domain.service.recorder.IRecorderService;
import pro.hirooka.chukasa.domain.service.recorder.IRecordingProgramManagementComponent;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;

@RequestMapping("/menu")
@Controller
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    CommonConfiguration commonConfiguration;
    @Autowired
    SystemConfiguration systemConfiguration;
    @Autowired
    ISystemService systemService;
    @Autowired
    IProgramService programService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    IRecordingProgramManagementComponent recordingProgramManagementComponent;
//    @Autowired
//    IRecorderService recorderService;
    @Autowired
    IChukasaBrowserDetector chukasaBrowserDetector;
    @Autowired
    IEpgService epgService;

    // TODO: logic -> service

    @RequestMapping
    String manu(@AuthenticationPrincipal ChukasaUserDetails chukasaUserDetails, Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof ChukasaUserDetails){
            ChukasaUserDetails authenticationChukasaUserDetails = ChukasaUserDetails.class.cast(authentication.getPrincipal());
            String username = authenticationChukasaUserDetails.getUsername();
        }
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(httpServletRequest.isUserInRole("ADMIN") || httpServletRequest.isUserInRole("ROLE_ADMIN")){
            log.info("ADMIN");
        }
        // SecurityExpressionRoot
        if(chukasaUserDetails == null){
            chukasaUserDetails = new ChukasaUserDetails();
            chukasaUserDetails.setUsername("inMmemory");
        }
        model.addAttribute("user", chukasaUserDetails);

//        if(epgdumpService.getStatus().equals(EpgdumpStatus.RUNNING)){
//            log.info("EpgdumpService is running.");
//            // TODO:
//        }

        boolean isSupported = false;
        String userAgent = httpServletRequest.getHeader("user-agent");
        if(chukasaBrowserDetector.isNativeSupported(userAgent) || chukasaBrowserDetector.isAlternativeSupported(userAgent)){
            isSupported = true;
        }
        log.info("{} : {}", isSupported, userAgent);

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

        // Okkake
        List<VideoFile> okkakeVideoFileModelList = new ArrayList<>();
        List<RecordingProgramModel> recordingProgramModelList = recordingProgramManagementComponent.get();
        for(RecordingProgramModel recordingProgramModel : recordingProgramModelList){
            Date now = new Date();
            if(recordingProgramModel.getStopRecording() > now.getTime() && now.getTime() > recordingProgramModel.getStartRecording()){
                String file = systemConfiguration.getFilePath() + FILE_SEPARATOR + recordingProgramModel.getFileName();
                if(new File(file).exists()){
                    VideoFile videoFileModel = new VideoFile();
                    videoFileModel.setName(recordingProgramModel.getFileName());
                    okkakeVideoFileModelList.add(videoFileModel);
                }
            }
        }
        if(isMongoDB) {
//            List<ReservedProgram> reservedProgramList = recorderService.read();
//            for (ReservedProgram reservedProgram : reservedProgramList) {
//                Date now = new Date();
//                if (reservedProgram.getStopRecording() > now.getTime() && now.getTime() > reservedProgram.getStartRecording()) {
//                    String file = systemConfiguration.getFilePath() + FILE_SEPARATOR + reservedProgram.getFileName();
//                    if (new File(file).exists()) {
//                        boolean isDuplicated = false;
//                        for (RecordingProgramModel recordingProgramModel : recordingProgramModelList) {
//                            if (recordingProgramModel.getFileName().equals(reservedProgram.getFileName())) {
//                                isDuplicated = true;
//                                break;
//                            }
//                        }
//                        if (!isDuplicated) {
//                            VideoFile videoFileModel = new VideoFile();
//                            videoFileModel.setName(reservedProgram.getFileName());
//                            okkakeVideoFileModelList.add(videoFileModel);
//                        }
//                    }
//                }
//            }
        }

        model.addAttribute("isSupported", isSupported);
        model.addAttribute("isPTxByChannel", isPTxByChannel);
        model.addAttribute("isPTxByProgram", isPTxByProgram);
        model.addAttribute("isWebCamera", isWebCamera);
        model.addAttribute("videoFileModelList", videoFileModelList);
        model.addAttribute("okkakeVideoFileModelList", okkakeVideoFileModelList);
        model.addAttribute("programList", programList);

        return "menu";
    }
}

