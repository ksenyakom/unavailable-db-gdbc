package com.example.unavailabledb.job;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeQueue {
    public static final int MAX_QUEUE_SIZE = 500;

    protected static final Queue<ZonedDateTime> TIME_QUEUE = new ConcurrentLinkedQueue<>();
}
