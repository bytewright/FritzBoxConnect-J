package de.codingForFun.el.persistance.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class TempSensorReadingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private Date date;
    private Integer TempValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTempValue() {
        return TempValue;
    }

    public void setTempValue(Integer tempValue) {
        TempValue = tempValue;
    }
}
