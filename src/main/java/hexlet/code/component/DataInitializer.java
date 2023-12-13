package hexlet.code.component;

import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final TaskStatusRepository taskStatusRepository;

    private final TaskStatusMapper taskStatusMapper;

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        initiateUser();
        initiateTaskStatuses();
        initiateLabels();
    }

    public void initiateUser() {
        var userData = new UserCreateDTO();
        userData.setFirstName("hexlet");
        userData.setEmail("hexlet@example.com");
        var hashedPassword = passwordEncoder.encode("qwerty");
        userData.setPassword(hashedPassword);
        var user = userMapper.map(userData);
        userRepository.save(user);
    }

    public void initiateTaskStatuses() {
        String[][] data = {{"draft", "Draft"}, {"to_review", "ToReview"}, {"to_be_fixed", "ToBeFixed"},
                {"to_publish", "ToPublish"}, {"published", "Published"}};
        Arrays.stream(data)
                .map(slugAndName -> {
                    TaskStatusDTO taskStatusData = new TaskStatusDTO();
                    taskStatusData.setSlug(JsonNullable.of(slugAndName[0]));
                    taskStatusData.setName(JsonNullable.of(slugAndName[1]));
                    return taskStatusMapper.map(taskStatusData);
                })
                .forEach(taskStatusRepository::save);


    }

    public void initiateLabels() {

        var newData = new LabelDTO();
        newData.setName(JsonNullable.of("feature"));
        var newLabel = labelMapper.map(newData);
        labelRepository.save(newLabel);

        var anotherData = new LabelDTO();
        anotherData.setName(JsonNullable.of("bug"));
        var anotherLabel = labelMapper.map(anotherData);
        labelRepository.save(anotherLabel);

    }

}
