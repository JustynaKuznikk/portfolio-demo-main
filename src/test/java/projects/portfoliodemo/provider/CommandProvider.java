package projects.portfoliodemo.provider;

import projects.portfoliodemo.web.command.CreateProjectCommand;
import projects.portfoliodemo.web.command.RegisterUserCommand;

public class CommandProvider {
    public static RegisterUserCommand registerUserCommand(String username, String password){
        RegisterUserCommand command = new RegisterUserCommand();
        command.setUsername("duke");
        command.setPassword("pass");
        return command;
    }
    public static CreateProjectCommand createProjectCommand(String name, String description,
                                                            String url){
        CreateProjectCommand command = new CreateProjectCommand();
        command.setName("project");
        command.setDescription("new project");
        command.setUrl("http://project.com");
        return command;
    }



}
