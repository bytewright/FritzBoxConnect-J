package de.codingForFun.el.fritzbox;

import de.codingForFun.el.homeAutomation.TempSensorReadout;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseFritzRespComponentTest {
    private ParseFritzRespComponent testee = new ParseFritzRespComponent();

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
                /*
                    <?xml version="1.0" encoding="utf-8"?>
                    <SessionInfo>
                    <SID>e6c9c956e2c44667</SID>
                    <Challenge>2$60000$acc908de77d8f9844bb07e843fd48f11$6000$408cbda21c04bbcc254aceec1724170a</Challenge>
                    <BlockTime>0</BlockTime>
                    <Rights>
                    <Name>Dial</Name>
                    <Access>2</Access><Name>App</Name>
                    <Access>2</Access><Name>HomeAuto</Name>
                    <Access>2</Access><Name>BoxAdmin</Name><Access>2</Access><Name>Phone</Name>
                    <Access>2</Access><Name>NAS</Name><Access>2</Access></Rights>
                    <Users><User last="1">fritz9292</User><User>kodi</User></Users>
                    </SessionInfo>
             */

    @Test
    void testParseSensorReadouts() {
        //GIVEN
        String rawXml = """
                <devicestats>
                <temperature>
                <stats count="96" grid="900" datatime="1682181182">230,230,230,230,230,230,225,225,225,225,225,225,225,225,225,225,225,225,225,220,220,220,220,220,220,225,225,225,225,225,225,225,225,225,225,225,225,225,225,220,220,220,220,220,220,220,215,215,215,215,220,220,215,220,220,220,220,220,220,220,220,220,220,220,220,220,225,225,225,225,225,225,230,230,230,230,230,230,230,230,230,225,230,230,230,225,225,225,225,225,225,230,230,225,230,225</stats>
                </temperature>
                </devicestats>
                """;
        //WHEN
        List<TempSensorReadout> response = testee.parseBasicDeviceStats("fakeAin", rawXml);

        //THEN
        assertEquals(96, response.size());
        TempSensorReadout tempSensorReadout = response.get(0);
        assertEquals(230, tempSensorReadout.temp().value());
    }

    @Test
    void testParseDeviceList() {
        //GIVEN
        String serverResp = getDeviceListXml();
        //WHEN
        List<DeviceInfo> response = testee.parseDeviceListInfos(serverResp);

        //THEN
        assertEquals(4, response.size());
        DeviceInfo deviceInfo = response.get(0);
        assertEquals("09995 0334556", deviceInfo.ain());
    }

    private String getDeviceListXml() {
        return """
                 <devicelist version="1" fwversion="7.5">
                    <device identifier="09995 0334556" id="16" functionbitmask="320" fwversion="05.08" manufacturer="AVM" productname="FRITZ!DECT 301">
                        <present>1</present>
                <txbusy>0</txbusy>
                        <name>Elias Arbeitszimmer</name>
                <battery>30</battery>
                <batterylow>0</batterylow>
                        <temperature>
                <celsius>220</celsius>
                <offset>0</offset>
                </temperature>
                        <hkr>
                <tist>44</tist>
                <tsoll>44</tsoll>
                <absenk>40</absenk>
                <komfort>44</komfort>
                <lock>0</lock>
                        <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                        <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                        <boostactiveendtime>0</boostactiveendtime>
                <batterylow>0</batterylow>
                        <battery>30</battery>
                <nextchange>
                <endperiod>1682112600</endperiod>
                <tchange>40</tchange>
                </nextchange>
                        <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                        <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                    </device>
                    <device identifier="09995 1043360" id="17" functionbitmask="320" fwversion="05.08" manufacturer="AVM" productname="FRITZ!DECT 301">
                <present>1</present>
                <txbusy>0</txbusy>
                <name>Schlafzimmer</name>
                <battery>30</battery>
                <batterylow>0</batterylow>
                <temperature>
                <celsius>205</celsius>
                <offset>0</offset>
                </temperature>
                <hkr>
                <tist>41</tist>
                <tsoll>41</tsoll>
                <absenk>41</absenk>
                <komfort>44</komfort>
                <lock>0</lock>
                <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                <boostactiveendtime>0</boostactiveendtime>
                <batterylow>0</batterylow>
                <battery>30</battery>
                <nextchange>
                <endperiod>1682103600</endperiod>
                <tchange>44</tchange>
                </nextchange>
                <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                </device>
                <device identifier="09995 1044502" id="18" functionbitmask="320" fwversion="05.08" manufacturer="AVM" productname="FRITZ!DECT 301">
                <present>1</present>
                <txbusy>0</txbusy>
                <name>Wohnzimmer</name>
                <battery>60</battery>
                <batterylow>0</batterylow>
                <temperature>
                <celsius>205</celsius>
                <offset>-20</offset>
                </temperature>
                <hkr>
                <tist>41</tist>
                <tsoll>41</tsoll>
                <absenk>41</absenk>
                <komfort>44</komfort>
                <lock>0</lock>
                <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                <boostactiveendtime>0</boostactiveendtime>
                <batterylow>0</batterylow>
                <battery>60</battery>
                <nextchange>
                <endperiod>1682089200</endperiod>
                <tchange>44</tchange>
                </nextchange>
                <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                </device>
                <device identifier="09995 1043362" id="19" functionbitmask="320" fwversion="05.08" manufacturer="AVM" productname="FRITZ!DECT 301">
                <present>1</present>
                <txbusy>0</txbusy>
                <name>Lena Arbeitszimmer</name>
                <battery>80</battery>
                <batterylow>0</batterylow>
                <temperature>
                <celsius>205</celsius>
                <offset>0</offset>
                </temperature>
                <hkr>
                <tist>41</tist>
                <tsoll>41</tsoll>
                <absenk>41</absenk>
                <komfort>43</komfort>
                <lock>0</lock>
                <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                <boostactiveendtime>0</boostactiveendtime>
                <batterylow>0</batterylow>
                <battery>80</battery>
                <nextchange>
                <endperiod>1682096400</endperiod>
                <tchange>43</tchange>
                </nextchange>
                <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                </device>
                <group synchronized="1" identifier="grp047233-3DFC04223" id="900" functionbitmask="4160" fwversion="1.0" manufacturer="AVM" productname="">
                <present>1</present>
                <txbusy>0</txbusy>
                <name>Elias Arbeitszimmer</name>
                <hkr>
                <tist>
                </tist>
                <tsoll>44</tsoll>
                <absenk>40</absenk>
                <komfort>44</komfort>
                <lock>0</lock>
                <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                <boostactiveendtime>0</boostactiveendtime>
                <nextchange>
                <endperiod>1682112600</endperiod>
                <tchange>40</tchange>
                </nextchange>
                <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                <groupinfo>
                <masterdeviceid>0</masterdeviceid>
                <members>16</members>
                </groupinfo>
                </group>
                <group synchronized="1" identifier="grp047233-3DFC044D0" id="903" functionbitmask="4160" fwversion="1.0" manufacturer="AVM" productname="">
                <present>1</present>
                <txbusy>0</txbusy>
                <name>Schlafzimmer</name>
                <hkr>
                <tist>
                </tist>
                <tsoll>41</tsoll>
                <absenk>41</absenk>
                <komfort>44</komfort>
                <lock>0</lock>
                <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                <boostactiveendtime>0</boostactiveendtime>
                <nextchange>
                <endperiod>1682103600</endperiod>
                <tchange>44</tchange>
                </nextchange>
                <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                <groupinfo>
                <masterdeviceid>0</masterdeviceid>
                <members>17</members>
                </groupinfo>
                </group>
                <group synchronized="1" identifier="grp047233-3DFC042B7" id="901" functionbitmask="4160" fwversion="1.0" manufacturer="AVM" productname="">
                <present>1</present>
                <txbusy>0</txbusy>
                <name>Wohnzimmer</name>
                <hkr>
                <tist>
                </tist>
                <tsoll>41</tsoll>
                <absenk>41</absenk>
                <komfort>44</komfort>
                <lock>0</lock>
                <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                <boostactiveendtime>0</boostactiveendtime>
                <nextchange>
                <endperiod>1682089200</endperiod>
                <tchange>44</tchange>
                </nextchange>
                <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                <groupinfo>
                <masterdeviceid>0</masterdeviceid>
                <members>18</members>
                </groupinfo>
                </group>
                <group synchronized="1" identifier="grp047233-3DFC03AF2" id="902" functionbitmask="4160" fwversion="1.0" manufacturer="AVM" productname="">
                <present>1</present>
                <txbusy>0</txbusy>
                <name>Lenas Arbeitszimmer</name>
                <hkr>
                <tist>
                </tist>
                <tsoll>41</tsoll>
                <absenk>41</absenk>
                <komfort>43</komfort>
                <lock>0</lock>
                <devicelock>0</devicelock>
                <errorcode>0</errorcode>
                <windowopenactiv>0</windowopenactiv>
                <windowopenactiveendtime>0</windowopenactiveendtime>
                <boostactive>0</boostactive>
                <boostactiveendtime>0</boostactiveendtime>
                <nextchange>
                <endperiod>1682096400</endperiod>
                <tchange>43</tchange>
                </nextchange>
                <summeractive>0</summeractive>
                <holidayactive>0</holidayactive>
                <adaptiveHeatingActive>1</adaptiveHeatingActive>
                <adaptiveHeatingRunning>0</adaptiveHeatingRunning>
                </hkr>
                <groupinfo>
                <masterdeviceid>0</masterdeviceid>
                <members>19</members>
                </groupinfo>
                </group>
                </devicelist>
                """;
    }
}