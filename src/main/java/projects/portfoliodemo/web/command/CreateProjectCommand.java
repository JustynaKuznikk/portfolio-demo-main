package projects.portfoliodemo.web.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProjectCommand {

    @NotNull
    @Size(min = 3, max = 64)
    private String name;
    @URL
    private String url;
    @NotNull
    @Size(min = 3, max = 160)
    private String description;
}
