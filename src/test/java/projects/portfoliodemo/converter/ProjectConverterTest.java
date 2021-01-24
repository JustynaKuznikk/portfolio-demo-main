package projects.portfoliodemo.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import projects.portfoliodemo.domain.model.Project;
import projects.portfoliodemo.web.command.CreateProjectCommand;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Project converting specification")
class ProjectConverterTest {

    ProjectConverter cut;

    @BeforeEach
    void setUp(){
        cut = new ProjectConverter();
    }

    static CreateProjectCommand createProjectCommand(String name, String description,
                                                     String url){
        CreateProjectCommand command = new CreateProjectCommand();
        command.setName(name);
        command.setDescription(description);
        command.setUrl(url);
        return command;
    }

    /*
    1. Pierwszy test: test optymistyczny; standardowe użycie metody, w której wszystko działa
    2. Przypadki alternatywne: gdy warunki zachowują się w inny sposób ---> 100% pokrycia kodu
    3. Przypadki błędne: gdy dane powodują wyjątki
     */

    @DisplayName("1. Converting from create command")
    @Nested
    class ConvertFromCreateProjectCommand{

        @DisplayName("- should convert to project with all provided data")
        @Test
        void test1(){
            //Given
            CreateProjectCommand command = createProjectCommand("New project",
                    "Fantastic new project", "https://github.com/honestit/portfolio-demo");

            //When
            Project result = cut.from(command);

            //Then

            assertThatProvidedValuesAreSet(result,"New project","Fantastic new project",
                    "https://github.com/honestit/portfolio-demo");

            assertThatNothingThanExpectedIsSet(result, "name", "description",
                    "url");

        }
        @DisplayName("- should raise error when no data provided")
        @Test
        void test2(){
            assertThatRaiseErrorWithMessage(null, UnconvertibleDataException.class, ".*Cannot convert from null");
        }
    }

    private void assertThatRaiseErrorWithMessage(CreateProjectCommand command, Class<? extends Exception> klass, String message) {
        Assertions.assertThatThrownBy(() -> cut.from(command))
                .isInstanceOf(klass)
                .hasMessageMatching(message)
                .hasNoCause();
    }

    private void assertThatNothingThanExpectedIsSet(Project result, String... properties) {
        Assertions.assertThat(result).hasAllNullFieldsOrPropertiesExcept(properties);
    }

    private void assertThatProvidedValuesAreSet(Project result, String name, String description,
                                                String url) {
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(url, result.getUrl());
    }

}