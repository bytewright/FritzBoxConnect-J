package de.codingForFun.el.homeAutomation;

import de.codingForFun.el.fritzbox.DeviceInfo;
import de.codingForFun.el.fritzbox.FritzConnectService;
import de.codingForFun.el.fritzbox.ParseFritzRespComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FetchSensorDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchSensorDataService.class);
    /**
     * Die Kommandos werden über einen HTTP GET Request an die URL abgesetzt
     */
    private static final String URL = "https://fritz.box/webservices/homeautoswitch.lua";

    @Autowired
    private ParseFritzRespComponent responseParser;
    @Autowired
    private FritzConnectService fritzConnectService;

    public List<TempSensorReadout> getTemps(String ain) {
        String cmd = "getbasicdevicestats";
        String fritzResponse = fritzConnectService.sendRequestRawResponseBody(URL, ain, cmd);
        List<TempSensorReadout> tempSensorReadouts = responseParser.parseBasicDeviceStats(fritzResponse);
        return tempSensorReadouts;
    }

    public List<DeviceInfo> getTempSensorDevices() {
        String cmd = "getdevicelistinfos";
        String fritzResponse = fritzConnectService.sendRequestRawResponseBody(URL, null, cmd);
        LOGGER.info("Got response: {}", fritzResponse);
        List<DeviceInfo> deviceInfos = responseParser.parseDeviceListInfos(fritzResponse);
        for (DeviceInfo deviceInfo : deviceInfos) {
            LOGGER.info("Device: {} {}", deviceInfo.functionBitMask(), deviceInfo.ain());
        }
        return deviceInfos;
    }
        /*
    <templatelist version="1">
    <template identifier="tmp047233-3DFC03369" id="60001" functionbitmask="4160" autocreate="0" applymask="4"><name>Gäste kommen</name><metadata></metadata>
    <devices><device identifier="grp047233-3DFC04223" /><device identifier="grp047233-3DFC042B7" /><device identifier="grp047233-3DFC03AF2" /></devices><triggers></triggers>
    <sub_templates></sub_templates><applymask><hkr_temperature /></applymask>
    </template>
    </templatelist>

    */

    public void fetchTempDataFromAllSensors() {
        LOGGER.info("Fetching all matching sensor device identifiers...");
        // erst mit gettemplatelistinfos die ains holen, dann temps fetchen
        List<DeviceInfo> tempSensorDevices = getTempSensorDevices();
        for (DeviceInfo tempSensorDevice : tempSensorDevices) {
            List<TempSensorReadout> temps = getTemps(tempSensorDevice.ain());
            LOGGER.info("Got {} temp readings from sensor {}, last one: {}",
                    temps.size(), tempSensorDevice, !temps.isEmpty() ? temps.get(0) : null);
        }
    }
}