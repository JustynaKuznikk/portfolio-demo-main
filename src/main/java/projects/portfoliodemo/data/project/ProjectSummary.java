package projects.portfoliodemo.data.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reprezentacja danych o projekcie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSummary {

    private Long id;
    private String name;
    private String username;
    private String url;
    private String description;
}
