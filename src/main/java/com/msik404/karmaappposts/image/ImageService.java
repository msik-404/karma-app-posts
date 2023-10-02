package com.msik404.karmaappposts.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.msik404.karmaappposts.image.exception.FileProcessingException;
import com.msik404.karmaappposts.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository repository;

    public void save(@NonNull ObjectId postId, byte[] imageData) throws FileProcessingException {

        try {
            final var bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            if (bufferedImage == null) {
                throw new FileProcessingException();
            }

            final var byteArrayOutputStream = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

            repository.save(new ImageDocument(postId, new Binary(byteArrayOutputStream.toByteArray())));
        } catch (IOException ex) {
            throw new FileProcessingException();
        }
    }

}
