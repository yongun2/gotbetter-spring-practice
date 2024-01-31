package hongik.pcrc.gotbetterserver.application.service.user;

import hongik.pcrc.gotbetterserver.application.domain.User;

public interface UserOperationUseCase {
    User createUser(User user);

    boolean checkUserIdDuplicate(String userId);
}
