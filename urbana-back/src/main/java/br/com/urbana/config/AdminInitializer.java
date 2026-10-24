package br.com.urbana.config;

import br.com.urbana.entity.User;
import br.com.urbana.enums.UserRole;
import br.com.urbana.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.name}") String adminName,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminPassword) {

        return args -> {
            if (adminPassword == null || adminPassword.length() < 8) {
                throw new IllegalStateException(
                        "A senha do ADMIN deve ter pelo menos 8 caracteres."
                );
            }

            User admin;

            if (userRepository.existsByEmail(adminEmail)) {
                admin = userRepository.findByEmail(adminEmail)
                        .orElseThrow();

                if (admin.getRole() != UserRole.ADMIN) {
                    throw new IllegalStateException(
                            "O e-mail configurado para ADMIN já pertence a um usuário comum."
                    );
                }

                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setName(adminName);
            } else {
                admin = new User();
                admin.setName(adminName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setStatus("Ativo");
                admin.setRole(UserRole.ADMIN);
            }

            userRepository.save(admin);
        };
    }
}
