package ru.job4j.todo.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleTaskService implements TaskService {

    private final TaskRepository taskRepository;

    public SimpleTaskService(TaskRepository hbnTaskRepository) {
        this.taskRepository = hbnTaskRepository;
    }

    @Override
    public Task add(Task task) {
        return taskRepository.add(task);
    }

    @Override
    public boolean replace(Integer id, Task task) {
        return taskRepository.replace(id, task);
    }

    @Override
    public boolean delete(Integer id) {
        return taskRepository.delete(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findByTitle(String key) {
        return taskRepository.findByTitle(key);
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findAllByDone(boolean done) {
        return taskRepository.findAllByDone(done);
    }

    @Override
    public boolean switchUndoneToDone(Integer id) {
        return taskRepository.switchUndoneToDone(id);
    }
}
