package hexlet.code.service;

import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.exeption.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskMapper taskMapper;
    private TaskSpecification specBuilder;

    public List<TaskDTO> getAll(TaskParamsDTO taskData) {
        var specification = specBuilder.build(taskData);
        var tasks = taskRepository.findAll(specification);
        var result = tasks.stream()
                .map(taskMapper::map)
                .toList();
        return result;
    }

    public Task create(TaskDTO taskData) {
        var task = taskMapper.map(taskData);
        var slug = taskData.getStatus();
        var status = taskStatusRepository.findBySlug(slug.get()).orElseThrow(() -> new ResourceNotFoundException("Status Not Found: " + slug));
        task.setTaskStatus(status);
        return taskRepository.save(task);

    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found: " + id));
        var taskDTO = taskMapper.map(task);
        return taskDTO;
    }

    public Task update(TaskDTO taskData, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found: " + id));
        var slug = taskData.getStatus();
        var status = taskStatusRepository.findBySlug(slug.get()).orElseThrow(() -> new ResourceNotFoundException("Status Not Found: " + slug));
        task.setTaskStatus(status);
        taskMapper.update(taskData, task);
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }


}
