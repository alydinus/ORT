package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateTagRequest;
import kg.spring.ort.dto.response.TagResponse;
import kg.spring.ort.entity.Tag;
import kg.spring.ort.exception.ConflictException;
import kg.spring.ort.repository.TagRepository;
import kg.spring.ort.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getAll() {
        return tagRepository.findAll().stream()
                .map(t -> new TagResponse(t.getId(), t.getName()))
                .sorted(java.util.Comparator.comparing(TagResponse::name))
                .toList();
    }

    @Override
    @Transactional
    public TagResponse create(CreateTagRequest request) {
        String normalized = normalize(request.name());
        if (tagRepository.findByName(normalized).isPresent()) {
            throw new ConflictException("Тег уже существует");
        }
        Tag saved = tagRepository.save(Tag.builder().name(normalized).build());
        return new TagResponse(saved.getId(), saved.getName());
    }

    private String normalize(String value) {
        var v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            throw new ConflictException("Имя тега не задано");
        }
        return v.toLowerCase(Locale.ROOT);
    }
}

