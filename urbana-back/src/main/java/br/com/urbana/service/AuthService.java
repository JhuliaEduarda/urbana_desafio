package br.com.urbana.service;

import br.com.urbana.dto.auth.LoginRequestDTO;
import br.com.urbana.dto.auth.LoginResponseDTO;
import br.com.urbana.entity.User;
import br.com.urbana.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO authenticate(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha inválidos."));

        boolean matches = false;

        try {
            // Tenta verificar com BCrypt
            matches = passwordEncoder.matches(request.password(), user.getPassword());
        } catch (Exception e) {
            // Se a senha no banco for texto puro (antiga)
            matches = request.password().equals(user.getPassword());
        }

        // Caso ainda assim não bata (ou se o BCrypt falhou em texto puro)
        if (!matches && !request.password().equals(user.getPassword())) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }

        if (!"Ativo".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Usuário inativo. Acesso negado.");
        }

        return new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                "sessao-ativa"
        );
    }
}