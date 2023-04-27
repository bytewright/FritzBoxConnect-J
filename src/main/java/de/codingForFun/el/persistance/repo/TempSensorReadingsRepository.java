package de.codingForFun.el.persistance.repo;

import de.codingForFun.el.homeAutomation.SensorAin;
import de.codingForFun.el.persistance.model.TempSensorReadingEntity;
import de.codingForFun.el.persistance.repo.projections.TempSensorReadingEntityInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TempSensorReadingsRepository extends
        PagingAndSortingRepository<TempSensorReadingEntity, Long>,
        CrudRepository<TempSensorReadingEntity, Long> {
    List<TempSensorReadingEntity> findBySensorAinAndTimestampBetweenOrderByTimestampAsc(SensorAin sensorAin, Timestamp timestampStart, Timestamp timestampEnd);
    List<TempSensorReadingEntityInfo> getDistinctBySensorAinNotNullOrderBySensorAinAsc();


}
