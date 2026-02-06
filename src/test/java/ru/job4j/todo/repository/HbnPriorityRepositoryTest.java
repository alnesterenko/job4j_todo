package ru.job4j.todo.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.HibernateConfiguration;
import ru.job4j.todo.model.Priority;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/* Чтобы тесты работали нужно не забывать добавлять новый Entity в hibernate.cfg.xml в папке test */

class HbnPriorityRepositoryTest {
    private static PriorityRepository priorityRepository;

    private static Priority firstPriority = new Priority("urgently", 1);
    private static Priority secondPriority = new Priority("normal", 2);

    @BeforeAll
    public static void initRepository() {
        priorityRepository = new HbnPriorityRepository(new CrudRepository(new HibernateConfiguration().sf()));
    }

    @AfterEach
    public void clearPriorities() {
        priorityRepository.clearRepository();
    }

    /* Тестируем add() */
    @Test
    public void whenSaveThenGetSame() {
        List<Priority> listBeforeAdd = priorityRepository.findAll();
        Priority priorityAfterAdd = priorityRepository.add(firstPriority);
        List<Priority> listAfterAdd = priorityRepository.findAll();
        assertThat(firstPriority).isEqualTo(priorityAfterAdd);
        assertThat(listBeforeAdd.contains(firstPriority)).isFalse();
        assertThat(listAfterAdd.contains(priorityAfterAdd)).isTrue();
        assertThat(listAfterAdd.size()).isGreaterThan(listBeforeAdd.size());
    }

    @Test
    public void whenSaveTwiceThenGetException() {
        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            priorityRepository.add(firstPriority);
            priorityRepository.add(firstPriority);
        });
        assertThat("could not execute statement").isEqualTo(exception.getMessage());
    }

    /* Тестируем findById() */
    @Test
    public void whenFindByIdSuccess() {
        priorityRepository.add(firstPriority);
        priorityRepository.add(secondPriority);
        Optional<Priority> optionalPriority1 = priorityRepository.findById(firstPriority.getId());
        Optional<Priority> optionalPriority2 = priorityRepository.findById(secondPriority.getId());
        assertThat(optionalPriority1.isPresent()).isTrue();
        assertThat(optionalPriority1.get()).isEqualTo(firstPriority);
        assertThat(optionalPriority2.isPresent()).isTrue();
        assertThat(optionalPriority2.get()).isEqualTo(secondPriority);
    }

    @Test
    public void whenTryFindByWrongIdThenGetNoting() {
        priorityRepository.add(firstPriority);
        priorityRepository.add(secondPriority);
        Optional<Priority> optionalPriority = priorityRepository.findById(secondPriority.getId() + 31);
        assertThat(optionalPriority.isPresent()).isFalse();
    }

    /* Тестируем delete() */
    @Test
    public void whenSaveOneThenDeleteIt() {
        Priority priorityAfterAdd = priorityRepository.add(firstPriority);
        List<Priority> listAfterAdd = priorityRepository.findAll();
        boolean success = priorityRepository.delete(priorityAfterAdd.getId());
        List<Priority> listAfterDelete = priorityRepository.findAll();
        assertThat(listAfterAdd.size()).isGreaterThan(listAfterDelete.size());
        assertThat(listAfterDelete.contains(priorityAfterAdd)).isFalse();
        assertThat(success).isTrue();
    }

    @Test
    public void whenTryDeleteTaskUsingWrongId() {
        Priority priorityAfterAdd = priorityRepository.add(firstPriority);
        List<Priority> listAfterAdd = priorityRepository.findAll();
        boolean success = priorityRepository.delete(priorityAfterAdd.getId() + 31);
        List<Priority> listAfterDeleteFail = priorityRepository.findAll();
        assertThat(listAfterAdd.size()).isEqualTo(listAfterDeleteFail.size());
        assertThat(listAfterDeleteFail.contains(priorityAfterAdd)).isTrue();
        assertThat(success).isFalse();
    }
}