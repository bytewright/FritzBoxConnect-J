package de.codingForFun.el.persistance.repo;

import de.codingForFun.el.persistance.model.TempSensorReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempSensorReadingsRepository extends JpaRepository<TempSensorReadingEntity, Long> {
}
