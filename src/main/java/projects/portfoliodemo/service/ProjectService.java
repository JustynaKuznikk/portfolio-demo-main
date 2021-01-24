package projects.portfoliodemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projects.portfoliodemo.converter.ProjectConverter;
import projects.portfoliodemo.data.project.ProjectSummary;
import projects.portfoliodemo.domain.model.Project;
import projects.portfoliodemo.domain.model.User;
import projects.portfoliodemo.domain.repository.ProjectRepository;
import projects.portfoliodemo.domain.repository.UserRepository;
import projects.portfoliodemo.security.AuthenticatedUser;
import projects.portfoliodemo.web.command.CreateProjectCommand;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectConverter projectConverter;
    private final AuthenticatedUser authenticatedUser;

    @Transactional
    public Long add(CreateProjectCommand createProjectCommand) {
        log.debug("Dane do utworzenia projektu: {}", createProjectCommand);

        Project project = projectConverter.from(createProjectCommand);
        updateProjectWithUser(project);
//        if(projectRepository.existByNameOrUrl(project.getName(), project.getUrl())){
//            throw new IllegalStateException("Project with same name or url already exist");
//        }

        User user = userRepository.getAuthenticatedUser(authenticatedUser.getUsername());
        log.debug("Projekt do zapisu: {}", project);
        project.setUser(user);
        projectRepository.save(project);
        log.debug("Zapisany projekt: {}", project);
        return project.getId();
    }

    private void updateProjectWithUser(Project project) {
        String username = authenticatedUser.getUsername();
        User user = userRepository.getAuthenticatedUser(username);
        project.setUser(user);
    }

    @Transactional
    public List<ProjectSummary> findUserProjects() {
        log.debug("Pobieranie informacji o projektach użytkownika");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return projectRepository.findAllByUserUsername(username).stream()
                .map(projectConverter::toProjectSummary)
                .collect(Collectors.toList());
    }
}
