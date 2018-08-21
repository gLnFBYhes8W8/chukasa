package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.hirooka.chukasa.domain.activity.IRecorderActivity;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.recorder.M4vTranscodingStatus;
import pro.hirooka.chukasa.domain.model.recorder.RecordingStatus;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.operator.IProgramOperator;
import pro.hirooka.chukasa.domain.service.epg.IProgramService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RequestMapping("programs")
@Controller
public class ProgramController {

    private static final Logger log = LoggerFactory.getLogger(ProgramController.class);

    private final IProgramService programService;
    private final HttpServletRequest httpServletRequest;
    private final IRecorderActivity recorderActivity;

    @Autowired
    IProgramOperator programOperator;

    @Autowired
    public ProgramController(
            IProgramService programService,
            HttpServletRequest httpServletRequest,
            IRecorderActivity recorderActivity
    ) {
        this.programService = requireNonNull(programService);
        this.httpServletRequest = requireNonNull(httpServletRequest);
        this.recorderActivity = requireNonNull(recorderActivity);
    }

    @GetMapping
    String read(Model model){
        Date now = new Date();
        List<Program> programList = programService.readOneDayByNow(now.getTime());
        model.addAttribute("programList", programList);
        return "programs/list";
    }

    @GetMapping(value = "now")
    String readNow(Model model){
        model.addAttribute("programList", programService.readByNow(new Date().getTime()));
        return "programs/list";
    }

    @GetMapping(value = "{channelRemoteControl}")
    String read(@PathVariable int channelRemoteControl, Model model){
        model.addAttribute("programList", programService.read(channelRemoteControl));
        return "programs/list";
    }

    @GetMapping(value = "{channelRemoteControl}/day")
    String getOneDayFromNowByChannelRemoteControl(@PathVariable int channelRemoteControl, Model model){
        model.addAttribute("programList", programService.getOneDayFromNowByChannelRemoteControl(channelRemoteControl));
        return "programs/list";
    }

    @GetMapping(value = "day")
    String getOneDayFromNow(Model model){
        model.addAttribute("listOfProgramList", programOperator.getOneDayFromNow());
        return "programs/list";
    }

    @GetMapping(value = "{channelRemoteControl}/{date}")
    String read(@PathVariable int channelRemoteControl, @PathVariable String date, Model model){
        List<Program> programList = programService.read(channelRemoteControl, date);
        model.addAttribute("programList", programList);
        return "programs/list";
    }

    @PostMapping(value = "create")
    String create(@Validated ReservedProgram reservedProgram, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return read(model);
        }
        log.info("reservation -> {}", reservedProgram.toString());
        // TODO: ->  service
        ReservedProgram createdReservedProgram = new ReservedProgram();
        createdReservedProgram.setChannel(reservedProgram.getChannel());
        createdReservedProgram.setTitle(reservedProgram.getTitle());
        createdReservedProgram.setDetail(reservedProgram.getDetail());
        createdReservedProgram.setStart(reservedProgram.getStart());
        createdReservedProgram.setBegin(reservedProgram.getStart());
        createdReservedProgram.setEnd(reservedProgram.getEnd());
        createdReservedProgram.setDuration(reservedProgram.getDuration());
        createdReservedProgram.setChannelRecording(reservedProgram.getChannelRecording());
        createdReservedProgram.setChannelRemoteControl(reservedProgram.getChannelRemoteControl());
        createdReservedProgram.setChannelName(reservedProgram.getChannelName());
        createdReservedProgram.setBeginDate(reservedProgram.getBeginDate());
        createdReservedProgram.setEndDate(reservedProgram.getEndDate());
        createdReservedProgram.setStartRecording(reservedProgram.getBegin());
        createdReservedProgram.setStopRecording(reservedProgram.getEnd());
        createdReservedProgram.setRecordingDuration(reservedProgram.getDuration());
        createdReservedProgram.setFileName("");
        createdReservedProgram.setRecordingStatus(RecordingStatus.Reserved);
        createdReservedProgram.setM4vTranscodingStatus(M4vTranscodingStatus.None);

        log.info("createdReservedProgram -> {}", createdReservedProgram.toString());
        //recorderService.create(createdReservedProgram);
        recorderActivity.create(createdReservedProgram);

        String referer = httpServletRequest.getHeader("Referer");
        return "redirect:"+ referer;
    }
}

