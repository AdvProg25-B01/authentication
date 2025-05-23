package id.ac.ui.cs.advprog.authentication.config;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String defaultAdminEmail = "admin@gmail.com";
        String defaultAdminPassword = "Admin123!";

        if (userRepository.findByEmail(defaultAdminEmail).isEmpty()) {
            User admin = User.builder()
                    .email(defaultAdminEmail)
                    .password(passwordEncoder.encode(defaultAdminPassword))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
            System.out.println("Default admin account created (email: admin@gmail.com / password: Admin123!)");
        } else {
            System.out.println("Default admin account already exists.");
        }
    }
}
