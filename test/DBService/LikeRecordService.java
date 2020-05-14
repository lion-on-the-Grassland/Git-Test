package DBService;

import object.LikeRecord;

import java.util.List;

public interface LikeRecordService {
    public int insert(LikeRecord likeRecord);
    public int batchInsert(List<LikeRecord> likeRecords);
    public int delete(LikeRecord likeRecord);
    public List<LikeRecord> select(LikeRecord likeRecord);
}
