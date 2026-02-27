package ru.job4j.todo.utility;

import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

public class LocalDateTimeConverter {

    public static Task convertTimeCreatedOneTask(Task task, String userTimezone) {
        task.setCreated(task.getCreated()
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of(userTimezone)).toLocalDateTime());
        return task;
    }

    public static List<Task> convertTimeCreatedTaskList(List<Task> taskList, String userTimezone) {
        for (Task oneTask : taskList) {
            convertTimeCreatedOneTask(oneTask, userTimezone);
        }
        return taskList;
    }

    public static User checkAndSetDefaultUserTimezone(User user) {
        if (user.getTimezone() == null) {
            user.setTimezone(TimeZone.getDefault().getID());
        }
        return user;
    }
}
