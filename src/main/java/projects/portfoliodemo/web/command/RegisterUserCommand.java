package projects.portfoliodemo.web.command;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterUserCommand {

    @NotNull @Email
    private String username;
    @NotBlank @Size(min = 4, max = 12)
    private String password;
}
