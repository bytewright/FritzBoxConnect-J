package de.codingForFun.eL.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Service
public class AppSecrets implements HealthIndicator, InitializingBean {
    private static final String FILENAME = "app.secret";
    private static final String KEY_HEALTH = "test.key";
    private static final String KEY_HEALTH_VALUE = "testkey";
    private static final String KEY_USERNAME = "fritz.user";
    private static final String KEY_PASS = "fritz.user.pw";
    private final Properties props = new Properties();

    @Override
    public Health health() {
        if (props.isEmpty()) {
            return Health.down().withDetail("secretfile", "empty or not found").build();
        }
        return Health.up().build();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() throws IOException {
        Path path = Path.of("secret", FILENAME);
        if (!Files.isReadable(path)) {
            throw new IllegalStateException("Can't read secrets file at " + path.toAbsolutePath());
        }
        // Load the secretfile from the current directory
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            props.load(fis);
        }

        // Get the values of some secrets
        String property = props.getProperty(KEY_HEALTH);
        if (!KEY_HEALTH_VALUE.equals(property)) {
            throw new IllegalStateException("secretfile does not contain healthkey");
        }
    }

    public String getUserName() {
        return props.getProperty(KEY_USERNAME);
    }

    public String getPassword() {
        return props.getProperty(KEY_PASS);
    }

    public Path getServerCertificate() {
        return Path.of("secret", "server.pem");
    }
}
