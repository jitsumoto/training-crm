package com.example.trainingcrm.controller;

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

import com.example.trainingcrm.dto.TaskForm;
import com.example.trainingcrm.entity.Project;
import com.example.trainingcrm.entity.Task;
import com.example.trainingcrm.entity.User;
import com.example.trainingcrm.service.ProjectService;
import com.example.trainingcrm.service.TaskService;
import com.example.trainingcrm.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private static final List<String> STATUSES = List.of("TODO", "DOING", "DONE");

    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserService userService;

    @ModelAttribute("taskStatuses")
    public List<String> statuses() {
        return STATUSES;
    }

    @ModelAttribute("assignees")
    public List<User> assignees() {
        return userService.getAllUsers();
    }

    @GetMapping
    public String list(@PathVariable Long projectId, Model model) {
    	
        Project project = projectService.findById(projectId);
        List<Task> tasks = taskService.findByProject(projectId);
        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTask(@PathVariable Long projectId, Model model) {
        TaskForm form = new TaskForm();
        form.setProjectId(projectId);
        model.addAttribute("taskForm", form);
        model.addAttribute("isNew", true);
        return "tasks/form";
    }

    @PostMapping
    public String create(@PathVariable Long projectId,
                         @Validated @ModelAttribute("taskForm") TaskForm taskForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        taskForm.setProjectId(projectId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            return "tasks/form";
        }
        taskService.create(taskForm);
        redirectAttributes.addFlashAttribute("successMessage", "タスクを作成しました。");
        return "redirect:/projects/" + projectId + "/tasks";
    }

    @GetMapping("/{taskId}/edit")
    public String edit(@PathVariable Long projectId,
                       @PathVariable Long taskId,
                       Model model) {
        Task task = taskService.findById(taskId);
        TaskForm form = new TaskForm();
        form.setTaskId(task.getTaskId());
        form.setProjectId(projectId);
        form.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getUserId() : null);
        form.setTitle(task.getTitle());
        form.setDetail(task.getDetail());
        form.setDueDate(task.getDueDate());
        form.setStatus(task.getStatus());
        form.setProgressPercent(task.getProgressPercent());
        model.addAttribute("taskForm", form);
        model.addAttribute("isNew", false);
        return "tasks/form";
    }

    @PostMapping("/{taskId}")
    public String update(@PathVariable Long projectId,
                         @PathVariable Long taskId,
                         @Validated @ModelAttribute("taskForm") TaskForm taskForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        taskForm.setProjectId(projectId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            return "tasks/form";
        }
        taskService.update(taskId, taskForm);
        redirectAttributes.addFlashAttribute("successMessage", "タスクを更新しました。");
        return "redirect:/projects/" + projectId + "/tasks";
    }

    @PostMapping("/{taskId}/delete")
    public String delete(@PathVariable Long projectId,
                         @PathVariable Long taskId,
                         RedirectAttributes redirectAttributes) {
        taskService.delete(taskId);
        redirectAttributes.addFlashAttribute("successMessage", "タスクを削除しました。");
        return "redirect:/projects/" + projectId + "/tasks";
    }
}
