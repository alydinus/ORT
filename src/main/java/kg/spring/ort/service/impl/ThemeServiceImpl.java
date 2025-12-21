package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateThemeRequest;
import kg.spring.ort.dto.response.ThemeResponse;
import kg.spring.ort.entity.TestTheme;
import kg.spring.ort.exception.ConflictException;
import kg.spring.ort.repository.ThemeRepository;
import kg.spring.ort.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ThemeResponse> getAll() {
        return themeRepository.findAll().stream()
                .map(t -> new ThemeResponse(t.getName()))
                .sorted(java.util.Comparator.comparing(ThemeResponse::name))
                .toList();
    }

    @Override
    @Transactional
    public ThemeResponse create(CreateThemeRequest request) {
        String name = normalize(request.name());
        if (themeRepository.findByName(name).isPresent()) {
            throw new ConflictException("Тема уже существует");
        }
        themeRepository.save(TestTheme.builder().name(name).build());
        return new ThemeResponse(name);
    }

    private String normalize(String value) {
        var v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            throw new ConflictException("Название темы не задано");
        }
        return v.toLowerCase(Locale.ROOT);
    }
}
