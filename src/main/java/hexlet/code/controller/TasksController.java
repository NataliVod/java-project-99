package hexlet.code.controller;

import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tasks")
public class TasksController {
    private final TaskService taskService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO taskDTO = taskService.findById(id);
        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> taskDTOList = taskService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskDTOList.size()))
                .body(taskDTOList);
    }


    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskData) {
        Long assigneeId = taskData.getAssigneeId();
        if (assigneeId != null && userRepository.findById(assigneeId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        TaskDTO taskDTO = taskService.create(taskData);
        return new ResponseEntity<>(taskDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskData) {
        TaskDTO taskDTO = taskService.update(taskData, id);
        return ResponseEntity.ok(taskDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}