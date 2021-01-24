package projects.portfoliodemo.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projects.portfoliodemo.data.user.UserSummary;
import projects.portfoliodemo.service.UserService;
import projects.portfoliodemo.web.command.EditUserCommand;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
@Slf4j
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @ModelAttribute("userSummary")
    public UserSummary userSummary() {
        return userService.getCurrentUserSummary();
    }

    @GetMapping
    public String getProfilePage(Model model) {
        EditUserCommand editUserCommand = createEditUserCommand(userSummary());
        model.addAttribute(editUserCommand);
        return "user/profile";
    }

    private EditUserCommand createEditUserCommand(UserSummary summary) {
        return EditUserCommand.builder()
                .firstName(summary.getFirstName())
                .lastName(summary.getLastName())
                .birthDate(summary.getBirthDate())
                .build();
    }

    @PostMapping("/edit")
    public String editUserProfile(@Valid EditUserCommand editUserCommand, BindingResult bindings) {
        log.debug("Dane do edycji użytkownika: {}", editUserCommand);
        if (bindings.hasErrors()) {
            log.debug("Błędne dane: {}", bindings.getAllErrors());
            return "user/profile";
        }

        try {
            boolean success = userService.edit(editUserCommand);
            log.debug("Udana edycja danych? {}", success);
            return "redirect:/profile";
        } catch (RuntimeException re) {
            log.warn(re.getLocalizedMessage());
            log.debug("Błąd przy edycji danych", re);
            bindings.rejectValue(null, null, "Wystąpił błąd");
        }
        return "redirect:/profile";
    }
}






