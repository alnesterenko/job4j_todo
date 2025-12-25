package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@ThreadSafe
@Repository
@AllArgsConstructor
public class HbnUserRepository implements UserRepository {

    private final CrudRepository crudRepository;

    private static Logger logger = Logger.getLogger(HbnUserRepository.class.getName());

    @Override
    public Optional<User> save(User user) {
        Optional<User> optionalUser = Optional.empty();
        try {
            crudRepository.run(session -> session.save(user));
            optionalUser = Optional.of(user);
        } catch (ConstraintViolationException e) {
            logger.info("Нарушение уникального индекса или первичного ключа: " + e);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return optionalUser;
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudRepository.optional(
                "FROM User AS u WHERE u.login = :login AND u.password = :password",
                User.class,
                Map.of("login", login, "password", password));
    }

    @Override
    public Collection<User> findAll() {
        return crudRepository.query("SELECT u FROM User u ORDER BY u.id ASC", User.class);
    }

    @Override
    public boolean deleteById(Integer id) {
        int result = crudRepository.run(
                "DELETE User WHERE id = :id",
                Map.of("id", id)
        );
        return result > 0;
    }

    @Override
    public Optional<User> findById(Integer id) {
        return crudRepository.optional(
                "FROM User WHERE id = :id", User.class,
                Map.of("id", id)
        );
    }
}
