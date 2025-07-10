package org.example.mailflowbackend.Controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.mailflowbackend.Service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping(value = "/file/upload", consumes ={"multipart/form-data"})
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "File upload", schema = @Schema(type = "string", format = "binary"))
            @RequestPart("file") MultipartFile file) throws IOException {
        String url = cloudinaryService.uploadFile(file);
        return ResponseEntity.ok(url);
    }
}
