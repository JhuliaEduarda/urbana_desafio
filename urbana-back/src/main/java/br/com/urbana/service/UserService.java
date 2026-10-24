package br.com.urbana.service;

import br.com.urbana.dto.user.ChangePasswordRequestDTO;
import br.com.urbana.dto.user.UserRequestDTO;
import br.com.urbana.dto.user.UserResponseDTO;
import br.com.urbana.dto.user.UserUpdateRequestDTO;
import br.com.urbana.entity.User;
import br.com.urbana.enums.UserRole;
import br.com.urbana.exception.EmailAlreadyExistsException;
import br.com.urbana.exception.UserNotFoundException;
import br.com.urbana.repository.UserRepository;
import br.com.urbana.repository.CardRequestRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardRequestRepository cardRequestRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CardRequestRepository cardRequestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cardRequestRepository = cardRequestRepository;
    }

    public UserResponseDTO create(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
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
            UserUpdateRequestDTO request) {
        User existingUser = findEntityById(id);

        if (!existingUser.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        existingUser.setName(request.name());
        existingUser.setEmail(request.email());

        if (request.password() != null
                && !request.password().isBlank()) {
            existingUser.setPassword(
                    passwordEncoder.encode(request.password()));
        }

        User updatedUser = userRepository.save(existingUser);

        return toResponseDTO(updatedUser);
    }

    public void changePassword(
            Long id,
            ChangePasswordRequestDTO request) {
        User user = findEntityById(id);

        boolean currentPasswordMatches = passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword());

        if (!currentPasswordMatches) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha atual está incorreta.");
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A nova senha deve ser diferente da senha atual.");
        }

        user.setPassword(
                passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);
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

    @Transactional
    public void delete(Long id) {
        User user = findEntityById(id);

        cardRequestRepository.deleteByUserId(user.getId());

        userRepository.delete(user);
    }

    private UserResponseDTO toResponseDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus());
    }
}