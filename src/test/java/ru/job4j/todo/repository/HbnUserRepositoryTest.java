package ru.job4j.todo.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.HibernateConfiguration;
import ru.job4j.todo.model.User;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;

class HbnUserRepositoryTest {

    private static UserRepository hbnUserRepository;

    @BeforeAll
    public static void initRepository() {
        hbnUserRepository = new HbnUserRepository(new HibernateConfiguration().sf());
    }

    @AfterEach
    public void clearUsers() {
        var users = hbnUserRepository.findAll();
        for (User oneUser : users) {
            hbnUserRepository.deleteById(oneUser.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        User user = new User("user@mail.ru", "admin", "1234");
        var userAfterSave = hbnUserRepository.save(user).orElse(null);
        var userFoundUserById = hbnUserRepository.findById(userAfterSave.getId()).orElse(null);
        var userFoundByLoginAndPassword = hbnUserRepository.findByLoginAndPassword("admin", "1234").orElse(null);
        assertThat(userAfterSave).usingRecursiveComparison().isEqualTo(userFoundUserById).isEqualTo(userFoundByLoginAndPassword);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = hbnUserRepository.save(new User("user1@mail.ru", "admin1", "1234"));
        var user2 = hbnUserRepository.save(new User("user2@mail.ru", "admin2", "12345"));
        var user3 = hbnUserRepository.save(new User("user3@mail.ru", "admin3", "123456"));
        var result = hbnUserRepository.findAll();
        assertThat(result).isEqualTo(List.of(user1.orElse(null), user2.orElse(null), user3.orElse(null)));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(hbnUserRepository.findAll()).isEqualTo(emptyList());
        assertThat(hbnUserRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var user = hbnUserRepository.save(new User("user@mail.ru", "admin", "1234")).orElse(null);
        var isDeleted = hbnUserRepository.deleteById(user.getId());
        var savedUser = hbnUserRepository.findById(user.getId());
        var wasFoundByLoginAndPassword = hbnUserRepository.findByLoginAndPassword(user.getLogin(), user.getPassword());
        assertThat(isDeleted).isTrue();
        assertThat(savedUser).isEqualTo(empty());
        assertThat(wasFoundByLoginAndPassword).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(hbnUserRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenSaveTwice() {
        User user = new User("user@mail.ru", "admin", "1234");
        var firstUser = hbnUserRepository.save(user).orElse(null);
        var secondUser = hbnUserRepository.save(user).orElse(null);
        var result = hbnUserRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(firstUser).isNotEqualTo(secondUser);
    }

    @Test
    public void whenSavedTwoDifferentUsersWithTheSameLoginAndPassword() {
        String login = "user@mail.ru";
        String password = "1234";
        var firstUser = hbnUserRepository.save(new User("Василий", login, password)).orElse(null);
        var secondUser = hbnUserRepository.save(new User("Сергей", login, password)).orElse(null);
        var userFoundByLoginAndPassword = hbnUserRepository.findByLoginAndPassword(login, password).orElse(null);
        var listOfUsers = hbnUserRepository.findAll();
        assertThat(listOfUsers.size()).isEqualTo(1);
        assertThat(listOfUsers.contains(firstUser)).isTrue();
        assertThat(listOfUsers.contains(secondUser)).isFalse();
        assertThat(listOfUsers.contains(userFoundByLoginAndPassword)).isTrue();
        assertThat(userFoundByLoginAndPassword).isEqualTo(firstUser);
    }

}