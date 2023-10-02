package com.example.unavailabledb.repository;

import com.example.unavailabledb.exception.RepositoryException;
import com.example.unavailabledb.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.unavailabledb.job.TimeProducer.ZONE_ID;

@Slf4j
@Repository
public class EventRepository {

    private static final String SAVE = "INSERT INTO event (event_time) VALUES (?)";
    private static final String SELECT_ALL = "SELECT * FROM event";

    private final ConnectionManager connectionManager;

    public EventRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void save(Event event) {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SAVE)) {
            statement.setTimestamp(1, new java.sql.Timestamp(event.getEventTime().toInstant().toEpochMilli()));

            connection.setAutoCommit(false);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            log.error("Exception while saving event", e);
            try {
                connection.rollback();
                throw new RepositoryException(e.getMessage());
            } catch (SQLException e1) {
                log.error("Rollback exception", e1);
                throw new RepositoryException(e.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                log.error("Error", e);
            }
        }
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) {
                Event event = new Event();
                event.setId(resultSet.getLong("id"));
                event.setEventTime(ZonedDateTime.of(resultSet.getTimestamp("event_time").toLocalDateTime(), ZONE_ID));
                list.add(event);
            }
        } catch (SQLException e) {
            log.error("Exception while getting all events", e);
            throw new RepositoryException(e.getMessage());
        }
        return list;
    }
}
