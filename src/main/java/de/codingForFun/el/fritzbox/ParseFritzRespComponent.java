package de.codingForFun.el.fritzbox;

import de.codingForFun.el.homeAutomation.SensorAin;
import de.codingForFun.el.homeAutomation.TempSensorReadout;
import de.codingForFun.el.homeAutomation.Temperature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class ParseFritzRespComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseFritzRespComponent.class);
    private final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    public FritzResp parse(String s) {
        return parse(new ByteArrayInputStream(s.trim().getBytes(StandardCharsets.UTF_8)));
    }

    public FritzResp parse(InputStream inputStream) {
        try {
            Document xmlDocument = builderFactory.newDocumentBuilder().parse(inputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String sid = parseSid(xPath, xmlDocument);
            String challenge = parseChallenge(xPath, xmlDocument);

            return new FritzResp(sid, challenge);
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public FritzResponse parseFritzResponse(InputStream inputStream) {
        try {
            Document xmlDocument = builderFactory.newDocumentBuilder().parse(inputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String sid = parseSid(xPath, xmlDocument);
            String challenge = parseChallenge(xPath, xmlDocument);
            String documentString = xmlDocument.toString();
            return new FritzResponse(sid, challenge, documentString);
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DeviceInfo> parseDeviceListInfos(String fritzResponse) {
        List<DeviceInfo> resultList = new ArrayList<>();
        try (InputStream inputStream = new ByteArrayInputStream(fritzResponse.getBytes())) {
            Document xmlDocument = builderFactory.newDocumentBuilder().parse(inputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String sidExprStr = "/devicelist/device";
            XPathExpression sidExpr = xPath.compile(sidExprStr);
            NodeList devices = (NodeList) sidExpr.evaluate(xmlDocument, XPathConstants.NODESET);
            LOGGER.info("Found {} devices in fritz response", devices);
            for (int i = 0; i < devices.getLength(); i++) {
                resultList.add(parseDevice(devices.item(i)));
            }
            return resultList;
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TempSensorReadout> parseBasicDeviceStats(String ain, String fritzResponse) {
        /*
            Die Attribute von <stats> sind „count“ für Anzahl der Werte und „grid“ für den zeitliche
            Abstand/Auflösung in Sekunden. Das „datatime“ Attribute enthält den Unix-Timestamp der letzten
            Aktualisierung. Der Inhalt von <stats> ist eine count-Anzahl kommaseparierte Liste von Werten. Werte
            mit „-“ sind unbekannt/undefiniert.

        */
        try (InputStream inputStream = new ByteArrayInputStream(fritzResponse.getBytes())) {
            Document xmlDocument = builderFactory.newDocumentBuilder().parse(inputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String sidExprStr = "/devicestats/temperature/stats";
            XPathExpression sidExpr = xPath.compile(sidExprStr);
            Node tempStats = (Node) sidExpr.evaluate(xmlDocument, XPathConstants.NODE);
            NamedNodeMap attributes = tempStats.getAttributes();
            int count = Integer.parseInt(attributes.getNamedItem("count").getNodeValue());
            int grid = Integer.parseInt(attributes.getNamedItem("grid").getNodeValue());
            String datatimeValue = attributes.getNamedItem("datatime").getNodeValue();
            Instant datatime = Instant.ofEpochSecond(Integer.parseInt(datatimeValue));
            LOGGER.info("Found {} sensor datapoints in fritz response, last event from {}", count, datatime);
            String tempSensorValues = tempStats.getTextContent();
            return parseSensorValues(ain, count, tempSensorValues, grid, datatime);
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private List<TempSensorReadout> parseSensorValues(String ain, int count, String sensorValues, int grid, Instant datatime) {
        List<String> stringList = List.of(sensorValues.split(","));
        if (stringList.size() != count) {
            LOGGER.warn("Expected {} values, but found only {}", count, stringList.size());
        }
        List<TempSensorReadout> tempSensorReadouts = new LinkedList<>();
        for (int i = 0; i < stringList.size(); i++) {
            Instant dataPointInstant = datatime.minus((long) i * grid, ChronoUnit.SECONDS);
            String s = stringList.get(i);
            if ("-".equals(s)) {
                LOGGER.info("Found missing datapoint at instant {}", dataPointInstant);
                continue;
            }
            try {
                int i1 = Integer.parseInt(s);
                tempSensorReadouts.add(new TempSensorReadout(dataPointInstant, new Temperature(i1), new SensorAin(ain)));
            } catch (NumberFormatException e) {
                LOGGER.error("Sensordata is not int: {}", s);
            }
        }
        return tempSensorReadouts;
    }

    private DeviceInfo parseDevice(Node item) {
        NamedNodeMap attributes = item.getAttributes();
        Node identifier = attributes.getNamedItem("identifier");
        Node name = attributes.getNamedItem("productname");
        Node functionBitmask = attributes.getNamedItem("functionbitmask");
        String bitmaskNodeValue = functionBitmask.getNodeValue();
        long parsed = Long.parseUnsignedLong(bitmaskNodeValue);
        return new DeviceInfo(identifier.getNodeValue(), name.getNodeValue(), parsed);
    }

    private String parseSid(XPath xPath, Document xmlDocument) throws XPathExpressionException {
        String sidExprStr = "/SessionInfo/SID";
        XPathExpression sidExpr = xPath.compile(sidExprStr);
        String sid = (String) sidExpr.evaluate(xmlDocument, XPathConstants.STRING);
        return sid;
    }

    private String parseChallenge(XPath xPath, Document xmlDocument) throws XPathExpressionException {
        String challengeExprStr = "/SessionInfo/Challenge";
        XPathExpression challengeExpr = xPath.compile(challengeExprStr);
        String challenge = (String) challengeExpr.evaluate(xmlDocument, XPathConstants.STRING);
        return challenge;
    }
}
