package hexlet.code.controller;

import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/labels")
public class LabelsController {
    private final LabelService labelService;

    @Operation(summary = "Get specific label by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabelById(
            @Parameter(description = "Id of label to be found")
            @PathVariable Long id) {
        LabelDTO labelDTO = labelService.findById(id);
        return ResponseEntity.ok(labelDTO);
    }

    @Operation(summary = "Get list of all labels")
    @ApiResponse(responseCode = "200", description = "List of all Labels")
    @GetMapping
    public ResponseEntity<List<LabelDTO>> getAllLabels() {
        List<LabelDTO> labels = labelService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "Label created")
    @PostMapping
    public ResponseEntity<LabelDTO> createLabel(
            @Parameter(description = "Label data to save")
            @Valid @RequestBody LabelDTO labelData) {
        LabelDTO labelDTO = labelService.create(labelData);
        return new ResponseEntity<>(labelDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Update label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> updateLabel(
            @Parameter(description = "Id of label to be updated")
            @PathVariable Long id,
            @Parameter(description = "Label data to update")
            @Valid @RequestBody LabelUpdateDTO labelData) {
        LabelDTO labelDTO = labelService.update(labelData, id);
        return ResponseEntity.ok(labelDTO);
    }

    @Operation(summary = "Delete label by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted"),
            @ApiResponse(responseCode = "404", description = "label with that id not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(
            @Parameter(description = "Id of label to be deleted")
            @PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}