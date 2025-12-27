package ru.job4j.todo.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.TaskService;

@ThreadSafe
@Controller
@SessionAttributes("user")
@RequestMapping({"/", "/tasks"})
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService simpleTaskService) {
        this.taskService = simpleTaskService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        model.addAttribute("pageTitle", "Все задачи");
        return "index";
    }

    @GetMapping("/done/{done}")
    public String getAllByDone(Model model, @PathVariable boolean done) {
        model.addAttribute("tasks", taskService.findAllByDone(done));
        model.addAttribute("pageTitle", "Только выполненные/не выполненные задачи");
        return "index";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("pageTitle", "Создание новой задачи");
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@SessionAttribute("user") User user, @ModelAttribute Task task, Model model) {
        task.setUser(user);
        try {
            taskService.add(task);
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        model.addAttribute("pageTitle", "Одна задача");
        return "tasks/one";
    }

    @GetMapping("/edit/{id}")
    public String getEditPage(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        model.addAttribute("pageTitle", "Редактирование задачи");
        return "tasks/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute Task task, Model model) {
        try {
            var isUpdated = taskService.replace(task.getId(), task);
            if (!isUpdated) {
                model.addAttribute("message", "Задача с указанным идентификатором не найдена");
                return "errors/404";
            }
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    @PostMapping("/one")
    public String switchUndoneToDone(@ModelAttribute Task task, Model model) {
        try {
            var isSwitched = taskService.switchUndoneToDone(task.getId());
            if (!isSwitched) {
                model.addAttribute("message", "Задача с указанным идентификатором не найдена");
                return "errors/404";
            }
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = taskService.delete(id);
        if (!isDeleted) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/tasks";
    }
}
