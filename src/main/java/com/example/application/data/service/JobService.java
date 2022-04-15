package com.example.application.data.service;

import com.example.application.data.entity.Job;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobRepository repository;

    @Autowired
    public JobService(JobRepository repository) {
        this.repository = repository;
    }

    public Optional<Job> get(UUID id) {
        return repository.findById(id);
    }

    public Job update(Job entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Job> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
