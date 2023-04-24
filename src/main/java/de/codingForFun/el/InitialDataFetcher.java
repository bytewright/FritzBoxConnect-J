package de.codingForFun.el;

import de.codingForFun.el.homeAutomation.FetchSensorDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class InitialDataFetcher implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataFetcher.class);
    @Autowired
    private FetchSensorDataService fetchSensorDataService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Context is initialized: {}", event);
        fetchSensorDataService.fetchTempDataFromAllSensors();
    }
}
