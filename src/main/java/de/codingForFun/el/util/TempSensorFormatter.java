package de.codingForFun.el.util;

public class TempSensorFormatter {
    public static String format(Integer tempValue) {
        return String.format("%.1f°C", (float) tempValue / 10.0f);
    }
}
