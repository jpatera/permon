package com.example.application.data.service;

import com.example.application.data.entity.Job;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, UUID> {

}