package hexlet.code.mapper;

import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class   TaskMapper {
    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "taskStatus", source = "status")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "labels", source = "labelIds")
    public abstract Task map(TaskDTO dto);

    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "labels", target = "labelIds")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "assignee.id", target = "assigneeId")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "taskStatus", source = "status")
    @Mapping(target = "labels", source = "labelIds")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract void update(TaskDTO dto, @MappingTarget Task model);

    public List<Long> getLabelIds(Set<Label> labels) {
        return labels.stream()
                .map(label -> label.getId())
                .collect(Collectors.toList());
    }

    public Set<Label> getLabels(List<Long> labelIds) {
        return labelIds.stream()
                .map(id -> labelRepository.findById(id).orElse(null))
                .collect(Collectors.toSet());
    }
}
