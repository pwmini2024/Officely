package uni.projects.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.backend.dao.ImageRepository;
import uni.projects.backend.dao.OfficeRepository;
import uni.projects.backend.exceptions.ResourceNotFoundException;
import uni.projects.backend.models.office.Image;
import uni.projects.backend.models.office.Office;
import uni.projects.backend.web.ImageDto;

import java.util.Base64;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private OfficeRepository officeRepository;

    public ImageDto createImage(ImageDto image) {
        Image newImage = new Image();
        newImage.setData(Base64.getDecoder().decode(image.data()));
        return ImageDto.valueFrom(imageRepository.save(newImage));
    }

    public ImageDto getImage(Integer id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            throw new ResourceNotFoundException("Image not found");
        }
        return ImageDto.valueFrom(image);
    }

    public List<Image> getImagesByIds(List<Integer> list) {
        return imageRepository.findAllById(list);
    }

    public void deleteImage(Integer imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        List<Office> offices = officeRepository.findAll();
        for (Office office : offices) {
            if (office.getImages().contains(image)) {
                office.getImages().remove(image);
                officeRepository.save(office);
            }
        }

        imageRepository.deleteById(imageId);
    }
}
