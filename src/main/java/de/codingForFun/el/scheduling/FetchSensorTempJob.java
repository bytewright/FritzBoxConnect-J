package de.codingForFun.el.scheduling;

import de.codingForFun.el.homeAutomation.FetchSensorDataService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FetchSensorTempJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchSensorTempJob.class);
    @Autowired
    private FetchSensorDataService fetchSensorDataService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Starting data collection using bean '{}', context: {}", fetchSensorDataService, context);
        fetchSensorDataService.fetchTempDataFromAllSensors();
    }
}
