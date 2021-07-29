package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.UserSeedDto;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final String USERS_FILE_PATH = "src/main/resources/files/users.json";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final PictureService pictureService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, PictureService pictureService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.pictureService = pictureService;
    }

    @Override
    public boolean areImported() {
        return this.userRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(USERS_FILE_PATH));
    }

    @Override
    public String importUsers() throws IOException {
        UserSeedDto[] userSeedDtos = gson.fromJson(readFromFileContent(), UserSeedDto[].class);
        StringBuilder sb = new StringBuilder();

        Arrays.stream(userSeedDtos)
                .filter(userSeedDto -> {
                    boolean isValid = validationUtil.isValid(userSeedDto)
                            && this.pictureService.isEntityExisting(userSeedDto.getProfilePicture())
                            && !isEntityExisting(userSeedDto.getUsername());

                    sb.append(isValid ? String.format("Successfully imported User: %s",
                            userSeedDto.getUsername())
                            : "Invalid User")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(userSeedDto -> {
                    User user = modelMapper.map(userSeedDto, User.class);
                    user.setProfilePicture(this.pictureService.findByPath(userSeedDto.getProfilePicture()));
                    return user;
                })
                .forEach(this.userRepository::save);

        return sb.toString();
    }

    @Override
    public boolean isEntityExisting(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public String exportUsersWithTheirPosts() {
        List<User> users = this.userRepository.findAllOrderByPostsCountDescThenByUserId();
        StringBuilder sb = new StringBuilder();

        users.forEach(user -> {
            sb.append(String.format("User: %s%n" +
                    "Post count: %d%n", user.getUsername(), user.getPosts().size()));

            user.getPosts()
                    .stream()
                    .sorted(Comparator.comparingDouble(p -> p.getPicture().getSize()))
                    .forEach(post -> {
                        sb.append(String.format("==Post Details:%n" +
                                "----Caption: %s%n" +
                                "----Picture Size: %.2f%n", post.getCaption(), post.getPicture().getSize()));
                    });
        });

        return sb.toString();
    }

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username).orElse(null);
    }
}
