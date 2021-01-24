package projects.portfoliodemo.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projects.portfoliodemo.service.ProjectService;

@Controller
@RequestMapping("/projects")
@Slf4j
@RequiredArgsConstructor
public class ProjectListController {

    private final ProjectService projectService;

    @GetMapping
    public String getProjectListPage(Model model) {
        model.addAttribute("userProjects", projectService.findUserProjects());
        return "project/list";
    }
}
