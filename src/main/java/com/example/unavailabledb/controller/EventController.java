package com.example.unavailabledb.controller;

import com.example.unavailabledb.model.Event;
import com.example.unavailabledb.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("event")
@AllArgsConstructor
public class EventController {

    private EventService eventService;

    @ApiOperation("Use this endpoint to retrieve all events")
    @ApiResponse(responseCode = "200", description = "Success",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    @GetMapping
    public List<Event> getEvents() {
        return eventService.getEvents();
    }
}
