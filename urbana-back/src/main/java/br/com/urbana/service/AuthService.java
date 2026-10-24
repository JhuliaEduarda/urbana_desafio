package br.com.urbana.service;

import br.com.urbana.dto.auth.LoginRequestDTO;
import br.com.urbana.dto.auth.LoginResponseDTO;
import br.com.urbana.entity.User;
import br.com.urbana.repository.UserRepository;
import br.com.urbana.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO authenticate(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new RuntimeException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(
                request.password(), user.getPassword())) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }

        if (!"Ativo".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Usuário inativo. Acesso negado.");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }
}
