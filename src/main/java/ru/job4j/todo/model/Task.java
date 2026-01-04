package ru.job4j.todo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "done", "priority"})
public class Task {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-EEEE-yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private boolean done = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id")
    private Priority priority;

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

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", created=" + created.format(FORMATTER)
                + ", done=" + done
                + ", user=" + user
                + '}';
    }
}
