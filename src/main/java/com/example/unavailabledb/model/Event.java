package com.example.unavailabledb.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
public class Event {

    private Long id;

    private ZonedDateTime eventTime;

    public Event(ZonedDateTime eventTime) {
        this.eventTime = eventTime;
    }
}
