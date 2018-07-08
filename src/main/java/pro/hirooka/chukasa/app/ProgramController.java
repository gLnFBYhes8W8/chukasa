package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pro.hirooka.chukasa.domain.activity.IRecorderActivity;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.recorder.M4vTranscodingStatus;
import pro.hirooka.chukasa.domain.model.recorder.RecordingStatus;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.service.epg.IProgramService;
import pro.hirooka.chukasa.domain.service.recorder.IRecorderService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("programs")
public class ProgramController {

    private static final Logger log = LoggerFactory.getLogger(ProgramController.class);

    @Autowired
    IProgramService programService;
    @Autowired
    IRecorderService recorderService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private IRecorderActivity recorderActivity;

    @RequestMapping(method = RequestMethod.GET)
    String read(Model model){
        //List<Program> programList = programTableService.read();
        Date now = new Date();
        List<Program> programList = programService.readOneDayByNow(now.getTime());
        model.addAttribute("programList", programList);
        return "programs/list";
    }

    @RequestMapping(value = "now", method = RequestMethod.GET)
    String readNow(Model model){
        Date now = new Date();
        List<Program> programList = programService.readByNow(now.getTime());
        model.addAttribute("programList", programList);
        return "programs/list";
    }

    @RequestMapping(value = "{channelRecording}", method = RequestMethod.GET)
    String read(@PathVariable int channelRecording, Model model){
        List<Program> programList = programService.read(channelRecording);
        model.addAttribute("programList", programList);
        return "programs/list";
    }

    @RequestMapping(value = "{channelRecording}/{date}", method = RequestMethod.GET)
    String read(@PathVariable int channelRecording, @PathVariable String date, Model model){
        List<Program> programList = programService.read(channelRecording, date);
        model.addAttribute("programList", programList);
        return "programs/list";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
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

