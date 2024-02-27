package hongik.pcrc.gotbetterserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {

    BAD_REQUEST("Check API request URL protocol, parameter, etc. for errors", HttpStatus.BAD_REQUEST),

    // User Error
    BAD_USER_ID_PATTERN("UserID must be 5-20 characters, lowercase letters, numbers, and special symbols (_), (-) only.", HttpStatus.BAD_REQUEST),
    BAD_PASSWORD_PATTERN("Password must be 8 to 16 characters of upper and lowercase letters, numbers, and special characters.", HttpStatus.BAD_REQUEST),
    BAD_NICKNAME_PATTERN("Nickname must be 4-12 characters", HttpStatus.BAD_REQUEST),
    DUPLICATED_USER_ID("Duplicated userId", HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME("Duplicated Nickname", HttpStatus.CONFLICT),
    USER_NOT_FOUND("No corresponding user was found.", HttpStatus.NOT_FOUND),

    // jwt
    INVALID_TOKEN("Received an invalid token.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("The token has expired.", HttpStatus.BAD_REQUEST),

    // StudyRoom
    INVALID_STUDY_ROOM_DURATION("Invalid study room duration.", HttpStatus.BAD_REQUEST),

    NOT_FOUND("No data was found for the server. Please refer  to parameter description.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String message;
    private final HttpStatus status;

    MessageType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
