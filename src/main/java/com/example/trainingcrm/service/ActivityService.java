package com.example.trainingcrm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trainingcrm.dto.ActivityForm;
import com.example.trainingcrm.entity.Activity;
import com.example.trainingcrm.entity.Task;
import com.example.trainingcrm.repository.ActivityRepository;
import com.example.trainingcrm.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<Activity> findByTask(Long taskId) {
        return activityRepository.findByTaskTaskIdOrderByActivityAtDesc(taskId);
    }

    @Transactional(readOnly = true)
    public Activity findById(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));
    }

    public Activity create(ActivityForm form) {
        Task task = taskRepository.findById(form.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + form.getTaskId()));
        Activity activity = new Activity();
        activity.setTask(task);
        applyForm(activity, form);
        return activityRepository.save(activity);
    }

    public Activity update(Long activityId, ActivityForm form) {
        Activity activity = findById(activityId);
        applyForm(activity, form);
        return activityRepository.save(activity);
    }

    public void delete(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    private void applyForm(Activity activity, ActivityForm form) {
        activity.setActivityType(form.getActivityType());
        activity.setDescription(form.getDescription());
        activity.setActivityAt(form.getActivityAt());
        activity.setLocation(form.getLocation());
    }
}
