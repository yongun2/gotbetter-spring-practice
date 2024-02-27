package hongik.pcrc.gotbetterserver.application.category;

import lombok.Getter;

@Getter
public enum CategoryId {
    STUDY(1),
    EXERCISE(2),
    DEVELOP(3);

    private final int value;

    CategoryId(int value) {
        this.value = value;
    }

}
