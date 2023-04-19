package de.codingForFun.eL.homeAutomation;

import de.codingForFun.eL.fritzbox.FritzBoxHttpClient;
import de.codingForFun.eL.fritzbox.SessionIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Service
public class FetchSensorDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchSensorDataService.class);
    /**
     * Die Kommandos werden über einen HTTP GET Request an die URL abgesetzt
     */
    private static final String URL = "https://fritz.box/webservices/homeautoswitch.lua";
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
    private SessionIdService sessionIdService;
    @Autowired
    private FritzBoxHttpClient fritzBoxHttpClient;

    public String generateUrl(String ain, String cmd, String sid) {
        return URL + "?" + PARAM_AIN + ain + "&" + PARAM_CMD + cmd + "&" + PARAM_SID + sid;
    }
    // erst mit gettemplatelistinfos die ains holen, dann temps fetchen

    public String getTemp(String ain) {
        String cmd = "gettemperature";
        Optional<String> sessionId = sessionIdService.getSessionId();
        String url = generateUrl(ain, cmd, sessionId.get());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .build();
        try {
            HttpResponse<InputStream> response = fritzBoxHttpClient.send(request);
            LOGGER.info("resp: {}", response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
