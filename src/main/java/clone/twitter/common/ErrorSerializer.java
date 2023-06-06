package clone.twitter.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

/**
 * not tested(현재 로직상 해당사항 없음)
 * @see clone.twitter.controller.TweetValidator
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
