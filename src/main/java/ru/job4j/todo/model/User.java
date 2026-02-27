package ru.job4j.todo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "todo_user")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String login;
    private String password;

    @Column(name = "user_zone")
    private String timezone;

    public User(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public User(String name, String login, String password, String timezone) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.timezone = timezone;
    }
}
