package pro.hirooka.chukasa.domain.service.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class FfmpegStopperService implements IFfmpegStopperService {

    private static final Logger log = LoggerFactory.getLogger(FfmpegStopperService.class);

    @Autowired
    IChukasaModelManagementComponent chukasaModelManagementComponent;

    @Override
    public Future<Integer> stop() {

        final String[] commandArray = {"ps", "aux"};
        final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        try {
            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str;
            while((str = bufferedReader.readLine()) != null){
                log.debug("{}", str);
                final String trimmedString = str.trim();
                // TODO: マズー
                if ((trimmedString.matches(".*libx264.*") && trimmedString.matches(".*mpegts.*"))
                        || (trimmedString.matches(".*h264_qsv.*") && trimmedString.matches(".*mpegts.*"))
                        || (trimmedString.matches(".*h264_omx.*") && trimmedString.matches(".*mpegts.*"))
                        || (trimmedString.matches(".*h264_nvenc.*") && trimmedString.matches(".*hls.*"))
                        || (trimmedString.matches(".*hevc_nvenc.*") && trimmedString.matches(".*hls.*"))
                        || (trimmedString.matches(".*hevc_qsv.*") && trimmedString.matches(".*hls.*"))
                        ) {
                    final String[] trimmedStringArray = trimmedString.split(" ");
                    final List<String> pidList = new ArrayList<>();
                    for(int i = 0; i < trimmedStringArray.length; i++) {
                        if (!(trimmedStringArray[i].equals(""))) {
                            pidList.add(trimmedStringArray[i]);
                        }
                    }
                    String pid = pidList.get(1);
                    log.debug("{}", pid);
                    stopPID(pid);
                }
            }
            bufferedReader.close();
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
            process.destroy();

            // TODO: reconsider
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            // TODO:
        }
        chukasaModelManagementComponent.deleteAll();
        log.info("all ChukasaModels have been deleted.");

        return new AsyncResult<>(0);
    }

    private void stopPID(String pid){

        final String[] commandArray = {"kill", "-KILL", pid };
        final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        try {
            final Process process = processBuilder.start();
            log.info("{} stopped ffmpeg (PID: {}).", this.getClass().getName(), pid);
//            process.getInputStream().close();
//            process.getErrorStream().close();
//            process.getOutputStream().close();
//            process.destroy();
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
    }

}

