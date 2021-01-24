package projects.portfoliodemo.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projects.portfoliodemo.provider.CommandProvider;
import projects.portfoliodemo.security.CustomUserDetailsService;
import projects.portfoliodemo.service.UserService;
import projects.portfoliodemo.web.command.RegisterUserCommand;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Registration specification: /register")
@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {
    @MockBean
    CustomUserDetailsService mockCUDS;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    String endpoint = "/register";

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(userService);
    }

    // MockMvcRequestBuilders -> do tworzenia żądań
    // MockMvc

    @DisplayName("1. On GET request")
    @Nested
    class GetRequest {

        @DisplayName("- should prepare registration view with data in model")
        @Test
        void test1() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("register/form"))
                    .andExpect(MockMvcResultMatchers.model().attribute("data", new RegisterUserCommand()));
        }

        @DisplayName("- should allow anonymous user")
        @Test
        void test2() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                    .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

    @DisplayName("2. On POST request")
    @Nested
    class PostRequest {

        @DisplayName("- should create user when data is correct")
        @Test
        void test1() throws Exception {
            RegisterUserCommand command = CommandProvider.registerUserCommand("duke@mvc.pl", "pass");
            Mockito.when(userService.create(command)).thenReturn(22L);

            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "duke@mvc.pl")
                    .param("password", "pass")
                    .with(SecurityMockMvcRequestPostProcessors.anonymous())
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));

            Mockito.verify(userService, Mockito.times(1)).create(command);
        }

        @DisplayName("- should stay on registration view with errors for invalid data")
        @ParameterizedTest
        @CsvSource({
                "null, pa",
                ",",
                "bob, abcdefghijklafaaga"})
        void test2(String username, String password) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", username)
                    .param("password", password)
                    .with(SecurityMockMvcRequestPostProcessors.anonymous())
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("register/form"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("data"))
                    .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("data", "username", "password"));

            Mockito.verify(userService, Mockito.never()).create(ArgumentMatchers.any());
            Mockito.verifyNoInteractions(userService);
        }

    }

}