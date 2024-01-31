package hongik.pcrc.gotbetterserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GotbetterException extends RuntimeException {
    private final HttpStatus status;
    private final String type;

    public GotbetterException(MessageType message) {
        super(message.getMessage());
        this.status = message.getStatus();
        this.type = message.name();
    }
}
