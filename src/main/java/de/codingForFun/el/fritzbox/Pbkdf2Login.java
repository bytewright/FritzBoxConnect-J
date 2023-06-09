package de.codingForFun.el.fritzbox;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * C&P from AVM docs
 */
public class Pbkdf2Login {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pbkdf2Login.class);

    /**
     * Calculate the secret key on Android.
     */
    public static String calculatePbkdf2Response(String challenge, String password) throws FailedChallengeResponse {
        try {
            String[] challengeParts = challenge.split("\\$");
            int iter1 = Integer.parseInt(challengeParts[1]);
            byte[] salt1 = fromHex(challengeParts[2]);
            int iter2 = Integer.parseInt(challengeParts[3]);
            byte[] salt2 = fromHex(challengeParts[4]);
            LOGGER.debug("Calculating Response from Challenge {}, Using Salt1:'{}', Salt2:'{}', iter 1/2: {}/{}",
                    challenge, salt1, salt2, iter1, iter2);
            byte[] hash1 = pbkdf2HmacSha256(password.getBytes(StandardCharsets.UTF_8), salt1, iter1);
            byte[] hash2 = pbkdf2HmacSha256(hash1, salt2, iter2);
            String response = challengeParts[4] + "$";
            return response + toHex(hash2);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new FailedChallengeResponse(e);
        }
    }

    /**
     * Hex string to bytes
     */
    private static byte[] fromHex(String hexString) {
        int len = hexString.length() / 2;
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = (byte) Short.parseShort(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        return ret;
    }

    /**
     * byte array to hex string
     */
    private static String toHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder s = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            s.append(String.format("%02x", b));
        }
        return s.toString();
    }

    /**
     * Create a pbkdf2 HMAC by appling the Hmac iter times as specified.
     * We can't use the Android-internal PBKDF2 here, as it only accepts char[] arrays, not bytes (for multi-stage hashing)
     */
    private static byte[] pbkdf2HmacSha256(final byte[] password, final byte[] salt, int iters) throws InvalidKeyException, NoSuchAlgorithmException {
        String alg = "HmacSHA256";
        Mac sha256mac = Mac.getInstance(alg);
        sha256mac.init(new SecretKeySpec(password, alg));
        byte[] ret = new byte[sha256mac.getMacLength()];
        byte[] tmp = new byte[salt.length + 4];
        System.arraycopy(salt, 0, tmp, 0, salt.length);
        tmp[salt.length + 3] = 1;
        for (int i = 0; i < iters; i++) {
            tmp = sha256mac.doFinal(tmp);
            for (int k = 0; k < ret.length; k++) {
                ret[k] ^= tmp[k];
            }
        }
        return ret;
    }
}
