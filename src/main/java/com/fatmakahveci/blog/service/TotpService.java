package com.fatmakahveci.blog.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TotpService {

    private static final String BASE32 = "ABCDEFGHIJKLM" + "NOPQRSTUVWXYZ" + "234567";
    private static final int SECRET_BYTES = 20;
    private static final long TIME_STEP_SECONDS = 30;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Clock clock;
    private final TextEncryptor encryptor;

    @Autowired
    public TotpService(
            @Value("${blog.security.mfa.encryption-password:dev}") String password,
            @Value("${blog.security.mfa.encryption-salt:0000000000000000}") String salt) {
        this(password, salt, Clock.systemUTC());
    }

    protected TotpService(String password, String salt, Clock clock) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("MFA encryption password must not be blank");
        }
        this.clock = clock;
        this.encryptor = Encryptors.delux(password, salt);
    }

    public String generateSecret() {
        byte[] bytes = new byte[SECRET_BYTES];
        secureRandom.nextBytes(bytes);
        return encodeBase32(bytes);
    }

    public String provisioningUri(String username, String secret) {
        return UriComponentsBuilder.newInstance()
                .scheme("otpauth")
                .host("totp")
                .pathSegment("Spring Blog:" + username)
                .queryParam("secret", secret)
                .queryParam("issuer", "Spring Blog")
                .queryParam("algorithm", "SHA1")
                .queryParam("digits", 6)
                .queryParam("period", 30)
                .build()
                .encode()
                .toUriString();
    }

    public boolean verify(String secret, String submittedCode) {
        if (secret == null || submittedCode == null || !submittedCode.matches("\\d{6}")) {
            return false;
        }
        long counter = clock.instant().getEpochSecond() / TIME_STEP_SECONDS;
        for (long offset = -1; offset <= 1; offset++) {
            if (constantTimeEquals(codeAt(secret, counter + offset), submittedCode)) {
                return true;
            }
        }
        return false;
    }

    public String encryptSecret(String secret) {
        return encryptor.encrypt(secret);
    }

    public String decryptSecret(String encryptedSecret) {
        return encryptor.decrypt(encryptedSecret);
    }

    String codeAt(String secret, long counter) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(decodeBase32(secret), "HmacSHA1"));
            byte[] hash = mac.doFinal(ByteBuffer.allocate(Long.BYTES).putLong(counter).array());
            int offset = hash[hash.length - 1] & 0x0f;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);
            return String.format(Locale.ROOT, "%06d", binary % 1_000_000);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to calculate TOTP", exception);
        }
    }

    private String encodeBase32(byte[] bytes) {
        StringBuilder encoded = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;
        for (byte value : bytes) {
            buffer = (buffer << 8) | (value & 0xff);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                encoded.append(BASE32.charAt((buffer >> (bitsLeft - 5)) & 31));
                bitsLeft -= 5;
            }
        }
        if (bitsLeft > 0) {
            encoded.append(BASE32.charAt((buffer << (5 - bitsLeft)) & 31));
        }
        return encoded.toString();
    }

    private byte[] decodeBase32(String value) {
        String normalized = value.replace("=", "").replace(" ", "").toUpperCase(Locale.ROOT);
        byte[] decoded = new byte[normalized.length() * 5 / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;
        for (char character : normalized.toCharArray()) {
            int digit = BASE32.indexOf(character);
            if (digit < 0) {
                throw new IllegalArgumentException("Invalid Base32 secret");
            }
            buffer = (buffer << 5) | digit;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                decoded[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }
        return decoded;
    }

    private boolean constantTimeEquals(String expected, String actual) {
        return java.security.MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII), actual.getBytes(StandardCharsets.US_ASCII));
    }

}
