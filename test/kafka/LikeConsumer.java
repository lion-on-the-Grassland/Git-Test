package kafka;

import DBService.LikeRecordService;
import object.LikeRecord;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @Description:从kafka消费点赞记录存入Mysql
 * @auther: zhaiyao
 * @date: 2020/5/14
 */
@Component
public class LikeConsumer  {
    private String kafka_topic = "old_MicroBlog_like";
    @Autowired
    private KafkaConsumer<String,String> kafkaConsumer;
    @Autowired
    private LikeRecordService likeRecordService;

    @PostConstruct
    public void init(){
        List<String> topicList = new ArrayList<>();
        topicList.add(kafka_topic);
        kafkaConsumer.subscribe(topicList);
        while(1==1){
            ConsumerRecords<String,String> kafkaRecords= kafkaConsumer.poll(Duration.ofMillis(50));
            if(!kafkaRecords.isEmpty()){
                List<LikeRecord> likeRecords = new ArrayList<>();
                for(ConsumerRecord consumerRecord:kafkaRecords){
                    String value = (String)consumerRecord.value();
                    String[] ids = value.split("_");
                    LikeRecord likeRecord = new LikeRecord();
                    likeRecord.setStatusId(Integer.valueOf(ids[1]));
                    likeRecord.setUid(Integer.valueOf(ids[0]));
                    likeRecords.add(likeRecord);
                }
                likeRecordService.batchInsert(likeRecords);
                kafkaConsumer.commitSync();
            }

        }
    }
}
