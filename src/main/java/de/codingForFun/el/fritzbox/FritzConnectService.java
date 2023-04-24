package de.codingForFun.el.fritzbox;

import de.codingForFun.el.util.AppSecrets;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FritzConnectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FritzConnectService.class);
    /**
     * see <a href="https://avm.de/service/schnittstellen/">API docu</a>
     */
    private static final String LOGIN_URL = "https://fritz.box/login_sid.lua?version=2";
    private static final String DEFAULT_SID = "0000000000000000";
    /**
     * Identifikation des Aktors oder Templates, z. B. "012340000123" oder MAC Adresse für
     * Netzwerkgeräte
     */
    private static final String PARAM_AIN = "ain=";
    /**
     * Auszuführendes Kommado, s. Tabelle 2: Kommandos
     */
    private static final String PARAM_CMD = "switchcmd=";
    /**
     * Session-ID, ab FRITZ!OS 6.50 wird der sid-Parameter immer benötigt, Spezifikation in [2],
     * Die AVM Home Automation Session benötigt immer die Smart-Home-Berechtigung. Ausserdem
     * wird für einige Kommandos die "Eingeschränkte FRITZ!Box Einstellungen für Apps"-Berechtigung
     * benötigt.
     * Zu Berechtigungen siehe "Actions and User Rights" in der TR064-Specifikation in [1].
     */
    private static final String PARAM_SID = "sid=";
    @Autowired
    private ParseFritzRespComponent responseParser;
    @Autowired
    private FritzBoxHttpClient httpClient;
    @Autowired
    private AppSecrets appSecrets;
    private FritzResponse lastResponse;

    public String sendRequestRawResponseBody(String url, @Nullable String ain, String cmd) {
        try {
            HttpResponse<String> response = getResponse(url, ain, cmd);
            String body = response.body();
            LOGGER.info("Response statusCode: {}, body len {}", response.statusCode(), body.length());
            return body;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private synchronized HttpResponse<String> getResponse(String url, String ain, String cmd) throws IOException, InterruptedException {
        String sid;
        Optional<String> sidOpt = getCurrentSessionId();
        if (sidOpt.isEmpty()) {
            LOGGER.info("No current sessionId found, need to login...");
            login();
            sid = getCurrentSessionId().orElseThrow();
        } else {
            sid = sidOpt.get();
        }
        URI generateURI = generateURI(url, ain, cmd, sid);
        LOGGER.info("Sending request to uri: {}", generateURI);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(generateURI)
                .timeout(Duration.ofMinutes(2))
                .build();
        return httpClient.getStringResponse(request);
    }

    private URI generateURI(String url, String ain, String cmd, String sid) {
        try {
            StringBuilder uriBuilder = new StringBuilder();
            uriBuilder.append(url).append("?");
            if (ain != null) {
                uriBuilder.append(PARAM_AIN).append(URLEncoder.encode(ain, StandardCharsets.UTF_8)).append("&");
            }
            uriBuilder.append(PARAM_CMD).append(cmd).append("&").append(PARAM_SID).append(sid);
            return new URI(uriBuilder.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<String> getCurrentSessionId() {
        if (lastResponse == null) {
            return Optional.empty();
        }
        String sid = lastResponse.sid();
        if (sid == null || sid.equals(DEFAULT_SID)) {
            return Optional.empty();
        }
        return Optional.of(sid);
    }

    private void login() {
        LOGGER.info("Login to Fritz!Box at {}", LOGIN_URL);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .timeout(Duration.ofMinutes(2))
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request);
            LOGGER.info("Initial SID request statusCode: {}", response.statusCode());
            FritzResp fritzResp = responseParser.parse(response.body());

            String challengeResponse = Pbkdf2Login.calculatePbkdf2Response(fritzResp.getChallenge(), appSecrets.getPassword());

            Map<String, String> formData = new HashMap<>();
            formData.put("username", appSecrets.getUserName());
            formData.put("response", challengeResponse);
            HttpRequest loginPOST = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .timeout(Duration.ofMinutes(2))
                    .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<InputStream> response2 = httpClient.send(loginPOST);
            LOGGER.info("Challenge response statusCode: {}", response2.statusCode());
            FritzResponse fritzResponse = responseParser.parseFritzResponse(response2.body());
            if (fritzResponse.sid().equals(DEFAULT_SID)) {
                throw new IllegalArgumentException("Wrong username or password, could not login!");
            }
            LOGGER.info("Login successful, obtained session id from Fritz!Box: {}", fritzResponse.sid());
            lastResponse = fritzResponse;
        } catch (Exception e) {
            LOGGER.error("Error while fetching session id", e);
        }
    }

    private String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }
}
