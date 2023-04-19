package de.codingForFun.eL.fritzbox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Pbkdf2LoginTest {

    @Test
    void testCalculatePbkdf2Response() {
        //GIVEN
        String password="pass";
        String challenge="";

        //WHEN
        String response = Pbkdf2Login.calculatePbkdf2Response(challenge, password);

        //THEN
    }
}