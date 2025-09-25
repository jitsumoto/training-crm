package com.example.trainingcrm.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.trainingcrm.dto.UserForm;
import com.example.trainingcrm.entity.User;
import com.example.trainingcrm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "userId"));
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User createUser(UserForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalStateException("Username already exists: " + form.getUsername());
        }
        if (!StringUtils.hasText(form.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }
        User user = new User();
        applyForm(user, form);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserForm form) {
        User user = getUser(id);
        if (userRepository.existsByUsernameAndUserIdNot(form.getUsername(), id)) {
            throw new IllegalStateException("Username already exists: " + form.getUsername());
        }
        applyForm(user, form);
        if (StringUtils.hasText(form.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void applyForm(User user, UserForm form) {
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setRole(form.getRole());
        user.setEnabled(Boolean.TRUE.equals(form.getEnabled()));
    }
}
