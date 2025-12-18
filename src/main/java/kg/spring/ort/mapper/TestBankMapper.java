package kg.spring.ort.mapper;

import kg.spring.ort.dto.response.QuestionResponse;
import kg.spring.ort.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestBankMapper {
    QuestionResponse toQuestionResponse(Question allQuestions);
}
