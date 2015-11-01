package io.manasobi.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by twjang on 15. 9. 30.
 */
@Slf4j
@Component
public class KafkaMessageProducer {

    private static String topic;

    //@Value("${kafka.topic.log.collector}")
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public static void main2(String[] args){

        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("jetstream-spring-test.xml");

        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");

        TestResultReporter.truncateTables(jdbcTemplate.getDataSource());

        log.debug("KafkaMessageProducer :: Start...");
        log.debug("KafkaMessageProducer :: Topic -> {}", topic);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println(args[0]);
        System.out.println(args[1]);

        int page = StringUtils.isEmpty(args[0]) ?
                1 : Integer.valueOf(args[0].replace("page=", ""));

        int size = StringUtils.isEmpty(args[1]) ?
                1000 : Integer.valueOf(args[1].replace("size=", ""));

        for (int i = 0; i < 10; i++) {

            Runnable worker = new KafkaMessageWorker(topic, jdbcTemplate, page + i, size, page == 1);

            executor.execute(worker);
        }

        executor.shutdown();

        while (!executor.isTerminated()) {}

        stopWatch.stop();

        log.debug("KafkaMessageProducer :: End...");

        log.debug("KafkaMessageProducer :: 수행시간 -> " + stopWatch.getTotalTimeMillis());
    }

}