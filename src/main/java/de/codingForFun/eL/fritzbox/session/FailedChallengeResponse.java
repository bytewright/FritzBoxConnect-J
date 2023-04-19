package de.codingForFun.eL.fritzbox.session;

import java.security.GeneralSecurityException;

public class FailedChallengeResponse extends GeneralSecurityException {
    public FailedChallengeResponse(GeneralSecurityException cause) {
        super(cause);
    }
}
