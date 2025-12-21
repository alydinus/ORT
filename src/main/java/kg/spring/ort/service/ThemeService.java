package kg.spring.ort.service;

import kg.spring.ort.dto.request.CreateThemeRequest;
import kg.spring.ort.dto.response.ThemeResponse;

import java.util.List;

public interface ThemeService {
    List<ThemeResponse> getAll();
    ThemeResponse create(CreateThemeRequest request);
}

