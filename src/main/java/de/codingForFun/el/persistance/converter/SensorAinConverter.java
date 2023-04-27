package de.codingForFun.el.persistance.converter;

import de.codingForFun.el.homeAutomation.SensorAin;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SensorAinConverter implements AttributeConverter<SensorAin, String> {
    @Override
    public String convertToDatabaseColumn(SensorAin attribute) {
        return attribute.ain();
    }

    @Override
    public SensorAin convertToEntityAttribute(String dbData) {
        return new SensorAin(dbData);
    }
}