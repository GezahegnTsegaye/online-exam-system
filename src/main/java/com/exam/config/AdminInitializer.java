package com.exam.config;


import com.exam.dal.model.Role;
import com.exam.dal.model.User;
import com.exam.dal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin-setup.email}")
    private String adminEmail;

    @Value("${app.security.admin-setup.password}")
    private String adminPassword;

    @Value("${app.security.admin-setup.name}")
    private String adminName;

    @Override
    public void run(String... args) {
        // Check if admin user already exists
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Initializing admin user: {}", adminEmail);

            User adminUser = User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(adminUser);

            log.info("Admin user created successfully");
        } else {
            log.info("Admin user already exists");
        }
    }
}