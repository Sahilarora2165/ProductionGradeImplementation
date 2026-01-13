package com.production.practice.controller;

import com.production.practice.Model.Task;
import com.production.practice.Repository.TaskRepository;
import com.production.practice.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class TaskControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldCreateTask() {
        // Given
        Task task = Task.builder()
                .title("Test Title")
                .description("Desc")
                .completed(false)
                .build();

        // When
        ResponseEntity<Task> response = restTemplate.postForEntity(
                "/api/tasks",
                task,
                Task.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Title");
        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldGetAllTasks() {
        // Given
        taskRepository.save(Task.builder()
                .title("Task1")
                .description("Desc1")
                .completed(false)
                .build());
        taskRepository.save(Task.builder()
                .title("Task2")
                .description("Desc2")
                .completed(false)
                .build());

        // When
        ResponseEntity<Task[]> response = restTemplate.getForEntity(
                "/api/tasks",
                Task[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldGetTaskById() {
        // Given
        Task saved = taskRepository.save(Task.builder()
                .title("Single")
                .description("Find me")
                .completed(false)
                .build());

        // When
        ResponseEntity<Task> response = restTemplate.getForEntity(
                "/api/tasks/" + saved.getId(),
                Task.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Single");
    }

    @Test
    void shouldUpdateTask() {
        // Given
        Task saved = taskRepository.save(Task.builder()
                .title("Old")
                .description("Old desc")
                .completed(false)
                .build());

        Task update = Task.builder()
                .title("New")
                .description("Updated")
                .completed(true)
                .build();

        // When
        restTemplate.put("/api/tasks/" + saved.getId(), update);

        // Then
        Task updated = taskRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("New");
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void shouldDeleteTask() {
        // Given
        Task saved = taskRepository.save(Task.builder()
                .title("DeleteMe")
                .description("Won't last")
                .completed(false)
                .build());

        // When
        restTemplate.delete("/api/tasks/" + saved.getId());

        // Then
        assertThat(taskRepository.findById(saved.getId())).isEmpty();
        assertThat(taskRepository.count()).isEqualTo(0);
    }

//    @Test
//    void shouldReturn404ForNonExistentTask() {
//        // When
//        ResponseEntity<String> response = restTemplate.getForEntity(
//                "/api/tasks/999",
//                String.class
//        );
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }

    @Test
    void shouldRejectInvalidCreate() {
        // Given - invalid task with blank title
        Task invalid = Task.builder()
                .title("")
                .description("Desc")
                .completed(false)
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/tasks",
                invalid,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}