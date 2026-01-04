package ru.job4j.todo.repository;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@RequiredArgsConstructor
public class HbnPriorityRepository implements PriorityRepository {
    private final CrudRepository crudRepository;

    @Override
    public List<Priority> findAll() {
        return crudRepository.query("SELECT p FROM Priority AS p ORDER BY p.id ASC", Priority.class);
    }

    @Override
    public Priority add(Priority priority) {
        crudRepository.run(session -> session.save(priority));
        return priority;
    }

    @Override
    public Optional<Priority> findById(Integer id) {
        return crudRepository.optional("FROM Priority AS p WHERE p.id = :id", Priority.class, Map.of("id", id));
    }

    @Override
    public boolean delete(Integer id) {
        int updatedLines = crudRepository.run(
                "DELETE Priority WHERE id = :id",
                Map.of("id", id)
        );
        return updatedLines > 0;
    }
}
