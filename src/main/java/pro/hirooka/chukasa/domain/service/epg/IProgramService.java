package pro.hirooka.chukasa.domain.service.epg;

import pro.hirooka.chukasa.domain.model.epg.Program;

import java.util.List;

public interface IProgramService {
    Program create(Program program);
    List<Program> read();
    List<Program> read(int channelRemoteControl);
    List<Program> readByBeginDate(String beginDate);
    List<Program> read(int ch, String beginDate);
    List<Program> readByNow(long now);
    List<Program> readOneDayByNow(long now);
    Program read(String id);
    Program readNow(int ch, long now);
    Program update(Program Program);
    void delete(String id);
    List<Program> deleteByEnd(long threshold);
    void deleteAll();
    int getNumberOfPhysicalLogicalChannels();
    List<Program> getOneDayFromNowByChannelRemoteControl(int channelRemoteControl);
    List<List<Program>> getOneDayFromNow();
}
