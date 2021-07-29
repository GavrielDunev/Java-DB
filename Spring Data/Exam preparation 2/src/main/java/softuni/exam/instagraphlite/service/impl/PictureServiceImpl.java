package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.PictureSeedDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {

    private static final String PICTURES_FILE_PATH = "src/main/resources/files/pictures.json";

    private final PictureRepository pictureRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public PictureServiceImpl(PictureRepository pictureRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.pictureRepository = pictureRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        PictureSeedDto[] pictureSeedDtos = gson.fromJson(readFromFileContent(), PictureSeedDto[].class);
        StringBuilder sb = new StringBuilder();

        Arrays.stream(pictureSeedDtos)
                .filter(pictureSeedDto -> {
                    boolean isValid = validationUtil.isValid(pictureSeedDto)
                            && !isEntityExisting(pictureSeedDto.getPath());

                    sb.append(isValid ? String.format("Successfully imported Picture, with size %.2f",
                            pictureSeedDto.getSize())
                            : "Invalid Picture")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(pictureSeedDto -> modelMapper.map(pictureSeedDto, Picture.class))
                .forEach(this.pictureRepository::save);

        return sb.toString();
    }

    @Override
    public boolean isEntityExisting(String path) {
        return this.pictureRepository.existsByPath(path);
    }

    @Override
    public Picture findByPath(String path) {
        return this.pictureRepository.findByPath(path).orElse(null);
    }

    @Override
    public String exportPictures() {
        List<Picture> pictures = this.pictureRepository.findAllBySizeGreaterThanOrderBySize(30000);
        StringBuilder sb = new StringBuilder();

        pictures.forEach(picture -> {
            sb.append(String.format("%.2f – %s", picture.getSize(), picture.getPath()))
            .append(System.lineSeparator());
        });

        return sb.toString();
    }
}
