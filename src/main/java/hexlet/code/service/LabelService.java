package hexlet.code.service;

import hexlet.code.dto.LabelDTO;
import hexlet.code.exeption.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        var labels = labelRepository.findAll();
        var result = labels.stream()
                .map(labelMapper::map)
                .toList();
        return result;
    }

    public LabelDTO create(LabelDTO labelData) {
        var label = labelMapper.map(labelData);
        labelRepository.save(label);
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label Not Found: " + id));
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    public LabelDTO update(LabelDTO labelData, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label Not Found: " + id));
        labelMapper.update(labelData, label);
        labelRepository.save(label);
        var labelDTO = labelMapper.map(label);
        return labelDTO;

    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }


}