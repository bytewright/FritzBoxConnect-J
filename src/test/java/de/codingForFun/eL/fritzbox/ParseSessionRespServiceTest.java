package de.codingForFun.eL.fritzbox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseSessionRespServiceTest {
    private ParseSessionRespService testee = new ParseSessionRespService();

    @Test
    void testParse() {
        //GIVEN
        String serverResp = """
                      <?xml version="1.0" encoding="utf-8"?>
                      <SessionInfo>
                      <SID>0000000000000000</SID>
                      <Challenge>2$60000$salt14bb07e843fd48f11$6000$salt2adce22c5548</Challenge>
                      <BlockTime>0</BlockTime>
                      <Rights></Rights>
                      <Users><User last="1">fritz123</User></Users>
                      </SessionInfo>
                """;
        //WHEN
        FritzResp response = testee.parse(serverResp);

        //THEN
        assertEquals("2$60000$salt14bb07e843fd48f11$6000$salt2adce22c5548", response.getChallenge());
    }
}