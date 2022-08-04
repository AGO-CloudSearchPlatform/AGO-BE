package ago.ago_be.dto;

import ago.ago_be.domain.Authority;
import ago.ago_be.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class UserRequestDto {

    private String email;
    private String password;
    private String nickname;

    @Builder
    public UserRequestDto(String email, String password, String nickname) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .authority(Authority.ROLE_USER)
                .build();
    }


}
