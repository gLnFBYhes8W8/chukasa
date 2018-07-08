package pro.hirooka.chukasa.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.recorder.M4vTranscodingStatus;
import pro.hirooka.chukasa.domain.model.recorder.RecordingStatus;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.service.epg.IProgramService;
import pro.hirooka.chukasa.domain.service.recorder.IRecorderService;

import java.util.Date;
import java.util.List;

// TODO: refactor

@RestController
@RequestMapping("api/v1/programs")
public class ProgramRestController {

    private static final Logger log = LoggerFactory.getLogger(ProgramRestController.class);

    @Autowired
    IProgramService programService;
    @Autowired
    IRecorderService recorderService;

    @RequestMapping(method = RequestMethod.GET)
    List<Program> read(){
        Date now = new Date();
        List<Program> programList = programService.readOneDayByNow(now.getTime());
        return programList;
    }

    @RequestMapping(value = "now", method = RequestMethod.GET)
    List<Program> readNow(){
        Date now = new Date();
        List<Program> programList = programService.readByNow(now.getTime());
        return programList;
    }

    @RequestMapping(value = "{channelRecording}", method = RequestMethod.GET)
    List<Program> read(@PathVariable int channelRecording){
        List<Program> programList = programService.read(channelRecording);
        return programList;
    }

    @RequestMapping(value = "{channelRecording}/{date}", method = RequestMethod.GET)
    List<Program> read(@PathVariable int channelRecording, @PathVariable String date){
        List<Program> programList = programService.read(channelRecording, date);
        return programList;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    ReservedProgram create(@Validated ReservedProgram reservedProgram){

        log.info("reservation -> {}", reservedProgram.toString());
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
        return recorderService.create(createdReservedProgram);
    }
}

