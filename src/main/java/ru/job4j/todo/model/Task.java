package ru.job4j.todo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "done", "priority", "categories"})
public class Task {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-EEEE-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime created = LocalDateTime.now(ZoneId.of("UTC")).truncatedTo(ChronoUnit.SECONDS);
    private boolean done = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id")
    private Priority priority;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_category_links",
            joinColumns = { @JoinColumn(name = "task_id") },
            inverseJoinColumns = { @JoinColumn(name = "category_id") }
    )
    private List<Category> categories = new ArrayList<>();

    public Task(String title, String description, User user, Priority priority) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.priority = priority;
    }

    public Task(String title, String description, boolean done, User user, Priority priority) {
        this.title = title;
        this.description = description;
        this.done = done;
        this.user = user;
        this.priority = priority;
    }

    public Task(String title, String description, User user, Priority priority, List<Category> categories) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.priority = priority;
        this.categories = categories;
    }

    public Task(String title, String description, boolean done, User user, Priority priority, List<Category> categories) {
        this.title = title;
        this.description = description;
        this.done = done;
        this.user = user;
        this.priority = priority;
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", created=" + created.format(FORMATTER)
                + ", done=" + done
                + ", user=" + user
                + ", categories=" + categories
                + '}';
    }
}
