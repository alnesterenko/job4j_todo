package ru.job4j.todo.service;

import lombok.RequiredArgsConstructor;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.repository.PriorityRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@ThreadSafe
@Service
public class SimplePriorityService implements PriorityService {

    private final PriorityRepository hbnPriorityRepository;

    @Override
    public Collection<Priority> findAll() {
        return hbnPriorityRepository.findAll();
    }

    @Override
    public Optional<Priority> findById(Integer id) {
        return hbnPriorityRepository.findById(id);
    }
}
