package com.example.trainingcrm.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trainingcrm.dto.ProjectForm;
import com.example.trainingcrm.entity.Project;
import com.example.trainingcrm.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return projectRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
    }

    @Transactional(readOnly = true)
    public Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }

    public Project create(ProjectForm form) {
        Project project = new Project();
        applyForm(project, form);
        return projectRepository.save(project);
    }

    public Project update(Long projectId, ProjectForm form) {
        Project project = findById(projectId);
        applyForm(project, form);
        return projectRepository.save(project);
    }

    public void delete(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    private void applyForm(Project project, ProjectForm form) {
        project.setName(form.getName());
        project.setClientName(form.getClientName());
        project.setStatus(form.getStatus());
        project.setStartDate(form.getStartDate());
        project.setEndDate(form.getEndDate());
        project.setDescription(form.getDescription());
        project.setBudget(form.getBudget());
    }
}
