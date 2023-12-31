package com.example.demo.jira.service;

import com.example.demo.jira.dto.AddTToSprintDto;
import com.example.demo.jira.dto.SprintDto;
import com.example.demo.jira.entity.Sprint;
import com.example.demo.jira.entity.Task;
import com.example.demo.jira.exception.SprintNotFound;
import com.example.demo.jira.exception.TaskNotFound;
import com.example.demo.jira.repo.SprintRepo;
import com.example.demo.jira.repo.TaskRepo;
import com.example.demo.jira.util.ModelCopy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class SprintServiceImpl implements SprintService {
    @Autowired
    private SprintRepo sprintRepo;
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ModelCopy modelCopy;

    @Override
    public SprintDto createSprint(SprintDto sprintDto) {
        System.err.println(sprintDto);
        Sprint sprint = modelCopy.dtoToEntity(sprintDto);
        Sprint save = sprintRepo.save(sprint);
        return modelCopy.entityToDto(save);
    }

    public List<SprintDto> getAllSprints() {
//        System.err.println("jcjcj");
//        System.err.println(sprintRepo.findAll());
        return sprintRepo.findAll().stream().map(t -> modelCopy.entityToDto(t)).collect(Collectors.toList());
    }

    @Override
    public SprintDto getSprintById(String sprintId) {
        Sprint sprint = sprintRepo.findById(sprintId).get();
        return modelCopy.entityToDto(sprint);
    }

    @Override
    public SprintDto addTToSprint(String sprintId, AddTToSprintDto addTToSprintDto) {
        try {
//            Sprint sprint = sprintRepo.findById(sId).get();
            Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new SprintNotFound("Sprint not found"+sprintId));
            String tasks = addTToSprintDto.getTasks();
//        Task task = taskRepo.findById(tasks).get();
            Task task = taskRepo.findById(tasks).orElseThrow(() -> new TaskNotFound("Task not found"+tasks));
            if (sprint.getTaskIds() == null) {
                sprint.setTaskIds(List.of(task.getTaskId()));
            } else {
                sprint.getTaskIds().add(task.getTaskId());
            }
            sprintRepo.save(sprint);
            return modelCopy.entityToDto(sprint);
        } catch (SprintNotFound e) {
            e.printStackTrace();
            return new SprintDto();
        }
    }

    @Override
    public SprintDto removeTFromSprint(String sprintId, AddTToSprintDto addTToSprintDto) {
        try {
            Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new SprintNotFound("Sprint not found"+sprintId));
            String tasks = addTToSprintDto.getTasks();
            Task task = taskRepo.findById(tasks).orElseThrow(() -> new TaskNotFound("Task not found"+tasks));
            sprint.getTaskIds().remove(task.getTaskId());
            sprintRepo.save(sprint);
            return modelCopy.entityToDto(sprint);
        } catch (SprintNotFound e) {
            e.printStackTrace();
            return new SprintDto();
        }
    }
}