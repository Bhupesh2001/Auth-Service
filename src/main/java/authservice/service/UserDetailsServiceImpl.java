package authservice.service;

import authservice.entities.UserInfo;
import authservice.model.UserInfoDto;
import authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * Service implementation for loading user-specific data.
 * This class integrates with Spring Security to perform authentication
 * and supports user signup functionality.
 */
@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    /**
     * Loads the user by username from the database.
     *
     * @param username the username to search for
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepository.findByUsername(username);
        if(user == null){
            log.error("Username not found: {}", username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }

    /**
     * Checks if a user already exists in the system.
     *
     * @param userInfoDto DTO containing the username to check
     * @return UserInfo if the user exists, otherwise null
     */
    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    /**
     * Registers a new user if the username is not already taken.
     * Password is encoded before saving.
     *
     * @param userInfoDto DTO containing user registration data
     * @return true if user was successfully registered, false if user already exists
     */
    public Boolean signupUser(UserInfoDto userInfoDto){
        // Validation placeholder - Uncomment after implementing ValidationUtil
        // ValidationUtil.validateUserAttributes(userInfoDto);

        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));

        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))){
            return false;
        }

        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), new HashSet<>()));

        // Placeholder for sending registration event to a queue
        // pushEventToQueue

        return true;
    }
}
