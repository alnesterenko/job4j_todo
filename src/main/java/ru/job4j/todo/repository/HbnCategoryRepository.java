package ru.job4j.todo.repository;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ThreadSafe
@Repository
@RequiredArgsConstructor
public class HbnCategoryRepository implements CategoryRepository {
    private final CrudRepository crudRepository;

    @Override
    public List<Category> findAll() {
        return crudRepository.query("FROM Category AS c ORDER BY c.id ASC", Category.class);
    }

    @Override
    public Category add(Category category) {
        crudRepository.run(session -> session.save(category));
        return category;
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return crudRepository.optional("FROM Category AS c WHERE c.id = :id", Category.class, Map.of("id", id));
    }

    @Override
    public List<Category> findAllByIds(List<Integer> ids) {
        List<Category> resultList = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            resultList = crudRepository.query("FROM Category AS c WHERE c.id IN (:ids)", Category.class, Map.of("ids", ids));
        }
        return resultList;
    }

    @Override
    public boolean delete(Integer id) {
        int updatedLines = crudRepository.run(
                "DELETE Category WHERE id = :id",
                Map.of("id", id)
        );
        return updatedLines > 0;
    }

    @Override
    public void clearRepository() {
        var categories = findAll();
        for (Category oneCategory : categories) {
            delete(oneCategory.getId());
        }
    }
}
