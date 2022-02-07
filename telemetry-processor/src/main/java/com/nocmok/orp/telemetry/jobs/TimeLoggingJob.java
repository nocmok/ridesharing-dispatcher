package com.nocmok.orp.telemetry.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeLoggingJob {

    private static final Logger log = LoggerFactory.getLogger(TimeLoggingJob.class);

    @Scheduled(fixedRate = 5000)
    public void logTime() {
        log.info(ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
