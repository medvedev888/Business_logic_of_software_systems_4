package me.ifmo.backend.bpm.security;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.User;
import me.ifmo.backend.exceptions.BusinessException;
import me.ifmo.backend.exceptions.NotFoundException;
import me.ifmo.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class ProcessAuthorizationService {
    private final UserRepository userRepository;

    public User getUserByCamundaUserId(String camundaUserId) {
        Long userId;
        try {
            userId = Long.valueOf(camundaUserId);
        } catch (NumberFormatException e) {
            throw new BusinessException("Invalid Camunda user id: " + camundaUserId);
        }

        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));
    }


    public void requirePrivilege(String camundaUserId, String privilegeName) {
        User user = getUserByCamundaUserId(camundaUserId);

        boolean allowed = user.getRoles().stream().flatMap(role -> role.getPrivileges().stream())
                .anyMatch(privilege -> privilegeName.equals(privilege.getName()));

        if (!allowed) {
            throw new BusinessException(
                    "User " + camundaUserId + " does not have privilege " + privilegeName
            );
        }
    }


    public void requireUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(
                    "User with id " + userId + " not found"
            );
        }
    }
}