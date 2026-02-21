package ru.job4j.todo.service;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@ThreadSafe
@Service
public class SimpleCategoryService implements CategoryService {

    private final CategoryRepository hbnCategoryRepository;

    @Override
    public List<Category> findAll() {
        return hbnCategoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return hbnCategoryRepository.findById(id);
    }

    @Override
    public List<Category> findAllByIds(List<Integer> ids) {
        return hbnCategoryRepository.findAllByIds(ids);
    }
}
