package ru.job4j.todo.repository;

import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends AutoCloseable {

    Optional<User> save(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    /* Пришлось добавить ради удобства тестирования */
    Collection<User> findAll();

    boolean deleteById(Integer id);

    Optional<User> findById(Integer id);
}
