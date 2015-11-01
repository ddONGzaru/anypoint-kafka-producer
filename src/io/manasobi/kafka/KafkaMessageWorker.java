package io.manasobi.kafka;

import com.google.common.collect.Lists;
import io.manasobi.io.CSVFileWriter;
import io.manasobi.io.DataSetReader;
import io.manasobi.io.DataSetWriter;
import io.manasobi.utils.DateUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import tv.anypoint.domain.ImpressionLog;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by twjang on 15. 10. 7.
 */
@Slf4j
public class KafkaMessageWorker implements Runnable {

    private String topic;

    private Producer<String, byte[]> producer;

    private JdbcTemplate jdbcTemplate;

    private int page;

    private int size;

    private boolean enableCSVFileWriteJob = false;

    private boolean enableDataSetWriteJob = false;

    private String datasetDir = "2015-10-27";

    KafkaMessageWorker(String topic, JdbcTemplate jdbcTemplate, int page, int size, boolean decreaseIndex) {

        this.topic = topic;
        this.jdbcTemplate = jdbcTemplate;

        this.page = (decreaseIndex) ? --page * size : page * size;
        this.size = size;

        producer = ProducerFactory.getInstance();
    }

    /*private Producer<String, byte[]> buildProducer() {

        Properties props = new Properties();

        props.put("metadata.broker.list", "localhost:9092,localhost:9093");
        //props.put("metadata.broker.list", "175.119.226.171:9092,175.119.226.171:9093,175.119.226.171:9094");
        props.put("partitioner.class", RoundRobinPartitioner.class.getName());
        props.put("compression.codec", "2");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        ProducerConfig producerConfig = new ProducerConfig(props);

        return new Producer<>(producerConfig);
    }*/

    @Override
    public void run() {

        List<ImpressionLog> messageList = Lists.newArrayList();

        if (enableDataSetWriteJob) {

            String sql =
                    MessageFormat.format("SELECT * FROM ImpressionLog ORDER BY impressionTime DESC LIMIT {0}, {1}", String.valueOf(page), String.valueOf(size));

            List resultMapList = jdbcTemplate.queryForList(sql);

            for (Object obj : resultMapList) {

                Map resultMap = (Map) obj;

                messageList.add(convertMapToMessage(resultMap));
            }

            String fileName = DateUtils.getCurrentDateAsString("yyyyMMdd") + "_impression-log_offset_" +
                    String.format("%06d", this.page) + "_size_" + this.size + ".jdo";

            DataSetWriter writer = new DataSetWriter();

            writer.write(messageList, fileName);

        } else {

            DataSetReader reader = new DataSetReader();

            messageList = reader.read(datasetDir, page, size);
        }

        for (ImpressionLog message : messageList) {

            producer.send(new KeyedMessage<String, byte[]>(topic, generateKey(), toByteArray(message)));
        }

        log.debug("Dataset 레코드 총계: {}", messageList.size());


        if (enableCSVFileWriteJob) {

            CSVFileWriter writer = new CSVFileWriter();

            String fileName = "impression-log_page_" + String.format("%05d", this.page) +
                    "_size_" + this.size + "_" + DateUtils.getCurrentDateTime("yyyy-MM-dd_HH:mm:ss.SSS") + ".csv";

            writer.write(fileName, messageList);

        }

        producer.close();
    }

    private ImpressionLog convertMapToMessage(Map resultMap) {

        ImpressionLog message = new ImpressionLog();

        message.setId(String.valueOf(resultMap.get("id")));
        message.setAsset((Long) resultMap.get("asset"));
        message.setCampaign((Long) resultMap.get("campaign"));
        message.setCpv((Integer) resultMap.get("cpv"));
        message.setCueOwner((Integer) resultMap.get("cueOwner"));
        message.setDevice((Long) resultMap.get("device"));
        message.setImpressionTime((Date) resultMap.get("impressionTime"));
        message.setError((Boolean) resultMap.get("isError"));
        message.setPlayTime((Integer) resultMap.get("playTime"));
        message.setProgramProvider((Integer) resultMap.get("programProvider"));
        message.setRegion1((Integer) resultMap.get("region1"));
        message.setRegion2((Integer) resultMap.get("region2"));
        message.setRegion3((Integer) resultMap.get("region3"));
        message.setRegion4((Integer) resultMap.get("region4"));
        message.setServiceOperator((Integer) resultMap.get("serviceOperator"));
        message.setVtr((Float) resultMap.get("vtr"));
        message.setZipCode((Integer) resultMap.get("zipCode"));

        /*message.setId(String.valueOf(resultMap.get("id")));
        message.setAsset((Long) resultMap.get("asset"));
        message.setCampaign((Long) resultMap.get("campaign"));
        message.setCpv((Integer) resultMap.get("cpv"));
        message.setCueOwner((Boolean) resultMap.get("cue_owner") ? 1 : 0);
        message.setDevice((Long) resultMap.get("device"));
        message.setImpressionTime((Date) resultMap.get("impression_time"));
        message.setError((Boolean) resultMap.get("is_error"));
        message.setPlayTime((Integer) resultMap.get("play_time"));
        message.setProgramProvider((Integer) resultMap.get("program_provider"));
        message.setRegion1((Integer) resultMap.get("region1"));
        message.setRegion2((Integer) resultMap.get("region2"));
        message.setRegion3((Integer) resultMap.get("region3"));
        message.setRegion4((Integer) resultMap.get("region4"));
        message.setServiceOperator((Integer) resultMap.get("service_operator"));
        message.setVtr((Float) resultMap.get("vtr"));
        message.setZipCode((Integer) resultMap.get("zip_code"));*/

        return message;

    }

    private byte[] toByteArray(Object object) {

        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(object);
            return bos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private AtomicInteger keyCounter = new AtomicInteger(0);

    private String generateKey() {

        int keyIndex = keyCounter.getAndIncrement();

        if (keyIndex == Integer.MAX_VALUE) {
            keyCounter.set(0);
            return "0";
        }

        return Integer.toString(keyIndex);
    }

}
