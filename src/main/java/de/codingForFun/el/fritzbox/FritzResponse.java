package de.codingForFun.el.fritzbox;

public record FritzResponse(String sid, String challenge, String documentString) {
    @Override
    public String toString() {
        return "FritzResponse{" + "sid='" + sid + '\'' +
                ", challenge='" + challenge + '\'' +
                ", documentString='" + documentString + '\'' +
                '}';
    }
}
