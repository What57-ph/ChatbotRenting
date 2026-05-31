package com.chatbot_renting.coreservice.dto.request;

public record FileSourceDto(
        String fileName,
        String fileUrl,
        String fileType,
        Long fileSize
) {}
