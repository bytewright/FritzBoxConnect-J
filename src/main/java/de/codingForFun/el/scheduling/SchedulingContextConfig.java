package de.codingForFun.el.scheduling;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Configuration
public class SchedulingContextConfig {
    @Bean
    public Trigger triggerTempSensor(@Qualifier("Job-Temp-Sensor-Fetch") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("Qrtz_Trigger_Fetch_Temp_Sensor_Data")
                .withDescription("Sensor data collection rate")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInMinutes(1))
                .build();
    }

    @Bean(name = "Job-Temp-Sensor-Fetch")
    public JobDetail jobDetailTempSensor() {
        return JobBuilder.newJob().ofType(FetchSensorTempJob.class)
                .storeDurably()
                .withIdentity("Qrtz_Job_Fetch_Temp_Sensor_Data_Detail")
                .withDescription("Fetching sensor data...")
                .build();
    }
}
