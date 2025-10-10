package authservice.controller;

import authservice.entities.RefreshToken;
import authservice.entities.UserInfo;
import authservice.request.AuthRequestDTO;
import authservice.request.RefreshTokenRequestDTO;
import authservice.response.JwtResponseDTO;
import authservice.service.JwtService;
import authservice.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class TokenController
{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("auth/v1/login")
    public ResponseEntity<JwtResponseDTO> AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            return new ResponseEntity<>(JwtResponseDTO.builder()
                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                    .token(refreshToken.getToken())
                    .build(), HttpStatus.OK);
        } catch (Exception ex){
            return new ResponseEntity<>(JwtResponseDTO.builder().error(ex.getMessage()).build(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public ResponseEntity<JwtResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        String requestToken = refreshTokenRequestDTO.getToken();

        // Find the refresh token in the database
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(requestToken);

        RefreshToken refreshToken;
        try{
            if (refreshTokenOptional.isEmpty()) {
                throw new RuntimeException("Refresh Token is not in DB..!!");
            }
            refreshToken = refreshTokenService.verifyExpiration(refreshTokenOptional.get());
        } catch (Exception e) {
            return new ResponseEntity<>(JwtResponseDTO.builder().error(e.getMessage()).build(),
                    HttpStatus.UNAUTHORIZED);
        }

        // Get user info from refresh token
        UserInfo userInfo = refreshToken.getUserInfo();

        // Generate new access token
        String accessToken = jwtService.GenerateToken(userInfo.getUsername());

        // Build and return response

        return ResponseEntity.ok(JwtResponseDTO.builder()
                .accessToken(accessToken)
                .token(requestToken)
                .build());
    }

}
