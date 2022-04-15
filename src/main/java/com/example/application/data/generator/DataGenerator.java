package com.example.application.data.generator;

import com.example.application.data.Role;
import com.example.application.data.entity.Job;
import com.example.application.data.entity.User;
import com.example.application.data.service.JobRepository;
import com.example.application.data.service.UserRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository,
            JobRepository jobRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);
            logger.info("... generating 100 Job entities...");
            ExampleDataGenerator<Job> jobRepositoryGenerator = new ExampleDataGenerator<>(Job.class,
                    LocalDateTime.of(2022, 4, 15, 0, 0, 0));
            jobRepositoryGenerator.setData(Job::setJobNo, DataType.NUMBER_UP_TO_1000);
            jobRepositoryGenerator.setData(Job::setJobName, DataType.WORD);
            jobRepositoryGenerator.setData(Job::setStartDate, DataType.DATETIME_LAST_1_YEAR);
            jobRepositoryGenerator.setData(Job::setDuration, DataType.DATETIME_NEXT_7_DAYS);
            jobRepository.saveAll(jobRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}