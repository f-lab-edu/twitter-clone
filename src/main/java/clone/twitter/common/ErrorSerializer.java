package clone.twitter.common;

import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.util.TweetValidator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

/**
 * not tested(현재 비즈니스 로직상 해당사항 없음). 이후 적용 예정.
 * @see clone.twitter.controller.TweetController#composeTweet(TweetComposeRequestDto, Errors)
 * @see clone.twitter.common.ErrorEntityModel
 * @see TweetValidator
 */
@JsonComponent
public class ErrorSerializer extends JsonSerializer<Errors> {
    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();

        // in case of FieldError
        errors.getFieldErrors().forEach(e -> {
            try {
                gen.writeStartObject();

                gen.writeStringField("field", e.getField());

                gen.writeStringField("objectName", e.getObjectName());

                gen.writeStringField("code", e.getCode());

                gen.writeStringField("defaultMessage", e.getDefaultMessage());

                Object rejectedValue = e.getRejectedValue();

                if (rejectedValue != null) {
                    gen.writeStringField("rejectedValue", rejectedValue.toString());
                }

                gen.writeEndObject();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        });

        // in case of GlobalError
        errors.getGlobalErrors().forEach(e -> {
            try {
                gen.writeStartObject();

                gen.writeStringField("objectName", e.getObjectName());

                gen.writeStringField("code", e.getCode());

                gen.writeStringField("defaultMessage", e.getDefaultMessage());

                gen.writeEndObject();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        });
        gen.writeEndArray();
    }
}
