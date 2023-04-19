package de.codingForFun.eL.fritzbox;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ParseSessionRespService {
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
}
