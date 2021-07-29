package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.PostSeedRootDto;
import softuni.exam.instagraphlite.models.entity.Post;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;
import softuni.exam.instagraphlite.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PostServiceImpl implements PostService {

    private static final String POSTS_FILE_PATH = "src/main/resources/files/posts.xml";

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final UserService userService;
    private final PictureService pictureService;

    public PostServiceImpl(PostRepository postRepository, ModelMapper modelMapper, XmlParser xmlParser, ValidationUtil validationUtil, UserService userService, PictureService pictureService) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.userService = userService;
        this.pictureService = pictureService;
    }

    @Override
    public boolean areImported() {
        return this.postRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(POSTS_FILE_PATH));
    }

    @Override
    public String importPosts() throws IOException, JAXBException {
        PostSeedRootDto postSeedRootDto = xmlParser.readFromFile(POSTS_FILE_PATH, PostSeedRootDto.class);
        StringBuilder sb = new StringBuilder();

        postSeedRootDto.getPosts().stream()
                .filter(postSeedDto -> {
                    boolean isValid = validationUtil.isValid(postSeedDto)
                            && this.pictureService.isEntityExisting(postSeedDto.getPicture().getPath())
                            && this.userService.isEntityExisting(postSeedDto.getUser().getUsername());

                    sb.append(isValid ? String.format("Successfully imported Post, made by %s",
                            postSeedDto.getUser().getUsername())
                            : "Invalid Post")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(postSeedDto -> {
                    Post post = modelMapper.map(postSeedDto, Post.class);
                    post.setPicture(this.pictureService.findByPath(postSeedDto.getPicture().getPath()));
                    post.setUser(this.userService.findByUsername(postSeedDto.getUser().getUsername()));

                    return post;
                })
                .forEach(this.postRepository::save);

        return sb.toString();
    }
}
