package pro.hirooka.chukasa.chukasa_common.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.chukasa_common.domain.configuration.CommonConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.enums.ChannelType;
import pro.hirooka.chukasa.chukasa_common.domain.model.ChannelConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.model.ChannelConfigurationWrapper;
import pro.hirooka.chukasa.chukasa_common.domain.model.Tuner;
import pro.hirooka.chukasa.chukasa_common.domain.model.TunerWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static pro.hirooka.chukasa.chukasa_common.domain.constants.ChukasaConstants.FILE_SEPARATOR;
import static pro.hirooka.chukasa.chukasa_common.domain.constants.ChukasaConstants.STREAM_ROOT_PATH_NAME;

@Slf4j
@Component
public class CommonUtilityService implements ICommonUtilityService {

    @Autowired
    private CommonConfiguration commonConfiguration;

    @Override
    public List<ChannelConfiguration> getChannelConfigurationList() {
        List<ChannelConfiguration> channelConfigurationList = new ArrayList<>();
        try {
            Resource resource = new ClassPathResource(commonConfiguration.getChannelConfiguration());
            ObjectMapper objectMapper = new ObjectMapper();
            channelConfigurationList = objectMapper.readValue(resource.getInputStream(), ChannelConfigurationWrapper.class).getChannelConfigurationList();
            log.info("channelConfigurationList = {}", channelConfigurationList.toString());
        } catch (IOException e) {
            log.error("invalid channel_settings.json: {} {}", e.getMessage(), e);
        }
        return channelConfigurationList;
    }

    @Override
    public String getStreamRootPath(String servletRealPath) {
        if(servletRealPath.substring(servletRealPath.length() - 1).equals(FILE_SEPARATOR)) {
            return servletRealPath + STREAM_ROOT_PATH_NAME; // e.g. Tomcat
        } else {
            return servletRealPath + FILE_SEPARATOR + STREAM_ROOT_PATH_NAME; // e.g. Jetty
        }
    }

    @Override
    public List<Tuner> getTunerList() {

        final String path = ICommonUtilityService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        final String[] pathArray = path.split(FILE_SEPARATOR);
        String currentPath = "";
        for(int i = 1; i < pathArray.length - 3; i++){
            currentPath = currentPath + FILE_SEPARATOR + pathArray[i];
        }
        log.info("currentPath = {}", currentPath);

        List<Tuner> tunerList = new ArrayList<>();
        try {
            Resource resource = new FileSystemResource(currentPath + FILE_SEPARATOR + commonConfiguration.getTuner());
            if(!resource.exists()){
                resource = new ClassPathResource(commonConfiguration.getTuner());
            }
            final ObjectMapper objectMapper = new ObjectMapper();
            tunerList = objectMapper.readValue(resource.getInputStream(), TunerWrapper.class).getTunerList();
            log.info("tunerList = {}", tunerList.toString());
        } catch (IOException e) {
            log.error("invalid tuner.json: {} {}", e.getMessage(), e);
        }
        return tunerList;
    }

    @Override
    public ChannelType getChannelType(int physicalLogicalChannel) {
        for(ChannelConfiguration channelConfiguration : getChannelConfigurationList()){
            if(channelConfiguration.getPhysicalLogicalChannel() == physicalLogicalChannel){
                if(channelConfiguration.getChannelType() == ChannelType.GR){
                    return ChannelType.GR;
                }else if(channelConfiguration.getChannelType() == ChannelType.BS){
                    return ChannelType.BS;
                }
            }
        }
        return null;
    }
}
