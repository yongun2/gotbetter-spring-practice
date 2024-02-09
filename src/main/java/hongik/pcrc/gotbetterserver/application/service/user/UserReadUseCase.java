package hongik.pcrc.gotbetterserver.application.service.user;

public interface UserReadUseCase {

    boolean checkUserIdDuplicate(String userId);

    boolean checkUserNicknameDuplicate(String nickname);
}
