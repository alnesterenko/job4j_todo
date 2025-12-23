package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ThreadSafe
@Repository
@AllArgsConstructor
public class HbnUserRepository implements UserRepository, AutoCloseable {

    private final SessionFactory sf;

    private static Logger logger = Logger.getLogger(HbnUserRepository.class.getName());

    @Override
    public Optional<User> save(User user) {
        Session session = this.sf.openSession();
        Optional<User> optionalUser = Optional.empty();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            optionalUser = Optional.of(user);
        } catch (ConstraintViolationException e) {
            session.getTransaction().rollback();
            logger.info("Нарушение уникального индекса или первичного ключа: " + e);
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return optionalUser;
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        try (Session session = this.sf.openSession()) {
            Query<User> query = session.createQuery(
                    "FROM User AS u WHERE u.login = :login AND u.password = :password", User.class);
            query.setParameter("login", login);
            query.setParameter("password", password);
            return Optional.ofNullable(query.uniqueResult());
        }
    }

    @Override
    public Collection<User> findAll() {
        List<User> result = new ArrayList<>();
        try (Session session = this.sf.openSession()) {
            Query<User> query = session.createQuery("SELECT u FROM User u ORDER BY u.id ASC");
            result = query.list();
        }
        return result;
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean result = false;
        Session session = this.sf.openSession();
        try {
            session.beginTransaction();
            var updatedLines = session.createQuery(
                            "DELETE User WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            session.getTransaction().commit();
            result = updatedLines > 0;
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public Optional<User> findById(Integer id) {
        try (Session session = this.sf.openSession()) {
            Query<User> query = session.createQuery(
                    "FROM User AS u WHERE u.id = :id", User.class);
            query.setParameter("id", id);
            return Optional.ofNullable(query.uniqueResult());
        }
    }

    @Override
    public void close() {
        this.sf.close();
    }
}
