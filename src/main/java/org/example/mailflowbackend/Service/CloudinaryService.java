package org.example.mailflowbackend.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    public String uploadFile(MultipartFile file) throws IOException;
}
