package me.ifmo.backend.services.impl;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.user.RegisterUserRequest;
import me.ifmo.backend.DTO.user.RegisterUserResponse;
import me.ifmo.backend.entities.Role;
import me.ifmo.backend.entities.User;
import me.ifmo.backend.exceptions.BusinessException;
import me.ifmo.backend.exceptions.NotFoundException;
import me.ifmo.backend.repositories.RoleRepository;
import me.ifmo.backend.repositories.UserRepository;
import me.ifmo.backend.services.UserService;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.authorization.*;
import org.camunda.bpm.engine.identity.Group;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;


@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityService identityService;
    private final AuthorizationService authorizationService;

    @Override
    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String roleName = request.getRole().trim().toUpperCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("User with this email already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(new HashSet<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        user.getRoles().add(role);
        user = userRepository.saveAndFlush(user);

        createCamundaUser(user, request.getPassword(), roleName);

        return new RegisterUserResponse(user.getId(), user.getEmail(), roleName, user.getId().toString());
    }


    private void createCamundaUser(User user, String password, String roleName) {
        String userId = user.getId().toString();

        org.camunda.bpm.engine.identity.User camundaUser = identityService.newUser(userId);
        camundaUser.setEmail(user.getEmail());
        camundaUser.setFirstName(user.getFirstName());
        camundaUser.setLastName(user.getLastName());
        camundaUser.setPassword(password);
        identityService.saveUser(camundaUser);

        Group group = identityService.createGroupQuery()
                .groupId(roleName)
                .singleResult();

        if (group == null) {
            group = identityService.newGroup(roleName);
            group.setName(roleName);
            group.setType("APPLICATION");
            identityService.saveGroup(group);
        }

        identityService.createMembership(userId, roleName);

        ensureCamundaPermissions(roleName);
    }

    private void ensureCamundaPermissions(String groupId) {
        createAuthorization(groupId, Resources.APPLICATION, "tasklist", Permissions.ACCESS);
        createAuthorization(groupId, Resources.PROCESS_DEFINITION, "course-enrollment-process",
                Permissions.READ, Permissions.CREATE_INSTANCE);
        createAuthorization(groupId, Resources.FILTER, "*", Permissions.READ);
        createAuthorization(groupId, Resources.PROCESS_INSTANCE, "*",
                ProcessInstancePermissions.CREATE);
    }

    private void createAuthorization(String groupId, Resources resource, String resourceId,
                                     Permission... permissions) {
        Authorization authorization = authorizationService.createAuthorizationQuery()
                .groupIdIn(groupId)
                .resourceType(resource.resourceType())
                .resourceId(resourceId)
                .singleResult();

        if (authorization == null) {
            authorization = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
            authorization.setGroupId(groupId);
            authorization.setResource(resource);
            authorization.setResourceId(resourceId);
        }

        for (Permission permission : permissions) {
            authorization.addPermission(permission);
        }

        authorizationService.saveAuthorization(authorization);
    }
}