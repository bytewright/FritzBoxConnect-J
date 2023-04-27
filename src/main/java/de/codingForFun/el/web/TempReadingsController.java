package de.codingForFun.el.web;

import de.codingForFun.el.homeAutomation.SensorAin;
import de.codingForFun.el.persistance.model.TempSensorReadingEntity;
import de.codingForFun.el.persistance.repo.TempSensorReadingsRepository;
import de.codingForFun.el.persistance.repo.projections.TempSensorReadingEntityInfo;
import de.codingForFun.el.util.TempSensorFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@RestController
public class TempReadingsController {
    private static final ZoneId ZONE_ID = TimeZone.getTimeZone("Berlin").toZoneId();
    private static final Logger LOGGER = LoggerFactory.getLogger(TempReadingsController.class);
    @Autowired
    private TempSensorReadingsRepository repository;

    @GetMapping(value = "/temp/{ain}/{date}", produces = "application/json")
    @ResponseBody
    public Map<Instant, String> findReadingByDate(
            @PathVariable("ain") String sensorAin,
            @PathVariable("date") String dateStr) {
        LOGGER.debug("Looking for TempReadings for date (yyyy-[m]m-[d]d): {}", dateStr);
        Instant dateInstant = findStartTimestamp(dateStr);
        Timestamp dateStart = Timestamp.from(dateInstant);
        Timestamp dateEnd = Timestamp.from(dateInstant.plus(1, ChronoUnit.DAYS));
        List<TempSensorReadingEntity> entityList = findMatchingEntities(new SensorAin(sensorAin), dateStart, dateEnd);
        Map<Instant, String> jsonMap = new LinkedHashMap<>();
        for (TempSensorReadingEntity entity : entityList) {
            jsonMap.put(entity.getTimestamp().toInstant(), TempSensorFormatter.format(entity.getTempValue()));
        }
        return jsonMap;
    }

    @GetMapping(value = "/ains/", produces = "application/json")
    @ResponseBody
    public List<SensorAin> findAllAins() {
        return repository.getDistinctBySensorAinNotNullOrderBySensorAinAsc().stream()
                .map(TempSensorReadingEntityInfo::getSensorAin)
                .toList();
    }

    private List<TempSensorReadingEntity> findMatchingEntities(SensorAin sensorAin, Timestamp dateStart, Timestamp dateEnd) {
        return repository.findBySensorAinAndTimestampBetweenOrderByTimestampAsc(sensorAin, dateStart, dateEnd);
    }

    private Instant findStartTimestamp(String dateStr) {
        Date date = Date.valueOf(dateStr);
        return date.toLocalDate()
                .atStartOfDay()
                .atZone(ZONE_ID)
                .toInstant();
    }
}
