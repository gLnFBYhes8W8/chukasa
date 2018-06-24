package pro.hirooka.chukasa.domain.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.domain.config.common.CommonConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
import pro.hirooka.chukasa.domain.model.common.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.common.ChannelConfigurationWrapper;
import pro.hirooka.chukasa.domain.model.common.Tuner;
import pro.hirooka.chukasa.domain.model.common.TunerWrapper;
import pro.hirooka.chukasa.domain.model.common.type.TunerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.STREAM_ROOT_PATH_NAME;

@Component
public class CommonUtilityService implements ICommonUtilityService {

    private static final Logger log = LoggerFactory.getLogger(CommonUtilityService.class);

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

    @Override
    public TunerType getTunerType(int channel) {
        for(ChannelConfiguration channelConfiguration : getChannelConfigurationList()){
            if(channelConfiguration.getPhysicalLogicalChannel() == channel){
                if(channelConfiguration.getChannelType() == ChannelType.GR){
                    return TunerType.GR;
                }else if(channelConfiguration.getChannelType() == ChannelType.BS){
                    return TunerType.BS;
                }
            }
        }
        return null;
    }
}
