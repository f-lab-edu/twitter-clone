package clone.twitter.controller;

import clone.twitter.dto.request.TweetComposeRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * not tested(현재 로직상 해당사항 없음)
 * @see clone.twitter.common.ErrorSerializer
 */
@Component
public class TweetValidator {
    public void validate(TweetComposeRequestDto tweetPostDto, Errors errors) {
        // in case of FieldError
        if (false) {
            errors.rejectValue("field", "wrongValue", "Value for {filed} is wrong.");
        }

        // in case of GlobalError
        if (false) {
            errors.reject("wrongValue", "Values for {field} are wrong.");
        }
    }
}
