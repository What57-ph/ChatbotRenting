package com.chatbot_renting.fileservice.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {

    private String fileName;

    private String contentType;

    private Long size;

    private String url;
}
