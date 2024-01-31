package hongik.pcrc.gotbetterserver.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseView<T>(T data) {
}
