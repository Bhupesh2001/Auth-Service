package authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    /**
     * Secret key used for signing and validating JWTs.
     *
     * ⚠️ For security reasons, this should be stored in an environment variable or a secure vault.
     * It's kept here in plain text temporarily for simplicity in development/testing.
     */
    public static final String SECRET = "357638792F423F4428472B4B6250655368566D597133743677397A2443264629";

    /**
     * Extracts the username (subject) from the given JWT.
     *
     * @param token the JWT token
     * @return the username embedded in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the given JWT.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT using a claims resolver function.
     *
     * @param <T> the type of the claim to be returned
     * @param token the JWT token
     * @param claimsResolver a function to resolve a specific claim from the JWT claims
     * @return the resolved claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the token using the secret key to retrieve all claims.
     *
     * @param token the JWT token
     * @return the claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates the token by checking:
     * - the username in the token matches the username in the user details
     * - the token has not expired
     *
     * @param token the JWT token
     * @param userDetails the user details to compare against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks whether the JWT has expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Decodes the secret key and returns the cryptographic signing key.
     *
     * @return the signing key used for JWT operations
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the given username with default claims.
     *
     * @param username the username to embed in the token
     * @return the generated JWT token
     */
    public String GenerateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Builds a JWT token with the given claims and username.
     *
     * @param claims a map of custom claims to include in the token
     * @param username the subject of the token
     * @return the JWT token string
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60)) // Token valid for 60 seconds
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }
}
