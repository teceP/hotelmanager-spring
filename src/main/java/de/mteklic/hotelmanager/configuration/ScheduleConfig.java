package de.mteklic.hotelmanager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable scheduling in the Spring application.
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    /**
     * Enables Spring's scheduled task execution capability.
     * Scheduling is used for executing methods at fixed intervals or specified times.
     * Methods annotated with @Scheduled can be found in different classes
     * and are supposed to start daily at 10:00 am, in order to keep all rooms clean and tidy.
     */
}
