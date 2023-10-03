package com.example.unavailabledb.job;

import com.example.unavailabledb.model.Event;
import com.example.unavailabledb.repository.ConnectionManager;
import com.example.unavailabledb.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.unavailabledb.job.TimeQueue.TIME_QUEUE;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeConsumerIT {
    private TimeProducer timeProducer;
    private TimeConsumer timeConsumer;
    private EventRepository eventRepository;
    private ConnectionManager connectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        connectionManager = new ConnectionManager(
            "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
            "sa",
            "sa",
            "org.h2.Driver");

        eventRepository = new EventRepository(connectionManager);
        timeConsumer = new TimeConsumer(eventRepository);
        timeProducer = new TimeProducer(timeConsumer);


        Connection connection = connectionManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
            "DROP TABLE IF EXISTS event; " +
                "CREATE TABLE IF NOT EXISTS event " +
                "(id BIGSERIAL PRIMARY KEY, event_time TIMESTAMP NOT NULL)"))
        {
            statement.executeUpdate();
        }
    }

    @Test
    void producerConsumer_dataStoredInQueue_dataSavedInDB() throws InterruptedException {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeProducer.runJob();
            }
        }, 0, 200);
        Thread.sleep(2000);

        List<Event> eventList = eventRepository.getAllEvents();
        assertTrue(TIME_QUEUE.size() < 1000);
        assertTrue(eventList.size() > 1);
    }
}