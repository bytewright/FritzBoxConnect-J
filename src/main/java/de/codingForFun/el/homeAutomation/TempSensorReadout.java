package de.codingForFun.el.homeAutomation;

import java.time.Instant;

public record TempSensorReadout(Instant dataRecordInstant, Temperature temp, SensorAin sensorAin) {
}
