package com.expenseinsight.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT (JSON Web Token) operations.
 * 
 * <p>Provides methods for:</p>
 * <ul>
 *   <li>Generating JWT tokens from user details</li>
 *   <li>Validating JWT tokens</li>
 *   <li>Extracting information (username, expiration, claims) from tokens</li>
 * </ul>
 * 
 * <p>Security considerations:</p>
 * <ul>
 *   <li>Tokens are signed using HS256 algorithm with a secure secret key</li>
 *   <li>Secret key should be at least 256 bits (32 characters) long</li>
 *   <li>Tokens expire after a configurable period (default 24 hours)</li>
 *   <li>Token validation includes signature verification and expiration check</li>
 * </ul>
 * 
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Component
public class JwtUtil {

    /**
     * Secret key used for signing JWT tokens.
     * Loaded from application.properties (jwt.secret).
     * Should be a strong, randomly generated key of at least 256 bits.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token expiration time in milliseconds.
     * Loaded from application.properties (jwt.expiration).
     * Default: 86400000 ms (24 hours).
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Extracts the username (subject) from a JWT token.
     * 
     * @param token JWT token string
     * @return Username contained in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     * 
     * @param token JWT token string
     * @return Expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from a JWT token using a claims resolver function.
     * 
     * @param token JWT token string
     * @param claimsResolver Function to extract the desired claim
     * @param <T> Type of the claim to extract
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     * 
     * @param token JWT token string
     * @return Claims object containing all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if a JWT token has expired.
     * 
     * @param token JWT token string
     * @return true if the token has expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates a JWT token against user details.
     * 
     * <p>Validation includes:</p>
     * <ul>
     *   <li>Username in token matches the provided UserDetails</li>
     *   <li>Token has not expired</li>
     * </ul>
     * 
     * @param token JWT token string
     * @param userDetails UserDetails to validate against
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Generates a JWT token for a user.
     * 
     * @param username Username to include in the token
     * @return Generated JWT token string
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Creates a JWT token with specified claims and subject.
     * 
     * <p>Token structure:</p>
     * <ul>
     *   <li>Claims: Custom data to include in the token</li>
     *   <li>Subject: Username</li>
     *   <li>IssuedAt: Current timestamp</li>
     *   <li>Expiration: Current time + expiration period</li>
     *   <li>Signature: HMAC-SHA256 signature using secret key</li>
     * </ul>
     * 
     * @param claims Additional claims to include in the token
     * @param username Username (subject) of the token
     * @return Signed JWT token string
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates the signing key from the secret.
     * 
     * @return Key object for signing/verifying tokens
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}