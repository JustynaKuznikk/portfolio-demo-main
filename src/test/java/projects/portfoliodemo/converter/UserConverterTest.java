package projects.portfoliodemo.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import projects.portfoliodemo.domain.model.User;
import projects.portfoliodemo.web.command.RegisterUserCommand;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User converting specification")
class UserConverterTest {

    UserConverter cut; //Class under test

    @BeforeEach
    void setUp(){
       cut = new UserConverter();
    }

    static RegisterUserCommand registerUserCommand(String username, String password){
        RegisterUserCommand command = new RegisterUserCommand();
        command.setUsername(username);
        command.setPassword(password);
        return command;
    }

    /*
    1. Pierwszy test: test optymistyczny; standardowe użycie metody, w której wszystko działa
    2. Przypadki alternatywne: gdy warunki zachowują się w inny sposób ---> 100% pokrycia kodu
    3. Przypadki błędne: gdy dane powodują wyjątki
     */

    // pogrupoanie obiektów - zagnieższanie
    @DisplayName("1. Converting from registration command")
    @Nested
    class ConvertFromRegisterUserCommand {

        @DisplayName("- should convert to user with all provided data")
        @Test
        void test1(){
            //given
            RegisterUserCommand command = registerUserCommand("duke", "s3cr3t");

            //when
            User result = cut.from(command);

            //then
            assertThatProvidedValuesAreSet(result, "duke", "s3cr3t");
            assertThatActiveFalseByDefault(result);
            assertThatNothingThanExpectedIsSet(result, "username",
                    "password", "roles", "active");
            assertThatRolesAreEmptyByDefault(result);
        }
        @DisplayName("- should convert to user with default values set")
        @Test
        void test2() {
            RegisterUserCommand command = registerUserCommand("any", "any");
            User result = cut.from(command);
            assertThatActiveFalseByDefault(result);
            assertThatRolesAreEmptyByDefault(result);
        }
        @DisplayName("- should raise error when no data provided")
        @Test
        void test3(){
            //RegisterUserCommand command = null;
            //assertThrows(UnconvertibleDataException.class,() -> cut.from(command));
            assertThatRaiseErrorWithMessage(null, UnconvertibleDataException.class, ".*Cannot convert from null");
        }


    }

    private void assertThatRaiseErrorWithMessage(RegisterUserCommand command,
                                                 Class<? extends Exception> klass, String message) {
        Assertions.assertThatThrownBy(() -> cut.from(command))
                .isInstanceOf(klass)
                .hasMessageMatching(message)
                .hasNoCause();
    }

    private void assertThatActiveFalseByDefault(User result) {
        assertEquals(false, result.getActive());
    }

    private void assertThatRolesAreEmptyByDefault(User result){
        Assertions.assertThat(result.getRoles()).isEmpty();
    }

    private void assertThatProvidedValuesAreSet(User result, String username, String password) {
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());

    }
    private void assertThatNothingThanExpectedIsSet(User result, String... properties) {
        Assertions.assertThat(result).hasAllNullFieldsOrPropertiesExcept(properties);

    }

}