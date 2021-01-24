package projects.portfoliodemo.converter;

import org.springframework.stereotype.Component;
import projects.portfoliodemo.data.project.ProjectSummary;
import projects.portfoliodemo.domain.model.Project;
import projects.portfoliodemo.web.command.CreateProjectCommand;

import static projects.portfoliodemo.converter.ConverterUtils.requiredNotNull;

@Component
public class ProjectConverter {

    public Project from(CreateProjectCommand command) {
        requiredNotNull(command);
        return Project.builder()
                .name(command.getName())
                .url(command.getUrl())
                .description(command.getDescription())
                .build();

    }

    public ProjectSummary toProjectSummary(Project project) {
        return ProjectSummary.builder()
                .name(project.getName())
                .url(project.getUrl())
                .description(project.getDescription())
                .username(project.getUser().getUsername())
                .build();

    }
}
