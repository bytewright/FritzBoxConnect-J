package de.codingForFun.el.fritzbox;

import java.security.GeneralSecurityException;

public class FailedChallengeResponse extends GeneralSecurityException {
    public FailedChallengeResponse(GeneralSecurityException cause) {
        super(cause);
    }
}
