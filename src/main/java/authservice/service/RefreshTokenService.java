package authservice.service;

import authservice.entities.RefreshToken;
import authservice.entities.UserInfo;
import authservice.repository.RefreshTokenRepository;
import authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Creates and stores a new refresh token for the given username.
     *
     * @param username the username for which the refresh token is to be generated
     * @return a newly created and persisted RefreshToken
     */
    public RefreshToken createRefreshToken(String username){
        UserInfo userInfoExtracted = userRepository.findByUsername(username);

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfoExtracted)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // Token valid for 10 minutes
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the refresh token string
     * @return an Optional containing the token if found
     */
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies whether the refresh token is still valid.
     *
     * @param token the RefreshToken to verify
     * @return the same token if it has not expired
     * @throws RuntimeException if the token is expired (and deletes it from DB)
     */
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}
