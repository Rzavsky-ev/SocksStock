package org.skypro.socksStock.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProviderTest;

    private final String testSecret = "testSecretKeyWithMinimumLength32Chars";
    private final int testExpirationMs = 3600000;
    private final String testUsername = "testUser";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProviderTest, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProviderTest, "jwtExpirationMs", testExpirationMs);
    }

    @DisplayName("Должен создать секретный ключ на основе конфигурируемого секрета")
    @Test
    void getSigningKeyReturnSecretKeyBasedOnConfiguredSecret() throws Exception {
        Method getSigningKeyMethod = JwtTokenProvider.class.getDeclaredMethod("getSigningKey");
        getSigningKeyMethod.setAccessible(true);

        SecretKey signingKey = (SecretKey) getSigningKeyMethod.invoke(jwtTokenProviderTest);

        assertNotNull(signingKey);
        assertEquals("HmacSHA256", signingKey.getAlgorithm());
    }

    @DisplayName("Должен сгенерировать валидный JWT токен для имени пользователя")
    @Test
    void generateTokenGenerateValidJwtToken() {
        String token = jwtTokenProviderTest.generateToken(testUsername);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String usernameFromToken = jwtTokenProviderTest.getUsernameFromToken(token);
        assertEquals(testUsername, usernameFromToken);
    }

    @DisplayName("Должен извлечь имя пользователя из валидного токена")
    @Test
    void getUsernameFromTokenExtractUsernameFromValidToken() {
        String token = jwtTokenProviderTest.generateToken(testUsername);

        String extractedUsername = jwtTokenProviderTest.getUsernameFromToken(token);

        assertEquals(testUsername, extractedUsername);
    }

    @DisplayName("Должен вернуть true для валидного токена")
    @Test
    void validateTokenReturnTrueForValidToken() {
        String token = jwtTokenProviderTest.generateToken(testUsername);

        boolean isValid = jwtTokenProviderTest.validateToken(token);

        assertTrue(isValid);
    }

    @DisplayName("Должен вернуть false для невалидного токена")
    @Test
    void validateTokenReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProviderTest.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @DisplayName("Должен вернуть false для истекшего токена")
    @Test
    void validateTokenReturnFalseForExpiredToken() {
        String expiredToken = generateExpiredToken();

        boolean isValid = jwtTokenProviderTest.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @DisplayName("Должен залогировать ошибку для истекшего токена")
    @Test
    void validateTokenLogErrorForExpiredToken() {
        String expiredToken = generateExpiredToken();

        try (MockedStatic<Jwts> mockedJwts = mockStatic(Jwts.class)) {
            JwtParserBuilder parserBuilder = mock(JwtParserBuilder.class);
            JwtParser parser = mock(JwtParser.class);

            mockedJwts.when(Jwts::parser).thenReturn(parserBuilder);
            when(parserBuilder.verifyWith(any(SecretKey.class))).thenReturn(parserBuilder);
            when(parserBuilder.build()).thenReturn(parser);
            when(parser.parseSignedClaims(expiredToken)).thenThrow(ExpiredJwtException.class);

            boolean isValid = jwtTokenProviderTest.validateToken(expiredToken);

            assertFalse(isValid);
        }
    }

    @DisplayName("Должен залогировать ошибку для некорректного формата токена")
    @Test
    void validateTokenLogErrorForMalformedToken() {
        String malformedToken = "malformed.token";

        boolean isValid = jwtTokenProviderTest.validateToken(malformedToken);

        assertFalse(isValid);
    }

    @DisplayName("Должен залогировать ошибку для null токена")
    @Test
    void validateTokenLogErrorForNullToken() {
        String nullToken = null;

        boolean isValid = jwtTokenProviderTest.validateToken(nullToken);

        assertFalse(isValid);
    }

    @DisplayName("Должен генерировать токены с разными именами пользователей")
    @Test
    void generateTokenGenerateDifferentTokensForDifferentUsernames() {
        String username1 = "user1";
        String username2 = "user2";

        String token1 = jwtTokenProviderTest.generateToken(username1);
        String token2 = jwtTokenProviderTest.generateToken(username2);

        assertNotEquals(token1, token2);
        assertEquals(username1, jwtTokenProviderTest.getUsernameFromToken(token1));
        assertEquals(username2, jwtTokenProviderTest.getUsernameFromToken(token2));
    }

    @DisplayName("Должен генерировать токен с временем экспирации")
    @Test
    void generateTokenIncludeExpirationDate() {
        String token = jwtTokenProviderTest.generateToken(testUsername);

        assertTrue(jwtTokenProviderTest.validateToken(token));
    }

    private String generateExpiredToken() {
        Date pastDate = new Date(System.currentTimeMillis() - 10000);

        return Jwts.builder()
                .subject(testUsername)
                .issuedAt(pastDate)
                .expiration(new Date(pastDate.getTime() + 1000))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(testSecret.getBytes());
    }
}
