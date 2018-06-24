package pro.hirooka.chukasa.domain.service.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Component
public class Recorder {

    private static final Logger log = LoggerFactory.getLogger(Recorder.class);

    @Autowired
    SystemConfiguration systemConfiguration;

    @Autowired
    RecorderRunner recorderRunner;

    Map<Integer, ScheduledFuture> scheduledFutureMap = new HashMap<>();

    @Async
    public void reserve(ReservedProgram reservedProgram){
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        TaskScheduler taskScheduler = new ConcurrentTaskScheduler(scheduledExecutorService);
        Date date = new Date(reservedProgram.getStart());
        recorderRunner.setReservedProgram(reservedProgram);
        ScheduledFuture scheduledFuture = taskScheduler.schedule(recorderRunner, date);
        scheduledFutureMap.put(reservedProgram.getId(), scheduledFuture);
        log.info("scheduler: {}", date.toString());
    }

    @Async
    public void reserve(List<ReservedProgram> reservedProgramList){
        reservedProgramList.forEach(reservedProgram -> {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            TaskScheduler taskScheduler = new ConcurrentTaskScheduler(scheduledExecutorService);
            Date date = new Date(reservedProgram.getStart());
            recorderRunner.setReservedProgram(reservedProgram);
            ScheduledFuture scheduledFuture = taskScheduler.schedule(recorderRunner, date);
            scheduledFutureMap.put(reservedProgram.getId(), scheduledFuture);
        });
    }

    public void cancel(int id){
        if(scheduledFutureMap.containsKey(id)){
            scheduledFutureMap.get(id).cancel(true);
        }
    }

    public void cancelAll(){
        scheduledFutureMap.values().stream().forEach(scheduledFuture -> {
            scheduledFuture.cancel(true);
        });
    }

    public Map<Integer, ScheduledFuture> getScheduledFutureMap() {
        return scheduledFutureMap;
    }
}
