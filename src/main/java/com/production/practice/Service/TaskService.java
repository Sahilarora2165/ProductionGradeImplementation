package com.production.practice.Service;

import com.production.practice.Repository.TaskRepository;
import org.springframework.stereotype.Service;
import com.production.practice.Model.Task;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    // Get all tasks
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    // Get task by id
    public Task getTaskById(Long id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    // Create a Task
    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    // Update a task
    public Task updateTask(Long id,Task taskDetails){
        Task task = getTaskById(id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        return taskRepository.save(task);
    }

    // Delete a task
    public void deleteTask(Long id){
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
