package kg.spring.ort.mapper;

import kg.spring.ort.dto.response.LoginResponse;
import kg.spring.ort.dto.response.RegisterResponse;
import kg.spring.ort.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TokenMapper.class)
public interface AuthMapper {

    RegisterResponse toRegisterResponse(User user);

    @Mapping(target = "tokens", source = ".")
    LoginResponse toLoginResponse(User user);
}
