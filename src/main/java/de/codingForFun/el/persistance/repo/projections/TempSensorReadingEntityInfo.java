package de.codingForFun.el.persistance.repo.projections;

import de.codingForFun.el.homeAutomation.SensorAin;

/**
 * A Projection for the {@link de.codingForFun.el.persistance.model.TempSensorReadingEntity} entity
 */
public interface TempSensorReadingEntityInfo {
    SensorAin getSensorAin();
}