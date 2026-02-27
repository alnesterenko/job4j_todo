package ru.job4j.todo.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;
import ru.job4j.todo.utility.LocalDateTimeConverter;

import java.util.List;

@ThreadSafe
@Controller
@SessionAttributes("user")
@RequestMapping({"/", "/tasks"})
public class TaskController {

    private final TaskService taskService;

    private final PriorityService priorityService;

    private final CategoryService categoryService;

    public TaskController(
            TaskService simpleTaskService,
            PriorityService simplePriorityService,
            CategoryService simpleCategoryService
    ) {
        this.taskService = simpleTaskService;
        this.priorityService = simplePriorityService;
        this.categoryService = simpleCategoryService;
    }

    /* Вывод всех задач */
    @GetMapping
    public String getAll(@SessionAttribute("user") User user, Model model) {
        List<Task> convertedTaskList = LocalDateTimeConverter.convertTimeCreatedTaskList(taskService.findAll(), user.getTimezone());
        model.addAttribute("tasks", convertedTaskList);
        model.addAttribute("pageTitle", "Все задачи");
        return "index";
    }

    /* Вывод только выполненных/не выполненных задач */
    @GetMapping("/done/{done}")
    public String getAllByDone(@SessionAttribute("user") User user, Model model, @PathVariable boolean done) {
        List<Task> convertedTaskList = LocalDateTimeConverter.convertTimeCreatedTaskList(taskService.findAllByDone(done), user.getTimezone());
        model.addAttribute("tasks", convertedTaskList);
        model.addAttribute("pageTitle", "Только выполненные/не выполненные задачи");
        return "index";
    }

    /* Создание задачи */
    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Создание новой задачи");
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(
            @SessionAttribute("user") User user,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @ModelAttribute Task task,
            Model model) {
        task.setUser(user);
        task.setCategories(categoryService.findAllByIds(categoryIds));
        try {
            taskService.add(task);
            return "redirect:/tasks";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    /* Вывод одной задачи */
    @GetMapping("/{id}")
    public String getById(@SessionAttribute("user") User user, Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанным идентификатором не найдена");
            return "errors/404";
        }
        Task convertedTask = LocalDateTimeConverter.convertTimeCreatedOneTask(taskOptional.get(), user.getTimezone());
        model.addAttribute("task", convertedTask);
        model.addAttribute("pageTitle", "Одна задача");
        return "tasks/one";
    }

    /* Редактированние задачи */
    @GetMapping("/edit/{id}")
    public String getEditPage(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Редактирование задачи");
        return "tasks/edit";
    }

    @PostMapping("/edit")
    public String update(
            @ModelAttribute Task task,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            Model model) {
        try {
            task.setCategories(categoryService.findAllByIds(categoryIds));
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

    /* Одностороннее переключение статуса задачи на "выполнена".
     Переключить обратно на "не выполнена" можно на странице редактирования */
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

    /* Удаление задачи */
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
