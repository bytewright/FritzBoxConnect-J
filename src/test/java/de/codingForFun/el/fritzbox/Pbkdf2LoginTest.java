package de.codingForFun.el.fritzbox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Pbkdf2LoginTest {

    @Test
    void testCalculatePbkdf2Response() throws FailedChallengeResponse {
        //GIVEN
        String password="pass";
        String challenge="2$60000$4bb07e843fd48f11$6000$adce22c5548";

        //WHEN
        String response = Pbkdf2Login.calculatePbkdf2Response(challenge, password);

        //THEN
        assertEquals("adce22c5548$1415667d7b9407e2ccb511041aaa7ffa45f49dd98bd3b6add2d1a48171cecadf", response);
    }
}