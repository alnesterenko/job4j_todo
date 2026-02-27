package ru.job4j.todo.service;

import ru.job4j.todo.model.User;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public interface UserService {

    Optional<User> save(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    Collection<User> findAll();

    default List<TimeZone> createAvailableTimeZonesList() {
        /* Делаем список и заодно фильтруем от старых неработающих с ZoneId часовых поясов */
        List<TimeZone> zones = ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .map(TimeZone::getTimeZone)
                .collect(Collectors.toList());
        return zones;
    }
}
