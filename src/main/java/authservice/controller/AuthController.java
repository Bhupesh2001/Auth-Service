package authservice.controller;

import authservice.entities.RefreshToken;
import authservice.model.UserInfoDto;
import authservice.response.JwtResponseDTO;
import authservice.service.JwtService;
import authservice.service.RefreshTokenService;
import authservice.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.nonNull;

@AllArgsConstructor
@RestController
@Slf4j
public class AuthController
{

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> SignUp(@RequestBody UserInfoDto userInfoDto){
        try{
            Boolean isSignedUp = userDetailsService.signupUser(userInfoDto);
            if(Boolean.FALSE.equals(isSignedUp)){
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername());
            return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtToken).
                    token(refreshToken.getToken()).build(), HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("auth/v1/ping")
    public ResponseEntity<String> ping(){
        log.info("=== Ping endpoint called ===");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication object: {}", authentication);
        log.info("Authentication class: {}", authentication != null ? authentication.getClass().getName() : "null");
        log.info("Is authenticated: {}", authentication != null ? authentication.isAuthenticated() : "null");
        log.info("Principal: {}", authentication != null ? authentication.getPrincipal() : "null");
        // Add this specific check
        if(authentication instanceof AnonymousAuthenticationToken){
            log.error("Authentication is anonymous!");
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        if(nonNull(authentication) && authentication.isAuthenticated()){
            log.info("Authentication check passed");
            String username = authentication.getName();
            log.info("Username from authentication: {}", username);

            try {
                String userId = userDetailsService.getUserByUsername(username);
                log.info("Retrieved userId: {}", userId);

                if(nonNull(userId)){
                    log.info("Returning success response with userId: {}", userId);
                    return new ResponseEntity<>(userId, HttpStatus.OK);
                } else {
                    log.info("UserId is null for username: {}", username);
                }
            } catch (Exception e) {
                log.info("Exception while getting userId: ", e);
                throw e; // Re-throw to see in logs
            }
        } else {
            log.info("Authentication check failed");
        }

        log.info("Returning unauthorized response");
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
