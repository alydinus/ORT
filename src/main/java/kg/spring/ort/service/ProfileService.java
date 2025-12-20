package kg.spring.ort.service;

import kg.spring.ort.dto.request.ChangePasswordRequest;
import kg.spring.ort.dto.request.UpdateProfileRequest;
import kg.spring.ort.dto.response.MeResponse;

public interface ProfileService {
    MeResponse getMe(String username);
    MeResponse updateProfile(String username, UpdateProfileRequest request);
    void changePassword(String username, ChangePasswordRequest request);
}

