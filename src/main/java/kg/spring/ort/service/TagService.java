package kg.spring.ort.service;

import kg.spring.ort.dto.request.CreateTagRequest;
import kg.spring.ort.dto.response.TagResponse;

import java.util.List;

public interface TagService {
    List<TagResponse> getAll();
    TagResponse create(CreateTagRequest request);
}

