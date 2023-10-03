package com.example.unavailabledb.repository;

import com.example.unavailabledb.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;

import static com.example.unavailabledb.job.TimeProducer.ZONE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventRepositoryIT {

    private EventRepository eventRepository;

    @BeforeEach
    void setUp() throws SQLException {
        ConnectionManager connectionManager = new ConnectionManager(
            "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
            "sa",
            "sa",
            "org.h2.Driver");

        eventRepository = new EventRepository(connectionManager);
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
    void save_validEvent_eventSaved() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 10, 2, 10, 10, 10, 0, ZONE_ID);
        eventRepository.save(new Event(zonedDateTime));

        List<Event> events = eventRepository.getAllEvents();

        assertEquals(1, events.size());
        assertEquals(zonedDateTime, events.get(0).getEventTime());
    }

}