package com.example.ecommercemarketplace.listeners;

import com.example.ecommercemarketplace.dto.UserDto;
import com.example.ecommercemarketplace.services.LoginAttemptEmailService;
import com.example.ecommercemarketplace.services.LoginAttemptIPService;
import com.example.ecommercemarketplace.services.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;



@Component
@AllArgsConstructor
@Slf4j
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final LoginAttemptEmailService loginAttemptEmailService;
    private final LoginAttemptIPService loginAttemptIpService;
    private final UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();
        UserDto userDto = userService.findByEmail(email);
        String ipAddress = loginAttemptIpService.getClientIP();

        loginAttemptIpService.registerFailedLogin(ipAddress);
        loginAttemptEmailService.registerFailureLogin(email);
        log.info("User with IP_ADDRESS={} and publicId={} is failed to login", ipAddress, userDto.getPublicId());
    }
}
