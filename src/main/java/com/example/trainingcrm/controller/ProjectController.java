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

import com.example.trainingcrm.dto.ProjectForm;
import com.example.trainingcrm.entity.Project;
import com.example.trainingcrm.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private static final List<String> STATUSES = List.of("PLANNING", "IN_PROGRESS", "CLOSED");

    private final ProjectService projectService;

    @ModelAttribute("projectStatuses")
    public List<String> statuses() {
        return STATUSES;
    }

    @GetMapping
    public String list(Model model) {
        List<Project> projects = projectService.findAll();
        model.addAttribute("projects", projects);
        return "projects/list";
    }

    @GetMapping("/new")
    public String newProject(Model model) {
        model.addAttribute("projectForm", new ProjectForm());
        model.addAttribute("isNew", true);
        return "projects/form";
    }

    @PostMapping
    public String create(@Validated @ModelAttribute("projectForm") ProjectForm projectForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            return "projects/form";
        }
        projectService.create(projectForm);
        redirectAttributes.addFlashAttribute("successMessage", "案件を作成しました。");
        return "redirect:/projects";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long projectId, Model model) {
        Project project = projectService.findById(projectId);
        ProjectForm form = new ProjectForm();
        form.setProjectId(project.getProjectId());
        form.setName(project.getName());
        form.setClientName(project.getClientName());
        form.setStatus(project.getStatus());
        form.setStartDate(project.getStartDate());
        form.setEndDate(project.getEndDate());
        form.setDescription(project.getDescription());
        form.setBudget(project.getBudget());
        model.addAttribute("projectForm", form);
        model.addAttribute("isNew", false);
        return "projects/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long projectId,
                         @Validated @ModelAttribute("projectForm") ProjectForm projectForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            return "projects/form";
        }
        projectService.update(projectId, projectForm);
        redirectAttributes.addFlashAttribute("successMessage", "案件を更新しました。");
        return "redirect:/projects";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long projectId,
                         RedirectAttributes redirectAttributes) {
        projectService.delete(projectId);
        redirectAttributes.addFlashAttribute("successMessage", "案件を削除しました。");
        return "redirect:/projects";
    }
}
