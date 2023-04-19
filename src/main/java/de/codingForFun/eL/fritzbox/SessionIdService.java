package de.codingForFun.eL.fritzbox;

import de.codingForFun.eL.util.AppSecrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionIdService implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionIdService.class);
    // see https://avm.de/service/schnittstellen/
    private static final String URL = "https://fritz.box/login_sid.lua?version=2";
    @Autowired
    private ParseSessionRespService parseSessionRespService;
    @Autowired
    private FritzBoxHttpClient httpClient;
    @Autowired
    private AppSecrets appSecrets;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Context is initialized: {}", event);
        Optional<String> sid = getSessionId();
        LOGGER.info("obtained SID: {}", sid);
    }

    public Optional<String> getSessionId() {
        LOGGER.info("Fetching SessionId from Fritz!Box at {}", URL);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofMinutes(2))
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request);
            LOGGER.info("Initial SID request statusCode: {}", response.statusCode());
            FritzResp fritzResp = parseSessionRespService.parse(response.body());

            String challengeResponse = Pbkdf2Login.calculatePbkdf2Response(fritzResp.getChallenge(), appSecrets.getPassword());

            Map<String, String> formData = new HashMap<>();
            formData.put("username", appSecrets.getUserName());
            formData.put("response", challengeResponse);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofMinutes(2))
                    .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<InputStream> response2 = httpClient.send(request2);
            LOGGER.info("Challenge response statusCode: {}", response2.statusCode());

            FritzResp fritzResp2 = parseSessionRespService.parse(response2.body());
            if (fritzResp2.getSid().equals("0000000000000000")) {
                throw new IllegalArgumentException("Wrong username or password, could not login!");
            }
            return Optional.of(fritzResp2.getSid());
        } catch (Exception e) {
            LOGGER.error("Error while fetching session id", e);
        }
        return Optional.empty();
    }

    private static String getFormDataAsString(Map<String, String> formData) {
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
