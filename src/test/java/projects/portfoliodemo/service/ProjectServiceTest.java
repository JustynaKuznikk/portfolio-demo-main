package projects.portfoliodemo.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import projects.portfoliodemo.converter.ProjectConverter;
import projects.portfoliodemo.converter.UnconvertibleDataException;
import projects.portfoliodemo.domain.model.Project;
import projects.portfoliodemo.domain.model.User;
import projects.portfoliodemo.domain.repository.ProjectRepository;
import projects.portfoliodemo.domain.repository.UserRepository;
import projects.portfoliodemo.provider.CommandProvider;
import projects.portfoliodemo.security.AuthenticatedUser;
import projects.portfoliodemo.web.command.CreateProjectCommand;
import projects.portfoliodemo.web.command.RegisterUserCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Project processes specification")
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectConverter projectConverter;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticatedUser authenticatedUser;

    @InjectMocks
    ProjectService cut;

    @Captor
    ArgumentCaptor<Project> projectCaptor;

    @BeforeEach
    void setUp(){
        //
    }

//    @Test
//    void testMockito(){
//        assertNotNull(cut);
//        assertNotNull(projectConverter);
//        assertNotNull(projectRepository);
//        assertNotNull(userRepository);
//    }

    @DisplayName("1. Creating new project")
    @Nested
    class CreateProject {

        @DisplayName("- Should save project and return its id")
        @Test
        void test1() {
            CreateProjectCommand command = CommandProvider.createProjectCommand("project", "new project", "http://project.com");
            Project projectToSave = Project.builder()
                    .name("project")
                    .description("new project")
                    .url("http://project.com")
                    .build();
            Mockito.when(projectConverter.from(command)).thenReturn(projectToSave);
            Mockito.when(projectRepository.save(projectCaptor.capture()))
                    .thenAnswer(invocationOnMock -> {
                        Project project = invocationOnMock.getArgument(0, Project.class);
                        project.setId(10L);
                        return project;
                    });
            Mockito.when(userRepository.getAuthenticatedUser("duke")).thenReturn(User.builder()
                    .id(5L)
                    .username("duke")
                    .build());
            Mockito.when(authenticatedUser.getUsername()).thenReturn("duke");
            Long result = cut.add(command);
            Project savedProject = projectCaptor.getValue();
            assertNotNull(savedProject);
            assertNotNull(savedProject.getUser());
            assertEquals(5L, savedProject.getUser().getId());
            assertEquals(10L, result);

        }

        @DisplayName("- should propagate error when command is uncorvertible")
        @Test
        void test2(){
            UnconvertibleDataException exception = new UnconvertibleDataException("Cannot convert from null");
            Mockito.when(projectConverter.from(null)).thenThrow(exception);
            assertThatThrownBy(() -> cut.add(null)).isEqualTo(exception);
        }
        @DisplayName("- Should raise an error when project with url or name already exist")
        @Test
        void test3(){
            CreateProjectCommand command = CommandProvider.createProjectCommand("project", "new project", "http://project.com");
            Project project = Project.builder()
                    .name("project")
                    .description("new project")
                    .url("http://project.com")
                    .build();
            Mockito.when(projectConverter.from(command)).thenReturn(project);
            //Mockito.when(projectRepository.existByNameOrUrl("project","http://project.com")).thenReturn(true);
            assertThatThrownBy(() -> cut.add(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Project with same name or url already exist")
                    .hasNoCause();
            Mockito.verifyNoMoreInteractions(projectRepository);
        }
    }

}