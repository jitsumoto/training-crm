package com.example.trainingcrm.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityForm {

    private Long activityId;

    private Long taskId;

    @Size(max = 30)
    private String activityType;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull
    private LocalDateTime activityAt;

    @Size(max = 100)
    private String location;
}
