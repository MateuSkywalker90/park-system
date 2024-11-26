package com.moliveira.demo_park_api.service;

import com.moliveira.demo_park_api.entity.User;
import com.moliveira.demo_park_api.exception.EntityNotFoundException;
import com.moliveira.demo_park_api.exception.PasswordInvalidException;
import com.moliveira.demo_park_api.exception.UsernameUniqueViolationException;
import com.moliveira.demo_park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("User '%s' already exists!", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User searchById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%s not found.", id))
        );
    }

    @Transactional
    public User changePassword(Long id, String actualPassword, String newPassword, String confirmedPassword) {
        if(!newPassword.equals(confirmedPassword)) {
            throw new PasswordInvalidException("New password is different from the confirmed one!");
        }

        User user = searchById(id);
        if (!passwordEncoder.matches(actualPassword, user.getPassword())) {
            throw new PasswordInvalidException("Current password is wrong!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User searchByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User '%s' not found.", username))
        );
    }

    @Transactional(readOnly = true)
    public User.Role getRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
