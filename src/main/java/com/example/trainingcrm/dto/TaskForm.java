package com.example.trainingcrm.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskForm {

    private Long taskId;

    private Long projectId;

    private Long assigneeId;

    @NotBlank
    @Size(max = 120)
    private String title;

    @Size(max = 2000)
    private String detail;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @NotBlank
    @Size(max = 20)
    private String status;

    @Min(0)
    @Max(100)
    private Integer progressPercent;
}
