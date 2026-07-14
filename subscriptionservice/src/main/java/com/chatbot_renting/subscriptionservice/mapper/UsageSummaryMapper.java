package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.response.UsageSummaryDto;
import com.chatbot_renting.subscriptionservice.entity.UsageSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsageSummaryMapper {
    UsageSummaryDto toDto(UsageSummary entity);
}
