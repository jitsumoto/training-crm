package com.example.trainingcrm.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.trainingcrm.dto.ActivityForm;
import com.example.trainingcrm.entity.Activity;
import com.example.trainingcrm.entity.Project;
import com.example.trainingcrm.entity.Task;
import com.example.trainingcrm.service.ActivityService;
import com.example.trainingcrm.service.ProjectService;
import com.example.trainingcrm.service.TaskService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/projects/{projectId}/tasks/{taskId}/activities")
@RequiredArgsConstructor
public class ActivityController {

    private static final List<String> ACTIVITY_TYPES = List.of("MEETING", "CALL", "VISIT", "OTHER");

    private final ActivityService activityService;
    private final TaskService taskService;
    private final ProjectService projectService;

    @ModelAttribute("activityTypes")
    public List<String> activityTypes() {
        return ACTIVITY_TYPES;
    }

    @GetMapping
    public String list(@PathVariable Long projectId,
                       @PathVariable Long taskId,
                       Model model) {
        Project project = projectService.findById(projectId);
        Task task = taskService.findById(taskId);
        List<Activity> activities = activityService.findByTask(taskId);
        model.addAttribute("project", project);
        model.addAttribute("task", task);
        model.addAttribute("activities", activities);
        return "activities/list";
    }

    @GetMapping("/new")
    public String newActivity(@PathVariable Long projectId,
                              @PathVariable Long taskId,
                              Model model) {
        ActivityForm form = new ActivityForm();
        form.setTaskId(taskId);
        form.setActivityAt(LocalDateTime.now());
        Task task = taskService.findById(taskId);
        Project project = projectService.findById(projectId);
        model.addAttribute("activityForm", form);
        model.addAttribute("isNew", true);
        model.addAttribute("task", task);
        model.addAttribute("project", project);
        return "activities/form";
    }

    @PostMapping
    public String create(@PathVariable Long projectId,
                         @PathVariable Long taskId,
                         @Validated @ModelAttribute("activityForm") ActivityForm activityForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        activityForm.setTaskId(taskId);
        Task task = taskService.findById(taskId);
        Project project = projectService.findById(projectId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            model.addAttribute("task", task);
            model.addAttribute("project", project);
            return "activities/form";
        }
        activityService.create(activityForm);
        redirectAttributes.addFlashAttribute("successMessage", "活動を登録しました。");
        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/activities";
    }

    @GetMapping("/{activityId}/edit")
    public String edit(@PathVariable Long projectId,
                       @PathVariable Long taskId,
                       @PathVariable Long activityId,
                       Model model) {
        Activity activity = activityService.findById(activityId);
        Task task = taskService.findById(taskId);
        Project project = projectService.findById(projectId);
        ActivityForm form = new ActivityForm();
        form.setActivityId(activity.getActivityId());
        form.setTaskId(taskId);
        form.setActivityType(activity.getActivityType());
        form.setDescription(activity.getDescription());
        form.setActivityAt(activity.getActivityAt());
        form.setLocation(activity.getLocation());
        model.addAttribute("activityForm", form);
        model.addAttribute("isNew", false);
        model.addAttribute("task", task);
        model.addAttribute("project", project);
        return "activities/form";
    }

    @PostMapping("/{activityId}")
    public String update(@PathVariable Long projectId,
                         @PathVariable Long taskId,
                         @PathVariable Long activityId,
                         @Validated @ModelAttribute("activityForm") ActivityForm activityForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        activityForm.setTaskId(taskId);
        Task task = taskService.findById(taskId);
        Project project = projectService.findById(projectId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            model.addAttribute("task", task);
            model.addAttribute("project", project);
            return "activities/form";
        }
        activityService.update(activityId, activityForm);
        redirectAttributes.addFlashAttribute("successMessage", "活動を更新しました。");
        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/activities";
    }

    @PostMapping("/{activityId}/delete")
    public String delete(@PathVariable Long projectId,
                         @PathVariable Long taskId,
                         @PathVariable Long activityId,
                         RedirectAttributes redirectAttributes) {
        activityService.delete(activityId);
        redirectAttributes.addFlashAttribute("successMessage", "活動を削除しました。");
        return "redirect:/projects/" + projectId + "/tasks/" + taskId + "/activities";
    }
}
