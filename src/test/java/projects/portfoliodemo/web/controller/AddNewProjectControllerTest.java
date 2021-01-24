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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import projects.portfoliodemo.provider.CommandProvider;
import projects.portfoliodemo.security.CustomUserDetailsService;
import projects.portfoliodemo.security.SecurityConfig;
import projects.portfoliodemo.service.ProjectService;
import projects.portfoliodemo.web.command.CreateProjectCommand;
import projects.portfoliodemo.web.command.RegisterUserCommand;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Add project specification: /project")
@WebMvcTest(AddNewProjectController.class)
class AddNewProjectControllerTest {

    @MockBean
    CustomUserDetailsService mockCUDS;

    @MockBean
    ProjectService projectService;

    @Autowired
    MockMvc mockMvc;

    String endpoint = "/projects/add";

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(projectService);
    }

    @DisplayName("1. On GET request")
    @Nested
    class GetRequest {
        @DisplayName("- should prepare add view with data in model")
        @Test
//        @WithMockUser("duke")
        void test1() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                    .with(SecurityMockMvcRequestPostProcessors.user("duke")))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("projects/add"))
                    .andExpect(MockMvcResultMatchers.model().attribute("data", new CreateProjectCommand()));

        }
    }
    @DisplayName("2. On POST request")
    @Nested
    class PostRequest {
        @DisplayName("-should create project when data is correct")
        @Test
        void test1() throws Exception {
            CreateProjectCommand command = CommandProvider.createProjectCommand("project", "new project", "http://project.com");
            Mockito.when(projectService.add(command)).thenReturn(2L);
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("name", "project")
                    .param("description", "new project")
                    .param("url", "http://project.com")
                    .with(SecurityMockMvcRequestPostProcessors.user("duke"))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.redirectedUrl("/projects"));
            Mockito.verify(projectService, Mockito.times(1)).add(command);
        }
        @DisplayName("- should stay on projects view with errors for invalid data")
        @ParameterizedTest
        @CsvSource({
                "1,s,ef",
                 "a,,4"
        })
        void test2(String name, String description, String url) throws Exception{
            Mockito.clearInvocations(projectService);
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("name", name)
                    .param("description", description)
                    .param("url", url)
                    .with(SecurityMockMvcRequestPostProcessors.user("duke"))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("project/add"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("data"))
                    .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("data",
                            "name", "description", "url"));
            Mockito.verify(projectService, Mockito.never()).add(ArgumentMatchers.any());
            Mockito.verifyNoInteractions(projectService);

        }
    }


}