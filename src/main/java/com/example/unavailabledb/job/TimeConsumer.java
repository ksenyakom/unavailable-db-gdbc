package com.example.unavailabledb.job;

import com.example.unavailabledb.model.Event;
import com.example.unavailabledb.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static com.example.unavailabledb.job.TimeQueue.TIME_QUEUE;

@Slf4j
@Component
public class TimeConsumer implements Runnable {
    private boolean running = true;
    private final Object isNotEmpty = new Object();

    private final EventRepository eventRepository;

    public TimeConsumer(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void waitIsNotEmpty() throws InterruptedException {
        synchronized (isNotEmpty) {
            isNotEmpty.wait();
        }
    }

    public void notifyIsNotEmpty() {
        synchronized (isNotEmpty) {
            isNotEmpty.notify();
        }
    }

    @Override
    public void run() {
        while (running) {
            if (TIME_QUEUE.isEmpty()) {
                try {
                    waitIsNotEmpty();
                } catch (InterruptedException e) {
                    log.info("Error while waiting to Consume.");
                    break;
                }
            }
            if (!running) {
                break;
            }

            ZonedDateTime element = TIME_QUEUE.element();

            try {
                eventRepository.save(new Event(element));
            } catch (Exception e) {
                log.error("Error while saving", e);
                continue;
            }
            TIME_QUEUE.remove(element);
        }
        log.info("Consumer Stopped");
    }
}
