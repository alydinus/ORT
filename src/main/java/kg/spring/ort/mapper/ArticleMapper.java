package kg.spring.ort.mapper;

import kg.spring.ort.dto.response.ArticleResponse;
import kg.spring.ort.entity.ArticleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "status", expression = "java(article.getStatus() != null ? article.getStatus().name() : null)")
    ArticleResponse toResponse(ArticleEntity article);
}
