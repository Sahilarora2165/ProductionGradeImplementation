package com.production.practice.Service;

import com.production.practice.Model.Task;
import com.production.practice.Repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables mockito
public class TaskServiceTest {

    @Mock // Creates fake repository
    private TaskRepository taskRepository;

    @InjectMocks // Create real service with fake repo injected
    private TaskService taskService;

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasksExist() {
        // ARRANGE:  Tell fake repo what to return
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        // ACT: Call the method
        List<Task> result = taskService.getAllTasks();

        // ASSERT:  Verify result
        assertTrue(result. isEmpty());
        assertEquals(0, result.size());

        // VERIFY: Confirm repository was called
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks_WhenTasksExist(){
        // ARRANGE
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setCompleted(true);

        // when(...) is tells the fake object how to behave.
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // ACT
        List<Task> result = taskService.getAllTasks();

        // ASSERT
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());
        assertFalse(result.get(0).isCompleted());
        assertTrue(result.get(1).isCompleted());
    }


    @Test
    void getTaskById_ShouldReturnTask_WhenTaskExists() {
        // ARRANGE
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // ACT
        Task result = taskService.getTaskById(taskId);

        // ASSERT
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Test Task", result. getTitle());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {
        // ARRANGE
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(taskId);
        });

        assertTrue(exception.getMessage().contains("Task not found"));
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        // ARRANGE
        Task taskToCreate = new Task();
        taskToCreate.setTitle("New Task");
        taskToCreate.setDescription("Description");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("New Task");
        savedTask.setDescription("Description");

        when(taskRepository.save(taskToCreate)).thenReturn(savedTask);

        // ACT
        Task result = taskService.createTask(taskToCreate);

        // ASSERT
        assertNotNull(result. getId());
        assertEquals("New Task", result.getTitle());
        verify(taskRepository, times(1)).save(taskToCreate);
    }
}
