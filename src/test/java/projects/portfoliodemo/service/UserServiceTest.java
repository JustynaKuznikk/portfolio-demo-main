package projects.portfoliodemo.service;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import projects.portfoliodemo.converter.UnconvertibleDataException;
import projects.portfoliodemo.converter.UserConverter;
import projects.portfoliodemo.domain.model.User;
import projects.portfoliodemo.domain.repository.UserRepository;
import projects.portfoliodemo.provider.CommandProvider;
import projects.portfoliodemo.web.command.RegisterUserCommand;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@DisplayName("User processes specification")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserConverter userConverter;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    //Poznala na przechwycenie argumentu:
    @Captor
    ArgumentCaptor<User> userCaptor;

    //jednocześnie tworzy objekt
    @InjectMocks
    UserService cut;

    @BeforeEach
    void setUp(){
        //
    }

//    @Test
//    void testMockito(){
//        assertNotNull(cut);
//        assertNotNull(userConverter);
//        assertNotNull(userRepository);
//        assertNotNull(passwordEncoder);
//    }

    @DisplayName("1. Creating new user")
    @Nested
    class CreateUser {



        @DisplayName("- should save user with provided data and set default values and return user id")
        @Test
        void test1() {
            RegisterUserCommand command = CommandProvider.registerUserCommand("duke", "pass");
            User convertedUser = User.builder()
                    .username("duke")
                    .password("pass")
                    .build();
            Mockito.when(userConverter.from(command)).thenReturn(convertedUser);
            Mockito.when(userRepository.existsByUsername("duke")).thenReturn(false);
            Mockito.when(passwordEncoder.encode("pass")).thenReturn("encoded");
            Mockito.when(userRepository.save(userCaptor.capture()))
                    .thenAnswer(invocationOnMock -> {
                        User userToSave = invocationOnMock.getArgument(0, User.class);
                        userToSave.setId(11L);
                        return userToSave;
                    });

            Long result = cut.create(command);
            User savedUser = userCaptor.getValue();
            assertNotNull(savedUser);
            assertEquals("duke", savedUser.getUsername());
            assertTrue(savedUser.getActive());
            Assertions.assertThat(savedUser.getRoles()).containsOnly("ROLE_USER");
            assertEquals("encoded", savedUser.getPassword());
            assertEquals(11L, result);


            //assertNotNull(result);
//            assertThat(result).isPositive();
//            MatcherAssert.assertThat(result, Matchers.greaterThan(0L));
//            assertThat(result)
//                    .isNotNull()
//                    .isNotPositive();
//            MatcherAssert.assertThat(result, allOf(notNullValue(), greaterThan(0L)));

        }
        @DisplayName("- should raise an error when user with username already exist")
        @Test
        void test2(){
            RegisterUserCommand command = CommandProvider.registerUserCommand("joe", "pass");
            User user = User.builder()
                    .username("joe")
                    .password("pass")
                    .build();
            Mockito.when(userConverter.from(command)).thenReturn(user);
            Mockito.when(userRepository.existsByUsername("joe")).thenReturn(true);
            assertThatThrownBy(() ->cut.create(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("User with same username already exist")
                    .hasMessageNotContaining("joe")
                    .hasNoCause();
            Mockito.verifyNoInteractions(passwordEncoder);
            Mockito.verifyNoMoreInteractions(userRepository);
        }
        @DisplayName("- should propagate error when command is unconvertible")
        @Test
        void test3(){
            UnconvertibleDataException exception = new UnconvertibleDataException("Cannot convert from null");
            Mockito.when(userConverter.from(null)).thenThrow(exception);
            assertThatThrownBy(() -> cut.create(null)).isEqualTo(exception);

        }
        @DisplayName("- should propagate error when cannot save")
        @Test
        void test4(){
            RegisterUserCommand command = CommandProvider.registerUserCommand("joe-cannot-be-saved", "pass");
            User user = User.builder().username("joe-cannot-be-saved").password("pass").build();
            Mockito.when(userConverter.from(command)).thenReturn(user);
            Mockito.when(userRepository.existsByUsername("joe-cannot-be-saved")).thenReturn(false);
            RuntimeException ex = new RuntimeException(new RuntimeException());
            Mockito.when(userRepository.save(ArgumentMatchers.any())).thenThrow(ex);
            Assertions.assertThatThrownBy(() -> cut.create(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(RuntimeException.class);
        }

//        @DisplayName("- should save user in repository")
//        @Test
//        void test2(){
//            RegisterUserCommand command = CommandProvider.registerUserCommand("Duke", "Pass");
//            cut.create(command);
//            Mockito.verify(userRepository, Mockito.times(1))
//                    .save(ArgumentMatchers.any(User.class));
//        }
//        @DisplayName("-should save user with provided data")
//        @Test
//        void test2(){
//            RegisterUserCommand command = CommandProvider.registerUserCommand("duke", "pass");
//            User expectedToSave = User.builder()
//                    .username("duke")
//                    .password("pass")
//                    .build();
//            Mockito.when(userConverter.from(command)).thenReturn(expectedToSave);
//            cut.create(command);
//            //upewniamy się że będzie wywołana i przechwytujemy wartość
//            Mockito.verify(userRepository, Mockito.atLeastOnce()).save(userCaptor.capture());
//            User savedUser = userCaptor.getValue();
//            //assertEquals(user.getUsername(), savedUser.getUsername());
//            assertThat(savedUser).isEqualToComparingOnlyGivenFields(expectedToSave, "username",
//                    "password");
//
//        }
//
//        @DisplayName("- should save user with as active with user role and encoded password")
//        @Test
//        void test3(){
//            RegisterUserCommand command = CommandProvider.registerUserCommand("duke", "pass");
//            User expectedToSave = User.builder()
//                    .username("duke")
//                    .password("pass")
//                    .build();
//            Mockito.when(userConverter.from(command)).thenReturn(expectedToSave);
//            Mockito.when(passwordEncoder.encode("pass")).thenReturn("encoded");
//
//            cut.create(command);
//
//            Mockito.verify(userRepository, atLeastOnce()).save(userCaptor.capture());
//            User savedUser = userCaptor.getValue();
//            assertTrue(savedUser.getActive());
//            Assertions.assertThat(savedUser.getRoles()).containsOnly("ROLE_USER");
//            assertEquals("encoded", savedUser.getPassword());
//
//
//        }

    }

}