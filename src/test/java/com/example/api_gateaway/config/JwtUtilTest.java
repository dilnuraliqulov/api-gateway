//package com.example.api_gateaway.config;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JwtUtilTest {
//
//    private JwtUtil jwtUtil;
//    private static final String SECRET = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm";
//    private SecretKey secretKey;
//
//    @BeforeEach
//    void setUp() {
//        jwtUtil = new JwtUtil(SECRET);
//        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
//    }
//
//    @Test
//    void validateToken_validToken_returnsTrue() {
//        String token = createValidToken("testuser");
//        assertTrue(jwtUtil.validateToken(token));
//    }
//
//    @Test
//    void validateToken_expiredToken_returnsFalse() {
//        String token = createExpiredToken("testuser");
//        assertFalse(jwtUtil.validateToken(token));
//    }
//
//    @Test
//    void validateToken_invalidToken_returnsFalse() {
//        assertFalse(jwtUtil.validateToken("invalid.token.here"));
//    }
//
//    @Test
//    void extractUsername_validToken_returnsUsername() {
//        String token = createValidToken("testuser");
//        assertEquals("testuser", jwtUtil.extractUsername(token));
//    }
//
//    @Test
//    void isTokenExpired_validToken_returnsFalse() {
//        String token = createValidToken("testuser");
//        assertFalse(jwtUtil.isTokenExpired(token));
//    }
//
//    @Test
//    void isTokenExpired_expiredToken_returnsTrue() {
//        String token = createExpiredToken("testuser");
//        assertTrue(jwtUtil.isTokenExpired(token));
//    }
//
//    private String createValidToken(String username) {
//        return Jwts.builder()
//                .subject(username)
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + 86400000))
//                .signWith(secretKey)
//                .compact();
//    }
//
//    private String createExpiredToken(String username) {
//        return Jwts.builder()
//                .subject(username)
//                .issuedAt(new Date(System.currentTimeMillis() - 100000))
//                .expiration(new Date(System.currentTimeMillis() - 50000))
//                .signWith(secretKey)
//                .compact();
//    }
//}
//
