package kg.spring.ort.mapper;

import kg.spring.ort.dto.response.ArticleResponse;
import kg.spring.ort.entity.ArticleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    ArticleResponse toResponse(ArticleEntity articleById);
}
