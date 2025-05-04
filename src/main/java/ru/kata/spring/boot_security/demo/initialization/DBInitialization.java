package ru.kata.spring.boot_security.demo.initialization; // Correct package name

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Component
public class DBInitialization {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(DBInitialization.class);

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";


    @Autowired
    public DBInitialization(UserService userService, RoleService roleService,
                            PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        Role adminRole = createRoleIfNotFound(ROLE_ADMIN);
        Role userRole = createRoleIfNotFound(ROLE_USER);

        createUserIfNotFound("111", "admin@example.com", "AdminFirstName",
                "AdminLastName", Collections.singleton(adminRole));
        createUserIfNotFound("222", "user@example.com", "UserFirstName",
                "UserLastName", Collections.singleton(userRole));
    }

    private Role createRoleIfNotFound(String roleName) {
        Role role = roleService.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            try {
                roleService.addRole(role);
                logger.info("Created role: " + roleName);
            } catch (Exception e) {
                logger.error("Error creating role " + roleName + ": " + e.getMessage(), e);
                throw new RuntimeException("Failed to create role: " + roleName, e);
            }
        } else {
            logger.info("Role already exists: " + roleName);
        }
        return role;
    }

    private void createUserIfNotFound(String password, String email, String firstName, String lastName, Collection<Role> roles) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setRoles((Set<Role>) roles);
            try {
                userService.save(user);
                System.out.println("Created user: " + email);
            } catch (Exception e) {
                System.err.println("Error creating user " + email + ": " + e.getMessage());
                throw new RuntimeException("Failed to create user: " + email, e);
            }
        } else {
            System.out.println("User already exists: " + email);
        }
    }
}
