package like;

import DBService.LikeRecordService;
import object.LikeRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @Description:
 * @auther: zhaiyao
 * @date: 2020/5/13
 */
public class LikeImplement implements Like {
    private final int EX_seconds = 3600*24*30;//点赞信息过期时间：一个月
    private String kafka_topic = "old_MicroBlog_like";
    @Autowired
    private JedisPool jedisPoll;
    @Autowired
    private LikeRecordService likeRecordService;
    @Autowired
    private Producer kafkaProducer;


    @Override
    public void like(int uid, int statusId) {
        if(!isLiked(uid,statusId)){
            String key = uid+"_"+statusId;
            if(isMicroBlogNew(statusId)){
                Jedis jedis = jedisPoll.getResource();
                jedis.setex(key,EX_seconds,"1");
            }else{
                LikeRecord likeRecord = new LikeRecord();
                likeRecord.setUid(uid);
                likeRecord.setStatusId(statusId);
                List<LikeRecord> record = likeRecordService.select(likeRecord);
                if(record==null||record.size()==0){
                    ProducerRecord<String,String> kafkaRecord = new ProducerRecord<String,String>("kafka_topic",key);
                    kafkaProducer.send(kafkaRecord);
                }
            }
        }
    }

    @Override
    public boolean isLiked(int uid, int statusId) {
        if(isMicroBlogNew(statusId)){
            String key = uid+"_"+statusId;
            Jedis jedis = jedisPoll.getResource();
            String result = jedis.get(key);
            if(result==null){
                return false;
            }
        }else{
            LikeRecord likeRecord = new LikeRecord();
            likeRecord.setUid(uid);
            likeRecord.setStatusId(statusId);
            List<LikeRecord> result = likeRecordService.select(likeRecord);
            if(result==null||result.size()==0){
                return false;
            }
        }
        return true;
    }

    private boolean isMicroBlogNew(int statusId){
        //查询微博发布时间（单位秒）
        int publicTime = queryMicroBlogPublicTime(statusId);
        int now = (int)System.currentTimeMillis()/1000;
        return now-publicTime<EX_seconds;
    }
}
