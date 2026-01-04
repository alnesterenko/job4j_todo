package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@AllArgsConstructor
public class HbnTaskRepository implements TaskRepository {
    private final CrudRepository crudRepository;

    @Override
    public Task add(Task task) {
        crudRepository.run(session -> session.save(task));
        return task;
    }

    @Override
    public boolean replace(Integer id, Task task) {
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("title", task.getTitle());
        argsMap.put("description", task.getDescription());
        argsMap.put("priority_id", task.getPriority().getId());
        argsMap.put("done", task.isDone());
        argsMap.put("id", id);
        int updatedLines = crudRepository.run(
                "UPDATE Task SET title = :title, "
                        + "description = :description, "
                        + "priority_id = :priority_id, "
                        + "done = :done  WHERE id = :id",
                argsMap);
        return updatedLines > 0;
    }

    @Override
    public boolean delete(Integer id) {
        int updatedLines = crudRepository.run(
                "DELETE Task WHERE id = :id",
                Map.of("id", id)
        );
        return updatedLines > 0;
    }

    @Override
    public List<Task> findAll() {
        return crudRepository.query("SELECT t FROM Task t JOIN FETCH t.priority ORDER BY t.id ASC", Task.class);
    }

    @Override
    public List<Task> findByTitle(String key) {
        return crudRepository.query("FROM Task AS t JOIN FETCH t.priority WHERE t.title = :title", Task.class, Map.of("title", key));
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return crudRepository.optional("FROM Task AS t JOIN FETCH t.priority WHERE t.id = :id", Task.class, Map.of("id", id));
    }

    @Override
    public List<Task> findAllByDone(boolean done) {
        return crudRepository.query("FROM Task AS t JOIN FETCH t.priority WHERE t.done = :done", Task.class, Map.of("done", done));
    }

    @Override
    public boolean switchUndoneToDone(Integer id) {
        int updatedLines = crudRepository.run(
                "UPDATE Task SET done = :done  WHERE id = :id",
                Map.of("done", true,
                        "id", id));
        return updatedLines > 0;
    }
}
