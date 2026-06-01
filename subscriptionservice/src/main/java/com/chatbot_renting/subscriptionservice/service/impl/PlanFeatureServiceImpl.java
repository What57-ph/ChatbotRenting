package com.chatbot_renting.subscriptionservice.service.impl;

import com.chatbot_renting.subscriptionservice.dto.request.PlanFeature.CreatePlanFeatureRequest;
import com.chatbot_renting.subscriptionservice.entity.PlanFeature;
import com.chatbot_renting.subscriptionservice.mapper.PlanFeatureMapper;
import com.chatbot_renting.subscriptionservice.repository.PlanFeatureRepository;
import com.chatbot_renting.subscriptionservice.service.PlanFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanFeatureServiceImpl implements PlanFeatureService {
    private final PlanFeatureRepository planFeatureRepository;
    private final PlanFeatureMapper mapper;

    @Override
    public List<PlanFeature> saveAllPlanFeatures(List<CreatePlanFeatureRequest> requests) {

        List<PlanFeature> features = requests.stream()
                .map(mapper::toEntity)
                .toList();

        return planFeatureRepository.saveAll(features);
    }
}
