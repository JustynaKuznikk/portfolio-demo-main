package projects.portfoliodemo.web.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditUserCommand {

    @Size(max = 64)
    private String firstName;
    @Size(max = 64)
    private String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past
    private LocalDate birthDate;
}
