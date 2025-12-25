package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.job4j.todo.model.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/* Этот класс добавлено только для примера. В приложении он никак использоваться не будет. */

@AllArgsConstructor
public class UserRepositoryExample {
    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     * @param user пользователь.
     * @return пользователь с id.
     */
    public User create(User user) {
        crudRepository.run(session -> session.persist(user));
        return user;
    }

    /**
     * Обновить в базе пользователя.
     * @param user пользователь.
     */
    public void update(User user) {
        crudRepository.run(session -> session.merge(user));
    }

    /**
     * Удалить пользователя по id.
     * @param userId ID
     */
    public void delete(int userId) {
        crudRepository.run(
                "DELETE FROM User WHERE id = :fId",
                Map.of("fId", userId)
        );
    }

    /**
     * Список пользователь отсортированных по id.
     * @return список пользователей.
     */
    public List<User> findAllOrderById() {
        return crudRepository.query("FROM User ORDER BY id ASC", User.class);
    }

    /**
     * Найти пользователя по ID
     * @return пользователь.
     */
    public Optional<User> findById(int userId) {
        return crudRepository.optional(
                "FROM User WHERE id = :fId", User.class,
                Map.of("fId", userId)
        );
    }

    /**
     * Список пользователей по login LIKE %key%
     * @param key key
     * @return список пользователей.
     */
    public List<User> findByLikeLogin(String key) {
        return crudRepository.query(
                "FROM User WHERE login LIKE :fKey", User.class,
                Map.of("fKey", "%" + key + "%")
        );
    }

    /**
     * Найти пользователя по login.
     * @param login login.
     * @return Optional or user.
     */
    public Optional<User> findByLogin(String login) {
        return crudRepository.optional(
                "FROM User WHERE login = :fLogin", User.class,
                Map.of("fLogin", login)
        );
    }

    /* Для справки:
    * Методы org.hibernate.Session (Hibernate)
    *
        -   save(Object entity): Сохраняет объект (создает новую запись в БД, генерирует ID).
    *
        -   update(Object entity): Обновляет существующий объект в БД.
    *
        -   persist(Object entity): Присоединяет объект к сессии, делая его persistent.
    *
        -   merge(Object entity): Объединяет detached-объект с состоянием в сессии.
    *
        -   delete(Object entity): Удаляет объект из БД.
    *
        -   get(Class<T> entityClass, Serializable id): Загружает объект по ID.
    *
        -   refresh(Object entity): Обновляет состояние объекта из БД.  */
}