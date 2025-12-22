package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.HibernateConfiguration;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class HbnTaskRepositoryTest {
    private static TaskRepository hbnTaskRepository;

    @BeforeAll
    public static void initRepository() {
        hbnTaskRepository = new HbnTaskRepository(new HibernateConfiguration().sf());
    }

    @AfterEach
    public void clearTasks() {
        var tasks = hbnTaskRepository.findAll();
        for (Task oneTask : tasks) {
            hbnTaskRepository.delete(oneTask.getId());
        }
    }

    /* Тестируем add() */
    @Test
    public void whenSaveThenGetSame() {
        Task taskBeforeAdd = new Task("Тест add", "Протестировать метод add()");
        List<Task> listBeforeAdd = hbnTaskRepository.findAll();
        Task taskAfterAdd = hbnTaskRepository.add(taskBeforeAdd);
        List<Task> listAfterAdd = hbnTaskRepository.findAll();
        assertThat(taskBeforeAdd).isEqualTo(taskAfterAdd);
        assertThat(listBeforeAdd.contains(taskBeforeAdd)).isFalse();
        assertThat(listAfterAdd.contains(taskBeforeAdd)).isTrue();
        assertThat(listAfterAdd.size()).isGreaterThan(listBeforeAdd.size());
    }

    @Test
    public void whenSaveOneSameSeveralThenGetAll() {
        Task task = new Task("Тест add", "Протестировать метод add(), добавив одну и ту же task-у несколько раз");
        List<Task> listBeforeAdd = hbnTaskRepository.findAll();
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        Task taskAfterAdd2 = hbnTaskRepository.add(task);
        Task taskAfterAdd3 = hbnTaskRepository.add(task);
        List<Task> listAfterAdd = hbnTaskRepository.findAll();
        assertThat(task).isEqualTo(taskAfterAdd1).isEqualTo(taskAfterAdd2).isEqualTo(taskAfterAdd3);
        assertThat(listBeforeAdd.contains(task)).isFalse();
        assertThat(listBeforeAdd.size()).isEqualTo(0);
        assertThat(listAfterAdd.size()).isEqualTo(3);
        assertThat(listAfterAdd).isEqualTo(List.of(taskAfterAdd1, taskAfterAdd2, taskAfterAdd3));
    }

    /* Тестируем replace() */
    @Test
    public void whenReplaceOneTaskThenGetIt() {
        Task task = new Task("Тест add", "Протестировать метод add(), добавив одну task-у");
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        Task taskAfterReplace1 = new Task("Тест replace()", "Протестировать метод replace(), изменив единственную task-у");
        boolean success = hbnTaskRepository.replace(taskAfterAdd1.getId(), taskAfterReplace1);
        List<Task> listAfterReplace = hbnTaskRepository.findAll();
        assertThat(success).isTrue();
        assertThat(listAfterReplace.size()).isEqualTo(1);
        assertThat(listAfterReplace.contains(taskAfterReplace1)).isTrue();
    }

    @Test
    public void whenTryToReplaceItWithWrongTaskThenGetFail() {
        Task task = new Task("Тест add", "Протестировать метод add(), добавив одну task-у");
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        Task taskAfterReplace1 = new Task("Тест replace()", null);
        boolean success = hbnTaskRepository.replace(taskAfterAdd1.getId(), taskAfterReplace1);
        List<Task> listAfterReplace = hbnTaskRepository.findAll();
        assertThat(success).isFalse();
        assertThat(listAfterReplace.size()).isEqualTo(1);
        assertThat(listAfterReplace.contains(taskAfterReplace1)).isFalse();
    }

    @Test
    public void whenTryToReplaceTaskUsingWrongIdThenGetFail() {
        Task task = new Task("Тест add", "Протестировать метод add(), добавив одну task-у");
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        Task taskAfterReplace1 = new Task("Тест replace()", "Протестировать метод replace(), изменив единственную task-у");
        boolean success = hbnTaskRepository.replace(taskAfterAdd1.getId() + 31, taskAfterReplace1);
        List<Task> listAfterReplace = hbnTaskRepository.findAll();
        assertThat(success).isFalse();
        assertThat(listAfterReplace.size()).isEqualTo(1);
        assertThat(listAfterReplace.contains(taskAfterReplace1)).isFalse();
    }

    /* Тестируем delete() */
    @Test
    public void whenSaveOneTaskThenDeleteIt() {
        Task task = new Task("Тест delete", "Протестировать метод delete(), добавив одну task-у и затем удалив её");
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        List<Task> listAfterAdd = hbnTaskRepository.findAll();
        boolean success = hbnTaskRepository.delete(taskAfterAdd1.getId());
        List<Task> listAfterDelete = hbnTaskRepository.findAll();
        assertThat(listAfterAdd.size()).isGreaterThan(listAfterDelete.size());
        assertThat(listAfterDelete.contains(taskAfterAdd1)).isFalse();
        assertThat(success).isTrue();
    }

    @Test
    public void whenTryDeleteTaskUsingWrongId() {
        Task task = new Task("Тест delete", "Протестировать метод delete(), добавив одну task-у и затем попытаться удалить НЕ ЕЁ");
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        List<Task> listAfterAdd = hbnTaskRepository.findAll();
        boolean success = hbnTaskRepository.delete(taskAfterAdd1.getId() + 31);
        List<Task> listAfterDeleteFail = hbnTaskRepository.findAll();
        assertThat(listAfterAdd.size()).isEqualTo(listAfterDeleteFail.size());
        assertThat(listAfterDeleteFail.contains(taskAfterAdd1)).isTrue();
        assertThat(success).isFalse();
    }

    /* Тестируем findByTitle() */
    @Test
    public void whenSaveTwoDifferentTasksThenGetAll() {
        Task task1 = new Task("findByTitle1", "Протестировать метод findByTitle(), добавив две разных task-и1");
        Task task2 = new Task("findByTitle2", "Протестировать метод findByTitle(), добавив две разных task-и2");
        hbnTaskRepository.add(task1);
        hbnTaskRepository.add(task2);
        List<Task> taskListFoundByTitle1 = hbnTaskRepository.findByTitle(task1.getTitle());
        List<Task> taskListFoundByTitle2 = hbnTaskRepository.findByTitle(task2.getTitle());
        assertThat(taskListFoundByTitle1.size()).isEqualTo(taskListFoundByTitle2.size()).isEqualTo(1);
        assertThat(taskListFoundByTitle1).isNotEqualTo(taskListFoundByTitle2);
        assertThat(taskListFoundByTitle1.get(0)).isEqualTo(task1);
        assertThat(taskListFoundByTitle2.get(0)).isEqualTo(task2);
    }

    @Test
    public void whenSaveTwoSameByNameTasksThenGetAll() {
        Task task1 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и1");
        Task task2 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и2");
        hbnTaskRepository.add(task1);
        hbnTaskRepository.add(task2);
        List<Task> taskListFoundByTitle = hbnTaskRepository.findByTitle(task1.getTitle());
        assertThat(taskListFoundByTitle.size()).isEqualTo(2);
        assertThat(taskListFoundByTitle.get(0)).isEqualTo(task1);
        assertThat(taskListFoundByTitle.get(1)).isEqualTo(task2);
        assertThat(taskListFoundByTitle.get(0)).isNotEqualTo(taskListFoundByTitle.get(1));
    }

    @Test
    public void whenTryFindByNameThenGetNoting() {
        Task task1 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и1");
        Task task2 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и2");
        hbnTaskRepository.add(task1);
        hbnTaskRepository.add(task2);
        List<Task> taskListFoundByTitle = hbnTaskRepository.findByTitle("noting");
        assertThat(taskListFoundByTitle.size()).isZero();
    }

    @Test
    public void whenTryFindByEmptyNameThenGetNoting() {
        Task task1 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и1");
        Task task2 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и2");
        hbnTaskRepository.add(task1);
        hbnTaskRepository.add(task2);
        List<Task> taskListFoundByTitle = hbnTaskRepository.findByTitle("");
        assertThat(taskListFoundByTitle.size()).isZero();
    }

    /* Тестируем findById() */
    @Test
    public void whenFindByIdSuccess() {
        Task task1 = new Task("findByTitle1", "Протестировать метод findByTitle(), добавив две разных task-и1");
        Task task2 = new Task("findByTitle2", "Протестировать метод findByTitle(), добавив две разных task-и2");
        hbnTaskRepository.add(task1);
        hbnTaskRepository.add(task2);
        Optional<Task> optionalTask1 = hbnTaskRepository.findById(task1.getId());
        Optional<Task> optionalTask2 = hbnTaskRepository.findById(task2.getId());
        assertThat(optionalTask1.isPresent()).isTrue();
        assertThat(optionalTask1.get()).isEqualTo(task1);
        assertThat(optionalTask2.isPresent()).isTrue();
        assertThat(optionalTask2.get()).isEqualTo(task2);
    }

    @Test
    public void whenTryFindByWrongIdThenGetNoting() {
        Task task1 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и1");
        Task task2 = new Task("findByTitle", "Протестировать метод findByTitle(), добавив две разных task-и2");
        hbnTaskRepository.add(task1);
        hbnTaskRepository.add(task2);
        Optional<Task> optionalTask = hbnTaskRepository.findById(task2.getId() + 1);
        assertThat(optionalTask.isPresent()).isFalse();
        assertThat(optionalTask.isEmpty()).isTrue();
    }

    /* Тестируем findAllByDone() */
    @Test
    public void whenSaveTwoTasksWithDifferentDoneThenGetBoth() {
        Task task1 = new Task("findByDone1", "Протестировать метод findByDone(), добавив две разных task-и1");
        Task task2 = new Task("findByDone2", "Протестировать метод findByDone(), добавив две разных task-и2", true);
        hbnTaskRepository.add(task1);
        hbnTaskRepository.add(task2);
        List<Task> listByDoneFalse = hbnTaskRepository.findAllByDone(false);
        List<Task> listByDoneTrue = hbnTaskRepository.findAllByDone(true);
        assertThat(listByDoneFalse.size()).isEqualTo(1);
        assertThat(listByDoneFalse.get(0)).isEqualTo(task1);
        assertThat(listByDoneTrue.size()).isEqualTo(1);
        assertThat(listByDoneTrue.get(0)).isEqualTo(task2);
    }

    /* Тестируем switchUndoneToDone() */
    @Test
    public void whenSwitchOneTaskThenGetIt() {
        Task task = new Task("switchUndoneToDone()", "По умолчанию task-и добавляются не выполненными.");
        boolean beforeSwitchIsDone = task.isDone();
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        boolean success = hbnTaskRepository.switchUndoneToDone(taskAfterAdd1.getId());
        List<Task> listDoneTasks = hbnTaskRepository.findAllByDone(true);
        List<Task> listUndoneTasks = hbnTaskRepository.findAllByDone(false);
        assertThat(success).isTrue();
        assertThat(listDoneTasks.size()).isEqualTo(1);
        assertThat(listUndoneTasks.size()).isEqualTo(0);
        assertThat(beforeSwitchIsDone).isFalse();
        assertThat(listDoneTasks.get(0).isDone()).isTrue();
    }

    @Test
    public void whenTryToSwitchDoneTaskThenGetSameTask() {
        Task task = new Task("switchUndoneToDone()", "Добавляем уже выполненную task-у", true);
        boolean beforeSwitchIsDone = task.isDone();
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        boolean success = hbnTaskRepository.switchUndoneToDone(taskAfterAdd1.getId());
        List<Task> listDoneTasks = hbnTaskRepository.findAllByDone(true);
        List<Task> listUndoneTasks = hbnTaskRepository.findAllByDone(false);
        assertThat(success).isTrue();
        assertThat(listDoneTasks.size()).isEqualTo(1);
        assertThat(listUndoneTasks.size()).isEqualTo(0);
        assertThat(beforeSwitchIsDone).isTrue();
        assertThat(listDoneTasks.get(0).isDone()).isTrue();
    }

    @Test
    public void whenTryToSwitchOneTaskUsingWrongIdThenGetFail() {
        Task task = new Task("switchUndoneToDone()", "По умолчанию task-и добавляются не выполненными.");
        boolean beforeSwitchIsDone = task.isDone();
        Task taskAfterAdd1 = hbnTaskRepository.add(task);
        boolean success = hbnTaskRepository.switchUndoneToDone(taskAfterAdd1.getId() + 31);
        List<Task> listDoneTasks = hbnTaskRepository.findAllByDone(true);
        List<Task> listUndoneTasks = hbnTaskRepository.findAllByDone(false);
        assertThat(success).isFalse();
        assertThat(listDoneTasks.size()).isEqualTo(0);
        assertThat(listUndoneTasks.size()).isEqualTo(1);
        assertThat(beforeSwitchIsDone).isFalse();
        assertThat(listUndoneTasks.get(0)).isEqualTo(task);
    }
}