package com.chatbot_renting.notificationservice.client;
import com.chatbot_renting.notificationservice.dto.response.client.AllUsersResponse;

import java.util.List;
public interface UserAccountClient {
    AllUsersResponse getAllUsers();
}
