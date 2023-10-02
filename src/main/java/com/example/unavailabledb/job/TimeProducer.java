package com.example.unavailabledb.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.example.unavailabledb.job.TimeQueue.MAX_QUEUE_SIZE;
import static com.example.unavailabledb.job.TimeQueue.TIME_QUEUE;

@Slf4j
@Component
public class TimeProducer {
    public static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private boolean consumerStarted = false;

    private TimeConsumer timeConsumer;

    public TimeProducer(TimeConsumer timeConsumer) {
        this.timeConsumer = timeConsumer;
    }

    @Scheduled(fixedRateString = "${time.save.rate.milliseconds}")
    private void runJob() {
        if (TIME_QUEUE.size() >= MAX_QUEUE_SIZE) {
            throw new IllegalStateException("Error. Internal storage is full, data will not be processed.");
        }

        TIME_QUEUE.add(ZonedDateTime.now(ZONE_ID));

        log.info("Queue size {}", TIME_QUEUE.size());
        timeConsumer.notifyIsNotEmpty();

        if (!consumerStarted) {
            startConsumer();
        }
    }

    private void startConsumer() {
        Thread thread = new Thread(timeConsumer);
        thread.start();

        consumerStarted = true;
    }
}
