package br.com.urbana.service;

import br.com.urbana.dto.user.UserRequestDTO;
import br.com.urbana.dto.user.UserResponseDTO;
import br.com.urbana.entity.User;
import br.com.urbana.enums.UserRole;
import br.com.urbana.exception.EmailAlreadyExistsException;
import br.com.urbana.exception.UserNotFoundException;
import br.com.urbana.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO create(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        // Criptografa a senha com BCrypt antes de salvar
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus("Ativo");
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        return toResponseDTO(savedUser);
    }

    public List<UserResponseDTO> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UserResponseDTO findById(Long id) {

        User user = findEntityById(id);

        return toResponseDTO(user);
    }

    public User findEntityById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserResponseDTO update(
            Long id,
            UserRequestDTO request) {

        User existingUser = findEntityById(id);

        if (!existingUser.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {

            throw new EmailAlreadyExistsException();
        }

        existingUser.setName(request.name());
        existingUser.setEmail(request.email());
        
        // Se a senha tiver mudado/sido enviada, criptografa novamente
        if (request.password() != null && !request.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        User updatedUser = userRepository.save(existingUser);

        return toResponseDTO(updatedUser);
    }

    public UserResponseDTO toggleStatus(Long id) {

        User user = findEntityById(id);

        if ("Ativo".equals(user.getStatus())) {
            user.setStatus("Inativo");
        } else {
            user.setStatus("Ativo");
        }

        User updatedUser = userRepository.save(user);

        return toResponseDTO(updatedUser);
    }

    public void delete(Long id) {

        User user = findEntityById(id);

        userRepository.delete(user);
    }

    private UserResponseDTO toResponseDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus()
        );
    }
}