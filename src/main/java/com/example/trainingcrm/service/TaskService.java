package com.example.trainingcrm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trainingcrm.dto.TaskForm;
import com.example.trainingcrm.entity.Project;
import com.example.trainingcrm.entity.Task;
import com.example.trainingcrm.entity.User;
import com.example.trainingcrm.repository.ProjectRepository;
import com.example.trainingcrm.repository.TaskRepository;
import com.example.trainingcrm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Task> findByProject(Long projectId) {
        return taskRepository.findByProjectProjectIdOrderByDueDateAsc(projectId);
    }

    @Transactional(readOnly = true)
    public Task findById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
    }

    public Task create(TaskForm form) {
        Project project = projectRepository.findById(form.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + form.getProjectId()));
        Task task = new Task();
        task.setProject(project);
        applyForm(task, form);
        return taskRepository.save(task);
    }

    public Task update(Long taskId, TaskForm form) {
        Task task = findById(taskId);
        applyForm(task, form);
        return taskRepository.save(task);
    }

    public void delete(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private void applyForm(Task task, TaskForm form) {
        task.setTitle(form.getTitle());
        task.setDetail(form.getDetail());
        task.setDueDate(form.getDueDate());
        task.setStatus(form.getStatus());
        task.setProgressPercent(form.getProgressPercent());

        if (form.getAssigneeId() != null) {
            User assignee = userRepository.findById(form.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + form.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }
    }
}
