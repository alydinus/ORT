package kg.spring.ort.mapper;

import kg.spring.ort.dto.response.TokenPair;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    TokenPair toTokenPair(String accessToken, String refreshToken);
}
