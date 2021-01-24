package projects.portfoliodemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projects.portfoliodemo.converter.UserConverter;
import projects.portfoliodemo.data.user.UserSummary;
import projects.portfoliodemo.domain.model.User;
import projects.portfoliodemo.domain.model.UserDetails;
import projects.portfoliodemo.domain.repository.UserRepository;
import projects.portfoliodemo.exception.UserAlreadyExistsException;
import projects.portfoliodemo.web.command.EditUserCommand;
import projects.portfoliodemo.web.command.RegisterUserCommand;

import java.util.Set;

@Service
@Slf4j @RequiredArgsConstructor
public class UserService {

    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long create(RegisterUserCommand registerUserCommand) {
        User user = userConverter.from(registerUserCommand);
        if(userRepository.existsByUsername(user.getUsername())){
            throw new IllegalStateException("User with same username already exist");
        }
        user.setActive(true);
        user.getRoles().add("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user.getId();
//        log.debug("Dane użytkownika do zapisania: {}", registerUserCommand);
//
//        User userToCreate = userConverter.from(registerUserCommand);
//        log.debug("Uzyskany obiekt użytkownika do zapisu: {}", userToCreate);
//        if (userRepository.existsByUsername(userToCreate.getUsername())) {
//            log.debug("Próba rejestracji na istniejącego użytkownika");
//            throw new UserAlreadyExistsException(String.format("Użytkownik %s już istnieje", userToCreate.getUsername()));
//        }
//
//        setEncodedPassword(userToCreate);
//        setDefaultData(userToCreate);
//        userRepository.save(userToCreate);
//        log.debug("Zapisany użytkownik: {}", userToCreate);
//
//        return userToCreate.getId();
    }

    private void setDefaultData(User userToCreate) {
        setDefaultActive(userToCreate);
        setDefaultRole(userToCreate);
        setDefaultDetails(userToCreate);
    }

    private void setDefaultDetails(User userToCreate) {
        userToCreate.setDetails(UserDetails.builder()
                .user(userToCreate)
                .build());
    }

    private void setEncodedPassword(User userToCreate) {
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
    }

    private void setDefaultRole(User userToCreate) {
        userToCreate.setRoles(Set.of("ROLE_USER"));
    }

    private void setDefaultActive(User userToCreate) {
        userToCreate.setActive(Boolean.TRUE);
    }

    @Transactional
    public UserSummary getCurrentUserSummary() {
        log.debug("Pobieranie danych użytkownika aktualnie zalogowanego");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.getAuthenticatedUser(username);
        UserSummary summary = userConverter.toUserSummary(user);
        log.debug("Podsumowanie danych użytkownika: {}", summary);

        return summary;
    }

    @Transactional
    public boolean edit(EditUserCommand editUserCommand) {
        log.debug("Dane do edycji użytkownika: {}", editUserCommand);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.getAuthenticatedUser(username);
        log.debug("Edycja użytkownika: {}", user);

        user = userConverter.from(editUserCommand, user);
        log.debug("Zmodyfikowane dane użytkownika: {}", user.getDetails());
        return true;
    }
}
