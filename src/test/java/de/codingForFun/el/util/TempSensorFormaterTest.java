package de.codingForFun.el.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TempSensorFormatterTest {

    @Test
    void format() {
        assertEquals("22,0°C", TempSensorFormatter.format(220));
    }
}