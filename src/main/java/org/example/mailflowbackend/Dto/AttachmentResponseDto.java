package org.example.mailflowbackend.Dto;

import lombok.Data;

@Data
public class AttachmentResponseDto {
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl; // Link Cloudinary
}
