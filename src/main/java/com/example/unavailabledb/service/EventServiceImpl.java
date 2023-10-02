package com.example.unavailabledb.service;

import com.example.unavailabledb.model.Event;
import com.example.unavailabledb.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class EventServiceImpl implements EventService{
    private EventRepository eventRepository;

    @Override
    public List<Event> getEvents() {
        return eventRepository.getAllEvents();
    }

}
