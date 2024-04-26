package com.example.ecommercemarketplace.services.impls;


import com.example.ecommercemarketplace.dto.MerchantDto;
import com.example.ecommercemarketplace.dto.PasswordResetConfirmationRequestDto;
import com.example.ecommercemarketplace.dto.PasswordResetRequestDto;
import com.example.ecommercemarketplace.dto.UserDto;
import com.example.ecommercemarketplace.events.EmailChangeEvent;
import com.example.ecommercemarketplace.mappers.Mapper;
import com.example.ecommercemarketplace.models.Merchant;
import com.example.ecommercemarketplace.models.PasswordResetToken;
import com.example.ecommercemarketplace.models.UserEntity;
import com.example.ecommercemarketplace.repositories.PasswordResetTokenRepository;
import com.example.ecommercemarketplace.security.JwtService;
import com.example.ecommercemarketplace.services.EmailService;
import com.example.ecommercemarketplace.services.MerchantService;
import com.example.ecommercemarketplace.services.PasswordResetService;
import com.example.ecommercemarketplace.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserService userService;
    private final MerchantService merchantService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final Mapper<Merchant, MerchantDto> merchantMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean requestPasswordReset(PasswordResetRequestDto passwordResetRequestDto) {
        String email = passwordResetRequestDto.getEmail();

        if (!userService.existsByEmail(email)) {
            return false;
        }

        PasswordResetToken passwordResetToken;
        UserEntity user = userMapper.mapFrom(userService.findByEmail(email));

        String tokenValue = jwtService.generatePasswordResetToken(email);
        if (passwordResetTokenRepository.existsByUser(user)) {
            passwordResetToken = passwordResetTokenRepository.findByUser(user).get();
            passwordResetToken.setToken(tokenValue);
        } else {
            passwordResetToken = PasswordResetToken.builder()
                    .token(tokenValue)
                    .user(user)
                    .build();
        }

        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendMessageWithPasswordResetCode(email, tokenValue);

        return true;
    }

    @Override
    public boolean confirmPasswordReset(PasswordResetConfirmationRequestDto passwordResetConfirmationRequestDto) {
        String token = passwordResetConfirmationRequestDto.getToken();
        if (!jwtService.isValid(token)) {
            return false;
        }

        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken.isEmpty()) {
            return false;
        }

        String newPassword = passwordEncoder.encode(passwordResetConfirmationRequestDto.getPassword());
        UserEntity user = passwordResetToken.get().getUser();
        user.setPassword(newPassword);

        updateUserMerchant(user);

        publishEvent(user);

        passwordResetTokenRepository.delete(passwordResetToken.get());
        return true;
    }

    private void publishEvent(UserEntity user) {
        eventPublisher.publishEvent(new EmailChangeEvent(this, user));
    }

    private void updateUserMerchant(UserEntity user) {
        if (user instanceof Merchant merchant) {
            merchantService.updateMerchant(merchantMapper.mapTo(merchant));
        } else {
            userService.updateUser(userMapper.mapTo(user));
        }
    }

    @Override
    public void deleteExpiredTokens() {
        List<PasswordResetToken> tokens = passwordResetTokenRepository.findAll();

        for (var token : tokens) {
            try {
                jwtService.isValid(token.getToken());
            } catch (Exception e) {
                passwordResetTokenRepository.delete(token);
            }
        }
    }
}
