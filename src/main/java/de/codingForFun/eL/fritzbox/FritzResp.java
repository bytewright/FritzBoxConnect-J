package de.codingForFun.eL.fritzbox;

public class FritzResp {
    private final String sid;
    private final String challenge;

    public FritzResp(String sid, String challenge) {
        this.sid = sid;
        this.challenge = challenge;
    }

    public String getSid() {
        return sid;
    }

    public String getChallenge() {
        return challenge;
    }
}
