package de.codingForFun.el.persistance.model;

import de.codingForFun.el.homeAutomation.SensorAin;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class TempSensorReadingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private Timestamp timestamp;
    private Integer TempValue;

    private SensorAin sensorAin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getTempValue() {
        return TempValue;
    }

    public void setTempValue(Integer tempValue) {
        TempValue = tempValue;
    }

    public SensorAin getSensorAin() {
        return sensorAin;
    }

    public void setSensorAin(SensorAin sensorAin) {
        this.sensorAin = sensorAin;
    }
}
