package com.chatbot_renting.coreservice.dto;


import java.util.UUID;

public record SituationDto(
        UUID id,
        String name,
        String instruction
) {
}
