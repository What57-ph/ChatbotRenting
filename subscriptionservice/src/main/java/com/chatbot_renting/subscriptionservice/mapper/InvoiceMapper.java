package com.chatbot_renting.subscriptionservice.mapper;

import com.chatbot_renting.subscriptionservice.dto.response.InvoiceDto;
import com.chatbot_renting.subscriptionservice.entity.Invoice;
import org.mapstruct.Mapper;

// Avoiding circular dependency by not injecting OrderMapper yet, or use specific ignores
@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceDto toDto(Invoice entity);
}
