package de.codingForFun.el.fritzbox;

public record DeviceInfo(String ain, String name, long functionBitMask) {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DeviceInfo{");
        sb.append("ain='").append(ain).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", functionBitMask=").append(functionBitMask);
        sb.append('}');
        return sb.toString();
    }
}
