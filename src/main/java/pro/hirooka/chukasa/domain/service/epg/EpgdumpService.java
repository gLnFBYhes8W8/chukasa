package pro.hirooka.chukasa.domain.service.epg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.epg.EpgConfiguration;
import pro.hirooka.chukasa.domain.model.epg.LastEpgdumpExecuted;
import pro.hirooka.chukasa.domain.model.epg.type.EpgdumpStatus;
import pro.hirooka.chukasa.domain.service.common.ICommonUtilityService;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.parser.IEpgdumpParser;
import pro.hirooka.chukasa.domain.service.epg.runner.IEpgdumpRunnerService;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class EpgdumpService implements IEpgdumpService {

    private static final Logger log = LoggerFactory.getLogger(EpgdumpService.class);

    @Autowired
    EpgConfiguration epgConfiguration;
    @Autowired
    SystemConfiguration systemConfiguration;
    @Autowired
    IEpgdumpParser epgDumpParser;
    @Autowired
    ILastEpgdumpExecutedService lastEPGDumpExecutedService;
    @Autowired
    ISystemService systemService;
    @Autowired
    ICommonUtilityService commonUtilityService;
    @Autowired
    private IEpgdumpRunnerService epgdumpRunnerService;

    @PostConstruct
    public void init(){

        // epgdump へのパスが存在していて，
        // 一度も情報を取得していない，あるいは前回情報を取得してから一定期間経過している場合，
        // アプリケーション起動時に別スレッドで情報を取得する．
        if(systemService.isMongoDB() && isEpgdump() && !isEpgdumpExecuted()) {
            LastEpgdumpExecuted lastEpgdumpExecuted = lastEPGDumpExecutedService.read(1);
            if (lastEpgdumpExecuted == null) {
                log.info("lastEpgdumpExecuted == null -> runEPGDump()");
                //runEPGDump();
                epgdumpRunnerService.submit(commonUtilityService.getChannelConfigurationList());
            } else {
                Date date = new Date();
                long now = date.getTime();
                long last = lastEpgdumpExecuted.getDate();
                long diff = last - now;
                log.info("now = {}, last epgdump executed = {}, diff = {}", convertMilliToDate(now), convertMilliToDate(last), diff);
                if (now - last > epgConfiguration.getEpgdumpExecuteOnBootIgnoreInterval()) {
                    //runEPGDump();
                    epgdumpRunnerService.submit(commonUtilityService.getChannelConfigurationList());
                }else{
                    log.info("chukasa.epgdump-execute-on-boot-ignore-interval > previous boot");
                }
            }
        }else{
            log.info("runEPGDump() is not executed because it is running now or MongoDB is not running or epgdump does not exist.");
        }
    }

    @Override
    public boolean isEpgdump() {
        File epgdump = new File(systemConfiguration.getEpgdumpPath());
        return epgdump.exists();
    }

    String convertMilliToDate(long milli){
        Instant instant = Instant.ofEpochMilli(milli);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        return zonedDateTime.format(dateTimeFormatter);
    }

    @Scheduled(cron = "${epg.epgdump-execute-schedule-cron}")
    void execute(){

        if(isEpgdump() && systemService.isMongoDB() ) {
            log.info("cheduled cron -> runEPGDump()");
            //runEPGDump();
            epgdumpRunnerService.submit(commonUtilityService.getChannelConfigurationList());
        }
    }

    void runEPGDump(){

        log.info("run runEPGDump()");
        //List<ChannelConfiguration> channelConfigurationList = commonUtilityService.getChannelConfigurationList();
//        Resource resource = new ClassPathResource(epgdumpConfiguration.getPhysicalChannelMap());
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            Map<String, String> epgdumpChannelMap = objectMapper.readValue(resource.getInputStream(), HashMap.class);
//            log.info(epgdumpChannelMap.toString());

//            EPGDumpRunner epgDumpRunner = new EPGDumpRunner(systemConfiguration, epgdumpConfiguration, epgDumpParser, lastEPGDumpExecutedService, epgdumpChannelMap);
        //EPGDumpRunner epgDumpRunner = new EPGDumpRunner(systemConfiguration, epgdumpConfiguration, epgDumpParser, lastEPGDumpExecutedService, channelConfigurationList);
        //epgdumpRunner.setChannelConfigurationList(channelConfigurationList);
        //epgdumpAsyncConfigurer.getAsyncExecutor().execute(epgdumpRunner);

//        } catch (IOException e) {
//            log.error("invalid epgdump_channel_map.json: {} {}", e.getMessage(), e);
//        }
    }

    boolean isEpgdumpExecuted(){
        String[] command = {"/bin/sh", "-c", "ps aux | grep epgdump.sh"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            while((str = bufferedReader.readLine()) != null){
                log.info(str);
                if(str.contains("epgdump.sh") && !str.contains("grep")){
                    bufferedReader.close();
                    process.destroy();
                    return true;
                }
            }
            bufferedReader.close();
            process.destroy();
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public EpgdumpStatus getStatus() {
//        int acvitve = epgdumpAsyncConfigurer.threadPoolTaskExecutor().getActiveCount();
//        if(acvitve == 1){
//            return EpgdumpStatus.RUNNING;
//        }else if(acvitve == 0){
//            return EpgdumpStatus.STOPPED;
//        }
        return EpgdumpStatus.STOPPED; // TODO:
    }
}

