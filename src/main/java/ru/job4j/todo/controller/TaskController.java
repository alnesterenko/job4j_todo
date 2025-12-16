package ru.job4j.todo.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.todo.service.TaskService;

@ThreadSafe
@Controller
@RequestMapping({"/", "/tasks"})
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService simpleTaskService) {
        this.taskService = simpleTaskService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "index";
    }
}
