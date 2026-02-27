package ru.job4j.todo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;
import ru.job4j.todo.utility.LocalDateTimeConverter;

import java.util.TimeZone;

@ThreadSafe
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService simpleUserService) {
        this.userService = simpleUserService;
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model) {
        model.addAttribute("timeZones", userService.createAvailableTimeZonesList());
        model.addAttribute("pageTitle", "Регистрация");
        /* Добавляем часовой пояс по умолчанию, который будет выбран, если ползователь не выберет часовой пояс */
        model.addAttribute("defaultTimeZoneId", TimeZone.getDefault().getID());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user) {
        var userOptional = userService.save(user);
        if (userOptional.isEmpty()) {
            model.addAttribute("message", "Пользователь с такими логином и паролем уже существует");
            return "errors/409";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest request) {
        var userOptional = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Логин или пароль введены неверно");
            return "users/login";
        }
        var session = request.getSession();
        /* Прежде чем записать user-а в сессию, проверяем и если отсутствует устанавливаем timezone */
        user = LocalDateTimeConverter.checkAndSetDefaultUserTimezone(userOptional.get());
        session.setAttribute("user", user);
        return "redirect:/tasks";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

}
