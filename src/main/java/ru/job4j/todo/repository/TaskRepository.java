package ru.job4j.todo.repository;

import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends AutoCloseable {

    Task add(Task task);

    boolean replace(Integer id, Task task);

    void delete(Integer id);

    List<Task> findAll();

    List<Task> findByTitle(String key);

    Optional<Task> findById(Integer id);

    List<Task> findAllByDone(boolean done);
}