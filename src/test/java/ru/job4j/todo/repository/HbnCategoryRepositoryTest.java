package ru.job4j.todo.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.configuration.HibernateConfiguration;
import ru.job4j.todo.model.Category;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/* Чтобы тесты работали нужно не забывать добавлять новый Entity в hibernate.cfg.xml в папке test */
class HbnCategoryRepositoryTest {
    
    private static CategoryRepository categoryRepository;

    private static Category firstCategory = new Category("job");
    private static Category secondCategory = new Category("home");

    @BeforeAll
    public static void initRepository() {
        categoryRepository = new HbnCategoryRepository(new CrudRepository(new HibernateConfiguration().sf()));
        categoryRepository.clearRepository();
    }

    @AfterEach
    public void clearCategories() {
        categoryRepository.clearRepository();
    }

    /* Тестируем add() */
    @Test
    public void whenSaveThenGetSame() {
        List<Category> listBeforeAdd = categoryRepository.findAll();
        Category categoryAfterAdd = categoryRepository.add(firstCategory);
        List<Category> listAfterAdd = categoryRepository.findAll();
        assertThat(firstCategory).isEqualTo(categoryAfterAdd);
        assertThat(listBeforeAdd.contains(firstCategory)).isFalse();
        assertThat(listAfterAdd.contains(categoryAfterAdd)).isTrue();
        assertThat(listAfterAdd.size()).isGreaterThan(listBeforeAdd.size());
    }

    @Test
    public void whenSaveTwiceThenGetException() {
        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            categoryRepository.add(firstCategory);
            categoryRepository.add(firstCategory);
        });
        assertThat("could not execute statement").isEqualTo(exception.getMessage());
    }

    /* Тестируем findById() */
    @Test
    public void whenFindByIdSuccess() {
        categoryRepository.add(firstCategory);
        categoryRepository.add(secondCategory);
        Optional<Category> optionalCategory1 = categoryRepository.findById(firstCategory.getId());
        Optional<Category> optionalCategory2 = categoryRepository.findById(secondCategory.getId());
        assertThat(optionalCategory1.isPresent()).isTrue();
        assertThat(optionalCategory1.get()).isEqualTo(firstCategory);
        assertThat(optionalCategory2.isPresent()).isTrue();
        assertThat(optionalCategory2.get()).isEqualTo(secondCategory);
    }

    @Test
    public void whenTryFindByWrongIdThenGetNoting() {
        categoryRepository.add(firstCategory);
        categoryRepository.add(secondCategory);
        Optional<Category> optionalCategory = categoryRepository.findById(secondCategory.getId() + 31);
        assertThat(optionalCategory.isPresent()).isFalse();
    }

    /* Тестируем delete() */
    @Test
    public void whenSaveOneThenDeleteIt() {
        Category categoryAfterAdd = categoryRepository.add(firstCategory);
        List<Category> listAfterAdd = categoryRepository.findAll();
        boolean success = categoryRepository.delete(categoryAfterAdd.getId());
        List<Category> listAfterDelete = categoryRepository.findAll();
        assertThat(listAfterAdd.size()).isGreaterThan(listAfterDelete.size());
        assertThat(listAfterDelete.contains(categoryAfterAdd)).isFalse();
        assertThat(success).isTrue();
    }

    @Test
    public void whenTryDeleteTaskUsingWrongId() {
        Category categoryAfterAdd = categoryRepository.add(firstCategory);
        List<Category> listAfterAdd = categoryRepository.findAll();
        boolean success = categoryRepository.delete(categoryAfterAdd.getId() + 31);
        List<Category> listAfterDeleteFail = categoryRepository.findAll();
        assertThat(listAfterAdd.size()).isEqualTo(listAfterDeleteFail.size());
        assertThat(listAfterDeleteFail.contains(categoryAfterAdd)).isTrue();
        assertThat(success).isFalse();
    }

    /* Тестируем findAllByIds() */
    @Test
    public void whenSaveNotingThenGetSame() {
        List<Category> findAllList = categoryRepository.findAll();
        List<Category> findByIdsEmptyList = categoryRepository.findAllByIds(List.of());
        List<Category> findByIdsList1 = categoryRepository.findAllByIds(List.of(1));
        assertThat(findAllList.size()).isEqualTo(findByIdsEmptyList.size()).isEqualTo(findByIdsList1.size()).isZero();
    }

    @Test
    public void whenSaveOneThenGetSame() {
        Category savedCategory = categoryRepository.add(firstCategory);
        List<Category> findByIdsEmptyList = categoryRepository.findAllByIds(List.of());
        List<Category> findByIdsList1 = categoryRepository.findAllByIds(List.of(savedCategory.getId()));
        List<Category> findAllList = categoryRepository.findAll();
        assertThat(findAllList.size()).isEqualTo(findByIdsList1.size()).isEqualTo(1);
        assertThat(findByIdsList1).containsExactly(savedCategory);
        assertThat(findAllList).containsExactly(savedCategory);
        assertThat(findByIdsEmptyList).isEmpty();
    }

    @Test
    public void whenSaveManyThenGetSome() {
        Category savedCategory1 = categoryRepository.add(firstCategory);
        Category savedCategory2 = categoryRepository.add(secondCategory);
        Category savedCategory3 = categoryRepository.add(new Category("pets"));
        Category savedCategory4 = categoryRepository.add(new Category("meetings"));
        Category savedCategory5 = categoryRepository.add(new Category("purchases"));
        List<Category> findByIdsEmptyList = categoryRepository.findAllByIds(List.of());
        List<Category> findByIdsList = categoryRepository.findAllByIds(List.of(savedCategory1.getId(), savedCategory3.getId(), savedCategory5.getId()));
        List<Category> findAllList = categoryRepository.findAll();
        assertThat(findAllList.size()).isNotEqualTo(findByIdsList.size());
        assertThat(findByIdsList).containsExactly(savedCategory1, savedCategory3, savedCategory5);
        assertThat(findByIdsList).doesNotContainAnyElementsOf(List.of(savedCategory2, savedCategory4));
        assertThat(findByIdsEmptyList).isEmpty();
    }

    @Test
    public void whenSaveManyThenTryGetSomeWithWrongIds() {
        Category savedCategory1 = categoryRepository.add(firstCategory);
        Category savedCategory2 = categoryRepository.add(secondCategory);
        Category savedCategory3 = categoryRepository.add(new Category("pets"));
        Category savedCategory4 = categoryRepository.add(new Category("meetings"));
        Category savedCategory5 = categoryRepository.add(new Category("purchases"));
        List<Category> findByIdsList = categoryRepository.findAllByIds(List.of(savedCategory1.getId(), savedCategory3.getId(), savedCategory5.getId(), savedCategory5.getId() + 31));
        assertThat(findByIdsList.size()).isEqualTo(3);
    }
}