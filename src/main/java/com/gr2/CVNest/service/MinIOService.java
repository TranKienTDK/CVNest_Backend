package com.gr2.CVNest.service;

import io.minio.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class MinIOService {
    private final MinioClient minioClient;

    private static final String END_POINT = "http://localhost:9000";
    private static final String BUCKET_NAME = "cvnest";

    public MinIOService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file) {
        try {
            // Lấy thông tin về tên tệp (objectName) và contentType
            String objectName = file.getOriginalFilename();
            String contentType = file.getContentType();

            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            // Upload file lên MinIO
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(contentType)
                                .build());
            }

            return generateFileUrl(objectName);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading the file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while uploading file: " + e.getMessage());
        }
    }

    private String generateFileUrl(String objectName) {
        return END_POINT + "/" + BUCKET_NAME + "/" + objectName;
    }
}
