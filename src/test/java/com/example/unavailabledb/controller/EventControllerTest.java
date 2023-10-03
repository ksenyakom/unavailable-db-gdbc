package com.example.unavailabledb.controller;

import com.example.unavailabledb.repository.ConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ConnectionManager connectionManager;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .build();
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
    void endpointEvents_statusOk() throws Exception {
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/events")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }
}