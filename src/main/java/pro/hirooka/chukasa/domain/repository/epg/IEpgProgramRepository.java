//package pro.hirooka.chukasa.domain.repository.epg;
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
//import org.springframework.stereotype.Repository;
//import pro.hirooka.chukasa.domain.model.recorder.Program;
//
//import java.util.List;
//
//@Repository
//public interface IEpgProgramRepository extends MongoRepository<Program, String> {
//
//    // ex. {$and:[{'piyo':{$eq:123}},{'channel':{$regex:/^GR_/}}]}
//    //@Query("{$and:[{'piyo':{$eq:?0}},{'channel':{$regex:?1}}]}")
//    List<Program> findAllByChannel(String channel);
//    List<Program> findAllByChannelRecording(int channelRecording);
//
//    @Query("{$and:[{'channelRecording':{$eq:?0}},{'start':{$lte:?1}},{'end':{$gte:?1}}]}")
//    Program findOneByChannelRecordingAndNowLike(String channelRecording, long now);
//
//    // spring-data-mongodb:1.9.x.RELEASE から spring-data-mongodb:1.10.0.RELEASE にすると機能せず
//    @Query("{$and:[{'start':{$lte:?0}},{'end':{$gte:?0}},{'channelRecording':{$ne:0}}]}")
//    List<Program> findAllByNowLike(long now);
//
//    @Query("{$and:[{'start':{$lte:?1}},{'end':{$gte:?1}},{'channelRecording':{$ne:?0}}]}")
//    List<Program> findOneByChannelRecordingAndNowLike(int channelRecording, long now);
//
//    @Query("{$and:[{'channelRecording':{$eq:?0}},{'start':{$lte:?2}},{'start':{$gte:?1}}]}")
//    Program findOneByChannelRecordingAndFromAndToLike(int channelRecording, long from, long to);
//
//    @Query("{$and:[{'begin':{$lte:?0}},{'end':{$lte:?0}}]}")
//    List<Program> deleteByDate(long date);
//
//    @Query("{'end':{$lte:?0}}")
//    List<Program> deleteByEnd(long end);
//
//    @Query("{$and:[{'begin':{$gte:?0}},{'end':{$lte:?1}},{'physicalLogicalChannel':{$ne:0}}]}")
//    List<Program> findAllByBeginAndEndLike(long begin, long end);
//
////    @Query("{$and:[{'begin':{$lte:?0}},{'end':{$lte:?0}}]}")
////    Long deleteProgramByDate(long date); // 機能しない...
//}
