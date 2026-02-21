package ru.job4j.todo.repository;

import ru.job4j.todo.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(Integer id);

    List<Category> findAllByIds(List<Integer> ids);

    /* Добавлено для удобства тестирования */

    Category add(Category category);

    boolean delete(Integer id);

    void clearRepository();
}
