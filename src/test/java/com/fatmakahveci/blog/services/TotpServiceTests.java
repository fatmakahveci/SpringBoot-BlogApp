package com.fatmakahveci.blog.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class TotpServiceTests {

    @Test
    void validatesCurrentCodesAndRejectsMalformedOrIncorrectCodes() {
        TotpService service = new TestableTotpService(Clock.fixed(Instant.ofEpochSecond(59), ZoneOffset.UTC));
        String generatedKey = service.generateSecret();
        String currentCode = service.codeAt(generatedKey, 1);

        assertThat(service.verify(generatedKey, currentCode)).isTrue();
        assertThat(service.verify(generatedKey, differentCode(currentCode))).isFalse();
        assertThat(service.verify(generatedKey, "12345")).isFalse();
    }

    @Test
    void encryptsSecretsAtRestAndCanDecryptThem() {
        TotpService service = new TestableTotpService(Clock.systemUTC());
        String generatedKey = service.generateSecret();

        String encrypted = service.encryptSecret(generatedKey);

        assertThat(encrypted).doesNotContain(generatedKey);
        assertThat(service.decryptSecret(encrypted)).isEqualTo(generatedKey);
    }

    @Test
    void generatedSecretsHaveFullAuthenticatorEntropy() {
        TotpService service = new TestableTotpService(Clock.systemUTC());

        assertThat(service.generateSecret()).matches("[A-Z2-7]{32}");
    }

    private static final class TestableTotpService extends TotpService {
        private TestableTotpService(Clock clock) {
            super("test", "0000000000000000", clock);
        }
    }

    private String differentCode(String code) {
        return code.equals("000000") ? "000001" : "000000";
    }
}
