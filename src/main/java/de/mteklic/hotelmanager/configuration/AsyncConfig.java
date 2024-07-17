package de.mteklic.hotelmanager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class that enables asynchronous execution and configures a custom thread pool for async tasks.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Defines a custom Executor bean for managing asynchronous tasks.
     * This bean configures a ThreadPoolTaskExecutor with specific parameters:
     * - Core pool size: 1 (minimum number of threads)
     * - Max pool size: 3 (maximum number of threads)
     * - Queue capacity: 1 (maximum number of tasks that can be queued if all threads are busy)
     * - Thread name prefix: "Housekeeper-" (prefix for the names of threads created by this executor)
     *
     * @return An Executor instance configured for managing asynchronous tasks.
     */
    @Bean
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("Housekeeper-");
        return executor;
    }
}
