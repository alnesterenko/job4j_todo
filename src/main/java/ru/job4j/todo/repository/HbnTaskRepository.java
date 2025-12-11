package ru.job4j.todo.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbnTaskRepository implements TaskRepository, AutoCloseable {
    private final SessionFactory sf;

    @Override
    public Task add(Task task) {
        Session session = this.sf.openSession();
        try {
            session.beginTransaction();
            session.save(task);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return task;
    }

    @Override
    public boolean replace(Integer id, Task task) {
        boolean result = false;
        Session session = this.sf.openSession();
        try {
            session.beginTransaction();
            var updatedLines = session.createQuery(
                            "UPDATE Task SET title = :title, "
                                    + "description = :description, "
                                    + "created = :created, "
                                    + "done = :done  WHERE id = :id")
                    .setParameter("title", task.getTitle())
                    .setParameter("description", task.getDescription())
                    .setParameter("created", task.getCreated())
                    .setParameter("done", task.isDone())
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
    public void delete(Integer id) {
        Session session = this.sf.openSession();
        try {
            session.beginTransaction();
            session.createQuery(
                            "DELETE Task WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Task> findAll() {
        List<Task> result = new ArrayList<>();
        try (Session session = this.sf.openSession()) {
            Query<Task> query = session.createQuery("FROM Task");
            result = query.list();
        }
        return result;
    }

    @Override
    public List<Task> findByTitle(String key) {
        List<Task> result = new ArrayList<>();
        try (Session session = this.sf.openSession()) {
            Query<Task> query = session.createQuery(
                    "FROM Task AS t WHERE t.title = :title", Task.class);
            query.setParameter("title", key);
            result = query.list();
        }
        return result;
    }

    @Override
    public Optional<Task> findById(Integer id) {
        try (Session session = this.sf.openSession()) {
            Query<Task> query = session.createQuery(
                    "FROM Task AS t WHERE t.id = :id", Task.class);
            query.setParameter("id", id);
            return Optional.ofNullable(query.uniqueResult());
        }
    }

    @Override
    public List<Task> findAllByDone(boolean done) {
        List<Task> result = new ArrayList<>();
        try (Session session = this.sf.openSession()) {
            Query<Task> query = session.createQuery(
                    "FROM Task AS t WHERE t.done = :done", Task.class);
            query.setParameter("done", done);
            result = query.list();
        }
        return result;
    }

    @Override
    public void close() {
        this.sf.close();
    }

}
